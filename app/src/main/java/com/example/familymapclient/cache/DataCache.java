package com.example.familymapclient.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;

public class DataCache {

    private static DataCache instance = new DataCache();
    private static Settings settings = new Settings();

    public static DataCache getInstance() {
        return instance;
    }
    public static Settings getSettings() {
        return settings;
    }

    private DataCache(){

    }
    List<Event> events;
    List<Person> people;

    Map<Integer,Person> peopleMap; //Person ID, person
    Map<Integer, Event> eventMap; //EventID, Event
    Map<Integer, List<Event>> personEvents;

    Set<Integer> paternalAncestors;
    Set<Integer> maternalAncestors;

}
