package com.example.sankarmanoj.thefoodkingapp;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGCMListenerService extends GcmListenerService {
     public final String TAG="MyGCMListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type= data.getString("status");
        switch ( type)
        {
            case "button":
            {
                String button = data.getString("button-type");
                if (button.equals("confirmed"))
                {

                }
                break;

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