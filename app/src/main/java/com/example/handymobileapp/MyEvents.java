package com.example.handymobileapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

        Calendar selectedCalendar = Calendar.getInstance();

        l =findViewById(R.id.list);


        ArrayAdapter<String> arr;
        arr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, returnEvents(fetchEvents(selectedCalendar)) );
        l.setAdapter(arr);


        SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = firestoreDateFormat.format(currentTime.getTime());



    }


    /**
     * Should retrieve all of the events a user has saved, or been invited to.
     * @param selectedCalendar
     * @return  a snapshot of the events a user has saved in firebase
     */
    private QuerySnapshot fetchEvents(Calendar selectedCalendar) {
        // Format selected date to match Firestore query format (yyyy-MM-dd)
        SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = firestoreDateFormat.format(selectedCalendar.getTime());

        // Get current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query Firestore for events on the selected date under the user's events subcollection
      return db.collection("users").document(userId).collection("events")
                .whereEqualTo("date", formattedDate)
                .get().getResult();
    }


    /**
     * Returns a list of events the user has saved, or been invited to.
     * @param querySnapshot
     * @return a list of the events a user has saved, formatted as strings and sorted by time
     */
    private List<String> returnEvents(QuerySnapshot querySnapshot) {
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

        List<String> events = new ArrayList<>();
        for (Event event : eventsList) {
            String formattedTime = timeFormat.format(event.getTime());

            // Append event details to the StringBuilder
            eventsStringBuilder.append("Title: ").append(event.getTitle()).append("\n");
            eventsStringBuilder.append("Time: ").append(formattedTime).append("\n");
            eventsStringBuilder.append("Description: ").append(event.getDescription()).append("\n\n");

            events.add(formattedTime);
            ;
        }



        return events;

    }


    /**
     * formats and simplifies the string
     * @param timeString
     * @return the formatted date
     */
    private Date parseTimeString(String timeString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return format.parse(timeString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }






}