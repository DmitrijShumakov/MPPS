package com.example.mpps.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mpps.Model.Category;
import com.example.mpps.Adapters.CategoryAdapter;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CATEGORY = 1;
    RecyclerView categoryRecyclerView;
    ArrayList<Category> categoryList;
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);

        // Initialize category list with images
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Daržovės ir vaisiai", R.drawable.darzoves));
        categoryList.add(new Category("Pieno gaminiai ir kiaušiniai", R.drawable.pieno));
        categoryList.add(new Category("Mėsa ir žuvis", R.drawable.mesa));
        categoryList.add(new Category("Bakalėja", R.drawable.bakaleja));

        // Set up RecyclerView
        categoryAdapter = new CategoryAdapter(this, categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(CategoryActivity.this, AppMainActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CATEGORY);
                return true;
            } else if (id == R.id.nav_categories) {
                return true;
            } else if (id == R.id.nav_cart) {
                Intent intent = new Intent(CategoryActivity.this, CartActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CATEGORY);
                return true;
            } else if (id == R.id.nav_account) {
                Intent intent = new Intent(CategoryActivity.this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CATEGORY);
                return true;
            }
            return false;
        });

        // Set default selected item in bottom navigation as categories
        bottomNavigationView.setSelectedItemId(R.id.nav_categories);
    }
}
