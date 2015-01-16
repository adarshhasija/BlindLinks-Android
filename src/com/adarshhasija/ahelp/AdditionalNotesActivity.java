package com.adarshhasija.ahelp;

import com.parse.ParseObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class AdditionalNotesActivity extends Activity {
	
	private MenuItem acceptButton;
	
	/**
	 * Hides the soft keyboard
	 */
	public void hideSoftKeyboard() {
	    if(getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}
	
	private void acceptPressed() {
		EditText view = (EditText) findViewById(R.id.notes);
		String notes = view.getText().toString();
		
		Bundle bundle = new Bundle();
		bundle.putString("notes", notes);

        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.additional_notes_activity);
		
		Bundle extras = getIntent().getExtras();
		EditText view = (EditText) findViewById(R.id.notes);
		String notes = extras.getString("notes");
		view.setHint("Type here....");
		//Exam e = extras.getParcelable("parcelable");
		if(notes != null) {
			view.setText(notes);
		}
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
		
		getMenuInflater().inflate(R.menu.additional_notes, menu);
		
		acceptButton = (MenuItem)menu.findItem(R.id.accept);
		
		return super.onCreateOptionsMenu(menu);
	}

}
