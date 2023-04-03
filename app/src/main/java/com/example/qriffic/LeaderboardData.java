package com.example.qriffic;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class holds all of the data that the leaderboard needs
 */
public class LeaderboardData {
    /**
     * An array of LeaderboardEntry objects that represent players, ordered descending by their total score
     */
    private ArrayList<LeaderboardEntry> topPlayerPoints;
    /**
     * An array of LeaderboardEntry objects that represent players, ordered descending by their total scans
     */
    private ArrayList<LeaderboardEntry> topPlayerScans;
    /**
     * An array of LeaderboardEntry objects that represent QRCodes, ordered descending by their individual score
     */
    private ArrayList<LeaderboardEntry> topQRPoints;
    /**
     * An array of LeaderboardEntry objects that represent QRCodes that belong in a certain region, ordered descending by their individual score
     */
    private ArrayList<LeaderboardEntry> topRegionQRPoints;
    /**
     * Holds fetchListeners used by the class to know when data has been successfully fetched from the database
     */
    private ArrayList<fetchListener> listeners;

    /**
     * Constructs the class and initializes all arrays as empty ones
     */
    public LeaderboardData() {
        this.topPlayerPoints = new ArrayList<>();
        this.topPlayerScans = new ArrayList<>();
        this.topQRPoints = new ArrayList<>();
        this.topRegionQRPoints = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    /**
     * This method adds a fetchListener to the PlayerProfile object
     *
     * This block references the following web page:
     * Link: https://programming.guide/java/create-a-custom-event.html
     * Author: Unavailable
     * Date: 10/03/2023
     *
     * @param listener
     * The fetchListener to be added
     */
    public void addListener(fetchListener listener) {
        listeners.add(listener);
    }

    /**
     * This method calls all onFetchComplete() listeners
     *
     * This block references the following web page:
     * Link: https://programming.guide/java/create-a-custom-event.html
     * Author: Unavailable
     * Date: 10/03/2023
     *
     */
    public void fetchComplete() {
        for (fetchListener fl : listeners)
            fl.onFetchComplete();
    }

    /**
     * This method calls all onFetchFailure() listeners
     *
     * This block references the following web page:
     * Link: https://programming.guide/java/create-a-custom-event.html
     * Author: Unavailable
     * Date: 10/03/2023
     */
    public void fetchFailed() {
        for (fetchListener fl : listeners)
            fl.onFetchFailure();
    }

    /**
     * Adds a LeaderboardEntry representing a player to the topPlayerPoints array
     * @param username The username of the player
     * @param score The total score of the player
     */
    public void addPlayerPoint(String username, String score) {
        topPlayerPoints.add(new LeaderboardEntry(username, score, username));
    }

    /**
     * Adds a LeaderboardEntry representing a player to the topPlayerScans array
     * @param username The username of the player
     * @param scans The total scans of the player
     */
    public void addPlayerScan(String username, String scans) {
        topPlayerScans.add(new LeaderboardEntry(username, scans, username));
    }

    /**
     * Adds a LeaderboardEntry representing a QRMon to the topQRPoints array
     * @param idHash The idHash of the QRMon
     * @param score The score of the QRMon
     * @param name The name of the QRMon
     */
    public void addQRPoint(String idHash, String score, String name) {
        topQRPoints.add(new LeaderboardEntry(idHash, score, name));
    }

    /**
     * Adds a LeaderboardEntry representing a QRMon to the topRegionQRPoints array
     * @param idHash The idHash of the QRMon
     * @param score The score of the QRMon
     * @param name The name of the QRMon
     */
    public void addRegionQRPoint(String idHash, String score, String name) {
        topRegionQRPoints.add(new LeaderboardEntry(idHash, score, name));
    }

    /**
     * Returns the topPlayerPoints array, which has all players in it ordered descending by their total score
     * @return The topPlayerPoints array
     */
    public ArrayList<LeaderboardEntry> getTopPlayerPoints() {
        return topPlayerPoints;
    }

    /**
     * Returns the topPlayerScans array, which has all players in it ordered descending by their total scans
     * @return The topPlayerScans array
     */
    public ArrayList<LeaderboardEntry> getTopPlayerScans() {
        return topPlayerScans;
    }

    /**
     * Returns the topQRPoints array, which has all QRMons in it ordered descending by their scores
     * @return The topQRPoints array
     */
    public ArrayList<LeaderboardEntry> getTopQRPoints() {
        return topQRPoints;
    }

    /**
     * Returns the topRegionQRPoints array, which has QRMons from a certain region ordered descending by their scores
     * @return The topRegionQRPoints array
     */
    public ArrayList<LeaderboardEntry> getTopRegionQRPoints() {
        return topRegionQRPoints;
    }
}
