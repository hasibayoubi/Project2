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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewEvent extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 101;

    private EditText editTextTitle, editTextDate, editTextTime, editTextDescription;
    private Button buttonAttachFile, buttonSaveEvent;

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
        buttonAttachFile = findViewById(R.id.buttonAttachFile);
        buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        toggleButton = findViewById(R.id.toggleButton);

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
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("date", date);
        eventData.put("time", time);
        eventData.put("description", description);
        if (attachmentUrl != null) {
            eventData.put("attachmentUrl", attachmentUrl);
        }

        db.collection("events")
                .add(eventData)
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
}
