package com.adarshhasija.blindlinks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

import com.adarshhasija.blindlinks.dummy.DummyContent;
import com.example.ngotransactionrecords.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RecordDetailFragment() {
	}
	
	private FindCallback<ParseObject> findCallback = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {
			
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
			
			mainApplication.setSelectedRecord(null);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == getActivity().RESULT_OK) {
			MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
			record = mainApplication.getModifiedRecord();
			mainApplication.setModifiedRecord(null);
			
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
									;
				((TextView) getActivity().findViewById(R.id.date))
				.setText(dateString);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.record_detail_activity, menu);
		
		MenuItem editButton = (MenuItem)menu.findItem(R.id.edit);
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
		
		MenuItem acceptButton = (MenuItem)menu.findItem(R.id.accept);
		acceptButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				item.setVisible(false); 

				return false;
			}
		});
		
		MenuItem cancelButton = (MenuItem)menu.findItem(R.id.cancel);
		cancelButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				item.setVisible(false); 

				return false;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
}
