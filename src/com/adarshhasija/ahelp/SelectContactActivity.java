package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.adarshhasija.ahelp.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectContactActivity extends ListActivity {
	
	private ArrayList<ParseUser> userObjects=null;
	private ArrayList<String> userList=null;
	
	private ParseUser selectedUser;
	private ParseObject event;
	private ParseObject exam;
	private ParseObject examLocation;
	private ParseObject action;
	private ParseObject location;
	
	//MenuItems
	private MenuItem progressButton;
	private MenuItem searchButton;
	private MenuItem sendButton;
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
	
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			toggleProgressBarVisibility();
			if(e == null) {
				Intent returnIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("uuid", event.getString("uuid"));
				returnIntent.putExtras(bundle);
				setResult(Activity.RESULT_OK, returnIntent);
		    	   finish();
		/*		JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.ahelp.intent.RECEIVE");
					jsonObj.put("objectId", event.getObjectId());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("recipientPhoneNumber", selectedUser.getString("phoneNumber"));
				params.put("data", jsonObj);
				ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
				   public void done(String success, ParseException e) {
					   toggleProgressBarVisibility();
				       if (e == null) {
				    	   setResult(Activity.RESULT_OK);
				    	   finish();
				       }
				       else {
				    	   Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				       }
				   }
				});		*/
				
				
			}
			else {
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
	
	private void send(int position) {
		toggleProgressBarVisibility();
		
		Bundle extras = getIntent().getExtras();
		selectedUser = userObjects.get(position);
		long dateTimeMillis = extras.getLong("dateTime");
		String locationId = extras.getString("locationId");
		String subject = extras.getString("subject");
		String notes = extras.getString("notes");
		
		Calendar dateTime = Calendar.getInstance();
		dateTime.setTimeInMillis(dateTimeMillis);
		Date finalDate = dateTime.getTime();
		
		examLocation = new ParseObject("ExamLocation");
		examLocation.put("title", location.getString("title"));
		examLocation.put("location", location);
		
		action = new ParseObject("Action");
		action.put("from", ParseUser.getCurrentUser());
		action.put("to", selectedUser);
		action.put("type", "request");
		
		exam = new ParseObject("Exam");
		exam.put("dateTime", finalDate);
		exam.put("location", examLocation);
		exam.put("subject", subject);
		if(notes != null) {
			exam.put("notes", notes);
		}
		List<ParseObject> actionList = new ArrayList<ParseObject>();
		actionList.add(action);
		exam.put("actions", actionList);
		
		event = new ParseObject("Event");
		event.put("createdBy", ParseUser.getCurrentUser());
		event.put("status", false);
		event.put("exam", exam);
		ParseACL recordAcl = new ParseACL();
		recordAcl.setReadAccess(ParseUser.getCurrentUser(), true);
		recordAcl.setReadAccess(selectedUser, true);
		recordAcl.setWriteAccess(ParseUser.getCurrentUser(), true);
		event.setACL(recordAcl);
		event.saveEventually();
	
		action.put("uuid", UUID.randomUUID().toString());
		exam.put("uuid", UUID.randomUUID().toString());
		event.put("uuid", UUID.randomUUID().toString());
		event.pinInBackground("Event", saveCallback);
		//examLocation.put("uuid", UUID.randomUUID().toString());
		//examLocation.pinInBackground("ExamLocation");
		//action.put("uuid", UUID.randomUUID().toString());
		//action.pinInBackground("Action", saveCallback);
		
	/*	List<ParseObject> actions = new ArrayList<ParseObject>();
		actions.add(action);
		event = new ParseObject("Event");
		event.put("exam", exam);
		event.put("actions", actions);
		ParseACL eventAcl = new ParseACL();
		eventAcl.setPublicReadAccess(true);
		eventAcl.setWriteAccess(ParseUser.getCurrentUser(), true);
		eventAcl.setWriteAccess(selectedUser, true);
		event.setACL(eventAcl);
		event.saveInBackground(saveCallback);	*/ 
	}
	
	private void refreshPressed() {
		refreshing=true;
		toggleProgressBarVisibility();
		ParseQuery<ParseUser> queryUsers = ParseUser.getQuery();
		queryUsers.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
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
		//queryUsersLocal.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		queryUsersLocal.fromLocalDatastore();
		queryUsersLocal.findInBackground(findCallbackLocal);
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) {
			ParseQuery<ParseUser> queryUsersCloud = ParseUser.getQuery();
			queryUsersCloud.findInBackground(findCallbackCloud);
		}
		
		Bundle extras = getIntent().getExtras();
		String uuid = extras.getString("locationUuid");
		String parseId = extras.getString("locationParseId");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
		query.fromLocalDatastore();
		if(uuid != null) {
			query.whereEqualTo("uuid", uuid);
			query.getFirstInBackground(new GetCallback<ParseObject>() {

				@Override
				public void done(ParseObject object,
						ParseException e) {
					if(e == null) {
						object.remove("uuid");
						location = object;
					}
					else {
						Log.d("SelectContactActivity", "Failed to get location object by uuid");
					}
				}
				
			});
		}
		else {
			query.getInBackground(parseId, new GetCallback<ParseObject>() {
			    public void done(ParseObject object, ParseException e) {
			        if (e == null) {
			            location = object;
			        } else {
			        	Log.d("SelectContactActivity", "Failed to get location object by parse id");
			        }
			    }
			});
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK) {
			//setResult(RESULT_OK);
			//finish();
		}
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		String user = userList.get(position);
		final int index = position;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Send to "+user+"?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   send(index);
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        builder.show();
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
		
		getMenuInflater().inflate(R.menu.select_contact, menu);
		
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
