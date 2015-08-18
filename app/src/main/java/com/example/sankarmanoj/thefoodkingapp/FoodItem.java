package com.example.sankarmanoj.thefoodkingapp;

import java.net.URI;

/**
 * Created by sankarmanoj on 8/13/15.
 */
public class FoodItem {
    public enum Kind
    {
        COLD_DRINK,HOT_DRINK,HOT_EATABLE,COLD_EATABLE
    }
    FoodItem(String name,int price)
    {
        this.name=name;
        this.price=price;
    }
    Kind TypeOfFood;
    int price;
    String name;
    URI image;
    public String getPrice()
    {
        return String.valueOf(price);
    }

}
