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
public class FoodArrayAdapter extends ArrayAdapter<FoodItem> {
    Context context;
    int resource;
    static class ViewHolder {
        public TextView price;
        public TextView name;
    }

    public FoodArrayAdapter(Context context, int resource, List<FoodItem> objects) {

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
            rowView=inflater.inflate(R.layout.fooditemlist,null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name=(TextView)rowView.findViewById(R.id.titleTextView);
            viewHolder.price=(TextView)rowView.findViewById(R.id.priceTextView);
            rowView.setTag(viewHolder);
        }
        FoodItem item = getItem(position);
        ViewHolder viewHolder = (ViewHolder)rowView.getTag();
        viewHolder.name.setText(item.name);
        viewHolder.price.setText(item.getPrice());
        return rowView;
    }
}
