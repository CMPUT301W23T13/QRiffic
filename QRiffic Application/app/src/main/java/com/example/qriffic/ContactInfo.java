package com.example.qriffic;

public class ContactInfo {

    private String address;
    private String street;
    private String country;
    private String city;
    private String phone;
    private String email;

    public ContactInfo(String address, String street, String country, String city, String phone, String email) {
        this.address = address;
        this.street = street;
        this.country = country;
        this.city = city;
        this.phone = phone;
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public String getStreet() {
        return street;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
