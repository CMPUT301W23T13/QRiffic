package com.example.qriffic;

import java.security.NoSuchAlgorithmException;

/**
 * This class defines a QRCode object
 */
public class QRCode {

    private int score;
    //private LocationImage locationImage
    private Location location;
    private String rawString;
    private Hash idHash;
    private String name;

    /**
     * This is a constructor for a QRCode object containing a location and location image
     * @param rawString
     * The string from scanning the QR code
     * @param location
     * @throws NoSuchAlgorithmException
     * From Hash class
     */
    public QRCode(String rawString, Location location) throws NoSuchAlgorithmException {
        // REMEMBER TO ADD LOCATIONIMAGE TO PARAMETERS AT SOME POINT

        //this.locationImage = locationImage;
        this.location = location;
        this.name = "UNNAMED MONSTER";
        this.rawString = rawString;
        this.idHash = new Hash(rawString);
        this.score = 0; // should be calculated here, new class?
    }

    /**
     * This is a constructor for a QRCode object containing a location image, but no location
     * @param rawString
     * The string from scanning the QR code
     * @throws NoSuchAlgorithmException
     * From Hash class
     */
    public QRCode(String rawString) throws NoSuchAlgorithmException {
        // REMEMBER TO ADD LOCATIONIMAGE TO PARAMETERS AT SOME POINT

        //this.locationImage = locationImage;
        this.location = new Location("", "");
        this.name = "UNNAMED MONSTER";
        this.rawString = rawString;
        this.idHash = new Hash(rawString);
        this.score = 0; // should be calculated here, new class?
    }


    /**
     * This is a constructor for a QRCode object containing no location image, nor location
     * @param rawString
     * The string from scanning the QR code
     * @throws NoSuchAlgorithmException
     * From Hash class
     */
     public QRCode(String rawString, int dummy) throws NoSuchAlgorithmException {
        // Ignore the dummy parameter, its just so we dont get an error, and the javadoc isn't dangling

        //this.locationImage = null or something, idk;
        this.location = new Location("", "");
        this.name = "UNNAMED MONSTER";
        this.rawString = rawString;
        this.idHash = new Hash(rawString);
        this.score = 0; // should be calculated here, new class?
     }
}
