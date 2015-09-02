package com.example.sankarmanoj.thefoodkingapp;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ExistingOrder extends Activity {

    ListView OrderListView;
    Button CancelOrderButton;
    TextView StatusTextView;
    String uid;
    private BroadcastReceiver statusReceiver;
    FoodCartArrayAdapter foodArrayAdapter;
    private final String TAG="ExistingOrderActivity";
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(statusReceiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusTextView.setText(FoodKing.status);
        if(FoodKing.status.equals("Order Confirmed")||FoodKing.status.equals("Order has been Dispatched."))
        {
            CancelOrderButton.setVisibility(View.INVISIBLE);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver,new IntentFilter("order-status"));
        Log.i(TAG,FoodKing.status);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_order);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        uid = sharedPreferences.getString("uid","null");

        if (uid.equals("null"))
        {
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }

        OrderListView= (ListView)findViewById(R.id.existingOrderListView);
        CancelOrderButton = (Button)findViewById(R.id.cancelOrderButton);
        StatusTextView = (TextView)findViewById(R.id.statusTextView);
        StatusTextView.setText(FoodKing.status);
        sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        String token = sharedPreferences.getString("token","null");
        if (token.equals("null"))
        {
            StatusTextView.setText("Unable to get Status");
        }

        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String status = intent.getStringExtra("status");
                Log.d(TAG,status);
                CancelOrderButton.setVisibility(View.INVISIBLE);
                StatusTextView.setText(status);
                FoodKing.status=status;
            }
        };
        List<FoodItem> items = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("json-object"));


            JSONArray menu = jsonObject.getJSONArray("order");
            for (int i = 0; i < menu.length(); i++) {
                JSONObject item = menu.getJSONObject(i);
                FoodItem toAddItem = new FoodItem(item.getString("name"), Integer.parseInt(item.getString("price")), Integer.parseInt(item.getString("quantity")), Integer.parseInt(item.getString("id")));
                toAddItem.setInCartQuantity(item.getString("quantity"));
                items.add(toAddItem);
            }
            if(jsonObject.getBoolean("confirmed"))
            {
                StatusTextView.setText("Order Confirmed");
                CancelOrderButton.setVisibility(View.INVISIBLE);
            }
            if(jsonObject.getBoolean("sent"))
            {
                StatusTextView.setText("Order Dispatched");
                CancelOrderButton.setVisibility(View.INVISIBLE);
            }
            foodArrayAdapter = new FoodCartArrayAdapter(getApplicationContext(),R.layout.foodcartitem,items);
            OrderListView.setAdapter(foodArrayAdapter);

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }


        CancelOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type","cancel-order");
                    jsonObject.put("uid",uid);
                    CancelOrder cancelOrder = new CancelOrder();
                    cancelOrder.execute(jsonObject);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public class CancelOrder extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(jsonObject==null)
            {
                Toast.makeText(getApplicationContext(), "Error Communicating With Server \n Please try again later", Toast.LENGTH_SHORT).show();
            }
            else
                try
                {
                    if(jsonObject.get("state").equals("cancelled-order"))
                    {
                        Toast.makeText(getApplicationContext(),"Order Cancelled Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();


                    }
                    else if (jsonObject.get("state").equals("already-confirmed"))
                    {
                        Toast.makeText(getApplicationContext(),"Order already confirmed, Can't cancel",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unknown error in cancelling order. Please contact the administrator", Toast.LENGTH_LONG).show();

                    }


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

        }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_existing_order, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
