package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.drawable.AdaptiveIconDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.familymapclient.ui.login.LoginFragment;

//TODO
//On marker click how do I display the event


//Unknown fragment in the mapfragment xml
//Messing up my design tab
//Make the linear layout clickable? Just the text?

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            //Do stuff with the old save state
            //Not figuring that out rn
        }

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);

        if(fragment==null){
            fragment = createLoginFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentFrameLayout,fragment).commit();
        }
        else{
            //If the fragment is not null, the Main activity was destroyed and recreated
            //So we need to reset the listener to the new instance of the fragment
            if(fragment instanceof LoginFragment){
                ((LoginFragment) fragment).registerListener(this);
            }

        }

    }

    private Fragment createLoginFragment(){
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putInt()
    }

    @Override
    public void notifyDone() {
        //Creating an instance of the second fragment
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapsFragment();

        //Swap the screen with the map fragment previously made
        fragmentManager.beginTransaction().replace(R.id.fragmentFrameLayout,fragment).commit();
        //setContentView();
    }
    @Override
    public void notifyLoginIsDone(String message) {

    }
    @Override
    public void notifyLoginFailed(String message) {

    }
    @Override
    public void notifyRegisterIsDone(String message) {

    }
    @Override
    public void notifyRegisterFailed(String message) {

    }

}