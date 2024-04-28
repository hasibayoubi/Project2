package com.example.handymobileapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * @author ivanw845
 * @version JDK 21
 * Class for handling the myEvents section of the application. Should display the events the user
 *  has saved or created.
 */
public class MyEvents extends AppCompatActivity {

    ListView l;
    private FirebaseFirestore db;

    Date currentTime = Calendar.getInstance().getTime();

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        // Initialize FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();

        // Get the ListView reference
        l = findViewById(R.id.list);

        // Fetch and populate events
        fetchAndPopulateEvents();
    }

    private void fetchAndPopulateEvents() {
        Calendar selectedCalendar = Calendar.getInstance();

        // Fetch events asynchronously
        fetchEvents(selectedCalendar, new OnEventsFetchedListener() {
            @Override
            public void onEventsFetched(QuerySnapshot querySnapshot) {
                // Populate ListView with events
                List<String> eventStrings = returnEvents(querySnapshot);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MyEvents.this, android.R.layout.simple_list_item_1, eventStrings);
                l.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }

    private void fetchEvents(Calendar selectedCalendar, OnEventsFetchedListener listener) {
        SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = firestoreDateFormat.format(selectedCalendar.getTime());
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("events")
                .whereEqualTo("date", formattedDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> listener.onEventsFetched(queryDocumentSnapshots))
                .addOnFailureListener(e -> listener.onError(e));
    }

    private List<String> returnEvents(QuerySnapshot querySnapshot) {
        List<String> events = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot) {
            String title = document.getString("title");
            String time = document.getString("time");
            String description = document.getString("description");

            Date eventTime = parseTimeString(time);

            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = format.format(eventTime);

            String formattedEvent = "Title: " + title + "\n"
                    + "Time: " + formattedTime + "\n"
                    + "Description: " + description + "\n";

            events.add(formattedEvent);
        }

        return events;
    }

    private Date parseTimeString(String timeString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return format.parse(timeString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    interface OnEventsFetchedListener {
        void onEventsFetched(QuerySnapshot querySnapshot);
        void onError(Exception e);
    }

}