package com.example.mpps.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProductDetailActivity extends AppCompatActivity {

    DatabaseHelper db;
    TextView productName, productPrice, productIngredients, productNutrition, quantityText;
    ImageView productImage, backArrow;
    Button decreaseQuantityButton, increaseQuantityButton, addToCartButton;
    int quantity = 1;  // Pradinis kiekis
    int userId; // Vartotojo ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = new DatabaseHelper(this);

        // Gauti prisijungusio vartotojo ID iš SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1); // Pakeiskite -1 su numatytąja reikšme

        if (userId == -1) {
            Toast.makeText(this, "Vartotojas nerastas, prisijunkite iš naujo.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productIngredients = findViewById(R.id.productIngredients);
        productNutrition = findViewById(R.id.productNutrition);
        productImage = findViewById(R.id.productImage);
        backArrow = findViewById(R.id.backArrow);
        quantityText = findViewById(R.id.quantityText);
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        addToCartButton = findViewById(R.id.addToCartButton);

        Intent intent = getIntent();
        int productId = intent.getIntExtra("product_id", -1);

        if (productId != -1) {
            loadProductDetails(productId);
        }

        // Atgal mygtuko funkcionalumas
        backArrow.setOnClickListener(v -> finish());

        // Kiekio mygtukų funkcionalumas
        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });

        increaseQuantityButton.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        // "Pridėti į krepšelį" mygtuko funkcionalumas
        addToCartButton.setOnClickListener(v -> {
            boolean isAdded = db.addToCart(userId, productId, quantity); // Naudojame userId
            if (isAdded) {
                Toast.makeText(ProductDetailActivity.this, "Prekė pridėta į krepšelį", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductDetailActivity.this, "Nepavyko atnaujinti krepšelio", Toast.LENGTH_SHORT).show();
            }
        });


        // Apatinės navigacijos nustatymai
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent1 = new Intent(ProductDetailActivity.this, AppMainActivity.class);
                startActivity(intent1);
                return true;
            } else if (id == R.id.nav_categories) {
                Intent intent1 = new Intent(ProductDetailActivity.this, CategoryActivity.class);
                startActivity(intent1);
                return true;
            } else if (id == R.id.nav_cart) {
                Intent intent1 = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(intent1);
                return true;
            } else if (id == R.id.nav_account) {
                Intent intent1 = new Intent(ProductDetailActivity.this, SettingsActivity.class);
                startActivity(intent1);
                return true;
            }

            return false;
        });
    }

    private void loadProductDetails(int productId) {
        Cursor productCursor = db.getProductById(productId);
        if (productCursor.moveToFirst()) {
            String name = productCursor.getString(1);
            double price = productCursor.getDouble(2);
            String ingredients = productCursor.getString(4);
            String nutrition = productCursor.getString(5);

            productName.setText(name);
            productPrice.setText("€" + price);
            productIngredients.setText("Sudedamosios dalys: " + ingredients);
            productNutrition.setText("Maistinė vertė: " + nutrition);

            int imageResourceId = getResources().getIdentifier(name.toLowerCase(), "drawable", getPackageName());
            if (imageResourceId != 0) {
                productImage.setImageResource(imageResourceId);
            } else {
                productImage.setImageResource(R.drawable.ic_product_placeholder);
            }
        }
        productCursor.close();
    }
}
