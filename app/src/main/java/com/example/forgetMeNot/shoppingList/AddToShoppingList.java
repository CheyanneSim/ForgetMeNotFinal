package com.example.forgetMeNot.shoppingList;

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
import android.widget.Toast;

import com.example.forgetMeNot.R;

public class AddToShoppingList extends AppCompatDialogFragment {
    private EditText item;
    private CheckBox isFood;
    private DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_to_shoppinglist_dialog, null);

        builder.setView(view)
                .setTitle("Add To Shopping List")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toAdd = item.getText().toString();
                        boolean food = isFood.isChecked();
                        if (toAdd.equals("")) {
                            Toast.makeText(getContext(), "Please input item!", Toast.LENGTH_LONG).show();
                        } else {
                            listener.addItem(toAdd, food);
                        }
                    }
                });

        item = view.findViewById(R.id.item_to_add_editText);
        isFood = view.findViewById(R.id.isFood_checkbox);
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
        void addItem(String item, boolean isFood);
    }
}
