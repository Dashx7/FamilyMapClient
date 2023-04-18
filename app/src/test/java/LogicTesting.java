import com.example.familymapclient.MapsFragment;
import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.cache.Settings;
import com.example.familymapclient.model.DataGenerator;
import com.example.familymapclient.model.FamilyPerson;
import com.example.familymapclient.model.LifeEvent;
import com.example.familymapclient.serverProxy.ServerProxy;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Event;
import Request.EventRequest;
import Request.LoginRequest;
import Result.LoginResult;

public class LogicTesting{
    //Calculates family relationships (i.e., spouses, parents, children)
    //Filters events according to the current filter settings
    //Chronologically sorts a person’s individual events (birth first, death last, etc.)
    //Correctly searches for people and events (for your Search Activit
    @Before
    public void cleanUp(){
        DataCache.resetCacheForTesting();
        Settings.Reset();
    }
    private static void loginSetup() {
        //Make sure that the local host 8080 default has been put in, and hasn't been cleared recently otherwise it will fail
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("sheila");
        loginRequest.setPassword("parker");

        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.login(loginRequest);
        DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken!=null;
        LoginResult result = DataCache.getInstance().loginResult;
        assert result.isSuccess();

        EventRequest eventRequest = new EventRequest();
        proxy.cachePeople(DataCache.getInstance().loginResult.getAuthtoken());
        proxy.cacheUserPeopleWithID(DataCache.getInstance().loginResult.getAuthtoken(), DataCache.getInstance().loginResult.getPersonID());
        proxy.cacheEvents(eventRequest, DataCache.getInstance().authToken);
    }
    @Test public void familyRelationships(){
        loginSetup();

        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.SetUpDataCacheWithFamilySides();
        List<String> mothersSideID = DataCache.getInstance().mothersSideID;
        List<String> fathersSideID = DataCache.getInstance().fathersSideID;
        assert mothersSideID.size() == 3;
        assert mothersSideID.contains("Betty_White");//Assert some people others are just size
        assert mothersSideID.contains("Mrs_Jones");
        assert mothersSideID.contains("Frank_Jones");
        assert fathersSideID.size() == 3;
        assert DataCache.getInstance().basePeopleID.size() == 2;
    }

    @Test public void familyRelationshipsNegative(){
        loginSetup();

        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.SetUpDataCacheWithFamilySides();
        List<String> mothersSideID = DataCache.getInstance().mothersSideID;
        List<String> fathersSideID = DataCache.getInstance().fathersSideID;
        assert mothersSideID.size() == 3;
        assert !mothersSideID.contains("Blaine_McGary");//Assert some people others are just size
        assert !mothersSideID.contains("Mrs_Rodham");
        assert !mothersSideID.contains("Ken_Rodham");
        assert fathersSideID.size() == 3;
        assert DataCache.getInstance().basePeopleID.size() == 2;

    }
    @Test public void FilterEvents(){
        loginSetup();

        Settings.isFilterMale = false;
        DataGenerator dataGenerator = new DataGenerator("Frog");
        List<FamilyPerson> familyPeople = dataGenerator.getFamilyPeople();
        List<LifeEvent> lifeEvents = dataGenerator.getLifeEvents();
        assert familyPeople.size()==0;
        assert lifeEvents.size()==0;

        Settings.isFilterMale = true;
        Settings.isFilterByMomsSide = false;
        Settings.isFilterByDadsSide = false;
        dataGenerator = new DataGenerator("Frog");
        familyPeople = dataGenerator.getFamilyPeople();
        lifeEvents = dataGenerator.getLifeEvents();
        assert familyPeople.size()==0;
        assert lifeEvents.size()==1;
    }
    @Test public void FilterEventsNegative(){
        loginSetup();

        Settings.isFilterMale = false;
        DataGenerator dataGenerator = new DataGenerator("Frog");
        List<FamilyPerson> familyPeople = dataGenerator.getFamilyPeople();
        List<LifeEvent> lifeEvents = dataGenerator.getLifeEvents();
        assert familyPeople.size()==0;
        assert lifeEvents.size()==0;

        Settings.isFilterMale = true;
        Settings.isFilterFemale = false;
        dataGenerator = new DataGenerator("Frog");
        familyPeople = dataGenerator.getFamilyPeople();
        lifeEvents = dataGenerator.getLifeEvents();
        assert familyPeople.size()==0;
        assert lifeEvents.size()==1;
    }
    @Test public void searchPeopleAndEvents(){
        loginSetup();

        DataGenerator dataGenerator = new DataGenerator("Frog");
        List<FamilyPerson> familyPeople = dataGenerator.getFamilyPeople();
        List<LifeEvent> lifeEvents = dataGenerator.getLifeEvents();
        assert familyPeople.size()==0;
        assert lifeEvents.size()==1;
        dataGenerator = new DataGenerator("Betty");
        familyPeople = dataGenerator.getFamilyPeople();
        lifeEvents = dataGenerator.getLifeEvents();
        assert familyPeople.size()==1;
        assert lifeEvents.size()==0;
    }
    @Test public void searchPeopleAndEventsNegative(){
        loginSetup();

        DataGenerator dataGenerator = new DataGenerator("Supercalifragilistic"); //Shouldn't be anything there
        List<FamilyPerson> familyPeople = dataGenerator.getFamilyPeople();
        List<LifeEvent> lifeEvents = dataGenerator.getLifeEvents();
        assert familyPeople.size()==0;
        assert lifeEvents.size()==0;
    }
    @Test
    public void chronologicalSorter(){
        Event event1999 = new Event();
        event1999.setYear(1999);
        Event event2000 = new Event();
        event2000.setYear(2000);
        Event event2001 = new Event();
        event2001.setYear(2001);
        List<Event> eventList = new ArrayList<>();
        eventList.add(event2000);
        eventList.add(event1999);
        eventList.add(event2001);
        Collections.sort(eventList, new MapsFragment.sorter());
        assert eventList.get(0).getYear()==1999;
        assert eventList.get(1).getYear()==2000;
        assert eventList.get(2).getYear()==2001;
    }
    @Test
    public void chronologicalSorterAbstract(){
        Event event1999 = new Event();
        event1999.setYear(-1999); //Imaging a family tree that went back into BC times
        Event event2000 = new Event();
        event2000.setYear(2000);
        Event event2001 = new Event();
        event2001.setYear(2001);
        List<Event> eventList = new ArrayList<>();
        eventList.add(event2000);
        eventList.add(event1999);
        eventList.add(event2001);
        Collections.sort(eventList, new MapsFragment.sorter());
        assert eventList.get(0).getYear()==-1999;
        assert eventList.get(1).getYear()==2000;
        assert eventList.get(2).getYear()==2001;
    }
}
