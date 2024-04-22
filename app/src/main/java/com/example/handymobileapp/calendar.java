package com.example.handymobileapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        // Query Firestore for events on the selected date
        db.collection("events")
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
        StringBuilder eventsStringBuilder = new StringBuilder();

        for (QueryDocumentSnapshot document : querySnapshot) {
            String title = document.getString("title");
            String time = document.getString("time");
            String description = document.getString("description");

            // Format time from Firestore to display as HH:MM AM/PM
            String formattedTime = formatTime(time);

            // Append event details to the StringBuilder
            eventsStringBuilder.append("Title: ").append(title).append("\n");
            eventsStringBuilder.append("Time: ").append(formattedTime).append("\n");
            eventsStringBuilder.append("Description: ").append(description).append("\n\n");
        }

        // Display events in a TextView or handle as needed (e.g., populate a RecyclerView)
        TextView eventsTextView = findViewById(R.id.eventsTextView);
        eventsTextView.setText(eventsStringBuilder.toString());
    }

    private String formatTime(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(time);

            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return time; // Return original time if parsing fails
        }
    }

}
