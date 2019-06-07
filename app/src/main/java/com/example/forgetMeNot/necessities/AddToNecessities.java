package com.example.forgetMeNot.necessities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.forgetMeNot.R;

public class AddToNecessities extends AppCompatDialogFragment {

    private EditText item;
    private CheckBox food;
    private CheckBox available;
    private DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_to_necessities_dialog, null);

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
                        listener.addItem(toAdd, isFood, isAvailable);
                    }
                });

        item = view.findViewById(R.id.item_to_add_editText);
        food = view.findViewById(R.id.food_checkBox);
        available = view.findViewById(R.id.availability_checkbox);
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
        void addItem(String item, boolean isFood, boolean isAvailable);
    }
}
