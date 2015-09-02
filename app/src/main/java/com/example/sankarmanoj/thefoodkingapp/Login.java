package com.example.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
public class Login extends Activity {
    Boolean registered=false;
    EditText EmailET;
    Dialog dialog;
    Button RegisterButton;
    EditText NameET;
    EditText Password;
    EditText ConfirmPass;
    public final String TAG="LoginActivity";

    @Override
    protected void onPause() {
        super.onPause();
        RegisterButton.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checkIntro",false);
        editor.commit();
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
                    ConfirmPass.setVisibility(View.INVISIBLE);
                    NameET.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setNegativeButton("No - Register", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    registered=false;
                    ConfirmPass.setVisibility(View.VISIBLE);
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


        getActionBar().setDisplayShowTitleEnabled(false);

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
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
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
                        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();

                    }
                    else if(jsonObject.get("state").equals("email-error"))
                    {
                        Toast.makeText(context,"Error in sending registration mail",Toast.LENGTH_LONG).show();
                        register.setEnabled(true);
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
