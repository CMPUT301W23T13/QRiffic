package com.example.qriffic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class LeaderboardEntryTest {

    @Test
    void testGetters() {
        LeaderboardEntry mockLeaderboardEntry = new LeaderboardEntry("idString", "valueString");

        assertEquals(mockLeaderboardEntry.getId(), "idString");
        assertNotEquals(mockLeaderboardEntry.getId(), "idString2");

        assertEquals(mockLeaderboardEntry.getValue(), "valueString");
        assertNotEquals(mockLeaderboardEntry.getValue(), "valueString2");
    }

    @Test
    void testSetters() {
        LeaderboardEntry mockLeaderboardEntry = new LeaderboardEntry("idString1", "valueString1");

        mockLeaderboardEntry.setId("idString2");
        assertEquals(mockLeaderboardEntry.getId(), "idString2");
        assertNotEquals(mockLeaderboardEntry.getId(), "idString1");

        mockLeaderboardEntry.setValue("valueString2");
        assertEquals(mockLeaderboardEntry.getValue(), "valueString2");
        assertNotEquals(mockLeaderboardEntry.getValue(), "valueString1");
    }
}
