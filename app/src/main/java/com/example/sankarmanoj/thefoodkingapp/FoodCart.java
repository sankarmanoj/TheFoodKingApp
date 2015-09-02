package com.example.sankarmanoj.thefoodkingapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FoodCart extends Activity {
    List<FoodItem> tempList;
    ListView listView;
    EditText Address;
    Activity activity;
    EditText Comments;
    TextView GrandTotal;
    FoodCartArrayAdapter foodArrayAdapter;
    Button Checkout;
    TextView ItemsTotal;
    int grandTotal=0;
    public final String TAG="FoodCart";
    Context context;
    EditText PhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        tempList=new ArrayList<>();
        final List<FoodItem> list=FoodKing.FoodMenu;
        if((list)!=null) {
            for (int i = 0; i < (list.size()); i++) {
                if(list.get(i).inCartQuantity>0) {
                    tempList.add(list.get(i));
                    grandTotal+=(list.get(i).price)*list.get(i).inCartQuantity;
                }
            }
        }
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_foodcart);
        activity = this;
        GrandTotal=(TextView)findViewById(R.id.grandTotalTextView);
        ItemsTotal=(TextView)findViewById(R.id.itemsTotalTextView);
        if(tempList.size()>1) {
            ItemsTotal.setText(String.valueOf(tempList.size()) + " Items");
        }
        else
        {
            ItemsTotal.setText(String.valueOf(tempList.size())+ " Item");
        }
        context=this;
        Comments=(EditText)findViewById(R.id.commentsEditText);
        Address=(EditText)findViewById(R.id.addressEditText);
        PhoneNumber=(EditText)findViewById(R.id.phoneEditText);
        listView=(ListView)findViewById(R.id.listView2);
        GrandTotal.setText("Grand Total : ₹ "+String.valueOf(grandTotal));
        foodArrayAdapter = new FoodCartArrayAdapter(getApplicationContext(),R.layout.fooditemlist,tempList);
        listView.setAdapter(foodArrayAdapter);
        Checkout=(Button)findViewById(R.id.checkoutButton);
        if(tempList.size()==0)
        {
            Checkout.setEnabled(false);
        }
        Comments.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Comments.setText("");
                Comments.setTextColor(Color.rgb(0,0,0));
                return false;
            }
        });
        Address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Address.setText("");
                Address.setTextColor(Color.rgb(0,0,0));
                return false;
            }
        });
        final View.OnClickListener secondClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Address.getText().toString().length()<=1)
                {
                    Toast.makeText(getApplicationContext(),"Please fill in a valid Address",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    new AlertDialog.Builder(context).setTitle("Proceed").setMessage("Would you like to Confirm your Order?").setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), com.example.sankarmanoj.thefoodkingapp.Checkout.class);
                            intent.putExtra("action","place-order");
                            intent.putExtra("address",Address.getText().toString());
                            String comments = Comments.getText().toString();
                            intent.putExtra("comments",comments);
                            intent.putExtra("phone",PhoneNumber.getText().toString());
                            startActivity(intent);
                            activity.finish();

                            dialog.dismiss();
                        }
                    }).setNegativeButton("No",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).  show();

                }

            }
        };
        Checkout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrandTotal.setVisibility(View.GONE);
                ItemsTotal.setVisibility(View.GONE);
                final int pixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
                Log.i("Pixels",String.valueOf(pixels));

                Animation A = new Animation() {


                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)listView.getLayoutParams();
                        params.bottomMargin = (int)(pixels * interpolatedTime);
                        listView.setLayoutParams(params);
                        if(interpolatedTime==1)
                        {
                            listView.setBackground(null);
                            Checkout.setText("Next");
                            Address.setVisibility(View.VISIBLE);

                            Comments.setVisibility(View.VISIBLE);



                        }
                    }
                };
                A.setDuration(300);
                listView.startAnimation(A);

                Checkout.setOnClickListener(secondClickListener);
                Log.i("FoodCart","Checkout Clicked");
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }


}
