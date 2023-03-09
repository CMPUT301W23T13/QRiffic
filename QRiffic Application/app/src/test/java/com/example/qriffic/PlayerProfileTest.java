package com.example.qriffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;

/**
 * Test suite for PlayerProfile class
 */
public class PlayerProfileTest {

    private QRCode mockQRCode() {
        return new QRCode("abcdef", null, null);
    }

    private PlayerProfile mockPlayerProfile() {

        return new PlayerProfile("username", "uuid", "username@outlook.com", "999.999.9999", 0, 0, new ArrayList<QRCode>());
    }

    @Test
    void testGetters() {

        PlayerProfile mockPlayerProfile = mockPlayerProfile();

        assertEquals("username", mockPlayerProfile.getUsername());
        assertNotEquals("player name", mockPlayerProfile.getUsername());

        assertEquals("uuid", mockPlayerProfile.getUniqueID());
        assertNotEquals("uuid2", mockPlayerProfile.getUniqueID());

        assertEquals("999.999.9999", mockPlayerProfile.getPhoneNum());
        assertEquals("username@outlook.com", mockPlayerProfile.getEmail());
        assertNotEquals("123.999.9999", mockPlayerProfile.getPhoneNum());
        assertNotEquals("player@outlook.com", mockPlayerProfile.getEmail());

        // CHANGE THIS WHEN WE HAVE SCORE CALCULATOR
        assertEquals(0, mockPlayerProfile.getHighScore());
        assertNotEquals(20000,mockPlayerProfile.getHighScore());

        assertEquals(0, mockPlayerProfile.getLowScore());
        assertNotEquals(500,mockPlayerProfile.getLowScore());

        // tests on Captured and getCaptured are done in other test methods
        // i.e. testAddQRCode(), testDeleteQRCode etc.
    }

    @Test
    void testAddQRCode() {

        PlayerProfile mockPlayerProfile = mockPlayerProfile();
        QRCode mockQRCode = mockQRCode();

        assertEquals(0, mockPlayerProfile.getCaptured().size());
        assertNotEquals(1, mockPlayerProfile.getCaptured().size());

        mockPlayerProfile.addQRCode(mockQRCode);

        assertEquals(1, mockPlayerProfile.getCaptured().size());
        assertNotEquals(0, mockPlayerProfile.getCaptured().size());

        assertTrue(mockPlayerProfile.getCaptured().contains(mockQRCode));
        assertFalse(mockPlayerProfile.getCaptured().contains(new QRCode("123", null, null)));
    }

    @Test
    void testDeleteQRCode() {

        PlayerProfile mockPlayerProfile = mockPlayerProfile();
        QRCode mockQRCode = mockQRCode();
        QRCode mockQRCode2 = new QRCode("123", null, null);

        mockPlayerProfile.addQRCode(mockQRCode);

        assertThrows(IllegalArgumentException.class, () -> {
            mockPlayerProfile.deleteQRCode(mockQRCode2);
        });

        assertEquals(1, mockPlayerProfile.getCaptured().size());
        assertNotEquals(0, mockPlayerProfile.getCaptured().size());

        assertTrue(mockPlayerProfile.getCaptured().contains(mockQRCode));
        assertFalse(mockPlayerProfile.getCaptured().contains(mockQRCode2));

        mockPlayerProfile.addQRCode(mockQRCode2);

        assertEquals(2, mockPlayerProfile.getCaptured().size());
        assertNotEquals(1, mockPlayerProfile.getCaptured().size());

        assertTrue(mockPlayerProfile.getCaptured().contains(mockQRCode2));

        mockPlayerProfile.deleteQRCode(mockQRCode);

        assertEquals(1, mockPlayerProfile.getCaptured().size());
        assertNotEquals(2, mockPlayerProfile.getCaptured().size());

        assertTrue(mockPlayerProfile.getCaptured().contains(mockQRCode2));
        assertFalse(mockPlayerProfile.getCaptured().contains(mockQRCode));
    }



}
