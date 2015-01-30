package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectSubjectActivity extends ListActivity {
	
	private List<ParseObject> objectList = new ArrayList<ParseObject>();
	
	
	/*
	 * Parse callbacks
	 * 
	 */
	private FindCallback<ParseObject> populateListCallbackLocal = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {
			if (e == null) {
				objectList.clear();
				List<String> localList = new ArrayList<String>();
				for(ParseObject obj : list) {
					objectList.add(obj);
					localList.add(obj.getString("title"));
				}
				LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(SelectSubjectActivity.this, 0, localList);
	            setListAdapter(adapter);
	            
	        } else {
	            Log.d("SelectLocationActivity", "Error: " + e.getMessage());
	        }
		}
		
	};
	
	private FindCallback<ParseObject> populateListCallbackCloud = new FindCallback<ParseObject>() {

		@Override
		public void done(final List<ParseObject> locations, ParseException e) {
			
			if(e == null) {
		        ParseObject.unpinAllInBackground("Subject", new DeleteCallback() {

					@Override
					public void done(ParseException e) {
						if(e == null) {
							ParseObject.pinAllInBackground("Subject", locations);
						}
						else {
							Log.d("SelectLocationActivity", "Error: " + e.getMessage());
						}
						
					}
		        	
		        });
		        
		        objectList.clear();
		        List<String> list = new ArrayList<String>();
		        for(ParseObject obj : locations) {
		        	objectList.add(obj);
		        	list.add(obj.getString("title"));
		        }
		        LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(SelectSubjectActivity.this, 0, list);
		        setListAdapter(adapter);
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	/*
	 * private functions
	 * 
	 */
	private void populateListLocal() {
		ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Subject");
		localQuery.fromLocalDatastore();
		//localQuery.orderByDescending("updatedAt");
		localQuery.orderByAscending("title");
		localQuery.findInBackground(populateListCallbackLocal);
	}
	
	private void populateListCloud() {
		ParseQuery<ParseObject> cloudQuery = ParseQuery.getQuery("Subject");
		//cloudQuery.orderByDescending("updatedAt");
		cloudQuery.orderByAscending("title");
		cloudQuery.findInBackground(populateListCallbackCloud);
	}
	
	private void populateList() {
		populateListLocal();
		ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) {
			populateListCloud();
		}
	}
	
	private Bundle prepareBundle(ParseObject parseObject) {
		Bundle bundle = new Bundle();
		bundle.putString("parseId", parseObject.getObjectId());
		bundle.putString("uuid", parseObject.getString("uuid"));
		bundle.putString("title", parseObject.getString("title"));
		
		return bundle;
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		populateList();
		
	}
	
	
	@Override
	protected void onStart() {
		TextView emptyView = new TextView(this);
		((ViewGroup) getListView().getParent()).addView(emptyView);
		emptyView.setText("There are currently no subjects to list.");
		emptyView.setGravity(Gravity.CENTER);
		getListView().setEmptyView(emptyView);
		
		super.onStart();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseObject selected = objectList.get(position);
		String parseId = selected.getObjectId();
		String uuid = selected.getString("uuid");
		String selectedString = selected.getString("title");
		
		Bundle bundle = new Bundle();
		bundle.putString("parseId", parseId);
		bundle.putString("uuid", uuid);
		bundle.putString("title", selectedString);

        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
	}

}
