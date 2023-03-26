package com.example.qriffic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.app.Instrumentation;

import com.google.firebase.FirebaseApp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class DBAccessorTest {

    private QRCode mockQRCode() {
        return new QRCode("testt", null, "testuser1", null, "test comment here");
    }

    private PlayerProfile mockPlayerProfile() {
        return new PlayerProfile("testname", "testuuid", "username@outlook.com", "999.999.9999", new HashMap<String, QRCode>());
    }

    @Test
    void testAddQR() {
        FirebaseApp.initializeApp(this);
        DBAccessor dba = new DBAccessor();
        dba.setPlayer(mockPlayerProfile());
        dba.addQR(mockPlayerProfile().getUsername(), mockQRCode());
    }

}


