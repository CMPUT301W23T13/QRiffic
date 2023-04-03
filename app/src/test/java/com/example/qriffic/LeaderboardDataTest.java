package com.example.qriffic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LeaderboardDataTest {



    @Test
    public void testAdders() {
        LeaderboardData mockLeaderboardData = new LeaderboardData();

        int beforeSize = mockLeaderboardData.getTopPlayerPoints().size();
        mockLeaderboardData.addPlayerPoint("username", "score");
        assertEquals(beforeSize+1, mockLeaderboardData.getTopPlayerPoints().size());

        beforeSize = mockLeaderboardData.getTopPlayerScans().size();
        mockLeaderboardData.addPlayerScan("username", "scans");
        assertEquals(beforeSize+1, mockLeaderboardData.getTopPlayerScans().size());

        beforeSize = mockLeaderboardData.getTopQRPoints().size();
        mockLeaderboardData.addQRPoint("1234", "score", "name");
        assertEquals(beforeSize+1, mockLeaderboardData.getTopQRPoints().size());

        beforeSize = mockLeaderboardData.getTopRegionQRPoints().size();
        mockLeaderboardData.addRegionQRPoint("1234", "score", "name");
        assertEquals(beforeSize+1, mockLeaderboardData.getTopRegionQRPoints().size());
    }

    @Test
    public void testFetchComplete() {
        LeaderboardData mockLeaderboardData = new LeaderboardData();
        mockLeaderboardData.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                assertEquals(1, 1);
            }

            @Override
            public void onFetchFailure() {
                assertEquals(1, 0);
            }
        });
        mockLeaderboardData.fetchComplete();
    }

    @Test
    public void testFetchFailed() {
        LeaderboardData mockLeaderboardData = new LeaderboardData();
        mockLeaderboardData.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                assertEquals(1, 0);
            }

            @Override
            public void onFetchFailure() {
                assertEquals(1, 1);
            }
        });
        mockLeaderboardData.fetchFailed();
    }

    @Test
    public void testGetters() {
        LeaderboardData mockLeaderboardData = new LeaderboardData();
        mockLeaderboardData.addPlayerPoint("username", "score");
        mockLeaderboardData.addPlayerScan("username", "scans");
        mockLeaderboardData.addQRPoint("1234", "score", "name");
        mockLeaderboardData.addRegionQRPoint("1234", "score", "name");

        assertEquals(mockLeaderboardData.getTopPlayerPoints().get(0).getId(), "username");
        assertEquals(mockLeaderboardData.getTopPlayerPoints().get(0).getValue(), "score");

        assertEquals(mockLeaderboardData.getTopPlayerScans().get(0).getId(), "username");
        assertEquals(mockLeaderboardData.getTopPlayerScans().get(0).getValue(), "scans");

        assertEquals(mockLeaderboardData.getTopQRPoints().get(0).getId(), "1234");
        assertEquals(mockLeaderboardData.getTopQRPoints().get(0).getValue(), "score");

        assertEquals(mockLeaderboardData.getTopRegionQRPoints().get(0).getId(), "1234");
        assertEquals(mockLeaderboardData.getTopRegionQRPoints().get(0).getValue(), "score");
    }

}
