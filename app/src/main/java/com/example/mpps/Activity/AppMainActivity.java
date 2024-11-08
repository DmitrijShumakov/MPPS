package com.example.mpps.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpps.DatabaseHelper;
import com.example.mpps.Model.HorizontalSpaceItemDecoration;
import com.example.mpps.Model.Product;
import com.example.mpps.Adapters.ProductAdapter;
import com.example.mpps.R;
import com.example.mpps.Model.Store;
import com.example.mpps.Adapters.StoreAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AppMainActivity extends AppCompatActivity {

    RecyclerView storeRecyclerView, productRecyclerView;
    public DatabaseHelper db;
    StoreAdapter storeAdapter;
    ProductAdapter productAdapter;
    EditText searchBar;
    Spinner priceFilterSpinner;

    ArrayList<Store> storeList;
    ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmain);

        searchBar = findViewById(R.id.searchBar);
        storeRecyclerView = findViewById(R.id.storeRecyclerView);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        priceFilterSpinner = findViewById(R.id.priceFilterSpinner);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_categories) {
                    Intent intent = new Intent(AppMainActivity.this, CategoryActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_cart) {
                    Intent intent = new Intent(AppMainActivity.this, CartActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_account) {
                    Intent intent = new Intent(AppMainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });

        db = new DatabaseHelper(this);

        // Initialize lists
        storeList = new ArrayList<>();
        productList = new ArrayList<>();

        // Load stores and products
        loadStores();
        loadAllProducts(); // Initially load all products under "Pasiūlymai"

        // Set up spinner for sorting products by price
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.price_filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceFilterSpinner.setAdapter(adapter);

        priceFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Sort by cheapest first
                    sortProductsByPrice(true);
                } else if (position == 1) {
                    // Sort by most expensive first
                    sortProductsByPrice(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Add a TextWatcher to filter stores/products based on the search query
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                filterStores(query);
                filterProducts(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadStores() {
        Cursor storeCursor = db.getStores();
        storeList.clear();

        if (storeCursor.getCount() > 0) {
            while (storeCursor.moveToNext()) {
                int id = storeCursor.getInt(0);
                String name = storeCursor.getString(1);
                String logo = storeCursor.getString(2);
                storeList.add(new Store(id, name, logo));
            }
        }

        storeAdapter = new StoreAdapter(storeList, this::loadProductsForStore);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Add space of 16dp between store items
        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);  // Assuming you've defined this dimension
        storeRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spaceInPixels));

        storeRecyclerView.setAdapter(storeAdapter);
    }

    private void loadAllProducts() {
        Cursor productCursor = db.getAllProducts(); // Method in DatabaseHelper to get all products
        productList.clear();

        if (productCursor.getCount() > 0) {
            while (productCursor.moveToNext()) {
                int id = productCursor.getInt(0);
                String name = productCursor.getString(1);
                double price = productCursor.getDouble(2);
                int storeId = productCursor.getInt(3);
                String ingredients = productCursor.getString(4); // Get ingredients
                String nutritionalValue = productCursor.getString(5); // Get nutritional value
                String category = productCursor.getString(6); // Get category

                // Add the new Product constructor to include category
                productList.add(new Product(id, name, price, storeId, ingredients, nutritionalValue, category));
            }
        }

        productAdapter = new ProductAdapter(this, productList);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productRecyclerView.setAdapter(productAdapter);
    }

    private void loadProductsForStore(int storeId) {
        ArrayList<Product> storeProducts = new ArrayList<>();
        Cursor productCursor = db.getProductsByStore(storeId);

        if (productCursor.getCount() > 0) {
            while (productCursor.moveToNext()) {
                int id = productCursor.getInt(0);
                String name = productCursor.getString(1);
                double price = productCursor.getDouble(2);
                String ingredients = productCursor.getString(4); // Get ingredients
                String nutritionalValue = productCursor.getString(5); // Get nutritional value
                String category = productCursor.getString(6); // Get category

                // Add the new Product constructor to include category
                storeProducts.add(new Product(id, name, price, storeId, ingredients, nutritionalValue, category));
            }
        }

        productAdapter = new ProductAdapter(this, storeProducts);
        productRecyclerView.setAdapter(productAdapter);
    }



    private void filterStores(String query) {
        ArrayList<Store> filteredStores = new ArrayList<>();
        for (Store store : storeList) {
            if (store.getName().toLowerCase().contains(query)) {
                filteredStores.add(store);
            }
        }
        storeAdapter.updateStoreList(filteredStores);
    }

    private void filterProducts(String query) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(query)) {
                filteredProducts.add(product);
            }
        }
        productAdapter.updateProductList(filteredProducts);
    }

    private void sortProductsByPrice(boolean cheapestFirst) {
        if (cheapestFirst) {
            productList.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        } else {
            productList.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        }
        productAdapter.updateProductList(productList); // Update the list in the adapter
    }
}