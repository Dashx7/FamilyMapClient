package com.example.familymapclient.model;

public class LifeEvent {
    //BirthInfo
    private final String birthInfo;
    //PersonName
    private final String personName;

    public LifeEvent(String name, String location) {
        this.birthInfo = name;
        this.personName = location;
    }

    public String getBirthInfo() {
        return birthInfo;
    }

    public String getPersonName() {
        return personName;
    }
}
