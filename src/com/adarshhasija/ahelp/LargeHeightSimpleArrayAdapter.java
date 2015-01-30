package com.adarshhasija.ahelp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LargeHeightSimpleArrayAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final List<String> values;
	
	public LargeHeightSimpleArrayAdapter(Context context, int resource, List<String> values) {
		super(context, resource, values);
		this.context = context;
		this.values = values;
	}

	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.list_item_large_height, parent, false);
	    TextView labelView = (TextView) rowView.findViewById(R.id.label);

	    labelView.setText(values.get(position));
	    labelView.setContentDescription(values.get(position));
	    return rowView;
	  }

}
