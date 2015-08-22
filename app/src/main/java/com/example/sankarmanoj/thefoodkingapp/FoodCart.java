package com.example.sankarmanoj.thefoodkingapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prudhvirampey on 18/08/15.
 */
public class FoodCart extends Activity {
    List<FoodItem> tempList;
    ListView listView;
    FoodCartArrayAdapter foodArrayAdapter;
    Button Checkout;
    public final String TAG="FoodCart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tempList=new ArrayList<>();
        List<FoodItem> list=FoodKing.FoodMenu;
        if((list)!=null) {
            for (int i = 0; i < (list.size()); i++) {
                if(list.get(i).inCartQuantity>0) {
                    tempList.add(list.get(i));
                }
            }
        }
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_foodcart);


        listView=(ListView)findViewById(R.id.listView2);
        foodArrayAdapter = new FoodCartArrayAdapter(getApplicationContext(),R.layout.fooditemlist,tempList);
        listView.setAdapter(foodArrayAdapter);
        Checkout=(Button)findViewById(R.id.checkoutButton);
        Checkout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)  listView.getLayoutParams();
                int pixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                params.setMargins(0,0,0,pixels);
                listView.setLayoutParams(params);

                Log.i("FoodCart","Checkout Clicked");
            }
        });




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
