package com.example.sankarmanoj.thefoodkingapp;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGCMListenerService extends GcmListenerService {
     public final String TAG="MyGCMListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i(TAG,data.toString());
        String Stringtype= data.getString("type");
        if (Stringtype.equals("button"))
        {

                String button = data.getString("button-type");
                if (button.equals("confirm"))
                {
                    Log.i(TAG,"Sent Confirm Broadcast");
                    Intent confirm = new Intent("order-status");
                    confirm.putExtra("button","confirm");
                    confirm.putExtra("status","Order Confirmed");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(confirm);
                }
                else  if (button.equals("dispatch"))
                {
                    Intent confirm = new Intent("order-status");
                    confirm.putExtra("button","dispatch");
                    confirm.putExtra("status","Order has been Dispatched.");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(confirm);
                } if (button.equals("close"))
                      {
                        Intent orderclose = new Intent(getApplicationContext(),MainActivity.class);
                          orderclose.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                          startActivity(orderclose);
                    }


            }

        Log.d(TAG, "From: " + from);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */


    }
}