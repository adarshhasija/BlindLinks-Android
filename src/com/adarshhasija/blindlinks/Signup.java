package com.adarshhasija.blindlinks;

import java.util.Locale;

import com.adarshhasija.blindlinks.R;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Signup extends Activity {
	
	private MenuItem progressButton;
	private MenuItem signupButton;
	private SignUpCallback signUpCallback = new SignUpCallback() {

		@Override
		public void done(ParseException e) {
			progressButton.setVisible(false);
			signupButton.setVisible(true);
			if (e == null) {
				//Create a new installation object for push notifications
				EditText emailWidget = (EditText) findViewById(R.id.email);
				String email = emailWidget.getText().toString();
				ParseInstallation installation = ParseInstallation.getCurrentInstallation();
				installation.put("email", email);
				installation.saveInBackground();
				finish();
			 } else {
			    	Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			 }
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.signup, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		signupButton = (MenuItem)menu.findItem(R.id.signup);
		signupButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				EditText emailWidget = (EditText) findViewById(R.id.email);
				EditText passwordWidget = (EditText) findViewById(R.id.password);
				EditText passwordConfirmWidget = (EditText) findViewById(R.id.password_confirm);
				EditText firstNameWidget = (EditText) findViewById(R.id.first_name);
				EditText lastNameWidget = (EditText) findViewById(R.id.last_name);
				
				String email = emailWidget.getText().toString();
				String password = passwordWidget.getText().toString();
				String passwordConfirm = passwordConfirmWidget.getText().toString();
				String firstName = firstNameWidget.getText().toString();
				String lastName = lastNameWidget.getText().toString();
				
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Signup.this.CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() == null) {
					Toast.makeText(Signup.this, "No internet connection", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(!email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
					Toast.makeText(Signup.this, "Email address is invalid", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(!password.equals(passwordConfirm)) {
					Toast.makeText(Signup.this, "Password and password confirm do not match", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(firstName.isEmpty()) {
					Toast.makeText(Signup.this, "You have not entered a first name", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(lastName.isEmpty()) {
					Toast.makeText(Signup.this, "You have not entered a last name", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				firstName = firstName.substring(0, 1).toUpperCase(Locale.US) + firstName.substring(1);
				lastName = lastName.substring(0, 1).toUpperCase(Locale.US) + lastName.substring(1);
				
				signupButton.setVisible(false);
				progressButton.setActionView(R.layout.action_progressbar);
	            progressButton.expandActionView();
				progressButton.setVisible(true);
				
				final ParseUser user = new ParseUser();
				user.setUsername(email);
				user.setPassword(password);
				user.setEmail(email);
				user.put("firstName", firstName);
				user.put("lastName", lastName);
				user.signUpInBackground(signUpCallback);
				
				return false;
			}
			
		});
		
		return super.onCreateOptionsMenu(menu);
	}

}
