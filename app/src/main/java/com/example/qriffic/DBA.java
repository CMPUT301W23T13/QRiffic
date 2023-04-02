package com.example.qriffic;


import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        data.put("totalScore", player.getTotalScore());
        data.put("totalScanned", player.getTotalScanned());
        data.put("captured", player.getCaptured());
        playersColRef.document(player.getUsername()).set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TESTPRINT", "Player added to database");
                            player.fetchComplete();
                        } else {
                            Log.d("TESTPRINT", "Player not added to database");
                        }
                    }
                });
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
    public static void addQR(PlayerProfile player, QRCode qr) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("username", player.getUsername());
        data.put("uniqueID", player.getUniqueID());
        data.put("email", player.getEmail());
        data.put("phoneNum", player.getPhoneNum());
        data.put("highScore", player.getHighScore());
        data.put("lowScore", player.getLowScore());
        data.put("totalScore", player.getTotalScore());
        data.put("totalScanned", player.getTotalScanned());
        data.put("captured", player.getCaptured());
        Task updatePlayer = playersColRef.document(player.getUsername()).set(data);

        QRData qrData = new QRData(qr);
        Task updateQRCode = qrColRef.document(qrData.getIdHash()).get()
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

        Tasks.whenAllComplete(updatePlayer,updateQRCode).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
            @Override
            public void onSuccess(List<Task<?>> tasks) {
                qr.fetchComplete();
            }
        });
    }

    /**
     * This method removes a QRCode from a PlayerProfile object's captured list
     * and removes the user from the that QRCode's users in the QRs collection
     * The input QRCode object is only used to get the username and idHash and attach the listener
     * @param qr
     * The QRCode object to be removed (Only the listeners, idHash, and username fields are used)
     */
    public static void deleteQR(QRCode qr) {
        qrColRef.document(qr.getIdHash()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   DocumentSnapshot document = task.getResult();
                                                   if (document.exists()) {
                                                       // qr in db
                                                       QRData dbQRData = document.toObject(QRData.class);
                                                       boolean removeFlag = dbQRData.removeUser(qr.getUsername());
                                                       if (removeFlag) {
                                                           qrColRef.document(qr.getIdHash()).delete()
                                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                       @Override
                                                                       public void onSuccess(Void aVoid) {
                                                                           Log.d("deleteQR", "QR successfully deleted!");
                                                                       }
                                                                   })
                                                                   .addOnFailureListener(new OnFailureListener() {
                                                                       @Override
                                                                       public void onFailure(@NonNull Exception e) {
                                                                           Log.w("deleteQR", "Error deleting document", e);
                                                                       }
                                                                   });
                                                       } else {
                                                           qrColRef.document(qr.getIdHash()).set(dbQRData);
                                                       }
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
                dbPlayer.removeListener(this);
                dbPlayer.deleteQRCode(qr.getIdHash());
                // TODO: You will need to update the lowScore/highScore/totalScanned/totalScore fields in the database but I don't want to break anything here
                dbPlayer.addListener(new fetchListener() {
                    @Override
                    public void onFetchComplete() {
                        qr.fetchComplete();
                    }
                    @Override
                    public void onFetchFailure() {
                        throw new IllegalArgumentException("Player does not exist in database");
                    }
                });
                DBA.setPlayer(dbPlayer);
            }
            @Override
            public void onFetchFailure() {
                throw new IllegalArgumentException("Player does not exist in database");
            }
        });
        getPlayer(dbPlayer, qr.getUsername());
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

    public static void getLeaderboard(LeaderboardData data, String city) {
        Task playerPointQuery = playersColRef.whereGreaterThan("totalScore", 0).orderBy("totalScore", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String username = document.get("username").toString();
                                String score = document.get("totalScore").toString();
                                data.addPlayerPoint(username, score);
                            }
                        } else {
                            Log.d("topPlayerPoints", "Error getting documents");
                            data.fetchFailed();
                        }
                    }
                });

        Task playerScanQuery = playersColRef.whereGreaterThan("totalScanned", 0).orderBy("totalScanned", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String username = document.get("username").toString();
                                String scanned = document.get("totalScanned").toString();
                                data.addPlayerScan(username, scanned);
                            }
                        } else {
                            Log.d("topPlayerScans", "Error getting documents");
                            data.fetchFailed();
                        }
                    }
                });

        Task qrPointQuery = qrColRef.whereGreaterThan("score", 0).orderBy("score", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idHash = document.get("idHash").toString();
                                String score = document.get("score").toString();
                                String name = document.get("name").toString();
                                data.addQRPoint(idHash, score, name);
                            }

                        } else {
                            Log.d("topQRPoints", "Error getting documents");
                            data.fetchFailed();
                        }
                    }
                });

        if (city != null) {
            Log.d("city", city);
            Task qrRegionQuery = qrColRef.whereGreaterThan("score", 0).orderBy("score", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    boolean regionFlag = false;

                                    Object users = document.get("users");
                                    if (Objects.nonNull(users)) {
                                        Map userMap = (Map) users;

                                        for (Object value : userMap.values()) {
                                            if (Objects.nonNull(value)) {
                                                Map valueMap = (Map) value;
                                                Object geoInfo = valueMap.get("geoLocation");
                                                if (Objects.nonNull(geoInfo)) {
                                                    Map geoMap = (Map) geoInfo;
                                                    Object qrCity = geoMap.get("city");
                                                    if (Objects.nonNull(qrCity)) {
                                                        String qrCityString = (String) qrCity;
                                                        if (qrCityString.equals(city)) {
                                                            regionFlag = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (regionFlag) {
                                            String idHash = document.get("idHash").toString();
                                            String score = document.get("score").toString();
                                            String name = document.get("name").toString();
                                            data.addRegionQRPoint(idHash, score, name);
                                        }
                                    }
                                }

                            } else {
                                Log.d("topRegionQRPoints", "Error getting documents");
                                data.fetchFailed();
                            }
                        }
                    });

            Tasks.whenAllComplete(playerPointQuery, playerScanQuery, qrPointQuery, qrRegionQuery).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                @Override
                public void onSuccess(List<Task<?>> tasks) {
                    data.fetchComplete();
                }
            });
        } else {
            Tasks.whenAllComplete(playerPointQuery, playerScanQuery, qrPointQuery).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                @Override
                public void onSuccess(List<Task<?>> tasks) {
                    data.fetchComplete();
                }
            });
        }
    }
}
