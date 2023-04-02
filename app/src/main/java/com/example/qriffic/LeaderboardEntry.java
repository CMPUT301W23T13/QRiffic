package com.example.qriffic;

public class LeaderboardEntry {
    private String id;
    private String value;
    private String name;


    public LeaderboardEntry(String id, String value, String name) {
        this.id = id;
        this.value = value;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
