package com.example.mpps.Model;

public class Address {
    private int id;
    private String addressName;
    private String city;
    private String street;
    private String houseNumber;

    // Constructor with city, street, and houseNumber
    public Address(int id, String addressName, String city, String street, String houseNumber) {
        this.id = id;
        this.addressName = addressName;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
