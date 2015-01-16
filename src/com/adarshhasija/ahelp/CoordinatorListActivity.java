package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class CoordinatorListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<String> list = new ArrayList<String>();
		list.add("Locations");
		list.add("Subjects");
		
		LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter); 
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if(position == 0) {
			Intent intent = new Intent(this, LocationsListActivity.class);
			startActivity(intent);
		}
		if(position == 1) {
			Intent intent = new Intent(this, SubjectsListActivity.class);
			startActivity(intent);
		}
	}

}
