package com.example.handymobileapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class calendar extends AppCompatActivity {

    private TextView selectedDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendarview);

        selectedDateTextView = findViewById(R.id.textView);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                updateSelectedDate(selectedCalendar);
            }
        });

        // Initialize selected date to current date
        Calendar currentCalendar = Calendar.getInstance();
        updateSelectedDate(currentCalendar);
    }

    private void updateSelectedDate(Calendar selectedCalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        String selectedDateString = sdf.format(selectedCalendar.getTime());
        selectedDateTextView.setText(selectedDateString);
    }
}