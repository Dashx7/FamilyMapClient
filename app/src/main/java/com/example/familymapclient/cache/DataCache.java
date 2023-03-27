package com.example.familymapclient.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;

public class DataCache {

    //public String SERVERPORT = "";

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
    public List<Event> events;
    public List<Person> people;

    public Map<Integer,Person> peopleMap; //Person ID, person
    public Map<Integer, Event> eventMap; //EventID, Event
    public Map<Integer, List<Event>> personEvents;

    public Set<Integer> paternalAncestors;
    public Set<Integer> maternalAncestors;

}
