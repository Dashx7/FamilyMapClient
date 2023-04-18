package com.example.familymapclient.cache;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Model.Event;
import Model.Person;
import Result.LoginResult;
import Result.RegisterResult;

public class DataCache {
    private static DataCache instance = new DataCache();

    public String serverPort;
    public String serverHost;
    public String authToken;

    public static DataCache getInstance() {
        return instance;
    }
    public static void resetCacheForTesting() {
        instance = new DataCache();
    }

    public DataCache(){

    }
    public List<Event> events; //Just straight up every event
    public Person theUserPerson;

    public List<Person> people;

    //Current thing clicked on
    public Person personClickedOn = null;
    public Event eventClickedOn = null;

    public void fillPeople(List<Person> toAdd){
        for(Person daPerson: toAdd){
            String key = daPerson.getPersonID();
            if(peopleMap.get(key)==null){
                peopleMap.put(key,daPerson);
            }
            else{
                throw new RuntimeException("Not supposed to happen");
            }
        }
    }
    public void fillEvents(List<Event> toAdd){
        eventMap.clear();
        eventMapPersonID.clear();
        for(Event daEvent: toAdd){
            String key = daEvent.getEventID();
            String key2 = daEvent.getPersonID();
            //The eventID to Event map
            if(eventMap.get(key)!=null){
                eventMap.get(key).add(daEvent);
            }
            else{
                List<Event> listEventToAdd = new ArrayList<>();
                listEventToAdd.add(daEvent);
                eventMap.put(key, listEventToAdd);
            }
            //The personID to Event map
            if(eventMapPersonID.get(key2)!=null){
                eventMapPersonID.get(key2).add(daEvent);
            }
            else{
                List<Event> listEventToAdd = new ArrayList<>();
                listEventToAdd.add(daEvent);
                eventMapPersonID.put(key2, listEventToAdd);
            }
        }
    }
    public Map<String,Person> peopleMap = new HashMap<>(); //Person ID as a string, person
    public Map<String, List<Event>> eventMap = new HashMap<>(); //EventID, Event
    //public Map<String, List<Event>> eventMapAssociatedUsername = new HashMap<>(); // Associated Username, Event
    public Map<String, List<Event>> eventMapPersonID = new HashMap<>(); // PersonID, Event list

    public RegisterResult registerResult;
    public LoginResult loginResult;

    public List<Polyline> currentLines = new ArrayList<>();
    public List<Marker> currentMarker = new ArrayList<>();

    public List<String> mothersSideID = new ArrayList<>();
    public List<String> fathersSideID = new ArrayList<>();
    public List<String> basePeopleID = new ArrayList<>(); //user, spouse, and their kids
    public boolean calledFromEventActivity = false; //To see if I need to inflate the menu

}
