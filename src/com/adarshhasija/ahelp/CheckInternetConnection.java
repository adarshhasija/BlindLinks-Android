package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class CheckInternetConnection extends BroadcastReceiver {
	
	//public static Queue<ParseObject> queueScribeRequest = new PriorityQueue<ParseObject>();
	//public static Queue<ParseUser> queueSelectedUser = new PriorityQueue<ParseUser>();
	private Context context = null;
	private ParseObject parseObject = null;
	private List<ParseObject> phoneNumbers = null;
	
	
	/*
	 * Parse Callback
	 * 
	 * 
	 */
	private FindCallback<ParseObject> findCallback = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {

			for(ParseObject obj : list) {
				parseObject = obj;
				phoneNumbers = obj.getList("phoneNumbers");
				//obj.remove("phoneNumbers");
				obj.remove("isDraft");
				obj.put("isDraft", false);
				obj.saveInBackground(saveCallback);
			}	
		}
		
	};
	
	
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.ahelp.intent.RECEIVE");
					jsonObj.put("objectId", parseObject.getObjectId());
					jsonObj.put("uuid", parseObject.getString("uuid"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("recipientPhoneNumber", phoneNumbers.get(0));
				params.put("data", jsonObj);
				ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
				   public void done(String success, ParseException e) {
				       if (e == null) {

				       }
				       else {
				    	   //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				    	   Log.i("CheckInternetConnection", e.getMessage());
				       }
				   }
				});		
				
				parseObject = null;
				
			}
			else {
				//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.i("CheckInternetConnection", e.getMessage());
			}	
		}
		
	};
	
	private boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        try {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (android.net.NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		if (haveNetworkConnection(context)) {
		    //TODO iF INTERNET IS CONNECTED 
			Log.d("WOW", "***************CONNECTION********************");
			this.context = context;
			ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRequest");
			query.fromLocalDatastore();
			query.whereEqualTo("isDraft", true);
			query.findInBackground(findCallback);
		    }else{
		    //TODO iF INTERNET IS DISCONNECTED 
		    	Log.d("WOW", "*************NO CONNECTION*********************");
		    }
	}
	
	

}
