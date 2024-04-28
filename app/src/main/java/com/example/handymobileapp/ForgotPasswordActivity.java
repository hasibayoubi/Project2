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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Hasibullah
 * Activity to handle password reset requests.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    /** EditText for inputting the email to send password reset instructions */
    EditText forgotEmailInput;

    /** Button to submit the password reset request */
    Button btnForgotPassword;

    /** TextView to navigate back to the login screen */
    TextView goBackToLogin;

    /** Dialog to show progress during the password reset process */
    ProgressDialog progressDialog;

    /** Firebase Authentication instance for managing user authentication */
    FirebaseAuth mAuth;

    /** FirebaseUser instance for getting the current logged-in user (unused in this context) */
    FirebaseUser mUser;

    /**
     * Called when the activity is starting. This method initializes the activity,
     * content view and its components.
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge mode for the layout
        setContentView(R.layout.activity_forgot_password);

        forgotEmailInput = findViewById(R.id.forgotEmailInput);
        btnForgotPassword = findViewById(R.id.btnForgotPasword);
        goBackToLogin = findViewById(R.id.backToLoginInForgotPassword);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        goBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performPasswordReset();
            }
        });
    }

    /**
     * Handles the password reset request by sending a reset email to the user.
     */
    private void performPasswordReset() {
        progressDialog.setMessage("Password reset in progress...");
        progressDialog.setTitle("Reset Password");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.sendPasswordResetEmail(forgotEmailInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    sendUserToNextActivity();
                    Toast.makeText(ForgotPasswordActivity.this, "If there's an account for that email, we've sent a reset link.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Navigates the user back to the MainActivity after the password reset operation,
     * clearing all activities on top of it in the current task.
     */
    private void sendUserToNextActivity() {
        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
