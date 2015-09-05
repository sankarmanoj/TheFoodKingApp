package com.sankarmanoj.thefoodkingapp;

import java.net.URI;

/**
 * Created by sankarmanoj on 8/13/15.
 */
public class FoodItem {
    public enum Kind
    {
        COLD_DRINK,HOT_DRINK,HOT_EATABLE,COLD_EATABLE
    }
    FoodItem(String name,int price,int maxQty,int id)
    {
        this.name=name;
        this.id=id;
        this.price=price;
        this.inCartQuantity=0;
        this.quantity=maxQty;
    }

    public void setInCartQuantity(String inCartQuantity) {
        this.inCartQuantity = Integer.parseInt(inCartQuantity);
    }

    Kind TypeOfFood;
    int quantity;
    int inCartQuantity;
    int price;
    int id;
    String name;
    URI image;
    public String getPrice()
    {
        return String.valueOf(price);
    }

}
