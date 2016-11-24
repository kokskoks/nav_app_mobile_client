package pl.lodz.p.navapp.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.osmdroid.util.GeoPoint;

import pl.lodz.p.navapp.PlaceInfo;

/**
 * Created by Calgon on 2016-11-13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "navAppDB.db";
    private static final String TABLE_NAME = "COORDINATES";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_TITLE = "TITLE";
    private static final String COLUMN_PLACE_NUMBER = "PLACE_NUMBER";
    private static final String COLUMN_ADDRESS = "ADDRESS";
    private static final String COLUMN_LONGITUDE = "LONGITUDE";
    private static final String COLUMN_LATITUDE = "LATITUDE";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";

    private static final String TABLE_CREATE_QUERY ="create table " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_TITLE + " TEXT, " + COLUMN_PLACE_NUMBER + " TEXT, " + COLUMN_ADDRESS + " TEXT, " + COLUMN_LONGITUDE + " TEXT," + COLUMN_LATITUDE + " TEXT, " + COLUMN_DESCRIPTION + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(PlaceInfo placeInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, placeInfo.getID());
        contentValues.put(COLUMN_TITLE, placeInfo.getTitle());
        contentValues.put(COLUMN_PLACE_NUMBER, placeInfo.getPlaceNumber());
        contentValues.put(COLUMN_ADDRESS, placeInfo.getAddress());
        GeoPoint geoPoint = placeInfo.getGeoPoint();
        contentValues.put(COLUMN_LONGITUDE, geoPoint.getLongitude());
        contentValues.put(COLUMN_LATITUDE, geoPoint.getLatitude());
        contentValues.put(COLUMN_DESCRIPTION, placeInfo.getDescription());
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }
}
