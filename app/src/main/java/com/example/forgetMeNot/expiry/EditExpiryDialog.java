package com.example.forgetMeNot.expiry;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forgetMeNot.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditExpiryDialog extends DialogFragment {

    private String item, currentExpiry;
    private TextView cancel, delete, update;
    private TextView expiry;
    private DialogListener listener;
    private SimpleDateFormat formatter;
    private Button changeExpiry;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_expiry_fragment,container, false);
        getDialog().setTitle(item);
        cancel = view.findViewById(R.id.cancel);
        delete = view.findViewById(R.id.delete);
        update = view.findViewById(R.id.update);
        expiry = view.findViewById(R.id.expiry_textView);
        changeExpiry = view.findViewById(R.id.change_expiry_btn);
        formatter = new SimpleDateFormat("dd/MM/yy");
        formatter.setLenient(false);
        expiry.setText(currentExpiry);

        changeExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year, month, day;
                try {
                    Date currentExpiryDate = formatter.parse(currentExpiry);
                    cal.setTime(currentExpiryDate);
                } catch (ParseException e) {
                }
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

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
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
                dismiss();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date expiryDate = formatter.parse(expiry.getText().toString());
                    listener.update(item, expiryDate);
                    Thread.sleep(1500);
                } catch (ParseException e) {
                    Toast.makeText(getContext(), "Please choose the expiry date!", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
                dismiss();
            }
        });

        return view;
    }

    @SuppressLint("ValidFragment")
    public EditExpiryDialog(String item, String currentExpiry) {
        this.item = item;
        this.currentExpiry = currentExpiry;
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
        void update(String item, Date expiry);
    }
}
