package com.example.sankarmanoj.thefoodkingapp;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class FoodKing extends Application {
    static List<FoodItem> FoodMenu;
    static Boolean gotMenu=false;
    static int registrationState = 0;
   public static FoodKing singleton;
    public static String status;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton=this;
        status="Order Not Confirmed";
        FoodMenu=new ArrayList<>();


        updateMenu();
        updateRegistrationState();
    }
   public void updateMenu()
   {
       LoadMenu loadMenu  = new LoadMenu();
       try {
           JSONObject toSend = new JSONObject();
           toSend.put("type","get-menu");
           loadMenu.execute(toSend);
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
   }
    public void updateRegistrationState()
    {      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       String uid = sharedPreferences.getString("uid","null");
        CheckRegistration checkRegistration = new CheckRegistration();
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("type", "check-registration");
            toSend.put("uid", uid);
            checkRegistration.execute(toSend);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class LoadMenu extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            List<FoodItem> items = new ArrayList<>();
            if(jsonObject==null)
            {
                Toast.makeText(getApplicationContext(),"Error Communicating With Server \n Please try again later",Toast.LENGTH_SHORT).show();
            }
            else
                try
                {
                    if(jsonObject.get("state").equals("got-menu"))
                    {
                        JSONArray menu=jsonObject.getJSONArray("menu");
                        for(int i=0;i<menu.length();i++)
                        {
                            JSONObject item = menu.getJSONObject(i);
                            items.add(new FoodItem(item.getString("name"),Integer.parseInt(item.getString("price")),Integer.parseInt(item.getString("quantity")),Integer.parseInt(item.getString("id"))));

                        }
                        FoodMenu=items;
                        gotMenu=true;
                        Intent gotMenuIntent = new Intent(QuickPreferences.menuUpdated);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(gotMenuIntent);

                        Log.d("MainActivity", "got items");
                        Log.d("MainActivity",items.toString());

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unknown error in loading menu. Please contact the administrator", Toast.LENGTH_LONG).show();
                        Log.e("Menu Loader","Returned Error");
                    }


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

        }
    }
    public class CheckRegistration extends JSONServerComm
    {


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try
            {
                if(jsonObject.get("state").equals("registered"))
                {
                   registrationState=1;
                    Intent registered= new Intent(QuickPreferences.regSuccess);
                    registered.putExtra("type",QuickPreferences.regSuccess);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registered);
                }
                else if(jsonObject.get("state").equals("does-not-exist"))
                {
                    registrationState=3;
                    Intent registered= new Intent(QuickPreferences.regSuccess);
                    registered.putExtra("type",QuickPreferences.noUser);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registered);

                }
                else
                {
                    registrationState=2;


                }

            }
            catch (Exception e)
            {
                registrationState=2;
                e.printStackTrace();
            }
        }
    }
}
