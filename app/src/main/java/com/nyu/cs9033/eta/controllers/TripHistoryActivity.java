package com.nyu.cs9033.eta.controllers;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.adapters.CardViewAdapter;
import com.nyu.cs9033.eta.helpers.CommonDBHelper;
import com.nyu.cs9033.eta.helpers.TripDBHelper;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.util.CommonUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TripHistoryActivity extends FragmentActivity
        implements ActionBar.TabListener {
    private static final String TAG = "TripHistoryActivity";
    private static final String ALL_TRIPS = "allTrips";
    private static final String CURRENT_TRIPS = "currentTrips";
    private static final String PAST_TRIPS = "pastTrips";
    private static final String FUTURE_TRIPS = "futureTrips";
    private static final String CURRENT_FRAGEMENT = "currentFragment";

    private List<Trip> allTrips;
    private ArrayList<Trip> currentTrips;
    private ArrayList<Trip> pastTrips;
    private ArrayList<Trip> futureTrips;

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        final Context context = this;
        if (!DataHolder.getInstance().isInitiated()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    CommonUtil.getTripsFromDB(context);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    allTrips = DataHolder.getInstance().getAllTrips();
                }
            }.execute();
        }
        else {
            allTrips = DataHolder.getInstance().getAllTrips();
        }

        for (Trip trip : allTrips) {
            Log.d("(((((", trip.get_id() + "");
        }

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(allTrips, getSupportFragmentManager());
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "ONRESUME!!!!!!!!!!!!!!!!!!!!!!!!");

        allTrips = (ArrayList<Trip>) DataHolder.getInstance().getAllTrips();
        mAppSectionsPagerAdapter.initiateTrips(allTrips);
