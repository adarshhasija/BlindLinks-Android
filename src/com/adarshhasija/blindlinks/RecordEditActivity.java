package com.adarshhasija.blindlinks;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.adarshhasija.blindlinks.R;
import com.parse.FindCallback;
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
	private ArrayList<ParseUser> userObjects;
	private ArrayList<String> userList;
	private Calendar dateTime;
	private MenuItem progressButton;
	private MenuItem saveButton;
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
				pushQuery.whereEqualTo("phoneNumber", ParseUser.getCurrentUser().getString("phoneNumber"));
				
				JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.blindlinks.intent.RECEIVE");
					jsonObj.put("objectId", record.getObjectId());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ParsePush push = new ParsePush();
				push.setQuery(pushQuery); // Set our Installation query
				push.setData(jsonObj);
				//push.setMessage("From the client");
				push.sendInBackground();
				
				
				progressButton.setVisible(false);
				saveButton.setVisible(true);
				finish();
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	private FindCallback populateSpinnerFindCallback = new FindCallback<ParseUser>() {

		@Override
		public void done(List<ParseUser> list, ParseException e) {
			if (e == null) {
				userObjects = new ArrayList<ParseUser>();
	            userList = new ArrayList<String>();
	            for(ParseUser user : list) {
	            	userObjects.add(user); //This is needed later when saving
	            	userList.add(user.getString("firstName")+" "+user.getString("lastName"));	
	            }
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, userList);
	            Spinner userView = ((Spinner) findViewById(R.id.user));
	            userView.setAdapter(adapter);
	        } else {
	            Log.d("RecordEditActivity", "Error: " + e.getMessage());
	        }
			
		}
		
	};
	private FindCallback setTitleFindCallback = new FindCallback() {

		@Override
		public void done(List arg0, ParseException arg1) {
			if (arg1 == null) {
				ParseUser user = (ParseUser) arg0.get(0);
				setTitle(user.getString("firstName") + " " + user.getString("lastName"));
				
		    } else {
		    	Toast.makeText(RecordEditActivity.this, arg1.getMessage(), Toast.LENGTH_SHORT).show();
		    }
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit_activity);

		MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
		record = mainApplication.getSelectedRecord();
		
		Spinner s1 = (Spinner) findViewById(R.id.user); s1.setFocusable(true); s1.setFocusableInTouchMode(true); s1.requestFocus();
		
	/*	ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
		            userList = new ArrayList<String>();
		            for(ParseObject obj : list) {
		            	userObjects.add(obj); //This is needed later when saving
		            	userList.add(obj.getString("firstName")+" "+obj.getString("lastName"));	
		            }
		            //AutoCompleteTextView categoryView = (AutoCompleteTextView) findViewById(R.id.user); 
		            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, userList);
		            //categoryView.setAdapter(adapter);
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
				
			}
		}); */

	}
	
	

	@Override
	protected void onResume() {
		EditText studentView = ((EditText) findViewById(R.id.student));
		EditText subjectView = ((EditText) findViewById(R.id.subject));
		Button datePicker = ((Button) findViewById(R.id.datePicker));
		Button timePicker = ((Button) findViewById(R.id.timePicker));
		
		ParseQuery<ParseUser> querySpinner = ParseUser.getQuery();
		querySpinner.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		querySpinner.findInBackground(populateSpinnerFindCallback);
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
        String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        String am_pm = c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
        //String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        
        //set private variable dateTime
        dateTime = c;
        
        if(record != null) {
        	Spinner userWidget = (Spinner) findViewById(R.id.user);
        	userWidget.setVisibility(View.GONE);
        	

			ParseUser user = record.getParseUser("user");
			ParseUser recipient = record.getParseUser("recipient");
			ParseUser currentUser = ParseUser.getCurrentUser();
			
			ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
			queryUser.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
			
			//If they are not the same, this means currentUser is the recipient, so display the user in title
			if(!user.getObjectId().equals(currentUser.getObjectId())) {
				queryUser.whereEqualTo("objectId", user.getObjectId());
			}
			//If they are the same, currentUser is the user and recipient should be displayed
			else {
				queryUser.whereEqualTo("objectId", recipient.getObjectId());
			}
			queryUser.findInBackground(setTitleFindCallback);
			String studentString = record.getString("student");
			String subjectString = record.getString("subject");
			c.setTime(record.getDate("dateTime"));
			
			studentView.setText(studentString);
			subjectView.setText(subjectString);
			
			year = c.get(Calendar.YEAR);
	        month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
	        day = c.get(Calendar.DAY_OF_MONTH);
	        hour = c.get(Calendar.HOUR);
	        minute = c.get(Calendar.MINUTE);
	        am_pm = c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
			datePicker.setText(day+" "+month+" "+year);
			if(minute < 10) timePicker.setText(hour+":0"+minute+" "+am_pm);
			else timePicker.setText(hour+":"+minute+" "+am_pm);
			
		} else {
			datePicker.setText(day+" "+month+" "+year);
			if(minute < 10) timePicker.setText(hour+":0"+minute+" "+am_pm);
			else timePicker.setText(hour+":"+minute+" "+am_pm);
		}
        
		super.onResume();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.record_edit_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		saveButton = (MenuItem)menu.findItem(R.id.save);
		saveButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Spinner userWidget = (Spinner) findViewById(R.id.user);
				EditText studentWidget = (EditText) findViewById(R.id.student);
				EditText subjectWidget = (EditText) findViewById(R.id.subject);

				String student = studentWidget.getText().toString();
				String subject = subjectWidget.getText().toString();
				
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
				
				saveButton.setVisible(false);
				progressButton.setActionView(R.layout.action_progressbar);
	            progressButton.expandActionView();
				progressButton.setVisible(true);
				
				ParseUser selectedUser=null;
				if(userWidget.getVisibility() != View.GONE) {
					selectedUser = userObjects.get((int)userWidget.getSelectedItemId());
				}
				if(record == null) {
					record = new ParseObject("Record");
					record.put("user", ParseUser.getCurrentUser());
				}

				if(selectedUser != null) {
					record.put("recipient", userObjects.get((int)userWidget.getSelectedItemId()));
				}
				record.put("student", student);
				record.put("subject", subject);
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
				
			/*	if(!categoryList.contains(category)) {
					ParseObject newCategory = new ParseObject("Category");
					newCategory.put("user", ParseUser.getCurrentUser());
					newCategory.put("title", category);
					newCategory.saveInBackground(saveCallback);
				} */
				
				return false; 
			}
		});
		
		return super.onCreateOptionsMenu(menu);
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
	
	

}
