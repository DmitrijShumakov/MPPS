package com.example.mpps.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public DatabaseHelper db;
    EditText emailInput, passwordInput, confirmPasswordInput, firstNameInput, lastNameInput, phoneInput;
    Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        // Initialize the input fields
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginLink);

        // Handle registration logic
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String pass = passwordInput.getText().toString().trim();
                String confirmPass = confirmPasswordInput.getText().toString().trim();
                String firstName = firstNameInput.getText().toString().trim();
                String lastName = lastNameInput.getText().toString().trim();
                String phone = phoneInput.getText().toString().trim();

                // Validate fields
                if (!validateInput(email, pass, confirmPass, firstName, lastName, phone)) {
                    return; // Return early if validation fails
                }

                // Check if the email is already registered
                boolean emailExists = db.checkEmailExists(email);
                if (emailExists) {
                    Snackbar.make(findViewById(android.R.id.content), "El. paštas jau užregistruotas", Snackbar.LENGTH_SHORT).show();
                } else {
                    // Insert the new user
                    boolean insert = db.insertUser(email, pass, firstName, lastName, phone);
                    if (insert) {
                        Snackbar.make(findViewById(android.R.id.content), "Registracija sėkminga", Snackbar.LENGTH_SHORT).show();

                        // Delay for 1.5 seconds to show Snackbar, then navigate to MainActivity
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();  // Finish RegisterActivity
                            }
                        }, 1500);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Registracija nepavyko", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Handle login button click to go to MainActivity
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    // Validate user input fields
    public boolean validateInput(String email, String password, String confirmPassword, String firstName, String lastName, String phone) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Užpildykite visus laukus", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(findViewById(android.R.id.content), "Neteisingas el. pašto adresas", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("\\d{9,}")) {
            Snackbar.make(findViewById(android.R.id.content), "Neteisingas telefono numeris", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPassword(password)) {
            Snackbar.make(findViewById(android.R.id.content), "Slaptažodis turi būti bent 8 simbolių, turėti didžiąją raidę, skaičių ir specialų simbolį", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Snackbar.make(findViewById(android.R.id.content), "Slaptažodžiai nesutampa", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Password strength validation
    public boolean isValidPassword(String password) {
        Pattern passwordPattern = Pattern.compile(
                "^" +
                        "(?=.*[0-9])" +
                        "(?=.*[a-z])" +
                        "(?=.*[A-Z])" +
                        "(?=.*[!@#$%^&*()_+])" +
                        ".{8,}" +
                        "$"
        );
        return passwordPattern.matcher(password).matches();
    }
}