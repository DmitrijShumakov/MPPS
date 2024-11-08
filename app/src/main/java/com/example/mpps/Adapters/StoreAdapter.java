package com.example.mpps.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpps.R;
import com.example.mpps.Model.Store;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private ArrayList<Store> storeList;
    private OnStoreClickListener listener;

    public StoreAdapter(ArrayList<Store> storeList, OnStoreClickListener listener) {
        this.storeList = storeList;
        this.listener = listener;
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        Store store = storeList.get(position);
        holder.bind(store);
        holder.itemView.setOnClickListener(v -> listener.onStoreClick(store.getId()));
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        public StoreViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(Store store) {
            TextView storeName = itemView.findViewById(R.id.storeName);
            ImageView storeLogo = itemView.findViewById(R.id.storeLogo);

            // Set store name
            storeName.setText(store.getName());

            // Set store logo
            int logoResourceId = itemView.getContext().getResources().getIdentifier(store.getLogo(), "drawable", itemView.getContext().getPackageName());
            if (logoResourceId != 0) {
                storeLogo.setImageResource(logoResourceId);
            } else {
                storeLogo.setImageResource(R.drawable.ic_store_placeholder);
            }
        }
    }

    public void updateStoreList(ArrayList<Store> newStoreList) {
        storeList = newStoreList;
        notifyDataSetChanged();
    }

    public interface OnStoreClickListener {
        void onStoreClick(int storeId);
    }
}
