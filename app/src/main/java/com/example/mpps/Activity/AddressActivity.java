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

                    // Additional validation checks for complexity
                    if (addressName.isEmpty() || city.isEmpty() || street.isEmpty()) {
                        if (addressName.isEmpty()) {
                            Toast.makeText(this, "Adreso pavadinimas yra tuščias", Toast.LENGTH_SHORT).show();
                        } else if (city.isEmpty()) {
                            Toast.makeText(this, "Miesto pavadinimas yra tuščias", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Gatvės pavadinimas yra tuščias", Toast.LENGTH_SHORT).show();
                        }
                        continue; // Skip invalid address
                    }

                    if (houseNumber == null || houseNumber.length() < 1) {
                        if (houseNumber == null) {
                            Toast.makeText(this, "Namas neturi numerio", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Namo numeris yra per trumpas", Toast.LENGTH_SHORT).show();
                        }
                        continue; // Skip invalid address
                    }

                    addresses.add(new Address(addressId, addressName, city, street, houseNumber));
                } while (cursor.moveToNext());
            } else {
                if (cursor == null) {
                    Toast.makeText(this, "Klaida įkeliant adresus: kursorius yra null", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Nerasta adresų", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            Toast.makeText(this, "Įkeliant adresus įvyko klaida", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    Toast.makeText(this, "Klaida uždarant kursorių", Toast.LENGTH_SHORT).show();
                }
            }
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
        if (address == null) {
            Toast.makeText(this, "Nepavyko redaguoti adreso", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(AddressActivity.this, EditAddressActivity.class);
        intent.putExtra("address_id", address.getId());
        startActivity(intent);
    }

    private void deleteAddress(Address address) {
        if (address == null || address.getId() <= 0) {
            Toast.makeText(this, "Adresas nėra galiojantis", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.deleteAddressById(address.getId())) {
            Toast.makeText(this, "Adresas pašalintas", Toast.LENGTH_SHORT).show();
            loadAddresses(); // Refresh the address list
        } else {
            Toast.makeText(this, "Klaida ištrinant adresą", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBackToSettings() {
        Intent backIntent = new Intent(AddressActivity.this, SettingsActivity.class);
        if (userId == -1) {
            Toast.makeText(this, "Nepavyko gauti vartotojo ID", Toast.LENGTH_SHORT).show();
        }
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
                Toast.makeText(this, "Jūs jau esate šiame puslapyje", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, "Neatpažinta navigacija", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }
}