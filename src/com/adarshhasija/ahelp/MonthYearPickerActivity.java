package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MonthYearPickerActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		ArrayList<String> list = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		if(		c.getActualMaximum(Calendar.DAY_OF_MONTH) == c.get(Calendar.MONTH) &&
				c.get(Calendar.HOUR_OF_DAY) == 23 && 
				c.get(Calendar.MINUTE) > 45) {
			c.add(Calendar.MINUTE, 15);  //We are going to set it to the next day
		}
		String monthString= "";
		int year = 0;
		for(int i=0; i < 6; i++) {
			monthString = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
			year = c.get(Calendar.YEAR);
			list.add(monthString + " " + year);
			c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
		}
		
		LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(this, 0, list);
        setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, position);
		
		Bundle bundle = new Bundle();
        bundle.putInt("month", c.get(Calendar.MONTH));
        bundle.putInt("year", c.get(Calendar.YEAR));

        Intent intent = new Intent(this,DatePickerActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, position);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK) {
			setResult(Activity.RESULT_OK, data);
	        finish();
		}
	}
	

}
