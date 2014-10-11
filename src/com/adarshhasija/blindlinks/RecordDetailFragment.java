package com.adarshhasija.blindlinks;

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
import android.widget.TextView;
import android.widget.Toast;

import com.adarshhasija.blindlinks.dummy.DummyContent;
import com.adarshhasija.blindlinks.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
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

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;
	private ParseObject record;
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
	private GetCallback getUserCallback = new GetCallback<ParseUser>() {

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
				((TextView) getActivity().findViewById(R.id.status))
				.setText("Status: " + record.getString("status").toUpperCase());
				((TextView) getActivity().findViewById(R.id.status))
				.setContentDescription("Status: " + record.getString("status").toUpperCase());
				
				JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.blindlinks.intent.RECEIVE");
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
		public void done(ParseException arg0) {
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
                	   setResultForDeleted();
                	   record.deleteEventually(deleteCallback);
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        builder.show();
	}
	
	private void editPressed()
	{
		//Pass the ParseObject as a global variable
		MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
		mainApplication.setSelectedRecord(record);
		
		//Bundle bundle = new Bundle();
		//bundle.putLong("id", media.getId());
		Intent newIntent = new Intent(getActivity(),RecordEditActivity.class);
    	//newIntent.putExtras(bundle);
    	startActivityForResult(newIntent, 0);
	}
	
	private void acceptPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Accept invitation?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   acceptButton.setVisible(false);
                	   cancelButton.setVisible(true);
                	   record.put("status", "accepted");
                	   record.put("status_by", ParseUser.getCurrentUser());
                	   record.saveInBackground(saveCallback);
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        builder.show();
	}
	
	private void cancelPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Reject invitation?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   cancelButton.setVisible(false);
                	   acceptButton.setVisible(true);
                	   record.put("status", "rejected");
                	   record.put("status_by", ParseUser.getCurrentUser());
                	   record.saveInBackground(saveCallback);
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
	 * Set results for return private functions
	 * 
	 */
	private void setResultForReturn() 
	{
	/*	Bundle bundle = new Bundle();
		bundle.putString("student", record.getString("student"));
		bundle.putString("subject", record.getString("subject"));
		bundle.putString("status", record.getString("status"));
		ParseProxyObject ppo = new ParseProxyObject(record);
		//returnIntent.putExtras(bundle);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("parseObject", ppo); */
		getActivity().setResult(getActivity().RESULT_OK);
		
		MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
		mainApplication.setModifiedRecord(record);
	}
	
	private void setResultForDeleted() 
	{
		getActivity().setResult(getActivity().RESULT_CANCELED);
		
		MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
		mainApplication.setModifiedRecord(record);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fragment_record_detail);
		setHasOptionsMenu(true);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
		
		MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
		record = mainApplication.getSelectedRecord();
		mainApplication.setSelectedRecord(null);
		
		ParseUser user = record.getParseUser("user");
		ParseUser recipient = record.getParseUser("recipient");
		ParseUser currentUser = ParseUser.getCurrentUser();
		ParseUser userToFetch;
		
		//If they are not the same, this means currentUser is the recipient, so display the user in title
		if(!user.getObjectId().equals(currentUser.getObjectId())) {
			userToFetch = user;
		}
		//If they are the same, currentUser is the user and recipient should be displayed
		else {
			userToFetch = recipient;
		}
		userToFetch.fetchIfNeededInBackground(getUserCallback);
	}



	@Override
	public void onResume() {
		if(record != null) {
			ParseUser currentUser = ParseUser.getCurrentUser();
			ParseUser user = record.getParseUser("user");
			String createdBy = null;
			
			if(user.getObjectId().equals(currentUser.getObjectId())) {
				createdBy = "Record created by: "+currentUser.getString("firstName") + " " + currentUser.getString("lastName");
			}
			else if(user.getObjectId().equals(otherUser.getObjectId())) {
				createdBy = "Record created by: "+otherUser.getString("firstName") + " " + otherUser.getString("lastName");
			}
			
			String studentDescription = "Student: "+record.getString("student");
			String subjectDescription = "Subject: "+record.getString("subject");
			((TextView) getActivity().findViewById(R.id.student))
			.setText(studentDescription);
			((TextView) getActivity().findViewById(R.id.student))
			.setContentDescription(studentDescription);
			((TextView) getActivity().findViewById(R.id.subject))
			.setText(subjectDescription);
			((TextView) getActivity().findViewById(R.id.subject))
			.setContentDescription(subjectDescription);
			((TextView) getActivity().findViewById(R.id.created_by))
			.setText(createdBy);
			((TextView) getActivity().findViewById(R.id.created_by))
			.setContentDescription(createdBy);
		
			Date d = record.getDate("dateTime");
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			if(hour > 12) hour = hour - 12;
			else if(hour == 0) hour = 12;
			int minute = c.get(Calendar.MINUTE);
			String minuteString = (minute < 10)?"0"+Integer.toString(minute):Integer.toString(minute);
			String dateString = c.get(Calendar.DATE) + " " + 
					c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " +
					c.get(Calendar.YEAR) + " " +
					hour + ":" +
					minuteString + " " +
					c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
			((TextView) getActivity().findViewById(R.id.date))
			.setText("Date and Time: " + dateString);
			((TextView) getActivity().findViewById(R.id.date))
			.setContentDescription("Date and Time: " + dateString);
			
			((TextView) getActivity().findViewById(R.id.status))
			.setText("Status: " + record.getString("status").toUpperCase());
			((TextView) getActivity().findViewById(R.id.status))
			.setContentDescription("Status: " + record.getString("status").toUpperCase());
			
		}
		
		super.onResume();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == getActivity().RESULT_OK) {
			MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
			record = mainApplication.getModifiedRecord();
			mainApplication.setModifiedRecord(null);
			setResultForReturn();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.delete:
	            deletePressed();
	            return true;
	        case R.id.edit:
	            editPressed();
	            return true;
	        case R.id.accept:
	        	acceptPressed();
	        	return true;
	        case R.id.cancel:
	        	cancelPressed();
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
		editButton = (MenuItem)menu.findItem(R.id.edit);
		acceptButton = (MenuItem)menu.findItem(R.id.accept);
		cancelButton = (MenuItem)menu.findItem(R.id.cancel);
		
		ParseUser recordUser = record.getParseUser("user");
		ParseUser currentUser = ParseUser.getCurrentUser();
		//If this is the recipient, delete button is not visible
		//Only the sending user is allowed to delete
		if(!currentUser.getObjectId().equals(recordUser.getObjectId())) {
			deleteButton.setVisible(false);
		}
		if(record.getString("status").equals("accepted")) { acceptButton.setVisible(false); }
		if(record.getString("status").equals("rejected")) { cancelButton.setVisible(false); }
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
}
