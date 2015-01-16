package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsListActivity extends ListActivity {
	
	private ArrayList<ParseUser> userObjects=null;
	private ArrayList<String> userList=null;
	
	//MenuItems
	private MenuItem progressButton;
	private MenuItem searchButton;
	private MenuItem refreshButton;
	private boolean refreshing=false;
	
	/*
	 * Parse callbacks
	 * 
	 * 
	 */
	private FindCallback<ParseUser> findCallbackLocal = new FindCallback<ParseUser>() {

		@Override
		public void done(List<ParseUser> list, ParseException e) {
			if (e == null) {
		        populateList(list);
		    } else {
		    	Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		    }
		}
	};
	private FindCallback<ParseUser> findCallbackCloud = new FindCallback<ParseUser>() {

		@Override
		public void done(final List<ParseUser> list, ParseException e) {
			if (e == null) {
				ParseUser.unpinAllInBackground(new DeleteCallback() {

					@Override
					public void done(ParseException e) {
						if(e == null) {
							ParseUser.pinAllInBackground(list);
							populateList(list);
						}
					}
		        	
		        });
		    } else {
		    	Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		    }
		}
		
	};
	
	/*
	 * Action bar buttons
	 * Private functions
	 * 
	 */
	private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

		@Override
		public boolean onQueryTextChange(String newText) {
			((Filterable) getListAdapter()).getFilter().filter(newText);
			return false;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			// TODO Auto-generated method stub
			return false;
		}

		
	};
	
	private void refreshPressed() {
		refreshing=true;
		toggleProgressBarVisibility();
		ParseQuery<ParseUser> queryUsers = ParseUser.getQuery();
		queryUsers.findInBackground(findCallbackCloud);
	};
	
	
	/*
	 * Private functions
	 * 
	 * 
	 */
	private void toggleProgressBarVisibility() {
		if(!refreshing) return;
		
		if(!progressButton.isVisible()) {
			progressButton.setVisible(true);
			searchButton.setVisible(false);
			refreshButton.setVisible(false);
		}
		else {
			progressButton.setVisible(false);
			searchButton.setVisible(true);
			refreshButton.setVisible(true);
		}
	}
	
	
	private void populateList(List<ParseUser> list) {
		if(userObjects != null) userObjects.clear();
		else userObjects = new ArrayList<ParseUser>();
		if(userList != null) userList.clear();
		else userList = new ArrayList<String>();
		
		MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
        HashMap<String, String> localContacts = mainApplication.getUpdatedDeviceContactsList();
        String phoneNumber;
        String phoneNumberWithZero;
        String phoneNumberNoPrefix;
        for(ParseUser user : list) {
        	//This covers all the combination for an India number
        	phoneNumber = user.getString("phoneNumber");
        	phoneNumberWithZero = "0" + phoneNumber.substring(3);
        	phoneNumberNoPrefix = phoneNumber.substring(3);
        	
        	if(localContacts.containsKey(phoneNumber) || 
        			localContacts.containsKey(phoneNumberWithZero) ||
        				localContacts.containsKey(phoneNumberNoPrefix)) {
        		userObjects.add(user); //This is needed later when saving
        		userList.add(user.getString("firstName")+" "+user.getString("lastName"));
        	}

        }
        LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, userList);
        setListAdapter(adapter); 
        
        if(refreshing) {
        	toggleProgressBarVisibility();
        	refreshing = false;
        }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ParseQuery<ParseUser> queryUsersLocal = ParseUser.getQuery();
		queryUsersLocal.fromLocalDatastore();
		queryUsersLocal.findInBackground(findCallbackLocal);
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) {
			ParseQuery<ParseUser> queryUsersCloud = ParseUser.getQuery();
			queryUsersCloud.findInBackground(findCallbackCloud);
		}
	}
	
	@Override
	public void onStart() {
		TextView emptyView = new TextView(this);
		((ViewGroup) getListView().getParent()).addView(emptyView);
		emptyView.setText("No one from your contacts list is registered with the app.\n If your contact is registed, make sure you enter them in your contacts list,\n then refresh this page");
		emptyView.setGravity(Gravity.CENTER);
		getListView().setEmptyView(emptyView);
		
		super.onStart();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.search:
	            return true;
	        case R.id.refresh:
	        	ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() == null) {
					Toast.makeText(this, "No internet connection, cannot refresh", Toast.LENGTH_SHORT).show();
					return false;
				}
	        	refreshPressed();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.contacts, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setOnQueryTextListener(onQueryTextListener);
	    searchButton = menu.findItem(R.id.search);
	    
	    //sendButton = menu.findItem(R.id.send);
	    
	    //uncomment this to set search to go to a new results page
	    //searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		
		refreshButton = (MenuItem)menu.findItem(R.id.refresh);
		
		return super.onCreateOptionsMenu(menu);
	}

	
}
