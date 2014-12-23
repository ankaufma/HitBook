package htwg.de.hitbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import htwg.de.hitbook.model.FelledTree;

/**
 * Created by Ecki on 13.12.2014.
 */
public class DatabaseAccess {
    private Context context;
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private static String[] COLUMNS;
    private static String TABLE_NAME;
    private static final String PICTURES_FOLDER = "pictures";
    private  File imgDirectory;

    public DatabaseAccess(Context context){
        this.context = context;
        try{
            dbHelper = new SQLiteHelper(context);
            COLUMNS = dbHelper.getColumns();
            TABLE_NAME = dbHelper.getTableName();
        }catch (Exception e){
            Toast.makeText(context,"Datenbank Fehler", Toast.LENGTH_LONG).show();
        }

        // get path to image directory
        ContextWrapper contextWrapper = new ContextWrapper(context);
        imgDirectory = contextWrapper.getDir(PICTURES_FOLDER, Context.MODE_PRIVATE);

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
        if (imgDirectory.isDirectory()) {
            String[] children = imgDirectory.list();
            for (int i = 0; i < children.length; i++) {
                new File(imgDirectory, children[i]).delete();
            }
        }
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
     * Puts the dates of all entries in a List
     * @return
     */
    public List<String> getAllDates(){
        List<String> dates = new ArrayList<String>();
        for (int i = 0; i<getAllFelledTrees().size(); i++){
            dates.add(getAllFelledTrees().get(i).getDate());
        }

        return dates;
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


        ContentValues cv = new ContentValues();

        cv.put(COLUMNS[1], lumberjack);
        cv.put(COLUMNS[2], team);
        cv.put(COLUMNS[3], areaDescription);
        cv.put(COLUMNS[4], latitude);
        cv.put(COLUMNS[5], longitude);
        cv.put(COLUMNS[6], height);
        cv.put(COLUMNS[7], diameter);
        cv.put(COLUMNS[8], date);

        long insertId = db.insert("Felled_Trees",null,cv);

        Cursor cursor = db.query(TABLE_NAME,COLUMNS,"ID ="+insertId,null,null,null,null);
        cursor.moveToFirst();

        //Save picture of tree
        savePictureById(picture, (int) insertId);

        return CursorToFelledTree(cursor);
    }

    private boolean savePictureById(Bitmap picture, int TreeId){
        try{
            ContextWrapper contextWrapper = new ContextWrapper(context);
            // create directory
            File picPath = new File(imgDirectory,TreeId+".png");
            FileOutputStream fos = new FileOutputStream(picPath);
            picture.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            return true;
        }catch (Exception e){
            Log.d("DatabaseAccess","Failed to save picture.");
            return false;
        }
    }

    /**
     * Returns the picture of the trees id
     * @param id
     * @return
     */
    public Bitmap getPictureById(int id){
        Bitmap picture;

        try{
            File picPath = new File(imgDirectory,id+".png");
            FileInputStream fis = new FileInputStream(picPath);
            picture = BitmapFactory.decodeStream(fis);
            fis.close();
            return picture;
        }catch (Exception e){
            Log.d("DatabaseAccess",e.toString());
            return null;
        }

    }

    private FelledTree CursorToFelledTree(Cursor cursor){
        FelledTree ft = new FelledTree(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getDouble(6),
                cursor.getDouble(7),
                cursor.getString(8)
        );

        return ft;
    }


}
