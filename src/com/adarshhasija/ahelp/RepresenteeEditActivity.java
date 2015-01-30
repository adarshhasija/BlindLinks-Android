package com.adarshhasija.ahelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class RepresenteeEditActivity extends Activity {
	
	private MenuItem acceptButton;
	Switch switchBtn;
	
	String phoneNumber=null;
	String firstName=null;
	String lastName=null;
	
	
	/*
	 * 
	 * Action bar button functions
	 * 
	 */
	private void acceptPressed() {
		if(validate()) {
			TextView phoneNumberView = (TextView) findViewById(R.id.phone_number);
			TextView firstNameView = (TextView) findViewById(R.id.first_name);
			TextView lastNameView = (TextView) findViewById(R.id.last_name);
			
			String phoneNumber = phoneNumberView.getText().toString();
			String firstName = firstNameView.getText().toString();
			String lastName = lastNameView.getText().toString();
			
			Bundle bundle = new Bundle();
			if(!switchBtn.isChecked()) {
				bundle.putString("phoneNumber", null);
				bundle.putString("firstName", null);
				bundle.putString("lastName", null);
			}
			else {
				bundle.putString("phoneNumber", phoneNumber);
				bundle.putString("firstName", firstName);
				bundle.putString("lastName", lastName);
			}
			Intent returnIntent = new Intent();
			returnIntent.putExtras(bundle);
			setResult(Activity.RESULT_OK, returnIntent);
			finish();
		}
	}
	
	
	/*
	 * private functions
	 * 
	 * 
	 */
	public void hideSoftKeyboard() {
	    if(getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}
	
	private boolean validate() {
		if(!switchBtn.isChecked()) {
			return true;
		}
		TextView phoneNumberView = (TextView) findViewById(R.id.phone_number);
		TextView firstNameView = (TextView) findViewById(R.id.first_name);
		TextView lastNameView = (TextView) findViewById(R.id.last_name);
		
		phoneNumber = phoneNumberView.getText().toString();
		firstName = firstNameView.getText().toString();
		lastName = lastNameView.getText().toString();
		
		if(phoneNumber.length() < 1) {
			Toast.makeText(this, "You have not entered a phone number", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(firstName.isEmpty()) {
			Toast.makeText(this, "You have not entered a first name", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(lastName.isEmpty()) {
			Toast.makeText(this, "You have not entered a last name", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.representee_edit_activity);
		
		Bundle extras = getIntent().getExtras();
		phoneNumber = extras.getString("phoneNumber");
		firstName = extras.getString("firstName");
		lastName = extras.getString("lastName");
		
		TextView phoneNumberView = (TextView) findViewById(R.id.phone_number);
		TextView firstNameView = (TextView) findViewById(R.id.first_name);
		TextView lastNameView = (TextView) findViewById(R.id.last_name);
		
		if(phoneNumber != null) {
			phoneNumberView.setText(phoneNumber);
		}
		if(firstName != null) {
			firstNameView.setText(firstName);
		}
		if(lastName != null) {
			lastNameView.setText(lastName);
		}
	}
	
	@Override
	public void onBackPressed() {
		acceptPressed();
		//super.onBackPressed();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.accept:
	        	hideSoftKeyboard();
	        	acceptPressed();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.representee_edit_activity, menu);
		
		//acceptButton = (MenuItem)menu.findItem(R.id.accept);
		
		MenuItem switchBtnItem = (MenuItem) menu.findItem(R.id.switchBtn);
		RelativeLayout rLayout = (RelativeLayout) switchBtnItem.getActionView();
		switchBtn = (Switch) rLayout.findViewById(R.id.switchForActionBar);
		switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean status) {
				if(status == false) {
					hideSoftKeyboard();
					((TextView) findViewById(R.id.phone_number))
					.setVisibility(View.GONE);
					((TextView) findViewById(R.id.first_name))
					.setVisibility(View.GONE);
					((TextView) findViewById(R.id.last_name))
					.setVisibility(View.GONE);
				}
				else {
					((TextView) findViewById(R.id.phone_number))
					.setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.first_name))
					.setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.last_name))
					.setVisibility(View.VISIBLE);
				}
			}
		});
		
		if(phoneNumber != null && firstName != null && lastName != null) {
			switchBtn.setChecked(true);
		}
		else {
			((TextView) findViewById(R.id.phone_number))
			.setVisibility(View.GONE);
			((TextView) findViewById(R.id.first_name))
			.setVisibility(View.GONE);
			((TextView) findViewById(R.id.last_name))
			.setVisibility(View.GONE);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	
}
