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
    MenuItem menuItem;
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
        final Timer t2 = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                  switch (FoodKing.registrationState)
                  {
                      case 0:
                          break;
                      case 1:
                          if(menuItem!=null)
                          {
                              menuItem.setVisible(false);
                              invalidateOptionsMenu();
                              Toast.makeText(getApplicationContext(),"Successfully Registered",Toast.LENGTH_SHORT).show();
                          }
                          t2.cancel();
                          break;
                      case 2:

                          menuItem= MainMenu.add("Verify");


                          menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                              @Override
                              public boolean onMenuItemClick(MenuItem item) {
                                FoodKing.singleton.updateRegistrationState();
                                  return true;
                              }
                          });
                            invalidateOptionsMenu();
                          t2.cancel();
                          break;
                      case 3:
                          Toast.makeText(getApplicationContext(),"User doesn't exist. Please re-register.",Toast.LENGTH_LONG).show();
                          PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("uid").apply();
                          Intent intent = new Intent(getApplicationContext(),Login.class);
                          startActivity(intent);
                          finish();
                          t2.cancel();
                          break;
                  }

            }
        },100,1000);






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
                Intent i = new Intent(getApplicationContext(), FoodCart.class);
                startActivity(i);
            }
            else
            {
                FoodKing.singleton.updateRegistrationState();
                Toast.makeText(getApplicationContext(),"Not Registered with Server\n Rechecking ...",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }



}
