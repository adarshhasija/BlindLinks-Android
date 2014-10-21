package com.adarshhasija.ahelp;


import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment {
	
	private Activity mActivity;
    private OnTimeSetListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        // This error will remind you to implement an OnTimeSetListener 
        //   in your Activity if you forget
        try {
            mListener = (OnTimeSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
        }
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		int hour = getArguments().getInt("hourOfDay");
        int minute = getArguments().getInt("minute");

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(mActivity, mListener, hour, minute,
                DateFormat.is24HourFormat(mActivity));
    }

}
