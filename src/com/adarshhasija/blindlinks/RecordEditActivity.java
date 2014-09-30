package com.adarshhasija.blindlinks;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.ngotransactionrecords.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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
	private ArrayList<ParseObject> userObjects;
	private ArrayList<String> userList;
	private Calendar dateTime;
	private MenuItem progressButton;
	private MenuItem saveButton;
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				progressButton.setVisible(false);
				saveButton.setVisible(true);
				if(record != null) {
					setTitle("Edit Record");
				}
				else {
					setTitle("New Record");
				}
				finish();
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	private FindCallback findCallback = new FindCallback() {

		@Override
		public void done(List arg0, ParseException arg1) {
			if (arg1 == null) {
				EditText recipientWidget = (EditText) findViewById(R.id.user);
				ParseUser user = (ParseUser) arg0.get(0);
				recipientWidget.setText(user.getString("firstName") + " " + user.getString("lastName"));
				
		    } else {
		        // Something went wrong.
		    }
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit_activity);
		
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
        
		Button datePicker = ((Button) findViewById(R.id.datePicker));
		datePicker.setText(day+" "+month+" "+year);
		
		Button timePicker = ((Button) findViewById(R.id.timePicker));
		if(minute < 10) timePicker.setText(hour+":0"+minute+" "+am_pm);
		else timePicker.setText(hour+":"+minute+" "+am_pm);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
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
		});
		
		MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
		record = mainApplication.getSelectedRecord();
		
		if(record != null) {
			setTitle("Edit Record");
			
			EditText studentView = ((EditText) findViewById(R.id.student));
			EditText subjectView = ((EditText) findViewById(R.id.subject));

			ParseUser user = record.getParseUser("user");
			ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
			queryUser.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
			queryUser.whereEqualTo("objectId", user.getObjectId());
			queryUser.findInBackground(findCallback);
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
			
		}
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
				EditText recipientWidget = (EditText) findViewById(R.id.user);
				EditText studentWidget = (EditText) findViewById(R.id.student);
				EditText subjectWidget = (EditText) findViewById(R.id.subject);
				//EditText additionalDetailsWidget = (EditText) findViewById(R.id.additionalDetails);
				
				String recipient = recipientWidget.getText().toString();
				String student = studentWidget.getText().toString();
				String subject = subjectWidget.getText().toString();
				
				ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() == null) {
					Toast.makeText(getBaseContext(), "No internet connection, cannot save", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(recipient.isEmpty()) {
					Toast.makeText(getBaseContext(), "You have not entered a volunteer name", Toast.LENGTH_SHORT).show();
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
				
				String firstName = recipient.split(" ")[0];
				String lastName = recipient.split(" ")[1];
				firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
				lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
				
				saveButton.setVisible(false);
				progressButton.setActionView(R.layout.action_progressbar);
	            progressButton.expandActionView();
				progressButton.setVisible(true);
				setTitle("Saving...");
				
				if(record == null) {
					record = new ParseObject("Record");
					record.put("user", ParseUser.getCurrentUser());
				}
				int index = userList.indexOf(firstName+" "+lastName);
				if(index > -1) record.put("recipient", userObjects.get(index));
				record.put("student", student);
				record.put("subject", subject);
				Date finalDate = dateTime.getTime();
				record.put("dateTime", finalDate);
				
				Intent returnIntent = new Intent();
				MainApplication mainApplication = (MainApplication) getApplicationContext();
				mainApplication.setModifiedRecord(record);
				Bundle bundle = new Bundle();
				bundle.putString("student", student);
				bundle.putString("subject", subject);
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
