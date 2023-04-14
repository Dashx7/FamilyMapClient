package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
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

import Model.Event;
import Model.Person;
import Request.EventRequest;

public class MapsFragment extends Fragment {

    //Colors to use
    float googleColorBirth = BitmapDescriptorFactory.HUE_GREEN;
    float googleColorMarriage = BitmapDescriptorFactory.HUE_AZURE;
    float googleColorDeadAsADoorNail = BitmapDescriptorFactory.HUE_RED;

    ImageView imageView = null;
    TextView eventText = null;
    Event clickedEvent = DataCache.getInstance().eventClickedOn;

    //Layout eventLayout = null;

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
        inflater.inflate(R.menu.menu_resource_file, menu);

        //Is this necessary?
        MenuItem searchMenuItem = menu.findItem(R.id.searchMenuButton);
        MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuButton);
        searchMenuItem.setEnabled(true);
        settingsMenuItem.setEnabled(true);
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


    public void drawLine(Event startEvent, Event endEvent, float googleColor, float width, GoogleMap googleMap) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endpoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endpoint)
                .color((int) googleColor)
                .width(width);
        Polyline line = googleMap.addPolyline(options);

        //line.remove();
    }

    //Adding a marker
    public Marker addMarker(GoogleMap googleMap, Event event) {
        //IF YOU SEE YELLOW I HAVE FUCKED UP
        float myColor = BitmapDescriptorFactory.HUE_YELLOW;
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
        marker.setTag(event);//Markers will have tags with their events
        return marker;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //After we cache the events
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    //Means its got the events cached
                    super.handleMessage(msg);

                    //Marking all events
                    for (Event event : DataCache.getInstance().events) {
                        if(clickedEvent!=null &&
                                event.getEventID().compareToIgnoreCase(clickedEvent.getEventID())==0){
                            ClickMarker(addMarker(googleMap, event));
                            clickedEvent = null;
                            DataCache.getInstance().eventClickedOn = null;
                        }
                        else{
                            addMarker(googleMap, event);
                        }
                    }

                    GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            return ClickMarker(marker);
                        }
                    };
                    googleMap.setOnMarkerClickListener(onMarkerClickListener);
                }

                private boolean ClickMarker(@NonNull Marker marker) {
                    LatLng position = marker.getPosition();
                    float zoom = (float) 4;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
                    //Create a event activity and send it the mapFragment
                    Event markerEvent = (Event) marker.getTag();
                    //Map<String, List<Person>> peopleMap =  DataCache.getInstance().peopleMap;
                    Person person = DataCache.getInstance().peopleMap.get(markerEvent.getPersonID()); //List of 1

                    DataCache.getInstance().personClickedOn = person;

                    //Doing all the event display logic
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
                        imageView.setBackgroundResource(R.drawable.ic_person); //Not so good

                    //TODO
                    //Drawing the lines
                    if (Settings.isLifeStoryLines) {

                    }
                    if (Settings.isSpouseLines) {
                        for (Person thePerson : DataCache.getInstance().people) {

                            if (thePerson.getSpouseID() != null) { //If they have a spouse
                                //Event spouseEvents = DataCache.getInstance().eventMapPersonID.get(thePerson.getSpouseID());
                                //if(thePerson.getSpouseID())
                            }
                        }
                    }
                    if (Settings.isFilterOutMale) {

                    }
                    if (Settings.isFilterOutFemale) {

                    }
                    if (Settings.isFilterByMomsSide) {

                    }
                    if (Settings.isFilterByDadsSide) {

                    }

                    return true;
                }
            };

            ServerProxy serverProxy = new ServerProxy(DataCache.getInstance().serverHost, DataCache.getInstance().serverPort);
            EventRequest eventRequest = new EventRequest();
            serverProxy.cacheEvents(eventRequest, handler, DataCache.getInstance().authToken);


        }

        public void testMethod(GoogleMap googleMap, Marker marker) {
//            googleMap.clear();
//            googleMap.addPolyline();
//            googleMap.addMarker();
//            googleMap.moveCamera();
//            googleMap.animateCamera();
//            googleMap.setMapType();
//            googleMap.setOnMarkerClickListener();
//            marker.setTag();
//            marker.getTag()
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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