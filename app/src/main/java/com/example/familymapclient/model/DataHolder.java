package com.example.familymapclient.model;

import com.example.familymapclient.cache.DataCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;

public class DataHolder {

    Person person;
    public DataHolder(Person person){ //Necessary rn
        this.person = person;
    }

    public List<LifeEvent> getLifeEvents() {
        List<LifeEvent> lifeEvents = new ArrayList<>();
        //Map<String, List<Event>> testMap = DataCache.getInstance().eventMapPersonID;
        List<Event> events = DataCache.getInstance().eventMapPersonID.get(person.getPersonID());
        if(events!=null){
            for(Event daEvent: events){
                lifeEvents.add(new LifeEvent(person,daEvent));
            }
        }
        else{
            System.out.println("Error: No events found for personActivity");
        }
        return lifeEvents;
    }

    public List<FamilyPerson> getFamilyPeople() {
        List<FamilyPerson> familyPeople = new ArrayList<>();
        List<Person> people = DataCache.getInstance().people;
        if(people!=null){
            for(Person daPerson: people){
                //TODO reduce code duplication
                if(daPerson.getSpouseID().compareToIgnoreCase(person.getPersonID())==0){ //Is it the persons spouse?
                    FamilyPerson familyPerson = new FamilyPerson("Spouse",daPerson);
                    familyPeople.add(familyPerson);
                }
                else if(daPerson.getFatherID().compareToIgnoreCase(person.getPersonID())==0){// Is it the persons child? (Male)
                    FamilyPerson familyPerson = new FamilyPerson("Child",daPerson);
                    familyPeople.add(familyPerson);
                }
                else if(daPerson.getMotherID().compareToIgnoreCase(person.getPersonID())==0){// Is it the persons child? (female)
                    FamilyPerson familyPerson = new FamilyPerson("Child", daPerson);
                    familyPeople.add(familyPerson);
                }
                else if(daPerson.getPersonID().compareToIgnoreCase(person.getFatherID())==0){ //Is it the persons father
                    FamilyPerson familyPerson = new FamilyPerson("Father",daPerson);
                    familyPeople.add(familyPerson);
                }
                else if(daPerson.getPersonID().compareToIgnoreCase(person.getMotherID())==0){ //Is it the persons Mother
                    FamilyPerson familyPerson = new FamilyPerson("Mother",daPerson);
                    familyPeople.add(familyPerson);
                }
            }
        }
        else{
            System.out.println("Error: No people found for personActivity");
        }
        return familyPeople;
    }
}
