package com.adarshhasija.ahelp;

import java.util.Calendar;
import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationsListActivity extends ListActivity {
	
	private ActionMode mActionMode;
	
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
				LocationListAdapter locationAdapter = new LocationListAdapter(LocationsListActivity.this, 0, list);
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
		        ParseObject.unpinAllInBackground("Location", new DeleteCallback() {

					@Override
					public void done(ParseException e) {
						if(e == null) {
							ParseObject.pinAllInBackground("Location", locations);
						}
						else {
							Log.d("SelectLocationActivity", "Error: " + e.getMessage());
						}
						
					}
		        	
		        });
		        
		        LocationListAdapter locationAdapter = new LocationListAdapter(LocationsListActivity.this, 0, locations);
		        setListAdapter(locationAdapter);
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	public void iconClicked(View v) {
		if (mActionMode != null) {
            
        }

        // Start the CAB using the ActionMode.Callback defined above
        mActionMode = startActionMode(mActionModeCallback);
        View view = (View) v.getParent();
        view.setSelected(true);
	};
	
	
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
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.location_context_menu, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.discard:
	                
	                return true;
	            case R.id.edit:
	            	
	            	return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
	
	
	private void addPressed() {
		//showNewLocationDialog();
		Intent intent = new Intent(this, LocationEditActivity.class);
		startActivityForResult(intent, 50000);
	};
	
	private void refreshPressed() {
		refreshing=true;
		toggleProgressBarVisibility();
		ParseQuery<ParseObject> queryLocation = ParseQuery.getQuery("Location");
		queryLocation.findInBackground(populateListCallbackCloud);
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
			addButton.setVisible(false);
			refreshButton.setVisible(false);
		}
		else {
			progressButton.setVisible(false);
			searchButton.setVisible(true);
			addButton.setVisible(true);
			refreshButton.setVisible(true);
		}
	}
	
	private void populateListLocal() {
		ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Location");
		localQuery.fromLocalDatastore();
		//localQuery.orderByDescending("updatedAt");
		localQuery.orderByAscending("title");
		localQuery.findInBackground(populateListCallbackLocal);
	}
	
	private void populateListCloud() {
		ParseQuery<ParseObject> cloudQuery = ParseQuery.getQuery("Location");
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
		emptyView.setText("There are currently no locations to list.\n Tap + to add a location");
		emptyView.setGravity(Gravity.CENTER);
		getListView().setEmptyView(emptyView);
		
		super.onStart();
	}
	


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseObject parseObject = (ParseObject) getListView().getAdapter().getItem(position);
		Bundle bundle = prepareBundle(parseObject);
		Intent intent = new Intent(LocationsListActivity.this, LocationEditActivity.class);
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
				ParseQuery<ParseObject> locationQuery = ParseQuery.getQuery("Location");
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
		
		getMenuInflater().inflate(R.menu.locations, menu);
		
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
