package com.adarshhasija.ahelp;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.adarshhasija.ahelp.R;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class RecordEditActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	
	private ParseObject record=null;
	private ParseUser otherUser=null;
	private ArrayList<ParseUser> userObjects;
	private ArrayList<String> userList;
	private Calendar dateTime;
	
	//MenuItems
	private MenuItem progressButton;
	private MenuItem sendButton;
	
	//UI Items
	private Spinner userWidget;
	private EditText studentWidget;
	private EditText subjectWidget;
	private EditText locationWidget;
	private Button datePicker;
	private Button timePicker;
	
	/*
	 * Parse callbacks
	 * 
	 */
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				
				JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.ahelp.intent.RECEIVE");
					jsonObj.put("objectId", record.getObjectId());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	      /*  	ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
				pushQuery.whereEqualTo("phoneNumber", ParseUser.getCurrentUser().getString("phoneNumber"));
				ParsePush push = new ParsePush();
				push.setQuery(pushQuery); // Set our Installation query
				push.setData(jsonObj);
				push.sendInBackground();	*/
	        	
	        	HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("recipientPhoneNumber", otherUser.getString("phoneNumber"));
				params.put("data", jsonObj);
				ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
				   public void done(String success, ParseException e) {
				       if (e == null) {
				    	   toggleProgressButton();
				    	   finish();
				       }
				       else {
				    	   Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				       }
				   }
				});
				
				
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	//Populate the select recipient spinner. 
	//Currently not in use as user is select from the select contact page
	private FindCallback populateSpinnerFindCallback = new FindCallback<ParseUser>() {

		@Override
		public void done(List<ParseUser> list, ParseException e) {
			if (e == null) {
				userObjects = new ArrayList<ParseUser>();
	            userList = new ArrayList<String>();
	            
	            MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
	            HashMap<String, String> localContacts = mainApplication.getUpdatedDeviceContactsList();
	            for(ParseUser user : list) {
	            	if(localContacts.containsKey(user.getString("phoneNumber"))) {
	            		userObjects.add(user); //This is needed later when saving
	            		userList.add(user.getString("firstName")+" "+user.getString("lastName"));
	            	}
	            }
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, userList);
	            Spinner userView = ((Spinner) findViewById(R.id.user));
	            userView.setAdapter(adapter);
	        } else {
	            Log.d("RecordEditActivity", "Error: " + e.getMessage());
	        }
			
		}
		
	};
	private GetCallback getUserCallback = new GetCallback<ParseUser>() {
		
		@Override
        public void done(ParseUser user, ParseException e) {
			if(e == null) {
				otherUser = user;
				setTitle(user.getString("firstName") + " " + user.getString("lastName"));
			}
			else {
				Toast.makeText(RecordEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
        }
    };
      
    /*
     * Action bar button functions
     *   
     */
    private void sendPressed() 
    {
    	String student = studentWidget.getText().toString();
		String subject = subjectWidget.getText().toString();
		String location = locationWidget.getText().toString();
		
		if(validate())
		{
			toggleProgressButton();
			
			if(record == null) {
				record = new ParseObject("Record");
				record.put("user", ParseUser.getCurrentUser());
			}

			//We are not fetching the user from the spinner, it comes from the select contact page
	/*		ParseUser selectedUser=null;
			if(userWidget.getVisibility() != View.GONE) {
				selectedUser = userObjects.get((int)userWidget.getSelectedItemId());
			}
			if(selectedUser != null) {
				record.put("recipient", userObjects.get((int)userWidget.getSelectedItemId()));
			}	*/
			record.put("recipient", otherUser);
			record.put("student", student);
			record.put("subject", subject);
			record.put("location", location);
			Date finalDate = dateTime.getTime();
			record.put("dateTime", finalDate);
			record.put("status", "edited");
			record.put("status_by", ParseUser.getCurrentUser());
			
			Intent returnIntent = new Intent();
			MainApplication mainApplication = (MainApplication) getApplicationContext();
			mainApplication.setModifiedRecord(record);
			Bundle bundle = new Bundle();
			bundle.putString("student", student);
			bundle.putString("subject", subject);
			bundle.putString("status", "edited");
			ParseProxyObject ppo = new ParseProxyObject(record);
			//returnIntent.putExtras(bundle);
			returnIntent.putExtra("parseObject", ppo);
			setResult(RESULT_OK,returnIntent);
			
			record.saveInBackground(saveCallback);
		}
    }
    
    /*
     * Private functions
     * 
     */
    private boolean validate()
    {
    	String student = studentWidget.getText().toString();
		String subject = subjectWidget.getText().toString();
		String location = locationWidget.getText().toString();
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() == null) {
			Toast.makeText(getBaseContext(), "No internet connection, cannot save", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(student.isEmpty()) {
			Toast.makeText(getBaseContext(), "You have not entered a description", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(subject.isEmpty()) {
			Toast.makeText(getBaseContext(), "You have not entered a subject", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(location.isEmpty()) {
			Toast.makeText(getBaseContext(), "You have not entered a location", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		Calendar c = Calendar.getInstance();
		if(dateTime.getTime().compareTo(c.getTime()) < 0) {
			Toast.makeText(getBaseContext(), "You must enter a date and time that is in the future", Toast.LENGTH_SHORT).show();
			return false;
		}
    	
    	return true;
    }
    
    private void toggleProgressButton()
    {
    	if(!progressButton.isVisible())
    	{
    		sendButton.setVisible(false);
			progressButton.setActionView(R.layout.action_progressbar);
            progressButton.expandActionView();
			progressButton.setVisible(true);
    	}
    	else 
    	{
    		progressButton.setVisible(false);
			sendButton.setVisible(true);
    	}
    }
    
    public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("hourOfDay", dateTime.get(Calendar.HOUR_OF_DAY));
	    args.putInt("minute", dateTime.get(Calendar.MINUTE));
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "timepicker");
	}
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("year", dateTime.get(Calendar.YEAR));
	    args.putInt("month", dateTime.get(Calendar.MONTH));
	    args.putInt("dayOfMonth", dateTime.get(Calendar.DAY_OF_MONTH));
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	/**
	 * Hides the soft keyboard
	 */
	public void hideSoftKeyboard() {
	    if(getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}
	
	
	/*
	 * Picker dialog set listeners
	 * 
	 * 
	 */
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		dateTime.set(dateTime.get(Calendar.YEAR), 
						dateTime.get(Calendar.MONTH), 
						dateTime.get(Calendar.DAY_OF_MONTH), 
						hourOfDay, 
						minute);
		
		int hour=hourOfDay;
		String am_pm="PM";
		if(hourOfDay < 12) {
			am_pm = "AM";
			if(hourOfDay == 0)
				hour = 12;
		}
		else if(hourOfDay > 12) {
			am_pm = "PM";
			hour = hourOfDay - 12;
		}
		
		Button timePicker = ((Button) findViewById(R.id.timePicker));
		if(minute < 10) timePicker.setText(hour+":0"+minute+" "+am_pm);
		else timePicker.setText(hour+":"+minute+" "+am_pm);
	}



	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		dateTime.set(year, monthOfYear, dayOfMonth);
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, monthOfYear);
		String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		
		Button datePicker = ((Button) findViewById(R.id.datePicker));
		datePicker.setText(dayOfMonth+" "+month+" "+year);
	}
	
	
      

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit_activity);

		MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
		record = mainApplication.getSelectedRecord();
		
		if(record != null) {
			ParseUser user = record.getParseUser("user");
			ParseUser recipient = record.getParseUser("recipient");
			ParseUser currentUser = ParseUser.getCurrentUser();
			ParseUser userToFetch;
		
			//If they are not the same, this means currentUser is the recipient, so display the user in title
			if(!user.getObjectId().equals(currentUser.getObjectId())) {
				userToFetch = user;
			}
			//If they are the same, currentUser is the user and recipient should be displayed
			else {
				userToFetch = recipient;
			}
			userToFetch.fetchIfNeededInBackground(getUserCallback);
		}
		else if(record == null) {
			//There is no record as we are creating a new record
			//Get the chosen recipient as passed in from the select contacts screen
			otherUser = mainApplication.getUserForNewRecord();
			setTitle(otherUser.getString("firstName") + " " + otherUser.getString("lastName"));
			mainApplication.setUserForNewRecord(null);
		}

	}
	
	

	@Override
	protected void onResume() {
		userWidget = (Spinner) findViewById(R.id.user);
		userWidget.setVisibility(View.GONE); //As we are display the chosen contact in the title, this is not needed now
		
		studentWidget = ((EditText) findViewById(R.id.student));
		subjectWidget = ((EditText) findViewById(R.id.subject));
		locationWidget = ((EditText) findViewById(R.id.location));
		datePicker = ((Button) findViewById(R.id.datePicker));
		timePicker = ((Button) findViewById(R.id.timePicker));
		
		ParseQuery<ParseUser> querySpinner = ParseUser.getQuery();
		querySpinner.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		//querySpinner.findInBackground(populateSpinnerFindCallback);
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
        String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if(hour > 12) hour = hour - 12;
        else if(hour == 0) hour = 12;
        int minute = c.get(Calendar.MINUTE);
        String am_pm = c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
        //String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        
        datePicker.setText(day+" "+month+" "+year);
		if(minute < 10) timePicker.setText(hour+":0"+minute+" "+am_pm);
		else timePicker.setText(hour+":"+minute+" "+am_pm);
        
        //set private variable dateTime
        dateTime = c;
        
        if(record != null) {
        	userWidget.setVisibility(View.GONE);

        	ParseUser user = record.getParseUser("user");
    		ParseUser recipient = record.getParseUser("recipient");
    		ParseUser currentUser = ParseUser.getCurrentUser();
    		ParseUser userToFetch;
    		
    		//If they are not the same, this means currentUser is the recipient, so display the user in title
    		if(!user.getObjectId().equals(currentUser.getObjectId())) {
    			userToFetch = user;
    		}
    		//If they are the same, currentUser is the user and recipient should be displayed
    		else {
    			userToFetch = recipient;
    		}
    		userToFetch.fetchIfNeededInBackground(getUserCallback);
			String studentString = record.getString("student");
			String subjectString = record.getString("subject");
			String locationString = record.getString("location");
			c.setTime(record.getDate("dateTime"));
			
			studentWidget.setText(studentString);
			subjectWidget.setText(subjectString);
			locationWidget.setText(locationString);
			
			year = c.get(Calendar.YEAR);
	        month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
	        day = c.get(Calendar.DAY_OF_MONTH);
	        hour = c.get(Calendar.HOUR_OF_DAY);
	        if(hour > 12) hour = hour - 12;
	        else if(hour == 0) hour = 12;
	        minute = c.get(Calendar.MINUTE);
	        am_pm = c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
			datePicker.setText(day+" "+month+" "+year);
			if(minute < 10) timePicker.setText(hour+":0"+minute+" "+am_pm);
			else timePicker.setText(hour+":"+minute+" "+am_pm);
		} 
        
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.send:
	        	hideSoftKeyboard();
	            sendPressed();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.record_edit_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		sendButton = (MenuItem)menu.findItem(R.id.send);
		
		return super.onCreateOptionsMenu(menu);
	}

}
