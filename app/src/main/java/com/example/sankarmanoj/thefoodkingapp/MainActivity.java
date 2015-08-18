package com.example.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    ListView listView;
    List<FoodItem>items;
    TextView ErrorView;
    String uid;
    FoodArrayAdapter foodArrayAdapter;
    public final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        uid = sharedPreferences.getString("uid","null");
       /* if (uid.equals("null"))
        {
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        ErrorView = (TextView)findViewById(R.id.errorView);
        ErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckRegistration checkRegistration = new CheckRegistration();
                try {
                    JSONObject toSend = new JSONObject();
                    toSend.put("type","check-registration");
                    toSend.put("uid",uid);
                    checkRegistration.execute(toSend);
                    ErrorView.setVisibility(View.INVISIBLE);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        listView=(ListView)findViewById(R.id.listView1);
        if(listView==null)
        {
            Log.i(TAG,"List View is null");
        }

        if(foodArrayAdapter==null)
        {
            Log.i(TAG,"Food Array is null");
        }
        /* CheckRegistration checkRegistration = new CheckRegistration();
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("type","check-registration");
            toSend.put("uid",uid);
            checkRegistration.execute(toSend);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/

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
    public class CheckRegistration extends JSONServerComm
    {


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
               SuperOnPostExecute(jsonObject);
               try
               {
                   if(jsonObject.get("state").equals("registered"))
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
                   else if(jsonObject.get("state").equals("does-not-exist"))
                   {
                       Toast.makeText(getApplicationContext(),"User doesn't exist. Please re-register.",Toast.LENGTH_LONG).show();
                       PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("uid").apply();
                       Intent intent = new Intent(getApplicationContext(),Login.class);
                       startActivity(intent);
                       finish();
                   }
                   else
                   {
                       Toast.makeText(getApplicationContext(),"Not Registered with Server",Toast.LENGTH_LONG).show();
                       ErrorView.setText("Click on the link sent to the registered email address");
                       ErrorView.setVisibility(View.VISIBLE);
                   }

               }
               catch (Exception e)
               {

               }
        }
    }
    public class LoadMenu extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
           SuperOnPostExecute(jsonObject);
            List<FoodItem> items = new ArrayList<>();
            try
            {
                if(jsonObject.get("state").equals("got-menu"))
                {
                    JSONArray menu=jsonObject.getJSONArray("menu");
                    for(int i=0;i<menu.length();i++)
                    {
                        JSONObject item = menu.getJSONObject(i);
                        items.add(new FoodItem(item.getString("name"),Integer.parseInt(item.getString("price")),Integer.parseInt(item.getString("quantity")),0));

                    }
                    foodArrayAdapter=new FoodArrayAdapter(getApplicationContext(),R.layout.fooditemlist,items);
                    listView.setAdapter(foodArrayAdapter);
                    Log.d("MainActivity","got items");
                    Log.d("MainActivity",items.toString());

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Unknown error in loading menu. Please contact the administrator",Toast.LENGTH_LONG).show();
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
