package com.example.mpps.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mpps.Adapters.OrderItemAdapter;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.Model.Order;
import com.example.mpps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OrderDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private TextView tvDeliveryAddress, tvDeliveryType, tvTotalAmount, tvOrderDate, tvOrderStatus;
    private RecyclerView orderItemsRecyclerView;
    private Button cancelOrderButton;
    private Order currentOrder;
    private static final long CANCEL_TIME_LIMIT = TimeUnit.MINUTES.toMillis(5); // 5 minutes in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        db = new DatabaseHelper(this);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvDeliveryType = findViewById(R.id.tvDeliveryType);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);

        // Retrieve order ID from intent
        int orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId != -1) {
            currentOrder = db.getOrderById(orderId);
            displayOrderDetails(currentOrder);
        }

        // Back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
            startActivity(intent);
            finish();
        });

        // Cancel Order functionality
        cancelOrderButton.setOnClickListener(v -> cancelOrder());

        // Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_account); // Set Account as selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(OrderDetailActivity.this, AppMainActivity.class));
                return true;
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(OrderDetailActivity.this, CategoryActivity.class));
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(OrderDetailActivity.this, CartActivity.class));
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(OrderDetailActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void displayOrderDetails(Order order) {
        if (order != null) {
            tvDeliveryAddress.setText("Pristatymo adresas: " + (order.getAddress() != null ? order.getAddress() : order.getStoreName()));
            tvDeliveryType.setText("Pristatymo tipas: " + order.getDeliveryType());
            tvTotalAmount.setText("Visa suma: €" + String.format("%.2f", order.getTotalAmount()));
            tvOrderDate.setText("Data: " + order.getDate());
            tvOrderStatus.setText("Statusas: " + order.getStatus());

            // Setup RecyclerView with order items
            ArrayList<Order.OrderItem> orderItems = (ArrayList<Order.OrderItem>) order.getItems();
            OrderItemAdapter adapter = new OrderItemAdapter(this, orderItems);
            orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            orderItemsRecyclerView.setAdapter(adapter);

            // Check if the order is already canceled or if it’s been more than 5 minutes since the order date
            if ("Atšauktas".equals(order.getStatus()) || !isCancelable(order.getDate())) {
                cancelOrderButton.setVisibility(View.GONE);
            } else {
                cancelOrderButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isCancelable(String orderDateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date orderDate = sdf.parse(orderDateString);
            if (orderDate == null) return false;
            long timeSinceOrder = System.currentTimeMillis() - orderDate.getTime();
            return timeSinceOrder <= CANCEL_TIME_LIMIT;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void cancelOrder() {
        if (db.cancelOrder(currentOrder.getOrderId())) {
            currentOrder.setStatus("Atšauktas"); // Update the status in the current order object
            tvOrderStatus.setText("Statusas: Atšauktas");
            Toast.makeText(this, "Užsakymas buvo sėkmingai atšauktas.", Toast.LENGTH_SHORT).show();
            cancelOrderButton.setVisibility(View.GONE); // Hide the button after cancellation
        } else {
            Toast.makeText(this, "Nepavyko atšaukti užsakymo.", Toast.LENGTH_SHORT).show();
        }
    }
}
