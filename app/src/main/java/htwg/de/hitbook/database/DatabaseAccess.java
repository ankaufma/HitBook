package htwg.de.hitbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import htwg.de.hitbook.model.FelledTree;

/**
 * Created by Ecki on 13.12.2014.
 */
public class DatabaseAccess {
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private static String[] COLUMNS;
    private static String TABLE_NAME;

    public DatabaseAccess(Context context){
        try{
            dbHelper = new SQLiteHelper(context);
            COLUMNS = dbHelper.getColumns();
            TABLE_NAME = dbHelper.getTableName();
        }catch (Exception e){
            Toast.makeText(context,"Datenbank Fehler", Toast.LENGTH_LONG);
        }

    }

    public void open(){
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void deleteDatabase(Context context){
        dbHelper.deleteDatabase(context);
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

    public List<String> getAllDates(){
        List<String> dates = new ArrayList<String>();
        for (int i = 0; i<getAllFelledTrees().size(); i++){
            dates.add(getAllFelledTrees().get(i).getDate());
        }

        return dates;
    }

    public FelledTree createNewFelledTree(
            String lumberjack, String team, String areaDescription, String latitude, String longitude, double height, double diameter){

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

        return CursorToFelledTree(cursor);
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
