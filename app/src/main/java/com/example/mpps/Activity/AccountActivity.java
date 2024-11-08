package com.example.mpps.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    ImageView profileImageView, backArrow;
    DatabaseHelper db;
    EditText emailEditText, passwordEditText, firstNameEditText, lastNameEditText, phoneEditText;
    Button saveButton, changePictureButton;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        db = new DatabaseHelper(this);

        profileImageView = findViewById(R.id.profileImageView);
        changePictureButton = findViewById(R.id.changePictureButton);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);
        backArrow = findViewById(R.id.backArrow);

        // Gauti vartotojo ID iš SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            loadUserInfo(userId);
        } else {
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        saveButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            ContentValues values = new ContentValues();
            values.put("FIRST_NAME", firstName);
            values.put("LAST_NAME", lastName);
            values.put("PHONE_NUMBER", phone);
            values.put("EMAIL", email);
            values.put("PASSWORD", password);
            db.getWritableDatabase().update("users", values, "ID = ?", new String[]{String.valueOf(userId)});

            // Atnaujinkite SharedPreferences su nauja informacija
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_email", email);
            editor.apply();

            Snackbar.make(findViewById(android.R.id.content), "Pakeitimai išsaugoti!", Snackbar.LENGTH_SHORT).show();
        });

        changePictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_account);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(AccountActivity.this, AppMainActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_categories) {
                Intent intent = new Intent(AccountActivity.this, CategoryActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_cart) {
                return true;
            } else if (id == R.id.nav_account) {
                return true;
            }
            return false;
        });
    }

    private void loadUserInfo(int userId) {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT FIRST_NAME, LAST_NAME, PHONE_NUMBER, EMAIL, PASSWORD FROM users WHERE ID = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            firstNameEditText.setText(cursor.getString(0));
            lastNameEditText.setText(cursor.getString(1));
            phoneEditText.setText(cursor.getString(2));
            emailEditText.setText(cursor.getString(3));
            passwordEditText.setText(cursor.getString(4));
        }
        cursor.close();
    }
}
