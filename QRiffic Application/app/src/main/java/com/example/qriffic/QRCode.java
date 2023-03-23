package com.example.qriffic;


import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class defines a QRCode object
 */
public class QRCode implements Comparable {
    public QRCode(String name, long score) {
        this.name = name;
        this.score = (int) score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public void setIdHash(String idHash) {
        this.idHash = idHash;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This method sets the location image of the QR code
     * @param locationImage
     * The location image of the QR code
     */
    public void setLocationImage(Bitmap locationImage) {
        this.locationImage = locationImage;
    }

    /**
     * This method sets the comment of the QR code
     * @param comment
     * The comment of the QR code
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    private int score;
    private GeoLocation geoLocation;
    private String idHash;
    private String name;
    private String username;
    private Bitmap locationImage;
    private String comment;

    /**
     * This is an empty constructor for a QRCode object
     * (Required for Firestore Custom Object Translation)
     */
    public QRCode() {}

    /**
     * This is a constructor for a QRCode object
     * @param rawString
     * The string from scanning the QR code
     * @param geoLocation
     * The location of the QR code as a Location object
     * @param username
     * The username of the player who scanned the QR code
     */
    public QRCode(String rawString, GeoLocation geoLocation, String username, Bitmap locationImage,
                  String comment) {

        this.idHash = new Hash(rawString).getHash();
        this.locationImage = locationImage;
        this.geoLocation = geoLocation;
        this.username = username;
        this.comment = comment;

        // last 6 digits of the hash
        String last6 = this.idHash.substring(this.idHash.length()-6);

        // name generator
        List<String> names1 = Arrays.asList("Minuscule", "Lesser", "Reticulated", "Spotted",
                "Round", "Boxy", "Triangular", "Octagonal", "Hexagonal", "Aquatic", "Jungle",
                "Long", "Tall", "Short", "Great", "Grand");
        List<String> names2 = Arrays.asList(" Young", " Old", " Ancient", " Leaping", " Flying",
                " Burrowing", " Inverted", " Joyous", " Juvenile", " Mature", " Larval", " Adult",
                " Skimming"," Floating", " Fluffy", " Smooth");
        List<String> names3 = Arrays.asList("", " Abram's", " Idlar's", " Ritwik's", " Kunal's",
                " Carissa's", " Luke's", " Garrett's", " Alden's", " Asian", " North American",
                " South American", " European", " African", " Australian", " Canadian");
        List<String> names4 = Arrays.asList(" Fa", " Fo", " Foo", " As", " Ar", " Cho", " Nu",
                " Ti", " Lu", " Ka", " Sa", " So", " Do", " Re", " Mi", " La");
        List<String> names5 = Arrays.asList("", "cault", "mun", "sum", "oz", "teer", "yol", "fal",
                "ort", "ral", "ohm", "lo", "ber", "jah", "cham", "zod");
        List<String> names6 = Arrays.asList("el", "li", "tsa", "za", "malien", "ale", "sser", "ta",
                "shoo", "puff", "er", "tir", "gur", "nit", "sha", "mon");

        List<List<String>> subNames = new ArrayList<>();
        subNames.add(names1);
        subNames.add(names2);
        subNames.add(names3);
        subNames.add(names4);
        subNames.add(names5);
        subNames.add(names6);

        this.name = "";
        for (int i=0; i<6; i++) {
            this.name += subNames.get(i).get(Integer.parseInt(last6.substring(i, i+1), 16));
        }

        // score generator
        this.score = Integer.parseInt(last6, 16);
    }

    /**
     * This defines how we compare QRCodes (last 6 digits of the hash)
     * @param o
     * The object to be compared.
     * @return
     * 0 if equal, GT 0 if .this is less than o, LT 0 if .this is greater than o
     */
    @Override
    public int compareTo(Object o) {
        QRCode qrCode = (QRCode) o;
        return this.idHash.substring(idHash.length()-6)
                .compareTo(qrCode.idHash.substring(qrCode.idHash.length()-6));
    }

    /**
     * This method returns the score of a QRCode object
     * @return
     * The score as an integer
     */
    public int getScore() {
        return score;
    }

    /**
     * This method returns the GeoLocation of a QRCode object
     * @return
     * The geolocation as a GeoLocation object
     */
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    /**
     * This method returns the ID hash of a QRCode object
     * @return
     * The ID hash as an string
     */
    public String getIdHash() {
        return idHash;
    }

    /**
     * This method returns the name of a QRCode object
     * @return
     * The name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the username of a QRCode object
     * @return
     * The username as a String
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method returns the location image of a QRCode object
     * @return
     * The location image as a Bitmap
     */
    public Bitmap getLocationImage() {
        return locationImage;
    }

    /**
     * This method returns the comment of a QRCode object
     * @return
     * The comment as a String
     */
    public String getComment() {
        return comment;
    }
}

