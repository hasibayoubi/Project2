package com.example.handymobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
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
 * MainActivity class that serves as the login screen for the application.
 */
public class MainActivity extends AppCompatActivity {

    /** TextView that if clicked, navigates the user to the registration activity */
    TextView createnewAccount;

    /** TextView that if clicked, navigates the user to reset their password */
    TextView forgotPassword;

    /** EditText field for entering email */
    EditText inputEmail;

    /** EditText field for entering password */
    EditText inputPassword;

    /** Button that triggers the login process */
    Button btnLogin;

    /** Regex pattern to validate email format */
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /** Dialog to show progress during the login process */
    ProgressDialog progressDialog;

    /** FirebaseAuth instance for handling authentication */
    FirebaseAuth mAuth;

    /** FirebaseUser instance for getting the current user */
    FirebaseUser mUser;

    /**
     * Initializes the MainActivity with appropriate contentView and hooks up views
     * with their respective IDs.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display mode
        setContentView(R.layout.activity_main);

        createnewAccount = findViewById(R.id.createNewAccount);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        createnewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });
    }

    /**
     * Handles the login operation using email and password entered by the user.
     */
    private void performLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter correct email address!");
            inputEmail.requestFocus();
        } else if (password.isEmpty()) {
            inputPassword.setError("Password is required!");
            inputPassword.requestFocus();
        } else {
            progressDialog.setMessage("Login progress ...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        sendUserToNextActivity();
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * Navigates the user to the HomeActivity and clears all previous activities from the stack
     * to ensure a clean state.
     */
    private void sendUserToNextActivity() {
        Intent intent = new Intent(MainActivity.this, NewEvent.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
