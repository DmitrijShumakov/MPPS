package com.example.mpps.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpps.Activity.ProductDetailActivity;
import com.example.mpps.Model.Product;
import com.example.mpps.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private ArrayList<Product> productList;
    private Context context;  // Use Context instead of specific activity

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;  // Assign context
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText("€" + product.getPrice());

            // Set product image (if available)
            int imageResourceId = itemView.getContext().getResources().getIdentifier(product.getName().toLowerCase(), "drawable", itemView.getContext().getPackageName());
            if (imageResourceId != 0) {
                productImage.setImageResource(imageResourceId);
            } else {
                productImage.setImageResource(R.drawable.ic_product_placeholder); // Default image
            }

            // Set onClickListener to navigate to ProductDetailActivity
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                itemView.getContext().startActivity(intent);
            });
        }
    }

    public void updateProductList(ArrayList<Product> newProductList) {
        productList = newProductList;
        notifyDataSetChanged();
    }
}
