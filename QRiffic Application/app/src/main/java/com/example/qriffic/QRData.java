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
     * This is an empty constructor for a QRData object
     * (Required for Firestore Custom Object Translation)
     */
    public QRData() {
        this.users= new HashMap<>();
    }

    /**
     * This is a constructor for a QRData object
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
        this.users = new HashMap<>();
    }

    public QRData(String idHash, int score, String name, HashMap<String, HashMap<String, Object>> users) {
        this.idHash = idHash;
        this.score = score;
        this.name = name;
        this.users = users;
    }

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

    public void addUser(QRCode qr) {
        this.addUser(qr.getUsername(), qr.getComment(), qr.getLocationImage(), qr.getGeoLocation());
    }

    public void addUser(String username, String comment, Bitmap locationImage, GeoLocation geoLocation) {
        this.users.put(username, new HashMap<String, Object>() {{
            put("comment", comment);
            put("locationImage", locationImage);
            put("geoLocation", geoLocation);
            put("username", username);
        }});
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
