package com.example.familymapclient.model;

import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.cache.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Model.Event;
import Model.Person;

public class DataHolder {

    Person person;

    public DataHolder(Person person) { //Necessary rn
        this.person = person;
    }

    public List<LifeEvent> getLifeEvents() {
        List<LifeEvent> lifeEvents = new ArrayList<>();
        List<Event> events = DataCache.getInstance().eventMapPersonID.get(person.getPersonID());
        if (events != null) {
            for (Event daEvent : events) {
                Boolean shouldAdd = true;
                if (!Settings.isFilterByMomsSide && shouldAdd) {
                    List<String> dataCacheMotherIDs = DataCache.getInstance().mothersSideID;
                    for (String motherSideIDs : dataCacheMotherIDs) {
                        if (daEvent.getPersonID().compareToIgnoreCase(motherSideIDs) == 0) {
                            shouldAdd = false;
                            break;
                        }
                    }
                }
                if (!Settings.isFilterByDadsSide && shouldAdd) {
                    List<String> dataCacheFatherIDs = DataCache.getInstance().fathersSideID;
                    for (String fatherIDs : dataCacheFatherIDs) {
                        if (daEvent.getPersonID().compareToIgnoreCase(fatherIDs) == 0) {
                            shouldAdd = false;
                            break;
                        }
                    }
                }
                Person person = DataCache.getInstance().peopleMap.get(daEvent.getPersonID());

                //If you filter out males and they are male or you filter out females and they are female
                if (person != null && (!Settings.isFilterMale && person.getGender().compareToIgnoreCase("m") == 0)
                        || (!Settings.isFilterFemale && person.getGender().compareToIgnoreCase("f") == 0)) {
                    shouldAdd = false;
                }
                if (shouldAdd) {
                    lifeEvents.add(new LifeEvent(person, daEvent));
                }
            }
        }
        else {
            System.out.println("Error: No events found for personActivity");
        }
        Collections.sort(lifeEvents, new sorter());
        return lifeEvents;
    }

    class sorter implements Comparator<LifeEvent> {
        @Override
        public int compare(LifeEvent o1, LifeEvent o2) {
            return o1.getEvent().getYear() - o2.getEvent().getYear();
        }
    }

    public List<FamilyPerson> getFamilyPeople() {
        List<FamilyPerson> familyPeople = new ArrayList<>();
        List<Person> people = DataCache.getInstance().people;
        if (people != null) {
            for (Person daPerson : people) {
                FamilyPerson familyPerson = new FamilyPerson(daPerson, person);
                if (familyPerson.getRelationship() != null &&
                        familyPerson.getRelationship().compareToIgnoreCase("") != 0) {
                    familyPeople.add(familyPerson);
                }
            }
        } else {
            System.out.println("Error: No people found for personActivity");
        }
        return familyPeople;
    }
}
