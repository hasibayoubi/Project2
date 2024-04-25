package com.example.handymobileapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {

    private TextView textViewName, textViewLastName, textViewDOB, textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize TextViews
        textViewName = findViewById(R.id.textViewName);
        textViewLastName = findViewById(R.id.textViewLastName);
        textViewDOB = findViewById(R.id.textViewDOB);
        textViewEmail = findViewById(R.id.textViewEmail);

        // Get current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Retrieve user's document from Firestore using their UID
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Retrieve user data from Firestore document
                                String name = document.getString("name");
                                String lastName = document.getString("lastName");
                                String dob = document.getString("dateOfBirth");
                                String email = currentUser.getEmail(); // Get email from FirebaseUser

                                // Set retrieved data to TextViews
                                if (textViewName != null) {
                                    textViewName.setText(name);
                                }
                                if (textViewLastName != null) {
                                    textViewLastName.setText(lastName);
                                }
                                if (textViewDOB != null) {
                                    textViewDOB.setText(dob);
                                }
                                if (textViewEmail != null) {
                                    textViewEmail.setText(email);
                                }
                            } else {
                                Log.e("AccountActivity", "User document not found");
                                Toast.makeText(AccountActivity.this, "User document not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("AccountActivity", "Failed to fetch user data", task.getException());
                            Toast.makeText(AccountActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("AccountActivity", "Current user is null");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

}
