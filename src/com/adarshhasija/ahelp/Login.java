package com.adarshhasija.ahelp;

import com.adarshhasija.ahelp.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
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
	private LogInCallback logInCallback = new LogInCallback() {

		@Override
		public void done(ParseUser user, ParseException e) {
			progressButton.setVisible(false);
			if(shouldSignupBeVisible) {
				signupButton.setVisible(true);
			}
			
			ParseInstallation installation = ParseInstallation.getCurrentInstallation();
			installation.put("phoneNumber", user.getString("phoneNumber"));
			installation.saveInBackground(); 
			
			if (user != null) {
				Intent mainIntent = new Intent(Login.this, RecordListActivity.class);
				startActivity(mainIntent);
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
			
			//loginButton.setVisible(false);
			signupButton.setVisible(false);
			progressButton.setActionView(R.layout.action_progressbar);
            progressButton.expandActionView();
			progressButton.setVisible(true);
			
			ParseUser.logInInBackground(phoneNumber, password, logInCallback);
			
			return;
		}
	};

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
			Intent mainAppIntent = new Intent(this, RecordListActivity.class);
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
