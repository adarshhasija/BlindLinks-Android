package com.adarshhasija.blindlinks;

import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MainApplication extends Application{

	private ParseObject selectedRecord=null; //This is to move selected objects forward as PO is not parcelable
	private ParseObject modifiedRecord=null; //This is to move modified objects back as PO is not parcelable
	
	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "9f3p730Ynsj9hLbeEuGxGC9Nifwmh5Co0NCAsbi5", "MogP0et66o0SlSgS2XXFNJhVqnlvICy3L3don29q");
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
	
	
	
/*	
	static boolean validEmail(String email) {
	    // editing to make requirements listed
	    // return email.matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}");
	    return email.matches("[A-Z0-9._%+-][A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{3}");
	}
*/
}
