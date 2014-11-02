package com.adarshhasija.ahelp;

import org.json.JSONException;
import org.json.JSONObject;

import com.adarshhasija.ahelp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class PushNotificationHandler extends BroadcastReceiver {
	private Context context=null;
	private JSONObject json=null;
	
	//This is the callback after a successful save to the local datastore
	//Called from GetCallback
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				generateNotification();
			} else {
				e.printStackTrace();
			}
		}
		
	};
	
	//This callback gets the record for saving in the local datastore
	private GetCallback getCallback = new GetCallback() {

		@Override
		public void done(ParseObject object, ParseException e) {
			if(e == null) {
				object.pinInBackground("Records", saveCallback);
			} else {
				e.printStackTrace();
			}
		}
		
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser != null) {
			try {
				json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
				this.context = context;
				this.json = json;
				generateNotification();
			
				//UNCOMMENT THIS IF YOU WANT OFFLINE STORAGE
				//saveRecord(); 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void saveRecord() {
		try {
			String objectId = json.getString("objectId");
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Record");
			query.getInBackground(objectId, getCallback);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void generateNotification() {
		Intent intent = new Intent(context, RecordListActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //stackBuilder.addParentStack(InboxActivity.class);
        stackBuilder.addNextIntent(intent);
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent contentIntent =
        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		        
		        NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("aHelp")
		        //.setLargeIcon(R.drawable.ic_launcher)
		        .setAutoCancel(true)
		        .setContentText("You have notifications");
		
		        mBuilder.setContentIntent(contentIntent);
		        int defaults = Notification.DEFAULT_SOUND;
		        //defaults |= Notification.FLAG_AUTO_CANCEL;
		        defaults |= Notification.DEFAULT_VIBRATE;
		        defaults |= Notification.FLAG_SHOW_LIGHTS;
		        defaults |= Notification.DEFAULT_LIGHTS;
		        mBuilder.setDefaults(defaults);
		
		        mNotifM.notify(0, mBuilder.build());

      }

}
