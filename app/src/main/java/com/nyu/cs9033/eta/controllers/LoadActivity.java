package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.util.CommonUtil;

public class LoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_load);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.back_ground);
        linearLayout.setBackground(new BitmapDrawable(CommonUtil.readBitMap(this, R.drawable.main_background)));

        final Context context = this;
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				CommonUtil.getTripsFromDB(context);
				return null;
			}

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.execute();
    }
}
