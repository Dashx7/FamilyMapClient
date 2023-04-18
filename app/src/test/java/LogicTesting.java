import com.example.familymapclient.MapsFragment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Event;

public class LogicTesting {
    //Calculates family relationships (i.e., spouses, parents, children)
    //Filters events according to the current filter settings
    //Chronologically sorts a person’s individual events (birth first, death last, etc.)
    //Correctly searches for people and events (for your Search Activit




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
