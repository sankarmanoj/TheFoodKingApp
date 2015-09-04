package com.sankarmanoj.thefoodkingapp;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;


public class FabIntro extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(new FirstSlide(), this);
        addSlide(new SecondSlide(), this);
    }
public void loadLogin()
{
    Intent i=new Intent(getApplicationContext(),Login.class);
    startActivity(i);
}
    @Override
    public void onSkipPressed() {
loadLogin();
    }

    @Override
    public void onDonePressed() {
loadLogin();
    }
}
