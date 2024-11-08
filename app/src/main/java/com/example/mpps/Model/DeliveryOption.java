package com.example.mpps.Model;


public class DeliveryOption {
    private String title;
    private String price;
    private int iconResId;

    public DeliveryOption(String title, String price, int iconResId) {
        this.title = title;
        this.price = price;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public int getIconResId() {
        return iconResId;
    }
}
