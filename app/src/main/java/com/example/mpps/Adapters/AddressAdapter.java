package com.example.mpps.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mpps.Model.Address;
import com.example.mpps.R;
import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final Context context;
    private final ArrayList<Address> addresses;
    private final AddressEditListener addressEditListener;
    private final AddressDeleteListener addressDeleteListener;
    private final AddressSelectionListener addressSelectionListener;

    // Constructor for CartActivity (selection only)
    public AddressAdapter(Context context, ArrayList<Address> addresses, AddressSelectionListener selectionListener) {
        this.context = context;
        this.addresses = addresses;
        this.addressSelectionListener = selectionListener;
        this.addressEditListener = null;
        this.addressDeleteListener = null;
    }

    // Constructor for AddressActivity (editing and deletion)
    public AddressAdapter(Context context, ArrayList<Address> addresses, AddressEditListener editListener, AddressDeleteListener deleteListener) {
        this.context = context;
        this.addresses = addresses;
        this.addressEditListener = editListener;
        this.addressDeleteListener = deleteListener;
        this.addressSelectionListener = null;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addresses.get(position);

        // Set the address title
        holder.tvAddressTitle.setText(address.getAddressName());

        // Set the detailed address in the format "City, Street, House Number"
        String detailedAddress = "Miestas: " + address.getCity() + ", Gatvė: " + address.getStreet() + ", Namo nr.: " + address.getHouseNumber();
        holder.tvAddressDetails.setText(detailedAddress);

        // Handle address selection in CartActivity
        if (addressSelectionListener != null) {
            holder.itemView.setOnClickListener(v -> addressSelectionListener.onAddressSelected(address));
        }

        // Handle address editing and deletion in AddressActivity
        if (addressEditListener != null && addressDeleteListener != null) {
            holder.btnDeleteAddress.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> addressEditListener.onEditAddress(address));
            holder.btnDeleteAddress.setOnClickListener(v -> addressDeleteListener.onDeleteAddress(address));
        } else {
            holder.btnDeleteAddress.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddressTitle;
        TextView tvAddressDetails;
        ImageView btnDeleteAddress;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddressTitle = itemView.findViewById(R.id.tvAddressTitle);
            tvAddressDetails = itemView.findViewById(R.id.tvAddressDetails);
            btnDeleteAddress = itemView.findViewById(R.id.btnDeleteAddress);
        }
    }

    // Interface for selecting an address
    public interface AddressSelectionListener {
        void onAddressSelected(Address address);
    }

    // Interface for editing an address
    public interface AddressEditListener {
        void onEditAddress(Address address);
    }

    // Interface for deleting an address
    public interface AddressDeleteListener {
        void onDeleteAddress(Address address);
    }
}
