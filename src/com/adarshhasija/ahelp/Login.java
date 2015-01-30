package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.adarshhasija.ahelp.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	
	private boolean shouldSignupBeVisible = true;
	private MenuItem progressButton;
	private MenuItem loginButton;
	private MenuItem signupButton;
	
	
	/*
	 * Parse callbacks
	 * 
	 * 
	 */
	private FindCallback<ParseUser> findCallback = new FindCallback<ParseUser>() {

		@Override
		public void done(List<ParseUser> list, ParseException e) {
			if (e == null) {
		        saveContacts(list);
		    } else {
		    	progressButton.setVisible(false);
		    	if(shouldSignupBeVisible == true) signupButton.setVisible(true);
		    	Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		    }
		}
	};
	
	private LogInCallback logInCallback = new LogInCallback() {

		@Override
		public void done(ParseUser user, ParseException e) {
			progressButton.setVisible(false);
			if(shouldSignupBeVisible) {
				signupButton.setVisible(true);
			} 
			
			if (user != null) {
				ParseInstallation installation = ParseInstallation.getCurrentInstallation();
				installation.put("phoneNumber", user.getString("phoneNumber"));
				installation.saveInBackground(); 
				
				populateContactsList();
				
				//Intent mainIntent = new Intent(Login.this, MainListActivity.class);
				//startActivity(mainIntent);
		    } else {
		    	Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		    	progressButton.setVisible(false);
		    	if(shouldSignupBeVisible == true) signupButton.setVisible(true);
		    }
		}
		
	};
	private View.OnClickListener loginCickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			hideSoftKeyboard();
			
			EditText phoneNumberWidget = (EditText) findViewById(R.id.phone_number);
			EditText passwordWidget = (EditText) findViewById(R.id.password);
			
			String phoneNumber = phoneNumberWidget.getText().toString();
			String password = passwordWidget.getText().toString();
			
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Login.this.CONNECTIVITY_SERVICE);
			if(cm.getActiveNetworkInfo() == null) {
				Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
				return;
			}
			
		/*	if(!email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
				Toast.makeText(Login.this, "Email address is invalid", Toast.LENGTH_SHORT).show();
				return false;
			} */
			if(phoneNumber.length() < 1) {
				Toast.makeText(Login.this, "You have not entered a phone number", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(password.isEmpty()) {
				Toast.makeText(Login.this, "You have not entered a password", Toast.LENGTH_SHORT).show();
				return;
			}
			
			//we should just be passing the local number on
			if(phoneNumber.startsWith("0")) {
				phoneNumber = phoneNumber.substring(1);
			}
			if(phoneNumber.startsWith("+91")) {
				phoneNumber = phoneNumber.substring(3);
			}
			
			signupButton.setVisible(false);
			progressButton.setActionView(R.layout.action_progressbar);
            progressButton.expandActionView();
			progressButton.setVisible(true);
			
			ParseUser.logInInBackground(phoneNumber, password, logInCallback); 
			
			return;
		}
	};

	/*
	 * private functions
	 * 
	 */
	public void hideSoftKeyboard() {
	    if(getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}
	
	private void populateContactsList() {
		ParseQuery<ParseUser> queryUsers = ParseUser.getQuery();
		queryUsers.findInBackground(findCallback);
	}
	
	private void openApp() {
		Intent mainIntent = new Intent(Login.this, MainListActivity.class);
		startActivity(mainIntent);
	}
	
	private void saveContacts(List<ParseUser> list) {
		final List<ParseUser> userObjects = new ArrayList<ParseUser>();
		
		MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
        HashMap<String, String> localContacts = mainApplication.getUpdatedDeviceContactsList();
        String phoneNumber;
        String phoneNumberWithZero;
        String phoneNumberNoPrefix;
        for(ParseUser user : list) {
        	//This covers all the combination for an India number
        	phoneNumber = user.getString("phoneNumber");
        	phoneNumberWithZero = "0" + phoneNumber.substring(3);
        	phoneNumberNoPrefix = phoneNumber.substring(3);
        	
        	if(localContacts.containsKey(phoneNumber) || 
        			localContacts.containsKey(phoneNumberWithZero) ||
        				localContacts.containsKey(phoneNumberNoPrefix)) {
        		userObjects.add(user); //This is needed later when saving
        	}
        }
        
        ParseUser.unpinAllInBackground(new DeleteCallback() {

			@Override
			public void done(ParseException e) {
				if(e == null) {
					ParseUser.pinAllInBackground(userObjects);
					openApp();
				}
			}
        	
        });
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		Button loginButton = (Button) findViewById(R.id.login);
		loginButton.setOnClickListener(loginCickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser != null) {
			populateContactsList();
			Intent mainAppIntent = new Intent(this, MainListActivity.class);
			startActivity(mainAppIntent);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.login, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
	/*	loginButton = (MenuItem)menu.findItem(R.id.login);
		loginButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				EditText emailWidget = (EditText) findViewById(R.id.email);
				EditText passwordWidget = (EditText) findViewById(R.id.password);
				
				String email = emailWidget.getText().toString();
				String password = passwordWidget.getText().toString();
				
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Login.this.CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() == null) {
					Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(!email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
					Toast.makeText(Login.this, "Email address is invalid", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(password.isEmpty()) {
					Toast.makeText(Login.this, "You have not entered a password", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				loginButton.setVisible(false);
				signupButton.setVisible(false);
				progressButton.setActionView(R.layout.action_progressbar);
	            progressButton.expandActionView();
				progressButton.setVisible(true);
				
				ParseUser.logInInBackground(email, password, logInCallback);
				
				return false;
			}
			
		});	*/
		
		signupButton = (MenuItem)menu.findItem(R.id.signup);
		signupButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Intent mainIntent = new Intent(Login.this, Signup.class);
				startActivity(mainIntent);
				
				return false;
			}
			
		});
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		String phoneNumber = installation.getString("phoneNumber");
		if(phoneNumber != null) {
			signupButton.setVisible(false);
			shouldSignupBeVisible = false;
		}
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
	

	
}
