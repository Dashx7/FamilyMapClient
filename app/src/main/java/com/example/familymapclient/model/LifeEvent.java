package com.example.familymapclient.model;

import Model.Event;
import Model.Person;

public class LifeEvent {
    private final String birthInfo;
    private final String personName;
    private final Event event;

    public LifeEvent(Person person, Event daEvent) {
        String upperCaseEvent = daEvent.getEventType().substring(0,1).toUpperCase() + daEvent.getEventType().substring(1);
        this.birthInfo = upperCaseEvent + ": " + daEvent.getCity() + ", " + daEvent.getCountry() + "("
                + daEvent.getYear() + ")";
        this.personName = person.getFirsName() + " " + person.getLastName();;
        this.event = daEvent;
    }

    public String getBirthInfo() {
        return birthInfo;
    }

    public String getPersonName() {
        return personName;
    }

    public Event getEvent() {
        return event;
    }
}
