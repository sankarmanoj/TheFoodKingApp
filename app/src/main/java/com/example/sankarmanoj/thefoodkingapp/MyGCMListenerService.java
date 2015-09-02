package com.example.sankarmanoj.thefoodkingapp;

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
                sendNotification("Order Confirmed");
                LocalBroadcastManager.getInstance(this).sendBroadcast(confirm);
            } else if (button.equals("dispatch")) {
                Intent confirm = new Intent("order-status");
                confirm.putExtra("button", "dispatch");
                confirm.putExtra("status", "Order has been Dispatched.");
                sendNotification("Order Dispatched");
                LocalBroadcastManager.getInstance(this).sendBroadcast(confirm);

            }
            if (button.equals("close")) {
                Intent orderclose = new Intent(getApplicationContext(), MainActivity.class);
                orderclose.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(orderclose);
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
    }


        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
    private void sendNotification(String message) {
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
}