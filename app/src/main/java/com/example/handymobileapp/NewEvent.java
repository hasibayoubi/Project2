package com.example.handymobileapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewEvent extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 101;

    private EditText editTextTitle, editTextDate, editTextTime, editTextDescription, editTextRecipientEmail;
    private Button buttonAttachFile, buttonSaveEvent, buttonShareEvent;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ToggleButton toggleButton;

    private Uri selectedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newevent);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextRecipientEmail = findViewById(R.id.editTextRecipientEmail);
        buttonAttachFile = findViewById(R.id.buttonAttachFile);
        buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        toggleButton = findViewById(R.id.toggleButton);
        buttonShareEvent = findViewById(R.id.buttonShareEvent);

        buttonAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFile();
            }
        });

        buttonSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEventToFirestore();
            }
        });
        buttonShareEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareEventViaEmail();
            }
        });

    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedFileUri = data.getData();
            Toast.makeText(this, "File selected: " + selectedFileUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEventToFirestore() {
        String title = editTextTitle.getText().toString();
        String date = editTextDate.getText().toString();
        String time = editTextTime.getText().toString();
        String description = editTextDescription.getText().toString();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedDate = formatDate(date);
        if (formattedDate == null) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format time to "HH:mm a" (e.g., "12:30 PM")
        String formattedTime = formatTime(time, toggleButton.isChecked());
        if (formattedTime == null) {
            Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload file if selected
        if (selectedFileUri != null) {
            StorageReference fileRef = storageRef.child("event_attachments/" + selectedFileUri.getLastPathSegment());
            UploadTask uploadTask = fileRef.putFile(selectedFileUri);

            uploadTask.addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                addEventToFirestore(title, formattedDate, formattedTime, description, downloadUrl);
                            }
                        });
                    } else {
                        Toast.makeText(NewEvent.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // No file attached, add event without attachment
            addEventToFirestore(title, formattedDate, formattedTime, description, null);
        }
    }

    private String formatTime(String inputTime, boolean isPM) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
            Date time = inputFormat.parse(inputTime);

            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
            String formattedTime = outputFormat.format(time);
            if(isPM){
                formattedTime += " PM";
            }else {
                formattedTime += " AM";
            }
            return formattedTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addEventToFirestore(String title, String date, String time, String description, String attachmentUrl) {
        // Get current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a reference to the user's events subcollection
        CollectionReference userEventsRef = db.collection("users").document(userId).collection("events");

        // Create a new event document within the user's events subcollection
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("date", date);
        eventData.put("time", time);
        eventData.put("description", description);
        if (attachmentUrl != null) {
            eventData.put("attachmentUrl", attachmentUrl);
        }

        // Add the event data to Firestore under the user's events subcollection
        userEventsRef.add(eventData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NewEvent.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                    navigateToCalendarActivity(); // Navigate to CalendarActivity upon success
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewEvent.this, "Failed to add event", Toast.LENGTH_SHORT).show();
                });
    }


    private String formatDate(String inputDate){
        try {
            //Parse input date in mm/dd/yyyy
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date date = inputDateFormat.parse(inputDate);

            //format date to YYYY-MM-DD
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputDateFormat.format(date);
        } catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }
    private void navigateToCalendarActivity() {
        Intent intent = new Intent(NewEvent.this, calendar.class);
        startActivity(intent);
        finish(); // Finish current activity to prevent going back to NewEventActivity
    }

    private void shareEventViaEmail() {
        String title = editTextTitle.getText().toString();
        String date = editTextDate.getText().toString();
        String time = editTextTime.getText().toString();
        String description = editTextDescription.getText().toString();

        String emailSubject = "Invitation to Event: " + title;
        String emailBody = "Event Details:\n" +
                "Date: " + date + "\n" +
                "Time: " + time + "\n" +
                "Description: " + description + "\n";

        // Retrieve email input from EditText
        String recipientEmail = editTextRecipientEmail.getText().toString();

        // Get UID of the recipient user based on the email
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", recipientEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String recipientUid = document.getId();
                            // Call method to share or duplicate event to recipientUid's collection
                            shareEventWithRecipient(title, date, time, description, recipientUid);
                        }
                    } else {
                        Toast.makeText(NewEvent.this, "Error finding user with this email", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void shareEventWithRecipient(String title, String date, String time, String description, String recipientUid) {
        // Create a reference to the recipient's events subcollection
        CollectionReference recipientEventsRef = db.collection("users").document(recipientUid).collection("events");

        // Create a new event document within the recipient's events subcollection
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("date", date);
        eventData.put("time", time);
        eventData.put("description", description);

        // Add the event data to Firestore under the recipient's events subcollection
        recipientEventsRef.add(eventData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NewEvent.this, "Event shared successfully with recipient", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewEvent.this, "Failed to share event with recipient", Toast.LENGTH_SHORT).show();
                });
    }

}
