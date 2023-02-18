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
    private String idHash;
    private String name;

    public QRCode(String rawString, String name, Location location) throws NoSuchAlgorithmException {
        // REMEMBER TO ADD LOCATION AND LOCATIONIMAGE TO PARAMETERS

        //this.locationImage = locationImage;
        //this.location = location;
        this.name = "UNNAMED MONSTER";
        this.rawString = rawString;
        this.score = 0;
        this.idHash = new Hash(rawString).getHash();
    }
}
