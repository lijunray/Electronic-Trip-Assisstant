package com.nyu.cs9033.eta.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.controllers.CreateTripActivity;
import com.nyu.cs9033.eta.helpers.CommonDBHelper;
import com.nyu.cs9033.eta.helpers.TripDBHelper;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.util.CommonUtil;

import java.util.List;

/**
 * Created by Ray on 2016/4/2.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
    private final List<Trip> trips;

    public CardViewAdapter (List<Trip> trips) {
        this.trips = trips;
    }

    public interface OnItemClick {
        public void onItemClick(int position);
    }

    public interface OnActivateButtonClick {
        public void onActivateButtonClick(int position, View view);
    }

    private OnItemClick onItemClick;
    private OnActivateButtonClick onActivateButtonClick;

    public void setOnItemClick (OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setOnActivateButtonClick (OnActivateButtonClick onActivateButtonClick) {
        this.onActivateButtonClick = onActivateButtonClick;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        holder.name.setText(trips.get(position).getName());
        holder.location.setText(trips.get(position).getLocation());
        holder.startTime.setText(CommonUtil.getMDY(trips.get(position).getStartTime()));
        holder.endTime.setText(CommonUtil.getMDY(trips.get(position).getEndTime()));
        if (trips.get(position).getStatus() == CreateTripActivity.STATUS_CURRENT_TRIP) {
            holder.activeButton.setText(R.string.finish_trip);
            holder.activeButton.setBackground(holder.activeButton.getResources().getDrawable(R.drawable.finish_trip_button, null));
        }
        else if (trips.get(position).getStatus() == CreateTripActivity.STATUS_PAST_TRIP) {
            holder.activeButton.setText(R.string.delete_trip);
            holder.activeButton.setBackground(holder.activeButton.getResources().getDrawable(R.drawable.cancel_button, null));
//            holder.activeButton.setClickable(false);
        }
        if (onActivateButtonClick != null) {
            holder.activeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onActivateButtonClick.onActivateButtonClick(position, v);

                }
            });
        }
        if (onItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(position);
                }
            });
        }
    }

    public void updateList (Trip trip) {
        trips.remove(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView location;
        private TextView startTime;
        private TextView endTime;
        public Button activeButton;

        public CardViewHolder (View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.card_trip_name);
            location = (TextView) view.findViewById(R.id.card_trip_location);
            startTime = (TextView) view.findViewById(R.id.card_trip_start_time);
            endTime = (TextView) view.findViewById(R.id.card_trip_end_time);
            activeButton = (Button) view.findViewById(R.id.active_trip);
        }
    }


}
