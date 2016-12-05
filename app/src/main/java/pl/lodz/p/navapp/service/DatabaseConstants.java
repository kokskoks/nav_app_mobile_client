package pl.lodz.p.navapp.service;

/**
 * Created by Łukasz Świtoń on 04.12.2016.
 */

public class DatabaseConstants {
    public static final String DATABASE_NAME = "navAppDB.db";
    //Coordinates table
    static final String TABLE_COORDINATES = "COORDINATES";
    static final String COORDINATES_COLUMN_ID = "COORD_ID";
    static final String COORDINATES_COLUMN_TITLE = "COORD_TITLE";
    static final String COORDINATES_COLUMN_PLACE_NUMBER = "COORD_PLACE_NUMBER";
    static final String COORDINATES_COLUMN_ADDRESS = "COORD_ADDRESS";
    static final String COORDINATES_COLUMN_LONGITUDE = "COORD_LONGITUDE";
    static final String COORDINATES_COLUMN_LATITUDE = "COORD_LATITUDE";
    static final String COORDINATES_COLUMN_DESCRIPTION = "COORD_DESCRIPTION";

    //Sublocations table
    static final String TABLE_SUBLOCATIONS = "SUBLOCATIONS";
    static final String SUBLOCATIONS_COLUMN_ID = "SUB_ID";
    static final String SUBLOCATIONS_COLUMN_CODE = "SUB_CODE";
    static final String SUBLOCATIONS_COLUMN_NAME = "SUB_NAME";
    static final String SUBLOCATIONS_COORDINATES_ID = "SUB_COORD_ID";

    static final String COORDINATES_TABLE_CREATE_QUERY = "create table "
            + TABLE_COORDINATES + "("
            + COORDINATES_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COORDINATES_COLUMN_TITLE + " TEXT, "
            + COORDINATES_COLUMN_PLACE_NUMBER + " TEXT, "
            + COORDINATES_COLUMN_ADDRESS + " TEXT, "
            + COORDINATES_COLUMN_LONGITUDE + " TEXT,"
            + COORDINATES_COLUMN_LATITUDE + " TEXT, "
            + COORDINATES_COLUMN_DESCRIPTION + " TEXT)";

    static final String SUBLOCATIONS_TABLE_CREATE_QUERY = "create table "
            + TABLE_SUBLOCATIONS + "("
            + SUBLOCATIONS_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + SUBLOCATIONS_COLUMN_CODE + " TEXT, "
            + SUBLOCATIONS_COLUMN_NAME + " TEXT, "
            + COORDINATES_COLUMN_ID + " INTEGER, "
            + SUBLOCATIONS_COORDINATES_ID + " INTEGER, FOREIGN KEY ("
            + SUBLOCATIONS_COORDINATES_ID + ") REFERENCES " + TABLE_COORDINATES + "(" + COORDINATES_COLUMN_ID + "))";

    static final String DROP_COORDINATES_TABLE = "DROP TABLE IF EXISTS" + TABLE_COORDINATES;

    static final String DROP_SUBLOCATIONS_TABLE = "DROP TABLE IF EXISTS" + TABLE_SUBLOCATIONS;

    private DatabaseConstants(){

    }

}
