package com.example.forgetMeNot.Inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forgetMeNot.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class ItemListAdapter extends ArrayAdapter<Item> {
    private Context mContext;
    int mResource;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference extraShoppingListCollection;
    private SimpleDateFormat formatter;


    public ItemListAdapter(Context context, int resource, ArrayList<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String group = sharedPreferences.getString(GROUP, "");
        extraShoppingListCollection = db.collection("Groups").document(group).collection("Shopping List");

        final String name = getItem(position).getName();
        Date expiry = getItem(position).getExpiry();
        boolean purchase = sharedPreferences.getBoolean(name, false);
        formatter = new SimpleDateFormat("dd/MM/yy");

        Log.d("Log back on", "" + purchase);

        Item item = new Item(name, expiry, purchase);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvItem = (TextView) convertView.findViewById(R.id.item_textView);
        TextView tvExpiry = (TextView) convertView.findViewById(R.id.expiry_textView);
        Switch switchPurchase = (Switch) convertView.findViewById(R.id.purchase_switch);

        tvItem.setText(name);
        if (expiry == null) {
            tvExpiry.setText("N.A.");
        } else {
            tvExpiry.setText(formatter.format(expiry));
        }
        switchPurchase.setChecked(purchase);


        switchPurchase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Add to the extra shopping list
                    Map<String, Object> data = new HashMap<>();
                    data.put("Item", name);
                    // If it's not food, it will be part of Necessities
                    data.put("Is Food", true);
                    extraShoppingListCollection.document(name).set(data);
                    Toast.makeText(mContext, name + " has been added to your shopping list", Toast.LENGTH_LONG).show();
                } else {
                    extraShoppingListCollection.document(name).delete();
                    Toast.makeText(mContext, name + " has been removed your shopping list", Toast.LENGTH_LONG).show();

                }
                editor.putBoolean(name, isChecked);
                editor.apply();
            }
        });

        return convertView;
    }
}
