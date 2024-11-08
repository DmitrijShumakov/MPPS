package com.example.mpps.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;
import com.example.mpps.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    TextView tvName, tvAddress;
    Button logoutButton;
    DatabaseHelper db;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        logoutButton = findViewById(R.id.logoutButton);
        Button accountSettingsButton = findViewById(R.id.btnAccountSettings); // Paskyros nustatymų mygtukas

        db = new DatabaseHelper(this);

        // Gauti vartotojo ID iš SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            // Gauti vartotojo informaciją iš duomenų bazės pagal ID
            currentUser = db.getUserById(userId);

            if (currentUser != null) {
                tvName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
                tvAddress.setText(currentUser.getEmail());
            } else {
                tvName.setText("Vartotojas nerastas");
                tvAddress.setText("Nėra adreso");
            }
        } else {
            tvName.setText("Vartotojas neprisijungęs");
            tvAddress.setText("Nėra adreso");
        }

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Įvykis mygtukui „Paskyros Nustatymai“, kuris nukreipia į AccountActivity
        accountSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        Button orderHistoryButton = findViewById(R.id.btnOrderHistory);
        orderHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, OrderActivity.class);
            startActivity(intent);
        });


        Button myAddressesButton = findViewById(R.id.btnMyAddresses);
        myAddressesButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AddressActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_account);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(SettingsActivity.this, AppMainActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_categories) {
                Intent intent = new Intent(SettingsActivity.this, CategoryActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_cart) {
                Intent intent = new Intent(SettingsActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_account) {
                return true;
            }
            return false;
        });
    }

}
