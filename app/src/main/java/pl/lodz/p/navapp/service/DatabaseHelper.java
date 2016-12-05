package pl.lodz.p.navapp.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.navapp.domain.PlaceInfo;
import pl.lodz.p.navapp.domain.Sublocation;

import static pl.lodz.p.navapp.service.DatabaseConstants.*;

/**
 * Created by Calgon on 2016-11-13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COORDINATES_TABLE_CREATE_QUERY);
        db.execSQL(SUBLOCATIONS_TABLE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_COORDINATES_TABLE);
        db.execSQL(DROP_SUBLOCATIONS_TABLE);
        onCreate(db);
    }

    /**
     * Method that inserts Building info into database
     *
     * @param placeInfo Building info to be stored in database
     * @return true if building was inserted, false otherwise
     */
    public boolean insertPlace(PlaceInfo placeInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COORDINATES_COLUMN_ID, placeInfo.getID());
        contentValues.put(COORDINATES_COLUMN_TITLE, placeInfo.getTitle());
        contentValues.put(COORDINATES_COLUMN_PLACE_NUMBER, placeInfo.getPlaceNumber());
        contentValues.put(COORDINATES_COLUMN_ADDRESS, placeInfo.getAddress());
        GeoPoint geoPoint = placeInfo.getGeoPoint();
        contentValues.put(COORDINATES_COLUMN_LONGITUDE, geoPoint.getLongitude());
        contentValues.put(COORDINATES_COLUMN_LATITUDE, geoPoint.getLatitude());
        contentValues.put(COORDINATES_COLUMN_DESCRIPTION, placeInfo.getDescription());
        long result = db.insert(TABLE_COORDINATES, null, contentValues);
        if (result != -1 && placeInfo.getSublocations() != null && !placeInfo.getSublocations().isEmpty()) {
            result = insertSublocations(placeInfo);
        }
        return result != -1;
    }

    /**
     * Method that inserts sublocations associated with building into database
     *
     * @param placeInfo Building info containing sublocations
     * @return 0 if sublocations was inserted, -1 if not
     */
    private long insertSublocations(PlaceInfo placeInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Sublocation sublocation : placeInfo.getSublocations()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SUBLOCATIONS_COLUMN_ID, sublocation.getId());
            contentValues.put(SUBLOCATIONS_COLUMN_CODE, sublocation.getCode());
            contentValues.put(SUBLOCATIONS_COLUMN_NAME, sublocation.getName());
            contentValues.put(COORDINATES_COLUMN_ID, placeInfo.getID());
            contentValues.put(SUBLOCATIONS_COORDINATES_ID, sublocation.getPlaceID());
            long result = db.insert(TABLE_SUBLOCATIONS, null, contentValues);
            if (result == -1) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Method used for checking if Coordinates Table is empty.
     *
     * @return true if Coordinates Table is empty, false otherwise
     */
    public boolean isPlacesEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT count(*) FROM " + TABLE_COORDINATES, null);
        boolean empty = res.getCount() <= 0;
        res.close();
        return empty;
    }

    /**
     * Method that withdraws all buildings info from database
     *
     * @return List with all buildings
     */
    public List<String> getPlacesNames() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> names = new ArrayList<>();
        Cursor res = db.rawQuery("SELECT " + SUBLOCATIONS_COLUMN_NAME + " FROM " + TABLE_SUBLOCATIONS, null);
        while (res.moveToNext()) {
            names.add(res.getString(0));
        }
        res.close();
        return names;
    }

    public PlaceInfo getPlace(String placeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Sublocation> sublocations = new ArrayList<>();
        Cursor res2 = db.rawQuery("SELECT * FROM " + TABLE_SUBLOCATIONS + " WHERE " + SUBLOCATIONS_COLUMN_NAME + "='" + placeName + "'", null);
        while (res2.moveToNext()) {
            Sublocation sublocation = new Sublocation();
            sublocation.setId(res2.getInt(0));
            sublocation.setCode(res2.getString(1));
            sublocation.setName(res2.getString(2));
            sublocation.setPlaceID(res2.getInt(3));
            sublocations.add(sublocation);
        }
        PlaceInfo placeInfo = new PlaceInfo();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_COORDINATES + " WHERE " + COORDINATES_COLUMN_ID + "=" + sublocations.get(0).getPlaceID(), null);
        while (res.moveToNext()) {
            placeInfo.setID(res.getInt(0));
            placeInfo.setTitle(res.getString(1));
            placeInfo.setPlaceNumber(res.getString(2));
            placeInfo.setAddress(res.getString(3));
            double lon = Double.valueOf(res.getString(4));
            double lat = Double.valueOf(res.getString(5));
            placeInfo.setGeoPoint(new GeoPoint(lat, lon));
            placeInfo.setDescription(res.getString(6));
            placeInfo.setSublocations(sublocations);
        }
        res2.close();
        res.close();
        return placeInfo;
    }

}
