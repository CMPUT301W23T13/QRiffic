package com.example.qriffic;

import java.util.ArrayList;

/**
 * This class defines a player profile
 */
public class PlayerProfile {

    private Player playerId;
    private ContactInfo contactInfo;
    private int highScore;
    private int lowScore;
    private ArrayList<QRCode> captured;

    /**
     * Constructor for PlayerProfile
     * @param playerId
     * @param contactInfo
     */
    public PlayerProfile(Player playerId, ContactInfo contactInfo) {

        this.playerId = playerId;
        this.contactInfo = contactInfo;
        highScore = 0;
        lowScore = 0;
        captured = new ArrayList<QRCode>();
    }


}
