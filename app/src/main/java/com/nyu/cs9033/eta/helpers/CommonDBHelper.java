package com.nyu.cs9033.eta.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ray on 2016/4/2.
 */
public class CommonDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "CommonDB";

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "trips";

    public static final String TABLE_PERSON = "person";
    public static final String COLUMN_PERSON_ID = "_id";
    public static final String COLUMN_PERSON_POSITION = "position";
    public static final String COLUMN_PERSON_NAME = "name";
    public static final String COLUMN_PERSON_LOCATION = "current_location";
    public static final String COLUMN_PERSON_PHONENUMBER = "phone_number";

    public static final String TABLE_TRIP = "trip";
    public static final String COLUMN_TRIP_ID = "_id";
    public static final String COLUMN_TRIP_NAME = "name";
    public static final String COLUMN_TRIP_LOCATION = "location";
    public static final String COLUMN_TRIP_FRIENDS = "friends";
    public static final String COLUMN_TRIP_START_TIME = "start_time";
    public static final String COLUMN_TRIP_END_TIME = "end_time";
    public static final String COLUMN_TRIP_DESCRIPTION = "description";
    public static final String COLUMN_TRIP_WEB_ID = "web_id";
    public static final String COLUMN_TRIP_STATUS = "status";
    public static final String COLUMN_TRIP_IS_ARRIVED = "is_arrived";

    private SQLiteDatabase mDb;

    public CommonDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDatabase open () {
        mDb = getWritableDatabase();
        return mDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create tables");

        db.execSQL("create table " + TABLE_PERSON + "(" +
                COLUMN_PERSON_ID + " integer primary key autoincrement," +
                COLUMN_PERSON_POSITION + " integer unique," +
                COLUMN_PERSON_NAME + " text," +
                COLUMN_PERSON_LOCATION + " text," +
                COLUMN_PERSON_PHONENUMBER + " text)");

        db.execSQL("create table " + TABLE_TRIP + "(" +
                COLUMN_TRIP_ID + " integer primary key autoincrement," +
                COLUMN_TRIP_NAME + " text," +
                COLUMN_TRIP_LOCATION + " text," +
                COLUMN_TRIP_FRIENDS + " text," +
                COLUMN_TRIP_START_TIME + " text," +
                COLUMN_TRIP_END_TIME + " text," +
                COLUMN_TRIP_DESCRIPTION + " text," +
                COLUMN_TRIP_WEB_ID + " integer," +
                COLUMN_TRIP_STATUS + " integer," +
                COLUMN_TRIP_IS_ARRIVED + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_PERSON);
        db.execSQL("drop table if exists " + TABLE_TRIP);
        onCreate(db);
    }

}
