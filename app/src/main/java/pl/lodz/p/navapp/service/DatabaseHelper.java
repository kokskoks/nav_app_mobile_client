package pl.lodz.p.navapp.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Calgon on 2016-11-13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "navAppDB.db";
    private static final String TABLE_NAME = "COORDINATES";
    public static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "SHORTNAME";
    private static final String COL_4 = "ADDRESS";
    private static final String COL_5 = "LONGITUDE";
    private static final String COL_6 = "LATITUDE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME, SHORT_NAME, ADDRESS, LONGITUDE, LATITUDE  )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String name,String shortname,String address, String longtitude, String latitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,shortname);
        contentValues.put(COL_4,address);
        contentValues.put(COL_5,longtitude);
        contentValues.put(COL_6,latitude);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        return result != -1;
    }
}
