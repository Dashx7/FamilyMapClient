package com.example.familymapclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familymapclient.cache.Settings;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        //Back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Creating all of my switches
        Switch switchFamilyTreeLines = findViewById(R.id.switchFamilyTreeLines);
        Switch switchLifeStoryLines = findViewById(R.id.switchLifeStoryLines);
        Switch switchSpouseLines = findViewById(R.id.switchSpouseLines);
        Switch switchFathersSide = findViewById(R.id.switchFathersSide);
        Switch switchMothersSide = findViewById(R.id.switchMothersSide);
        Switch switchFemaleEvents = findViewById(R.id.switchFemaleEvents);
        Switch switchMaleEvents = findViewById(R.id.switchMaleEvents);

        LinearLayout logout = findViewById(R.id.logOutLayoutInSettings);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Logout button
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        //Set the switches to equal current settings
        switchFamilyTreeLines.setChecked(Settings.isFamilyTreeLines);
        switchLifeStoryLines.setChecked(Settings.isLifeStoryLines);
        switchSpouseLines.setChecked(Settings.isSpouseLines);
        switchFathersSide.setChecked(Settings.isFilterByDadsSide);
        switchMothersSide.setChecked(Settings.isFilterByMomsSide);
        switchFemaleEvents.setChecked(Settings.isFilterFemale);
        switchMaleEvents.setChecked(Settings.isFilterMale);

        //My listener that just puts all the settings in place
        CompoundButton.OnCheckedChangeListener myListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.isFamilyTreeLines = switchFamilyTreeLines.isChecked();
                Settings.isSpouseLines = switchSpouseLines.isChecked();
                Settings.isLifeStoryLines = switchLifeStoryLines.isChecked();
                Settings.isFilterByDadsSide = switchFathersSide.isChecked();
                Settings.isFilterByMomsSide = switchMothersSide.isChecked();
                Settings.isFilterMale = switchMaleEvents.isChecked();
                Settings.isFilterFemale = switchFemaleEvents.isChecked();

                if(!Settings.isFilterMale || !Settings.isFilterFemale){ //If you don't show one gender they you can never see a spouse
                    switchSpouseLines.setChecked(false);
                    Settings.isSpouseLines = false;
                }
            }
        };

        //Putting that listener on all of my switches
        switchFamilyTreeLines.setOnCheckedChangeListener(myListener);
        switchLifeStoryLines.setOnCheckedChangeListener(myListener);
        switchSpouseLines.setOnCheckedChangeListener(myListener);
        switchFathersSide.setOnCheckedChangeListener(myListener);
        switchMothersSide.setOnCheckedChangeListener(myListener);
        switchFemaleEvents.setOnCheckedChangeListener(myListener);
        switchMaleEvents.setOnCheckedChangeListener(myListener);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

//    public static class SettingsFragment extends PreferenceFragmentCompat {
//        @Override
//        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey);
//        }
//    }
}