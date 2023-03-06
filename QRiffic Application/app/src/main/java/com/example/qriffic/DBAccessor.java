package com.example.qriffic;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class DBAccessor {

    private FirebaseFirestore db;
    private CollectionReference playersColRef;
    private CollectionReference qrColRef;
    private CollectionReference mapColRef;
    private CollectionReference testColRef;

    public void DBAccessor() {
        db = FirebaseFirestore.getInstance();
        playersColRef = db.collection("Players");
        qrColRef = db.collection("QRs");
        mapColRef = db.collection("Map");
        testColRef = db.collection("TestCol");
    }

}
