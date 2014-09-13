package com.adarshhasija.blindlinks;

import com.example.ngotransactionrecords.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	
	private ProgressBar progressBar;
	private TextView progressBarLbl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
		progressBar.setVisibility(View.INVISIBLE);
		
		progressBarLbl = (TextView) findViewById(R.id.loggingInLbl);
		progressBarLbl.setVisibility(View.INVISIBLE);
		
		Button loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText emailWidget = (EditText) findViewById(R.id.email);
				EditText passwordWidget = (EditText) findViewById(R.id.password);
				
				String email = emailWidget.getText().toString();
				String password = passwordWidget.getText().toString();
				
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Login.this.CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() == null) {
					Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
					Toast.makeText(Login.this, "Email address is invalid", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(password.isEmpty()) {
					Toast.makeText(Login.this, "You have not entered a password", Toast.LENGTH_SHORT).show();
					return;
				}
				
				progressBar.setVisibility(View.VISIBLE);
				progressBarLbl.setVisibility(View.VISIBLE);
				
				ParseUser.logInInBackground(email, password, new LogInCallback() {

					@Override
					public void done(ParseUser user, ParseException e) {
						progressBar.setVisibility(View.INVISIBLE);
						progressBarLbl.setVisibility(View.INVISIBLE);
						
						if (user != null) {
								Intent mainIntent = new Intent(Login.this, RecordListActivity.class);
								startActivity(mainIntent);
						    } else {
						    	Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						    }
						
					}});
			}
		});
		
		TextView signUp = (TextView) findViewById(R.id.signUpLbl);
		signUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent signupIntent = new Intent(Login.this, Signup.class);
				startActivity(signupIntent);
				
			}
		});
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
	

	
}
