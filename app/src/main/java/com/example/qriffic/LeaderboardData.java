package com.example.qriffic;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LeaderboardData {
    private ArrayList<LeaderboardEntry> topPlayerPoints;
    private ArrayList<LeaderboardEntry> topPlayerScans;
    private ArrayList<LeaderboardEntry> topQRPoints;
    private ArrayList<fetchListener> listeners;

    public LeaderboardData() {
        this.topPlayerPoints = new ArrayList<>();
        this.topPlayerScans = new ArrayList<>();
        this.topQRPoints = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void addListener(fetchListener listener) {
        listeners.add(listener);
    }

    public void fetchComplete() {
        for (fetchListener fl : listeners)
            fl.onFetchComplete();
    }

    public void fetchFailed() {
        for (fetchListener fl : listeners)
            fl.onFetchFailure();
    }

    public void addPlayerPoint(String username, String score) {
        topPlayerPoints.add(new LeaderboardEntry(username, score));
    }

    public void addPlayerScan(String username, String scans) {
        topPlayerScans.add(new LeaderboardEntry(username, scans));
    }

    public void addQRPoint(String idHash, String score) {
        topQRPoints.add(new LeaderboardEntry(idHash, score));
    }

    public ArrayList<LeaderboardEntry> getTopPlayerPoints() {
        return topPlayerPoints;
    }

    public ArrayList<LeaderboardEntry> getTopPlayerScans() {
        return topPlayerScans;
    }

    public ArrayList<LeaderboardEntry> getTopQRPoints() {
        return topQRPoints;
    }
}
