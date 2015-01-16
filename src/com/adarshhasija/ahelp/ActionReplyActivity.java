package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ActionReplyActivity extends ListActivity {
	
	private String parseId=null;
	private String uuid=null;
	
	//MenuItems
	private MenuItem progressButton;
	private MenuItem sendButton;

	/*
	 * Parse callbacks
	 * 
	 */
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if (e == null) {
				Bundle bundle = new Bundle();
				bundle.putString("parseId", parseId);
				bundle.putString("uuid", uuid);
				Intent returnIntent = new Intent();
				returnIntent.putExtras(bundle);
				setResult(Activity.RESULT_OK, returnIntent);
				finish();
			}
			else {
				Toast.makeText(ActionReplyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	
	/*
	 * Action bar item functions
	 * 
	 */
	private void sendPressed() {
		int position = getListView().getCheckedItemPosition();
		ParseObject parseObject = getActionObject();
		if(position == 0) {
			parseObject.put("statusString", "accepted");
		}
		else {
			parseObject.put("statusString", "rejected");
		}
		parseObject.remove("uuid");
		parseObject.saveEventually(); //this is because we dont want uuid saved to the cloud
		if(uuid != null) {
			parseObject.put("uuid", uuid);
		}
		parseObject.pinInBackground(saveCallback);
	}
	
	
	/*
	 * private functions
	 * 
	 * 
	 */
	private ParseObject getActionObject() {
		ParseObject parseObject=null;
		ParseQuery query = ParseQuery.getQuery("Action");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parseObject;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		parseId = extras.getString("parseId");
		uuid = extras.getString("uuid");
		
		List<String> list = new ArrayList<String>();
		list.add("Accept");
		list.add("Reject");
		
		LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		View unselectedView;
		if(position == 0) {
			Toast.makeText(ActionReplyActivity.this, "You selected accept", Toast.LENGTH_SHORT).show();
			unselectedView = l.getChildAt(1);
		}
		else {
			Toast.makeText(ActionReplyActivity.this, "You selected reject", Toast.LENGTH_SHORT).show();
			unselectedView = l.getChildAt(0);
		}
			v.setBackgroundColor(Color.rgb(204, 255, 255)); //baby blue
			unselectedView.setBackgroundColor(Color.TRANSPARENT);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
        case R.id.send:
        	sendPressed();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.action_reply_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		sendButton = (MenuItem)menu.findItem(R.id.send);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
	
	
}
