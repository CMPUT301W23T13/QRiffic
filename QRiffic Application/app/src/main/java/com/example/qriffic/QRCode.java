package com.example.qriffic;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

    // REFERENCE:
    // https://www.geeksforgeeks.org/sha-256-hash-in-java/
    public QRCode(String rawString, String name, Location location) throws NoSuchAlgorithmException {
        // REMEMBER TO ADD LOCATION AND LOCATIONIMAGE TO PARAMETERS

        //this.locationImage = locationImage;
        //this.location = location;
        //this.name = DO THIS LATER;
        this.rawString = rawString;
        this.score = 0;

        // Create message digest
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // Calculate message digest of rawString
        byte[] byteArray = md.digest(rawString.getBytes(StandardCharsets.UTF_8));
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, byteArray);
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        this.idHash = hexString.toString();
    }
}
