package com.adarshhasija.blindlinks;

import com.example.ngotransactionrecords.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Signup extends Activity {
	
	private ProgressBar progressBar;
	private TextView progressBarLbl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
		//for now starting balance UI is not needed
		TextView startingBalanceLbl = (TextView) findViewById(R.id.startingBalanceLbl);
		EditText rupeesWidget = (EditText) findViewById(R.id.rupees);
		EditText paiseWidget = (EditText) findViewById(R.id.paise);
		startingBalanceLbl.setVisibility(View.INVISIBLE);
		rupeesWidget.setVisibility(View.INVISIBLE);
		paiseWidget.setVisibility(View.INVISIBLE);
		
		progressBar = (ProgressBar) findViewById(R.id.signUpProgressBar);
		progressBar.setVisibility(View.INVISIBLE);
		
		progressBarLbl = (TextView) findViewById(R.id.signingUpLbl);
		progressBarLbl.setVisibility(View.INVISIBLE);
		
		Button signUpBtn = (Button) findViewById(R.id.signupBtn);
		signUpBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText emailWidget = (EditText) findViewById(R.id.email);
				EditText passwordWidget = (EditText) findViewById(R.id.password);
				EditText passwordConfirmWidget = (EditText) findViewById(R.id.password_confirm);
				EditText rupeesWidget = (EditText) findViewById(R.id.rupees);
				EditText paiseWidget = (EditText) findViewById(R.id.paise);
				
				String email = emailWidget.getText().toString();
				String password = passwordWidget.getText().toString();
				String passwordConfirm = passwordConfirmWidget.getText().toString();
				String rupees = rupeesWidget.getText().toString();
				String paise = paiseWidget.getText().toString();
				
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Signup.this.CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() == null) {
					Toast.makeText(Signup.this, "No internet connection", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
					Toast.makeText(Signup.this, "Email address is invalid", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!password.equals(passwordConfirm)) {
					Toast.makeText(Signup.this, "Password and password confirm do not match", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(rupees.isEmpty()) {
					Toast.makeText(Signup.this, "You have not entered a starting balance", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(paise.isEmpty()) {
					paise = "00";
				}
				
				String amountString = rupees + "." + paise;
				final float amount = Float.parseFloat(amountString);
				
				final ParseUser user = new ParseUser();
				user.setUsername(email);
				user.setPassword(password);
				user.setEmail(email);
				
				progressBar.setVisibility(View.VISIBLE);
				progressBarLbl.setVisibility(View.VISIBLE);
				 
				user.signUpInBackground(new SignUpCallback() {

					@Override
					public void done(ParseException e) {
						
						if (e == null) {
							progressBar.setVisibility(View.INVISIBLE);
							progressBarLbl.setVisibility(View.INVISIBLE);
							finish();
							/*	ParseObject startingBalance = new ParseObject("Record");
								startingBalance.put("user", user);
								startingBalance.put("amount", amount);
								startingBalance.put("description", "Starting balance");
								startingBalance.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException error) {
										if(error == null) {
											progressBar.setVisibility(View.INVISIBLE);
											progressBarLbl.setVisibility(View.INVISIBLE);
											finish();
										}
										else {
											Toast.makeText(Signup.this, error.getMessage(), Toast.LENGTH_SHORT).show();
										}
										
									}}); */
						    } else {
						      // Sign up didn't succeed. Look at the ParseException
						      // to figure out what went wrong
						    	Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						    }
					}});
				
			}
		});
	}

}
