package com.example.sankarmanoj.thefoodkingapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    Button checkOut;
    ListView listView;

    TextView ErrorView;
    String uid;
    Handler handler;
    FoodArrayAdapter foodArrayAdapter;
    public final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        uid = sharedPreferences.getString("uid","null");
        if (uid.equals("null"))
        {
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
        CheckRegistration checkRegistration = new CheckRegistration();
        try {
            JSONObject toSend = new JSONObject();
            toSend.put("type","check-registration");
            toSend.put("uid",uid);
            checkRegistration.execute(toSend);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listView.setAdapter(foodArrayAdapter);
            }
        };




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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
else if(id==R.id.checkout)
        {
            Intent i = new Intent(getApplicationContext(), FoodCart.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
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
                       final Timer t = new Timer();
                       t.scheduleAtFixedRate(new TimerTask() {
                           @Override
                           public void run() {
                               if (FoodKing.gotMenu)
                               {
                                   foodArrayAdapter = new FoodArrayAdapter(getApplicationContext(),R.layout.fooditemlist,FoodKing.FoodMenu);
                                   handler.sendEmptyMessage(1);
                                   t.cancel();

                               }
                           }
                       },0,1000);
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


}
