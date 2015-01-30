package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainDetailActivity extends Activity {

	private ParseObject scribeRequest=null;
	
	private MenuItem progressButton;
	private MenuItem deleteButton;
	
	/*
	 * Parse callbacks
	 * 
	 * 
	 */
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.ahelp.intent.RECEIVE");
					jsonObj.put("objectId", scribeRequest.getObjectId());
					jsonObj.put("uuid", scribeRequest.getString("uuid"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	HashMap<String, Object> params = new HashMap<String, Object>();
	        	List<ParseObject> phoneNumbers = scribeRequest.getList("phoneNumbers");
				params.put("recipientPhoneNumber", phoneNumbers.get(0));
				params.put("data", jsonObj);
				ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
				   public void done(String success, ParseException e) {
				       if (e == null) {

				       }
				       else {
				    	   Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				       }
				   }
				});		
				
				
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}	
		}
		
	};
	
	
	private DeleteCallback deleteCallback = new DeleteCallback() {

		@Override
		public void done(ParseException e) {
			setResult(Activity.RESULT_OK);
			finish();
		}
		
	};
	
	/*
	 * Action bar button
	 * Private functions
	 * 
	 * 
	 */
	private void deletePressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   if(scribeRequest.getParseUser("createdBy").equals(ParseUser.getCurrentUser())) { //They can delete the entire thing
                		   List<ParseObject> actionList = scribeRequest.getList("actions");
                		   for(int i = 0; i < actionList.size(); i++) {
                			   ((ParseObject) scribeRequest.getList("actions").get(i)).deleteEventually();
                		   }
                		   scribeRequest.getParseObject("location").deleteEventually();
       	            		scribeRequest.getParseObject("subject").deleteEventually();
                		   scribeRequest.deleteEventually();
                	   }
                	   else {  //Simply remove the read access
                		   ParseACL recordAcl = scribeRequest.getACL();
                		   recordAcl.setReadAccess(ParseUser.getCurrentUser(), false);
                		   scribeRequest.setACL(recordAcl);
                		   
                		   scribeRequest.saveEventually();
                	   }
                	   
                	   List<ParseObject> actionList = scribeRequest.getList("actions");
            		   for(int i = 0; i < actionList.size(); i++) {
            			   ((ParseObject) scribeRequest.getList("actions").get(i)).unpinInBackground();
            		   }
            		   scribeRequest.getParseObject("location").unpinInBackground();
   	            		scribeRequest.getParseObject("subject").unpinInBackground();
                	   scribeRequest.unpinInBackground("ScribeRequest", deleteCallback);
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
	 * 
	 * 
	 */
	private void setExamDetails() {
		ParseUser creator = scribeRequest.getParseUser("createdBy");
		Date dateTime = scribeRequest.getDate("dateTime");
		boolean status = scribeRequest.getBoolean("status");
		String statusString = "";
		
		ImageView iconView = (ImageView) findViewById(R.id.icon);
		if(!status) {
			iconView.setImageResource(R.drawable.ic_action_event);
			statusString = "scribe not found";
			((ImageView) findViewById(R.id.edit)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.exam_content))
			.setOnClickListener(examOnClickListener);
		}
		else {
			iconView.setImageResource(R.drawable.ic_action_accept);
			statusString = "scribe found";
			((ImageView) findViewById(R.id.edit)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.exam_content)).setOnClickListener(null);
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if(hour > 12) hour = hour - 12;
		else if(hour == 0) hour = 12;
		int minute = c.get(Calendar.MINUTE);
		String minuteString = (minute < 10)?"0"+Integer.toString(minute):Integer.toString(minute);
		String dateTimeString = c.get(Calendar.DATE) + " " + 
				c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " +
				c.get(Calendar.YEAR) + " " +
				hour + ":" +
				minuteString + " " +
				c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
		
		ParseObject location = scribeRequest.getParseObject("location");
		ParseObject subject = scribeRequest.getParseObject("subject");
		final String representeePhoneNumber = scribeRequest.getString("representeePhoneNumber");
		final String representeeFirstName = scribeRequest.getString("representeeFirstName");
		String representeeLastName = scribeRequest.getString("representeeLastName");
		try {
			creator.fetchFromLocalDatastore();
			location.fetchFromLocalDatastore();
			subject.fetchFromLocalDatastore();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		((TextView) findViewById(R.id.user))
		.setText(creator.getString("firstName") + " " + creator.getString("lastName"));
		((TextView) findViewById(R.id.recordDateTime))
		.setText(dateTimeString);
		((TextView) findViewById(R.id.location))
		.setText(location.getString("title"));
		((TextView) findViewById(R.id.subject))
		.setText(subject.getString("title"));
		
		if(representeePhoneNumber != null &&
				representeeFirstName != null &&
					representeeLastName != null) {
			((TextView) findViewById(R.id.representee_name))
			.setText("Representing: "+ representeeFirstName + " " + representeeLastName);
			((TextView) findViewById(R.id.representee_phone_number))
			.setText(representeePhoneNumber);
			((ImageView) findViewById(R.id.call))
			.setVisibility(View.VISIBLE);
			((ImageView) findViewById(R.id.call))
			.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainDetailActivity.this);
			        builder.setMessage("Call "+representeeFirstName+"?")
			               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   String uri = "tel:" + representeePhoneNumber.trim() ;
			  					 	Intent intent = new Intent(Intent.ACTION_CALL);
			  					 	intent.setData(Uri.parse(uri));
			  					 	startActivity(intent);
			                   }
			               })
			               .setNegativeButton("No", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                       // User cancelled the dialog
			                   }
			               });
			        builder.show();
				}
			});
		}
		else {
			((TextView) findViewById(R.id.representee_name))
			.setText("Representing: Nobody");
			((TextView) findViewById(R.id.representee_phone_number))
			.setText("");
			((ImageView) findViewById(R.id.call))
			.setVisibility(View.GONE);
		}
		
		String contentDescription;
		
		if(representeePhoneNumber != null &&
				representeeFirstName != null &&
					representeeLastName != null) {
			contentDescription = "Scribe requested by "+creator.getString("firstName") + " " + creator.getString("lastName") +
				" on " + dateTimeString + " at " + location.getString("title") + " for "+ subject.getString("title") + 
				". Request made on behalf " + representeeFirstName + " " + representeeLastName + " who can be reached at " + representeePhoneNumber.toString() +
				"Current status is " + statusString + ". Tap to edit";
		}
		else {
			contentDescription = "Scribe requested by "+creator.getString("firstName") + " " + creator.getString("lastName") +
					" on " + dateTimeString + " at " + location.getString("title") + " for "+ subject.getString("title") + ". Representing nobody. " +
					"Current status is " + statusString + ". Tap to edit";
		}
		
		LinearLayout examLayout = ((LinearLayout) findViewById(R.id.exam_content));
		examLayout.setContentDescription(contentDescription);
	}
	
	private View.OnClickListener examOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			Bundle bundle = new Bundle();
			bundle.putString("parseId", scribeRequest.getObjectId());
			bundle.putString("uuid", scribeRequest.getString("uuid"));
			Intent intent = new Intent(MainDetailActivity.this, RecordEditActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, 1000); //this means exam being edited 
		}
	};
	
	/*
	 * Set results for return private functions
	 * 
	 */
	private void setResultForReturn() 
	{
		Bundle bundle = new Bundle();
		bundle.putString("parseId", scribeRequest.getObjectId());
		bundle.putString("uuid", scribeRequest.getString("uuid"));
		Intent returnIntent = new Intent();
		returnIntent.putExtras(bundle);
		setResult(Activity.RESULT_OK, returnIntent);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_record_detail);

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("parseId")) {
			String parseId = extras.getString("parseId");
			ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRequest");
			query.fromLocalDatastore();
			try {
				scribeRequest = query.get(parseId);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (extras.containsKey("uuid")) {
			String uuid = extras.getString("uuid");
			ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRequest");
			query.fromLocalDatastore();
			query.whereEqualTo("uuid", uuid);
			try {
				scribeRequest = query.getFirst();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(scribeRequest != null) {
			setExamDetails();
			
			List<ParseObject> list = scribeRequest.getList("actions");
			List<ParseObject> actionList = new ArrayList<ParseObject>();
			try {
				for(ParseObject action : list) {
					action.fetchFromLocalDatastore();
					actionList.add(action);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			LinearLayout examLayout = ((LinearLayout) findViewById(R.id.exam_content));
			
			ParseACL recordAcl = scribeRequest.getACL();
			if(!recordAcl.getWriteAccess(ParseUser.getCurrentUser())) {
				ImageView editView = ((ImageView) findViewById(R.id.edit));
				editView.setVisibility(View.GONE);
			}
			else {
				examLayout.setOnClickListener(examOnClickListener);
			}
			
			
			ListView actionListView = (ListView) findViewById(R.id.actionList);
			Collections.reverse(actionList);
			ActionListAdapter actionAdapter = new ActionListAdapter(this, 0, actionList);
	        actionListView.setAdapter(actionAdapter);
	        actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Bundle bundle = new Bundle();
					ActionListAdapter adapter = (ActionListAdapter) parent.getAdapter();
					ActionListAdapter.ViewHolderRecord holder =  (ActionListAdapter.ViewHolderRecord) view.getTag();
					String parseId = holder.parseId;
					String uuid = holder.uuid;
					bundle.putString("parseId", parseId);
					bundle.putString("uuid", uuid);
					if(holder.type.equals("request")) {
						Intent intent = new Intent(MainDetailActivity.this, ActionReplyActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, position);
					}
					else if(holder.type.equals("examUpdate")) {
						Intent intent = new Intent(MainDetailActivity.this, ExamChangesActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, position);
					}
				}
	        	
			});
			
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK) {
			//Exam has been modified
			if(requestCode == 1000) {
				if(data != null) {
					Bundle extras = data.getExtras();
					String parseId = extras.getString("parseId");
					String uuid = extras.getString("uuid");
					ParseQuery<ParseObject> scribeRequestQuery = ParseQuery.getQuery("ScribeRequest");
					scribeRequestQuery.fromLocalDatastore();
					
					ParseObject lastAction;
					try {
						if(parseId != null) {
							scribeRequest = scribeRequestQuery.get(parseId);
						}
						else {
							scribeRequestQuery.whereEqualTo("uuid", uuid);
							scribeRequest = scribeRequestQuery.getFirst();
						}
						setExamDetails();
						
						List<ParseObject> actions = scribeRequest.getList("actions");
						ParseObject action = actions.get(actions.size()-1);
						
						ListView actionListView = (ListView) findViewById(R.id.actionList);
						ActionListAdapter actionAdapter = (ActionListAdapter) actionListView.getAdapter();
						actionAdapter.insert(action, 0);
						actionAdapter.notifyDataSetChanged();
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			else if(data != null) {
					Bundle extras = data.getExtras();
					ParseObject newAction=null;
					String actionParseId = extras.getString("parseId");
					String actionUuid = extras.getString("uuid");
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Action");
					query.fromLocalDatastore();
					if(actionParseId != null) {
						try {
							newAction = query.get(actionParseId);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(actionUuid != null) {
						try {
							query.whereEqualTo("uuid", actionUuid);
							newAction = query.getFirst();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					ListView actionListView = (ListView) findViewById(R.id.actionList);
					ActionListAdapter actionAdapter = (ActionListAdapter) actionListView.getAdapter();
					ParseObject oldAction = actionAdapter.getItem(requestCode);
					
					actionAdapter.remove(oldAction);
					actionAdapter.insert(newAction, 0);
					actionAdapter.notifyDataSetChanged();
					
					//As the action has been updated, it should be moved to the front of the q
					List<ParseObject> list = scribeRequest.getList("actions");
					list.remove(newAction);
					list.add(newAction);
					scribeRequest.remove("actions");
					scribeRequest.put("actions", list);
					
					String statusString = newAction.getString("statusString");
					if(statusString != null) {
						ImageView iconView = (ImageView) findViewById(R.id.icon);
						
						if(statusString.equals("accepted")) {
							scribeRequest.put("status", true);
							deleteButton.setVisible(false);
							ParseACL recordAcl = scribeRequest.getACL();
							recordAcl.setWriteAccess(ParseUser.getCurrentUser(), false);
							scribeRequest.setACL(recordAcl);
							setExamDetails();
						}
						else {
							scribeRequest.put("status", false);
							deleteButton.setVisible(true);
							ParseACL recordAcl = scribeRequest.getACL();
							recordAcl.setWriteAccess(ParseUser.getCurrentUser(), true);
							scribeRequest.setACL(recordAcl);
							setExamDetails();
						}
					}
					ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
					if(cm.getActiveNetworkInfo() != null) {
						scribeRequest.pinInBackground();
						scribeRequest.saveInBackground(saveCallback);
					}
					else {
						scribeRequest.remove("isDraft");
						scribeRequest.put("isDraft", true);
						scribeRequest.pinInBackground();
					}
				}
			
			setResultForReturn();	//Exam or action has been edited
		}

	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.delete:
	            deletePressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.record_detail_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		deleteButton = (MenuItem)menu.findItem(R.id.delete);
		
	/*	ListView actionListView = (ListView) findViewById(R.id.actionList);
		ActionListAdapter actionAdapter = (ActionListAdapter) actionListView.getAdapter();
		ParseObject lastAction = actionAdapter.getItem(0);
		if(lastAction.getString("statusString") != null) {
			if(lastAction.getString("statusString").equals("accepted")) {
				deleteButton.setVisible(false);
			}
		}	*/
		if(scribeRequest.getBoolean("status") == true) {
			deleteButton.setVisible(false);
		}
		
		return super.onCreateOptionsMenu(menu);
	}
}
