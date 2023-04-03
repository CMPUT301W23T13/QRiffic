package com.example.qriffic;

/**
 * Represents either a player or a QR in a way that can be used easily by the leaderboard
 */
public class LeaderboardEntry {
    /**
     * The username of the player, or the idHash of the QR being represented
     */
    private String id;
    /**
     * The total score/total scanned of the player, or the score of the QR being represented
     */
    private String value;
    /**
     * The username of the player, or the name of the QR being represented
     */
    private String name;

    /**
     * Creates a new LeaderboardEntry
     *
     * @param id    The username of the player, or the idHash of the QR
     * @param value The total score/total scanned of the player, or the score of the QR
     * @param name  The username of the player, or the name of the QR
     */
    public LeaderboardEntry(String id, String value, String name) {
        this.id = id;
        this.value = value;
        this.name = name;
    }

    /**
     * Sets the id of the entry to a new one
     *
     * @param id The new id to be set to, as a String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the id of the entry
     *
     * @return The id, as a String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the value of the entry to a new one
     *
     * @param value The new value to be set to, as a String
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the value of the entry
     *
     * @return The value, as a String
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Returns the name of the entry
     *
     * @return The name, as a String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the entry to a new one
     *
     * @param name The new name to be set to, as a String
     */
    public void setName(String name) {
        this.name = name;
    }
}
