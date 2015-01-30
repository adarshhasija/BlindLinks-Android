package com.adarshhasija.ahelp;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ExamChangesActivity extends Activity {
	
	private String parseId=null;
	private String uuid=null;
	
	/*
	 * private functions
	 * 
	 * 
	 */
	private ParseObject getActionObject() {
		ParseObject parseObject=null;
		ParseQuery query = ParseQuery.getQuery("Action");
		query.fromLocalDatastore();
		try {
			if(parseId != null) {
				parseObject = query.get(parseId);
			}
			else if(uuid != null) {
				query.whereEqualTo("uuid", uuid);
				parseObject = query.getFirst();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parseObject;
	}
	
	private String dateTimeFormatted(Calendar c) {
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if(hour > 12) hour = hour - 12;
		else if(hour == 0) hour = 12;
		int minute = c.get(Calendar.MINUTE);
		String minuteString = (minute < 10)?"0"+Integer.toString(minute):Integer.toString(minute);
		String dateTimeString = c.get(Calendar.DATE) + " " + 
				c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " +
				c.get(Calendar.YEAR) + " " +
				hour + ":" +
				minuteString + " " +
				c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
		
		return dateTimeString;
	}
	
	
	private void applyDateTimeChanges(ParseObject parseObject)
	{
		Date oldDate = parseObject.getDate("oldDate");
		Calendar oldCalendar = Calendar.getInstance();
		oldCalendar.setTime(oldDate);
		Date newDate = parseObject.getDate("newDate");
		Calendar newCalendar = Calendar.getInstance();
		newCalendar.setTime(newDate);
		
		TextView dateView = (TextView) findViewById(R.id.dateTime);
		if(oldCalendar.getTimeInMillis() != newCalendar.getTimeInMillis()) {
			String oldDateText = dateTimeFormatted(oldCalendar);
			String newDateText = dateTimeFormatted(newCalendar);
			dateView.setText("DATE\nFROM "+oldDateText+"\nTO "+newDateText);
			dateView.setContentDescription("DATE FROM "+oldDateText+" TO "+newDateText);
		}
		else {
			dateView.setVisibility(View.GONE);
		}
	}
	
	private void applyLocationChanges(ParseObject parseObject) throws ParseException
	{
	/*	ParseObject oldLocation = parseObject.getParseObject("oldLocation");
		ParseObject newLocation = parseObject.getParseObject("newLocation");
		oldLocation.fetchFromLocalDatastore();
		newLocation.fetchFromLocalDatastore();	*/
		String oldLocation = parseObject.getString("oldLocationString");
		String newLocation = parseObject.getString("newLocationString");
		
		TextView locationView = (TextView) findViewById(R.id.location);
		if(!oldLocation.equals(newLocation)) {
			String string = "LOCATION\nFROM " + oldLocation + "\nTO " +
									newLocation;
			String contentDescription = "LOCATION FROM " + oldLocation + " TO " +
					newLocation;
			locationView.setText(string);
			locationView.setContentDescription(contentDescription);
		}
		else {
			locationView.setVisibility(View.GONE);
		}
	}
	
	private void applySubjectChanges(ParseObject parseObject) throws ParseException
	{
	/*	ParseObject oldSubject = parseObject.getParseObject("oldSubject");
		ParseObject newSubject = parseObject.getParseObject("newSubject");
		oldSubject.fetchFromLocalDatastore();
		newSubject.fetchFromLocalDatastore();	*/
		String oldSubject = parseObject.getString("oldSubjectString");
		String newSubject = parseObject.getString("newSubjectString");
		
		TextView subjectView = (TextView) findViewById(R.id.subject);
		if(!oldSubject.equals(newSubject)) {
			String string = "SUBJECT\nFROM " + oldSubject + "\nTO " +
									newSubject;
			String contentDescription = "SUBJECT FROM " + oldSubject + " TO " +
					newSubject;
			subjectView.setText(string);
			subjectView.setContentDescription(contentDescription);
		}
		else {
			subjectView.setVisibility(View.GONE);
		}
	}
	
	private void applyRepresenteeChanges(ParseObject parseObject)
	{
		String oldRepresenteePhoneNumber = parseObject.getString("oldRepresenteePhoneNumber");
		String oldRepresenteeFirstName = parseObject.getString("oldRepresenteeFirstName");
		String oldRepresenteeLastName = parseObject.getString("oldRepresenteeLastName");
		String newRepresenteePhoneNumber = parseObject.getString("newRepresenteePhoneNumber");
		String newRepresenteeFirstName = parseObject.getString("newRepresenteeFirstName");
		String newRepresenteeLastName = parseObject.getString("newRepresenteeLastName");
		
		TextView notesView = (TextView) findViewById(R.id.notes);
		if(oldRepresenteePhoneNumber == null && newRepresenteePhoneNumber == null) {
			notesView.setVisibility(View.GONE);
		}
		else if(oldRepresenteePhoneNumber == null && newRepresenteePhoneNumber != null) {
			notesView.setText("DETAILS OF WHO IS BEING REPRESENTED HAVE BEEN ADDED:\n" + 
								newRepresenteeFirstName + " " + newRepresenteeLastName +
									"\n" + newRepresenteePhoneNumber);
			notesView.setContentDescription("DETAILS ON WHO IS BEING REPRESENTED HAVE BEEN ADDED: " + 
												newRepresenteeFirstName + " " + newRepresenteeLastName +
													" " + newRepresenteePhoneNumber);
		}
		else if(oldRepresenteePhoneNumber != null && newRepresenteePhoneNumber == null) {
			notesView.setText("DETAILS OF WHO IS BEING REPRESENTED HAVE BEEN REMOVED");
			notesView.setContentDescription("DETAILS OF WHO IS BEING REPRESENTED HAVE BEEN REMOVED");
		}
		else if(!oldRepresenteePhoneNumber.equals(newRepresenteePhoneNumber) ||
				!oldRepresenteeFirstName.equals(newRepresenteeFirstName) || 
					!oldRepresenteeLastName.equals(newRepresenteeLastName)) {
			notesView.setText("DETAILS OF WHO IS BEING REPRESENTED HAVE CHANGED:\n" +
								"FROM " + oldRepresenteeFirstName + " " + oldRepresenteeLastName + ", " + oldRepresenteePhoneNumber +
									"\nTO " + newRepresenteeFirstName + " " + newRepresenteeLastName + ", " + newRepresenteePhoneNumber);
			notesView.setContentDescription("DETAILS OF WHO IS BEING REPRESENTED HAVE CHANGED: " +
					"FROM " + oldRepresenteeFirstName + " " + oldRepresenteeLastName + ", " + oldRepresenteePhoneNumber +
					" TO " + newRepresenteeFirstName + " " + newRepresenteeLastName + ", " + newRepresenteePhoneNumber);
		}
		else {
			notesView.setVisibility(View.GONE);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.exam_changes_activity);
		
		Bundle extras = getIntent().getExtras();
		parseId = extras.getString("parseId");
		uuid = extras.getString("uuid");
		ParseObject parseObject = getActionObject();
		
		try {
			applyDateTimeChanges(parseObject);
			
			if(parseObject.getString("newLocationString") != null) {
				applyLocationChanges(parseObject);
			}
			if(parseObject.getString("newSubjectString") != null) {
				applySubjectChanges(parseObject);
			}
			applyRepresenteeChanges(parseObject);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
