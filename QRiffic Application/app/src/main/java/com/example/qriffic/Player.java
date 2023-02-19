package com.example.qriffic;

import java.security.NoSuchAlgorithmException;

/**
 * This class defines a player
 */
public class Player {

    private String userId;
    private Hash secret;

    /**
     * This is the constructor for a Player object
     * @param userId
     * The player's ID (username) as a string
     * @throws NoSuchAlgorithmException
     * From Hash class
     */
    public Player(String userId) throws NoSuchAlgorithmException {
        // Be sure to check whether database contains an identical userId, otherwise we have problems

        this.userId = userId;
        secret = new Hash(userId); // We don't have to do it this way, though Im sure this is sufficient
    }

    /**
     * This method returns the userId of a Player object
     * @return
     * The userId as a string
     */
    public String getUserId() {
        return userId;
    }

    /**
     * This method returns the secret code of a Player object
     * @return
     * The secret code as a string
     */
    public String getSecret() {
        return secret.getHash();
    }
}
