package com.example.handymobileapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import java.util.Calendar;

public class calendar extends AppCompatActivity{

    private int year1 = 0;
    private int month1 = 0;
    private int day1 = 0;
    private int total1 = 0;
    private int total2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendarview);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                year1 = i;
                month1 = i1 + 1;
                day1 = i2;
            }
        });
    }

    public void calculate(View view) {
        total1 = (year1 * 360) + (month1 * 30) + day1;

        System.out.println(total1);
        System.out.println(total2);

        TextView textView = findViewById(R.id.textView);

        if (total1 - total2 == 0 || total1 - total2 > 1)
            textView.setText((total1 - total2) + " Days After Today");
        else if (total1 - total2 < 0)
            textView.setText((total1 - total2) + " Days Before Today");
        else if (total1 - total2 == 1)
            textView.setText((total1 - total2) + " Day After Today");

        Calendar calendar = Calendar.getInstance();
        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH) + 1;
        int day2 = calendar.get(Calendar.DAY_OF_MONTH);
        total2 = (year2 * 360) + (month2 * 30) + day2;
    }
}


