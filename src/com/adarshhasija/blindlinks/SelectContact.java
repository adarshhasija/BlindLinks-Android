package com.adarshhasija.blindlinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class SelectContact extends ListActivity {
	
	private ArrayList<ParseUser> userObjects;
	private ArrayList<String> userList;
	
	/*
	 * Private functions
	 * 
	 * 
	 */
	private void populateList() {
	/*	MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
        HashMap<String, String> localContacts = mainApplication.getUpdatedDeviceContactsList();
        for(ParseUser user : list) {
        	if(localContacts.containsKey(user.getString("phoneNumber"))) {
        		userObjects.add(user); //This is needed later when saving
        		userList.add(user.getString("firstName")+" "+user.getString("lastName"));
        	}
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, userList);
        setLisAdapter(adapter); */
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}

}
