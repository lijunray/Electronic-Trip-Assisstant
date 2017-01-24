package com.nyu.cs9033.eta.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.nyu.cs9033.eta.controllers.CreateTripActivity;
import com.nyu.cs9033.eta.controllers.DataHolder;
import com.nyu.cs9033.eta.models.Trip;

public class LocationReceiverUtil extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = intent.getParcelableExtra(LocationManager.KEY_STATUS_CHANGED);
        if (location != null) {
            onLocationReceived(context, location);
        }
    }

    private void onLocationReceived(Context context, Location location) {
        if (DataHolder.getInstance().isInitiated()) {
            for (Trip trip : DataHolder.getInstance().getAllTrips()) {
                if (trip.getStatus() == CreateTripActivity.STATUS_CURRENT_TRIP) {
                    CommonUtil.updateLocation(trip, context, location);
                    Log.d("LocationReceiver", "UPDATE");
                }
            }
        }
    }
}
