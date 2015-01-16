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
import android.widget.ListView;
import android.widget.TextView;

public class DatePickerActivity extends ListActivity {
	
	private Calendar dateTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		int month = extras.getInt("month");
		int year = extras.getInt("year");
		
		dateTime = Calendar.getInstance();
		dateTime.set(Calendar.MONTH, month);
		dateTime.set(Calendar.YEAR, year);
		
		String monthString = dateTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		//setTitle(monthString + " " + dateTime.get(Calendar.YEAR));
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		ArrayList<String> list = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		if(		c.get(Calendar.HOUR_OF_DAY) == 23 && 
				c.get(Calendar.MINUTE) > 45) {
			c.add(Calendar.MINUTE, 15);  //We are going to set it to the next day
		}
		int start = 1;
		if(dateTime.get(Calendar.MONTH) == c.get(Calendar.MONTH)) {
			start = c.get(Calendar.DAY_OF_MONTH);
		}
		int end = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		for(int i=start; i < end+1; i++) {
			list.add(Integer.toString(i));
		}
		
		LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(this, 0, list);
        setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		TextView labelView = (TextView) v.findViewById(R.id.label);
		int date = Integer.parseInt(labelView.getText().toString());
		dateTime.set(Calendar.DAY_OF_MONTH, date);
		
		Bundle bundle = new Bundle();
		bundle.putInt("dayOfMonth", dateTime.get(Calendar.DAY_OF_MONTH));
        bundle.putInt("month", dateTime.get(Calendar.MONTH));
        bundle.putInt("year", dateTime.get(Calendar.YEAR));

        Intent intent = new Intent(this,TimePickerActivity.class);
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
