package com.example.handymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * @author Misael
 * Activity class that handles user account information display and interactions.
 */
public class AccountActivity extends AppCompatActivity {

    // TextViews to display user information
    private TextView textViewName, textViewLastName, textViewDOB, textViewEmail;

    /**
     * Called when the activity is starting.
     * This method initializes the layout and its components, and loads user data from Firestore.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize TextViews by finding them by their IDs
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

        // Edit button click listener
        findViewById(R.id.buttonEditInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToEditAccount();
            }
        });

        // Logout button click listener
        findViewById(R.id.buttonLogOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the current user
                FirebaseAuth.getInstance().signOut();

                // Navigate back to MainActivity (Login screen)
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    /**
     * Navigates to the EditAccount activity to allow the user to edit their personal information.
     */
    private void navigateToEditAccount() {
        Intent intent = new Intent(AccountActivity.this, EditAccount.class);
        startActivity(intent);
        finish(); // Finish current activity to prevent going back to AccountActivity
    }
}
