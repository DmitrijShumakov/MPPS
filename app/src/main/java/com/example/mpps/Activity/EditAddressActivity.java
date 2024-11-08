package com.example.mpps.Activity;

import android.content.Intent;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditAddressActivity extends AppCompatActivity {

    EditText etCity, etAddress, etAddressName, etPhone, etApartmentNumber;
    Button btnSaveAddress;
    DatabaseHelper db;
    int addressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        db = new DatabaseHelper(this);
        addressId = getIntent().getIntExtra("address_id", -1);

        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etAddressName = findViewById(R.id.etAddressName);
        etPhone = findViewById(R.id.etPhone);
        etApartmentNumber = findViewById(R.id.etApartmentNumber);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);

        loadAddressDetails();

        btnSaveAddress.setOnClickListener(v -> saveAddressDetails());

        // Set up back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> navigateBackToAddressActivity());

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void loadAddressDetails() {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT CITY, ADDRESS, ADDRESS_NAME, PHONE, APARTMENT_NUMBER FROM " + DatabaseHelper.ADDRESS_TABLE + " WHERE ID=?",
                new String[]{String.valueOf(addressId)}
        );
        if (cursor != null && cursor.moveToFirst()) {
            etCity.setText(cursor.getString(0));
            etAddress.setText(cursor.getString(1));
            etAddressName.setText(cursor.getString(2));
            etPhone.setText(cursor.getString(3));
            etApartmentNumber.setText(cursor.getString(4));
            cursor.close();
        }
    }

    private void saveAddressDetails() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("CITY", etCity.getText().toString());
        contentValues.put("ADDRESS", etAddress.getText().toString());
        contentValues.put("ADDRESS_NAME", etAddressName.getText().toString());
        contentValues.put("PHONE", etPhone.getText().toString());
        contentValues.put("APARTMENT_NUMBER", etApartmentNumber.getText().toString());

        int rowsAffected = db.getWritableDatabase().update(DatabaseHelper.ADDRESS_TABLE, contentValues, "ID=?", new String[]{String.valueOf(addressId)});
        if (rowsAffected > 0) {
            Toast.makeText(this, "Adresas sėkmingai atnaujintas!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Nepavyko atnaujinti adreso.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBackToAddressActivity() {
        Intent backIntent = new Intent(EditAddressActivity.this, AddressActivity.class);
        startActivity(backIntent);
        finish();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_account);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(EditAddressActivity.this, AppMainActivity.class));
                return true;
            } else if (itemId == R.id.nav_categories) {
                startActivity(new Intent(EditAddressActivity.this, CategoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(EditAddressActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_account) {
                return true;
            }
            return false;
        });
    }
}
