package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.adarshhasija.ahelp.RecordAdapter.ViewHolderRecord;
import com.parse.ParseObject;

public class LocationListAdapter extends ArrayAdapter<ParseObject> implements Filterable {
	
	private final Context context;
	private List<ParseObject> recordList;
	private final List<ParseObject> backupList; //used when filtering is happening
	static class ViewHolderRecord {
	    TextView locationView;
	    ImageView iconView;
	    ImageView placeView;
	}
	
	/*
	 * my custom Filter
	 * 
	 * 
	 */
	Filter filter = new Filter() {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			//If recordList size is less, filtering has happened
			//restore original before continuing
			if(recordList.size() < backupList.size()) {
				restoreOriginalList();
			}
			FilterResults filterResults = new FilterResults();   
	         ArrayList<ParseObject> tempList=new ArrayList<ParseObject>();
	         //constraint is the result from text you want to filter against. 
	         //objects is your data set you will filter from
	         if(constraint != null && recordList !=null) {
	             int length=recordList.size();
	             int i=0;
	                while(i<length){
	                    ParseObject record=recordList.get(i);
	                    String location = record.getString("title").toLowerCase();
	                    if(location.contains(constraint)) {
	                    	tempList.add(record);
	                    }
	                    i++;
	                }
	                //following two lines is very important
	                //as publish result can only take FilterResults objects
	                filterResults.values = tempList;
	                filterResults.count = tempList.size();
	          }
	          return filterResults;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			recordList = (ArrayList<ParseObject>) results.values;
	          if (recordList.size() > 0) {
	           notifyDataSetChanged();
	          } else {
	              notifyDataSetInvalidated();
	          }
			
		}
		
	};
	
	
	
	/*
	 * private function to controller what is and isnt visible
	 * 
	 */
	private void visibilitySettings(ViewHolderRecord viewHolder)
	{
		viewHolder.placeView.setVisibility(View.GONE);
	}

	public LocationListAdapter(Context context, int resource, List<ParseObject> values) {
		super(context, resource, values);
		this.context = context;
		this.recordList = values;
		this.backupList = new ArrayList<ParseObject>(values);
	}
	
	@Override
	public int getCount() {
		return recordList != null?recordList.size() : 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolderRecord viewHolder;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.location_row_layout, parent, false);
			
			viewHolder = new ViewHolderRecord();
			viewHolder.locationView = (TextView) convertView.findViewById(R.id.label);
			viewHolder.placeView = (ImageView) convertView.findViewById(R.id.place);
			convertView.setTag(viewHolder);
			
		}
		else {
			viewHolder = (ViewHolderRecord) convertView.getTag();
		}
		
		visibilitySettings(viewHolder);
		
	    ParseObject record = recordList.get(position);
		if(record != null) {
			viewHolder.locationView.setText(record.getString("title"));
			convertView.setContentDescription(record.getString("title"));
			//viewHolder.categoryView.setTag(record);
		}

		return convertView;
	}
	
	@Override
	public Filter getFilter() {
		return filter;
	}
	
	private void restoreOriginalList() {
		recordList.clear();
		recordList.addAll(backupList);
	}

}
