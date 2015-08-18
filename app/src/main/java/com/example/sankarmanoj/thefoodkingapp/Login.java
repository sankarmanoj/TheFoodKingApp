package com.example.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
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

import org.json.JSONObject;

import android.app.DialogFragment;
public class Login extends Activity {

    EditText EmailET;
    Button RegisterButton;
    EditText NameET;
    EditText Password;
    EditText ConfirmPass;
    public final String TAG="LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String uid = sharedPreferences.getString("uid","null");
        if (!uid.equals("null"))
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RegisterButton = (Button)findViewById(R.id.registerButton);
        EmailET=(EditText)findViewById(R.id.emailEdit);

        Password=(EditText)findViewById(R.id.passEditText);
        ConfirmPass=(EditText)findViewById(R.id.confirmEdit);

        NameET=(EditText)findViewById(R.id.nameEditText);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConfirmPass.getText().toString().equals(Password.getText().toString())) {
                    String mailName = EmailET.getText().toString();
                    if(mailName.length()>0)
                    if (mailName.charAt(mailName.length() - 1) == ' ') {
                        mailName = mailName.substring(0, mailName.length() - 1);

                    }
                    if (mailName.equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter an email address", Toast.LENGTH_SHORT).show();
                    } else if (isEmailValid(mailName)) {

                        JSONServerComm register = new JSONServerComm(getApplicationContext(), RegisterButton, EmailET, getParent());
                        JSONObject toSend = new JSONObject();
                        try {
                            toSend.put("name", NameET.getText());
                            toSend.put("email", EmailET.getText());
                            toSend.put("type", "add_new_user");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        register.execute(toSend);
                        RegisterButton.setEnabled(false);
                    } else {
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
    public static class RegorLogin extends DialogFragment
    {

    }
}
