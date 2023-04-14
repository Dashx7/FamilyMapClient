package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapsFragment();

        //Embed the fragment
        fragmentManager.beginTransaction().add(R.id.fragmentFrameLayoutEventActivity,fragment).commit();
        //setHasOptionsMenu(true);
    }
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