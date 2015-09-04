package com.sankarmanoj.thefoodkingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sankarmanoj.thefoodkingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankarmanoj on 8/14/15.
 */
public class FoodArrayAdapter extends ArrayAdapter<FoodItem> {
    Context context;
    int resource;
    static class ViewHolder {
        public TextView price;
        public TextView name;
        public TextView quantity;
    }

    public FoodArrayAdapter(Context context, int resource, List<FoodItem> objects) {

        super(context, resource, objects);
        this.context = context;
        this.resource=resource;
    }
    public List<FoodItem> getAllElements()
    {
        List <FoodItem> toreturn = new ArrayList<>();
        for (int i = 0; i<getCount();i++)
        {
            toreturn.add(getItem(i));
        }
        return toreturn;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            rowView=inflater.inflate(R.layout.fooditemlist,null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name=(TextView)rowView.findViewById(R.id.titleTextView);
            viewHolder.price=(TextView)rowView.findViewById(R.id.priceTextView);
            viewHolder.quantity=(TextView)rowView.findViewById(R.id.quantityTextView);
            rowView.setTag(viewHolder);
        }
        final FoodItem item = getItem(position);
        ImageButton plusButton = (ImageButton) rowView.findViewById(R.id.plusImageButton);
        ImageButton minusButton = (ImageButton)rowView.findViewById(R.id.minusImageButton);
        final ViewHolder viewHolder = (ViewHolder)rowView.getTag();



            plusButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {
                        if ((item.inCartQuantity) < (item.quantity)) {
                            (item.inCartQuantity)++;
                            viewHolder.quantity.setText(Integer.toString(item.inCartQuantity));

                        } else
                            Log.d("Adapter", "Max quantity reached");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
         minusButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(item.inCartQuantity>0)
                 {
                     item.inCartQuantity--;
                     viewHolder.quantity.setText(Integer.toString(item.inCartQuantity));
                 }
             }
         });

        viewHolder.name.setText(item.name);
        viewHolder.price.setText("₹"+item.getPrice());
        viewHolder.quantity.setText(Integer.toString(item.inCartQuantity));
        return rowView;
    }
}
