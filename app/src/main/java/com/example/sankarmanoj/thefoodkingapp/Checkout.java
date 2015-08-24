package com.example.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class Checkout extends Activity {
    final public String TAG="CheckoutActivity";
    ProgressBar ServerProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        String uid;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        uid=sharedPreferences.getString("uid","null");
        ServerProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        List<FoodItem> order=new ArrayList<>();
        final List<FoodItem> list=FoodKing.FoodMenu;
        if((list)!=null) {
            for (int i = 0; i < (list.size()); i++) {
                if(list.get(i).inCartQuantity>0) {
                    order.add(list.get(i));

                }
            }
        }
        try
        {

            JSONArray orderJSONArray = new JSONArray();
            for (FoodItem x : order)
            {
                JSONObject a = new JSONObject();
                a.put("name",x.name);
                a.put("quantity",x.inCartQuantity);
                a.put("id",x.id);
                orderJSONArray.put(a);

            }
            JSONObject toSend = new JSONObject();
            toSend.put("type","create-order");
            toSend.put("uid",uid);
            toSend.put("order",orderJSONArray);
            SubmitOrder toServer = new SubmitOrder();
            toServer.execute(toSend);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.e(TAG,"JSON Failed!");

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
    public class SubmitOrder extends AsyncTask<JSONObject,Integer,JSONObject>
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Toast.makeText(getApplicationContext(),"Successfully made order",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            String result= new String();
            HttpsURLConnection connection;
            try {
                URL url = new URL("https://sankar-manoj.com/request/");


                connection = (HttpsURLConnection) url.openConnection();
                SSLContext sc;
                sc = SSLContext.getInstance("TLS");
                sc.init(null, null, new java.security.SecureRandom());
                connection.setSSLSocketFactory(sc.getSocketFactory());
                connection.setReadTimeout(7000);
                connection.setConnectTimeout(7000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                String postParameters=createQueryStringForParameters(params[0]);
                connection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                connection.connect();
                this.publishProgress(50);
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(postParameters);
                out.close();
                int reply=connection.getResponseCode();
                Log.i(TAG,"Reply from Server="+String.valueOf(reply));

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String inputLine;
                this.publishProgress(70);
                while ((inputLine = in.readLine()) != null) {
                    result += inputLine;
                }
                JSONObject replyFromServer=null;
                try {

                    replyFromServer = new JSONObject(result);
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                    Log.i(TAG,"JSON Error in parsing reply");
                }
                Log.i(TAG,result);
                this.publishProgress(100);
                return replyFromServer;


            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                Log.i(TAG, "URL Error");
            }
            catch (IOException e)
            {

                e.printStackTrace();
                Log.i(TAG,"Error Connecting to Server");
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
                Log.i(TAG,"Algorithm Error");
            }
            catch (KeyManagementException e)
            {
                e.printStackTrace();
            }
            return null;



        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            ServerProgressBar.setProgress(progress);
        }
        private static final char PARAMETER_DELIMITER = '&';
        private static final char PARAMETER_EQUALS_CHAR = '=';


        public  String createQueryStringForParameters(JSONObject parameters) {
            StringBuilder parametersAsQueryString = new StringBuilder();
            Iterator<String > iterator = parameters.keys();

            if (parameters != null) {
                boolean firstParameter = true;

                while(iterator.hasNext())
                { String key=iterator.next();
                    if (!firstParameter) {
                        parametersAsQueryString.append(PARAMETER_DELIMITER);
                    }
                    try {
                        parametersAsQueryString.append(key)
                                .append(PARAMETER_EQUALS_CHAR)
                                .append(URLEncoder.encode(parameters.getString(key), "UTF-8"));

                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    firstParameter = false;
                }
            }
            Log.i("Build POST Response", parametersAsQueryString.toString());
            return parametersAsQueryString.toString();
        }
    }
}
