package com.example.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by prudhvirampey on 18/08/15.
 */
public class FoodCart extends Activity {
    List<FoodItem> tempList;
    ListView listView;
    DiffFoodArrayAdapter foodArrayAdapter;
    public final String TAG="FoodCart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tempList=new ArrayList<>();
        List<FoodItem> list=FoodKing.FoodMenu;
        if((list)!=null) {
            for (int i = 0; i < (list.size()); i++) {
                tempList.add(list.get(i));
            }
        }



        super.onCreate(savedInstanceState);
        if(tempList==null)
            Log.d(TAG,"Damn!");
        for(int i=0;i<(tempList.size());i++)
        {
if(((tempList.get(i)).inCartQuantity)==0)
    tempList.remove(i);
        }
        setContentView(R.layout.foodcart);

        listView=(ListView)findViewById(R.id.listView2);
        if(listView==null)
        {
            Log.i(TAG, "List View is null");
        }
        foodArrayAdapter = new DiffFoodArrayAdapter(getApplicationContext(),R.layout.fooditemlist1,tempList);
        if(foodArrayAdapter==null)
        {
            Log.i(TAG,"Food Array is null");
        }



                listView.setAdapter(foodArrayAdapter);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.logout)
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().remove("uid").apply();
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
