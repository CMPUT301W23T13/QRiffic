package com.example.qriffic;

/**
 * This class defines a GeoLocation
 */
public class GeoLocation {

    private double longitude;
    private double latitude;
    private String city;

    public GeoLocation() {}

    /**
     * This is the constructor for a Location object
     * @param longitude
     * The longitude as a string
     * @param latitude
     * The latitude as a string
     */
    public GeoLocation(double longitude, double latitude, String city) {

        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
    }

    /**
     * This method returns the longitude of a Location object
     * @return
     * The longitude as a double
     */
    public double getLongitude() {
        return longitude;
    }
    /**
     * This method returns the latitude of a Location object
     * @return
     * The latitude as a double
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * This method returns the city of a Location object
     * @return
     * The city as a string
     */
    public String getCity() {
        return city;
    }
}
