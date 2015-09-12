package com.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Checkout extends Activity {
    final public String TAG="CheckoutActivity";
    ProgressBar ServerProgressBar;
    Activity activity;
    TextView Status;
    String uid;
    String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getActionBar().setDisplayShowTitleEnabled(false);
        activity=this;
        Status=(TextView)findViewById(R.id.confirmOrderTextView);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        uid=sharedPreferences.getString("uid","null");
        action=getIntent().getStringExtra("action");
        ServerProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        if(action.equals("place-order")) {

            List<FoodItem> order = new ArrayList<>();
            final List<FoodItem> list = FoodKing.FoodMenu;
            if ((list) != null) {
                for (int i = 0; i < (list.size()); i++) {
                    if (list.get(i).inCartQuantity > 0) {
                        order.add(list.get(i));

                    }
                }
            }
            try {

                JSONArray orderJSONArray = new JSONArray();
                for (FoodItem x : order) {
                    JSONObject a = new JSONObject();
                    a.put("name", x.name);
                    a.put("quantity", x.inCartQuantity);
                    a.put("id", x.id);
                    orderJSONArray.put(a);

                }
                JSONObject toSend = new JSONObject();
                toSend.put("type", "create-order");
                toSend.put("uid", uid);
                String address = getIntent().getStringExtra("address");
                toSend.put("address",address+"  ");
                toSend.put("phone",getIntent().getStringExtra("phone"));
                String comments = getIntent().getStringExtra("comments");
                toSend.put("comments",comments+"  ");

                toSend.put("order", orderJSONArray);
                SubmitOrder toServer = new SubmitOrder();
                toServer.execute(toSend);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSON Failed!");

            }
        }
        else if (action.equals("get-status"))
        {
            JSONObject jsonObject1 = new JSONObject();
            try
            {

                jsonObject1.put("type","get-order");
                jsonObject1.put("uid",uid);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            GetOrder getOrder = new GetOrder();
            getOrder.execute(jsonObject1);
            ServerProgressBar.setVisibility(View.VISIBLE);
            Status.setText("Retrieving Order Status...");
            ServerProgressBar.setProgress(0);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_checkout, menu);
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
    public class GetOrder extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            if(jsonObject==null)
            {
                Toast.makeText(getApplicationContext(),"Error Communicating With Server \n Please try again later",Toast.LENGTH_SHORT).show();
            }
            else
                try
                {
                    if(jsonObject.get("state").equals("got-order"))
                    {
                        if(!jsonObject.get("user").equals(uid))
                        {
                            throw new RuntimeException("User on device with uid = "+uid +" does not match returned uid of "+jsonObject.get("user"));
                        }
                        Intent toExistingOrder = new Intent(getApplicationContext(),ExistingOrder.class);
                        toExistingOrder.putExtra("json-object",jsonObject.toString());
                        if(jsonObject.getBoolean("confirmed"))
                        {
                            FoodKing.status="Order Confirmed";
                        }
                        if(jsonObject.getBoolean("sent"))
                        {
                            FoodKing.status="Order has been Dispatched.";
                        }
                        finish();
                        startActivity(toExistingOrder);


                    }
                    else if (jsonObject.get("state").equals("no-order-found"))
                    {
                        Intent toFoodCart = new Intent(getApplicationContext(),FoodCart.class);
                        startActivity(toFoodCart);
                        finish();
                    }  else if(jsonObject.get("state").equals("timeout"))
                    {
                        Toast.makeText(getApplicationContext(),"Unable to establish connection with Server",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unknown error in loading menu. Please contact the administrator", Toast.LENGTH_LONG).show();
                        Log.e("Menu Loader","Returned Error");
                    }


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            ServerProgressBar.setProgress(progress);
        }
    }
    public class SubmitOrder extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try
            {
                  if(jsonObject.get("state").equals("order-successful"))
                  {
                      Toast.makeText(getApplicationContext(),"Placed Order Successfully",Toast.LENGTH_SHORT).show();
                      ServerProgressBar.setVisibility(View.INVISIBLE);
                      Status.setText("Placed Order Successfully");
                      JSONObject jsonObject1 = new JSONObject();
                      try
                      {

                          jsonObject1.put("type","get-order");
                          jsonObject1.put("uid",uid);
                      }
                      catch (JSONException e)
                      {
                          e.printStackTrace();
                      }
                      GetOrder getOrder = new GetOrder();
                      getOrder.execute(jsonObject1);
                      FoodKing.status="Order Not Confirmed";

                  }
                  else if(jsonObject.get("state").equals("invalid-request"))
                  {
                        Toast.makeText(getApplicationContext(),"Error placing order",Toast.LENGTH_LONG).show();
                      ServerProgressBar.setVisibility(View.INVISIBLE);
                  }
                else if(jsonObject.get("state").equals("menu-error"))
                  {
                      Toast.makeText(getApplicationContext(),"Menu has been changed \n Item not available",Toast.LENGTH_LONG).show();
                      ServerProgressBar.setVisibility(View.INVISIBLE);
                  }
                else if (jsonObject.get("state").equals("invalid-user"))
                  {
                      Toast.makeText(getApplicationContext(),"User is not valid \n Login Again",Toast.LENGTH_LONG).show();
                      ServerProgressBar.setVisibility(View.INVISIBLE);
                      PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("uid").apply();
                      Intent toLogin = new Intent(getApplicationContext(),Login.class);
                      activity.finish();
                      startActivity(toLogin);

                  }
                else if (jsonObject.get("state").equals("order-already-exists"))
                  {JSONObject jsonObject1 = new JSONObject();
                      try
                      {

                          jsonObject1.put("type","get-order");
                          jsonObject1.put("uid",uid);
                      }
                      catch (JSONException e)
                      {
                          e.printStackTrace();
                      }
                      GetOrder getOrder = new GetOrder();
                      getOrder.execute(jsonObject1);
                      Toast.makeText(getApplicationContext(),"Order has already been placed",Toast.LENGTH_LONG).show();
                      ServerProgressBar.setVisibility(View.VISIBLE);
                      Status.setText("Retrieving Order...");
                      ServerProgressBar.setProgress(0);


                  }
                  else if(jsonObject.get("state").equals("timeout"))
                  {
                      Toast.makeText(getApplicationContext(),"Unable to establish connection with Server",Toast.LENGTH_SHORT).show();
                  }



            }
            catch (Exception e)
              {
                Toast.makeText(getApplicationContext(),"Error Communicating with Server",Toast.LENGTH_SHORT).show();
            }



        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            ServerProgressBar.setProgress(progress);
        }




    }
}
