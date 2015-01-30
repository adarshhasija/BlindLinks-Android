package com.adarshhasija.ahelp;

import java.util.List;

import com.adarshhasija.ahelp.RecordAdapter.ViewHolderRecord;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordEditAdapter extends ArrayAdapter<String> {
	  private final Context context;
	  private final List<String> values;
	  static class ViewHolderRecord {
		    TextView labelView;
		    TextView contentView;
		}

	  public RecordEditAdapter(Context context, int resource, List<String> values) {
	    super(context, resource, values);
	    this.context = context;
	    this.values = values;
	  }

	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		
	    ViewHolderRecord viewHolder;
	    
	    if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.record_edit_row_layout, parent, false);
			
			viewHolder = new ViewHolderRecord();
			viewHolder.labelView = (TextView) convertView.findViewById(R.id.label);
			viewHolder.contentView = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolderRecord) convertView.getTag();
		}
	    
	    if(position == 0) viewHolder.labelView.setText("Date and Time");
	    if(position == 1) viewHolder.labelView.setText("Location");
	    if(position == 2) viewHolder.labelView.setText("Subject");
	    if(position == 3) viewHolder.labelView.setText("Representing Someone?");
	    viewHolder.contentView.setText(values.get(position));
	    viewHolder.contentView.setContentDescription(viewHolder.labelView.getText() + " " + viewHolder.contentView.getText());

	    return convertView;
	  }

}
