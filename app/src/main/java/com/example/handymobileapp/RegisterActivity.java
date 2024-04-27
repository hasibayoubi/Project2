package com.example.handymobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * RegisterActivity handles user registration using Firebase authentication.
 */
public class RegisterActivity extends AppCompatActivity {

    /** TextView for navigating to the login screen if the user already has an account */
    TextView alreadyHaveAccount;

    /** EditText field for user email input */
    EditText inputEmail;

    /** EditText field for user password input */
    EditText inputPassword;

    /** EditText field for user to confirm password input */
    EditText inputCPassword;
    EditText name;
    EditText lastName;
    EditText dateOfBirth;

    /** Button for triggering the registration process */
    Button btnRegister;

    /** Regex pattern for validating email input */
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /** ProgressDialog to show user registration is in progress */
    ProgressDialog progressDialog;

    /** FirebaseAuth instance for handling login and signup */
    FirebaseAuth mAuth;

    /** FirebaseUser instance for getting the current logged in user */
    FirebaseUser mUser;
    String userID;
    FirebaseFirestore mStore;


    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        lastName = findViewById(R.id.lastName);
        dateOfBirth = findViewById(R.id.dob);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputCPassword = findViewById(R.id.inputCPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();


        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAuthorization();
            }
        });
    }

    /**
     * Performs user registration using email and password entered along with additional user details
     * like name, last name, and date of birth. It validates the user inputs and interacts with Firebase
     * Authentication to create a user account. Post registration, it saves additional user details to
     * Firestore under a document that uses the user's UID as the identifier.
     */
    private void performAuthorization() {
        // Retrieve user inputs from EditText fields
        String usersName = name.getText().toString();
        String usersLastName = lastName.getText().toString();
        String dob = dateOfBirth.getText().toString();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputCPassword.getText().toString().trim();

        // Validate email pattern
        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter a valid email address!");
            inputEmail.requestFocus();
        } else if (password.isEmpty()) {
            inputPassword.setError("Password is required!");
            inputPassword.requestFocus();
        } else if (password.length() < 6) {
            inputPassword.setError("Password must be at least 6 characters long!");
            inputPassword.requestFocus();
        } else if (!password.equals(confirmPassword)) {
            inputCPassword.setError("Passwords do not match!");
            inputCPassword.requestFocus();
        } else {
            progressDialog.setMessage("Registration in progress...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Create a user with email and password in Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss(); // Dismiss the progress dialog on completion
                            if (task.isSuccessful()) { // Registration success
                                sendUserToNextActivity(); // Navigate to the next activity
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                userID = mAuth.getCurrentUser().getUid();  // Get the user ID from Firebase

                                // Create a document reference in Firestore using the user ID
                                DocumentReference documentReference = mStore.collection("users").document(userID);
                                Map <String, Object> hashMap = new HashMap<>();
                                hashMap.put("name", usersName);
                                hashMap.put("lastName", usersLastName);
                                hashMap.put("dateOfBirth", dob);
                                hashMap.put("email", email);

                                // Set the user details in Firestore
                                documentReference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "User profile is created for "+userID);

                                    }
                                });

                            } else { // Registration failure
                                Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    /**
     * Redirects the user to the HomeActivity after successful registration.
     * Clears all previous activities from the stack to prevent returning to the registration screen via the back button.
     */
    private void sendUserToNextActivity() {
        Intent intent = new Intent(RegisterActivity.this, calendar.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
