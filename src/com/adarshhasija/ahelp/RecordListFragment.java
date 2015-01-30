package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.adarshhasija.ahelp.dummy.DummyContent;
import com.adarshhasija.ahelp.R;

/**
 * A list fragment representing a list of Records. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link RecordDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RecordListFragment extends ListFragment {
	
	private MenuItem progressButton;
	private MenuItem searchButton;
	private MenuItem addButton;
	private MenuItem contactsButton;
	private MenuItem coordinatorButton;
	private MenuItem refreshButton;
	private MenuItem logoutButton;
	private boolean refreshing=false;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(int requestCode, Intent data);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {

		@Override
		public void onItemSelected(int requestCode, Intent data) {
			// TODO Auto-generated method stub
			
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RecordListFragment() {
	}
	
	//private RecordAdapter recordAdapter=null;
	public static ArrayList<ParseObject> recordsList = new ArrayList<ParseObject>();
	
	
	/*
	 * Parse callbacks
	 * 
	 */
	private FindCallback<ParseObject> populateListCallbackLocal = new FindCallback<ParseObject>() {

		@Override
		public void done(final List<ParseObject> list, ParseException e) {
			if (e == null) {
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
				RecordAdapter recordAdapter = new RecordAdapter(getActivity(), 0, list);
	            setListAdapter(recordAdapter);
			/*	List<Event> eventList = new ArrayList<Event>();
				for(ParseObject examObject : examList) {
					try {
						String parseId = examObject.getObjectId();
						String uuid = examObject.getString("uuid");
						Exam exam = new Exam(parseId, uuid);
						
						ParseObject locationObject = examObject.getParseObject("location");
						locationObject.fetchFromLocalDatastore();
						Location examLocation = new ExamLocation(locationObject.getObjectId(), locationObject.getString("uuid"));
						
						ParseUser user = examObject.getParseUser("createdBy");
						user.fetchFromLocalDatastore();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				//Then get the location
			/*	ParseQuery<ParseObject> locationQuery = ParseQuery.getQuery("ExamLocation");
				locationQuery.fromLocalDatastore();
				locationQuery.findInBackground(new FindCallback<ParseObject> () {

					@Override
					public void done(final List<ParseObject> examLocationList, ParseException e) {
						if (e == null) {
							ParseQuery<ParseObject> actionQuery = ParseQuery.getQuery("Action");
							actionQuery.fromLocalDatastore();
							actionQuery.findInBackground(new FindCallback<ParseObject> () {

								@Override
								public void done(List<ParseObject> actionList,
										ParseException e) {
									if (e == null) {
										setupEvents(examList, examLocationList, actionList);
									}
									else {
										Log.d("RecordListFragment", "Error: " + e.getMessage());
									}
								}
								
							});
						}	
						else {
				            Log.d("RecordListFragment", "Error: " + e.getMessage());
				        }
					}	
					
				});	*/
	        } else {
	            Log.d("RecordListFragment", "Error: " + e.getMessage());
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
	            RecordAdapter recordAdapter = new RecordAdapter(getActivity(), 0, list);
	            setListAdapter(recordAdapter);
	            if(refreshing) {
	            	toggleProgressBarVisibility();
	            	refreshing = false;
	            }		
	        } else {
	            Log.d("RecordListFragment", "Error: " + e.getMessage());
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
		Intent intent = new Intent(getActivity(), RecordEditActivity.class);
		int index = 50000; //random very large integer to show insert
		startActivityForResult(intent, index);
	};
	
	private void contactsPressed() {
		Intent intent = new Intent(getActivity(), ContactsListActivity.class);
		startActivity(intent);
	};
	
	private void coordinatorPressed() {
		Intent intent = new Intent(getActivity(), CoordinatorListActivity.class);
		startActivity(intent);
	};
	
	private void refreshPressed() {
		refreshing=true;
		toggleProgressBarVisibility();
		populateList();
	};
	
	private void logoutPressed() {
		ParseUser.logOut();
		Intent loginIntent = new Intent(getActivity(), Login.class);
		startActivity(loginIntent);
		getActivity().finish();
	};
	
	
	/*
	 * Private functions
	 * 
	 * 
	 */	
	private void populateList() {
		//List<ParseQuery<ParseObject>> queries = getQueryForLocalDatastore();
		//List<ParseQuery<ParseObject>> queries = getQueryForCloud();
		//ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
		//mainQuery.findInBackground(populateListCallback);
		
		populateListLocal();
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);
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
	
	private List<ParseQuery<ParseObject>> getQueryForLocalDatastore() {
		ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("Record");
		queryUser.whereEqualTo("user", ParseUser.getCurrentUser());
		queryUser.fromLocalDatastore();
		
		ParseQuery<ParseObject> queryRecipient = ParseQuery.getQuery("Record");
		queryRecipient.whereEqualTo("recipient", ParseUser.getCurrentUser());
		queryRecipient.fromLocalDatastore();
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(queryUser);
		queries.add(queryRecipient);
		return queries;
	}
	
	private List<ParseQuery<ParseObject>> getQueryForCloud() {
		ParseQuery<ParseObject> queryExam = ParseQuery.getQuery("Exam");
		queryExam.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		
		ParseQuery<ParseObject> queryRecipient = ParseQuery.getQuery("Exam");
		//queryRecipient.whereEqualTo("from", ParseUser.getCurrentUser());
		queryRecipient.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(queryExam);
		//queries.add(queryRecipient);
		return queries;
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
		setHasOptionsMenu(true);
	}

	@Override
	public void onStart() {
		TextView emptyView = new TextView(getActivity());
		((ViewGroup) getListView().getParent()).addView(emptyView);
		emptyView.setText("There are currently no records to list.\n Tap + to add a record");
		emptyView.setGravity(Gravity.CENTER);
		getListView().setEmptyView(emptyView);
		
		super.onStart();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
		//ParseObject record = mainApplication.getModifiedRecord();
		if(resultCode == getActivity().RESULT_OK) {
			if(data == null) {
				if(requestCode > -1 && requestCode != 50000) { //delete
					RecordAdapter adapter = (RecordAdapter) getListAdapter();
					if(adapter != null) {
						ParseObject parseObject = adapter.getItem(requestCode);
						adapter.remove(parseObject);
						adapter.notifyDataSetChanged();
					}
				}
				else {	return;	}
			}
			ParseObject record=null;
			Bundle extras = data.getExtras();
			String recordParseId = extras.getString("parseId");
			String recordUuid = extras.getString("uuid");
			ParseQuery<ParseObject> recordQuery = ParseQuery.getQuery("Event");
			recordQuery.fromLocalDatastore();
			if(recordUuid != null) {
				recordQuery.whereEqualTo("uuid", recordUuid);
				try {
					record = recordQuery.getFirst();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					record = recordQuery.get(recordParseId);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
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
		//mainApplication.setModifiedRecord(null);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		if(getActivity() != null) {
			populateList();
		}
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		//Pass the ParseObject as a global variable
		MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
		mainApplication.setSelectedRecord((ParseObject) getListAdapter().getItem(position));
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		ParseObject record = (ParseObject) getListAdapter().getItem(position);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("parseId", record.getObjectId());
		bundle.putString("uuid", record.getString("uuid"));
		intent.putExtras(bundle);
		mCallbacks.onItemSelected(position, intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater.inflate(R.menu.records_list_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
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
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	

}
