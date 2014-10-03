package com.adarshhasija.blindlinks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import com.parse.FindCallback;
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
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RecordDetailFragment() {
	}
	
	private FindCallback setTitleFindCallback = new FindCallback() {

		@Override
		public void done(List arg0, ParseException arg1) {
			if (arg1 == null) {
				ParseUser user = (ParseUser) arg0.get(0);
				otherUser = user;
				if(getActivity() != null) {
					getActivity().setTitle(user.getString("firstName") + " " + user.getString("lastName"));
				}
				
		    } else {
		    	Toast.makeText(getActivity(), arg1.getMessage(), Toast.LENGTH_SHORT).show();
		    }
		}
		
	};
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				((TextView) getActivity().findViewById(R.id.status))
				.setText("Status: " + record.getString("status").toUpperCase());
				
				ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
				pushQuery.whereEqualTo("email", ParseUser.getCurrentUser().getUsername());
				
				JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.blindlinks.intent.RECEIVE");
					jsonObj.put("objectId", record.getObjectId());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ParsePush push = new ParsePush();
				push.setQuery(pushQuery); // Set our Installation query
				push.setData(jsonObj);
				//push.setMessage("From the client");
				push.sendInBackground();
			}
			else {
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};

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
		
		ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
		queryUser.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		
		//If they are not the same, this means currentUser is the recipient, so display the user in title
		if(!user.getObjectId().equals(currentUser.getObjectId())) {
			queryUser.whereEqualTo("objectId", user.getObjectId());
		}
		//If they are the same, currentUser is the user and recipient should be displayed
		else {
			queryUser.whereEqualTo("objectId", recipient.getObjectId());
		}
		queryUser.findInBackground(setTitleFindCallback);
	}
	


	@Override
	public void onDestroy() {
		Bundle bundle = new Bundle();
		bundle.putString("student", record.getString("student"));
		bundle.putString("subject", record.getString("subject"));
		bundle.putString("status", record.getString("status"));
		ParseProxyObject ppo = new ParseProxyObject(record);
		//returnIntent.putExtras(bundle);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("parseObject", ppo);
		getActivity().setResult(getActivity().RESULT_OK,returnIntent);
		
		super.onDestroy();
	}



	@Override
	public void onResume() {
		if(record != null) {
			((TextView) getActivity().findViewById(R.id.student))
			.setText(record.getString("student"));
			((TextView) getActivity().findViewById(R.id.subject))
			.setText(record.getString("subject"));
		
			Date d = record.getDate("dateTime");
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int minute = c.get(Calendar.MINUTE);
			String minuteString = (minute < 10)?"0"+Integer.toString(minute):Integer.toString(minute);
			String dateString = c.get(Calendar.DATE) + " " + 
					c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " +
					c.get(Calendar.YEAR) + " " +
					c.get(Calendar.HOUR) + ":" +
					minuteString + " " +
					c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
			((TextView) getActivity().findViewById(R.id.date))
			.setText("Date: " + dateString);
			
			((TextView) getActivity().findViewById(R.id.status))
			.setText("Status: " + record.getString("status").toUpperCase());
		}
		
		super.onResume();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == getActivity().RESULT_OK) {
			MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
			record = mainApplication.getModifiedRecord();
			mainApplication.setModifiedRecord(null);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.record_detail_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		MenuItem editButton = (MenuItem)menu.findItem(R.id.edit);
		final MenuItem acceptButton = (MenuItem)menu.findItem(R.id.accept);
		final MenuItem cancelButton = (MenuItem)menu.findItem(R.id.cancel);
		
		editButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				//Pass the ParseObject as a global variable
				MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
				mainApplication.setSelectedRecord(record);
				
				//Bundle bundle = new Bundle();
				//bundle.putLong("id", media.getId());
				Intent newIntent = new Intent(getActivity(),RecordEditActivity.class);
		    	//newIntent.putExtras(bundle);
		    	startActivityForResult(newIntent, 0); 

				return false;
			}
		});
		
		acceptButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(final MenuItem item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        builder.setMessage("Accept invitation?")
		               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   item.setVisible(false);
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

				return false;
			}
		});
		if(record.getString("status").equals("accepted")) { acceptButton.setVisible(false); }
		
		
		cancelButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(final MenuItem item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        builder.setMessage("Reject invitation?")
		               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   item.setVisible(false);
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
				

				return false;
			}
		});
		if(record.getString("status").equals("rejected")) { cancelButton.setVisible(false); }

		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
}
