package com.example.qriffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;

/**
 * Test suite for PlayerProfile class
 */
public class PlayerProfileTest {

    private QRCode mockQRCode() throws NoSuchAlgorithmException {
        return new QRCode("abcdef");
    }

    private PlayerProfile mockPlayerProfile() throws NoSuchAlgorithmException {

        return new PlayerProfile(new Player("username"),
                new ContactInfo("Canada", "Edmonton", "999.999.9999",
                        "username@outlook.com"));
    }

    @Test
    void testGetters() throws NoSuchAlgorithmException {

        PlayerProfile mockPlayerProfile = mockPlayerProfile();

        assertEquals("username", mockPlayerProfile.getPlayerId().getUserId());
        assertNotEquals("player name", mockPlayerProfile.getPlayerId().getUserId());

        assertEquals("Canada", mockPlayerProfile.getContactInfo().getCountry());
        assertEquals("Edmonton", mockPlayerProfile.getContactInfo().getCity());
        assertEquals("999.999.9999", mockPlayerProfile.getContactInfo().getPhone());
        assertEquals("username@outlook.com", mockPlayerProfile.getContactInfo().getEmail());
        assertNotEquals("USA", mockPlayerProfile.getContactInfo().getCountry());
        assertNotEquals("New York", mockPlayerProfile.getContactInfo().getCity());
        assertNotEquals("123.999.9999", mockPlayerProfile.getContactInfo().getPhone());
        assertNotEquals("player@outlook.com", mockPlayerProfile.getContactInfo().getEmail());

        // CHANGE THIS WHEN WE HAVE SCORE CALCULATOR
        assertEquals(0, mockPlayerProfile.getHighScore());
        assertNotEquals(20000,mockPlayerProfile.getHighScore());

        assertEquals(0, mockPlayerProfile.getLowScore());
        assertNotEquals(500,mockPlayerProfile.getLowScore());

        // tests on Captured and getCaptured are done in other test methods
        // i.e. testAddQRCode(), testDeleteQRCode etc.
    }

    @Test
    void testAddQRCode() throws NoSuchAlgorithmException {

        PlayerProfile mockPlayerProfile = mockPlayerProfile();
        QRCode mockQRCode = mockQRCode();

        assertEquals(0, mockPlayerProfile.getCaptured().size());
        assertNotEquals(1, mockPlayerProfile.getCaptured().size());

        mockPlayerProfile.addQRCode(mockQRCode);

        assertEquals(1, mockPlayerProfile.getCaptured().size());
        assertNotEquals(0, mockPlayerProfile.getCaptured().size());

        assertTrue(mockPlayerProfile.getCaptured().contains(mockQRCode));
        assertFalse(mockPlayerProfile.getCaptured().contains(new QRCode("123")));
    }



}
