package com.example.handymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        // Initialize buttons and search view
        Button buttonNewEvent = findViewById(R.id.buttonNewEvent);
        Button buttonMyEvents = findViewById(R.id.buttonMyEvents);
        Button buttonAccessCalendar = findViewById(R.id.buttonCalendar);
        Button buttonMyAccount = findViewById(R.id.buttonMyAccount);

        // Set click listeners for buttons
        buttonNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to NewEvent activity
                startActivity(new Intent(MainMenu.this, NewEvent.class));
            }
        });
        buttonMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to NewEvent activity
                startActivity(new Intent(MainMenu.this, AccountActivity.class));
            }
        });

        buttonMyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to calendar activity (My Events)
                startActivity(new Intent(MainMenu.this, MyEvents.class));
            }
        });

        buttonAccessCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to calendar activity (Access Calendar)
                startActivity(new Intent(MainMenu.this, calendar.class));
            }
        });


    }

}
