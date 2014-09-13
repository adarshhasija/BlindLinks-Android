package com.adarshhasija.blindlinks;

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

import android.app.Activity;
import android.app.ActionBar.OnNavigationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class Archives extends Activity {
	
	private FindCallback<ParseObject> findCallback = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {
			
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set a calendar object as 1st of the month
		//so we can query for objects before this month for the archives
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Record");
		query.whereLessThan("updatedAt", c.getTime());
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		
		final List<String> list = new ArrayList<String>();
    	list.add("");
    	list.add("item 2");
    	
    	/** Create an array adapter to populate dropdownlist */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
 
        /** Enabling dropdown list navigation for the action bar */
        //getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
 
        /** Defining Navigation listener */
        OnNavigationListener navigationListener = new OnNavigationListener() {
 
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {

                return false;
            }
        };
 
        /** Setting dropdown items and item navigation listener for the actionbar */
        this.getActionBar().setListNavigationCallbacks(adapter, navigationListener);
	}

}
