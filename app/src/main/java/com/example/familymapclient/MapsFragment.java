package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.cache.Settings;
import com.example.familymapclient.serverProxy.ServerProxy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import Model.Event;
import Model.Person;
import Request.EventRequest;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap googleMap;
    public List<Person> findChildren(Person potentialParent) {
        List<Person> children = new ArrayList<>();
        for (Person person : DataCache.getInstance().people) {
            //If persons father or mother ID matches that of the potential parent
            if ((person.getFatherID() != null && person.getFatherID().compareTo(potentialParent.getPersonID()) == 0) ||
                    (person.getMotherID() != null && person.getMotherID().compareTo(potentialParent.getPersonID()) == 0)) {
                children.add(person); //They are a child
            }
        }
        return children;
    }

    public void ClearMarkersAndLines() {
        for (Marker marker : DataCache.getInstance().currentMarker) {
            marker.remove();
        }
        for (Polyline polyline : DataCache.getInstance().currentLines) {
            polyline.remove();
        }
    }
    public void familyTreeParsingMarkers(Person person, String side) {
        //Get the person associated with the map
        //Should I mark them
        boolean alreadyAdded = false;

        //Which side do I add it too
        if (side.compareTo("Father") == 0) {
            if (DataCache.getInstance().mothersSideID.contains(person.getPersonID()) ||
                    DataCache.getInstance().fathersSideID.contains(person.getPersonID())) {
                alreadyAdded = true;
            } else {
                DataCache.getInstance().fathersSideID.add(person.getPersonID());
            }
        } else if (side.compareTo("Mother") == 0) {
            if (DataCache.getInstance().mothersSideID.contains(person.getPersonID()) ||
                    DataCache.getInstance().fathersSideID.contains(person.getPersonID())) {
                alreadyAdded = true;
            } else {
                DataCache.getInstance().mothersSideID.add(person.getPersonID());
            }

        }

        if (!alreadyAdded) {
            if (person.getMotherID() != null) {
                if (DataCache.getInstance().peopleMap.get(person.getMotherID()) != null) {
                    familyTreeParsingMarkers(DataCache.getInstance().peopleMap.get(person.getMotherID()), side); //Recursive call
                } else {
                    System.out.println("Person not found while parsing ");
                }
            }
            if (person.getFatherID() != null) {
                if (DataCache.getInstance().peopleMap.get(person.getMotherID()) != null) {
                    familyTreeParsingMarkers(DataCache.getInstance().peopleMap.get(person.getFatherID()), side); //Recursive call
                } else {
                    System.out.println("Person not found while parsing ");
                }
            }
            for (Person child : findChildren(person)) {
                if (!DataCache.getInstance().mothersSideID.contains(child.getPersonID()) &&
                        !DataCache.getInstance().fathersSideID.contains(child.getPersonID()) &&
                        DataCache.getInstance().theUserPerson.getPersonID().compareToIgnoreCase(child.getPersonID()) != 0) {
                    familyTreeParsingMarkers(child, side);
                }
            }
        }

    }
    public void SetUpDataCacheWithFamilySides() {
        //Marking the clicked on event and resetting it
        if (clickedEvent != null) {
            ClickMarker(addMarker(googleMap, clickedEvent));
            clickedEvent = null;
            DataCache.getInstance().eventClickedOn = null;
        }

        //Setting up a new map data cache
        DataCache.getInstance().basePeopleID.clear();
        DataCache.getInstance().mothersSideID.clear();
        DataCache.getInstance().fathersSideID.clear();

        //Marking the base events for the user, and their children
        Person user = DataCache.getInstance().theUserPerson;
        if (user.getPersonID() != null) {
            //DataCache dataCache = DataCache.getInstance(); //For debug
            DataCache.getInstance().basePeopleID.add(user.getPersonID());
            Person spouse = DataCache.getInstance().peopleMap.get(user.getSpouseID());
            if (spouse != null) { //Add the spouse if they exist
                DataCache.getInstance().basePeopleID.add(spouse.getPersonID());
            }
            List<Person> children = findChildren(user);
            for (Person child : children) {
                DataCache.getInstance().basePeopleID.add(child.getPersonID());
            }
        }
        //Setting up the data cache lists
        if (DataCache.getInstance().theUserPerson.getMotherID() != null) {
            familyTreeParsingMarkers(DataCache.getInstance().peopleMap.
                            get(DataCache.getInstance().theUserPerson.getMotherID()),
                    "Mother"); //Recursive call
        }
        if (DataCache.getInstance().theUserPerson.getFatherID() != null) {
            familyTreeParsingMarkers(DataCache.getInstance().peopleMap.
                            get(DataCache.getInstance().theUserPerson.getFatherID()),
                    "Father"); //Recursive call
        }
    }
    private boolean ClickMarker(@NonNull Marker marker) {
        //Zooming in the camera
        LatLng position = marker.getPosition();
        float zoom = (float) 4;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

        //Create a event activity and send it the mapFragment
        Event markerEvent = (Event) marker.getTag();
        assert markerEvent != null;
        Person person = DataCache.getInstance().peopleMap.get(markerEvent.getPersonID()); //List of 1

        DataCache.getInstance().personClickedOn = person; //Caching the person clicked on

        //Doing all the event display logic
        assert person != null;
        String name = person.getFirsName() + " " + person.getLastName();
        String birth = "Birth: " + markerEvent.getCity() + ", " + markerEvent.getCountry()
                + "(" + markerEvent.getYear() + ")";
        String eventDescription = name + "\n" + birth;
        eventText.setText(eventDescription);
        if (person.getGender().compareToIgnoreCase("M") == 0) {
            imageView.setBackgroundResource(R.drawable.ic_male);
        } else if (person.getGender().compareToIgnoreCase("F") == 0) {
            imageView.setBackgroundResource(R.drawable.ic_female);
        } else
            imageView.setBackgroundResource(R.drawable.ic_person); //Not so good, shouldn't happen

        //Undraws your line
        for (Polyline line : DataCache.getInstance().currentLines) {
            line.remove();
        }
        //Drawing the lines
        float baseWidth = (float) 25; //What?
        if (Settings.isLifeStoryLines) {
            List<Event> allPersonEvents = DataCache.getInstance().eventMapPersonID.get(person.getPersonID());
            assert allPersonEvents != null;
            Collections.sort(allPersonEvents, new sorter());
            float currentWidth = baseWidth;
            for (int i = 0; i < allPersonEvents.size() - 1; i++) {
                drawLine(allPersonEvents.get(i), allPersonEvents.get(i + 1), googleColorLifeStoryLine, currentWidth);
                currentWidth *= .6;
            }
        }
        if (Settings.isSpouseLines) {
            if (person.getSpouseID() != null) { //If they have a spouse
                List<Event> spouseEvents = DataCache.getInstance().eventMapPersonID.get(person.getSpouseID());
                assert spouseEvents != null;
                Collections.sort(spouseEvents, new sorter());
                drawLine((Event) marker.getTag(), spouseEvents.get(0), googleColorSpouseLineInt, baseWidth);
            }
        }
        if (Settings.isFamilyTreeLines) {
            if (Settings.isFilterByMomsSide) {
                if (person.getMotherID() != null) {
                    List<Event> momsEvents = DataCache.getInstance().eventMapPersonID.get(person.getMotherID());
                    assert momsEvents != null;
                    if (!momsEvents.isEmpty()) {
                        Collections.sort(momsEvents, new sorter());
                        Event firstEvent = momsEvents.get(0);
                        familyTreeParsingLines(firstEvent, markerEvent, baseWidth);
                    }
                }
            }
            if (Settings.isFilterByDadsSide) {
                if (person.getFatherID() != null) {
                    List<Event> dadsEvents = DataCache.getInstance().eventMapPersonID.get(person.getFatherID());
                    assert dadsEvents != null;
                    if (!dadsEvents.isEmpty()) {
                        Collections.sort(dadsEvents, new sorter());
                        Event firstEvent = dadsEvents.get(0);
                        familyTreeParsingLines(firstEvent, markerEvent, baseWidth); //Parse through
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        //After we cache the events
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                //Means its got the events cached
                super.handleMessage(msg);


                //Clear
                ClearMarkersAndLines();

                //Filling the data cache with mother's side, father's, and base
                SetUpDataCacheWithFamilySides();


                //Marking all events based upon conditions
                if (Settings.isFilterByMomsSide) { //Marking moms side
                    List<String> mothersSide = DataCache.getInstance().mothersSideID;
                    for (String personID : mothersSide) {
                        Person person = DataCache.getInstance().peopleMap.get(personID);
                        if ((Settings.isFilterMale && Objects.requireNonNull(person).getGender().compareToIgnoreCase("m") == 0)
                                || (Settings.isFilterFemale && Objects.requireNonNull(person).getGender().compareToIgnoreCase("f") == 0)) {
                            for (Event event : Objects.requireNonNull(DataCache.getInstance().eventMapPersonID.get(person.getPersonID()))) {
                                addMarker(googleMap, event);
                            }
                        }

                    }
                }
                if (Settings.isFilterByDadsSide) { //Marking dads side
                    List<String> fathersSide = DataCache.getInstance().fathersSideID;
                    for (String personID : fathersSide) {
                        Person person = DataCache.getInstance().peopleMap.get(personID);
                        if ((Settings.isFilterMale && Objects.requireNonNull(person).getGender().compareToIgnoreCase("m") == 0)
                                || (Settings.isFilterFemale && Objects.requireNonNull(person).getGender().compareToIgnoreCase("f") == 0)) {
                            for (Event event : Objects.requireNonNull(DataCache.getInstance().eventMapPersonID.get(person.getPersonID()))) {
                                addMarker(googleMap, event);
                            }
                        }
                    }
                }
                List<String> basePeople = DataCache.getInstance().basePeopleID;
                for (String personID : basePeople) { //Marking user, spouse and children
                    Person person = DataCache.getInstance().peopleMap.get(personID);
                    if ((Settings.isFilterMale && Objects.requireNonNull(person).getGender().compareToIgnoreCase("m") == 0)
                            || (Settings.isFilterFemale && Objects.requireNonNull(person).getGender().compareToIgnoreCase("f") == 0)) {
                        for (Event event : Objects.requireNonNull(DataCache.getInstance().eventMapPersonID.get(person.getPersonID()))) {
                            addMarker(googleMap, event);
                        }
                    }
                }
                //DataCache dataCache = DataCache.getInstance(); //For debug
                GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        return ClickMarker(marker);
                    }
                };
                googleMap.setOnMarkerClickListener(onMarkerClickListener);
            }




        };

        //Updating the events
        ServerProxy serverProxy = new ServerProxy(DataCache.getInstance().serverHost, DataCache.getInstance().serverPort);
        EventRequest eventRequest = new EventRequest();
        serverProxy.cacheEvents(eventRequest, handler, DataCache.getInstance().authToken);
    }

    public void familyTreeParsingLines(Event baseEvent, Event lastEvent, float currentWidth) {
        //Get the person associated with the map
        Person associatedPerson = DataCache.getInstance().peopleMap.get(baseEvent.getPersonID());

        if (Settings.isFilterMale) {
            assert associatedPerson != null;
            if (associatedPerson.getGender().compareToIgnoreCase("m") == 0) {
                drawLine(lastEvent, baseEvent, googleColorFamilyTreeLine, currentWidth);
            }
        }
        if (Settings.isFilterFemale) {
            assert associatedPerson != null;
            if (associatedPerson.getGender().compareToIgnoreCase("f") == 0) {
                drawLine(lastEvent, baseEvent, googleColorFamilyTreeLine, currentWidth);
            }
        }
        currentWidth *= .6;
        assert associatedPerson != null;
        if (associatedPerson.getMotherID() != null) {
            List<Event> momsEvents = DataCache.getInstance().eventMapPersonID.get(associatedPerson.getMotherID());
            assert momsEvents != null;
            if (!momsEvents.isEmpty()) {
                Collections.sort(momsEvents, new sorter());
                familyTreeParsingLines(momsEvents.get(0), baseEvent, currentWidth); //Parse through
            }
        }
        if (associatedPerson.getFatherID() != null) {
            List<Event> dadsEvents = DataCache.getInstance().eventMapPersonID.get(associatedPerson.getFatherID());
            assert dadsEvents != null;
            if (!dadsEvents.isEmpty()) {
                Collections.sort(dadsEvents, new sorter());
                familyTreeParsingLines(dadsEvents.get(0), baseEvent, currentWidth); //Parse through
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this); //
    }


    //Colors to use
    float googleColorBirth = BitmapDescriptorFactory.HUE_GREEN;
    float googleColorMarriage = BitmapDescriptorFactory.HUE_AZURE;
    float googleColorDeadAsADoorNail = BitmapDescriptorFactory.HUE_RED;
    int googleColorSpouseLineInt = Color.WHITE;
    int googleColorFamilyTreeLine = Color.BLACK;
    int googleColorLifeStoryLine = Color.CYAN;


    ImageView imageView = null;
    TextView eventText = null;
    Event clickedEvent = DataCache.getInstance().eventClickedOn;


    //Menu section
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_maps);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!DataCache.getInstance().calledFromEventActivity) {
            inflater.inflate(R.menu.menu_resource_file, menu);

            //Is this necessary?
            MenuItem searchMenuItem = menu.findItem(R.id.searchMenuButton);
            MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuButton);
            searchMenuItem.setEnabled(true);
            settingsMenuItem.setEnabled(true);
        } else {
            DataCache.getInstance().calledFromEventActivity = false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsMenuButton) {
            //Get activity is the current context
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.searchMenuButton) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }


    public void drawLine(Event startEvent, Event endEvent, int googleColor, float width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endpoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endpoint)
                .color(googleColor)
                .width(width);
        Polyline line = googleMap.addPolyline(options);
        DataCache.getInstance().currentLines.add(line);
    }

    //Adding a marker
    public Marker addMarker(GoogleMap googleMap, Event event) {
        float myColor = BitmapDescriptorFactory.HUE_YELLOW;//If you see yellow its just other whack events
        if (event.getEventType().compareTo("birth") == 0) {
            myColor = googleColorBirth;
        } else if (event.getEventType().compareTo("death") == 0) {
            myColor = googleColorDeadAsADoorNail;
        } else if (event.getEventType().compareTo("marriage") == 0) {
            myColor = googleColorMarriage;
        }
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(event.getLatitude(), event.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(myColor)));
        assert marker != null;
        marker.setTag(event);//Markers will have tags with their events
        DataCache.getInstance().currentMarker.add(marker);

        return marker;
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            //Implemented in the other onMapReady
        }
    };

    public static class sorter implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getYear() - o2.getYear();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        imageView = view.findViewById(R.id.iconForEvent);
        eventText = view.findViewById(R.id.mapTextView);
        imageView.setBackgroundResource(R.drawable.ic_person);
        View layout = view.findViewById(R.id.eventSection);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating an eventActivity if its been clicked before
                if (!eventText.getText().equals("Events will appear here")) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

}