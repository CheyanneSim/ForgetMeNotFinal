package com.example.forgetMeNot.Inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forgetMeNot.R;

public class AddToInventory extends AppCompatDialogFragment {

    private EditText food;
    private EditText expiry;
    private DialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
                        String date = expiry.getText().toString();
                        if (toAdd.equals("")) {
                            Toast.makeText(getContext(), "Please enter your food item!", Toast.LENGTH_LONG).show();
                        } else if (date.equals("")) {
                            Toast.makeText(getContext(), "Please enter the expiry date!", Toast.LENGTH_LONG).show();
                        } else {
                            listener.addItem(toAdd, date);

                        }
                    }
                });

        food = view.findViewById(R.id.non_essential_item_editText);
        expiry = view.findViewById(R.id.expiry_editText);
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
        void addItem(String food, String expiry);
    }
}
