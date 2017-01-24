package com.nyu.cs9033.eta.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.nyu.cs9033.eta.controllers.CreateTripActivity;
import com.nyu.cs9033.eta.controllers.DataHolder;
import com.nyu.cs9033.eta.helpers.CommonDBHelper;
import com.nyu.cs9033.eta.helpers.TripDBHelper;
import com.nyu.cs9033.eta.jsons.JSONMessage;
import com.nyu.cs9033.eta.models.Trip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Ray on 2016/4/25.
 */
public class CommonUtil {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm");
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static Date stringToDate(String dateString) throws ParseException {
        return DATE_FORMAT.parse(dateString);
    }

    //No time
    public static String getMDY(String date) {
        return date.substring(0, date.indexOf(" "));
    }

    public static void getTripsFromDB (Context context) {
        CommonDBHelper commonDBHelper = new CommonDBHelper(context);
        SQLiteDatabase commonDB = commonDBHelper.open();
        commonDB.delete(CommonDBHelper.TABLE_PERSON, null, null);
        commonDB.execSQL("DELETE FROM sqlite_sequence WHERE name = '" + CommonDBHelper.TABLE_PERSON + "'");
        TripDBHelper tripDBHelper = new TripDBHelper(commonDB);
        DataHolder.getInstance().initiatePersons(context);//Initiate person data
        DataHolder.getInstance().initialAllTrips(context);//Initiate person data
        tripDBHelper.close();
        commonDB.close();
    }

    public static void saveTripsToDB (Context context) {
        CommonDBHelper commonDBHelper = new CommonDBHelper(context);
        SQLiteDatabase commonDB = commonDBHelper.open();
        commonDB.delete(CommonDBHelper.TABLE_TRIP, null, null);
        commonDB.execSQL("DELETE FROM sqlite_sequence WHERE name = '" + CommonDBHelper.TABLE_TRIP + "'");
        TripDBHelper tripDBHelper = new TripDBHelper(commonDB);

        if (tripDBHelper.insertTrips(DataHolder.getInstance().getAllTrips())) {
            Log.d("CommonUtil", "trips saved successfully " + DataHolder.getInstance().getAllTrips().size());
        }

        tripDBHelper.close();
        commonDB.close();
    }


    public static String post (String url, String json) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateToCurrentTrip (Trip trip) {
        List<Trip> trips = DataHolder.getInstance().getAllTrips();
        for (int i = 0; i < trips.size(); i++) {
            Log.d("CCCCCCCCCCCCCCCCCCC", trips.get(i).get_id() + " - " + trip.get_id());
            if (trips.get(i).get_id() == trip.get_id()) {
                Log.d("AAAAAAAAAAAAAAAA", trips.size() + "");
                DataHolder.getInstance().getAllTrips().get(i).setStatus(CreateTripActivity.STATUS_CURRENT_TRIP);
            }
            Log.d("BBBBBBBBBBB", DataHolder.getInstance().getAllTrips().get(i).isArrived() + "");
        }
    }

    public static void updateArrivalStates (Trip trip) {
        List<Trip> trips = DataHolder.getInstance().getAllTrips();
        for (int i = 0; i < trips.size(); i++) {
            Log.d("CCCCCCCCCCCCCCCCCCC", trips.get(i).get_id() + " - " + trip.get_id());
            if (trips.get(i).get_id() == trip.get_id()) {
                Log.d("AAAAAAAAAAAAAAAA", trips.size() + "");
                DataHolder.getInstance().getAllTrips().get(i).setIsArrived(true);
            }
            Log.d("BBBBBBBBBBB", DataHolder.getInstance().getAllTrips().get(i).isArrived() + "");
        }
    }


    public static void updateLocation(final Trip trip, Context context, final Location location) {

        try {
            int responseCode = new AsyncTask<String, Void, Integer>() {
                @Override
                protected Integer doInBackground(String... params) {
                    try {
                        long datetime = CommonUtil.stringToDate(trip.getEndTime()).getTime() -
                                CommonUtil.stringToDate(trip.getStartTime()).getTime();
                        Log.d("Datetime", datetime + "");

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        JSONMessage jsonMessage = new JSONMessage(
                                "UPDATE_LOCATION",
                                latitude,
                                longitude,
                                datetime);
                        return new JSONObject(CommonUtil.post(CreateTripActivity.URL,
                                jsonMessage.toJSON().toString())).getInt("response_code");
                    } catch (ParseException | JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute(CreateTripActivity.URL).get();
            Log.d("-----", "response_code = " + responseCode);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPastTrip (Trip trip) {
        Date currentTime = new Date(System.currentTimeMillis());
        try {
            if (currentTime.after(CommonUtil.stringToDate(trip.getEndTime()))) {
                return true;
            }
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap readBitMap(Context context, int resId) {

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is,null,opt);

    }
}
