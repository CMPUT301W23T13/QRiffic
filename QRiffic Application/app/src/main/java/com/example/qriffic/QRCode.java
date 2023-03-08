package com.example.qriffic;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class defines a QRCode object
 */
public class QRCode implements Comparable {

    private int score;
//    private LocationImage locationImage
    private Location location;
    private String idHash;
    private String name;
    private String username;

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
     * This is an empty constructor for a QRCode object
     * (Required for Firestore Custom Object Translation)
     */
    public QRCode() {}

    /**
     * This is a constructor for a QRCode object
     * @param rawString
     * The string from scanning the QR code
     * @param location
     * The location of the QR code as a Location object
     * @param username
     * The username of the player who scanned the QR code
     */
    public QRCode(String rawString, Location location, String username) {
        // REMEMBER TO ADD LOCATIONIMAGE TO PARAMETERS AT SOME POINT

        this.idHash = new Hash(rawString).getHash();
        //this.locationImage = locationImage;
        this.location = location;
        this.username = username;


        // name and score probably need their own class, this is very long

        // last 6 digits of the hash
        String last6 = this.idHash.substring(this.idHash.length()-6);
        List<String> alphas = Arrays.asList("a", "b", "c", "d", "e", "f");
        // name generator
        List<String> subNames = Arrays.asList("Alpha", "Beta", "Charlie", "Delta", "Echo",
                "Foxtrot", "Golf", "Hotel", "India", "Juliet", "Kilo", "Lima", "Mike", "November",
                "Oscar", "Papa");
        int index = 0;
        this.name = "";
        for (int i=0; i<6; i++) {
            // if the character is a letter, set multiplier to its corresponding integer value
            if (alphas.contains(last6.charAt(i))) {
                index = (int) last6.charAt(i) - 87;
            }
            // if the character is a letter, set multiplier to its corresponding integer value
            if (alphas.contains(last6.charAt(i))) {
                index = (int) last6.charAt(i) - 87;
            }
            this.name += subNames.get(index);
        }

        // score generator
        int multiplier = 0;
        this.score = 0;
        for (int i=0; i<6; i++) {
            // if the character is a letter, set multiplier to its corresponding integer value
            if (alphas.contains(last6.charAt(i))) {
                multiplier = (int) last6.charAt(i) - 87;
            }
            // if the character is a letter, set multiplier to its corresponding integer value
            if (alphas.contains(last6.charAt(i))) {
                multiplier = (int) last6.charAt(i) - 87;
            }

            this.score += multiplier * 2^(5-i);
        }
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
     * This method returns the location of a QRCode object
     * @return
     * The location as a Location object
     */
    public Location getLocation() {
        return location;
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
}
