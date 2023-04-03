package com.example.qriffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test suite for PlayerProfile class
 */
public class PlayerProfileTest {

    private QRCode mockQRCode() {
        return new QRCode("abcdef", null, null, null, null);
    }

    private PlayerProfile mockPlayerProfile() {

        return new PlayerProfile("username", "username@outlook.com", "999.999.9999",  new HashMap<String, QRCode>());
    }

    @Test
    void testGetters() {

        PlayerProfile mockPlayerProfile = mockPlayerProfile();

        assertEquals("username", mockPlayerProfile.getUsername());
        assertNotEquals("player name", mockPlayerProfile.getUsername());

        assertEquals("999.999.9999", mockPlayerProfile.getPhoneNum());
        assertEquals("username@outlook.com", mockPlayerProfile.getEmail());
        assertNotEquals("123.999.9999", mockPlayerProfile.getPhoneNum());
        assertNotEquals("player@outlook.com", mockPlayerProfile.getEmail());

        assertEquals(-1, mockPlayerProfile.getHighScore());
        assertNotEquals(20000,mockPlayerProfile.getHighScore());

        assertEquals(-1, mockPlayerProfile.getLowScore());
        assertNotEquals(500,mockPlayerProfile.getLowScore());

        // tests on Captured and getCaptured are done in other test methods
        // i.e. testAddQRCode(), testDeleteQRCode etc.
    }

    @Test
    void testSetters() {
        PlayerProfile mockPlayerProfile = mockPlayerProfile();

        mockPlayerProfile.setUsername("testUsername");
        assertEquals(mockPlayerProfile.getUsername(), "testUsername");

        mockPlayerProfile.setEmail("testEmail");
        assertEquals(mockPlayerProfile.getEmail(), "testEmail");

        mockPlayerProfile.setPhoneNum("testPhoneNum");
        assertEquals(mockPlayerProfile.getPhoneNum(), "testPhoneNum");

        mockPlayerProfile.setHighScore(100);
        assertEquals(mockPlayerProfile.getHighScore(), 100);

        mockPlayerProfile.setLowScore(100);
        assertEquals(mockPlayerProfile.getLowScore(), 100);
    }

    @Test
    void testAddQRCode() {

        PlayerProfile mockPlayerProfile = mockPlayerProfile();
        QRCode mockQRCode = mockQRCode();

        assertEquals(0, mockPlayerProfile.getCaptured().values().size());
        assertNotEquals(1, mockPlayerProfile.getCaptured().values().size());

        mockPlayerProfile.addQRCode(mockQRCode);

        assertEquals(1, mockPlayerProfile.getCaptured().values().size());
        assertNotEquals(0, mockPlayerProfile.getCaptured().values().size());
        assertEquals(mockQRCode.getScore(), mockPlayerProfile.getLowScore());
        assertEquals(mockQRCode.getScore(), mockPlayerProfile.getHighScore());

        assertTrue(mockPlayerProfile.getCaptured().values().contains(mockQRCode));
        assertFalse(mockPlayerProfile.getCaptured().values().contains(new QRCode("123", null, null, null, null)));
    }

    @Test
    void testDeleteQRCode() {

        PlayerProfile mockPlayerProfile = mockPlayerProfile();
        QRCode mockQRCode = mockQRCode();
        QRCode mockQRCode2 = new QRCode("123", null, null, null, null);

        mockPlayerProfile.addQRCode(mockQRCode);

        assertThrows(Exception.class, () -> {
            mockPlayerProfile.deleteQRCode(mockQRCode2);
        });

        assertEquals(1, mockPlayerProfile.getCaptured().values().size());
        assertNotEquals(0, mockPlayerProfile.getCaptured().values().size());

        assertTrue(mockPlayerProfile.getCaptured().values().contains(mockQRCode));
        assertFalse(mockPlayerProfile.getCaptured().values().contains(mockQRCode2));

        mockPlayerProfile.addQRCode(mockQRCode2);

        assertEquals(2, mockPlayerProfile.getCaptured().values().size());
        assertNotEquals(1, mockPlayerProfile.getCaptured().values().size());

        assertTrue(mockPlayerProfile.getCaptured().values().contains(mockQRCode2));

        mockPlayerProfile.deleteQRCode(mockQRCode);

        assertEquals(1, mockPlayerProfile.getCaptured().values().size());
        assertNotEquals(2, mockPlayerProfile.getCaptured().values().size());

        assertTrue(mockPlayerProfile.getCaptured().values().contains(mockQRCode2));
        assertFalse(mockPlayerProfile.getCaptured().values().contains(mockQRCode));
    }



}
