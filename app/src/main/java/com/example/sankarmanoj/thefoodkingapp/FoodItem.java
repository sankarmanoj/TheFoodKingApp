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
    FoodItem(String name,int price,int maxQty)
    {
        this.name=name;
        this.price=price;
        this.inCartQuantity=0;
        this.quantity=maxQty;
    }
    Kind TypeOfFood;
    int quantity;
    int inCartQuantity;
    int price;
    String name;
    URI image;
    public String getPrice()
    {
        return String.valueOf(price);
    }

}
