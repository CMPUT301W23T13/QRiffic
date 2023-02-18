package com.example.qriffic;

public class ContactInfo {

    private String country;
    private String city;
    private String phone;
    private String email;

    public ContactInfo(String country, String city, String phone, String email) {
        this.country = country;
        this.city = city;
        this.phone = phone;
        this.email = email;
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

}
