package com.adarshhasija.blindlinks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.example.ngotransactionrecords.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RecordEditActivity extends Activity {
	
	private ParseObject record=null;
	private ArrayList<String> categoryList;
	private MenuItem progressButton;
	private MenuItem saveButton;
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				progressButton.setVisible(false);
				saveButton.setVisible(true);
				if(record != null) {
					setTitle("Edit Record");
				}
				else {
					setTitle("New Record");
				}
				finish();
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit_activity);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
		            categoryList = new ArrayList<String>();
		            for(ParseObject obj : list) {
		            	categoryList.add(obj.getString("title"));	
		            }
		            AutoCompleteTextView categoryView = (AutoCompleteTextView) findViewById(R.id.category); 
		            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, categoryList);
		            categoryView.setAdapter(adapter);
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
				
			}
		});
		
		MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
		record = mainApplication.getSelectedRecord();
		
		if(record != null) {
			setTitle("Edit Record");
			
			TextView categoryView = ((TextView) findViewById(R.id.category));
			EditText rupeesView = ((EditText) findViewById(R.id.rupees));
			EditText paiseView = ((EditText) findViewById(R.id.paise));

			String categoryString = record.getString("category");
			Number amount = record.getNumber("amount");
			double amountDouble = amount.doubleValue();
			int rupees = amount.intValue();
			double paiseDouble = amountDouble - rupees;
			int paise = (int) (Math.round(paiseDouble*100));
			String rupeesString = Integer.toString(rupees);
			
			categoryView.setText(categoryString);
			rupeesView.setText(rupeesString);
			if(paise > 9) {
				String paiseString = Integer.toString(paise);
				paiseView.setText(paiseString);
			}
			else if(paise > 0) {
				String paiseString = Integer.toString(paise);
				paiseView.setText("0"+paiseString);
			}
			
			TextView additionalDetailsView = ((TextView) findViewById(R.id.additionalDetails));
			additionalDetailsView.setText(record.getString("additionalDetails"));
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.record_edit_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		saveButton = (MenuItem)menu.findItem(R.id.save);
		saveButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				EditText categoryWidget = (EditText) findViewById(R.id.category);
				EditText rupeesWidget = (EditText) findViewById(R.id.rupees);
				EditText paiseWidget = (EditText) findViewById(R.id.paise);
				EditText additionalDetailsWidget = (EditText) findViewById(R.id.additionalDetails);
				
				String categoryTemp = categoryWidget.getText().toString();
				final String category = categoryTemp.substring(0, 1).toUpperCase() + categoryTemp.substring(1);
				String rupees = rupeesWidget.getText().toString();
				String paise = paiseWidget.getText().toString();
				String additionalDetails = additionalDetailsWidget.getText().toString();
				
				ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() == null) {
					Toast.makeText(getBaseContext(), "No internet connection, cannot save", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(category.isEmpty()) {
					Toast.makeText(getBaseContext(), "You have not entered a description", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(rupees.isEmpty()) {
					Toast.makeText(getBaseContext(), "You have not entered an amount", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				if(paise.isEmpty()) {
					paise = "00";
				}
				
				String amountString = rupees + "." + paise;
				final float amount = Float.parseFloat(amountString);
				
				saveButton.setVisible(false);
				progressButton.setActionView(R.layout.action_progressbar);
	            progressButton.expandActionView();
				progressButton.setVisible(true);
				setTitle("Saving...");
				
				if(record == null) {
					record = new ParseObject("Record");
					record.put("user", ParseUser.getCurrentUser());
				}
				record.put("category", category);
				record.put("amount", amount);
				record.put("additionalDetails", additionalDetails);
				
				Intent returnIntent = new Intent();
				MainApplication mainApplication = (MainApplication) getApplicationContext();
				mainApplication.setModifiedRecord(record);
				Bundle bundle = new Bundle();
				bundle.putString("category", category);
				bundle.putFloat("amount", amount);
				bundle.putString("additionalDetails", additionalDetails);
				ParseProxyObject ppo = new ParseProxyObject(record);
				//returnIntent.putExtras(bundle);
				returnIntent.putExtra("parseObject", ppo);
				setResult(RESULT_OK,returnIntent);
				
				record.saveInBackground(saveCallback);
				
				if(!categoryList.contains(category)) {
					ParseObject newCategory = new ParseObject("Category");
					newCategory.put("user", ParseUser.getCurrentUser());
					newCategory.put("title", category);
					newCategory.saveInBackground(saveCallback);
				}
				
				return false;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	
	

}
