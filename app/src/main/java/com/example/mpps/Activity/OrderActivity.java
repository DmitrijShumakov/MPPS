package com.example.mpps.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mpps.Adapters.OrderAdapter;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.Model.Order;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView orderRecyclerView;
    private ArrayList<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        db = new DatabaseHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load orders from the database
        orders = db.getOrdersByUserId(userId);

        OrderAdapter orderAdapter = new OrderAdapter(this, orders);
        orderRecyclerView.setAdapter(orderAdapter);

        // Back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });

        // Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_account);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(OrderActivity.this, AppMainActivity.class));
                return true;
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(OrderActivity.this, CategoryActivity.class));
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(OrderActivity.this, CartActivity.class));
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(OrderActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }
}
