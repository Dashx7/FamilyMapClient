package com.example.familymapclient;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.cache.Settings;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Don't ask me about this
        setContentView(R.layout.settings_activity);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.settings, new SettingsFragment())
//                    .commit();
//        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //My switches
        Switch switchFamilyTreeLines = findViewById(R.id.switchFamilyTreeLines);
        Switch switchLifeStoryLines = findViewById(R.id.switchLifeStoryLines);
        Switch switchSpouseLines = findViewById(R.id.switchSpouseLines);
        Switch switchFathersSide = findViewById(R.id.switchFathersSide);
        Switch switchMothersSide = findViewById(R.id.switchMothersSide);
        Switch switchFemaleEvents = findViewById(R.id.switchFemaleEvents);
        Switch switchMaleEvents = findViewById(R.id.switchMaleEvents);

        //Set the switches to equal current settings
        switchFamilyTreeLines.setChecked(Settings.isFamilyTreeLines);
        switchLifeStoryLines.setChecked(Settings.isLifeStoryLines);
        switchSpouseLines.setChecked(Settings.isSpouseLines);
        switchFathersSide.setChecked(Settings.isFilterByDadsSide);
        switchMothersSide.setChecked(Settings.isFilterByMomsSide);
        switchFemaleEvents.setChecked(Settings.isFilterOutFemale);
        switchMaleEvents.setChecked(Settings.isFilterOutMale);

        switchFamilyTreeLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Updates all my settings here when any is clicked
                Settings.isFamilyTreeLines = switchFamilyTreeLines.isChecked();
                Settings.isSpouseLines = switchSpouseLines.isChecked();
                Settings.isLifeStoryLines = switchLifeStoryLines.isChecked();
                Settings.isFilterByDadsSide = switchFathersSide.isChecked();
                Settings.isFilterByMomsSide = switchMothersSide.isChecked();
                Settings.isFilterOutMale = switchMaleEvents.isChecked();
                Settings.isFilterOutFemale = switchFemaleEvents.isChecked();
            }
        });

    }

//    public static class SettingsFragment extends PreferenceFragmentCompat {
//        @Override
//        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey);
//        }
//    }
}