package com.example.mpps.Model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int storeId;
    private String ingredients;  // New field
    private String nutrition;    // New field
    private String category;

    public Product(int id, String name, double price, int storeId, String ingredients, String nutrition, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.storeId = storeId;
        this.ingredients = ingredients;
        this.nutrition = nutrition;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }
}
