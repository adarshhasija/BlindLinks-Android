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
import java.util.UUID;

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
import android.app.ListActivity;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class RecordEditActivity extends ListActivity { //extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	
	private MainApplication mainApplication;
	private ParseObject oldExam=null;

	//Input items
	private String parseId=null;
	private String uuid=null;
	private Calendar dateTime;
	private String locationUuid;
	private String locationParseId;
	private String subject;
	private String notes;
	
	//MenuItems
	private MenuItem progressButton;
	private MenuItem nextButton;
	private MenuItem saveButton;
	
	/*
	 * Parse callbacks
	 * 
	 */
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				Bundle bundle = new Bundle();
				bundle.putString("parseId", parseId);
				bundle.putString("uuid", uuid);
				Intent returnIntent = new Intent();
				returnIntent.putExtras(bundle);
				setResult(Activity.RESULT_OK, returnIntent);
				finish();
			/*	JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.ahelp.intent.RECEIVE");
					jsonObj.put("objectId", record.getObjectId());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
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
				});	*/
				
				
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	private GetCallback getUserCallback = new GetCallback<ParseUser>() {
		
		@Override
        public void done(ParseUser user, ParseException e) {
			if(e == null) {
				//otherUser = user;
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
    private void nextPressed() 
    {
    	if(validate()) {
    		Bundle bundle = new Bundle();
    		bundle.putLong("dateTime", dateTime.getTimeInMillis());
    		bundle.putString("locationUuid", locationUuid);
    		bundle.putString("locationParseId", locationParseId);
    		bundle.putString("subject", subject);
    		bundle.putString("notes", notes);
		
    		Intent intent = new Intent(RecordEditActivity.this, SelectContactActivity.class);
    		intent.putExtras(bundle);
    		startActivityForResult(intent, 50000);
    	}
    }
    
    
    private void savePressed() {
    	if(!validate()) {
    		return;
    	}
    	ParseObject exam;
    	ParseQuery query = ParseQuery.getQuery("Exam");
    	query.fromLocalDatastore();
    	try {
    		if(parseId != null) {
    			exam = query.get(parseId);
    		}
    		else {
    			query.whereEqualTo("uuid", uuid);
    			exam = query.getFirst();
    		}
    		exam.put("dateTime", dateTime.getTime());
    		ParseObject location=null;
    		
    /*		ParseObject examVersioned = new ParseObject("ExamVersioned");
    		examVersioned.put("dateTime", oldExam.getDate("dateTime"));
    		examVersioned.put("location", oldExam.getParseObject("location"));
    		examVersioned.put("subject", oldExam.getString("subject"));
    		examVersioned.put("notes", oldExam.getString("notes"));
    		examVersioned.put("uuid", UUID.randomUUID().toString());	*/
    		
    		ParseObject action = new ParseObject("Action");
    		action.put("from", ParseUser.getCurrentUser());
    		action.put("type", "examUpdate");
    		action.put("oldDate", oldExam.getDate("dateTime"));
    		action.put("oldLocation", oldExam.getParseObject("location"));
    		action.put("oldSubject", oldExam.getString("subject"));
    		if(oldExam.getString("notes") != null) {
    			action.put("oldNotes", oldExam.getString("notes"));
    		}
    		action.put("newDate", dateTime.getTime());
    		
    		ParseQuery locationQuery = ParseQuery.getQuery("Location");
    		locationQuery.fromLocalDatastore();
    		if(locationParseId != null) {
    			location = locationQuery.get(locationParseId);
    		}
    		else {
    			locationQuery.whereEqualTo("uuid", locationUuid);
    			location = locationQuery.getFirst();
    		}
    		if(location != null) {
    			ParseObject examLocation = exam.getParseObject("location");
    			examLocation.fetchFromLocalDatastore();
    			examLocation.put("title", location.getString("title"));
    			examLocation.put("location", location);
    			exam.put("location", examLocation);
    			action.put("newLocation", examLocation);
    		}
    		exam.put("subject", subject);
    		action.put("newSubject", subject);
    		if(notes != null) {
    			exam.put("notes", notes);
    			action.put("newNotes", notes);
    		}
    		else {
    			exam.remove("notes");
    		}
    		exam.add("actions", action);
    		
    		exam.saveEventually();
    		action.put("uuid", UUID.randomUUID().toString());
    		exam.pinInBackground(saveCallback);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /*
     * Private functions
     * 
     */
    private boolean validate()
    {
		Calendar c = Calendar.getInstance();
		if(dateTime.getTime().compareTo(c.getTime()) < 0) {
			Toast.makeText(getBaseContext(), "You must enter a date and time that is in the future", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(locationUuid == null && locationParseId == null) {
			Toast.makeText(getBaseContext(), "You have not entered a location", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(subject == null) {
			Toast.makeText(getBaseContext(), "You have not entered a subject", Toast.LENGTH_SHORT).show();
			return false;
		}
    	
    	return true;
    }
    
    private void toggleProgressButton()
    {
    	if(!progressButton.isVisible())
    	{
    		nextButton.setVisible(false);
			progressButton.setActionView(R.layout.action_progressbar);
            progressButton.expandActionView();
			progressButton.setVisible(true);
    	}
    	else 
    	{
    		progressButton.setVisible(false);
			nextButton.setVisible(true);
    	}
    }
	
	
	/*
	 * Picker dialog set listeners
	 * 
	 * 
	 */
	private AdapterView.OnItemClickListener itemClickedListener = new AdapterView.OnItemClickListener() {

	      @Override
	      public void onItemClick(AdapterView<?> parent, final View view,
	          int position, long id) {
	    	  switch (position) {
		        case 0:
		        	Intent intent = new Intent(RecordEditActivity.this, MonthYearPickerActivity.class);
					startActivityForResult(intent, position);
		            return;
		        default:
		            return;
		    }
	      }

	    };
	
	
      

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.record_edit_activity);

		mainApplication = (MainApplication) getBaseContext().getApplicationContext();
		ParseObject exam;
		ParseObject examLocation;
		
		exam = null;
		examLocation = null;
		dateTime = null;
		locationUuid = null;
		locationParseId = null;
		subject = "eg: Maths";
		notes = null;
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			parseId = extras.getString("parseId");
			uuid = extras.getString("uuid");
		}
		ParseQuery query = ParseQuery.getQuery("Exam");
		query.fromLocalDatastore();
		if(parseId != null) {
			try {
				exam = query.get(parseId);
				oldExam = new ParseObject("Exam");
				oldExam.put("dateTime", exam.getDate("dateTime"));
				oldExam.put("subject", exam.getString("subject"));
				if(exam.getString("notes") != null) {
					oldExam.put("notes", exam.getString("notes"));
				}
				if(exam != null) {
					Date date = exam.getDate("dateTime");
					examLocation = exam.getParseObject("location");
					examLocation.fetchFromLocalDatastore();
					
					ParseObject oldExamLocation = new ParseObject("Location");
					oldExamLocation.put("title", examLocation.getString("title"));
					oldExam.put("location", oldExamLocation);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(uuid != null) {
			query.whereEqualTo("uuid", uuid);
			try {
				exam = query.getFirst();
				oldExam = exam;
				if(exam != null) {
					examLocation = exam.getParseObject("location");
					examLocation.fetchFromLocalDatastore();
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(exam != null) {
			setTitle("Edit details");
			
			Date date = exam.getDate("dateTime");
			dateTime = Calendar.getInstance();
			dateTime.setTime(date);
			ParseObject location = examLocation.getParseObject("location");
			try {
				location.fetchFromLocalDatastore();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			locationParseId = location.getObjectId();
			locationUuid = location.getString("uuid");
			subject = exam.getString("subject");
			notes = exam.getString("notes");
		}
		
		String timeString = null;
		String location = "eg: School";
		
		if(dateTime == null) {
			dateTime = Calendar.getInstance();
			int mod = dateTime.get(Calendar.MINUTE) % 15;
			dateTime.add(Calendar.MINUTE, 15-mod);
		}
		int dayOfMonth = dateTime.get(Calendar.DAY_OF_MONTH);
		String monthString = dateTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		int year = dateTime.get(Calendar.YEAR);
		int hourOfDay = dateTime.get(Calendar.HOUR_OF_DAY);
		if(hourOfDay > 12) hourOfDay = hourOfDay - 12;
        else if(hourOfDay == 0) hourOfDay = 12;
		int minute = dateTime.get(Calendar.MINUTE);
        String minuteString = "";
        if(minute == 0) minuteString = "00";
        else minuteString = Integer.toString(minute);
        String am_pm = dateTime.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
		timeString = dayOfMonth + " " + monthString + " " + year + "  " + hourOfDay + ":" + minuteString + " " + am_pm;
		if(locationParseId != null || locationUuid != null) {
			location = examLocation.getString("title");
		}
		String notesString = null;
		if(notes == null) {
			notesString = "eg: student's name, student's phone number";
		}
		else {
			notesString = notes;
		}
		
		String[] content = new String[] { 	timeString, 
				location, 
				subject, 
				notesString};
		
		
        
	    
	    final ArrayList<String> contentList = new ArrayList<String>();
	    for (int i = 0; i < content.length; ++i) {
	      contentList.add(content[i]);
	    }
	    
	    RecordEditAdapter editAdapter = new RecordEditAdapter(this, 0, contentList);
	    setListAdapter(editAdapter);

	}
	

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent intent;
		Bundle bundle;
		
		switch (position) {
			case 0:
				intent = new Intent(RecordEditActivity.this, MonthYearPickerActivity.class);
				startActivityForResult(intent, position);
				return;
			case 1:
				intent = new Intent(RecordEditActivity.this, SelectLocationActivity.class);
				startActivityForResult(intent, position);
				return;
			case 2:
				bundle = new Bundle();
		        bundle.putString("subject", subject);
		        
				intent = new Intent(RecordEditActivity.this, SubjectEditActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, position);
				return;
			case 3:
				bundle = new Bundle();
		        bundle.putString("notes", notes);
		        //Exam exam = new Exam(123, "abc");
		        //exam.setParseId("12345");
		        //bundle.putParcelable("parcelable", exam);
				
				intent = new Intent(RecordEditActivity.this, AdditionalNotesActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, position);
				return;
			default:
				return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == Activity.RESULT_OK) {
			RecordEditAdapter adapter = (RecordEditAdapter) getListAdapter();
			if(data == null) {
				return;
			}
			Bundle extras = data.getExtras();
			
			switch(requestCode) {
				case 0:
					dateTime.set(Calendar.YEAR, extras.getInt("year"));
					dateTime.set(Calendar.MONTH, extras.getInt("month"));
					dateTime.set(Calendar.DAY_OF_MONTH, extras.getInt("dayOfMonth"));
					dateTime.set(Calendar.HOUR_OF_DAY, extras.getInt("hourOfDay"));
					dateTime.set(Calendar.MINUTE, extras.getInt("minute"));
					
					String monthString = dateTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
					int hourOfDay = dateTime.get(Calendar.HOUR_OF_DAY);
					if(hourOfDay > 12) hourOfDay = hourOfDay - 12;
			        else if(hourOfDay == 0) hourOfDay = 12;
					
					int minute = dateTime.get(Calendar.MINUTE);
			        String minuteString = "";
			        if(minute == 0) minuteString = "00";
			        else minuteString = Integer.toString(minute);
			        
					String am_pm = dateTime.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
					String dateTimeString = dateTime.get(Calendar.DAY_OF_MONTH) + " " + monthString + " " + dateTime.get(Calendar.YEAR) +
											"  " + hourOfDay + ":" + minuteString + " " + am_pm;
					
					adapter.remove(adapter.getItem(0));
					adapter.insert(dateTimeString, 0);
					adapter.notifyDataSetChanged();
					return;
				case 1:
					locationUuid = extras.getString("uuid");
					locationParseId = extras.getString("parseId");
					adapter.remove(adapter.getItem(1));
					adapter.insert(extras.getString("title"), 1);
					adapter.notifyDataSetChanged();
					return;
				case 2:
					adapter.remove(adapter.getItem(2));
					
					if(!extras.getString("subject").isEmpty()) {
						subject = extras.getString("subject");
						adapter.insert(extras.getString("subject"), 2);
					}
					else {
						subject = null;
						adapter.insert("eg: Maths", 2);
					}
					adapter.notifyDataSetChanged();
					return;
				case 3:
					adapter.remove(adapter.getItem(3));
					
					if(!extras.getString("notes").isEmpty()) {
						notes = extras.getString("notes");
						adapter.insert(extras.getString("notes"), 3);
					}
					else {
						notes = null;
						adapter.insert("eg: student's name, student's phone number", 3);
					}
					adapter.notifyDataSetChanged();
					return;
				case 50000: //This means we are returning from SelectContactActivity and new event has been successfully created
					setResult(Activity.RESULT_OK, data);
					finish();
					return;
				default:
					return;
			}	
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.next:
	            nextPressed();
	        	return true;
	        case R.id.save:
	        	savePressed();
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
		
		nextButton = (MenuItem)menu.findItem(R.id.next);
		saveButton = (MenuItem)menu.findItem(R.id.save);
		
		if(parseId != null || uuid != null) {
			nextButton.setVisible(false);
			saveButton.setVisible(true);
		}
		else {
			nextButton.setVisible(true);
			saveButton.setVisible(false);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

}
