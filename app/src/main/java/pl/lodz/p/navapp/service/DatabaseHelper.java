package pl.lodz.p.navapp.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.navapp.PlaceInfo;

/**
 * Created by Calgon on 2016-11-13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "navAppDB.db";
    private static final String TABLE_COORDINATES = "COORDINATES";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_TITLE = "TITLE";
    private static final String COLUMN_PLACE_NUMBER = "PLACE_NUMBER";
    private static final String COLUMN_ADDRESS = "ADDRESS";
    private static final String COLUMN_LONGITUDE = "LONGITUDE";
    private static final String COLUMN_LATITUDE = "LATITUDE";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";

    private static final String TABLE_CREATE_QUERY = "create table "
            + TABLE_COORDINATES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_PLACE_NUMBER + " TEXT, "
            + COLUMN_ADDRESS + " TEXT, "
            + COLUMN_LONGITUDE + " TEXT,"
            + COLUMN_LATITUDE + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_COORDINATES);
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
        long result = db.insert(TABLE_COORDINATES, null, contentValues);
        return result != -1;
    }

    public List<PlaceInfo> getPlaces() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<PlaceInfo> places = new ArrayList<>();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_COORDINATES, null);
        if (res.getCount() == 0) {
            return places;
        }

        while (res.moveToNext()) {
            PlaceInfo placeInfo = new PlaceInfo();
            placeInfo.setID(res.getInt(0));
            placeInfo.setTitle(res.getString(1));
            placeInfo.setPlaceNumber(res.getString(2));
            placeInfo.setAddress(res.getString(3));
            double lon = Double.valueOf(res.getString(4));
            double lat = Double.valueOf(res.getString(5));
            placeInfo.setGeoPoint(new GeoPoint(lat, lon));
            placeInfo.setDescription(res.getString(6));
            places.add(placeInfo);
        }
        res.close();
        return places;
    }
}
