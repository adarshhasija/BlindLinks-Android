package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SubjectEditActivity extends Activity {
	
	//private variables
	private ParseObject parseObject=null;
	
	//MenuItems
	private MenuItem progressButton;
	private MenuItem saveButton;
	private MenuItem deleteButton;
	

	/*
	 * Parse callbacks
	 * 
	 * 
	 */
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
				toggleProgressButton();
				if (e == null) {
						Intent returnIntent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("parseId", parseObject.getObjectId());
						bundle.putString("uuid", parseObject.getString("uuid"));
						EditText view = (EditText) findViewById(R.id.title);
						bundle.putString("title", view.getText().toString());
						returnIntent.putExtras(bundle);
						setResult(Activity.RESULT_OK, returnIntent);
						finish();
			       }
			       else {
			    	   Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			       }
		}
		
	};
	
	private DeleteCallback deleteCallback = new DeleteCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				finish();
			}
			else {
				toggleProgressButton();
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	
	/**
	 * private functions
	 */
	public void hideSoftKeyboard() {
	    if(getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}
	
	private boolean validate() {
		EditText view = (EditText) findViewById(R.id.title);
		final String title = view.getText().toString();
		
		if(title.isEmpty()) {
			Toast.makeText(getBaseContext(), "You have not entered a subject", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private void savePressed() {
		if(validate()) {
			toggleProgressButton();
			
			EditText view = (EditText) findViewById(R.id.title);
			final String title = view.getText().toString();
		
			if(parseObject != null) {
				parseObject.put("title", title);
				parseObject.saveEventually();
			}
			else {
				parseObject = new ParseObject("Subject");
				parseObject.put("title", title);
				parseObject.put("createdBy", ParseUser.getCurrentUser());
				ParseACL acl = new ParseACL();
				acl.setPublicReadAccess(true);
				acl.setPublicWriteAccess(true);
				parseObject.setACL(acl);
				parseObject.saveEventually();
				
				UUID uuid=null;
				if(parseObject.getString("uuid") == null) {
					uuid = UUID.randomUUID();
				}
				parseObject.put("uuid", uuid.toString());
			}

			parseObject.pinInBackground("Subject", saveCallback);
		}
	}
	
	private void deletePressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   setResult(Activity.RESULT_OK);
                	   if(parseObject != null) {
                		   parseObject.deleteEventually();
                		   parseObject.unpinInBackground("Subject", deleteCallback);
                	   }
                	   
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        builder.show();
	}
	
	/*
	 * private functions
	 */
	private void toggleProgressButton()
    {
    	if(!progressButton.isVisible())
    	{
    		saveButton.setVisible(false);
    		deleteButton.setVisible(false);
			progressButton.setActionView(R.layout.action_progressbar);
            progressButton.expandActionView();
			progressButton.setVisible(true);
    	}
    	else 
    	{
    		progressButton.setVisible(false);
			saveButton.setVisible(true);
			deleteButton.setVisible(true);
    	}
    }
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subject_edit_activity);
		
		Bundle extras = getIntent().getExtras();
		String title=null;
		String parseId = null;
		UUID uuid = null;
		if(extras != null) {
			parseId = extras.getString("parseId");
			if(extras.getString("uuid") != null) {
				uuid = UUID.fromString(extras.getString("uuid"));
			}
			title = extras.getString("title");
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Subject");
			query.fromLocalDatastore();
			try {
				if(parseId != null) {
					parseObject = query.get(parseId);
				}
				else if(uuid != null) {
					query.whereEqualTo("uuid", uuid);
					parseObject = query.getFirst();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		EditText view = (EditText) findViewById(R.id.title);
		view.setHint("Subject....");
		if(title != null) {
			view.setText(title);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.save:
	        	hideSoftKeyboard();
	            savePressed();
	        	return true;
	        case R.id.delete:
	        	deletePressed();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.subject_edit_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		saveButton = (MenuItem)menu.findItem(R.id.save);
		deleteButton = (MenuItem)menu.findItem(R.id.delete);
		if(parseObject == null) {
			deleteButton.setVisible(false);
		}
		else if(!parseObject.getParseUser("createdBy").equals(ParseUser.getCurrentUser())) {
			deleteButton.setVisible(false);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

}
