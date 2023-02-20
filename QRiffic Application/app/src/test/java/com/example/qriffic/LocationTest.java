package com.example.qriffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Location class
 */
public class LocationTest {

    private Location mockLocation() {

        return new Location("53.5461", "113.4937");
    }

    @Test
    void testGetters() {

        Location mockLocation = mockLocation();

        assertEquals("53.5461", mockLocation.getLongitude());
        assertNotEquals("12.3456", mockLocation.getLongitude());

        assertEquals("113.4937", mockLocation.getLatitude());
        assertNotEquals("12.3456", mockLocation.getLatitude());

        assertEquals("53.5461, 113.4937", mockLocation.getLongLat());
        assertNotEquals("12.3456, 12.3456", mockLocation.getLongLat());

    }
}
