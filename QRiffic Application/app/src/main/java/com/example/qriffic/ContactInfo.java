package com.example.qriffic;

/**
 * This class defines a player's contact info
 */
public class ContactInfo {

    private String country;
    private String city;
    private String phone;
    private String email;

    /**
     * This is the constructor for a ContactInfo object
     * @param country
     * The country as a string
     * @param city
     * The city as a string
     * @param phone
     * The phone number as a string
     * @param email
     * The email address as a string
     */
    public ContactInfo(String country, String city, String phone, String email) {
        this.country = country;
        this.city = city;
        this.phone = phone;
        this.email = email;
    }

    /**
     * This method returns the country of a ContactInfo object
     * @return
     * The country as a string
     */
    public String getCountry() {
        return country;
    }

    /**
     * This method returns the city of a ContactInfo object
     * @return
     * The city as a string
     */
    public String getCity() {
        return city;
    }

    /**
     * This method returns the phone number of a ContactInfo object
     * @return
     * The email address as a string
     */
    public String getPhone() {
        return phone;
    }

    /**
     * This method returns the email address of a ContactInfo object
     * @return
     * The email address as a string
     */
    public String getEmail() {
        return email;
    }

}