//        mAppSectionsPagerAdapter.initiateTrips();
        for (Trip trip : allTrips) {
            Log.d("(((((", trip.get_id() + " " + trip.getStatus());
            Log.d(")))))", trip.get_id() + " " + trip.isArrived());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Trip> allTrips;
        private ArrayList<Trip> currentTrips;
        private ArrayList<Trip> pastTrips;
        private ArrayList<Trip> futureTrips;

        public AppSectionsPagerAdapter(List<Trip> allTrips, FragmentManager fm) {
            super(fm);
            currentTrips = new ArrayList<>();
            pastTrips = new ArrayList<>();
            futureTrips = new ArrayList<>();
            initiateTrips(allTrips);
        }

        public void initiateTrips(List<Trip> allTrips) {
            this.allTrips = allTrips;
            currentTrips.clear();
            pastTrips.clear();
            futureTrips.clear();
            for (Trip trip : allTrips) {
                if (CommonUtil.isPastTrip(trip)) {
                    trip.setStatus(CreateTripActivity.STATUS_PAST_TRIP);
                    trip.setIsArrived(true);
                }
                switch (trip.getStatus()) {
                    case CreateTripActivity.STATUS_FUTURE_TRIP:
                        futureTrips.add(trip);
                        break;
                    case CreateTripActivity.STATUS_CURRENT_TRIP:
                        currentTrips.add(trip);
                        break;
                    case CreateTripActivity.STATUS_PAST_TRIP:
                        pastTrips.add(trip);
                        break;
                }
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public android.support.v4.app.Fragment getItem (int i){

            Bundle tripsBundle = new Bundle();
            tripsBundle.putInt(CURRENT_FRAGEMENT, i);
            tripsBundle.putParcelableArrayList(CURRENT_TRIPS, currentTrips);
            tripsBundle.putParcelableArrayList(PAST_TRIPS, pastTrips);
            tripsBundle.putParcelableArrayList(FUTURE_TRIPS, futureTrips);
            Log.d("getItem", i + "#####");

            switch (i) {
                case 0:
                    android.support.v4.app.Fragment currentTripsFragment = new TripsFragment();
                    currentTripsFragment.setArguments(tripsBundle);
                    return currentTripsFragment;

                case 1:
                    android.support.v4.app.Fragment pastTripsFragment = new TripsFragment();
                    pastTripsFragment.setArguments(tripsBundle);
                    return pastTripsFragment;

                default:
                    android.support.v4.app.Fragment futureTripsFragment = new TripsFragment();
                    futureTripsFragment.setArguments(tripsBundle);
                    return futureTripsFragment;
            }
        }

        @Override
        public int getCount () {
                return 3;
            }

        @Override
        public CharSequence getPageTitle (int position){
            switch (position) {
                case 0:
                    return "Current Trips";
                case 1:
                    return "Past Trips";
                case 2:
                    return "Future Trips";
                default:
                    return "Fault";
            }
        }
    }

    public static class TripsFragment extends android.support.v4.app.Fragment {
        protected RecyclerView mRecyclerView;
        protected TextView countTripsView;
        protected Context context;
        List<Trip> allTrips;
        List<Trip> trips;

        private CardViewAdapter mAdapter;

        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_current_trips, container, false);
            context = rootView.getContext();

            int currentFragment = getArguments().getInt(CURRENT_FRAGEMENT);
            allTrips = DataHolder.getInstance().getAllTrips();
            switch (currentFragment) {
                case 0:
                    trips = getArguments().getParcelableArrayList(CURRENT_TRIPS);
                    break;
                case 1:
                    trips = getArguments().getParcelableArrayList(PAST_TRIPS);
                    break;
                case 2:
                    trips = getArguments().getParcelableArrayList(FUTURE_TRIPS);
                    break;
            }
            final TextView noTrip = (TextView) rootView.findViewById(R.id.no_trip);

            if (trips.size() != 0) {
                noTrip.setText("");
            }

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            countTripsView = (TextView) rootView.findViewById(R.id.fragment_trip_count);

            RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new CardViewAdapter(trips);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClick(new CardViewAdapter.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    handleItemClick(position);
                }
            });
            mAdapter.setOnActivateButtonClick(new CardViewAdapter.OnActivateButtonClick() {
                @Override
                public void onActivateButtonClick(int position, View view) {
                    handleActivateButtonClick(trips.get(position), view);
                }
            });

            countTripsView.setText(getString(R.string.count) + mAdapter.getItemCount());
            return rootView;
        }

        private void handleItemClick (int position) {
//            Toast.makeText(context, "Clicked " + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, ViewTripActivity.class);
            intent.putExtra("trip", trips.get(position));
            Log.d("***********", trips.get(position).get_id() + " - ");
            startActivity(intent);
        }

        private void handleActivateButtonClick (Trip trip, View view) {
            Button button = (Button) view;
            switch (trip.getStatus()) {
                case CreateTripActivity.STATUS_FUTURE_TRIP:
                    for (Trip eachTrip : allTrips) {
                        if (eachTrip.getStatus() == CreateTripActivity.STATUS_CURRENT_TRIP) {
                            Toast.makeText(button.getContext(),
                                    "You can only activate one current trip!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    CommonUtil.updateToCurrentTrip(trip);
//                    trip.setStatus(CreateTripActivity.STATUS_CURRENT_TRIP);

                    Log.d("<<<<<<<<<<<<<<<<<<<<<<", trip.getStatus() + "");
                    Log.d("______________________", trips.size() + "");
                    mAdapter.updateList(trip);
                    mAdapter.notifyDataSetChanged();
                    countTripsView.setText(getString(R.string.count) + mAdapter.getItemCount());
                    break;
                case CreateTripActivity.STATUS_CURRENT_TRIP:
//                    DataHolder.getInstance().changeTripStatus(trip, trip.getStatus());
                    trip.setStatus(CreateTripActivity.STATUS_PAST_TRIP);
                    trip.setIsArrived(true);

                    mAdapter.updateList(trip);
                    mAdapter.notifyDataSetChanged();
                    countTripsView.setText(getString(R.string.count) + mAdapter.getItemCount());
                    break;
                case CreateTripActivity.STATUS_PAST_TRIP:
                    DataHolder.getInstance().getAllTrips().remove(trip);
                    allTrips.remove(trip);
                    mAdapter.updateList(trip);
                    mAdapter.notifyDataSetChanged();
                    countTripsView.setText(getString(R.string.count) + mAdapter.getItemCount());
                    break;
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
                Log.d(TAG, "---Save Data---");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
        super.onStop();
//        finish();
    }
}



