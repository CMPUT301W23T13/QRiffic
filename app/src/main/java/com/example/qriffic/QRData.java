package com.example.qriffic;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRData {

    private int score;
    private String idHash;
    private String name;
    private HashMap<String, HashMap<String, Object>> users;
    private ArrayList<fetchListener> listeners = new ArrayList<fetchListener>();

    /**
     * This is an empty constructor for a QRData object.
     * (Required for Firestore Custom Object Translation)
     */
    public QRData() {
        this.users = new HashMap<String, HashMap<String, Object>>();
    }

    /**
     * This is a constructor for a QRData object with no users.
     * @param idHash
     * The hashed string from scanning the QR code
     * @param score
     * The score of the QR code
     * @param name
     * The name of the QR code
     */
    public QRData(String idHash, int score, String name) {
        this.idHash = idHash;
        this.score = score;
        this.name = name;
        this.users = new HashMap<String, HashMap<String, Object>>();
    }

    /**
     * This is a constructor for a QRData object with all fields.
     * @param idHash
     * The hashed string from scanning the QR code
     * @param score
     * The score of the QR code
     * @param name
     * The name of the QR code
     * @param users
     * The users who have scanned the QR code
     */
    public QRData(String idHash, int score, String name, HashMap<String, HashMap<String, Object>> users) {
        this.idHash = idHash;
        this.score = score;
        this.name = name;
        this.users = users;
    }

    /**
     * This is a constructor for a QRData object with a QRCode object.
     * @param qr
     * The QRCode object to be converted to a QRData object
     */
    public QRData(QRCode qr) {
        this.idHash = qr.getIdHash();
        this.score = qr.getScore();
        this.name = qr.getName();
        this.users = new HashMap<String, HashMap<String, Object>>() {{
            put(qr.getUsername(), new HashMap<String, Object>() {{
                put("comment", qr.getComment());
                put("locationImage", qr.getLocationImage());
                put("geoLocation", qr.getGeoLocation());
                put("username", qr.getUsername());
            }});
        }};
    }

    /**
     * Adds a user to the QRData object from a QRCode object.
     * @param qr
     */
    public void addUser(QRCode qr) {
        this.addUser(qr.getUsername(), qr.getComment(), qr.getLocationImage(), qr.getGeoLocation());
    }

    /**
     * Adds a user to the QRData object given the user's username, comment,
     * location image, and geolocation for that QR code.
     * @param qr
     */
    public void addUser(String username, String comment, String locationImage, GeoLocation geoLocation) {
        this.users.put(username, new HashMap<String, Object>() {{
            put("comment", comment);
            put("locationImage", locationImage);
            put("geoLocation", geoLocation);
            put("username", username);
        }});
    }

    /**
     * Removes a user from the QRData object given the user's username.
     * @param username
     */
    public boolean removeUser(String username) {
        this.users.remove(username);
        if (this.users.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the score of the QR code.
     * @param score
     * the score of the QR code
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Sets the hashed string from scanning the QR code.
     * @param idHash
     * the hashed string from scanning the QR code
     */
    public void setIdHash(String idHash) {
        this.idHash = idHash;
    }

    /**
     * Sets the name of the QR code.
     * @param name
     * the name of the QR code
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the data of users who have scanned the QR code.
     * Avoid using this unless overwriting the entire list of users.
     * Instead prefer using addUser() and removeUser().
     * @param users
     * the data of users who have scanned the QR code as a HashMap
     */
    public void setUsers(HashMap<String, HashMap<String, Object>> users) {
        this.users = users;
    }

    /**
     * Returns the score of the QR code.
     * @return
     * the score of the QR code
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the hashed string from scanning the QR code.
     * @return
     * the hashed string from scanning the QR code
     */
    public String getIdHash() {
        return idHash;
    }

    /**
     * Returns the name of the QR code.
     * @return
     * the name of the QR code
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the data of users who have scanned the QR code.
     * @return
     * the data of users who have scanned the QR code as a HashMap
     */
    public HashMap<String, HashMap<String, Object>> getUsers() {
        return users;
    }

    /**
     * This method adds a fetchListener to the QRData object
     *
     * This block references the following web page:
     * Link: https://programming.guide/java/create-a-custom-event.html
     * Author: Unavailable
     * Date: 24/03/2023
     *
     * @param toAdd
     */
    public void addListener(fetchListener toAdd) {
        listeners.add(toAdd);
    }

    /**
     * This method calls all onFetchComplete() listeners
     *
     * This block references the following web page:
     * Link: https://programming.guide/java/create-a-custom-event.html
     * Author: Unavailable
     * Date: 24/03/2023
     *
     */
    public void fetchComplete() {
        for (fetchListener fl : listeners)
            fl.onFetchComplete();
    }

    /**
     * This method calls all onFetchFailure() listeners
     *
     * This block references the following web page:
     * Link: https://programming.guide/java/create-a-custom-event.html
     * Author: Unavailable
     * Date: 24/03/2023
     */
    public void fetchFailed() {
        for (fetchListener fl : listeners)
            fl.onFetchFailure();
    }
    
}
