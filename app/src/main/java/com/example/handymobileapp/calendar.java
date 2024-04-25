package com.example.handymobileapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class calendar extends AppCompatActivity {

    private TextView selectedDateTextView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendarview);

        selectedDateTextView = findViewById(R.id.textView);
        db = FirebaseFirestore.getInstance();

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                updateSelectedDate(selectedCalendar);

                // Fetch events for the selected date
                fetchEvents(selectedCalendar);
            }
        });

        // Initialize selected date to current date
        Calendar currentCalendar = Calendar.getInstance();
        updateSelectedDate(currentCalendar);
        fetchEvents(currentCalendar); // Fetch events for the current date initially
    }

    private void updateSelectedDate(Calendar selectedCalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        String selectedDateString = sdf.format(selectedCalendar.getTime());
        selectedDateTextView.setText(selectedDateString);
    }

    private void fetchEvents(Calendar selectedCalendar) {
        // Format selected date to match Firestore query format (yyyy-MM-dd)
        SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = firestoreDateFormat.format(selectedCalendar.getTime());

        // Get current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query Firestore for events on the selected date under the user's events subcollection
        db.collection("users").document(userId).collection("events")
                .whereEqualTo("date", formattedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        displayEvents(task.getResult());
                    } else {
                        Toast.makeText(calendar.this, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void displayEvents(QuerySnapshot querySnapshot) {
        List<Event> eventsList = new ArrayList<>();

        // Parse each document into Event object and add to the list
        for (QueryDocumentSnapshot document : querySnapshot) {
            String title = document.getString("title");
            String time = document.getString("time");
            String description = document.getString("description");

            // Parse time string into a Date object for sorting
            Date eventTime = parseTimeString(time);

            // Create Event object and add to list
            Event event = new Event(title, eventTime, description);
            eventsList.add(event);
        }

        // Sort eventsList based on eventTime using a Comparator
        Collections.sort(eventsList, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                return event1.getTime().compareTo(event2.getTime());
            }
        });

        // Prepare the formatted event details for display
        StringBuilder eventsStringBuilder = new StringBuilder();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        for (Event event : eventsList) {
            String formattedTime = timeFormat.format(event.getTime());

            // Append event details to the StringBuilder
            eventsStringBuilder.append("Title: ").append(event.getTitle()).append("\n");
            eventsStringBuilder.append("Time: ").append(formattedTime).append("\n");
            eventsStringBuilder.append("Description: ").append(event.getDescription()).append("\n\n");
        }

        // Display events in a TextView
        TextView eventsTextView = findViewById(R.id.eventsTextView);
        eventsTextView.setText(eventsStringBuilder.toString());
    }

    // Helper method to parse time string into a Date object
    private Date parseTimeString(String timeString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return format.parse(timeString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


}
