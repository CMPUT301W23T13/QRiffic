package com.example.qriffic;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class DBAccessor {

    private FirebaseFirestore db;
    private CollectionReference playersColRef;
    private CollectionReference qrColRef;
    private CollectionReference mapColRef;
    private CollectionReference testColRef;

    public DBAccessor() {
        this.db = FirebaseFirestore.getInstance();
        this.playersColRef = db.collection("Players");
        this.qrColRef = db.collection("QRs");
        this.mapColRef = db.collection("Map");
        this.testColRef = db.collection("TestCol");
    }

    public void setPlayer(PlayerProfile player) {
        String name = player.getUsername();
        playersColRef.document(name).set(player);
    }

    //wip
    public PlayerProfile getPlayer(String name) {
        final PlayerProfile[] player = {new PlayerProfile()};
        playersColRef.document(name).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                player[0] = documentSnapshot.toObject(PlayerProfile.class);
            }
        });
        return player[0];
    }

    public void setQR(QRCode qr) {
        String name = qr.getName();
        qrColRef.document(name).set(qr);
    }




}
