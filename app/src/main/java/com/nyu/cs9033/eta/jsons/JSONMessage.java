package com.nyu.cs9033.eta.jsons;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ray on 2016/4/23.
 */
public class JSONMessage implements MessageInterface {
    private static final String COMMAND = "command";
    private static final String LOCATION = "location";
    private static final String DATETIME = "datetime";
    private static final String PEOPLE = "people";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TRIP_ID = "trip_id";

    private String command;
    private List<String> location;
    private long datetime;
    private String people;

    private double latitude;
    private double longitude;

    private long tripId;

    //If the command is "CREATE_TRIP"
    public JSONMessage(String command, List<String> location, long datetime, String people) {
        this.command = command;
        this.location = location;
        this.datetime = datetime;
        this.people = people;
    }

    //If the command is "UPDATE_LOCATION"
    public JSONMessage(String command, double latitude, double longitude, long datetime) {
        this.command = command;
        this.latitude = latitude;
        this.longitude = longitude;
        this.datetime = datetime;
    }

    //If the command is "TRIP_STATUS"
    public JSONMessage(String command, long tripId) {
        this.command = command;
        this.tripId = tripId;
    }

    @Override
    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        switch (command) {
            case "CREATE_TRIP":
                JSONArray location = new JSONArray(this.location);
                json.put(COMMAND, command).put(LOCATION, location).put(DATETIME, datetime)
                        .put(PEOPLE, people);
                Log.d("JSONMessage", json.toString());
                return json;
            case "UPDATE_LOCATION":
                json.put(COMMAND, command).put(LATITUDE, latitude).put(LONGITUDE, longitude)
                        .put(DATETIME, datetime);
                Log.d("JSONMessage", json.toString());
                return json;
            case "TRIP_STATUS":
                json.put(COMMAND, command).put(TRIP_ID, tripId);
                Log.d("JSONMessage", json.toString());
                return json;
            default:
                Log.d("JSONMessage", "Not valid command - " + command);
                return null;
        }
    }

    public String getCommand() {
        return command;
    }

    public List<String> getLocation() {
        return location;
    }

    public long getDatetime() {
        return datetime;
    }

    public String getPeople() {
        return people;
    }
}
