package com.example.handymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

/**@author ivanw845
 * **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        // Initialize buttons and search view
        Button buttonNewEvent = findViewById(R.id.buttonNewEvent);
        Button buttonMyEvents = findViewById(R.id.buttonMyEvents);
        Button buttonAccessCalendar = findViewById(R.id.buttonCalendar);
        Button buttonMyAccount = findViewById(R.id.buttonMyAccount);
       //  searchView = findViewById(R.id.searchView);

     /*  menuIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openCustomMenu();
            }
        });

      */

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


        // Set up search functionality
       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                // Implement your logic here to search Firebase based on 'query'
                // Show the search results activity or update the current activity
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text change (optional)
                // You can implement live search functionality here
                return false;
            }
        });

        */

    }

    // Method to handle opening the custom menu
 /*   private void openCustomMenu() {
        // Inflate the menu layout from ic_menu.xml
        View menuView = getLayoutInflater().inflate(R.layout.ic_menu, null);

        // Create a PopupMenu anchored to a specific view (e.g., a button or menu icon)
        PopupMenu popupMenu = new PopupMenu(this, menuView);

        // Set the menu layout
        popupMenu.setContentView(menuView);

        // Find buttons in the menu layout
        Button buttonAccount = menuView.findViewById(R.id.buttonAccount);
        Button buttonSignOut = menuView.findViewById(R.id.buttonSignOut);

        // Set click listeners for menu buttons
        buttonAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Account button click (e.g., navigate to account settings)
                // Example: startActivity(new Intent(MainMenu.this, AccountSettingsActivity.class));
                popupMenu.dismiss(); // Dismiss the popup menu after handling the click
            }
        });



        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Sign Out button click (e.g., sign out the user)
                // Example: signOutUser();
                popupMenu.dismiss(); // Dismiss the popup menu after handling the click
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }

  */

}
