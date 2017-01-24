package com.nyu.cs9033.eta.controllers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.nyu.cs9033.eta.helpers.CommonDBHelper;
import com.nyu.cs9033.eta.helpers.TripDBHelper;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/4/24.
 */
public class DataHolder {
    private static final String TAG = "DataHolder";
    public static final String CURRENT_TRIP_LIST_NAME = "current trip";
    public static final String PAST_TRIP_LIST_NAME = "past trip";
    public static final String FUTURE_TRIP_LIST_NAME = "future trip";

    private List<Person> contactFriends;
    private List<Trip> allTrips;
    private List<Trip> currentTrips;
    private List<Trip> pastTrips;
    private List<Trip> futureTrips;

    private static class Singleton {
        private static final DataHolder Instance = new DataHolder();
    }

    private DataHolder () {}

    public static DataHolder getInstance() {
        return Singleton.Instance;
    }

    public boolean isInitiated () {
        return allTrips != null && contactFriends != null;
    }

    /**
     * Save persons in contact to database
     * Save persons in contact to contactFriends
     * @param context
     */
    public void initiatePersons (Context context) {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts._ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
        };

        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        int i = 1;
        contactFriends = new ArrayList<>();
        Cursor friendCursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);
        if (friendCursor != null) {
            while (friendCursor.moveToNext()) {
                int nameIndex = friendCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String name = friendCursor.getString(nameIndex);
                int phoneNumberIndex = friendCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = friendCursor.getString(phoneNumberIndex);
                String currentLocation = name + " Current Location";
                Person friend = new Person(i, name, currentLocation, phoneNumber);
                contactFriends.add(friend);
                i++;
            }
//            PersonDBHelper personDBHelper = new PersonDBHelper(new CommonDBHelper(context).open());
//            List<Person> dbFriends = personDBHelper.getAllPersons();
//            personDBHelper.insertPersons(contactFriends);
//            dbFriends = personDBHelper.getAllPersons();
//            Log.d("Persons in DB", String.valueOf(dbFriends.size()));
            Log.d(TAG, "Save successfully");
        }
        else {
            contactFriends = null;
        }
    }

    /**
     * @param context
     */
    public void initialAllTrips (Context context) {
        TripDBHelper tripDBHelper = new TripDBHelper(new CommonDBHelper(context).open());
        allTrips = tripDBHelper.getAllTrips();
        currentTrips = new ArrayList<>();
        pastTrips = new ArrayList<>();
        futureTrips = new ArrayList<>();
        for (Trip trip : allTrips) {
            if (CommonUtil.isPastTrip(trip)) {
                trip.setStatus(CreateTripActivity.STATUS_PAST_TRIP);
                trip.setIsArrived(true);
            }
        }
        initiateTripsStatus();
        tripDBHelper.close();
    }

    //Get contactFriends
    public List<Person> getContactFriends() {
        return contactFriends;
    }

    public List<Person> getFriendsByIds(String[] ids) {
        List<Person> friends = new ArrayList<>();
        if (ids.length == 0) {
            return friends;
        }
        for (String id : ids) {
            for (Person friend : contactFriends) {
                if (friend.getPosition() == Integer.valueOf(id)) {
                    friends.add(friend);
                }
            }
        }
        return friends;
    }

    //Get all trips
    public List<Trip> getAllTrips() {
        return allTrips;
    }

    public void initiateTripsStatus () {
        for (Trip trip : allTrips) {
            switch (trip.getStatus()) {
                case CreateTripActivity.STATUS_CURRENT_TRIP:
                    currentTrips.add(trip);
                    break;
                case CreateTripActivity.STATUS_FUTURE_TRIP:
                    futureTrips.add(trip);
                    break;
                case CreateTripActivity.STATUS_PAST_TRIP:
                    pastTrips.add(trip);
                    break;
            }
        }
    }

    public void changeTripStatus (Trip trip, int status) {
        deleteTripByStatus(trip, status);
        setTripStatusFrom(trip, status);
    }

    public void deleteTripByStatus (Trip trip, int status) {
        Log.d("123456789", trip.getStatus() + "");
        switch (status) {
            case CreateTripActivity.STATUS_CURRENT_TRIP:
                currentTrips.remove(trip);
                break;
            case CreateTripActivity.STATUS_PAST_TRIP:
                pastTrips.remove(trip);
                break;
            case CreateTripActivity.STATUS_FUTURE_TRIP:
                futureTrips.remove(trip);
                break;
        }
    }

    public void setTripStatusFrom (Trip trip, int status) {
        for (Trip eachTrip : allTrips) {
            if (eachTrip.get_id() == trip.get_id()) {
                if (status == CreateTripActivity.STATUS_CURRENT_TRIP) {
                    pastTrips.add(trip);
                    eachTrip.setStatus(CreateTripActivity.STATUS_PAST_TRIP);
                    eachTrip.setIsArrived(true);
                }
                else {
                    currentTrips.add(trip);
                    eachTrip.setStatus(CreateTripActivity.STATUS_CURRENT_TRIP);
                }
            }
        }
        Log.d("qwertyuu", trip.getStatus() + "");
    }

    public List<Trip> getCurrentTrips() {
        return currentTrips;
    }

    public List<Trip> getPastTrips() {
        return pastTrips;
    }

    public List<Trip> getFutureTrips() {
        return futureTrips;
    }
}
