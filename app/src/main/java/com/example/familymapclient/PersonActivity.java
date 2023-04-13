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

import java.util.List;

import Model.Person;

public class PersonActivity extends AppCompatActivity {

    Person person = DataCache.getInstance().personClickedOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //Person stuff
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        //Creating all of the life events and family
        DataHolder dataHolder = new DataHolder(person);
        List<LifeEvent> LifeEvents = dataHolder.getLifeEvents();
        List<FamilyPerson> familyPeople = dataHolder.getHikingTrails();

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
                    itemView = getLayoutInflater().inflate(R.layout.hiking_trail_item, parent, false);
                    initializeHikingTrailView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventView(View skiResortItemView, final int childPosition) {
            TextView lifeEventBirthInfo = skiResortItemView.findViewById(R.id.lifeEventBirthInfo);
            lifeEventBirthInfo.setText(LifeEvents.get(childPosition).getBirthInfo());

            TextView lifeEventNameInfo = skiResortItemView.findViewById(R.id.lifeEventNameInfo);
            lifeEventNameInfo.setText(LifeEvents.get(childPosition).getPersonName());

            ImageView lifeEventMarkerImage = skiResortItemView.findViewById(R.id.lifeEventImage);
            lifeEventMarkerImage.setBackgroundResource(R.drawable.ic_event_marker);

            skiResortItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String string = "R.string.skiResortToastText" + LifeEvents.get(childPosition).getBirthInfo();
                    Toast.makeText(PersonActivity.this,string, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void initializeHikingTrailView(View hikingTrailItemView, final int childPosition) {
            TextView trailNameView = hikingTrailItemView.findViewById(R.id.hikingTrailTitle);
            trailNameView.setText(familyPeople.get(childPosition).getName());

            TextView trailLocationView = hikingTrailItemView.findViewById(R.id.hikingTrailLocation);
            trailLocationView.setText(familyPeople.get(childPosition).getLocation());

            TextView trailDifficulty = hikingTrailItemView.findViewById(R.id.hikingTrailDifficulty);
            trailDifficulty.setText(familyPeople.get(childPosition).getDifficulty());

            hikingTrailItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String string = "example" + familyPeople.get(childPosition).getName();
                    Toast.makeText(PersonActivity.this, string, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }



    //Menu section
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenuInflater = new MenuInflater(getApplicationContext());
        myMenuInflater.inflate(R.menu.menu_resource_file, menu);

        //Is this necessary?
        MenuItem searchMenuItem = menu.findItem(R.id.searchMenuButton);
        MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuButton);

        searchMenuItem.setEnabled(true);
        settingsMenuItem.setEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsMenuButton) {
            //Get activity is the current context
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.searchMenuButton) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}