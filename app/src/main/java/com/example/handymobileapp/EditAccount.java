package com.example.handymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * @author Misael
 * Activity for editing and updating a user's account information stored in Firebase Firestore.
 */
public class EditAccount extends AppCompatActivity {

    private EditText editTextName, editTextLastName, editTextDOB, editTextEmail;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    /**
     * Initializes the activity, Firestore and FirebaseAuth instances, and UI elements.
     * Retrieves and displays current user details.
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaccount);

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize EditTexts
        editTextName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextEmail = findViewById(R.id.editTextEmail);

        // Fetch and display user details
        fetchUserDetails();

        // Save button click listener
        findViewById(R.id.buttonSaveInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        // Cancel button click listener (optional)
        findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle cancel action (e.g., navigate back)
                navigateToAccountActivity(); // Finish the activity to return to the previous screen
            }
        });
    }

    // Method to navigate back to the AccountActivity
    private void navigateToAccountActivity() {
        Intent intent = new Intent(EditAccount.this, AccountActivity.class);
        startActivity(intent);
        finish(); // Finish current activity to prevent going back to NewEventActivity
    }

    /**
     * Fetches and displays the current user's details from Firestore.
     */
    private void fetchUserDetails() {
        if (currentUser != null) {
            // Get the user's document reference from Firestore based on their UID
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            // Fetch the user's document
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve user data and populate EditTexts
                            String name = document.getString("name");
                            String lastName = document.getString("lastName");
                            String dob = document.getString("dateOfBirth");
                            String email = currentUser.getEmail(); // Use current user's email

                            editTextName.setText(name);
                            editTextLastName.setText(lastName);
                            editTextDOB.setText(dob);
                            editTextEmail.setText(email);
                        } else {
                            Toast.makeText(EditAccount.this, "User document not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditAccount.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Updates the user's information in Firestore based on input from EditText fields.
     */
    private void updateUserInfo() {
        // Get updated user info from EditTexts
        String updatedName = editTextName.getText().toString().trim();
        String updatedLastName = editTextLastName.getText().toString().trim();
        String updatedDOB = editTextDOB.getText().toString().trim();
        String updatedEmail = editTextEmail.getText().toString().trim();

        // Update user details in Firestore
        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.update("name", updatedName,
                            "lastName", updatedLastName,
                            "dateOfBirth", updatedDOB,
                            "email", updatedEmail)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditAccount.this, "User info updated successfully", Toast.LENGTH_SHORT).show();
                            navigateToAccountActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditAccount.this, "Failed to update user info", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
