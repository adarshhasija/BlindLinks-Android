package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.HashMap;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class MainApplication extends Application{


	private ParseObject parseObject=null;
	private ParseObject selectedRecord=null; //This is to move selected objects forward from the main list as PO is not parcelable
	private ParseObject modifiedRecord=null; //This is to move modified objects back to the main list as PO is not parcelable
	private ParseUser userForNewRecord=null; //This is to pass a contact forward for creating a new record
	
	/*
	 * Helper functions
	 * 
	 * 
	 */
	public HashMap<String, String> getUpdatedDeviceContactsList() {
		HashMap<String, String> localContacts=new HashMap<String, String>();
		
		Cursor data = getContentResolver()
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}, null, null,  Phone.DISPLAY_NAME + " ASC");
		
		int i=0;
		data.moveToFirst();
		while (data.isAfterLast() == false) 
		{
			//String number = data.getString(data.getColumnIndex(Phone.NUMBER));
			int numberIndex = data.getColumnIndex(Phone.NUMBER);
			int nameIndex = data.getColumnIndex(Phone.DISPLAY_NAME);
			String number = data.getString(numberIndex).replaceAll("\\s+","");
			String name = data.getString(nameIndex);
		    if(!localContacts.containsKey(number)) {  
		    	localContacts.put(number, name);
		    }
		    i++;
		    data.moveToNext();
		}
		data.close();
		
		return localContacts;
	}
	
	public ArrayList getUpdatedDeviceContactsListAsArray() {
		ArrayList<String> localContacts=new ArrayList<String>();
		
		Cursor data = getContentResolver()
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}, null, null,  Phone.DISPLAY_NAME + " ASC");
		
		int i=0;
		data.moveToFirst();
		while (data.isAfterLast() == false) 
		{
			//String number = data.getString(data.getColumnIndex(Phone.NUMBER));
			int numberIndex = data.getColumnIndex(Phone.NUMBER);
			int nameIndex = data.getColumnIndex(Phone.DISPLAY_NAME);
			String number = data.getString(numberIndex).replaceAll("\\s+","");
			String name = data.getString(nameIndex);
		    if(!localContacts.contains(number)) {  
		    	localContacts.add(number);
		    }
		    i++;
		    data.moveToNext();
		}
		data.close();
		
		return localContacts;
	}
		
	
/*	
	static boolean validEmail(String email) {
	    // editing to make requirements listed
	    // return email.matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}");
	    return email.matches("[A-Z0-9._%+-][A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{3}");
	}
*/
	
	
	/*
	 * 
	 * Getters and setters
	 * 
	 */
	public ParseObject getParseObject() {
		return parseObject;
	}
	public void setParseObject(ParseObject parseObject) {
		this.parseObject = parseObject;
	}
	
	public ParseObject getSelectedRecord() {
		return selectedRecord;
	}

	public void setSelectedRecord(ParseObject selectedRecord) {
		this.selectedRecord = selectedRecord;
	}


	public ParseObject getModifiedRecord() {
		return modifiedRecord;
	}

	public void setModifiedRecord(ParseObject modifiedRecord) {
		this.modifiedRecord = modifiedRecord;
	}
	
	public ParseUser getUserForNewRecord() {
		return userForNewRecord;
	}

	public void setUserForNewRecord(ParseUser userForNewRecord) {
		this.userForNewRecord = userForNewRecord;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Parse.enableLocalDatastore(getApplicationContext());
		Parse.initialize(this, "9f3p730Ynsj9hLbeEuGxGC9Nifwmh5Co0NCAsbi5", "MogP0et66o0SlSgS2XXFNJhVqnlvICy3L3don29q");
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);

		PushService.setDefaultPushCallback(this, RecordListActivity.class);
	}
	
}
