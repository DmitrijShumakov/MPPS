package com.example.mpps.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mpps.Model.Order;
import com.example.mpps.R;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final List<Order.OrderItem> items;
    private final Context context;

    public OrderItemAdapter(Context context, List<Order.OrderItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order.OrderItem item = items.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvItemQuantity.setText("Kiekis: " + item.getQuantity());
        holder.tvItemPrice.setText("€" + String.format("%.2f", item.getPrice()));  // Format price with two decimal points

        // Set item image based on the item name, similar to CartAdapter
        int imageResourceId = context.getResources().getIdentifier(
                item.getName().toLowerCase(), "drawable", context.getPackageName());
        if (imageResourceId != 0) {
            holder.ivItemImage.setImageResource(imageResourceId);
        } else {
            holder.ivItemImage.setImageResource(R.drawable.ic_product_placeholder);  // Default image
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemQuantity, tvItemPrice;
        ImageView ivItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
        }
    }
}
