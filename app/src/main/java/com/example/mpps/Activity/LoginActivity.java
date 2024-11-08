package com.example.mpps.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton, registerButton;
    public DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Pakeiskite į savo login veiklos layout'ą

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Inicializuokite DatabaseHelper
        db = new DatabaseHelper(this);

        // Login mygtuko funkcionalumas
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Patikrinkite įvesties laukus
                if (!validateInput(email, password)) {
                    return; // Grįžti, jei tikrinimas nepraėjo
                }

                // Patikrinkite, ar email ir password teisingi
                int userId = db.getUserId(email, password);
                if (userId != -1) {
                    Snackbar.make(findViewById(android.R.id.content), "Prisijungimas sėkmingas", Snackbar.LENGTH_SHORT).show();

                    // Išsaugoti `userId` SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", userId);  // Saugo prisijungusio vartotojo `userId`
                    editor.apply();

                    // Pereiti į `AppMainActivity`
                    Intent intent = new Intent(LoginActivity.this, AppMainActivity.class);
                    intent.putExtra("user_id", userId);  // Perduoti `userId` į kitą veiklą
                    startActivity(intent);
                    finish(); // Uždarome LoginActivity, kad vartotojas negalėtų grįžti atgal į prisijungimo ekraną
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Prisijungimo duomenys neteisingi", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // Register mygtuko funkcionalumas
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perėjimas į registracijos veiklą
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // Tikrina vartotojo įvesties laukus
    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Įveskite visus laukus", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(findViewById(android.R.id.content), "Neteisingas el. pašto adresas", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
