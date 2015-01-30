package com.adarshhasija.ahelp;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ContactDetailsActivity extends Activity {
	
	ParseUser user = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_details_activity);
		
		Bundle extras = getIntent().getExtras();
		String parseId = extras.getString("parseId");
		String uuid = extras.getString("uuid");
		
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.fromLocalDatastore();
		try {
			if(parseId != null) {
				user = query.get(parseId);
			}
			else if(uuid != null){
				query.whereEqualTo("uuid", uuid);
				user = query.getFirst();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String phoneNumber = null;
		if(user != null) {
			setTitle(user.getString("firstName") + " " + user.getString("lastName"));
			phoneNumber = user.getString("phoneNumber");
		}
		TextView phoneNumberView = (TextView) findViewById(R.id.phoneNumber);
		if(phoneNumber != null) {
			phoneNumberView.setText(phoneNumber);
		}
	}
	
	

}
