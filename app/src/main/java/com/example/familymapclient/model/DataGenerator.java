package com.example.familymapclient.model;

import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.cache.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Model.Event;
import Model.Person;

public class DataGenerator {

    String searchText; //What has been

    public DataGenerator(String searchText) {
        this.searchText = searchText;
    }

    public List<LifeEvent> getLifeEvents() {
        List<LifeEvent> lifeEvents = new ArrayList<>();

        for (Event daEvent : DataCache.getInstance().events) {
            LifeEvent lifeEvent = eventSearch(daEvent, searchText);
            if (lifeEvent != null) {
                Boolean shouldAdd = true;
                if(!Settings.isFilterByMomsSide && shouldAdd){
                    List<String> dataCacheMotherIDs = DataCache.getInstance().mothersSideID;
                    for(String motherSideIDs: dataCacheMotherIDs){
                        if(daEvent.getPersonID().compareToIgnoreCase(motherSideIDs)==0){
                            shouldAdd = false;
                            break;
                        }
                    }
                }
                if(!Settings.isFilterByDadsSide && shouldAdd){
                    List<String> dataCacheFatherIDs = DataCache.getInstance().fathersSideID;
                    for(String fatherIDs: dataCacheFatherIDs){
                        if(daEvent.getPersonID().compareToIgnoreCase(fatherIDs)==0){
                            shouldAdd = false;
                            break;
                        }
                    }
                }
                Person person = DataCache.getInstance().peopleMap.get(daEvent.getPersonID());

                //If you filter out males and they are male or you filter out females and they are female
                if (person !=null && (!Settings.isFilterMale && person.getGender().compareToIgnoreCase("m") == 0)
                        || (!Settings.isFilterFemale && person.getGender().compareToIgnoreCase("f") == 0)) {
                    shouldAdd = false;
                }
                if(shouldAdd){
                    lifeEvents.add(lifeEvent);
                }


            }
        }
        return lifeEvents;
    }

    public List<FamilyPerson> getFamilyPeople() {
        List<FamilyPerson> familyPeople = new ArrayList<>();
        for (Person person : DataCache.getInstance().people) {
            FamilyPerson familyPerson = familySearch(person, searchText);
            if (familyPerson != null) {
                familyPeople.add(familyPerson);
            }
        }
        return familyPeople;
    }

    private LifeEvent eventSearch(Event daEvent, String text) {
        text = text.toLowerCase();
        LifeEvent toReturn = null;

        //If any of the event have "text" somewhere in it
        if (daEvent.getEventType().toLowerCase().contains(text) ||
                String.valueOf(daEvent.getYear()).toLowerCase().contains(text) ||
                daEvent.getCity().toLowerCase().contains(text) ||
                daEvent.getCountry().toLowerCase().contains(text)) {
            //Add it as a new lifeEvent with the person associated with it
            Person associatedPerson = DataCache.getInstance().peopleMap.get(daEvent.getPersonID()); //PersonID to person
            if (associatedPerson == null) {
                System.out.println("We messed up, couldn't find the associated person");
            } else {
                toReturn = new LifeEvent(associatedPerson, daEvent);
            }
        }

        return toReturn;
    }

    private FamilyPerson familySearch(Person person, String text) {
        text = text.toLowerCase();
        FamilyPerson toReturn = null;

        //If any of the event have "text" somewhere in it
        if (person.getFirsName().toLowerCase().contains(text) ||
                person.getLastName().toLowerCase().contains(text)) {
            //Add it as a new lifeEvent with the person associated with it
            toReturn = new FamilyPerson(person); //No relationship in the familyPerson
        } else if (person.getGender().compareToIgnoreCase("m")==0) {
            if("male".contains(text)){
                toReturn = new FamilyPerson(person); //No relationship in the familyPerson
            }
        } else if (person.getGender().compareToIgnoreCase("f")==0) {
            if("female".contains(text)){
                toReturn = new FamilyPerson(person); //No relationship in the familyPerson
            }
        }

        return toReturn;
    }


}
