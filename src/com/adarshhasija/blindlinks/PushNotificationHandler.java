package com.adarshhasija.blindlinks;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseUser;

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

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
			this.context = context;
			this.json = json;
			generateNotification();
		} catch (JSONException e) {
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
		        .setContentTitle("Blind Links")
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
