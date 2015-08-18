package com.example.sankarmanoj.thefoodkingapp;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankarmanoj on 8/18/15.
 */
public class FoodKing extends Application {
    static List<FoodItem> FoodMenu;
    static Boolean gotMenu=false;

    @Override
    public void onCreate() {
        super.onCreate();
        FoodMenu=new ArrayList<>();
        Log.d("Whatthefuck","Running menu loader");
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

    public class LoadMenu extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            List<FoodItem> items = new ArrayList<>();
            try
            {
                if(jsonObject.get("state").equals("got-menu"))
                {
                    JSONArray menu=jsonObject.getJSONArray("menu");
                    for(int i=0;i<menu.length();i++)
                    {
                        JSONObject item = menu.getJSONObject(i);
                        items.add(new FoodItem(item.getString("name"),Integer.parseInt(item.getString("price"))));

                    }
                    FoodMenu=items;
                    gotMenu=true;


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
}
