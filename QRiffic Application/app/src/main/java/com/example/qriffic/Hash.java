package com.example.qriffic;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    private String hash;

    // REFERENCE:
    // https://www.geeksforgeeks.org/sha-256-hash-in-java/
    // Article Contributed By: bilal-hungund
    public Hash(String preHash) throws NoSuchAlgorithmException {

        // Create message digest
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Calculate message digest of preHash
        byte[] byteArray = md.digest(preHash.getBytes(StandardCharsets.UTF_8));

        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, byteArray);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        hash = hexString.toString();
    }

    public String getHash(){
        return hash;
    }
}
