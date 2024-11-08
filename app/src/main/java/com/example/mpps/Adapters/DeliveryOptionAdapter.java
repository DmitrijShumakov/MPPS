// DeliveryOptionAdapter.java
package com.example.mpps.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpps.Model.DeliveryOption;
import com.example.mpps.R;

import java.util.List;

public class DeliveryOptionAdapter extends RecyclerView.Adapter<DeliveryOptionAdapter.ViewHolder> {

    private List<DeliveryOption> deliveryOptions;
    private int selectedPosition = -1;
    private OnDeliveryOptionSelectedListener listener;

    public DeliveryOptionAdapter(List<DeliveryOption> deliveryOptions, OnDeliveryOptionSelectedListener listener) {
        this.deliveryOptions = deliveryOptions;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.delivery_option_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DeliveryOption option = deliveryOptions.get(position);
        holder.title.setText(option.getTitle());
        holder.price.setText(option.getPrice());
        holder.icon.setImageResource(option.getIconResId());

        holder.cardView.setCardBackgroundColor(
                position == selectedPosition ? 0xFFDDDDDD : 0xFFFFFFFF // Change background if selected
        );

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onDeliveryOptionSelected(option);
        });
    }

    @Override
    public int getItemCount() {
        return deliveryOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView icon;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.deliveryOptionTitle);
            price = itemView.findViewById(R.id.deliveryOptionPrice);
            icon = itemView.findViewById(R.id.deliveryOptionIcon);
            cardView = (CardView) itemView;
        }
    }

    public interface OnDeliveryOptionSelectedListener {
        void onDeliveryOptionSelected(DeliveryOption option);
    }
}
