package com.example.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by sankarmanoj on 8/14/15.
 */
public class JSONServerComm extends AsyncTask<JSONObject,String,JSONObject>{
    public final String TAG="JSONServerComm";
    Context context;
    Activity activity;
    Button register;
    EditText email;
    public JSONServerComm(Context context,Button register, EditText email,Activity activity)
    {
        this.context=context;
        this.activity=activity;
        this.register = register;
        this.email = email;
    }
    public JSONServerComm()
    {
        this.context=null;
        this.activity=null;
        this.register=null;
        this.email=null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);

    }


    @Override
    protected JSONObject doInBackground(JSONObject... params) {
        String result= new String();
        String error=new String();
        HttpsURLConnection connection=null;
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
            HttpPost post = new HttpPost(URI.create(url.toString()));
            String postParameters=createQueryStringForParameters(params[0]);
            connection.setFixedLengthStreamingMode(postParameters.getBytes().length);
            connection.connect();
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(postParameters);
            out.close();
            int reply=connection.getResponseCode();
            Log.i(TAG,"Reply from Server="+String.valueOf(reply));

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
            }

            Log.i(TAG,result);

        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            Log.i(TAG, "URL Error");
        }
        catch (IOException e)
        {
            Toast.makeText(context.getApplicationContext(),"Server is not responding",Toast.LENGTH_LONG).show();
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


        JSONObject replyFromServer=null;
        try {

            replyFromServer = new JSONObject(result);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.i(TAG,"JSON Error in parsing reply");
        }

        return replyFromServer;
    }
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    public static String createQueryStringForParameters(JSONObject parameters) {
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
                            .append(URLEncoder.encode(parameters.getString(key),"UTF-8"));

                }catch(Exception e)
                    {
                       e.printStackTrace();
                    }

                firstParameter = false;
            }
        }
        Log.i("Build POST Response",parametersAsQueryString.toString());
        return parametersAsQueryString.toString();
    }
}
