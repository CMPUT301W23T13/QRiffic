package com.example.qriffic;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    public void updateContactInfo(PlayerProfile player) {
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

                    ArrayList<HashMap<String, Object>> tempMap = new ArrayList<HashMap<String, Object>>(((HashMap<String, Object>) fetchedPlayer.get("captured")).values());

                    for (int i = 0; i < tempMap.size(); i++) {
                        System.out.println("in DBAccessor: " + qrCode.getName());
                        QRCode qrCode = new QRCode();
                        qrCode.setName(tempMap.get(i).toString());
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
    public void addQR(String player, QRCode qr) {
        playersColRef.document(player).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // qr in QRs collection in db
                                PlayerProfile dbPlayer = new PlayerProfile();
                                Map<String, Object> fetchedPlayer = document.getData();
                                dbPlayer.setUsername(fetchedPlayer.get("username").toString());
                                if (fetchedPlayer.get("uniqueID") == null) {
                                    dbPlayer.setUniqueID(null);
                                } else {
                                    dbPlayer.setUniqueID(fetchedPlayer.get("uniqueID").toString());
                                }
                                if (fetchedPlayer.get("email") == null) {
                                    dbPlayer.setEmail(null);
                                } else {
                                    dbPlayer.setEmail(fetchedPlayer.get("email").toString());
                                }
                                if (fetchedPlayer.get("phoneNum") == null) {
                                    dbPlayer.setPhoneNum(null);
                                } else {
                                    dbPlayer.setPhoneNum(fetchedPlayer.get("phoneNum").toString());
                                }
                                dbPlayer.setHighScore(Math.toIntExact((long) fetchedPlayer.get("highScore")));
                                dbPlayer.setLowScore(Math.toIntExact((long) fetchedPlayer.get("lowScore")));
                                for (QRCode qrCode : ((HashMap<String, QRCode>) fetchedPlayer.get("captured")).values()) {
                                    dbPlayer.addQRCode(qrCode);
                                }
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
                            } else {
                                // qr not in QRs collection in db
                                throw new IllegalArgumentException("User does not exist in database: "+player);
                            }
                        } else {
                            Log.d("TESTPRINT", "Failed to get data");
                        }
                    }
                });

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

    //OLD METHOD. DO NOT USE. USE addQR() INSTEAD. Only retained here for reference
    /**
     * This method adds a QRCode to a PlayerProfile object's captured list and the QRs collection
     * @param player
     * The username of the PlayerProfile object to be added to
     * @param qr
     * The QRCode object to be added
     */
    public void addQROLD(String player, QRCode qr) {
        playersColRef.document(player).update("captured", FieldValue.arrayUnion(qr));
        Map<String, Object> QRPlayerData = new HashMap<>();
        QRPlayerData.put("username", player);
        QRPlayerData.put("comment", qr.getComment());
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

    /**
     * This method removes a QRCode from a PlayerProfile object's captured list
     * and removes the user from the that QRCode's users in the QRs collection
     * @param qr
     * The QRCode object to be removed
     */
    public void deleteQR(QRCode qr) {
        qrColRef.document(qr.getIdHash()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // qr in db
                                QRData dbQRData = document.toObject(QRData.class);
                                dbQRData.removeUser(qr.getUsername());
                                qrColRef.document(qr.getIdHash()).set(dbQRData);
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
        playersColRef.document(qr.getUsername()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   DocumentSnapshot document = task.getResult();
                                                   if (document.exists()) {
                                                       // qr in db
                                                       PlayerProfile dbPlayer = document.toObject(PlayerProfile.class);
                                                       dbPlayer.deleteQRCode(qr);
                                                       playersColRef.document(qr.getIdHash()).set(dbPlayer);
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
    }

    /**
     * This method removes a QRCode from a PlayerProfile object's captured list
     * and removes the user from the that QRCode's users in the QRs collection
     * @param idHash
     * The idHash of the QRCode to be removed
     * @param username
     * The username of the user to be removed from
     */
    public void deleteQR(String idHash, String username) {
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
        playersColRef.document(username).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   DocumentSnapshot document = task.getResult();
                                                   if (document.exists()) {
                                                       // qr in db
                                                       PlayerProfile dbPlayer = document.toObject(PlayerProfile.class);
                                                       dbPlayer.deleteQRCode(idHash);
                                                       playersColRef.document(idHash).set(dbPlayer);
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
    }
    
    /**
     * This method fetches a QRData object from the database and overwrites its data onto a
     * given QRData object
     * @param qrData
     * The QRData object to be overwritten to
     * @param idHash
     * The idHash of the QRData object to be fetched
     */
    public void getQRData(QRData qrData, String idHash) {
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
