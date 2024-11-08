package com.example.mpps.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.Model.Product;
import com.example.mpps.Adapters.ProductAdapter;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView productRecyclerView;
    ProductAdapter productAdapter;
    ArrayList<Product> productList;
    DatabaseHelper db;
    TextView tvProductListTitle;
    ImageView backArrow; // Back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Initialize views
        productRecyclerView = findViewById(R.id.productRecyclerView);
        tvProductListTitle = findViewById(R.id.tvProductList);
        backArrow = findViewById(R.id.backArrow);

        productList = new ArrayList<>();
        db = new DatabaseHelper(this);

        // Get the selected category from the intent
        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        // Set category name as title
        if (category != null) {
            tvProductListTitle.setText(category);
            loadProductsByCategory(category); // Load products based on the category
        } else {
            tvProductListTitle.setText("Products");
        }

        // Set up the RecyclerView
        productAdapter = new ProductAdapter(this, productList);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productRecyclerView.setAdapter(productAdapter);

        // Back button functionality
        backArrow.setOnClickListener(v -> finish());

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_categories); // Highlight "Catalog" tab

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(ProductListActivity.this, AppMainActivity.class);
                startActivity(navIntent);
                return true;
            } else if (id == R.id.nav_categories) {
                // Don't open CategoryActivity if already in ProductListActivity
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(ProductListActivity.this, CartActivity.class);
                startActivity(navIntent);
                return true;
            } else if (id == R.id.nav_account) {
                navIntent = new Intent(ProductListActivity.this, SettingsActivity.class);
                startActivity(navIntent);
                return true;
            }

            return false;
        });
    }


    private void loadProductsByCategory(String category) {
        Cursor productCursor = db.getProductsByCategory(category);

        if (productCursor != null && productCursor.getCount() > 0) {
            while (productCursor.moveToNext()) {
                int id = productCursor.getInt(0);
                String name = productCursor.getString(1);
                double price = productCursor.getDouble(2);
                int storeId = productCursor.getInt(3);
                String ingredients = productCursor.getString(4);
                String nutrition = productCursor.getString(5);
                String productCategory = productCursor.getString(6);

                productList.add(new Product(id, name, price, storeId, ingredients, nutrition, productCategory));
            }
            productCursor.close();
        }
    }
}
