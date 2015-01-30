package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class MainListActivity extends ListActivity {
	
	private ArrayList<ParseObject> recordsList = new ArrayList<ParseObject>();

	private MenuItem progressButton;
	private MenuItem searchButton;
	private MenuItem addButton;
	private MenuItem contactsButton;
	private MenuItem coordinatorButton;
	private MenuItem refreshButton;
	private MenuItem logoutButton;
	private boolean refreshing=false;
	
	
	/*
	 * Parse callbacks
	 * 
	 */
	private FindCallback<ParseObject> populateListCallbackLocal = new FindCallback<ParseObject>() {

		@Override
		public void done(final List<ParseObject> list, ParseException e) {
			if (e == null) {
				if(list.size() == 0) {
					ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
					if(cm.getActiveNetworkInfo() != null) {
						populateListCloud();
					}
					return;
				}
				Collections.sort(list, new Comparator<ParseObject>() {
		  			  public int compare(ParseObject o1, ParseObject o2) {
		  				if(o2.getUpdatedAt() == null)
		  			         if(o1.getUpdatedAt() == null)
		  			            return 0; //equal
		  			         else
		  			            return -1; // null is before other strings
		  				else // this.member != null
		  			         if(o1.getUpdatedAt() == null)
		  			            return 1;  // all other strings are after null
		  			         else
		  			        	 return o2.getUpdatedAt().compareTo(o1.getUpdatedAt()); //descending
		  			  }
		  			});
				RecordAdapter recordAdapter = new RecordAdapter(MainListActivity.this, 0, list);
	            setListAdapter(recordAdapter);
	        } else {
	            Log.d("MainListActivity", "Error: " + e.getMessage());
	        }
		}
		
	};
	
	private FindCallback<ParseObject> populateListCallbackCloud = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {		
			if (e == null) {
	            Collections.sort(list, new Comparator<ParseObject>() {
	  			  public int compare(ParseObject o1, ParseObject o2) {
	  				  return o2.getUpdatedAt().compareTo(o1.getUpdatedAt()); //descending
	  			  }
	  			});
	            for(ParseObject obj : list) {
	            	obj.getParseObject("location").fetchIfNeededInBackground();
	            	obj.getParseObject("subject").fetchIfNeededInBackground();
	            	try {
						List<ParseObject> actionList = obj.getList("actions");
		            	for(int i = 0; i < actionList.size(); i++) {
		            		((ParseObject) obj.getList("actions").get(i)).fetchIfNeeded();
		            	}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

	            }
	            ParseObject.pinAllInBackground("ScribeRequest", list);
	            
	            RecordAdapter recordAdapter = new RecordAdapter(MainListActivity.this, 0, list);
	            setListAdapter(recordAdapter);
	         /*   if(refreshing) {
	            	toggleProgressBarVisibility();
	            	refreshing = false;
	            }	*/		
	        } else {
	            Log.d("MainListActivity", "Error: " + e.getMessage());
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
	
	
	/*
	 * Action bar item functions
	 * 
	 * 
	 * 
	 */
	private void addPressed() {
		Intent intent = new Intent(this, RecordEditActivity.class);
		int index = 50000; //random very large integer to show insert
		startActivityForResult(intent, index);
	};
	
	private void contactsPressed() {
		Intent intent = new Intent(this, ContactsListActivity.class);
		startActivity(intent);
	};
	
	private void coordinatorPressed() {
		Intent intent = new Intent(this, CoordinatorListActivity.class);
		startActivity(intent);
	};
	
	private void refreshPressed() {
		refreshing=true;
		toggleProgressBarVisibility();
		populateList();
	};
	
	private void logoutPressed() {
		ParseUser.logOut();
		Intent loginIntent = new Intent(this, Login.class);
		startActivity(loginIntent);
		finish();
	};
	
	
	
	
	/*
	 * Private functions
	 * 
	 * 
	 */	
	private void populateList() {
		populateListLocal();
		ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) {
			//populateListCloud();
		}
	}
	
	private void populateListLocal() {
		ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("ScribeRequest");
		localQuery.fromLocalDatastore();
		localQuery.findInBackground(populateListCallbackLocal);
	}
	
	private void populateListCloud() {
		ParseQuery<ParseObject> cloudQuery = ParseQuery.getQuery("ScribeRequest");
		cloudQuery.findInBackground(populateListCallbackCloud);
	}
	
	private void toggleProgressBarVisibility() {
		if(!refreshing) return;
		
		if(!progressButton.isVisible()) {
			progressButton.setVisible(true);
			searchButton.setVisible(false);
			addButton.setVisible(false);
			refreshButton.setVisible(false);
			logoutButton.setVisible(false);
		}
		else {
			progressButton.setVisible(false);
			searchButton.setVisible(true);
			addButton.setVisible(true);
			refreshButton.setVisible(true);
			logoutButton.setVisible(true);
		}
	}
	
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		populateList();
	}

	@Override
	public void onStart() {
		TextView emptyView = new TextView(this);
		((ViewGroup) getListView().getParent()).addView(emptyView);
		emptyView.setText("There are currently no scribe requests to list.\n Tap + to add a record");
		emptyView.setGravity(Gravity.CENTER);
		getListView().setEmptyView(emptyView);
		
		super.onStart();
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
		//ParseObject record = mainApplication.getModifiedRecord();
		if(resultCode == Activity.RESULT_OK) {
			if(data == null) {
				if(requestCode > -1 && requestCode != 50000) { //delete
					RecordAdapter adapter = (RecordAdapter) getListAdapter();
					if(adapter != null) {
						ParseObject parseObject = adapter.getItem(requestCode);
						adapter.remove(parseObject);
						adapter.notifyDataSetChanged();
						return;
					}
					return;
				}
				else {	return;	}
			}
			ParseObject record=null;
			Bundle extras = data.getExtras();
			String recordParseId = extras.getString("parseId");
			String recordUuid = extras.getString("uuid");
			ParseQuery<ParseObject> recordQuery = ParseQuery.getQuery("ScribeRequest");
			recordQuery.fromLocalDatastore();
			if(recordUuid != null) {
				recordQuery.whereEqualTo("uuid", recordUuid);
				try {
					record = recordQuery.getFirst();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					record = recordQuery.get(recordParseId);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			if(record != null) {
				RecordAdapter adapter = (RecordAdapter) getListAdapter();
				//random large number for insert
				if(requestCode == 50000) {
					adapter.insert(record, 0);
				}
				else {
					adapter.remove(record);
					adapter.insert(record, 0);
				}
				adapter.notifyDataSetChanged();
				getListView().scrollTo(0, 0);  //scroll to top after done	
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		//Pass the ParseObject as a global variable
		ParseObject record = (ParseObject) getListAdapter().getItem(position);
		Intent intent = new Intent(this, MainDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("parseId", record.getObjectId());
		bundle.putString("uuid", record.getString("uuid"));
		intent.putExtras(bundle);
		startActivityForResult(intent, position);
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
	        case R.id.contacts:
	        	contactsPressed();
	        	return true;
	        case R.id.coordinator:
	        	coordinatorPressed();
	        	return true;
	        case R.id.refresh:
	        	refreshPressed();
	        	return true;
	        case R.id.logout:
	        	logoutPressed();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.records_list_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setOnQueryTextListener(onQueryTextListener);
	    searchButton = menu.findItem(R.id.search);
	    
	    //uncomment this to set search to go to a new results page
	    //searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
	    
		
		addButton = (MenuItem)menu.findItem(R.id.add);
		contactsButton = (MenuItem)menu.findItem(R.id.contacts);
		coordinatorButton = (MenuItem)menu.findItem(R.id.coordinator);
		refreshButton = (MenuItem)menu.findItem(R.id.refresh);
		logoutButton = (MenuItem)menu.findItem(R.id.logout);
		
		return super.onCreateOptionsMenu(menu);
	}
}
