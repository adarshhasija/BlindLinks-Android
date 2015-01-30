package com.adarshhasija.ahelp;

import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class SubjectsListActivity extends ListActivity {
	
	
	//MenuItems
		private MenuItem progressButton;
		private MenuItem searchButton;
		private MenuItem addButton;
		private MenuItem refreshButton;
		private boolean refreshing=false;
		
		/*
		 * Parse callbacks
		 * 
		 */
		private FindCallback<ParseObject> populateListCallbackLocal = new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					LocationListAdapter locationAdapter = new LocationListAdapter(SubjectsListActivity.this, 0, list);
		            setListAdapter(locationAdapter);
		            
		        } else {
		            Log.d("LocationsListActivity", "Error: " + e.getMessage());
		        }
			}
			
		};
		
		private FindCallback<ParseObject> populateListCallbackCloud = new FindCallback<ParseObject>() {

			@Override
			public void done(final List<ParseObject> locations, ParseException e) {
				
				if(e == null) {
			        ParseObject.unpinAllInBackground("Subject", new DeleteCallback() {

						@Override
						public void done(ParseException e) {
							if(e == null) {
								ParseObject.pinAllInBackground("Subject", locations);
							}
							else {
								Log.d("SelectLocationActivity", "Error: " + e.getMessage());
							}
							
						}
			        	
			        });
			        
			        LocationListAdapter locationAdapter = new LocationListAdapter(SubjectsListActivity.this, 0, locations);
			        setListAdapter(locationAdapter);
				}
				else {
					Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
			
		};
	
	
	/*
	 * private functions
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
	
	private void populateListLocal() {
		ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Subject");
		localQuery.fromLocalDatastore();
		//localQuery.orderByDescending("updatedAt");
		localQuery.orderByAscending("title");
		localQuery.findInBackground(populateListCallbackLocal);
	}
	
	private void populateListCloud() {
		ParseQuery<ParseObject> cloudQuery = ParseQuery.getQuery("Subject");
		//cloudQuery.orderByDescending("updatedAt");
		cloudQuery.orderByAscending("title");
		cloudQuery.findInBackground(populateListCallbackCloud);
	}
	
	private void populateList() {
		populateListLocal();
		ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) {
			populateListCloud();
		}
	}
	
	
	/*
	 * Action bar buttons
	 * Private functions
	 * 
	 */
	private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

		@Override
		public boolean onQueryTextChange(String newText) {
			RecordAdapter adapter = (RecordAdapter) getListAdapter();
			if(adapter != null) {
				adapter.getFilter().filter(newText);
			}
			return false;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			// TODO Auto-generated method stub
			return false;
		}

		
	};
	
	private void addPressed() {
		Intent intent = new Intent(this, SubjectEditActivity.class);
		startActivityForResult(intent, 50000);
	};
	
	private void refreshPressed() {
		refreshing=true;
		toggleProgressBarVisibility();
		ParseQuery<ParseObject> queryLocation = ParseQuery.getQuery("Subject");
		queryLocation.findInBackground(populateListCallbackCloud);
	};
	
	private Bundle prepareBundle(ParseObject parseObject) {
		Bundle bundle = new Bundle();
		bundle.putString("parseId", parseObject.getObjectId());
		bundle.putString("uuid", parseObject.getString("uuid"));
		bundle.putString("title", parseObject.getString("title"));
		
		return bundle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		populateList();
	}
	
	@Override
	public void onStart() {
		TextView emptyView = new TextView(this);
		((ViewGroup) getListView().getParent()).addView(emptyView);
		emptyView.setText("There are currently no subjects to list.\n Tap + to add a subjects");
		emptyView.setGravity(Gravity.CENTER);
		getListView().setEmptyView(emptyView);
		
		super.onStart();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseObject parseObject = (ParseObject) getListView().getAdapter().getItem(position);
		Bundle bundle = prepareBundle(parseObject);
		Intent intent = new Intent(this, SubjectEditActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, position);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		final LocationListAdapter adapter = (LocationListAdapter) getListAdapter();
		
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode != 50000 && data != null) {  //modify
				Bundle extras = data.getExtras();
				ParseObject parseObject = adapter.getItem(requestCode);
				adapter.remove(adapter.getItem(requestCode));
				parseObject.put("title", extras.getString("title"));
				adapter.insert(parseObject, requestCode);
				adapter.notifyDataSetChanged();
			}
			else if(requestCode != 50000 && data == null) {	//delete
				ParseObject parseObject = adapter.getItem(requestCode);
				adapter.remove(parseObject);
				adapter.notifyDataSetChanged();
			}
			else if(data != null) { //insert new
				Bundle extras = data.getExtras();
				String uuid = extras.getString("uuid");
				ParseQuery<ParseObject> locationQuery = ParseQuery.getQuery("Subject");
				locationQuery.whereEqualTo("uuid", uuid);
				locationQuery.fromLocalDatastore();
				locationQuery.findInBackground(new FindCallback<ParseObject> () {

					@Override
					public void done(List<ParseObject> list, ParseException e) {
						if(list.size() > 0) {
							ParseObject obj = list.get(0);
							adapter.insert(obj, 0);
							adapter.notifyDataSetChanged();
						}
					}
					
				});
			}
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.search:
	            return true;
	        case R.id.add:
	            addPressed();
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
		
		getMenuInflater().inflate(R.menu.subjects, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setOnQueryTextListener(onQueryTextListener);
	    searchButton = menu.findItem(R.id.search);
		
		addButton = (MenuItem)menu.findItem(R.id.add);
		refreshButton = (MenuItem)menu.findItem(R.id.refresh);
		refreshButton.setVisible(false);
		
		return super.onCreateOptionsMenu(menu);
	}

	
}
