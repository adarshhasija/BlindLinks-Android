package com.adarshhasija.blindlinks;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.adarshhasija.blindlinks.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchResultsListActivity extends ListActivity {
	
	private MenuItem progressButton;
	
	/*
	 * Parse callbacks
	 * 
	 */
	
	private FindCallback<ParseObject> findCallback = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {
			if (e == null) {
				if(list.size() > 0) {
					populateResults(list);
				}
				else {
					setEmptyView("No search results");
				}
	        } else {
	            Log.d("score", "Error: " + e.getMessage());
	            setEmptyView(e.getMessage());
	        }
		}
		
	};
	
	
	/*
	 * private functions
	 * 
	 */
	private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            query = query.substring(0, 1).toUpperCase() + query.substring(1);
            
            //queryFromParse(query);
    		
    		
        	
        }
    }
	
	private void queryFromParse(String query)
	{
		ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Record");
		parseQuery.whereEqualTo("user", ParseUser.getCurrentUser());
		parseQuery.whereContains("category", query);
		parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		parseQuery.findInBackground(findCallback);
	}
	
	private void queryFromLocal(String query) 
	{
		MainApplication mainApplication = (MainApplication) getApplicationContext();
		HashMap<String, String> map = mainApplication.getUpdatedDeviceContactsList();
		
		
	}
	
	private void populateResults(List<ParseObject> list) 
	{
		Collections.sort(list, new Comparator<ParseObject>() {
			public int compare(ParseObject o1, ParseObject o2) {
				return o2.getUpdatedAt().compareTo(o1.getUpdatedAt()); //descending
			}
		});
		RecordAdapter recordAdapter = new RecordAdapter(getBaseContext(), 0, list);
		setListAdapter(recordAdapter);
	}
	
	private void setEmptyView(String message) 
	{
		TextView emptyView = new TextView(getBaseContext());
		((ViewGroup) getListView().getParent()).addView(emptyView);
		emptyView.setText(message);
		emptyView.setGravity(Gravity.CENTER);
		getListView().setEmptyView(emptyView);
	}

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		handleIntent(getIntent());
		super.onCreate(savedInstanceState);
	}


	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.record_search, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(true);
		
		return super.onCreateOptionsMenu(menu);
	}

}
