package com.example.qriffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for GeoLocation class
 */
public class GeoLocationTest {

    private GeoLocation mockLocation() {

        return new GeoLocation(53.5461, 113.4937, "Edmonton");
    }

    @Test
    void testGetters() {

        GeoLocation mockGeoLocation = mockLocation();

        assertEquals(53.5461, mockGeoLocation.getLongitude());
        assertNotEquals(12.3456, mockGeoLocation.getLongitude());

        assertEquals(113.4937, mockGeoLocation.getLatitude());
        assertNotEquals(12.3456, mockGeoLocation.getLatitude());

        assertEquals("Edmonton", mockGeoLocation.getCity());
        assertNotEquals("Calgary", mockGeoLocation.getCity());
    }
}
