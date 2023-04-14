package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.model.DataHolder;
import com.example.familymapclient.model.FamilyPerson;
import com.example.familymapclient.model.LifeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {

    Person person = DataCache.getInstance().personClickedOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //Person stuff
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        TextView firstNameText = findViewById(R.id.firstNamePersonActivity);
        TextView lastNameText = findViewById(R.id.lastNamePersonActivity);
        TextView genderText = findViewById(R.id.genderPersonActivity);
        TextView firstNameDescriptionText = findViewById(R.id.firstNameDescriptionPersonActivity);
        TextView lastNameDescriptionText = findViewById(R.id.lastNameDescriptionPersonActivity);
        TextView genderDescriptionText = findViewById(R.id.genderDescriptionPersonActivity);
        firstNameDescriptionText.setText("First Name");
        lastNameDescriptionText.setText("Last Name");
        genderDescriptionText.setText("Gender");


        firstNameText.setText(person.getFirsName());
        lastNameText.setText(person.getLastName());
        if(person.getGender().compareToIgnoreCase("M")==0){
            genderText.setText("Male");
        }
        else if (person.getGender().compareToIgnoreCase("F")==0) {
            genderText.setText("Female");
        }
        else{
            genderText.setText("Agender");
        }

        //Creating all of the life events and family
        DataHolder dataHolder = new DataHolder(person);
        List<LifeEvent> LifeEvents = dataHolder.getLifeEvents();
        List<FamilyPerson> familyPeople = dataHolder.getFamilyPeople();

        expandableListView.setAdapter(new ExpandableListAdapter(LifeEvents, familyPeople));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int LIFE_EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<LifeEvent> LifeEvents;
        private final List<FamilyPerson> familyPeople;

        ExpandableListAdapter(List<LifeEvent> LifeEvents, List<FamilyPerson> familyPeople) {
            this.LifeEvents = LifeEvents;
            this.familyPeople = familyPeople;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    return LifeEvents.size();
                case FAMILY_GROUP_POSITION:
                    return familyPeople.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            // Not used
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // Not used
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    titleView.setText("Life Events");
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText("Family");
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.life_event_item, parent, false);
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family_person, parent, false);
                    initializeFamilyPersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventView(View lifeEventView, final int childPosition) {
            TextView lifeEventBirthInfo = lifeEventView.findViewById(R.id.lifeEventBirthInfo);
            lifeEventBirthInfo.setText(LifeEvents.get(childPosition).getBirthInfo());

            TextView lifeEventNameInfo = lifeEventView.findViewById(R.id.lifeEventNameInfo);
            lifeEventNameInfo.setText(LifeEvents.get(childPosition).getPersonName());

            ImageView lifeEventMarkerImage = lifeEventView.findViewById(R.id.lifeEventImage);
            lifeEventMarkerImage.setBackgroundResource(R.drawable.ic_event_marker);

            lifeEventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),EventActivity.class);
                    //Make this a bundle
                    //LifeEvents.get(childPosition)
                    //intent.putExtra("key",map);
                    DataCache.getInstance().eventClickedOn = LifeEvents.get(childPosition).getEvent(); //Caching the event associated with the thing
                    startActivity(intent); //Starting it

//                    String string = "R.string.skiResortToastText" + LifeEvents.get(childPosition).getBirthInfo();
//                    Toast.makeText(PersonActivity.this,string, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void initializeFamilyPersonView(View familyPersonView, final int childPosition) {
            TextView personName = familyPersonView.findViewById(R.id.personNameForFamilyPerson);
            personName.setText(familyPeople.get(childPosition).getName());

            TextView personRelationship = familyPersonView.findViewById(R.id.personRelationshipForFamilyPerson);
            personRelationship.setText(familyPeople.get(childPosition).getRelationship());

            //Figuring out image
            ImageView genderImageForPerson = familyPersonView.findViewById(R.id.imageViewForFamilyPerson);
            if(familyPeople.get(childPosition).getGender().compareToIgnoreCase("M")==0){
                genderImageForPerson.setBackgroundResource(R.drawable.ic_male);
            }
            else if(familyPeople.get(childPosition).getGender().compareToIgnoreCase("F")==0){
                genderImageForPerson.setBackgroundResource(R.drawable.ic_female);
            }
            else {
                genderImageForPerson.setBackgroundResource(R.drawable.ic_person); //Not good
            }

            familyPersonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),PersonActivity.class);
                    Person test = familyPeople.get(childPosition).getPerson();
                    DataCache.getInstance().personClickedOn = familyPeople.get(childPosition).getPerson(); //Caching the event associated with the thing
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }



    //Menu section
    //Realized I don't want a menu section
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater myMenuInflater = new MenuInflater(getApplicationContext());
//        myMenuInflater.inflate(R.menu.menu_resource_file, menu);
//
//        //Is this necessary?
//        MenuItem searchMenuItem = menu.findItem(R.id.searchMenuButton);
//        MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuButton);
//
//        searchMenuItem.setEnabled(true);
//        settingsMenuItem.setEnabled(true);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.settingsMenuButton) {
//            //Get activity is the current context
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivity(intent);
//        }
//        else if (item.getItemId() == R.id.searchMenuButton) {
//            Intent intent = new Intent(this, SearchActivity.class);
//            startActivity(intent);
//        }
//        else if(item.getItemId() == android.R.id.home){
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }
//        return true;
//    }
}