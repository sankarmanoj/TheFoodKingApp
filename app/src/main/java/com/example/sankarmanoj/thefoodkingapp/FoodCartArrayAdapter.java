package com.example.sankarmanoj.thefoodkingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sankarmanoj on 8/14/15.
 */
public class FoodCartArrayAdapter extends ArrayAdapter<FoodItem> {
    Context context;
    int resource;
    static class ViewHolder {
        public TextView price;
        public TextView name;
        public TextView quantity;
        public TextView total;
    }

    public FoodCartArrayAdapter(Context context, int resource, List<FoodItem> objects) {

        super(context, resource, objects);
        this.context = context;
        this.resource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            rowView=inflater.inflate(R.layout.foodcartitem,null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name=(TextView)rowView.findViewById(R.id.titleTextView);
            viewHolder.price=(TextView)rowView.findViewById(R.id.priceTextView);
            viewHolder.quantity=(TextView)rowView.findViewById(R.id.quantityTextView);
            viewHolder.total=(TextView)rowView.findViewById(R.id.totalTextView);

            rowView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)rowView.getTag();
        final FoodItem item = getItem(position);




        viewHolder.name.setText(item.name);
        viewHolder.price.setText(item.getPrice());
        viewHolder.quantity.setText(String.valueOf(item.inCartQuantity));
        viewHolder.total.setText("₹"+Integer.toString((item.inCartQuantity) * (item.price)));
        return rowView;
    }
}
