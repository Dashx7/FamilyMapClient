package com.example.familymapclient.model;

public class FamilyPerson {
    private final String name;
    private final String location;
    private final String difficulty;

    public FamilyPerson(String name, String location, String difficulty) {
        this.name = name;
        this.location = location;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
