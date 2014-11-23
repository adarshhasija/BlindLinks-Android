package com.adarshhasija.ahelp;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser != null) {
			Intent intent = new Intent(this, RecordListActivity.class);
			startActivity(intent);
			finish(); 	 
		}
		else {
			Intent loginIntent = new Intent(this, Login.class);
			startActivity(loginIntent);
			finish();
		}	
	}

	
}
