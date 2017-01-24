package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.jsons.JSONMessage;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.util.CommonUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewTripActivity extends Activity {

	private static final String TAG = "ViewTripActivity";
	private Trip trip;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_trip);
		trip = getIntent().getExtras().getParcelable("trip");
		Log.v("ViewActivity", "Created Successfully.");
		Log.d("************", trip.get_id() + " - ");
		Button arriveButton = (Button) findViewById(R.id.arrive_button);
		if (trip == null) {
			Toast.makeText(this, "Trip is null!", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Trip is null1");
			arriveButton.setEnabled(false);

		}
		else {
			if (trip.getStatus() == CreateTripActivity.STATUS_FUTURE_TRIP) {
				arriveButton.setText(R.string.activate_trip);
			} else if (trip.isArrived()) {
				arriveButton.setText(R.string.has_arrived);
				arriveButton.setEnabled(false);
			}
			arriveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (trip != null) {
						handleActivateButtonClick(trip, v);
					}
				}
			});
			viewTrip(trip);
		}
	}

	private void handleActivateButtonClick (Trip trip, View view) {
		Button button = (Button) view;
		switch (trip.getStatus()) {
			case CreateTripActivity.STATUS_FUTURE_TRIP:
				for (Trip eachTrip : DataHolder.getInstance().getAllTrips()) {
					if (eachTrip.getStatus() == CreateTripActivity.STATUS_CURRENT_TRIP) {
						Toast.makeText(button.getContext(),
								"You can only activate one current trip!", Toast.LENGTH_SHORT).show();
						return ;
					}
				}
				CommonUtil.updateToCurrentTrip(trip);
				Log.d("<<<<<<<<<<<<<<<<<<<<<<", trip.getStatus() + "");
				button.setText(R.string.arrive);
				break;
			case CreateTripActivity.STATUS_CURRENT_TRIP:
				if (!trip.isArrived()) {
					button.setText(R.string.has_arrived);
					final LocationManager locationManager = (LocationManager) view.getContext()
							.getSystemService(Context.LOCATION_SERVICE);
					Location location =
							locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					CommonUtil.updateLocation(trip, button.getContext(), location);
					CommonUtil.updateArrivalStates(trip);
					Toast.makeText(button.getContext(),
							"You have arrived!", Toast.LENGTH_SHORT).show();
				}
		}
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

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				finish();
			}
		}.execute();
		super.onStop();
	}

//	@Override
//	protected void onDestroy() {
//		Intent intent = new Intent(this, TripHistoryActivity.class);
//		super.onDestroy();
//		startActivity(intent);
//	}

	public void cancelTrip (View v) {
		finish();
	}
	
	/**
	 * Create a Trip object via the recent trip that
	 * was passed to TripViewer via an Intent.
	 * 
	 * @param i The Intent that contains
	 * the most recent trip data.
	 * 
	 * @return The Trip that was most recently
	 * passed to TripViewer, or null if there
	 * is none.
	 */
	public Trip getTrip(Intent i) {

		return (Trip) i.getExtras().getParcelable("sentTrip");

	}

	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip The Trip model used to
	 * populate the View.
	 */
	public void viewTrip(Trip trip) {

		TextView tripName = (TextView) findViewById(R.id.view_name);
		TextView tripLocation = (TextView) findViewById(R.id.view_location);
		TextView tripFriends = (TextView) findViewById(R.id.view_friends);
		TextView tripStartTime = (TextView) findViewById(R.id.view_start_time);
		TextView tripEndTime = (TextView) findViewById(R.id.view_end_time);
		TextView tripDescription = (TextView) findViewById(R.id.view_description);
		TextView tripStatus = (TextView) findViewById(R.id.view_status);

		tripName.setText(trip.getName());
		tripLocation.setText(trip.getLocation());

		String friendsString = "";
		for (Person person : trip.getFriends()) {
			Log.v("view", person.getName());
			friendsString += ", " + person.getName();
		}
        tripFriends.setText(friendsString.substring(2));

        Log.v("Time", trip.getStartTime());

		tripStartTime.setText(trip.getStartTime());
		tripEndTime.setText(trip.getEndTime());
		tripDescription.setText(trip.getDescription().isEmpty() ? "None" : trip.getDescription());
		switch (trip.getStatus()) {
			case CreateTripActivity.STATUS_CURRENT_TRIP:
				tripStatus.setText(R.string.current_trip);
				break;
			case CreateTripActivity.STATUS_FUTURE_TRIP:
				tripStatus.setText(R.string.future_trip);
				break;
			case CreateTripActivity.STATUS_PAST_TRIP:
				tripStatus.setText(R.string.past_trip);
				break;
			default:
				Log.d(TAG, "trip status is wrong:" + trip.getStatus());
		}
	}
}
