package com.nyu.cs9033.eta.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nyu.cs9033.eta.controllers.DataHolder;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/4/2.
 */
public class TripDBHelper {

    private static final String TAG = "Trip DB Util";

//    private final PersonDBHelper personDBHelper;
    private SQLiteDatabase mDb;

    public TripDBHelper(SQLiteDatabase db) {
        mDb = db;
//        personDBHelper = new PersonDBHelper(db);
    }

    public long insertTrip (Trip trip) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CommonDBHelper.COLUMN_TRIP_NAME, trip.getName());
        contentValues.put(CommonDBHelper.COLUMN_TRIP_LOCATION, trip.getLocation());
        String positions = "";
        for (Person friend : trip.getFriends()) {
            positions += ", " + friend.getPosition();
        }
        positions = positions.substring(2);
        Log.d(TAG, positions);
        contentValues.put(CommonDBHelper.COLUMN_TRIP_FRIENDS, positions);
        contentValues.put(CommonDBHelper.COLUMN_TRIP_START_TIME, trip.getStartTime());
        contentValues.put(CommonDBHelper.COLUMN_TRIP_END_TIME, trip.getEndTime());
        contentValues.put(CommonDBHelper.COLUMN_TRIP_DESCRIPTION, trip.getDescription());
        contentValues.put(CommonDBHelper.COLUMN_TRIP_WEB_ID, trip.getWebId());
        contentValues.put(CommonDBHelper.COLUMN_TRIP_STATUS, trip.getStatus());
        contentValues.put(CommonDBHelper.COLUMN_TRIP_IS_ARRIVED, trip.isArrived());
        return mDb.insert(CommonDBHelper.TABLE_TRIP, null, contentValues);
    }

    public List<Trip> getAllTrips () {
        List<Trip> trips = new ArrayList<>();
        Cursor cursor = mDb.rawQuery("select * from " + CommonDBHelper.TABLE_TRIP, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String[] friendIds = cursor.getString(
                    cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_FRIENDS)).split(", ");
            List<Person> friends = DataHolder.getInstance().getFriendsByIds(friendIds);
//            Log.d("#####################", String.valueOf(friends.size()));
            Trip trip = new Trip(
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_ID)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_NAME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_LOCATION)),
                    friends,
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_END_TIME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_WEB_ID)),
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_STATUS)),
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_IS_ARRIVED)) == 1
            );
            trips.add(trip);
        }
        cursor.close();
        return trips;
    }

    public Trip getTripById (int _id) {
        Cursor cursor = mDb.rawQuery("select * from " + CommonDBHelper.TABLE_TRIP +
                " where " + CommonDBHelper.COLUMN_TRIP_ID + " = " + _id, null);
        Trip trip = null;
        if (cursor.moveToFirst()) {
            String[] friendIds = cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_FRIENDS)).split(", ");
            List<Person> friends = DataHolder.getInstance().getFriendsByIds(friendIds);
            trip = new Trip(
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_ID)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_NAME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_LOCATION)),
                    friends,
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_END_TIME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_WEB_ID)),
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_STATUS)),
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_IS_ARRIVED)) == 1
            );
        }
        cursor.close();
        return trip;
    }

    public List<Trip> getTripsByName (String name) {
        List<Trip> trips = new ArrayList<>();
        Cursor cursor = mDb.rawQuery("select * from ? where ? = ?",
                new String[]{CommonDBHelper.TABLE_TRIP, CommonDBHelper.COLUMN_TRIP_NAME, name});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String[] friendIds = cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_FRIENDS)).split(", ");
            List<Person> friends = DataHolder.getInstance().getFriendsByIds(friendIds);
            Trip trip = null;
            trip = new Trip(
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_ID)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_NAME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_LOCATION)),
                    friends,
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_END_TIME)),
                    cursor.getString(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_WEB_ID)),
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_STATUS)),
                    cursor.getInt(cursor.getColumnIndex(CommonDBHelper.COLUMN_TRIP_IS_ARRIVED)) == 1
            );
            trips.add(trip);
        }
        cursor.close();
        return trips;
    }

    public void close () {
        if (mDb != null) {
            mDb.close();
        }
    }

    public int updateStatus (Trip trip) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CommonDBHelper.COLUMN_TRIP_STATUS, trip.getStatus());
        Log.d("~~~~~~", trip.get_id() + "");
        return mDb.update(CommonDBHelper.TABLE_TRIP, contentValues,
                CommonDBHelper.COLUMN_TRIP_ID + " = " + trip.get_id(), null);
    }

    public int updateIsArrived (Trip trip) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CommonDBHelper.COLUMN_TRIP_IS_ARRIVED, trip.isArrived());
        Log.d("~~~~~~", trip.get_id() + "");
        return mDb.update(CommonDBHelper.TABLE_TRIP, contentValues,
                CommonDBHelper.COLUMN_TRIP_ID + " = " + trip.get_id(), null);
    }

    public boolean insertTrips(List<Trip> allTrips) {
        for (Trip trip : allTrips) {
            if (insertTrip(trip) == -1) {
                Log.d("Failed to Save trips", trip.get_id() + "");
                return false;
            }
        }
        return true;
    }
}
