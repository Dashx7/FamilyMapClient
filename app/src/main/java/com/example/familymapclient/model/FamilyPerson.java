package com.example.familymapclient.model;

import Model.Person;

public class FamilyPerson {
    private final String name;
    private final String relationship;
    private final String gender;
    private final Person person;

    public FamilyPerson(String relationship, Person person) {
        this.name = person.getFirsName() + " " + person.getLastName();
        this.relationship = relationship;
        this.gender = person.getGender();
        this.person = person;
    }
    public FamilyPerson(Person person) {
        this.name = person.getFirsName() + " " + person.getLastName();
        this.gender = person.getGender();
        this.person = person;
        this.relationship = null;
    }
    public FamilyPerson(Person daPerson, Person personToCompareTo) {
        //daPerson is the person and personToCompareTo is there for relationship advice
        if(daPerson.getSpouseID().compareToIgnoreCase(personToCompareTo.getPersonID())==0){ //Is it the persons spouse?
            relationship = "Spouse";
        }
        else if(daPerson.getFatherID().compareToIgnoreCase(personToCompareTo.getPersonID())==0){// Is it the persons child? (Male)
            relationship = "Child";
        }
        else if(daPerson.getMotherID().compareToIgnoreCase(personToCompareTo.getPersonID())==0){// Is it the persons child? (female)
            relationship = "Child";
        }
        else if(daPerson.getPersonID().compareToIgnoreCase(personToCompareTo.getFatherID())==0){ //Is it the persons father
            relationship = "Father";
        }
        else if(daPerson.getPersonID().compareToIgnoreCase(personToCompareTo.getMotherID())==0){ //Is it the persons Mother
            relationship = "Mother";
        }
        else {
            relationship = null;
        }
        this.name = personToCompareTo.getFirsName() + " " + personToCompareTo.getLastName();
        this.gender = personToCompareTo.getGender();
        this.person = personToCompareTo;
    }

    public String getName() {
        return name;
    }

    public String getRelationship() {
        return relationship;
    }
    public String getGender(){return gender;}

    public Person getPerson() {
        return person;
    }
}
