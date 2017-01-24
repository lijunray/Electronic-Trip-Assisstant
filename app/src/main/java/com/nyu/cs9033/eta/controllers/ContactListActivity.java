package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.adapters.ListViewAdapter;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends Activity {

    private static final String TAG = "ContactListActivity";

    private ListView contactsList;
    private ListViewAdapter mAdapter;
    private List<Person> friends;
    private ArrayList<Person> selectedFriends;
    private boolean[] selectedPosition;
    private int selectedItemCount;
    private TextView tv_show;

    private Button finishButton;
    private Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        contactsList = (ListView) findViewById(R.id.list_view);

        friends = new ArrayList<>();
        selectedFriends = new ArrayList<>();

        getContacts();
        mAdapter = new ListViewAdapter(friends, this);
        contactsList.setAdapter(mAdapter);

        selectedPosition = new boolean[friends.size()];

        finishButton = (Button) findViewById(R.id.contacts_submit_button);
        cancelButton = (Button) findViewById(R.id.contacts_cancel_button);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitContacts(v);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelContacts(v);
            }
        });

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long id) {
                Log.d("Main", "Clicked " + position);
                ListViewAdapter.ViewHolder holder = (ListViewAdapter.ViewHolder) view.getTag();
                holder.cb.toggle();
                ListViewAdapter.getIsSelected().put(position, holder.cb.isChecked());
                if (holder.cb.isChecked()) {
                    selectedPosition[position] = true;
                    selectedItemCount++;
                } else {
                    selectedPosition[position] = false;
                    selectedItemCount--;
                }
                dataChanged();
            }
        });
    }

    public void submitContacts (View view) {

        for (int i = 0; i < selectedPosition.length; i++) {
            if (selectedPosition[i]) {
                selectedFriends.add((Person) contactsList.getItemAtPosition(i));
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("savedFriends", selectedFriends);
        Log.v("Create", String.valueOf(selectedFriends.size()));
        try {
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelContacts (View view) {
        Intent resultIntent = new Intent(this, CreateTripActivity.class);
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
        Log.v("CancelTrip", "cancel");
    }


    private void dataChanged() {
        mAdapter.notifyDataSetChanged();
        finishButton.setText("finish (" + selectedItemCount + ")");
    };

    private void getContacts () {
        friends = DataHolder.getInstance().getContactFriends();
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

}
