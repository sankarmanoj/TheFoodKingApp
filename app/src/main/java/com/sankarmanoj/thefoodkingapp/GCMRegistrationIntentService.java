package com.sankarmanoj.thefoodkingapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sankarmanoj.thefoodkingapp.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 *
 */

public class GCMRegistrationIntentService extends IntentService {
    public GCMRegistrationIntentService() {
        super(TAG);
    }
    String uid;
    private static final String TAG = "RegIntentService";
    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid=sharedPreferences.getString("uid","null");
        if(uid.equals("null"))
        {
            Intent intent1 = new Intent(getApplicationContext(),Login.class);
            startActivity(intent1);
        }
        else {
            try {
                synchronized (TAG) {
                    InstanceID instanceID = InstanceID.getInstance(this);
                    String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i(TAG, "GCM Registration Token: " + token);

                    sharedPreferences.edit().putString("token", token).apply();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type","gcm-register");
                    jsonObject.put("uid",uid);
                    jsonObject.put("gcm-token",token);
                    GCMRegisterComm gcmRegisterComm = new GCMRegisterComm();
                    gcmRegisterComm.execute(jsonObject);

                }
            } catch (IOException e) {
                e.printStackTrace();
                //Fill in IOE handling
            }
            catch (JSONException e)
            {

            }
        }
    }
    public class GCMRegisterComm extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try
            {
                if(jsonObject.get("state").equals("success"))
                {

                }
                else if(jsonObject.get("state").equals("does-not-exist"))
                {
                    Intent intent1 = new Intent(getApplicationContext(),Login.class);
                    startActivity(intent1);
                }
                else
                {
                    throw new RuntimeException("Error GCM Registering with App Server");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}
