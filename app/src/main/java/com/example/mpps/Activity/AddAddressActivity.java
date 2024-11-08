package com.example.mpps.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddAddressActivity extends AppCompatActivity {

    EditText etCity, etAddress, etAddressName, etPhone, etApartmentNumber;
    Button btnSaveAddress;
    DatabaseHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // Initialize views
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etAddressName = findViewById(R.id.etAddressName);
        etPhone = findViewById(R.id.etPhone);
        etApartmentNumber = findViewById(R.id.etApartmentNumber);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        ImageView backArrow = findViewById(R.id.backArrow);
        db = new DatabaseHelper(this);

        // Retrieve user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Back button functionality
        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(AddAddressActivity.this, AddressActivity.class));
            finish();
        });

        // Save address button functionality
        btnSaveAddress.setOnClickListener(v -> {
            String city = etCity.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String addressName = etAddressName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String apartmentNumber = etApartmentNumber.getText().toString().trim();

            if (city.isEmpty() || address.isEmpty() || addressName.isEmpty() || phone.isEmpty() || apartmentNumber.isEmpty()) {
                Toast.makeText(this, "Prašome užpildyti visus laukus", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = db.insertAddress(userId, city, address, addressName, phone, apartmentNumber);
                if (success) {
                    Toast.makeText(this, "Adresas sėkmingai pridėtas", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddAddressActivity.this, AddressActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Nepavyko pridėti adreso", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Setup bottom navigation and set the account icon as selected
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_account);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(AddAddressActivity.this, AppMainActivity.class));
                return true;
            } else if (itemId == R.id.nav_categories) {
                startActivity(new Intent(AddAddressActivity.this, CategoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(AddAddressActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_account) {
                return true;
            }
            return false;
        });
    }
}
