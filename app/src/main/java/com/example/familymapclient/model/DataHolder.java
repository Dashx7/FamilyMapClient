package com.example.familymapclient.model;

import com.example.familymapclient.cache.DataCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;

public class DataHolder {

    Person person;
    public DataHolder(Person person){
        this.person = person;
    }

    public List<LifeEvent> getLifeEvents() {
        List<LifeEvent> lifeEvents = new ArrayList<>();
        Map<String, List<Event>> testMap = DataCache.getInstance().eventMapPersonID;
        List<Event> events = DataCache.getInstance().eventMapPersonID.get(person.getPersonID());
        if(events!=null){
            for(Event daEvent: events){

                String event = daEvent.getEventType() + ": " + daEvent.getCity() + ", " + daEvent.getCountry() + "("
                        + daEvent.getYear() + ")";
                String name = person.getFirsName() + " " + person.getLastName();
                lifeEvents.add(new LifeEvent(event,name));
            }
        }
        return lifeEvents;
    }

    public List<FamilyPerson> getHikingTrails() {
        List<FamilyPerson> familyPeople = new ArrayList<>();

        familyPeople.add(new FamilyPerson("Angel's Landing", "Zion National Park", "Strenuous"));
        familyPeople.add(new FamilyPerson("Donut Falls", "Big Cottonwood Canyon", "Easy"));
        familyPeople.add(new FamilyPerson("The Narrows", "Zion National Park", "Easy to Strenuous"));
        familyPeople.add(new FamilyPerson("The Wave", "Vermilion Cliffs Wilderness", "Moderate"));
        familyPeople.add(new FamilyPerson("Golden Cathedral Trail", "Grand Staircase-Escalante National Monument", "Moderate"));
        familyPeople.add(new FamilyPerson("Paria Canyon Backpacking Trail", "Vermilion Cliffs Wilderness", "Moderate"));
        familyPeople.add(new FamilyPerson("Buckskin Gulch Day Hike", "Vermilion Cliffs Wilderness", "Strenuous"));
        familyPeople.add(new FamilyPerson("Delicate Arch Trail", "Arches National Park", "Moderate"));
        familyPeople.add(new FamilyPerson("Mount Timpanogos", "Wasatch Mountain Range", "Strenuous"));
        familyPeople.add(new FamilyPerson("Pfiefferhorn Trail", "Wasatch Mountain Range", "Strenuous"));
        familyPeople.add(new FamilyPerson("Fairyland Loop", "Bryce Canyon", "Strenuous"));
        familyPeople.add(new FamilyPerson("Diamond Fork Hot Springs Trail", "Springville", "Easy"));
        familyPeople.add(new FamilyPerson("The Highland Trail", "Uintah Mountains", "Strenuous"));
        familyPeople.add(new FamilyPerson("Little Wild Horse/Bell Canyon Trail", "Hanksville", "Moderate"));
        familyPeople.add(new FamilyPerson("Kannara Creek Canyon Trail", "Kannaraville", "Moderate"));

        return familyPeople;
    }
}
