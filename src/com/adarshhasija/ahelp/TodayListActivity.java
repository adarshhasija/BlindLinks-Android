package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

public class TodayListActivity extends ListActivity {
	
	/*
	 * Parse callbacks
	 * 
	 */
	private FindCallback<ParseObject> populateListCallback = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {
			if (e == null) {
	            
	        } else {
	            Log.d("score", "Error: " + e.getMessage());
	        }
		}
		
	};
	
	/*
	 * Private functions
	 * 
	 * 
	 */
	private void populateList() {
		//List<ParseQuery<ParseObject>> queries = getQueryForLocalDatastore();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Record");
		query.whereEqualTo("dateTime", Calendar.getInstance().getTime());
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(populateListCallback);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		populateList();
	}

}
