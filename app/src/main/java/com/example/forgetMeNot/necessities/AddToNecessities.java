package com.example.forgetMeNot.necessities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forgetMeNot.Notification.Alarm;
import com.example.forgetMeNot.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddToNecessities extends AppCompatDialogFragment {

    private EditText item, expiry;
    private CheckBox food;
    private CheckBox available;
    private DialogListener listener;
    private SimpleDateFormat formatter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_to_necessities_dialog, null);

        item = view.findViewById(R.id.item_to_add_editText);
        food = view.findViewById(R.id.food_checkBox);
        available = view.findViewById(R.id.availability_checkbox);
        expiry = view.findViewById(R.id.expiry_editText);
        formatter = new SimpleDateFormat("dd/MM/yy");
        formatter.setLenient(false);

        // Determines whether or not to show expiry
        food.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (available.isChecked() && isChecked) {
                    expiry.setVisibility(View.VISIBLE);
                } else {
                    expiry.setVisibility(View.GONE);
                }
            }
        });

        available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (food.isChecked() && isChecked) {
                    expiry.setVisibility(View.VISIBLE);
                } else {
                    expiry.setVisibility(View.GONE);
                }
            }
        });

        builder.setView(view)
                .setTitle("Add To Necessities")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toAdd = item.getText().toString();
                        boolean isFood = food.isChecked();
                        boolean isAvailable = available.isChecked();
                        if (toAdd.equals("")) {
                            Toast.makeText(getContext(), "Please enter your item!", Toast.LENGTH_LONG).show();
                        } else if (isFood && isAvailable) {
                            if (expiry.getText().toString().equals("")) {
                                    Toast.makeText(getContext(), "Please enter the expiry date!", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    Date expiryDate = formatter.parse(expiry.getText().toString());
                                    listener.addItem(toAdd, expiryDate, isFood, isAvailable);
                                    // Set alarm
                                    Alarm.setAlarm(getContext(), expiryDate, toAdd, true, toAdd.hashCode());
                                } catch (ParseException e) {
                                    Toast.makeText(getContext(), "Please check your date input!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            listener.addItem(toAdd, null, isFood, isAvailable);
                        }
                    }
                });


        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }

    public interface DialogListener {
        void addItem(String item, Date expiry, boolean isFood, boolean isAvailable);
    }
}
