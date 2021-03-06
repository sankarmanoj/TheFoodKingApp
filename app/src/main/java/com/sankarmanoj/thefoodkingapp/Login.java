package com.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity {
    Boolean registered=false;
    EditText EmailET;
    Dialog dialog;
    Button RegisterButton;
    Button ForgetPassButton;
    EditText NameET;
    TextView NameTextView;
    AlertDialog LoadingLocation;
    EditText Password;
    Context ActivityContext;
    EditText ConfirmPass;
    Activity activity;
    ArrayAdapter locations;
    Spinner LocationSpinner;
    TextView LocationTV;
    public final String TAG="LoginActivity";

    @Override
    protected void onPause() {
        super.onPause();
        RegisterButton.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity=this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checkIntro",false);
        editor.commit();
        ActivityContext=this;
        FoodKing.registrationState=0;
        String uid = sharedPreferences.getString("uid","null");
        if (!uid.equals("null"))
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();

        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you have an account?");
            alertDialogBuilder.setPositiveButton("Yes - Login",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    registered=true;
                    Log.d(TAG,String.valueOf(registered));
                    RegisterButton.setText("Login");
                    LocationSpinner.setVisibility(View.INVISIBLE);
                    LocationTV.setVisibility(View.INVISIBLE);
                    ConfirmPass.setVisibility(View.INVISIBLE);
                    NameET.setVisibility(View.INVISIBLE);
                    NameTextView.setVisibility(View.INVISIBLE);
                    ForgetPassButton.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setNegativeButton("No - Register", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    registered=false;
                    ConfirmPass.setVisibility(View.VISIBLE);
                    AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                    adb.setTitle("Loading Locations..");
                    LoadingLocation=adb.create();
                    LoadingLocation.show();
                    RegisterButton.setEnabled(false);
                    ConfirmPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                ConfirmPass.setText("");
                                ConfirmPass.setOnFocusChangeListener(null);
                                ConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                ConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

                            }
                        }
                    });
                    dialog.dismiss();
                }
            });
            dialog=  alertDialogBuilder.create();
            dialog.show();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        RegisterButton = (Button)findViewById(R.id.registerButton);
        EmailET=(EditText)findViewById(R.id.emailEdit);
        ConfirmPass = (EditText) findViewById(R.id.confirmEdit);
        Password=(EditText)findViewById(R.id.passEditText);
        ForgetPassButton=(Button) findViewById(R.id.forgotPassButton);
        NameTextView = (TextView)findViewById(R.id.nameTextView);
        LocationSpinner = (Spinner)findViewById(R.id.locationSpinner);
        LocationTV=(TextView)findViewById(R.id.locationTextView);

        try {
            getActionBar().setDisplayShowTitleEnabled(false);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }


        locations = new ArrayAdapter(getApplicationContext(), R.layout.locationitem, FoodKing.singleton.Locations);
        LocationSpinner.setAdapter(locations);
        JSONObject getLocationCommand = new JSONObject();
        try{
            getLocationCommand.put("type","get-locations");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        GetLocation getLocation = new GetLocation();
        getLocation.execute(getLocationCommand);



        NameET=(EditText)findViewById(R.id.nameEditText);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConfirmPass.getText().toString().equals(Password.getText().toString())||registered) {
                    String mailName = EmailET.getText().toString();
                    if(mailName.length()>0)
                    if (mailName.charAt(mailName.length() - 1) == ' ') {
                        mailName = mailName.substring(0, mailName.length() - 1);

                    }
                    if (mailName.equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter an email address", Toast.LENGTH_SHORT).show();
                    }
                    else if (isEmailValid(mailName)) {

                        if(registered)
                        {
                           LoginServerComm login = new LoginServerComm();
                            JSONObject toSend = new JSONObject();
                            try {
                                toSend.put("name", NameET.getText());
                                toSend.put("email", EmailET.getText());
                                toSend.put("password", Password.getText());
                                toSend.put("type", "login_user");

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            login.execute(toSend);
                        }
                        else {
                            RegisterServerComm register = new RegisterServerComm(getApplicationContext(), RegisterButton, EmailET, getParent());

                            JSONObject toSend = new JSONObject();
                            try {
                                toSend.put("name", NameET.getText());
                                toSend.put("email", EmailET.getText());
                                toSend.put("password", Password.getText());
                                toSend.put("type", "add_new_user");
                                toSend.put("location",LocationSpinner.getSelectedItem().toString());

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            register.execute(toSend);

                        }
                        RegisterButton.setEnabled(false);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Passwords Don't Match",Toast.LENGTH_LONG).show();
                    ConfirmPass.setText("");
                }
            }

        });

        ForgetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String mailName = EmailET.getText().toString();
                    if(mailName.length()>0) {
                        if (mailName.charAt(mailName.length() - 1) == ' ') {
                            mailName = mailName.substring(0, mailName.length() - 1);

                        }
                    }
                    if (mailName.equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter an email address", Toast.LENGTH_SHORT).show();
                    }
                    else if (isEmailValid(mailName)) {

                        if(registered)
                        {
                            ResetPassword resetPassword = new ResetPassword();
                            JSONObject toSend = new JSONObject();
                            try {
                                toSend.put("email", EmailET.getText());
                                toSend.put("type", "forgot_pass");

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            resetPassword.execute(toSend);
                            ForgetPassButton.setEnabled(false);
                        }


                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG).show();
                    }

                }



        });
        EmailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    EmailET.setText("");
                    EmailET.setOnFocusChangeListener(null);
                }
            }
        });
        NameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    NameET.setText("");
                    NameET.setOnFocusChangeListener(null);
                }
            }
        });


        Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Password.setText("");
                    Password.setOnFocusChangeListener(null);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
        else if (id==R.id.action_switch)
        {
            recreate();
        }

        return super.onOptionsItemSelected(item);
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
        public class LoginServerComm extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(jsonObject==null)
            {
                Toast.makeText(getApplicationContext(),"Error Communicating With Server \n Please try again later",Toast.LENGTH_SHORT).show();
                RegisterButton.setEnabled(true);
            }
            else
                try
                {
                    if(jsonObject.getString("state").equals("logged-in"))
                    {
                        String uid = jsonObject.getString("uid");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sharedPreferences.edit().putString("uid",uid).apply();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        FoodKing.registrationState=1;
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();

                    }
                    else if (jsonObject.getString("state").equals("password-error"))
                    {
                        Toast.makeText(getApplicationContext(),"Password is incorrect",Toast.LENGTH_LONG).show();
                        ConfirmPass.setVisibility(View.INVISIBLE);
                        RegisterButton.setEnabled(true);
                        registered=true;

                    }
                    else if(jsonObject.getString("state").equals("not-registered"))
                    {
                        Toast.makeText(getApplicationContext(),"User does not exist",Toast.LENGTH_LONG).show();
                        RegisterButton.setEnabled(true);

                    }
                    else if(jsonObject.getString("state").equals("password-changed"))
                    {
                        Toast.makeText(getApplicationContext(),"Your password has been changed successfully",Toast.LENGTH_LONG).show();
                        ForgetPassButton.setEnabled(true);

                    }
                    else if(jsonObject.getString("state").equals("password-change-error"))
                    {
                        Toast.makeText(getApplicationContext(),"Sorry! There was a problem in changing your password. Please try again.",Toast.LENGTH_LONG).show();
                        ForgetPassButton.setEnabled(true);

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
        }
    }
    public class ResetPassword extends JSONServerComm
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            super.onPostExecute(jsonObject);
            if (jsonObject == null) {
                Toast.makeText(getApplicationContext(), "Error Communicating With Server \n Please try again later", Toast.LENGTH_SHORT).show();
            } else
                try {
                    if (jsonObject.get("state").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Password reset link has been sent to the registered Email Address", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.get("state").equals("not-registered")) {
                        Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                        ForgetPassButton.setEnabled(true);

                    }
                    else if(jsonObject.get("state").equals("timeout"))
                    {
                        Toast.makeText(getApplicationContext(),"Unable to establish connection with Server",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
    public  class GetLocation extends  JSONServerComm {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject == null) {
                Toast.makeText(getApplicationContext(), "Error Communicating With Server \n Please try again later", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    if (jsonObject.getString("state").equals("success")) {
                        LoadingLocation.dismiss();
                        JSONArray locationArray = new JSONArray(jsonObject.getString("locations"));
                        FoodKing.singleton.setUpLocations(locationArray);
                        locations.notifyDataSetChanged();
                        RegisterButton.setEnabled(true);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public class RegisterServerComm extends JSONServerComm
    {
        public RegisterServerComm(Context context,Button register, EditText email,Activity activity)
        {
            this.context=context;
            this.activity=activity;
            this.register = register;
            this.email = email;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(jsonObject==null)
            {
                Toast.makeText(getApplicationContext(),"Error Communicating With Server \n Please try again later",Toast.LENGTH_SHORT).show();
            }
            else
                try {
                    if (jsonObject.get("state").equals("success"))
                    {
                        String uid=jsonObject.getString("uid");
                        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
                        sharedPreferences.edit().putString("uid",uid).apply();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityContext);
                        builder.setTitle("A Registration Link has been sent to your Email Address");
                        builder.setMessage("Please click on it to register");
                        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog1= builder.create();
                        FoodKing.singleton.updateMenu();
                        dialog1.show();


                    }
                    else if (jsonObject.get("state").equals("already-exists"))
                    {
                        Toast.makeText(getApplicationContext(),"That Email has already been used",Toast.LENGTH_LONG).show();
                        register.setEnabled(true);
                    }
                    else if(jsonObject.get("state").equals("email-error"))
                    {
                        Toast.makeText(context,"Error in sending registration mail",Toast.LENGTH_LONG).show();
                        register.setEnabled(true);
                    }  else if(jsonObject.get("state").equals("timeout"))
                    {
                        Toast.makeText(getApplicationContext(),"Unable to establish connection with Server",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    register.setEnabled(true);

                    e.printStackTrace();
                }
        }

    }
}
