package com.example.qriffic;

public class Location {

    private String longitude;
    private String latitude;

    public Location(String longitude, String latitude) {

        // prime meridian equator???
        this.longitude = "0";
        this.latitude = "0";
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongLat() {
        return longitude + ", " + latitude;
    }
}
