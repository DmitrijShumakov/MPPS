package com.example.mpps.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mpps.Adapters.AddressAdapter;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.Model.Address;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity {

    private RecyclerView rvAddresses;
    private DatabaseHelper db;
    private int userId;
    private AddressAdapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Initialize views and database
        rvAddresses = findViewById(R.id.rvAddresses);
        db = new DatabaseHelper(this);

        // Setup back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> navigateBackToSettings());

        // Retrieve user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Load and display addresses
        loadAddresses();

        // Add new address button
        findViewById(R.id.btnAddNewAddress).setOnClickListener(v -> openAddAddressActivity());

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void loadAddresses() {
        ArrayList<Address> addresses = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.getAllAddressesByUserId(userId);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int addressId = cursor.getInt(cursor.getColumnIndex("ID"));
                    @SuppressLint("Range") String addressName = cursor.getString(cursor.getColumnIndex("ADDRESS_NAME"));
                    @SuppressLint("Range") String city = cursor.getString(cursor.getColumnIndex("CITY"));
                    @SuppressLint("Range") String street = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                    @SuppressLint("Range") String houseNumber = cursor.getString(cursor.getColumnIndex("APARTMENT_NUMBER"));

                    addresses.add(new Address(addressId, addressName, city, street, houseNumber));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            Toast.makeText(this, "Įkeliant adresus įvyko klaida", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }

        addressAdapter = new AddressAdapter(this, addresses, this::openEditAddressActivity, this::deleteAddress);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(addressAdapter);
    }


    private void openAddAddressActivity() {
        Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
        startActivity(intent);
    }

    private void openEditAddressActivity(Address address) {
        Intent intent = new Intent(AddressActivity.this, EditAddressActivity.class);
        intent.putExtra("address_id", address.getId());
        startActivity(intent);
    }

    private void deleteAddress(Address address) {
        if (db.deleteAddressById(address.getId())) {
            Toast.makeText(this, "Adresas pašalintas", Toast.LENGTH_SHORT).show();
            loadAddresses(); // Refresh the address list
        } else {
            Toast.makeText(this, "Klaida ištrinant adresą", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBackToSettings() {
        Intent backIntent = new Intent(AddressActivity.this, SettingsActivity.class);
        startActivity(backIntent);
        finish();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_account);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(AddressActivity.this, AppMainActivity.class));
                return true;
            } else if (itemId == R.id.nav_categories) {
                startActivity(new Intent(AddressActivity.this, CategoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(AddressActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_account) {
                return true;
            }
            return false;
        });
    }

}
