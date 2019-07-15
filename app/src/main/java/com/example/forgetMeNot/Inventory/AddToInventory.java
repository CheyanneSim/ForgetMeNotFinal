package com.example.forgetMeNot.Inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forgetMeNot.R;
import com.example.forgetMeNot.barcodeScanner.mainBarcodeAct;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddToInventory extends AppCompatDialogFragment {

    private EditText food;
    private EditText expiry;
    private DialogListener listener;
    private SimpleDateFormat formatter;
    Button scannerBtn;
    //boolean clicked = false;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public  Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        formatter = new SimpleDateFormat("dd/MM/yy");
        formatter.setLenient(false);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_to_inventory_dialog, null);

        builder.setView(view)
                .setTitle("Add To Inventory")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add Food", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toAdd = food.getText().toString();
                        try {
                            if (toAdd.equals("")) {
                                Toast.makeText(getContext(), "Please enter your food item!", Toast.LENGTH_LONG).show();
                            } else if (expiry.getText().toString().equals("")) {
                                Toast.makeText(getContext(), "Please enter the expiry date!", Toast.LENGTH_LONG).show();
                            } else {
                                Date date = formatter.parse(expiry.getText().toString());
                                listener.addItem(toAdd, date);
                            }
                        } catch (ParseException e) {
                            Toast.makeText(getContext(), "Please check your date input!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        scannerBtn = view.findViewById(R.id.scanButton);
        food = view.findViewById(R.id.non_essential_item_editText);
        expiry = view.findViewById(R.id.expiry_editText);

        scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), mainBarcodeAct.class);

                startActivityForResult(intent, 0);
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
        void addItem(String food, Date expiry);
    }
}
