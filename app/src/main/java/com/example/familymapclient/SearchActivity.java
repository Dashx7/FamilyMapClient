package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.model.DataGenerator;
import com.example.familymapclient.model.FamilyPerson;
import com.example.familymapclient.model.LifeEvent;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final int LIFE_EVENT_ITEM_VIEW_TYPE = 0;
    private static final int FAMILY_PERSON_ITEM_VIEW_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SearchView searchView = findViewById(R.id.searchView);

        RecyclerView recyclerView = findViewById(R.id.RecyclerViewsSearchActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //I start to data generator and display it every time I change it
            @Override
            public boolean onQueryTextChange(String newText) {
                DataGenerator generator = new DataGenerator(newText);
                List<LifeEvent> lifeEvents = generator.getLifeEvents();
                List<FamilyPerson> familyPeople = generator.getFamilyPeople();

                UtahOutdoorsAdapter adapter = new UtahOutdoorsAdapter(lifeEvents, familyPeople);
                recyclerView.setAdapter(adapter);
                return true;
            }
        });


    }

    private class UtahOutdoorsAdapter extends RecyclerView.Adapter<UtahOutdoorsViewHolder> {
        private final List<LifeEvent> lifeEvents;
        private final List<FamilyPerson> familyPeople;

        UtahOutdoorsAdapter(List<LifeEvent> lifeEvents, List<FamilyPerson> familyPeople) {
            this.lifeEvents = lifeEvents;
            this.familyPeople = familyPeople;
        }

        @Override //Its a life event if its in the first part otherwise its a family person
        public int getItemViewType(int position) {
            return position < lifeEvents.size() ? LIFE_EVENT_ITEM_VIEW_TYPE : FAMILY_PERSON_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public UtahOutdoorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == LIFE_EVENT_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.life_event_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.family_person, parent, false);
            }

            return new UtahOutdoorsViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull UtahOutdoorsViewHolder holder, int position) {
            if (position < lifeEvents.size()) {
                holder.bind(lifeEvents.get(position));
            } else {
                holder.bind(familyPeople.get(position - lifeEvents.size()));
            }
        }

        @Override
        public int getItemCount() {
            return lifeEvents.size() + familyPeople.size();
        }
    }

    private class UtahOutdoorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView familyPersonName;
        private final TextView familyPersonRelationship;
        private final ImageView familyPersonImage;

        private final int viewType;
        private LifeEvent lifeEvent;
        private FamilyPerson familyPerson;

        UtahOutdoorsViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == LIFE_EVENT_ITEM_VIEW_TYPE) {
                familyPersonName = itemView.findViewById(R.id.lifeEventNameInfo);
                familyPersonRelationship = itemView.findViewById(R.id.lifeEventBirthInfo);
                familyPersonImage = itemView.findViewById(R.id.lifeEventImage);
                //difficulty = null;
            } else { //Family person
                familyPersonImage = itemView.findViewById(R.id.imageViewForFamilyPerson);
                familyPersonName = itemView.findViewById(R.id.personNameForFamilyPerson);
                familyPersonRelationship = itemView.findViewById(R.id.personRelationshipForFamilyPerson);

            }
        }

        private void bind(LifeEvent lifeEvent) {
            this.lifeEvent = lifeEvent;
            this.familyPersonName.setText(lifeEvent.getPersonName());
            this.familyPersonRelationship.setText(lifeEvent.getBirthInfo());
            this.familyPersonImage.setBackgroundResource(R.drawable.ic_event_marker);
        }

        private void bind(FamilyPerson familyPerson) {
            this.familyPerson = familyPerson;
            this.familyPersonName.setText(familyPerson.getName());
            this.familyPersonRelationship.setText(familyPerson.getGender());
            if(familyPerson.getGender().compareToIgnoreCase("M")==0){
                familyPersonImage.setBackgroundResource(R.drawable.ic_male);
            }
            else if(familyPerson.getGender().compareToIgnoreCase("F")==0){
                familyPersonImage.setBackgroundResource(R.drawable.ic_female);
            }
            else {
                familyPersonImage.setBackgroundResource(R.drawable.ic_person); //Not good
            }
        }

        @Override
        public void onClick(View view) {
            if (viewType == LIFE_EVENT_ITEM_VIEW_TYPE) {
                // This is were we could pass the skiResort to a ski resort detail activity
                Intent intent = new Intent(getApplicationContext(),EventActivity.class);
                DataCache.getInstance().eventClickedOn = this.lifeEvent.getEvent(); //Caching the event associated with the thing
                startActivity(intent); //Starting it
            }
            else {
                // This is were we could pass the hikingTrail to a hiking trail detail activity
                Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                DataCache.getInstance().personClickedOn = this.familyPerson.getPerson(); //Caching the event associated with the thing
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}