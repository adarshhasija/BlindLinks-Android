package com.adarshhasija.blindlinks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.adarshhasija.blindlinks.dummy.DummyContent;
import com.adarshhasija.blindlinks.R;

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
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
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
	private FindCallback<ParseObject> populateListCallback = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {
			if (e == null) {
	          /*  for(ParseObject obj : list) {
	            	recordsList.add(obj);
	            } */
	            
	            Collections.sort(list, new Comparator<ParseObject>() {
	  			  public int compare(ParseObject o1, ParseObject o2) {
	  				  return o2.getUpdatedAt().compareTo(o1.getUpdatedAt()); //descending
	  			  }
	  			});
	            RecordAdapter recordAdapter = new RecordAdapter(getActivity(), 0, list);
	            setListAdapter(recordAdapter);
	            if(refreshing) {
	            	toggleProgressBarVisibility();
	            	refreshing = true;
	            }
	        } else {
	            Log.d("score", "Error: " + e.getMessage());
	        }
		}
		
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		populateList();
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
	public void onResume() {
		super.onResume();
		RecordAdapter adapter = (RecordAdapter) getListAdapter();
		if(adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == getActivity().RESULT_OK) {
			MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
			ParseObject record = mainApplication.getModifiedRecord();
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
				mainApplication.setModifiedRecord(null);
			}
		}
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
		mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater.inflate(R.menu.records_list_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getActivity().getComponentName()));
	    searchButton = menu.findItem(R.id.search);
	    searchButton.setVisible(false);
		
		addButton = (MenuItem)menu.findItem(R.id.add);
		addButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getActivity(), RecordEditActivity.class);
				Calendar c = Calendar.getInstance();
				int index = 50000; //random very large integer to show insert
				startActivityForResult(intent, index);

				return false;
			}
		});
		
		refreshButton = (MenuItem)menu.findItem(R.id.refresh);
		refreshButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				refreshing=true;
				toggleProgressBarVisibility();
				populateList();
				return false;
			}
		});
		
		logoutButton = (MenuItem)menu.findItem(R.id.logout);
		logoutButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ParseUser.logOut();
				Intent loginIntent = new Intent(getActivity(), Login.class);
				startActivity(loginIntent);
				getActivity().finish();
				return false;
			}
		});
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private void populateList() {
		ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("Record");
		queryUser.whereEqualTo("user", ParseUser.getCurrentUser());
		queryUser.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		
		ParseQuery<ParseObject> queryRecipient = ParseQuery.getQuery("Record");
		queryRecipient.whereEqualTo("recipient", ParseUser.getCurrentUser());
		queryRecipient.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(queryUser);
        queries.add(queryRecipient);
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
		mainQuery.findInBackground(populateListCallback);
	}
	
	private void toggleProgressBarVisibility() {
		if(!refreshing) return;
		
		if(!progressButton.isVisible()) {
			progressButton.setVisible(true);
			//searchButton.setVisible(false);
			addButton.setVisible(false);
			refreshButton.setVisible(false);
			logoutButton.setVisible(false);
		}
		else {
			progressButton.setVisible(false);
			//searchButton.setVisible(true);
			addButton.setVisible(true);
			refreshButton.setVisible(true);
			logoutButton.setVisible(true);
		}
	}
	
	

}
