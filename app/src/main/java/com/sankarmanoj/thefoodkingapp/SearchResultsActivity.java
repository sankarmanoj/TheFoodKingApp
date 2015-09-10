package com.sankarmanoj.thefoodkingapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prudhvirampey on 10/09/15.
 */
public class SearchResultsActivity extends Activity {

    ListView listView;
    FoodArrayAdapter foodArrayAdapter;
    List<FoodItem> FoodArray;
    Button doneButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        FoodArray = new ArrayList<>();

        handleIntent(getIntent());
        listView = (ListView) findViewById(R.id.listView1);
        foodArrayAdapter = new FoodArrayAdapter(getApplicationContext(), R.layout.fooditemlist, FoodArray);
        listView.setAdapter(foodArrayAdapter);
        doneButton = (Button) findViewById(R.id.doneButton);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }


        });
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
        for(int i=0;i<FoodKing.FoodMenu.size();i++)
        {
            if(FoodKing.FoodMenu.get(i).name.toLowerCase().contains(query.toLowerCase()))
            {
                FoodArray.add(FoodKing.FoodMenu.get(i));
            }
        }

    }
}