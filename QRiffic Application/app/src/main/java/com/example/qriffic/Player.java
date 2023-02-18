package com.example.qriffic;

import java.security.NoSuchAlgorithmException;

public class Player {

    private String userId;
    private String secret;

    // Be sure to check whether database contains an identical userId, otherwise we have problems
    public Player(String userId) throws NoSuchAlgorithmException {

        this.userId = userId;
        secret = new Hash(userId).getHash();
    }
}
