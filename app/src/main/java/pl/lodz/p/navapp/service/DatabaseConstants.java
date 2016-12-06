package pl.lodz.p.navapp.service;

/**
 * Created by Łukasz Świtoń on 04.12.2016.
 */

public class DatabaseConstants {
    public static final String DATABASE_NAME = "navAppDB.db";
    //Coordinates table
    private static final String TABLE_COORDINATES = "COORDINATES";
    private static final String COORDINATES_COLUMN_ID = "COORD_ID";
    private static final String COORDINATES_COLUMN_TITLE = "COORD_TITLE";
    private static final String COORDINATES_COLUMN_PLACE_NUMBER = "COORD_PLACE_NUMBER";
    private static final String COORDINATES_COLUMN_ADDRESS = "COORD_ADDRESS";
    private static final String COORDINATES_COLUMN_LONGITUDE = "COORD_LONGITUDE";
    private static final String COORDINATES_COLUMN_LATITUDE = "COORD_LATITUDE";
    private static final String COORDINATES_COLUMN_DESCRIPTION = "COORD_DESCRIPTION";

    //Sublocations table
    private static final String TABLE_SUBLOCATIONS = "SUBLOCATIONS";
    private static final String SUBLOCATIONS_COLUMN_ID = "SUB_ID";
    private static final String SUBLOCATIONS_COLUMN_CODE = "SUB_CODE";
    private static final String SUBLOCATIONS_COLUMN_NAME = "SUB_NAME";
    private static final String SUBLOCATIONS_COORDINATES_ID = "SUB_COORD_ID";

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

    //UNIVERSITY CLASS RESOURCE


    //UNIVERSITY CLASS TABLE
    private static final String TABLE_CLASS = "CLASS";
    private static final String CLASS_COLUMN_ID = "CLASS_ID";
    private static final String CLASS_COLUMN_NAME = "CLASS_NAME";
    private static final String CLASS_COLUMN_MODULE_CODE = "CLASS_MODULE_CODE";
    private static final String CLASS_COLUMN_DESCRIPTION = "CLASS_DESCRIPTION";
    private static final String CLASS_COLUMN_TYPE = "CLASS_TYPE";
    private static final String CLASS_COLUMN_START_HOUR = "CLASS_START_HOUR";
    private  static final String CLASS_COLUMN_END_HOUR = "CLASS_END_HOUR";
    private static final String CLASS_COLUMN_WEEKDAY = "CLASS_WEEKDAY";

    private static final String CLASS_LECTURER_ID = "CLASS_LECTURER_ID" ;



    //SUBCLASSES WEEK
    private static final String TABLE_WEEKS = "WEEKS";
    private static final String WEEK_COLUMN_ID = "WEEK_ID";
    private static final String WEEK_COLUMN_NUMBER = "WEEK_NUMBER";
    private static final String WEEK_COLUMN_STARTDATE = "WEEK_STARTDATE";
    private static final String WEEK_COLUMN_ENDDATE = "WEEK_ENDATE";

    static final String TABLE_WEEKS_CREATE_QUERY = "create table "
            + TABLE_WEEKS + "("
            + WEEK_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + WEEK_COLUMN_NUMBER + " INTEGER, "
            + WEEK_COLUMN_STARTDATE + " TEXT, "
            + WEEK_COLUMN_ENDDATE + " TEXT) ";



    private  static final String TABLE_LECTURERS = "LECTRURERS";
    private  static final String LECTURERS_COLUMN_ID= "LECTURERS_ID";
    private static final String LECTURERS_COLUMN_FIRSTNAME= "LECTURERS_FIRSTNAME";
    private static final String LECTURERS_COLUMN_LASTNAME= "LECTURERS_LASTNAME";
    private static final String LECTURERS_COLUMN_TITLE= "LECTURERS_TITLE";
    private static final String LECTURERS_COLUMN_DESCRIPTION= "LECTURERS_DESCRIPTION";
    private static final String LECTURERS_COLUMN_MAIL= "LECTURERS_MAIL";

    static final String TABLE_LECTURERS_CREATE_QUERY = "create table "
            + TABLE_LECTURERS + "("
            + LECTURERS_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + LECTURERS_COLUMN_FIRSTNAME + " TEXT, "
            + LECTURERS_COLUMN_LASTNAME + " TEXT, "
            + LECTURERS_COLUMN_TITLE + " TEXT) "
            + LECTURERS_COLUMN_DESCRIPTION + " TEXT, "
            + LECTURERS_COLUMN_MAIL + " TEXT) ";


    static final String TABLE_CLASS_CREATE_QUERY = "create table "
            + TABLE_CLASS + "("
            + CLASS_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + CLASS_COLUMN_NAME + " TEXT, "
            + CLASS_COLUMN_MODULE_CODE + " TEXT, "
            + CLASS_COLUMN_DESCRIPTION + " TEXT, "
            + CLASS_COLUMN_TYPE + " TEXT,"
            + CLASS_COLUMN_START_HOUR + " INTEGER, "
            + CLASS_COLUMN_END_HOUR + " INTEGER, "
            + CLASS_COLUMN_WEEKDAY + " TEXT, "
            + CLASS_LECTURER_ID + " INTEGER, FOREIGN KEY ("
            + CLASS_LECTURER_ID + ") REFERENCES " + TABLE_LECTURERS + "(" + LECTURERS_COLUMN_ID + "))";

    static final String DROP_CLASS_TABLE = "DROP TABLE IF EXISTS" + TABLE_CLASS;

    static final String DROP_LECTURERS_TABLE = "DROP TABLE IF EXISTS" + TABLE_LECTURERS;

    private DatabaseConstants(){

    }

}
