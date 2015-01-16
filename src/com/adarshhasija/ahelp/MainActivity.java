package com.adarshhasija.ahelp;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
		
		//ParseObject.unpinAllInBackground("Action");
		//ParseObject.unpinAllInBackground("Exam");
		//ParseObject.unpinAllInBackground("Event");
		
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
