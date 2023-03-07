package com.example.qriffic;

import java.util.ArrayList;

/**
 * This class defines a player profile
 */
public class PlayerProfile {

    private String username;
    private String uniqueID;
    private String email;
    private String phoneNum;
    private int highScore;
    private int lowScore;
    private ArrayList<QRCode> captured;

    /**
     * This is an empty constructor for a PlayerProfile object
     * (Required for Firestore Custom Object Translation)
     */
    public PlayerProfile() {
    }

    /**
     * This is a constructor for a PlayerProfile object
     * @param username
     * The player's username as a String
     * @param uniqueID
     * The player's unique ID as a string
     * @param email
     * The player's email address as a string
     * @param phoneNum
     * The player's phone number as a string
     * @param highScore
     * The player's high score as an integer
     * @param lowScore
     * The player's low score as an integer
     * @param captured
     * The player's captured QRCodes as an ArrayList of QRCode objects
     */
    public PlayerProfile(String username, String uniqueID, String email, String phoneNum, int highScore, int lowScore, ArrayList<QRCode> captured) {
        this.username = username;
        this.uniqueID = uniqueID;
        this.email = email;
        this.phoneNum = phoneNum;
        this.highScore = highScore;
        this.lowScore = lowScore;
        this.captured = captured;
    }

    /**
     * This method returns the username of a PlayerProfile object
     * @return
     * The player's username as a string
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method returns the uniqueID of a PlayerProfile object
     * @return
     * The player's uniqueID as a string
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * This method returns the email address of a PlayerProfile object
     * @return
     * The email address as a string
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method returns the phone number of a PlayerProfile object
     * @return
     * The phone number as a string
     */
    public String getPhoneNum() {
        return phoneNum;
    }

    /**
     * This method returns the high score of a PlayerProfile object
     * @return
     * The high score as an integer
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * This method returns the high score of a PlayerProfile object
     * @return
     * The high score as an integer
     */
    public int getLowScore() {
        return lowScore;
    }

    /**
     * This method returns the list of captured QRCodes of a PlayerProfile object
     * @return
     * The captured QRCodes as an ArrayList of QRCode objects
     */
    public ArrayList<QRCode> getCaptured() {
        return captured;
    }

    /**
     * This method adds a QRCode object to the list of captured QRCodes of a PlayerProfile object
     * @param qrCode
     * The QRCode object to be added to the ArrayList of QRCode objects
     */
    public void addQRCode(QRCode qrCode) {

        // do we allow players to add identical QRCodes? Do we cap the amount of QR codes you can collect?
        captured.add(qrCode);
        updateHighScore(qrCode.getScore());
    }

    /**
     * This method deletes a QRCode object from the list of captured QRCodes of a PlayerProfile object
     * @param qrCode
     * The QRCode object to be deleted from the ArrayList of QRCode objects
     */
    public void deleteQRCode(QRCode qrCode) {

        if (captured.contains(qrCode) == false) {
            throw new IllegalArgumentException();
        }
        captured.remove(qrCode);
    }

    /**
     * This method updates the high score of a PlayerProfile object
     * @param score
     * The score to be compared to the current high score
     */
    public void updateHighScore(int score) {
        highScore = Math.max(highScore, score);
    }

    /**
     * This method updates the low score of a PlayerProfile object
     * @param score
     * The score to be compared to the current low score
     */
    public void updateLowScore(int score) {
        lowScore = Math.min(lowScore, score);
    }

}
