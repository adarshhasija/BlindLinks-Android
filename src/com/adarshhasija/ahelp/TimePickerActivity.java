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

public class TimePickerActivity extends ListActivity {
	
	private Calendar dateTime;
	private int startHourOfDay;
	private int startMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		int dayOfMonth = extras.getInt("dayOfMonth");
		int month = extras.getInt("month");
		int year = extras.getInt("year");
		
		dateTime = Calendar.getInstance();
		dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		dateTime.set(Calendar.MONTH, month);
		dateTime.set(Calendar.YEAR, year);
		
		String monthString = dateTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		//setTitle(dateTime.get(Calendar.DAY_OF_MONTH) + " " + monthString + " " + dateTime.get(Calendar.YEAR));
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		ArrayList<String> list = new ArrayList<String>();
		Calendar c = Calendar.getInstance();

		if(dateTime.get(Calendar.MONTH) == c.get(Calendar.MONTH) &&
				dateTime.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH) &&
					c.get(Calendar.HOUR_OF_DAY) >= 7) {  //If it is today and after 7am, give the current time rounded to the next 15th min
			int mod = c.get(Calendar.MINUTE) % 15;
			c.add(Calendar.MINUTE, 15-mod);
		}
		else {
			c.set(Calendar.HOUR_OF_DAY, 7);
			c.set(Calendar.MINUTE, 0);
		}
		startHourOfDay = c.get(Calendar.HOUR_OF_DAY);
		startMinute = c.get(Calendar.MINUTE);
		
		//keep looping until we reach 12am next day
		while(true) {
			int hour = c.get(Calendar.HOUR_OF_DAY);
	        if(hour > 12) hour = hour - 12;
	        else if(hour == 0) hour = 12;
	        int minute = c.get(Calendar.MINUTE);
	        String minuteString = "";
	        if(minute == 0) minuteString = "00";
	        else minuteString = Integer.toString(minute);
	        String am_pm = c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
	        list.add(hour + ":" + minuteString + " " + am_pm);
	        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 15);
	        if(0 == c.get(Calendar.HOUR_OF_DAY) && 0 == c.get(Calendar.MINUTE)) {
	        	break;
	        }
		}
		
		LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(this, 0, list);
        setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		int difference = 0;
		if(position > 0) difference = 15*position;
		
		dateTime.set(Calendar.HOUR_OF_DAY, startHourOfDay);
		dateTime.set(Calendar.MINUTE, startMinute);
		dateTime.add(Calendar.MINUTE, difference);
		
		Bundle bundle = new Bundle();
		bundle.putInt("hourOfDay", dateTime.get(Calendar.HOUR_OF_DAY));
		bundle.putInt("minute", dateTime.get(Calendar.MINUTE));
		bundle.putInt("dayOfMonth", dateTime.get(Calendar.DAY_OF_MONTH));
        bundle.putInt("month", dateTime.get(Calendar.MONTH));
        bundle.putInt("year", dateTime.get(Calendar.YEAR));

        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
	}

}
