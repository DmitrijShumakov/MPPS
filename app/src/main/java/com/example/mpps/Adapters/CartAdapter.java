package com.example.mpps.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpps.Model.Cart;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<Cart> cartItems;
    private Context context;
    private DatabaseHelper db;
    private int userId;
    private Consumer<Void> calculateTotalCallback;

    public CartAdapter(Context context, ArrayList<Cart> cartItems, DatabaseHelper db, int userId, Consumer<Void> calculateTotalCallback) {
        this.context = context;
        this.cartItems = cartItems;
        this.db = db;
        this.userId = userId;
        this.calculateTotalCallback = calculateTotalCallback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartItems.get(position);
        holder.bind(cart, position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage, decreaseQuantity, increaseQuantity, removeProduct;

        public CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.cartProductName);
            productPrice = itemView.findViewById(R.id.cartProductPrice);
            productQuantity = itemView.findViewById(R.id.cartProductQuantity);
            productImage = itemView.findViewById(R.id.cartProductImage);  // Nuoroda į produkto paveikslėlį
            decreaseQuantity = itemView.findViewById(R.id.decreaseQuantity);
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity);
            removeProduct = itemView.findViewById(R.id.removeProduct);
        }

        public void bind(Cart cart, int position) {
            productName.setText(cart.getProductName());
            productPrice.setText("€" + String.format("%.2f", cart.getTotalPrice()));  // Formatuoti kainą su dviem dešimtainiais skaitmenimis
            productQuantity.setText(String.valueOf(cart.getQuantity()));

            // Nustatyti produkto paveikslėlį pagal produkto detales
            int imageResourceId = context.getResources().getIdentifier(
                    cart.getProductName().toLowerCase(), "drawable", context.getPackageName());
            if (imageResourceId != 0) {
                productImage.setImageResource(imageResourceId);
            } else {
                productImage.setImageResource(R.drawable.ic_product_placeholder);  // Numatytoji nuotrauka
            }

            // Kiekio mažinimo mygtuko funkcionalumas
            decreaseQuantity.setOnClickListener(v -> {
                if (cart.getQuantity() > 1) {
                    cart.setQuantity(cart.getQuantity() - 1);
                    productQuantity.setText(String.valueOf(cart.getQuantity()));
                    db.updateCartItemQuantity(userId, cart.getProductId(), cart.getQuantity());
                    notifyItemChanged(position);
                    calculateTotalCallback.accept(null);
                }
            });

            // Kiekio didinimo mygtuko funkcionalumas
            increaseQuantity.setOnClickListener(v -> {
                cart.setQuantity(cart.getQuantity() + 1);
                productQuantity.setText(String.valueOf(cart.getQuantity()));
                db.updateCartItemQuantity(userId, cart.getProductId(), cart.getQuantity());
                notifyItemChanged(position);
                calculateTotalCallback.accept(null);
            });

            // Produkto pašalinimo mygtuko funkcionalumas
            removeProduct.setOnClickListener(v -> {
                db.removeCartItem(userId, cart.getProductId());
                cartItems.remove(position);
                notifyItemRemoved(position);
                calculateTotalCallback.accept(null);
                Toast.makeText(context, "Produktas pašalintas iš krepšelio", Toast.LENGTH_SHORT).show();

                // Patikrinti, ar krepšelis yra tuščias po pašalinimo
                if (cartItems.isEmpty()) {
                    calculateTotalCallback.accept(null);
                }
            });
        }
    }
}
