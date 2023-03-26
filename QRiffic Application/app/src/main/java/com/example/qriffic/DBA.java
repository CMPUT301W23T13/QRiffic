package com.example.qriffic;


import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used as controller to access the database
 */
public class DBA {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference playersColRef= db.collection("Players");;
    private static CollectionReference qrColRef = db.collection("QRs");
    private static CollectionReference mapColRef = db.collection("Map");
    private static CollectionReference testColRef = db.collection("TestCol"); // Temporary test collection

    /**
     * This is the constructor for the DBA class
     */
    public DBA() {
    }

    /**
     * This method adds/sets a PlayerProfile object to the database
     * @param player
     * The PlayerProfile object to be added
     */
    public static void setPlayer(PlayerProfile player) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", player.getUsername());
        data.put("uniqueID", player.getUniqueID());
        data.put("email", player.getEmail());
        data.put("phoneNum", player.getPhoneNum());
        data.put("highScore", player.getHighScore());
        data.put("lowScore", player.getLowScore());
        data.put("captured", player.getCaptured());
        playersColRef.document(player.getUsername()).set(data);
    }

    /**
     * This method updates the contact info of an existing Player in the database.
     * Throws an error if the player does not exist in the database.
     * @param player
     * The PlayerProfile object with the updated info
     */
    public static void updateContactInfo(PlayerProfile player) {
        playersColRef.document(player.getUsername()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        playersColRef.document(player.getUsername()).update("phoneNum", player.getPhoneNum());
                                        playersColRef.document(player.getUsername()).update("email", player.getEmail());
                                    } else {
                                        throw new IllegalArgumentException("Player does not exist in database");
                                    }
                                }
                            }
                        });
    }

    /**
     * This method fetches a PlayerProfile object from the database and overwrites its data onto a
     * given PlayerProfile object
     * @param name
     * The username of the Player
     * @param player
     * The PlayerProfile object to be overwritten to
     */
    public static void getPlayer(PlayerProfile player, String name) {
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
                    Map<String, Object> fetchedPlayer = documentSnapshot.getData();
                    if (fetchedPlayer == null) {
                        // the username is not in the database
                        player.fetchFailed();
                        return;
                    }

                    player.setUsername(fetchedPlayer.get("username").toString());
                    if (fetchedPlayer.get("uniqueID") == null) {
                        player.setUniqueID(null);
                    } else {
                        player.setUniqueID(fetchedPlayer.get("uniqueID").toString());
                    }
                    if (fetchedPlayer.get("email") == null) {
                        player.setEmail(null);
                    } else {
                        player.setEmail(fetchedPlayer.get("email").toString());
                    }
                    if (fetchedPlayer.get("phoneNum") == null) {
                        player.setPhoneNum(null);
                    } else {
                        player.setPhoneNum(fetchedPlayer.get("phoneNum").toString());
                    }
                    player.setHighScore(Math.toIntExact((long) fetchedPlayer.get("highScore")));
                    player.setLowScore(Math.toIntExact((long) fetchedPlayer.get("lowScore")));

                    Collection<Object> tempCollection = ((HashMap<String, Object>) fetchedPlayer.get("captured")).values();
                    ArrayList<Object> tempMap = new ArrayList<Object>(tempCollection);
                    ObjectMapper mapper = new ObjectMapper();

                    for (int i = 0; i < tempMap.size(); i++) {
                        QRCode qrCode = mapper.convertValue(tempMap.get(i), QRCode.class);
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
     * @param player
     * The username of the PlayerProfile object to be added to
     * @param qr
     * The QRCode object to be added
     */
    public static void addQR(String player, QRCode qr) {
        PlayerProfile dbPlayer = new PlayerProfile();
        dbPlayer.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                dbPlayer.addQRCode(qr);
                HashMap<String, Object> data = new HashMap<>();
                data.put("username", dbPlayer.getUsername());
                data.put("uniqueID", dbPlayer.getUniqueID());
                data.put("email", dbPlayer.getEmail());
                data.put("phoneNum", dbPlayer.getPhoneNum());
                data.put("highScore", dbPlayer.getHighScore());
                data.put("lowScore", dbPlayer.getLowScore());
                data.put("captured", dbPlayer.getCaptured());
                playersColRef.document(dbPlayer.getUsername()).set(data);
            }
            @Override
            public void onFetchFailure() {
                throw new IllegalArgumentException("Player does not exist in database");
            }
        });
        getPlayer(dbPlayer, player);
        QRData qrData = new QRData(qr);
        qrColRef.document(qrData.getIdHash()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // qr in QRs collection in db
                                QRData dbQRData = document.toObject(QRData.class);
                                dbQRData.addUser(qr);
                                qrColRef.document(qr.getIdHash()).set(dbQRData);
                            } else {
                                // qr not in QRs collection in db
                                qrColRef.document(qr.getIdHash()).set(qrData);
                            }
                        } else {
                            Log.d("TESTPRINT", "Failed to get QRData");
                        }
                    }
                });
    }

    /**
     * This method removes a QRCode from a PlayerProfile object's captured list
     * and removes the user from the that QRCode's users in the QRs collection
     * @param qr
     * The QRCode object to be removed
     */
    public static void deleteQR(QRCode qr) {
        deleteQR(qr.getIdHash(), qr.getUsername());
    }

    /**
     * This method removes a QRCode from a PlayerProfile object's captured list
     * and removes the user from the that QRCode's users in the QRs collection
     * @param idHash
     * The idHash of the QRCode to be removed
     * @param username
     * The username of the user to be removed from
     */
    public static void deleteQR(String idHash, String username) {
        qrColRef.document(idHash).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   DocumentSnapshot document = task.getResult();
                                                   if (document.exists()) {
                                                       // qr in db
                                                       QRData dbQRData = document.toObject(QRData.class);
                                                       dbQRData.removeUser(username);
                                                       qrColRef.document(idHash).set(dbQRData);
                                                   } else {
                                                       // qr not in db
                                                       throw new IllegalArgumentException("QRCode does not exist in database");
                                                   }
                                               } else {
                                                   throw new IllegalArgumentException("Failed to get QRData");
                                               }
                                           }
                                       }
                );
        PlayerProfile dbPlayer = new PlayerProfile();
        dbPlayer.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                dbPlayer.deleteQRCode(idHash);
                DBA.setPlayer(dbPlayer);
            }
            @Override
            public void onFetchFailure() {
                throw new IllegalArgumentException("Player does not exist in database");
            }
        });
        getPlayer(dbPlayer, username);
    }
    
    /**
     * This method fetches a QRData object from the database and overwrites its data onto a
     * given QRData object
     * @param qrData
     * The QRData object to be overwritten to
     * @param idHash
     * The idHash of the QRData object to be fetched
     */
    public static void getQRData(QRData qrData, String idHash) {
        qrColRef.document(idHash).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // qr in db
                                QRData dbQRData = document.toObject(QRData.class);
                                qrData.setIdHash(dbQRData.getIdHash());
                                qrData.setName(dbQRData.getName());
                                qrData.setScore(dbQRData.getScore());
                                qrData.setUsers(dbQRData.getUsers());
                                qrData.fetchComplete();
                            } else {
                                // qr not in db
                                qrData.fetchFailed();
                            }
                        } else {
                            Log.d("TESTPRINT", "Failed to get QRData");
                        }
                    }
                });
    }



}
