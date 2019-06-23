package com.example.forgetMeNot.necessities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.forgetMeNot.R;

import java.util.ArrayList;

public class NecessitiesAdapter extends ArrayAdapter<Necessity> {
    private Context mContext;
    int mResource;

    public NecessitiesAdapter(Context context, int resource, ArrayList<Necessity> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String name = getItem(position).getName();
        String expiry = getItem(position).getExpiry();
        boolean available = getItem(position).getAvailability();
/*
        Necessity item;
        if (expiry.equals("N.A.")) {
            item = new NecessityNonFood(name, available);
        } else {
            item = new NecessityFood(name, expiry, available);
        }
        */
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvItem = (TextView) convertView.findViewById(R.id.item_tv);
        TextView tvExpiry = (TextView) convertView.findViewById(R.id.expiry_tv);
        TextView tvAvailable = (TextView) convertView.findViewById(R.id.availability_tv);

        String availability;
        if (available) {
            availability = "Available";
        } else {
            availability = "Not Available";
        }
        tvItem.setText(name);
        tvExpiry.setText(expiry);
        tvAvailable.setText(availability);

        return convertView;
    }
}
