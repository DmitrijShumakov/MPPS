package com.example.mpps.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpps.Adapters.AddressAdapter;
import com.example.mpps.Adapters.CartAdapter;
import com.example.mpps.Adapters.DeliveryOptionAdapter;
import com.example.mpps.Model.Cart;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.Model.DeliveryOption;
import com.example.mpps.Model.Address;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView, deliveryOptionsRecyclerView, addressRecyclerView;
    private CartAdapter cartAdapter;
    private AddressAdapter addressAdapter;
    private ArrayList<Cart> cartItems;
    private ArrayList<Address> addressList = new ArrayList<>();
    private TextView totalAmount, emptyCartMessage, deliveryAddressOption, pickupLocationOption, paymentMethodOption;
    private Button checkoutButton;
    private View expandableAddressSection, expandablePickupLocationSection, expandablePaymentMethodSection;
    private DatabaseHelper db;
    private int userId;
    private Address selectedAddress = null; // Store the selected Address object
    private double deliveryCost = 0.0; // Variable to store selected delivery cost
    private TextView deliveryTypeTitle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = new DatabaseHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalAmount = findViewById(R.id.totalAmount);
        checkoutButton = findViewById(R.id.checkoutButton);
        emptyCartMessage = findViewById(R.id.emptyCartMessage);
        deliveryAddressOption = findViewById(R.id.deliveryAddressOption);
        pickupLocationOption = findViewById(R.id.pickupLocationOption);
        paymentMethodOption = findViewById(R.id.paymentMethodOption);
        expandableAddressSection = findViewById(R.id.expandableAddressSection);
        expandablePickupLocationSection = findViewById(R.id.expandablePickupLocationSection);
        expandablePaymentMethodSection = findViewById(R.id.expandablePaymentMethodSection);
        deliveryOptionsRecyclerView = findViewById(R.id.deliveryOptionsRecyclerView);
        addressRecyclerView = findViewById(R.id.addressRecyclerView);
        deliveryTypeTitle = findViewById(R.id.deliveryTypeTitle); // Add this line to find the TextView for "Pristatymo tipas"

        // Hide delivery address, pickup, and payment options initially
        deliveryAddressOption.setVisibility(View.GONE);
        pickupLocationOption.setVisibility(View.GONE);
        expandableAddressSection.setVisibility(View.GONE);
        expandablePickupLocationSection.setVisibility(View.GONE);
        expandablePaymentMethodSection.setVisibility(View.GONE);
        deliveryTypeTitle.setVisibility(View.GONE); // Initially hide it

        cartItems = loadCartItems();
        cartAdapter = new CartAdapter(this, cartItems, db, userId, unused -> calculateTotal());

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        calculateTotal();
        setupDeliveryOptions();

        deliveryAddressOption.setOnClickListener(v -> toggleSection(expandableAddressSection));
        pickupLocationOption.setOnClickListener(v -> {
            if (pickupLocationOption.getVisibility() == View.VISIBLE) {
                Intent mapIntent = new Intent(CartActivity.this, MapActivity.class);
                startActivityForResult(mapIntent, 1001);
            }
        });

        // Toggle payment method section
        paymentMethodOption.setOnClickListener(v -> toggleSection(expandablePaymentMethodSection));

        // Set listeners for payment options
        findViewById(R.id.paymentOptionCash).setOnClickListener(v -> {
            paymentMethodOption.setText("Mokėjimo būdas: Grynais");
            expandablePaymentMethodSection.setVisibility(View.GONE);
            Toast.makeText(this, "Pasirinkta mokėjimo būdas: Grynais", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.paymentOptionCard).setOnClickListener(v -> {
            paymentMethodOption.setText("Mokėjimo būdas: Kortele");
            expandablePaymentMethodSection.setVisibility(View.GONE);
            Toast.makeText(this, "Pasirinkta mokėjimo būdas: Kortele", Toast.LENGTH_SHORT).show();
        });

        setupAddressList();

        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Krepšelis tuščias.", Toast.LENGTH_SHORT).show();
                return;
            }

            String deliveryType = null;
            if (deliveryAddressOption.getVisibility() == View.VISIBLE) {
                deliveryType = "Pristatymas į namus";
            } else if (pickupLocationOption.getVisibility() == View.VISIBLE) {
                deliveryType = "Atsiemsiu pats";
            }

            String address = (deliveryType != null && deliveryType.equals("Pristatymas į namus") && selectedAddress != null) ? getFormattedAddress(selectedAddress) : null;
            String storeName = (deliveryType != null && deliveryType.equals("Atsiemsiu pats") && !pickupLocationOption.getText().toString().equals("Atsiemimo vieta")) ? pickupLocationOption.getText().toString() : null;
            String paymentMethod = paymentMethodOption.getText().toString().replace("Mokėjimo būdas: ", "");

            // Check if all required fields are selected
            if (deliveryType == null) {
                Toast.makeText(this, "Pasirinkite pristatymo būdą.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (deliveryType.equals("Pristatymas į namus") && address == null) {
                Toast.makeText(this, "Pasirinkite pristatymo adresą.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (deliveryType.equals("Atsiemsiu pats") && storeName == null) {
                Toast.makeText(this, "Pasirinkite atsiėmimo vietą.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (paymentMethod.isEmpty() || paymentMethod.equals("Mokėjimo būdas")) {
                Toast.makeText(this, "Pasirinkite mokėjimo būdą.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proceed with order if all fields are valid
            String status = "Pateiktas";
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            double totalAmount = calculateTotalWithDelivery();

            // Insert the order into the database
            long orderId = db.insertOrder(userId, totalAmount, date, deliveryType, address, storeName, status);

            // Insert each item in the cart as an order item
            for (Cart item : cartItems) {
                db.insertOrderItem(orderId, item.getProductName(), item.getQuantity(), item.getTotalPrice());
            }

            // Clear the cart after placing the order
            db.clearCart(userId);
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();
            calculateTotal(); // Recalculate to update the total

            Toast.makeText(this, "Užsakymas pateiktas sėkmingai!", Toast.LENGTH_SHORT).show();

            // Redirect to order summary or main activity if needed
            // startActivity(new Intent(CartActivity.this, OrderSummaryActivity.class));
        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(CartActivity.this, AppMainActivity.class));
                return true;
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(CartActivity.this, CategoryActivity.class));
                return true;
            } else if (id == R.id.nav_cart) {
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(CartActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String storeName = data.getStringExtra("storeName");
            double storeLat = data.getDoubleExtra("storeLat", 0);
            double storeLng = data.getDoubleExtra("storeLng", 0);

            // Update pickup location option text with selected store
            pickupLocationOption.setText("Atsiemimo vieta: " + storeName);
            Toast.makeText(this, "Pasirinkta vieta: " + storeName, Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Cart> loadCartItems() {
        ArrayList<Cart> items = new ArrayList<>();
        Cursor cursor = db.getCartItems(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int productId = cursor.getInt(cursor.getColumnIndex("PRODUCT_ID"));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("QUANTITY"));

                Cursor productCursor = db.getProductById(productId);
                if (productCursor != null && productCursor.moveToFirst()) {
                    @SuppressLint("Range") String name = productCursor.getString(productCursor.getColumnIndex("NAME"));
                    @SuppressLint("Range") double price = productCursor.getDouble(productCursor.getColumnIndex("PRICE"));
                    items.add(new Cart(productId, name, price, quantity));
                }

                if (productCursor != null) {
                    productCursor.close();
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return items;
    }

    private void calculateTotal() {
        double total = 0;
        for (Cart item : cartItems) {
            total += item.getTotalPrice();
        }
        total += deliveryCost; // Include delivery cost
        totalAmount.setText("Kaina: €" + String.format("%.2f", total));

        if (cartItems.isEmpty()) {
            showEmptyCartMessage();
        } else {
            // Show delivery options, payment method, total amount, and delivery type title if cart is not empty
            emptyCartMessage.setVisibility(View.GONE);
            cartRecyclerView.setVisibility(View.VISIBLE);
            checkoutButton.setVisibility(View.VISIBLE);
            totalAmount.setVisibility(View.VISIBLE);
            deliveryOptionsRecyclerView.setVisibility(View.VISIBLE);
            paymentMethodOption.setVisibility(View.VISIBLE);
            deliveryTypeTitle.setVisibility(View.VISIBLE); // Show "Pristatymo tipas" when cart is not empty

            // Update checkout button text with total amount
            checkoutButton.setText("Apmokėti");
        }
    }

    private void showEmptyCartMessage() {
        // Show only the empty cart message
        emptyCartMessage.setVisibility(View.VISIBLE);
        cartRecyclerView.setVisibility(View.GONE);
        checkoutButton.setVisibility(View.GONE);
        totalAmount.setVisibility(View.GONE);
        deliveryOptionsRecyclerView.setVisibility(View.GONE);
        paymentMethodOption.setVisibility(View.GONE);
        deliveryTypeTitle.setVisibility(View.GONE); // Hide "Pristatymo tipas" when cart is empty

        // Hide delivery address and pickup options when cart is empty
        deliveryAddressOption.setVisibility(View.GONE);
        pickupLocationOption.setVisibility(View.GONE);
        expandableAddressSection.setVisibility(View.GONE);
        expandablePickupLocationSection.setVisibility(View.GONE);
    }



    private double calculateTotalWithDelivery() {
        double total = 0;
        for (Cart item : cartItems) {
            total += item.getTotalPrice();
        }
        total += deliveryCost; // Include delivery cost
        return total;
    }

    private String getFormattedAddress(Address address) {
        return address.getCity() + ", " + address.getStreet() + ", Namo nr.: " + address.getHouseNumber();
    }


    private void setupDeliveryOptions() {
        List<DeliveryOption> deliveryOptions = Arrays.asList(
                new DeliveryOption("Pristatymas į namus", "€3.00", R.drawable.ic_home_delivery),
                new DeliveryOption("Atsiemsiu pats", "€0.00", R.drawable.ic_self_pickup)
        );

        DeliveryOptionAdapter adapter = new DeliveryOptionAdapter(deliveryOptions, option -> {
            // Collapse both sections initially
            expandableAddressSection.setVisibility(View.GONE);
            expandablePickupLocationSection.setVisibility(View.GONE);

            // Show/hide delivery address or pickup location based on selected option
            if (option.getTitle().equals("Pristatymas į namus")) {
                deliveryCost = 3.0; // Set delivery cost
                deliveryAddressOption.setVisibility(View.VISIBLE); // Show delivery address
                pickupLocationOption.setVisibility(View.GONE); // Hide pickup location
                showHomeDeliveryAddress();
            } else if (option.getTitle().equals("Atsiemsiu pats")) {
                deliveryCost = 0.0; // Set no delivery cost
                deliveryAddressOption.setVisibility(View.GONE); // Hide delivery address
                pickupLocationOption.setVisibility(View.VISIBLE); // Show pickup location
                showPickupLocations();
            } else {
                // Hide both if no option is selected
                deliveryAddressOption.setVisibility(View.GONE);
                pickupLocationOption.setVisibility(View.GONE);
            }

            calculateTotal(); // Recalculate total with delivery cost
            Toast.makeText(this, "Pasirinktas pristatymo būdas: " + option.getTitle(), Toast.LENGTH_SHORT).show();
        });

        deliveryOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        deliveryOptionsRecyclerView.setAdapter(adapter);
    }


    private void toggleSection(View section) {
        section.setVisibility(section.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void showHomeDeliveryAddress() {
        if (selectedAddress != null) {
            deliveryAddressOption.setText("Pristatymo adresas: " + getFormattedAddress(selectedAddress));
        }
    }

    private void showPickupLocations() {
        pickupLocationOption.setText("Atsiemimo vieta");
    }

    private void setupAddressList() {
        Cursor cursor = db.getAllAddressesByUserId(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int addressId = cursor.getInt(cursor.getColumnIndex("ID"));
                @SuppressLint("Range") String addressName = cursor.getString(cursor.getColumnIndex("ADDRESS_NAME"));
                @SuppressLint("Range") String city = cursor.getString(cursor.getColumnIndex("CITY"));
                @SuppressLint("Range") String street = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                @SuppressLint("Range") String houseNumber = cursor.getString(cursor.getColumnIndex("APARTMENT_NUMBER"));
                addressList.add(new Address(addressId, addressName, city, street, houseNumber));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        // Create adapter and set the selected address functionality
        addressAdapter = new AddressAdapter(this, addressList, address -> {
            selectedAddress = address;
            showHomeDeliveryAddress();
            toggleSection(expandableAddressSection); // Hide address list after selection
            Toast.makeText(CartActivity.this, "Pasirinktas adresas: " + getFormattedAddress(address), Toast.LENGTH_SHORT).show();
        });

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressRecyclerView.setAdapter(addressAdapter);
    }
}