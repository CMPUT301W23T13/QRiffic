package com.example.qriffic;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class defines a QRCode
 */
public class QRCode {

    //private int score;
    //private LocationImage locationImage;
    //private Location geoLocation;
    private String rawString;
    private String idHash;
    //private String name;

    /**
     * Constructor for QRCode
     * REFERENCE FOR SHA-256 HASH:
     * https://www.geeksforgeeks.org/sha-256-hash-in-java/
     * Article Contributed By: bilal-hungund
     * @param rawString
     * @throws NoSuchAlgorithmException
     */
    public QRCode(String rawString) throws NoSuchAlgorithmException {
        // additional attributes will be added later

        this.rawString = rawString;

        // Set message digest
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Calculate message digest of rawString and store in byteArray
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

    /**
     * This method returns the rawString from the QRCode
     * @return
     */
    public String getRawString() {
        return rawString;
    }

    /**
     * This method returns the id hash from the QRCode
     * @return
     */
    public String getIdHash() {
        return idHash;
    }
}
