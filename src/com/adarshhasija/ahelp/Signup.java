package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.adarshhasija.ahelp.R;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Signup extends AccountAuthenticatorActivity {
	
	private Map<String, String> isoMap = new HashMap<String, String>();
	private MenuItem progressButton;
	private MenuItem signupButton;
	private SignUpCallback signUpCallback = new SignUpCallback() {

		@Override
		public void done(ParseException e) {
			progressButton.setVisible(false);
			if (e == null) {
				EditText phoneNumberWidget = (EditText) findViewById(R.id.phone_number);
				String phoneNumber = phoneNumberWidget.getText().toString();
				EditText firstNameWidget = (EditText) findViewById(R.id.first_name);
				EditText lastNameWidget = (EditText) findViewById(R.id.last_name);
				String firstName = firstNameWidget.getText().toString();
				String lastName = lastNameWidget.getText().toString();
				firstName = firstName.substring(0, 1).toUpperCase(Locale.US) + firstName.substring(1);
				lastName = lastName.substring(0, 1).toUpperCase(Locale.US) + lastName.substring(1);
				
			/*	//Create a local account
				Account account = new Account(phoneNumber, "com.adarshhasija.blindlinks");  
				AccountManager mAccountManager = AccountManager.get(Signup.this);
				Bundle userdata = new Bundle();
				userdata.putString("name", firstName + " " + lastName);
				if (mAccountManager.addAccountExplicitly(account, null, userdata)) {
			    	Bundle result = new Bundle();
			        result.putString(AccountManager.KEY_ACCOUNT_NAME, "Blind Links");
			        result.putString(AccountManager.KEY_ACCOUNT_TYPE, "com.adarshhasija.blindlinks");
			        result.putString("someKey", "stringData");
			        setAccountAuthenticatorResult(result);
			    } */
				
				finish();
			 } else {
			    	Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			    	progressButton.setVisible(false);
			 }
		}
		
	};
	private View.OnClickListener signupCickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			hideSoftKeyboard();
			
			Spinner countriesSpinner = (Spinner) findViewById(R.id.countries_spinner);
			EditText phoneNumberWidget = (EditText) findViewById(R.id.phone_number);
			EditText passwordWidget = (EditText) findViewById(R.id.password);
			EditText passwordConfirmWidget = (EditText) findViewById(R.id.password_confirm);
			EditText firstNameWidget = (EditText) findViewById(R.id.first_name);
			EditText lastNameWidget = (EditText) findViewById(R.id.last_name);
			
			String phoneNumber = phoneNumberWidget.getText().toString();
			String userName = phoneNumber; //the username will be the phone number without the country code
			String password = passwordWidget.getText().toString();
			String passwordConfirm = passwordConfirmWidget.getText().toString();
			String firstName = firstNameWidget.getText().toString();
			String lastName = lastNameWidget.getText().toString();
			
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Signup.this.CONNECTIVITY_SERVICE);
			if(cm.getActiveNetworkInfo() == null) {
				Toast.makeText(Signup.this, "No internet connection", Toast.LENGTH_SHORT).show();
				return;
			}
			
		/*	if(!email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
				Toast.makeText(Signup.this, "Email address is invalid", Toast.LENGTH_SHORT).show();
				return false;
			}	*/
			if(phoneNumber.length() < 1) {
				Toast.makeText(Signup.this, "You have not entered a phone number", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(!password.equals(passwordConfirm)) {
				Toast.makeText(Signup.this, "Password and password confirm do not match", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(firstName.isEmpty()) {
				Toast.makeText(Signup.this, "You have not entered a first name", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(lastName.isEmpty()) {
				Toast.makeText(Signup.this, "You have not entered a last name", Toast.LENGTH_SHORT).show();
				return;
			}
			
			firstName = firstName.substring(0, 1).toUpperCase(Locale.US) + firstName.substring(1);
			lastName = lastName.substring(0, 1).toUpperCase(Locale.US) + lastName.substring(1);
			phoneNumber = phoneNumber.replaceAll("[^\\d+]", "");
			String countryCode = "+";
			if(phoneNumber.startsWith("0")) {
				phoneNumber = phoneNumber.substring(1);
			}
			if(phoneNumber.indexOf("+", 0) == -1) {
				String country = countriesSpinner.getSelectedItem().toString();
				String countryISO = isoMap.get(country);
				countryCode = Iso2Phone.getPhone(countryISO);
				phoneNumber = countryCode + phoneNumber;
			}

			progressButton.setActionView(R.layout.action_progressbar);
            progressButton.expandActionView();
			progressButton.setVisible(true);
			
			//Create a new installation object for push notifications
			ParseInstallation installation = ParseInstallation.getCurrentInstallation();
			installation.put("phoneNumber", phoneNumber);
			installation.saveInBackground();
			
			final ParseUser user = new ParseUser();
			user.setUsername(userName);
			user.setPassword(password);
			user.put("phoneNumber", phoneNumber);
			user.put("countryCode", countryCode);
			user.put("firstName", firstName);
			user.put("lastName", lastName);
			user.signUpInBackground(signUpCallback);
			
			return;
		}
	};
	
	/**
	 * Hides the soft keyboard
	 */
	public void hideSoftKeyboard() {
	    if(getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
		Spinner spinner = (Spinner) findViewById(R.id.countries_spinner);	
		String[] isoCountries = Locale.getISOCountries();
		ArrayList<String> countries = new ArrayList<String>();
		for(String countryISO : isoCountries) {
			Locale locale = new Locale("en", countryISO);
            String name = locale.getDisplayCountry();
            countries.add(name);
            isoMap.put(name, countryISO);
		}
		Collections.sort(countries);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries);
		
		dataAdapter
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		Locale current = getResources().getConfiguration().locale;
		spinner.setSelection(countries.indexOf(current.getDisplayCountry()));
		
		Button signupButton = (Button) findViewById(R.id.signup);
		signupButton.setOnClickListener(signupCickListener);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.signup, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
	/*	signupButton = (MenuItem)menu.findItem(R.id.signup);
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
			
		}); */
		
		return super.onCreateOptionsMenu(menu);
	}

}
