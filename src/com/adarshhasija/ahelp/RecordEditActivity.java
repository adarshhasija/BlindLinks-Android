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
	private ParseObject scribeRequest=null;
	
	private Calendar dateTime = Calendar.getInstance();
	//This is the reference to the currently selected location
	private String locationUuid;
	private String locationParseId;
	//This is a reference to the currently selected subject
	private String subjectUuid;
	private String subjectParseId;
	private String representeePhoneNumber;
	private String representeeFirstName;
	private String representeeLastName;
	
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
				JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.ahelp.intent.RECEIVE");
					jsonObj.put("objectId", scribeRequest.getObjectId());
					jsonObj.put("uuid", scribeRequest.getString("uuid"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	HashMap<String, Object> params = new HashMap<String, Object>();
	        	List<ParseObject> phoneNumbers = scribeRequest.getList("phoneNumbers");
				params.put("recipientPhoneNumber", phoneNumbers.get(0));
				params.put("data", jsonObj);
				ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
				   public void done(String success, ParseException e) {
				       if (e == null) {
				    	   toggleProgressButton();
				    	   Bundle bundle = new Bundle();
							bundle.putString("parseId", scribeRequest.getObjectId());
							bundle.putString("uuid", scribeRequest.getString("uuid"));
							Intent returnIntent = new Intent();
							returnIntent.putExtras(bundle);
							setResult(Activity.RESULT_OK, returnIntent);
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
    		bundle.putString("subjectUuid", subjectUuid);
    		bundle.putString("subjectParseId", subjectParseId);
    		bundle.putString("representeePhoneNumber", representeePhoneNumber);
    		bundle.putString("representeeFirstName", representeeFirstName);
    		bundle.putString("representeeLastName", representeeLastName);
		
    		Intent intent = new Intent(RecordEditActivity.this, SelectContactActivity.class);
    		intent.putExtras(bundle);
    		startActivityForResult(intent, 50000);
    	}
    }
    
    
    private void savePressed() throws ParseException {
    	if(!validate()) {
    		return;
    	}
    	ParseObject oldScribeRequest = oldScribeRequestSetup(scribeRequest);
    	ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRequest");
    	query.fromLocalDatastore();
    	try {
    		Log.d("WOW", "****************FIRST*******************");
    		ParseObject action = createExamUpdateAction(oldScribeRequest);
    		Log.d("WOW", "**************ONE****************");
    		updateScribeRequest();
    		Log.d("WOW", "****************TWO****************");
    		//scribeRequest.add("actions", action);
    		List<ParseObject> list = scribeRequest.getList("actions");
    		list.add(action);
    		scribeRequest.remove("actions");
    		scribeRequest.put("actions", list);
    		
    		
    		Log.d("WOW", "****************SECOND*********************");
    		ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
    		if(cm.getActiveNetworkInfo() != null) {
    			Log.d("WOW", "*****************THIRD*******************");
				scribeRequest.pinInBackground();
				toggleProgressButton();
				scribeRequest.saveInBackground(saveCallback);
			}
			else {
				scribeRequest.remove("isDraft");
				scribeRequest.put("isDraft", true);
				scribeRequest.pinInBackground();
				
				Bundle bundle = new Bundle();
				bundle.putString("parseId", scribeRequest.getObjectId());
				bundle.putString("uuid", scribeRequest.getString("uuid"));
				Intent returnIntent = new Intent();
				returnIntent.putExtras(bundle);
				setResult(Activity.RESULT_OK, returnIntent);
				finish();
			}		
			
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
		
		if(scribeRequest != null) {
			return true;
		}
		
		if(locationUuid == null && locationParseId == null) {
			Toast.makeText(getBaseContext(), "You have not selected a location", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(subjectUuid == null && subjectParseId == null) {
			Toast.makeText(getBaseContext(), "You have not selected a subject", Toast.LENGTH_SHORT).show();
			return false;
		}
    	
    	return true;
    }
    
    private void toggleProgressButton()
    {
    	if(!progressButton.isVisible())
    	{
    		nextButton.setVisible(false);
    		saveButton.setVisible(false);
			progressButton.setActionView(R.layout.action_progressbar);
            progressButton.expandActionView();
			progressButton.setVisible(true);
    	}
    	else 
    	{
    		progressButton.setVisible(false);
    		Bundle extras = getIntent().getExtras();
    		if(extras != null) {
    			saveButton.setVisible(true);
    		}
    		else {
    			nextButton.setVisible(true);
    		}
    	}
    }
    
    private ParseObject oldScribeRequestSetup(ParseObject input) throws ParseException
    {
    	ParseObject output;
    	
    	output = new ParseObject("ScribeRequest");
		output.put("dateTime", input.getDate("dateTime"));
    	
		ParseObject inputLocation;
		inputLocation = input.getParseObject("location");
		inputLocation.fetchFromLocalDatastore();
//		scribeRequestLocationReferenceLocation = scribeRequestLocation.getParseObject("referenceLocation");
//		scribeRequestLocationReferenceLocation.fetchFromLocalDatastore();
		
		ParseObject inputSubject;
		inputSubject = input.getParseObject("subject");
		inputSubject.fetchFromLocalDatastore();
//		scribeRequestSubjectReferenceSubject = scribeRequestSubject.getParseObject("referenceSubject");
//		scribeRequestSubjectReferenceSubject.fetchFromLocalDatastore();	
		
		ParseObject outputLocation;
		outputLocation = new ParseObject("ScribeRequestLocation");
		outputLocation.put("title", inputLocation.getString("title"));
		output.put("location", outputLocation);
		
		ParseObject outputSubject;
		outputSubject = new ParseObject("ScribeRequestSubject");
		outputSubject.put("title", inputSubject.getString("title"));
		output.put("subject", outputSubject);
		
		if(input.getString("representeePhoneNumber") != null &&
				input.getString("representeeFirstName") != null && 
					input.getString("representeeLastName") != null) {
			output.put("representeePhoneNumber", input.getString("representeePhoneNumber"));
			output.put("representeeFirstName", input.getString("representeeFirstName"));
			output.put("representeeLastName", input.getString("representeeLastName"));
		}
		
		return output;
    }
    
    private void setupPrivateVariables(ParseObject input) throws ParseException
    {
    	dateTime = null;
		locationUuid = null;
		locationParseId = null;
		subjectUuid = null;
		subjectParseId = null;
		representeePhoneNumber = null;
		representeeFirstName = null;
		representeeLastName = null;
		
		Date date = input.getDate("dateTime");
		dateTime = Calendar.getInstance();
		dateTime.setTime(date);
		
		ParseObject location;
		location = input.getParseObject("location");
		location.fetchFromLocalDatastore();
		//locationParseId = location.getObjectId();
		//locationUuid = location.getString("uuid");
		
		ParseObject subject;
		subject = input.getParseObject("subject");
		subject.fetchFromLocalDatastore();
		//subjectParseId = subject.getObjectId();
		//subjectUuid = subject.getString("uuid");
		
		representeePhoneNumber = input.getString("representeePhoneNumber");
		representeeFirstName = input.getString("representeeFirstName");
		representeeLastName = input.getString("representeeLastName");
    }
    
    private String dateTimeFormatted(Date input)
    {
    	String result = null;
    	
    	Calendar inputDateTime = Calendar.getInstance();
    	if(input != null) {
    		inputDateTime.setTime(input);
    	}
    	else {
    		if(inputDateTime.get(Calendar.HOUR_OF_DAY) < 7) {
    			inputDateTime.set(Calendar.HOUR_OF_DAY, 7);
    			inputDateTime.set(Calendar.MINUTE, 0);
    		}
    		else {
    			int mod = inputDateTime.get(Calendar.MINUTE) % 15;
				inputDateTime.add(Calendar.MINUTE, 15-mod);
    		}
    	}
    	
    	int dayOfMonth = inputDateTime.get(Calendar.DAY_OF_MONTH);
		String monthString = inputDateTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		int year = inputDateTime.get(Calendar.YEAR);
		int hourOfDay = inputDateTime.get(Calendar.HOUR_OF_DAY);
		if(hourOfDay > 12) hourOfDay = hourOfDay - 12;
        else if(hourOfDay == 0) hourOfDay = 12;
		int minute = inputDateTime.get(Calendar.MINUTE);
        String minuteString = "";
        if(minute == 0) minuteString = "00";
        else minuteString = Integer.toString(minute);
        String am_pm = inputDateTime.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
		result = dayOfMonth + " " + monthString + " " + year + "  " + hourOfDay + ":" + minuteString + " " + am_pm;
    	
    	return result;
    }
    
    private String[] setupUIContent(ParseObject input) throws ParseException
    {
    	String timeString=null;
    	if(input != null) {
    		timeString = dateTimeFormatted(input.getDate("dateTime"));
    	}
    	else {
    		timeString = dateTimeFormatted(null);
    	}
		String locationString = "No location selected";
		ParseObject locationObject = null;
		if(input != null) {
			locationObject = input.getParseObject("location");
			locationObject.fetchFromLocalDatastore();
			locationString = locationObject.getString("title");
		}
		
		String subjectString = "No subject selected";
		ParseObject subjectObject = null;
		if(input != null) {
			subjectObject = scribeRequest.getParseObject("subject");
			subjectObject.fetchFromLocalDatastore();
			subjectString = subjectObject.getString("title");
		}
		
		String notesString = null;
		if(representeePhoneNumber == null &&
				representeeFirstName == null && 
					representeeLastName == null) {
			notesString = "No";
		}
		else {
			notesString = representeeFirstName + " " + representeeLastName;
		}
		
		String[] content = new String[] { 	timeString, 
				locationString, 
				subjectString, 
				notesString};
		
		return content;
    }
    
    
    private ParseObject createExamUpdateAction(ParseObject oldScribeRequest) throws ParseException
    {
    	ParseObject action = new ParseObject("Action");
    	action.put("from", ParseUser.getCurrentUser());
		action.put("type", "examUpdate");
		action.put("oldDate", oldScribeRequest.getDate("dateTime"));
		ParseObject oldLocationObject = oldScribeRequest.getParseObject("location");
		String oldLocationString = oldLocationObject.getString("title");
		action.put("oldLocationString", oldLocationString);
		ParseObject oldSubjectObject = oldScribeRequest.getParseObject("subject");
		String oldSubjectString = oldSubjectObject.getString("title");
		action.put("oldSubjectString", oldSubjectString);
		if(oldScribeRequest.getString("representeePhoneNumber") != null &&
				oldScribeRequest.getString("representeeFirstName") != null &&
					oldScribeRequest.getString("representeeLastName") != null) {
			action.put("oldRepresenteePhoneNumber", oldScribeRequest.getString("representeePhoneNumber"));
			action.put("oldRepresenteeFirstName", oldScribeRequest.getString("representeeFirstName"));
			action.put("oldRepresenteeLastName", oldScribeRequest.getString("representeeLastName"));
		}
		
		
		
		action.put("newDate", dateTime.getTime());
		ParseObject location=null;
		ParseQuery<ParseObject> locationQuery = ParseQuery.getQuery("Location");
		locationQuery.fromLocalDatastore();
		if(locationParseId != null) {
			location = locationQuery.get(locationParseId);
		}
		else if(locationUuid != null){
			locationQuery.whereEqualTo("uuid", locationUuid);
			location = locationQuery.getFirst();
		}

		if(location != null) {
			//ParseObject scribeRequestLocation = scribeRequest.getParseObject("location");
			//scribeRequestLocation.fetchFromLocalDatastore();
			//scribeRequestLocation.put("title", location.getString("title"));
			String newLocationString = location.getString("title");
			action.put("newLocationString", newLocationString);
		}
		
		
		ParseObject subject=null;
		ParseQuery<ParseObject> subjectQuery = ParseQuery.getQuery("Subject");
		subjectQuery.fromLocalDatastore();
		if(subjectParseId != null) {
			subject = subjectQuery.get(subjectParseId);
		}
		else if(subjectUuid != null){
			subjectQuery.whereEqualTo("uuid", subjectUuid);
			subject = subjectQuery.getFirst();
		}

		if(subject != null) {
			//ParseObject scribeRequestSubject = scribeRequest.getParseObject("subject");
			//scribeRequestSubject.fetchFromLocalDatastore();
			//scribeRequestSubject.put("title", location.getString("title"));
			String newSubjectString = subject.getString("title");
			action.put("newSubjectString", newSubjectString);
		}
		
		if(representeePhoneNumber != null && representeeFirstName != null && representeeLastName != null) {
			action.put("newRepresenteePhoneNumber", representeePhoneNumber);
			action.put("newRepresenteeFirstName", representeeFirstName);
			action.put("newRepresenteeLastName", representeeLastName);
		}

		action.put("uuid", UUID.randomUUID().toString());
    	return action;
    }
    
    private void updateScribeRequest() throws ParseException
    {
    	scribeRequest.put("dateTime", dateTime.getTime());
    	
    	ParseObject location=null;
		ParseQuery<ParseObject> locationQuery = ParseQuery.getQuery("Location");
		locationQuery.fromLocalDatastore();
		if(locationParseId != null) {
			location = locationQuery.get(locationParseId);
		}
		else if(locationUuid != null) {
			locationQuery.whereEqualTo("uuid", locationUuid);
			location = locationQuery.getFirst();
		}

		if(location != null) {
			String locationTitle = location.getString("title");
			ParseObject scribeRequestLocation = scribeRequest.getParseObject("location");
			scribeRequestLocation.fetchFromLocalDatastore();
			scribeRequestLocation.put("title", locationTitle);
			//scribeRequest.put("location", scribeRequestLocation);
		}
		
		ParseObject subject=null;
		ParseQuery<ParseObject> subjectQuery = ParseQuery.getQuery("Subject");
		subjectQuery.fromLocalDatastore();
		if(subjectParseId != null) {
			subject = subjectQuery.get(subjectParseId);
		}
		else if(subjectUuid != null) {
			subjectQuery.whereEqualTo("uuid", subjectUuid);
			subject = subjectQuery.getFirst();
		}
		
		if(subject != null) {
			String subjectTitle = subject.getString("title");
			ParseObject scribeRequestSubject = scribeRequest.getParseObject("subject");
			scribeRequestSubject.fetchFromLocalDatastore();
			scribeRequestSubject.put("title", subjectTitle);
			//scribeRequest.put("subject", scribeRequestSubject);
		}
		
		
		if(representeePhoneNumber != null && representeeFirstName != null && representeeLastName != null) {
			scribeRequest.put("representeePhoneNumber", representeePhoneNumber);
			scribeRequest.put("representeeFirstName", representeeFirstName);
			scribeRequest.put("representeeLastName", representeeLastName);
		}
		else {
			scribeRequest.remove("representeePhoneNumber");
			scribeRequest.remove("representeeFirstName");
			scribeRequest.remove("representeeLastName");
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
		String parseId=null;
		String uuid=null;

		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			parseId = extras.getString("parseId");
			uuid = extras.getString("uuid");
		}
		ParseQuery query = ParseQuery.getQuery("ScribeRequest");
		query.fromLocalDatastore();
		if(parseId != null) {
			try {
				scribeRequest = query.get(parseId);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(uuid != null) {
			query.whereEqualTo("uuid", uuid);
			try {
				scribeRequest = query.getFirst();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(scribeRequest != null) {
			setTitle("Edit details");
			try {
				setupPrivateVariables(scribeRequest);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String[] content = null;
		try {
			content = setupUIContent(scribeRequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
				intent = new Intent(RecordEditActivity.this, SelectSubjectActivity.class);
				startActivityForResult(intent, position);
				return;
			case 3:
				bundle = new Bundle();
		        bundle.putString("phoneNumber", representeePhoneNumber);
		        bundle.putString("firstName", representeeFirstName);
		        bundle.putString("lastName", representeeLastName);
		        //Exam exam = new Exam(123, "abc");
		        //exam.setParseId("12345");
		        //bundle.putParcelable("parcelable", exam);
				
				intent = new Intent(RecordEditActivity.this, RepresenteeEditActivity.class);
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
					subjectUuid = extras.getString("uuid");
					subjectParseId = extras.getString("parseId");
					adapter.remove(adapter.getItem(2));
					adapter.insert(extras.getString("title"), 2);
					adapter.notifyDataSetChanged();
					return;
				case 3:
					adapter.remove(adapter.getItem(3));
					
					String notes;
					if(extras.getString("phoneNumber") != null &&
							extras.getString("firstName") != null && 
								extras.getString("lastName") != null) {
						representeePhoneNumber = extras.getString("phoneNumber");
						representeeFirstName = extras.getString("firstName");
						representeeLastName = extras.getString("lastName");
						
						notes = representeeFirstName + " " + representeeLastName;
						adapter.insert(notes, 3);
					}
					else {
						representeePhoneNumber = null;
						representeeFirstName = null;
						representeeLastName = null;
						adapter.insert("No", 3);
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
	        	try {
	        		savePressed();
	        	} catch (ParseException e) {
	        		// TODO Auto-generated catch block
	        		e.printStackTrace();
	        	}
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
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
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
