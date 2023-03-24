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



}
