package htwg.de.hitbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import htwg.de.hitbook.HitbookActivity;
import htwg.de.hitbook.model.FelledTree;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Daniel Eckstein on 13.12.2014.
 * Class for getting Access to the Database, use this instead of Helper Class
 */
public class DatabaseAccess {
    Context context;
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private static String[] COLUMNS;
    private static String TABLE_NAME;
    private static final int ZERO_OFFSET = 0;

    public DatabaseAccess(Context context){
        this.context = context;
        try{
            dbHelper = new SQLiteHelper(context);
            COLUMNS = dbHelper.getColumns();
            TABLE_NAME = dbHelper.getTableName();
        }catch (Exception e){
            Toast.makeText(context,"Datenbank Fehler", LENGTH_LONG).show();
        }

//        // get path to image directory
//        ContextWrapper contextWrapper = new ContextWrapper(context);
//        imgDirectory = contextWrapper.getDir(PICTURES_FOLDER, Context.MODE_PRIVATE);

    }

    public void open(){
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void deleteDatabase(Context context){
        // delete Database
        dbHelper.deleteDatabase(context);

        // delete pictures from storage
        File imgDirectory = new File(HitbookActivity.EXTERNAL_STORAGE_FOLDER_PATH);
        if (imgDirectory.isDirectory()) {
            String[] children = imgDirectory.list();
            for (int i = 0; i < children.length; i++) {
                new File(imgDirectory, children[i]).delete();
            }
        }
    }

    public void deleteTreeById(int id){
        // delete from database
        db.delete(TABLE_NAME,COLUMNS[0]+"="+id,null);
        // delete image of tree
        new File(HitbookActivity.EXTERNAL_STORAGE_FOLDER_PATH,id+".jpg").delete();
    }

    public List<FelledTree> getAllFelledTrees(){
        List<FelledTree> TreeList = new ArrayList<FelledTree>();

        Cursor cursor = db.query(TABLE_NAME,COLUMNS,null,null,null,null,null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            TreeList.add(CursorToFelledTree(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return TreeList;
    }

    public FelledTree getFelledTreeById(int id){
        FelledTree felledTree =null;
        Cursor cursor = db.query(TABLE_NAME,COLUMNS,null,null,null,null,null);
        List<Integer> ids = new ArrayList<Integer>();

        cursor.moveToFirst();

        // Search for the Felled Tree by his Id
        while (!cursor.isAfterLast()){
            if(cursor.getInt(0) == id)
            {
                felledTree = CursorToFelledTree(cursor);
                break;
            }
            cursor.moveToNext();
        }

        return felledTree;
    }


    /**
     * Puts the Ids of all entries in a List
     * @return Ids as String List
     */
    public List<String> getAllIds(){
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i<getAllFelledTrees().size(); i++){
            ids.add(((Integer)getAllFelledTrees().get(i).getId()).toString());
        }

        return ids;
    }



    /**
     * creates a new entry inside database and saves the belonging picture to internal storage
     * @param lumberjack
     * @param team
     * @param areaDescription
     * @param latitude
     * @param longitude
     * @param height
     * @param diameter
     * @param picture
     * @return
     */
    public FelledTree createNewFelledTree(
            String lumberjack, String team, String areaDescription, String latitude, String longitude, double height, double diameter, Bitmap picture){

        // get the actual date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());

        // Prepare thumbnail for database
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] byteArray = bos.toByteArray();

        ContentValues cv = new ContentValues();

        cv.put(COLUMNS[1], lumberjack);
        cv.put(COLUMNS[2], team);
        cv.put(COLUMNS[3], areaDescription);
        cv.put(COLUMNS[4], latitude);
        cv.put(COLUMNS[5], longitude);
        cv.put(COLUMNS[6], height);
        cv.put(COLUMNS[7], diameter);
        cv.put(COLUMNS[8], date);
        cv.put(COLUMNS[9], byteArray);

        long insertId = db.insert("Felled_Trees",null,cv);

        Cursor cursor = db.query(TABLE_NAME,COLUMNS,"ID ="+insertId,null,null,null,null);
        cursor.moveToFirst();

        //Save picture of tree
        //savePictureById(picture, (int) insertId);

        return CursorToFelledTree(cursor);
    }

//    private boolean savePictureById(Bitmap picture, int TreeId){
//        try{
//            ContextWrapper contextWrapper = new ContextWrapper(context);
//            // create directory
//            File picPath = new File(imgDirectory,TreeId+".png");
//            FileOutputStream fos = new FileOutputStream(picPath);
//            picture.compress(Bitmap.CompressFormat.PNG,100,fos);
//            fos.flush();
//            fos.close();
//            return true;
//        }catch (Exception e){
//            Log.d("DatabaseAccess","Failed to save picture.");
//            return false;
//        }
//    }

//    /**
//     * Returns the picture of the trees id
//     * @param id
//     * @return
//     */
//    public Bitmap getThumbnailById(int id){
//        Bitmap thumbnail;
//
//        try{
//            File picPath = new File(imgDirectory,id+".png");
//            FileInputStream fis = new FileInputStream(picPath);
//            thumbnail = BitmapFactory.decodeStream(fis);
//            fis.close();
//            return thumbnail;
//        }catch (Exception e){
//            Log.d("DatabaseAccess",e.toString());
//            return null;
//        }
//
//    }

    private FelledTree CursorToFelledTree(Cursor cursor){

        // Get Thumbnail
        byte[] byteArray = cursor.getBlob(9);
        Bitmap thumbnail = BitmapFactory.decodeByteArray(byteArray, ZERO_OFFSET, byteArray.length);

        // Get Three
        FelledTree ft = new FelledTree(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getDouble(6),
                cursor.getDouble(7),
                cursor.getString(8),
                thumbnail
        );

        return ft;
    }


}
