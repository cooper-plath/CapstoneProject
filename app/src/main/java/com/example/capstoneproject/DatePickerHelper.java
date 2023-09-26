package com.example.capstoneproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
//Class function that enables date button dialog pop up and display text
public class DatePickerHelper {
    public static void initDateButton(final Button button, final Context context) {
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = " " + day + " / " + month + " / " + year;
                button.setText(date);
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int style = AlertDialog.THEME_HOLO_LIGHT;
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, style, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
    }
}
