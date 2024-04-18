package com.example.handymobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputCPassword = findViewById(R.id.inputCPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
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
     * Perform user registration using email and password.
     */
    private void performAuthorization() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputCPassword.getText().toString().trim();

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

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                sendUserToNextActivity();
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            } else {
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
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
