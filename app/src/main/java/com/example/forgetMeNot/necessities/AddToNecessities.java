package com.example.forgetMeNot.necessities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.forgetMeNot.Notification.Alarm;
import com.example.forgetMeNot.OCR.OCR;
import com.example.forgetMeNot.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AddToNecessities extends AppCompatDialogFragment {

    private EditText item;
    private Button expiry;
    private CheckBox food;
    private CheckBox available;
    private ImageButton scanFood;
    private DialogListener listener;
    private SimpleDateFormat formatter;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_to_necessities_dialog, null);

        item = view.findViewById(R.id.item_to_add_editText);
        scanFood = view.findViewById(R.id.scan_food);
        food = view.findViewById(R.id.food_checkBox);
        available = view.findViewById(R.id.availability_checkbox);
        expiry = view.findViewById(R.id.choose_expiry_btn);
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

        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                expiry.setText(date);
            }
        };

        scanFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), OCR.class), 0);
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
                            try {
                                Date expiryDate = formatter.parse(expiry.getText().toString());
                                listener.addItem(toAdd, expiryDate, isFood, isAvailable);
                            } catch (ParseException e) {
                                Toast.makeText(getContext(), "Please choose an expiry date!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            listener.addItem(toAdd, null, isFood, isAvailable);
                        }
                    }
                });


        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String text = data.getStringExtra("Recognised Text");
            switch (requestCode) {
                case 0:
                    item.setText(text);
                    break;
            }
        }
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
