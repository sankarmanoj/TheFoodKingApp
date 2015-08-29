package com.example.sankarmanoj.thefoodkingapp;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    ListView listView;
    Menu MainMenu;
    TextView ErrorView;
    String uid;
    Message msg;
    Timer t2;
    Handler handler;
    Handler ToastHandler;
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
        String gcmtoken = sharedPreferences.getString("token","null");
        if(gcmtoken.equals("null"))
        {
           Intent intent = new Intent(getApplicationContext(),GCMRegistrationIntentService.class);
            startService(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayShowTitleEnabled(false);

        ErrorView = (TextView)findViewById(R.id.errorView);
        ErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listView.setAdapter(foodArrayAdapter);
            }
        };
        ToastHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String toShow = (String)msg.obj;
                Toast.makeText(getApplicationContext(),toShow,Toast.LENGTH_SHORT).show();
            }
        };

        listView=(ListView)findViewById(R.id.listView1);
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
        },100,1000);
        t2 = new Timer(true);

        t2.scheduleAtFixedRate(new checkRegTimer(),10,100);






    }

    public class checkRegTimer extends TimerTask {
        @Override
        public void run() {

            switch (FoodKing.registrationState)
            {
                case 0:
                    break;
                case 1:



                    t2.cancel();
                    break;
                case 2:

                    msg = new Message();
                    msg.obj=new String("Still not registered \n Click on link sent to your email");
                    ToastHandler.sendMessage(msg);
                    t2.cancel();
                    break;
                case 3:

                    msg = new Message();
                    msg.obj=new String("User doesn't exist. Please re-register.");
                    ToastHandler.sendMessage(msg);
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("uid").apply();
                    Intent intent = new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);
                    finish();
                    t2.cancel();
                    break;
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        this.MainMenu=menu;

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
            if(FoodKing.registrationState==1) {
                Intent i = new Intent(getApplicationContext(), Checkout.class);
                i.putExtra("action","get-status");
                startActivity(i);
            }
            else
            {
                FoodKing.singleton.updateRegistrationState();
                Toast.makeText(getApplicationContext(),"Not Registered with Server\n Rechecking ...",Toast.LENGTH_SHORT).show();

                    t2 = new Timer(true);
                    t2.scheduleAtFixedRate(new checkRegTimer(), 100, 500);

            }
        }
        return super.onOptionsItemSelected(item);
    }



}
