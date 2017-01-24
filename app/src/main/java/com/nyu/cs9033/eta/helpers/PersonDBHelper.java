package com.nyu.cs9033.eta.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nyu.cs9033.eta.models.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Ray on 2016/4/2.
 */
public class PersonDBHelper {

    private static final String TABLE_PERSON = "person";
    private static final String COLUMN_PERSON_ID = "_id";
    private static final String COLUMN_PERSON_POSITION = "position";
    private static final String COLUMN_PERSON_NAME = "name";
    private static final String COLUMN_PERSON_LOCATION = "current_location";
    private static final String COLUMN_PERSON_PHONENUMBER = "phone_number";

    private SQLiteDatabase mDb;

    public PersonDBHelper(SQLiteDatabase db) {
        mDb = db;
    }

    public long insertPerson(Person person) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PERSON_POSITION, person.getPosition());
        contentValues.put(COLUMN_PERSON_NAME, person.getName());
        contentValues.put(COLUMN_PERSON_LOCATION, person.getCurrentLocation());
        contentValues.put(COLUMN_PERSON_PHONENUMBER, person.getPhoneNumber());
        return mDb.insert(TABLE_PERSON, null, contentValues);
    }

    public long insertPersons(List<Person> persons) {
        for (Person person : persons) {
            if (insertPerson(person) == -1) {
                return -1;
            }
        }
        return 1;
    }

    public long deletePerson(Person person) {
        return mDb.delete(TABLE_PERSON, "position = " + person.getPosition(), null);
    }

//    public long updatePersons (List<Person> persons) {
//        deletePerson();
//        for (Person person : persons) {
//            mDb.update(TABLE_PERSON, )
//        }
//    }

    public List<Person> getAllPersons() {
        List<Person> friends = new ArrayList<>();
        Cursor cursor = mDb.rawQuery("select * from " + TABLE_PERSON, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Person friend = new Person(cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_PHONENUMBER)));
            friends.add(friend);
        }
        cursor.close();
        return friends;
    }

    public Person getPersonByPosition(int position) {
        Cursor cursor = mDb.rawQuery("select * from ? where _id = ?",
                new String[]{TABLE_PERSON, String.valueOf(position)});
        Person friend = null;
        if (cursor.moveToFirst()) {
            friend = new Person(cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_POSITION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_PHONENUMBER)));
        }
        cursor.close();
        return friend;
    }

    public List<Person> getPersonByIds(String[] positions) {
        List<Person> friends = new ArrayList<>();
        if (positions.length == 0) {
            return friends;
        }
        String sql = "select * from " + TABLE_PERSON + " where position = " + positions[0];
        for (int i = 1; i < positions.length; i++) {
            sql += " or position = " + positions[i];
        }
        Log.d("%%%%%%%%%%%%%%", sql);
        Cursor cursor = mDb.rawQuery(sql, null);
        Log.d("**********", String.valueOf(cursor.getCount()));
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.d("&&&&&&&&&&&&", String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID))));
            Log.d("||||||||||||", String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_POSITION))));
            Person friend = new Person(cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_POSITION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_PHONENUMBER)));
            friends.add(friend);
            Log.d("!!!!!!!!!", friend.getName());
        }
        Log.d("---------", "------------");
        cursor.close();
        return friends;
    }

    public void close() {
        if (mDb != null) {
            mDb.close();
        }
    }
}
