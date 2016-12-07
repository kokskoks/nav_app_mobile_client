package pl.lodz.p.navapp.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.lodz.p.navapp.NavAppApplication;
import pl.lodz.p.navapp.domain.Classes;
import pl.lodz.p.navapp.domain.Lecturer;
import pl.lodz.p.navapp.domain.PlaceInfo;
import pl.lodz.p.navapp.domain.Sublocation;

import static pl.lodz.p.navapp.ApplicationConstants.URL;
import static pl.lodz.p.navapp.service.DatabaseConstants.*;

/**
 * Created by Calgon on 2016-11-13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private int version = -1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COORDINATES_TABLE_CREATE_QUERY);
        db.execSQL(SUBLOCATIONS_TABLE_CREATE_QUERY);
        db.execSQL(TABLE_LECTURERS_CREATE_QUERY);
        db.execSQL(TABLE_CLASS_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_COORDINATES_TABLE);
        db.execSQL(DROP_SUBLOCATIONS_TABLE);
        db.execSQL(DROP_LECTURERS_TABLE);
        db.execSQL(DROP_CLASS_TABLE);
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
        Cursor res = db.rawQuery("SELECT count(*) FROM " + TABLE_SUBLOCATIONS, null);
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

    public Lecturer getLecturer(int ID){
        SQLiteDatabase db = this.getWritableDatabase();
        Lecturer lecturer = new Lecturer();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_LECTURERS + " WHERE " + LECTURERS_COLUMN_ID + "='" + ID + "'", null); // brak pewnosci czy dobry select :) do sprawdzenia
        while(res.moveToNext()){
            lecturer.setID(res.getInt(2));
            lecturer.setFirstName(res.getString(1));
            lecturer.setLastName(res.getString(3));
            lecturer.setDescription(res.getString(0));
            lecturer.setMail(res.getString(4));
            lecturer.setTitle(res.getString(5));
        }
        res.close();
        return lecturer;
    }

    public Classes getClassbyId(int ID){
        SQLiteDatabase db = this.getWritableDatabase();
        Classes classes = new Classes();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_LECTURERS + " WHERE " + CLASS_COLUMN_ID + "='" + ID + "'", null); // brak pewnosci
        while(res.moveToNext()) {
            classes.setID(res.getInt(0));
            classes.setName(res.getString(1));
            classes.setModuleCode(res.getString(2));
            classes.setDescription(res.getString(3));
            classes.setType(res.getString(4));
            classes.setStartHour(res.getInt(5));
            classes.setEndHour(res.getInt(6));
            classes.setWeekday(res.getString(7));
            //skad wziac recturerID
        }
        res.close();
        return classes;
    }

    public Classes getAllClasses(){
        SQLiteDatabase db = this.getWritableDatabase();
        Classes classes = new Classes();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_LECTURERS + "'", null); // ??
        while(res.moveToNext()) {
            classes.setID(res.getInt(0));
            classes.setName(res.getString(1));
            classes.setModuleCode(res.getString(2));
            classes.setDescription(res.getString(3));
            classes.setType(res.getString(4));
            classes.setStartHour(res.getInt(5));
            classes.setEndHour(res.getInt(6));
<<<<<<< HEAD
            classes.setWeekday(res.getString(7));
            //skad wziac recturerID
=======
            classes.setWeekday(res.getInt(7));
            //skad wziac lecturerID
>>>>>>> b065ddadb9ae48f8de51c90b5b263e62b88bec92
        }
        res.close();
        return classes;
    }

    public int checkDBVersion() {
        RequestManager.sendRequest(Request.Method.GET,URL + "/versions", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    JSONObject versionObject = array.getJSONObject(0);
                    version = Integer.parseInt(versionObject.getString("ver"));
                    Toast.makeText(context, "Wersja bazy budynków " + version, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    version = -1;
                    Toast.makeText(context, "Błąd podczas parsowania wersji", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                version = -1;
                Toast.makeText(context, "Błąd podczas pobierania wersji bazy", Toast.LENGTH_SHORT).show();
            }
        });
        return version;
    }

}
