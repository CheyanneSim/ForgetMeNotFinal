package com.example.forgetMeNot.expiry;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.forgetMeNot.R;

public class EditExpiryDialog extends DialogFragment {

    private String item;
    private TextView cancel, delete, update;
    private EditText expiry;
    private DialogListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_expiry_fragment,container, false);
        getDialog().setTitle(item);
        cancel = (TextView) view.findViewById(R.id.cancel);
        delete = (TextView) view.findViewById(R.id.delete);
        update = (TextView) view.findViewById(R.id.update);
        expiry = (EditText) view.findViewById(R.id.expiry_editText);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete(item);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expiryDate = expiry.getText().toString();
                listener.update(item, expiryDate);
            }
        });

        return view;
    }

    @SuppressLint("ValidFragment")
    public EditExpiryDialog(String item) {
        this.item = item;
    }

    public EditExpiryDialog() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) getTargetFragment();
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }

    public interface DialogListener {
        void delete(String item);
        void update(String item, String expiry);
    }
}
