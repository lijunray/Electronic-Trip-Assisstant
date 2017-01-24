package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.jsons.JSONMessage;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.util.CommonUtil;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreateTripActivity extends Activity {

	private static final String TAG = "CreateTripActivity";

	public static final int STATUS_FUTURE_TRIP = 0;
	public static final int STATUS_CURRENT_TRIP = 1;
	public static final int STATUS_PAST_TRIP = 2;

	private static final int CONTACT_PICKER_RESULT = 1;
	private static final int LOCATION_PICKER_RESULT = 2;
	public static final String URL = "http://cs9033-homework.appspot.com/";

	private ArrayList<Person> friends;
	private ArrayList<String> location;
	private StringBuilder friendNames;

	private EditText mNameEditText;
	private EditText mLocationEditText;
	private EditText mTypeEditText;
	private TextView mFriendsTextView;
	private static TextView mStartTimeTextView;
	private static TextView mEndTimeTextView;

	private volatile DatePickerFragment datePickerFragment;
	private volatile TimePickerFragment timePickerFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_trip);

		friends = new ArrayList<>();

		mNameEditText = (EditText) findViewById(R.id.trip_name);
		mLocationEditText = (EditText) findViewById(R.id.trip_location);
		mTypeEditText = (EditText) findViewById(R.id.trip_type);
		mFriendsTextView = (TextView) findViewById(R.id.trip_friends);
		mStartTimeTextView = (TextView) findViewById(R.id.trip_start_time);
		mEndTimeTextView = (TextView) findViewById(R.id.trip_end_time);

		Log.v("CreateActivity", "Create Successfully!");
		Button createTripButton = (Button) findViewById(R.id.submit_button);
		createTripButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (areBlanks()) {
					Toast.makeText(getApplicationContext(), "Some blanks should be filled.", Toast.LENGTH_SHORT).show();
				}
				else {
					Trip trip = createTrip();
					if (trip != null && saveTrip(trip)) {
						Log.v("create:time", trip.getStartTime());
						Log.v("SubmitTrip", "Trip sent back successfully.");
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"Saving trip is failed. Please Try again!", Toast.LENGTH_SHORT).show();
						Log.v("SubmitTrip", "Trip failed to be sent back.");
					}
				}
			}
		});
		setViewOnfocusChangeListeners();
	}

	private void setViewOnfocusChangeListeners() {
		mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && mNameEditText.getText().length() == 0) {
					mNameEditText.setHint("Name can not be blank!");
					mNameEditText.setHintTextColor(getResources().getColor(R.color.hint_warn));
				}
			}
		});

		mLocationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && mLocationEditText.getText().length() == 0) {
					mLocationEditText.setHint("Location can not be blank!");
					mLocationEditText.setHintTextColor(getResources().getColor(R.color.hint_warn));
				}
			}
		});

		mTypeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && mTypeEditText.getText().length() == 0) {
					mTypeEditText.setHint("Type can not be blank!");
					mTypeEditText.setHintTextColor(getResources().getColor(R.color.hint_warn));
				}
			}
		});

		mFriendsTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && mFriendsTextView.getText().length() == 0) {
					mFriendsTextView.setHint("Friends can not be blank!");
					mFriendsTextView.setHintTextColor(getResources().getColor(R.color.hint_warn));
				}
			}
		});

		mStartTimeTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && mStartTimeTextView.getText().length() == 0) {
					mStartTimeTextView.setHint("Time can not be blank!");
					mStartTimeTextView.setHintTextColor(getResources().getColor(R.color.hint_warn));
				}
			}
		});

		mEndTimeTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && mEndTimeTextView.getText().length() == 0) {
					mEndTimeTextView.setHint("Time can not be blank!");
					mEndTimeTextView.setHintTextColor(getResources().getColor(R.color.hint_warn));
				}
			}
		});
	}

	/**
	 * This method should be used to
	 * instantiate a Trip model object.
	 *
	 * @return The Trip as represented
	 * by the View.
	 */
	public Trip createTrip() {
		String tripName = ((EditText) findViewById(R.id.trip_name)).getText().toString();
		String tripLocation = mLocationEditText.getText().toString();
		String tripStartTime = mStartTimeTextView.getText().toString();
		String tripEndTime = mEndTimeTextView.getText().toString();
		String tripDescription = ((EditText) findViewById(R.id.trip_description)).getText().toString();
		String tripWebId = "";
		try {
			final long datetime = CommonUtil.stringToDate(tripEndTime).getTime() -
					CommonUtil.stringToDate(tripStartTime).getTime();
			Log.d("CREATE_TRIP", String.valueOf(datetime));
			if (datetime < 0) {
				Toast.makeText(this, "End time must be after Start time!", Toast.LENGTH_SHORT).show();
				return null;
			}
			if (location != null) {
				final ConnectivityManager manager =
						(ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
				try {
					tripWebId = new AsyncTask<String, Void, String>() {
						@Override
						protected String doInBackground(String... params) {
							JSONMessage jsonMessage = new JSONMessage(
									"CREATE_TRIP",
									location,
									datetime,
									friendNames.toString().substring(1));
							final String jsonString;
							try {
								jsonString = jsonMessage.toJSON().toString();
								NetworkInfo networkInfo = manager.getActiveNetworkInfo();
								if (networkInfo != null && networkInfo.isConnected()) {
									return parseResponse(CommonUtil.post(URL, jsonString));
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							return null;
						}
					}.execute(URL).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
//				Toast.makeText(this, String.valueOf(tripWebId), Toast.LENGTH_SHORT).show();
		Log.v("Create:time", tripStartTime);

		if (tripWebId.isEmpty()) {
			Log.d("web id", tripWebId);
			return null;
		}
		return new Trip(tripName, tripLocation, friends, tripStartTime, tripEndTime,
				tripDescription, Long.parseLong(tripWebId), STATUS_FUTURE_TRIP);
	}

	public void searchForLocation(View view) {
		Log.d(TAG, "Search Button Clicked.");
		String location = mLocationEditText.getText().toString();
		String type = mTypeEditText.getText().toString();
		if (location.length() == 0 || type.length() == 0) {
			Toast.makeText(this, "Invalid Location or Type", Toast.LENGTH_SHORT).show();
		} else {
			String extra = location + "::" + type;
			Uri uri = Uri.parse("location://com.example.nyu.hw3api");
			Intent searchLocationIntent = new Intent(Intent.ACTION_VIEW, uri);
			PackageManager packageManager = getPackageManager();
			List<ResolveInfo> activities = packageManager.queryIntentActivities(searchLocationIntent, 0);
			boolean isIntentSafe = activities.size() > 0;

			Log.d(TAG, String.valueOf(isIntentSafe));
			if (isIntentSafe) {
//				Toast.makeText(this, extra, Toast.LENGTH_SHORT).show();
				Log.d("Search", extra);
				searchLocationIntent.putExtra("searchVal", extra);
				startActivityForResult(searchLocationIntent, LOCATION_PICKER_RESULT);
			} else {
				Toast.makeText(this, "You have not installed HW3API!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void selectFromContact(View view) {
		Intent contactIntent = new Intent(this, ContactListActivity.class);
		startActivityForResult(contactIntent, CONTACT_PICKER_RESULT);
	}

	public static class DatePickerFragment extends DialogFragment
			implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (getArguments().getInt("viewType") == R.id.trip_start_time) {
				mStartTimeTextView.append((month + 1) + "/" + day + "/" + year);
			}
			else {
				mEndTimeTextView.append((month + 1) + "/" + day + "/" + year);
			}
		}
	}

	public static class TimePickerFragment extends DialogFragment
			implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute,
					android.text.format.DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (getArguments().getInt("viewType") == R.id.trip_start_time) {
				mStartTimeTextView.append(" " + hourOfDay + ":" + minute);
			}
			else {
				mEndTimeTextView.append(" " + hourOfDay + ":" + minute);
			}
		}
	}

	public void selectTime(View view) {
		((TextView)view).setText("");
		timePickerFragment = new TimePickerFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("viewType", view.getId());
		timePickerFragment.setArguments(bundle);
		timePickerFragment.show(getFragmentManager(), "timePicker");
		datePickerFragment = new DatePickerFragment();
		datePickerFragment.setArguments(bundle);
		datePickerFragment.show(getFragmentManager(), "datePicker");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CONTACT_PICKER_RESULT && resultCode == RESULT_OK) {
			friends = data.getExtras().getParcelableArrayList("savedFriends");
			friendNames = new StringBuilder();
			for (Person friend : friends) {
				friendNames.append("," + friend.getName());
			}
			mFriendsTextView.setText(friendNames.toString().substring(1));
		}

		if (requestCode == LOCATION_PICKER_RESULT && resultCode == RESULT_FIRST_USER) {
			location = new ArrayList<>();
			String locationName = data.getExtras().getStringArrayList("retVal").get(0).toString();
			String locationAddress = data.getExtras().getStringArrayList("retVal").get(1).toString();
			String latitude = data.getExtras().getStringArrayList("retVal").get(2);
			String longitude = data.getExtras().getStringArrayList("retVal").get(3);
			location.add(locationName);
			location.add(locationAddress);
			location.add(latitude);
			location.add(longitude);
			mLocationEditText.setText(locationName);
			Log.d(TAG, locationName);
		}
	}

	/**
	 * For HW2 you should treat this method as a
	 * way of sending the Trip data back to the
	 * main Activity.
	 * <p>
	 * Note: If you call finish() here the Activity
	 * will end and pass an Intent back to the
	 * previous Activity using setResult().
	 *
	 * @return whether the Trip was successfully
	 * saved.
	 */
	public boolean saveTrip(Trip trip) {
//		long savePersonsSuccessfully = personDBHelper.insertPersons(trip.getFriends());
		DataHolder.getInstance().getAllTrips().add(trip);
		DataHolder.getInstance().getFutureTrips().add(trip);
//		if (savePersonsSuccessfully == -1) {
//			Log.d(TAG, "failed to save persons.");
//			return false;
//		}
		Log.v("Create", "success to save trip and persons.");
		return true;
	}

	private boolean areBlanks() {
		Log.d(TAG, mNameEditText.getText().toString());
		Log.d(TAG, mLocationEditText.getText().toString());
		Log.d(TAG, mFriendsTextView.getText().toString());
		Log.d(TAG, mStartTimeTextView.getText().toString());
		Log.d(TAG, mEndTimeTextView.getText().toString());
		if (
				mNameEditText.getText().toString().length() == 0
						|| mLocationEditText.getText().toString().length() == 0
						|| mFriendsTextView.getText().toString().length() == 0
						|| mStartTimeTextView.getText().toString().length() == 0
						|| mEndTimeTextView.getText().toString().length() == 0
				) {
			return true;
		}
		return false;
	}

	//Post to the web server
	//@param url the web server's url
	//@param json the content sent to the web server as a json format String
	//@return the response as a json format String


	//Parse the response
	//@param response the response sent back from web server
	//return the trip id generated by the server
	private String parseResponse (String response) {
		try {
			JSONObject jsonResponse = new JSONObject(response);
			int responseCode = jsonResponse.getInt("response_code");
			String tripId = jsonResponse.getString("trip_id");
			if (responseCode != 0) {
				Log.d(TAG, "Response code is " + responseCode + " INSTEAD OF 0!");
				return null;
			}
			else {
				Log.d(TAG, tripId);
				return tripId;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(this, "JSON parsing failed.", Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	/**
	 * This method should be used when a
	 * user wants to cancel the creation of
	 * a Trip.
	 * <p>
	 * Note: You most likely want to call this
	 * if your activity dies during the process
	 * of a trip creation or if a cancel/back
	 * button event occurs. Should return to
	 * the previous activity without a result
	 * using finish() and setResult().
	 */
	public void cancelTrip(View v) {
		finish();
		Log.v("CancelTrip", "cancel");
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
				Log.d(TAG, "---Save Data---");
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
			}
		}.execute();
		super.onStop();
	}
}
