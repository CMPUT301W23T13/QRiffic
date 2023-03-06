package com.example.qriffic;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class defines the hash of some input
 */
public class Hash {
    private String hash;

    /**
     * This is the constructor for a Hash object
     * @param preHash
     * The input string to be hashed
     * @throws NoSuchAlgorithmException
     * From MessageDigest.getInstance()
     */
    public Hash(String preHash) {

        // REFERENCE:
        // https://www.geeksforgeeks.org/sha-256-hash-in-java/
        // Article Contributed By: bilal-hungund

        // Create message digest
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm");
        }

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

    /**
     * This method returns the hash of a Hash object
     * @return
     * The hash value as a string
     */
    public String getHash(){
        return hash;
    }
}
