package com.example.qriffic;

import java.security.NoSuchAlgorithmException;

/**
 * This class Defines a QRCode
 */
public class QRCode {

    private int score;
    //private LocationImage locationImage
    private Location location;
    private String rawString;
    private Hash idHash;
    private String name;

    // With location and locationImage
    public QRCode(String rawString, String name, Location location) throws NoSuchAlgorithmException {
        // REMEMBER TO ADD LOCATIONIMAGE TO PARAMETERS AT SOME POINT

        //this.locationImage = locationImage;
        this.location = location;
        this.name = "UNNAMED MONSTER";
        this.rawString = rawString;
        this.idHash = new Hash(rawString);
        this.score = 0;
    }

    // With locationImage but No location
    public QRCode(String rawString, String name) throws NoSuchAlgorithmException {
        // REMEMBER TO ADD LOCATIONIMAGE TO PARAMETERS AT SOME POINT

        //this.locationImage = locationImage;
        this.location = new Location("", "");
        this.name = "UNNAMED MONSTER";
        this.rawString = rawString;
        this.idHash = new Hash(rawString);
        this.score = 0;
    }

/*    // With no location nor locationImage
    public QRCode(String rawString, String name) throws NoSuchAlgorithmException {

        //this.locationImage = locationImage;
        this.location = new Location("", "");
        this.name = "UNNAMED MONSTER";
        this.rawString = rawString;
        this.idHash = new Hash(rawString);
        this.score = 0;
    }*/
}
