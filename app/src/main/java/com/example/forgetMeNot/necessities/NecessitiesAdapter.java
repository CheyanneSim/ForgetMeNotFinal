package com.example.forgetMeNot.necessities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.forgetMeNot.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NecessitiesAdapter extends ArrayAdapter<Necessity> {
    private Context mContext;
    int mResource;
    private SimpleDateFormat formatter;

    public NecessitiesAdapter(Context context, int resource, ArrayList<Necessity> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String name = getItem(position).getName();
        Date expiry = getItem(position).getExpiry();
        boolean available = getItem(position).getAvailability();
        formatter = new SimpleDateFormat("dd/MM/yy");

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
        if (expiry == null) {
            tvExpiry.setText("Nil");
        } else {
            tvExpiry.setText(formatter.format(expiry));
        }
        tvAvailable.setText(availability);

        return convertView;
    }
}
