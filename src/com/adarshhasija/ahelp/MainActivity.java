package com.adarshhasija.ahelp;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
