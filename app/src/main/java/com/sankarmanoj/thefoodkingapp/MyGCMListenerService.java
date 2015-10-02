package com.sankarmanoj.thefoodkingapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGCMListenerService extends GcmListenerService {
     public final String TAG="MyGCMListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i(TAG, data.toString());
        String Stringtype = data.getString("type");
        if (Stringtype.equals("button")) {

            String button = data.getString("button-type");
            if (button.equals("confirm")) {
                Log.i(TAG, "Sent Confirm Broadcast");
                Intent confirm = new Intent("order-status");
                confirm.putExtra("button", "confirm");
                confirm.putExtra("status", "Order Confirmed");
                sendOrderNotification("Order Confirmed");
                LocalBroadcastManager.getInstance(this).sendBroadcast(confirm);
            } else if (button.equals("dispatch")) {
                Intent confirm = new Intent("order-status");
                confirm.putExtra("button", "dispatch");
                confirm.putExtra("status", "Order has been Dispatched.");
                sendOrderNotification("Order Dispatched");
                LocalBroadcastManager.getInstance(this).sendBroadcast(confirm);

            }
            if (button.equals("close")) {
                Intent confirm = new Intent("order-status");
                confirm.putExtra("button","closed");
                LocalBroadcastManager.getInstance(this).sendBroadcast(confirm);
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

            }
        }
        else if (Stringtype.equals("update-menu"))
        {
            FoodKing.singleton.updateMenu();

        }
        else if (Stringtype.equals(QuickPreferences.regSuccess))
        {
            FoodKing.singleton.updateRegistrationState();
        }
        else if (Stringtype.equals("notification"))
        {
            sendNotification(data.getString("title"),data.getString("body"));
        }
    }


        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
    private void sendOrderNotification(String message) {
        final Uri RollURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.roll);
        Intent intent = new Intent(this, Checkout.class);
        intent.putExtra("action","get-status");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("FoodKing Order Update")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RollURI)
                .setColor(Color.argb(255,255,0,0))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notificationBuilder.build());


    }
    private void sendNotification(String Title, String message) {
        final Uri RollURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.roll);
        Intent intent = new Intent(this, Checkout.class);
        intent.putExtra("action","get-status");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(Title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RollURI)
                .setColor(Color.argb(255,255,0,0))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notificationBuilder.build());


    }
}