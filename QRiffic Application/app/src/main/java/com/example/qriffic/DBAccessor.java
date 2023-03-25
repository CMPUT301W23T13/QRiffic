package com.example.qriffic;


import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used as controller to access the database
 */
public class DBAccessor {

    private FirebaseFirestore db;
    private CollectionReference playersColRef;
    private CollectionReference qrColRef;
    private CollectionReference mapColRef;
    private CollectionReference testColRef; // Temporary test collection

    /**
     * This is the constructor for the DBAccessor class
     */
    public DBAccessor() {
        this.db = FirebaseFirestore.getInstance();
        this.playersColRef = db.collection("Players");
        this.qrColRef = db.collection("QRs");
        this.mapColRef = db.collection("Map");
        this.testColRef = db.collection("TestCol"); // Temporary test collection
    }

    /**
     * This method adds/sets a PlayerProfile object to the database
     * @param player
     * The PlayerProfile object to be added
     */
    public void setPlayer(PlayerProfile player) {
        playersColRef.document(player.getUsername()).set(player);
    }

    /**
     * This method fetches a PlayerProfile object from the database and overwrites its data onto a
     * given PlayerProfile object
     * @param name
     * The username of the Player
     * @param player
     * The PlayerProfile object to be overwritten to
     */
    public void getPlayer(PlayerProfile player, String name) {
        /*
        Log.d("TESTPRINT", "Player: " + player.getUsername() + " "
                + player.getUniqueID() + " " + player.getEmail() + " "
                + player.getPhoneNum() + " " + player.getHighScore() + " "
                + player.getLowScore() + " " + player.getCaptured().size());
         */
        playersColRef.document(name).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    PlayerProfile fetchedPlayer = documentSnapshot.toObject(PlayerProfile.class);

                    if (fetchedPlayer == null) {
                        // the username is not in the database
                        player.fetchFailed();
                        return;
                    }

                    player.setUsername(fetchedPlayer.getUsername());
                    player.setUniqueID(fetchedPlayer.getUniqueID());
                    player.setEmail(fetchedPlayer.getEmail());
                    player.setPhoneNum(fetchedPlayer.getPhoneNum());
                    player.setHighScore(fetchedPlayer.getHighScore());
                    player.setLowScore(fetchedPlayer.getLowScore());

                    for (QRCode qrCode : fetchedPlayer.getCaptured()) {
                        System.out.println("in DBAccessor: " + qrCode.getName());
                        player.addQRCode(qrCode);
                    }

                    player.fetchComplete();

                    /*
                    // For testing purposes
                    Log.d("TESTPRINT", "Player: " + player.getUsername() + " "
                            + player.getUniqueID() + " " + player.getEmail() + " "
                            + player.getPhoneNum() + " " + player.getHighScore() + " "
                            + player.getLowScore() + " " + player.getCaptured().size());
//                     */
                    return;
                }
        });
    }
    
    /**
     * This method adds a QRCode to a PlayerProfile object's captured list and the QRs collection
     * with no comment
     * @param player
     * The username of the PlayerProfile object to be added to
     * @param qr
     * The QRCode object to be added
     */
    public void addQR(String player, QRCode qr) {
        addQR(player, qr, null);
    }

    /**
     * This method adds a QRCode to a PlayerProfile object's captured list and the QRs collection
     * @param player
     * The username of the PlayerProfile object to be added to
     * @param qr
     * The QRCode object to be added
     * @param comment
     * The comment to be added to be added
     */
    public void addQR(String player, QRCode qr, String comment) {
        playersColRef.document(player).update("captured", FieldValue.arrayUnion(qr));
        Map<String, Object> QRPlayerData = new HashMap<>();
        QRPlayerData.put("username", player);
        QRPlayerData.put("comment", comment);
        QRPlayerData.put("geoLocation", qr.getGeoLocation());
        QRPlayerData.put("locationImage", qr.getLocationImage());
        qrColRef.document(qr.getIdHash()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            // the QRCode is not in the database
                            Map<String, Object> data = new HashMap<>();
                            data.put("idHash", qr.getIdHash());
                            data.put("name", qr.getName());
                            data.put("score", qr.getScore());
                            qrColRef.document(qr.getIdHash()).set(data);
                            qrColRef.document(qr.getIdHash()).collection("qr_assoc_players").document(player).set(QRPlayerData);
                        } else {
                            // the QRCode is in the database
                            qrColRef.document(qr.getIdHash()).collection("qr_assoc_players").document(player).set(QRPlayerData);
                        }
                    }
                });

    }

    // TEST FUNCTION: Testing alternative storage method using QRData class
    public void addQR2(String player, QRCode qr) {
        playersColRef.document(player).update("captured", FieldValue.arrayUnion(qr));
        QRData qrData = new QRData(qr);
        qrColRef.document(qrData.getIdHash()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            // the QRCode is not in the database

                            qrColRef.document(qr.getIdHash()).collection("qr_assoc_players").document(player).set(qrData);
                        } else {
                            // the QRCode is in the database
                            qrColRef.document(qr.getIdHash()).collection("qr_assoc_players").document(player).set(qrData);
                        }
                    }
                });
        qrColRef.document(qrData.getIdHash()).set(qrData);
    }

    // getQR is a WIP. DO NOT USE YET
    /**
     * This method fetches a QRCode object from the database and overwrites its data onto a
     * given PlayerProfile object
     * @param name
     * The username of the Player
     * @param player
     * The PlayerProfile object to be overwritten to
     */
    public void getQR(PlayerProfile player, String name) {
    }



}
