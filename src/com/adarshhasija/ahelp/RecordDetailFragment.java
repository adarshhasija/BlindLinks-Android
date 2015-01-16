package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adarshhasija.ahelp.RecordAdapter.ViewHolderRecord;
import com.adarshhasija.ahelp.dummy.DummyContent;
import com.adarshhasija.ahelp.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

/**
 * A fragment representing a single Record detail screen. This fragment is
 * either contained in a {@link RecordListActivity} in two-pane mode (on
 * tablets) or a {@link RecordDetailActivity} on handsets.
 */
public class RecordDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public static final String ARG_DATA = "data";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem; 
	private ParseObject record;
	private ParseObject exam;
	private ParseUser otherUser;
	private MenuItem progressButton;
	private MenuItem deleteButton;
	private MenuItem editButton;
	private MenuItem acceptButton;
	private MenuItem cancelButton;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RecordDetailFragment() {
	}
	
	
	/*
	 * Our Parse callbacks
	 * 
	 * 
	 */
	private GetCallback<ParseUser> getUserCallback = new GetCallback<ParseUser>() {

		@Override
		public void done(ParseUser user, ParseException e) {
			if (e == null) {
				otherUser = user;
				if(getActivity() != null) {
					getActivity().setTitle(user.getString("firstName") + " " + user.getString("lastName"));
				}
				
		    } else {
		    	Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
		    }
		}
		
	};
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				Toast.makeText(getActivity(), "Status successfully changed to "+record.getString("status"), Toast.LENGTH_SHORT).show();
				
		/*		((TextView) getActivity().findViewById(R.id.status))
				.setText("Status: " + record.getString("status").toUpperCase());
				((TextView) getActivity().findViewById(R.id.status))
				.setContentDescription("Status: " + record.getString("status").toUpperCase());	*/
				
				JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.ahelp.intent.RECEIVE");
					jsonObj.put("objectId", record.getObjectId());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        /*	ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
	    		pushQuery.whereEqualTo("phoneNumber", ParseUser.getCurrentUser().getString("phoneNumber"));
				ParsePush push = new ParsePush();
				push.setQuery(pushQuery); 
				push.setData(jsonObj); 
				push.setMessage("From the client");
				push.sendInBackground();	*/	
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("recipientPhoneNumber", otherUser.getString("phoneNumber"));
				params.put("data", jsonObj);
				ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
				   public void done(String success, ParseException e) {
				       if (e == null) {
				          // Push sent successfully
				       }
				       else {
				    	   Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
				       }
				   }
				});
				
				setResultForReturn();
			}
			else {
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	private DeleteCallback deleteCallback = new DeleteCallback() {

		@Override
		public void done(ParseException e) {
			getActivity().setResult(getActivity().RESULT_OK);
			getActivity().finish();
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
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   if(record.getParseUser("createdBy").equals(ParseUser.getCurrentUser())) { //They can delete the entire event
                		   record.deleteEventually();
                		   record.unpinInBackground("Event", deleteCallback);
                	   }
                	   else {  //Simply remove the read access
                		   ParseACL recordAcl = record.getACL();
                		   recordAcl.setReadAccess(ParseUser.getCurrentUser(), false);
                		   record.setACL(recordAcl);
                		   record.saveEventually();
                		   record.unpinInBackground("Event", deleteCallback);
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
	 * 
	 * 
	 */
	private void setExamDetails() {
		ParseUser creator = record.getParseUser("createdBy");
		Date dateTime = exam.getDate("dateTime");
		
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
		
		ParseObject examLocation = exam.getParseObject("location");
		String subject = exam.getString("subject");
		String notes = exam.getString("notes");
		if(notes == null) {
			notes = "No additional notes";
		}
		try {
			creator.fetchFromLocalDatastore();
			examLocation.fetchFromLocalDatastore();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String contentDescription = "Exam set by "+creator.getString("firstName") + " " + creator.getString("lastName") +
									" on " + dateTimeString + " at " + examLocation.getString("title") + ".";
		
		((TextView) getActivity().findViewById(R.id.user))
		.setText(creator.getString("firstName") + " " + creator.getString("lastName"));
		((TextView) getActivity().findViewById(R.id.recordDateTime))
		.setText(dateTimeString);
		((TextView) getActivity().findViewById(R.id.location))
		.setText(examLocation.getString("title"));
		((TextView) getActivity().findViewById(R.id.subject))
		.setText(subject);
		((TextView) getActivity().findViewById(R.id.notes))
		.setText(notes);
		
		RelativeLayout examLayout = ((RelativeLayout) getActivity().findViewById(R.id.exam));
		examLayout.setContentDescription(contentDescription);
	}
	
	
	/*
	 * Set results for return private functions
	 * 
	 */
	private void setResultForReturn() 
	{
		Bundle bundle = new Bundle();
		bundle.putString("parseId", record.getObjectId());
		bundle.putString("uuid", record.getString("uuid"));
		Intent returnIntent = new Intent();
		returnIntent.putExtras(bundle);
		getActivity().setResult(getActivity().RESULT_OK, returnIntent);
	}
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fragment_record_detail);
		setHasOptionsMenu(true);

		if (getArguments().containsKey("parseId")) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			//mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
			String parseId = getArguments().getString("parseId");
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Record");
			query.fromLocalDatastore();
			try {
				record = query.get(parseId);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (getArguments().containsKey("uuid")) {
			String uuid = getArguments().getString("uuid");
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
			query.fromLocalDatastore();
			query.whereEqualTo("uuid", uuid);
			try {
				record = query.getFirst();
				if(record != null) {
					exam = record.getParseObject("exam");
					exam.fetchFromLocalDatastore();
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(exam != null) {
			setExamDetails();
			
			List<ParseObject> list = exam.getList("actions");
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
			
			RelativeLayout examLayout = ((RelativeLayout) getActivity().findViewById(R.id.exam));
			
			ParseACL examAcl = exam.getACL();
			if(!examAcl.getWriteAccess(ParseUser.getCurrentUser())) {
				ImageView editView = ((ImageView) getActivity().findViewById(R.id.edit));
				editView.setVisibility(View.GONE);
			}
			else {
				examLayout.setOnClickListener(new View.OnClickListener() {
				
					@Override
					public void onClick(View view) {
						Bundle bundle = new Bundle();
						bundle.putString("parseId", exam.getObjectId());
						bundle.putString("uuid", exam.getString("uuid"));
						Intent intent = new Intent(getActivity(), RecordEditActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 1000); //this means exam being edited 
					}
				});
			}
			
			
			ListView actionListView = (ListView) getActivity().findViewById(R.id.actionList);
			Collections.reverse(actionList);
			ActionListAdapter actionAdapter = new ActionListAdapter(getActivity(), 0, actionList);
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
						Intent intent = new Intent(getActivity(), ActionReplyActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, position);
					}
					else if(holder.type.equals("examUpdate")) {
						Intent intent = new Intent(getActivity(), ExamChangesActivity.class);
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
		
		if(resultCode == getActivity().RESULT_OK) {
			//Exam has been modified
			if(requestCode == 1000) {
				if(data != null) {
					Bundle extras = data.getExtras();
					String parseId = extras.getString("parseId");
					String uuid = extras.getString("uuid");
					ParseQuery examQuery = ParseQuery.getQuery("Exam");
					examQuery.fromLocalDatastore();
					
					ParseObject lastAction;
					try {
						if(parseId != null) {
							exam = examQuery.get(parseId);
						}
						else {
							examQuery.whereEqualTo("uuid", uuid);
							exam = examQuery.getFirst();
						}
						setExamDetails();
						
						List<ParseObject> actions = exam.getList("actions");
						ParseObject action = actions.get(actions.size()-1);
						
						ListView actionListView = (ListView) getActivity().findViewById(R.id.actionList);
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
					ParseQuery query = ParseQuery.getQuery("Action");
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
					
					ListView actionListView = (ListView) getActivity().findViewById(R.id.actionList);
					ActionListAdapter actionAdapter = (ActionListAdapter) actionListView.getAdapter();
					ParseObject oldAction = actionAdapter.getItem(requestCode);
					
					actionAdapter.remove(oldAction);
					actionAdapter.insert(newAction, 0);
					actionAdapter.notifyDataSetChanged();
					
					String statusString = newAction.getString("statusString");
					if(statusString != null) {
						ImageView iconView = (ImageView) getActivity().findViewById(R.id.icon);
						
						if(statusString.equals("accepted")) {
							iconView.setImageResource(R.drawable.ic_action_accept);
							deleteButton.setVisible(false);
							ParseACL examAcl = exam.getACL();
							examAcl.setWriteAccess(ParseUser.getCurrentUser(), false);
							exam.setACL(examAcl);
							((ImageView) getActivity().findViewById(R.id.edit)).setVisibility(View.GONE);
							((RelativeLayout) getActivity().findViewById(R.id.exam)).setOnClickListener(null);
						}
						else {
							iconView.setImageResource(R.drawable.ic_action_event);
							deleteButton.setVisible(true);
							ParseACL examAcl = exam.getACL();
							examAcl.setWriteAccess(ParseUser.getCurrentUser(), true);
							exam.setACL(examAcl);
							((ImageView) getActivity().findViewById(R.id.edit)).setVisibility(View.VISIBLE);
							((RelativeLayout) getActivity().findViewById(R.id.exam))
							.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View view) {
									Bundle bundle = new Bundle();
									bundle.putString("parseId", exam.getObjectId());
									bundle.putString("uuid", exam.getString("uuid"));
									Intent intent = new Intent(getActivity(), RecordEditActivity.class);
									intent.putExtras(bundle);
									startActivityForResult(intent, 1000); //this means exam being edited 
								}
							});
						}
						exam.saveEventually();
						exam.pinInBackground();
					}
					record.saveEventually();
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.record_detail_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		deleteButton = (MenuItem)menu.findItem(R.id.delete);
		
		ListView actionListView = (ListView) getActivity().findViewById(R.id.actionList);
		ActionListAdapter actionAdapter = (ActionListAdapter) actionListView.getAdapter();
		ParseObject lastAction = actionAdapter.getItem(0);
		if(lastAction.getString("statusString") != null) {
			if(lastAction.getString("statusString").equals("accepted")) {
				deleteButton.setVisible(false);
			}
		}
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
}
