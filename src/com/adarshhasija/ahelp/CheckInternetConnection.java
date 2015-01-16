package com.adarshhasija.ahelp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CheckInternetConnection extends BroadcastReceiver {
	
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
		    }else{
		    //TODO iF INTERNET IS DISCONNECTED 
		    	Log.d("WOW", "*************NO CONNECTION*********************");
		    }
	}
	
	

}
