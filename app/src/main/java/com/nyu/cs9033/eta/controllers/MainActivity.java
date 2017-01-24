package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.helpers.CommonDBHelper;
import com.nyu.cs9033.eta.helpers.TripDBHelper;
import com.nyu.cs9033.eta.jsons.JSONMessage;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.util.CommonUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private static final int SECONDS_PER_DAY = 86400;
	private static final int SECONDS_PER_HOUR = 3600;
	private static final int SECONDS_PER_MINUTE = 60;

	private Trip trip;

	private CommonDBHelper commonDBHelper;
	private SQLiteDatabase commonDB;
	private TripDBHelper tripDBHelper;

	TextView tripNameView;
	TextView textLogo;
	ImageView imageLogo;
	TextView friendNameView1;
	TextView friendNameView2;
	TextView friendDistanceView1;
	TextView friendDistanceView2;
	TextView friendTimeView1;
	TextView friendTimeView2;
	CardView tripStatusView;

	List<Double> distances;
	List<String> names;
	List<Long> times;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Context context = this;
		tripStatusView = (CardView) findViewById(R.id.main_card_view);
		textLogo = (TextView) findViewById(R.id.big_app_name);
		imageLogo = (ImageView) findViewById(R.id.big_logo);
	}

	@Override
	protected void onStart() {
		super.onStart();
		final Context context = this;
		if (!DataHolder.getInstance().isInitiated()) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					CommonUtil.getTripsFromDB(context);
					Log.d(TAG, "+++ Get Data +++");
					return null;
				}
			}.execute();
		}
		else {
			new AsyncTask<Void, Void, Trip>() {
				@Override
				protected Trip doInBackground(Void... params) {
					for (Trip trip : DataHolder.getInstance().getAllTrips()) {
						if (trip.getStatus() == CreateTripActivity.STATUS_CURRENT_TRIP) {
							return trip;
						}
					}
					return null;
				}

				@Override
				protected void onPostExecute(Trip trip) {
					if (trip != null) {
						displayCurrentTrip(trip);
					}
					else {
						tripStatusView.setVisibility(View.GONE);
						displayLogo();
					}
					super.onPostExecute(trip);
				}
			}.execute();
		}
	}

	private void displayLogo() {
		textLogo.setVisibility(View.VISIBLE);
		imageLogo.setVisibility(View.VISIBLE);
	}

	private void displayCurrentTrip (final Trip trip) {
		tripNameView = (TextView) findViewById(R.id.main_trip_name);
		friendNameView1 = (TextView) findViewById(R.id.main_person_name_1);
		friendNameView2 = (TextView) findViewById(R.id.main_person_name_2);
		friendDistanceView1 = (TextView) findViewById(R.id.main_distance_left_1);
		friendDistanceView2 = (TextView) findViewById(R.id.main_distance_left_2);
		friendTimeView1 = (TextView) findViewById(R.id.main_time_left_1);
		friendTimeView2 = (TextView) findViewById(R.id.main_time_left_2);
		ImageView bigLogo = (ImageView) findViewById(R.id.big_logo);
//		bigLogo.setVisibility(View.GONE);
		bigLogo.getLayoutParams().height = 300;
		bigLogo.requestLayout();
		TextView bigAppName = (TextView) findViewById(R.id.big_app_name);
		bigAppName.setVisibility(View.GONE);
		tripNameView.setText(getResources().getString(R.string.current_trip) + ": " + trip.getName());
		getTripStatus(trip.getWebId());
	}

	private void getTripStatus (final long tripWebId) {
		new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				JSONMessage jsonMessage = new JSONMessage("TRIP_STATUS", tripWebId);
				try {
					String response = CommonUtil.post(CreateTripActivity.URL,
							jsonMessage.toJSON().toString());
					Log.d(TAG, response);
					JSONObject jsonResponse = new JSONObject(response);
					return jsonResponse;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(JSONObject jsonResponse) {
				super.onPostExecute(jsonResponse);
				JSONArray distanceJSONArray = null;
				try {
					distanceJSONArray = new JSONArray(jsonResponse.getString("distance_left"));
					JSONArray namesJSONArray = new JSONArray(jsonResponse.getString("people"));
					JSONArray timesJSONArray = new JSONArray(jsonResponse.getString("time_left"));
					distances = new ArrayList<>();
					names = new ArrayList<>();
					times = new ArrayList<>();
					if(namesJSONArray.length() < 2) {

					}
					for (int i = 0; i < 2; i++) {
						distances.add(distanceJSONArray.getDouble(i));
						names.add(namesJSONArray.getString(i));
						times.add(timesJSONArray.getLong(i));
					}
					updateUI();
				} catch (JSONException | NullPointerException e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}

	private void updateUI() {
		tripStatusView.setVisibility(View.VISIBLE);
		friendDistanceView1.setText(String.format("%.2f", distances.get(0)) + " miles");
		friendDistanceView2.setText(String.format("%.2f", distances.get(1)) + " miles");
		friendNameView1.setText(names.get(0));
		friendNameView2.setText(names.get(1));
		friendTimeView1.setText(changeTime(times.get(0)));
		friendTimeView2.setText(changeTime(times.get(1)));
	}

	/**
	 * This method should start the
	 * Activity responsible for creating
	 * a Trip.
	 */
	public void startCreateTripActivity(View v) {
		Intent intent = new Intent(this, CreateTripActivity.class);
		startActivity(intent);
	}

	/**
	 * This method should start the
	 * Activity responsible for viewing
	 * a Trip.
	 */
	public void viewHistoryTripActivity(View v) {
		Intent intent = new Intent(this, TripHistoryActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		final Context context = this;
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				CommonUtil.saveTripsToDB(context);
				Log.d(TAG, "--- Save Data ---");
				return null;
			}
		}.execute();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "DESTROY!");
		super.onDestroy();
	}

	private String changeTime (long time) {
		String result = "";
		long day = time / SECONDS_PER_DAY;
		time = time % SECONDS_PER_DAY;
		long hour = time / SECONDS_PER_HOUR;
		time = time % SECONDS_PER_HOUR;
		long minute = time / SECONDS_PER_MINUTE;
		time = time % SECONDS_PER_MINUTE;
		long second = time;
		result = validTime(result, day, "day") +
				validTime(result, hour, "hour") +
				validTime(result, minute, "minute") +
				validTime(result, second, "second");
		return result;
	}

	private String validTime (String result, long time, String timeName) {
		if (time != 0) {
			if (time == 1) {
				result = time + " " + timeName + " ";
			}
			else {
				result = time + " " + timeName + "s ";
			}
		}
		return result;
	}

}
