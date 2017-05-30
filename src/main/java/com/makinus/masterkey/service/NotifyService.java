package com.makinus.masterkey.service;

import java.util.List;

import com.makinus.masterkey.db.service.impl.DatabaseHandler;
import com.makinus.masterkey.MainActivity;
import com.makinus.masterkey.R;
import com.makinus.masterkey.utils.MasterKeyUtils;
import com.makinus.masterkey.model.Card;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class NotifyService extends Service {

	private static int nID = 0;
	@Override
   public IBinder onBind(Intent intent) {
      return null;
   }
	
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
	  System.out.println("NotifyService"); 
      DatabaseHandler dbService = DatabaseHandler.getInstance(getApplicationContext());
      List<Card> cards = dbService.getAllCards();
      for(Card card : cards) {
    	  if(card.getRemindMe().equals("Y") 
    			  && MasterKeyUtils.getInstance(this.getApplicationContext()).isExpired(card.getUpdatedOn())) {
    		  showNotification(card.getAccount());
    	  }
      }
      
      return START_STICKY;
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
   }
   
   private void showNotification(String cardTitle) {
		NotificationCompat.Builder builder =
		         new NotificationCompat.Builder(this)
		         .setSmallIcon(R.drawable.ic_launcher)
		         .setContentTitle(">MasterKey - Password Expired for "+cardTitle)
		         .setContentText("Your account "+cardTitle+ "'s password is expired.");

		      Intent notificationIntent = new Intent(this, MainActivity.class);
		      PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
		         PendingIntent.FLAG_UPDATE_CURRENT);
		      builder.setContentIntent(contentIntent);

		      // Add as notification
		      NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		      manager.notify(nID++, builder.build());
	}
}