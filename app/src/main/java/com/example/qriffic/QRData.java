package com.example.qriffic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a QRMon for use in the database
 */
public class QRData {
    /**
     * The score of the QRMon
     */
    private int score;
    /**
     * The idHash of the QRMon
     */
    private String idHash;
    /**
     * The name of the QRMon
     */
    private String name;
    /**
     * A hashmap containing all of the users that have scanned the QRMon, keyed by their username and containing values with their associated data
     */
    private HashMap<String, HashMap<String, Object>> users;
    /**
     * Contains listeners used when fetching data from the database
     */
    private final ArrayList<fetchListener> listeners = new ArrayList<fetchListener>();

    /**
     * This is an empty constructor for a QRData object.
     * (Required for Firestore Custom Object Translation)
     */
    public QRData() {
        this.users = new HashMap<String, HashMap<String, Object>>();
    }

    /**
     * This is a constructor for a QRData object with no users.
     *
     * @param idHash The hashed string from scanning the QR code
     * @param score  The score of the QR code
     * @param name   The name of the QR code
     */
    public QRData(String idHash, int score, String name) {
        this.idHash = idHash;
        this.score = score;
        this.name = name;
        this.users = new HashMap<String, HashMap<String, Object>>();
    }

    /**
     * This is a constructor for a QRData object with all fields.
     *
     * @param idHash The hashed string from scanning the QR code
     * @param score  The score of the QR code
     * @param name   The name of the QR code
     * @param users  The users who have scanned the QR code and their associated data
     */
    public QRData(String idHash, int score, String name, HashMap<String, HashMap<String, Object>> users) {
        this.idHash = idHash;
        this.score = score;
        this.name = name;
        this.users = users;
    }

    /**
     * This is a constructor for a QRData object with a QRCode object.
     *
     * @param qr The QRCode object to be converted to a QRData object
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
     *
     * @param qr The qr which contains the userdata to be added
     */
    public void addUser(QRCode qr) {
        this.addUser(qr.getUsername(), qr.getComment(), qr.getLocationImage(), qr.getGeoLocation());
    }

    /**
     * Adds a user to the QRData object given the user's username, comment,
     * location image, and geolocation for that QR code.
     *
     * @param username      The username of the user
     * @param comment       The comment the user left on the QR code
     * @param locationImage The location image that the user took
     * @param geoLocation   An object representing the geolocation of the user
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
     *
     * @param username The username of the user to be removed
     * @return true if no users are associated with the QR after the remove, false if not
     */
    public boolean removeUser(String username) {
        this.users.remove(username);
        return this.users.size() == 0;
    }

    /**
     * Sets the score of the QR code.
     *
     * @param score The score of the QR code
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Sets the hashed string from scanning the QR code.
     *
     * @param idHash The hashed string from scanning the QR code
     */
    public void setIdHash(String idHash) {
        this.idHash = idHash;
    }

    /**
     * Sets the name of the QR code.
     *
     * @param name The name of the QR code
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the data of users who have scanned the QR code.
     * Avoid using this unless overwriting the entire list of users.
     * Instead prefer using addUser() and removeUser().
     *
     * @param users The data of users who have scanned the QR code as a HashMap
     */
    public void setUsers(HashMap<String, HashMap<String, Object>> users) {
        this.users = users;
    }

    /**
     * Returns the score of the QR code.
     *
     * @return The score of the QR code
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the hashed string from scanning the QR code.
     *
     * @return The hashed string from scanning the QR code
     */
    public String getIdHash() {
        return idHash;
    }

    /**
     * Returns the name of the QR code.
     *
     * @return The name of the QR code
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the data of users who have scanned the QR code.
     *
     * @return The data of users who have scanned the QR code as a HashMap
     */
    public HashMap<String, HashMap<String, Object>> getUsers() {
        return users;
    }

    /**
     * This method adds a fetchListener to the QRData object
     * <p>
     * This block references the following web page:
     * Link: https://programming.guide/java/create-a-custom-event.html
     * Author: Unavailable
     * Date: 24/03/2023
     *
     * @param toAdd The fetchListener to be added
     */
    public void addListener(fetchListener toAdd) {
        listeners.add(toAdd);
    }

    /**
     * This method calls all onFetchComplete() listeners
     * <p>
     * This block references the following web page:
     * Link: https://programming.guide/java/create-a-custom-event.html
     * Author: Unavailable
     * Date: 24/03/2023
     */
    public void fetchComplete() {
        for (fetchListener fl : listeners)
            fl.onFetchComplete();
    }

    /**
     * This method calls all onFetchFailure() listeners
     * <p>
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
