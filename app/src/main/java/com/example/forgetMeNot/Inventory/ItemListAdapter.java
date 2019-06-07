package com.example.forgetMeNot.Inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.forgetMeNot.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ItemListAdapter extends ArrayAdapter<Item> {
    private Context mContext;
    int mResource;

    public ItemListAdapter(Context context, int resource, ArrayList<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String expiry = getItem(position).getExpiry();
        boolean purchase = getItem(position).isPurchase();

        Item item = new Item(name, expiry, purchase);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvItem = (TextView) convertView.findViewById(R.id.item_textView);
        TextView tvExpiry = (TextView) convertView.findViewById(R.id.expiry_textView);
        Switch switchPurchase = (Switch) convertView.findViewById(R.id.purchase_switch);

        tvItem.setText(name);
        tvExpiry.setText(expiry);
        switchPurchase.setChecked(purchase);

        return convertView;
    }
}
