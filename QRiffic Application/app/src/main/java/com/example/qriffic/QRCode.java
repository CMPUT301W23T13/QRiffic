package com.example.qriffic;

import java.security.NoSuchAlgorithmException;

/**
 * This class defines a QRCode object
 */
public class QRCode implements Comparable {

    private int score;
    //private LocationImage locationImage
    private Location location;
    private String idHash;
    private String name;

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
     * This is a constructor for a QRCode object containing a location and location image
     * @param rawString
     * The string from scanning the QR code
     * @param location
     * @throws NoSuchAlgorithmException
     * From Hash class
     */
    public QRCode(String rawString, Location location) throws NoSuchAlgorithmException {
        // REMEMBER TO ADD LOCATIONIMAGE TO PARAMETERS AT SOME POINT

        //this.locationImage = locationImage;
        this.location = location;
        this.name = "UNNAMED MONSTER";
        this.idHash = new Hash(rawString).getHash();
        this.score = 0; // should be calculated here, new class?
    }

    /**
     * This is a constructor for a QRCode object containing a location image, but no location
     * @param rawString
     * The string from scanning the QR code
     * @throws NoSuchAlgorithmException
     * From Hash class
     */
    public QRCode(String rawString) throws NoSuchAlgorithmException {
        // REMEMBER TO ADD LOCATIONIMAGE TO PARAMETERS AT SOME POINT

        //this.locationImage = locationImage;
        this.location = new Location("", "");
        this.name = "UNNAMED MONSTER";
        this.idHash = new Hash(rawString).getHash();
        this.score = 0; // should be calculated here, new class?
    }


    /**
     * This is a constructor for a QRCode object containing no location image, nor location
     * @param rawString
     * The string from scanning the QR code
     * @throws NoSuchAlgorithmException
     * From Hash class
     */
     public QRCode(String rawString, int dummy) throws NoSuchAlgorithmException {
        // Ignore the dummy parameter, its just so we dont get an error, and the javadoc isn't dangling

        //this.locationImage = null or something, idk;
        this.location = new Location("", "");
        this.name = "UNNAMED MONSTER";
        this.idHash = new Hash(rawString).getHash();
        this.score = 0; // should be calculated here, new class?
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
}
