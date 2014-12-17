package htwg.de.hitbook.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ecki on 12.12.2014.
 */
public class SQLiteHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "TreeTable.db";
    private static final int DATABASE_VERSION = 1;

    // table name
    private static final String TABLE_NAME="Felled_Trees";

    // the colums of the table
    private static final String[] COLUMNS={
            "ID",
            "Lumberjack",
            "Team",
            "AreaDescription",
            "Latitude",
            "Longitude",
            "Height",
            "Diameter",
            "Date"
    };
    // SQLite commands to create new table
    private static final String CREATE_TABLE_FELLED_TREES=""
            +"create table "+TABLE_NAME+ "("
            +COLUMNS[0]+" integer primary key autoincrement,"
            +COLUMNS[1]+" text,"
            +COLUMNS[2]+" text,"
            +COLUMNS[3]+" text,"
            +COLUMNS[4]+" text,"
            +COLUMNS[5]+" text,"
            +COLUMNS[6]+" real,"
            +COLUMNS[7]+" real,"
            +COLUMNS[8]+" text"
            +")";

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public String[] getColumns(){
        return COLUMNS;
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FELLED_TREES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version "+oldVersion+" to "+newVersion+". This destroys all old data");
        db.execSQL("DROP TABLE IF EXISTS SCANITEM");
        onCreate(db);
    }
}
