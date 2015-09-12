package com.sankarmanoj.thefoodkingapp;


import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    ListView listView;
    Menu MainMenu;
    TextView ErrorView;
    String uid;
    TextView LoadingTV;
    ProgressBar LoadingPB;
    FoodArrayAdapter foodArrayAdapter;
    BroadcastReceiver registerSuccess;
    BroadcastReceiver menuUpdated;
    public final String TAG="MainActivity";
    List<FoodItem> FoodArray;

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(menuUpdated);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(menuUpdated, new IntentFilter(QuickPreferences.menuUpdated));
    }

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
        else
        {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "gcm-register");
                jsonObject.put("uid", uid);
                jsonObject.put("gcm-token", gcmtoken);
                JSONServerComm json = new JSONServerComm();
                json.execute(jsonObject);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoadingPB=(ProgressBar)findViewById(R.id.loadingProgressBar);
        LoadingTV=(TextView)findViewById(R.id.loadingTextView);
        registerSuccess = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String type = intent.getStringExtra("type");
                if(type.equals(QuickPreferences.regSuccess))
                {
                    FoodKing.FoodMenu=foodArrayAdapter.getAllElements();
                    Intent i = new Intent(getApplicationContext(), Checkout.class);
                    i.putExtra("action","get-status");
                    startActivity(i);
                }
                else if (type.equals(QuickPreferences.noUser))
                {
                    Toast.makeText(getApplicationContext(),"User does not exist\n Login In Again...",Toast.LENGTH_SHORT);
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("uid").apply();
                    Intent intent1 = new Intent(getApplicationContext(),Login.class);
                    startActivity(intent1);
                    finish();
                }
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(registerSuccess);
            }
        };
        sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        menuUpdated = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String type=intent.getStringExtra("type");

                if(type!=null)
                {

                    if(type.equals(QuickPreferences.fullUpdate))
                    {
                        LoadingTV.setVisibility(View.INVISIBLE);
                        LoadingPB.setVisibility(View.INVISIBLE);
                        foodArrayAdapter.clear();
                        foodArrayAdapter.addAll(FoodKing.FoodMenu);
                        foodArrayAdapter.notifyDataSetChanged();
                        Log.d(TAG,"Full Update");
                    }
                    if(type.equals(QuickPreferences.commFail))
                    {
                        LoadingPB.setVisibility(View.INVISIBLE);
                        LoadingTV.setText("Unable to Load Menu");
                        LoadingTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FoodKing.singleton.updateRegistrationState();

                            }
                        });
                        Log.d(TAG,"Comm Failed");
                    }

                }
                else
                {

                    return ;
                }
                if(FoodKing.FoodMenu.size()==foodArrayAdapter.getCount()) {
                    Log.d(TAG,"Same Size");
                    for (int i = 0; i < foodArrayAdapter.getCount(); i++)
                    {
                       FoodItem x = foodArrayAdapter.getItem(i);
                        x.quantity = FoodKing.FoodMenu.get(i).quantity;
                        if (x.inCartQuantity>x.quantity)
                        {
                            x.inCartQuantity=x.quantity;
                        }

                    }
                    foodArrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    foodArrayAdapter.clear();
                    foodArrayAdapter.addAll(FoodKing.FoodMenu);
                    foodArrayAdapter.notifyDataSetChanged();
                }
                foodArrayAdapter.notifyDataSetChanged();
                Log.d(TAG, "List View updated");
         }
        };
        getActionBar().setDisplayShowTitleEnabled(false);
        handleIntent(getIntent());
        ErrorView = (TextView)findViewById(R.id.errorView);
        ErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        listView=(ListView)findViewById(R.id.listView1);
        foodArrayAdapter = new FoodArrayAdapter(getApplicationContext(),R.layout.fooditemlist,FoodKing.FoodMenu);
        listView.setAdapter(foodArrayAdapter);
        if(FoodKing.gotMenu)
        {
            Log.i(TAG,"Got Menu On Load");
            LoadingTV.setVisibility(View.GONE);
            LoadingPB.setVisibility(View.GONE);
        }
        if(!FoodKing.runningGetMenu)
        {
            FoodKing.singleton.updateMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        this.MainMenu=menu;

        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s != "") {
                    Intent i;
                    i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setAction(Intent.ACTION_SEARCH);
                    i.putExtra(SearchManager.QUERY, s);
                    startActivity(i);
                    return true;
                }
                return false;

            }
        });

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);


            showResults(query);
        }
    }
    private void showResults(String query) {
        FoodArray = new ArrayList<>();
        for(int i=0;i<FoodKing.FoodMenu.size();i++)
        {
            if(FoodKing.FoodMenu.get(i).name.toLowerCase().contains(query.toLowerCase()))
            {
                FoodArray.add(FoodKing.FoodMenu.get(i));
            }
        }
        foodArrayAdapter = new FoodArrayAdapter(getApplicationContext(), R.layout.fooditemlist, FoodArray);
        listView.setAdapter(foodArrayAdapter);
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
                FoodKing.FoodMenu=foodArrayAdapter.getAllElements();
                Intent i = new Intent(getApplicationContext(), Checkout.class);
                i.putExtra("action","get-status");
                startActivity(i);
            }
            else
            {
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(registerSuccess,new IntentFilter(QuickPreferences.regSuccess));
                FoodKing.singleton.updateRegistrationState();
                Toast.makeText(getApplicationContext(),"Not Registered with Server\n Rechecking ...",Toast.LENGTH_SHORT).show();

            }
        }
        return super.onOptionsItemSelected(item);
    }



}
