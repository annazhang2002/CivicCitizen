package com.example.votingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.votingapp.R;
import com.example.votingapp.fragments.ElectionFragment;
import com.example.votingapp.fragments.InfoFragment;
import com.example.votingapp.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        // allowing the user to navigate with bottom tabs
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = new Fragment();
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new ElectionFragment();
                        break;
                    case R.id.action_representatives:
//                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_faqs:
                        fragment = new InfoFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    default:
                        Log.i(TAG, "Error with the bottom navigation tabs");
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    public static void goInfo() {
        Fragment fragment = new InfoFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    public static void goUserProfile(ParseUser user) {
        Fragment fragment = new ProfileFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }
}