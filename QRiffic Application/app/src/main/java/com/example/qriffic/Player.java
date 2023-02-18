package com.example.qriffic;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Player {

    private String userId;
    private String secret;

    // REFERENCE FOR SHA-256 HASH:
    // https://www.geeksforgeeks.org/sha-256-hash-in-java/
    // Article Contributed By: bilal-hungund
    // Be sure to check whether database contains an identical userId
    public Player(String userId) throws NoSuchAlgorithmException {
        this.userId = userId;

        // Set message digest
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Calculate message digest of userId and store in byteArray
        byte[] byteArray = md.digest(userId.getBytes(StandardCharsets.UTF_8));

        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, byteArray);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        this.secret = hexString.toString();
    }
}
