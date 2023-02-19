package com.example.qriffic;

/**
 * This class defines a location on earth
 */
public class Location {

    private String longitude;
    private String latitude;

    /**
     * This is the constructor for a Location object
     * @param longitude
     * The longitude as a string
     * @param latitude
     * The latitude as a string
     */
    public Location(String longitude, String latitude) {

        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * This method returns the longitude of a Location object
     * @return
     * The longitude as a string
     */
    public String getLongitude() {
        return longitude;
    }
    /**
     * This method returns the latitude of a Location object
     * @return
     * The latitude as a string
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * This method returns the longitude and latitude of a Location object
     * @return
     * The longitude and latitude combined, separated by a comma, as one string
     */
    public String getLongLat() {
        return longitude + ", " + latitude;
    }
}
