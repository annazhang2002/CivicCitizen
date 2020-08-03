package com.example.votingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.votingapp.R;
import com.example.votingapp.fragments.ElectionDetailsFragment;
import com.example.votingapp.fragments.ElectionFragment;
import com.example.votingapp.fragments.FriendFragment;
import com.example.votingapp.fragments.InfoFragment;
import com.example.votingapp.fragments.ProfileFragment;
import com.example.votingapp.fragments.RepDetailsFragment;
import com.example.votingapp.fragments.RepsFragment;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.Rep;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private static final long FADE_DEFAULT_TIME = 2;
    private static final long MOVE_DEFAULT_TIME = 2;
    static BottomNavigationView bottomNavigationView;
    public static FragmentManager fragmentManager;
    public static PackageManager packageManager;
    static ProgressDialog pd;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        createProgressDialog();
        pd.show();
        fragmentManager = getSupportFragmentManager();
        packageManager = getPackageManager();

        // allowing the user to navigate with bottom tabs
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = new Fragment();
                menuItem.setEnabled(true);
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new ElectionFragment();
                        break;
                    case R.id.action_representatives:
                        fragment = new RepsFragment(getPackageManager());
                        break;
                    case R.id.action_faqs:
                        fragment = new InfoFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_friends:
                        fragment = new FriendFragment();
                        break;
                    default:
                        Log.i(TAG, "Error with the bottom navigation tabs");
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        showStartFragment();
    }

    private void showStartFragment() {
        String intentFragment = getIntent().getStringExtra("frgToLoad");

        switch (intentFragment){
            case "home":
                bottomNavigationView.setSelectedItemId(R.id.action_home);
                break;
            case "profile":
                bottomNavigationView.setSelectedItemId(R.id.action_profile);
                pd.hide();
                break;
            case "reps":
                bottomNavigationView.setSelectedItemId(R.id.action_representatives);
                pd.hide();
                break;
            case "faqs":
                bottomNavigationView.setSelectedItemId(R.id.action_faqs);
                pd.hide();
                break;
        }
    }

    public static void goInfo() {
        Fragment fragment = new InfoFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        bottomNavigationView.setSelectedItemId(R.id.action_faqs);
    }

    public static void goRepDetails(Rep rep, Integer pos) {
        Fragment fragment = RepDetailsFragment.newInstance(context, rep, packageManager, pos);
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null).commit();
    }

    public static void backToReps(Integer position) {
        Fragment fragment = new RepsFragment(packageManager, position);
        Log.i(TAG, "rep position: " + position);
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null).commit();
    }

    public static void goElectionDetails(Election election) {
        Fragment fragment = ElectionDetailsFragment.newInstance(context, election);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    public static void goUserProfile(ParseUser user) {
        Fragment fragment = new ProfileFragment(user);
        if (user == ParseUser.getCurrentUser()) {
            bottomNavigationView.setSelectedItemId(R.id.action_profile);
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        } else {
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        }
    }

    public static void goFriends(String startFragment) {
        Fragment fragment = new FriendFragment(true, startFragment);
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        bottomNavigationView.setSelectedItemId(R.id.action_friends);
    }

    public void createProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.getWindow().setBackgroundDrawableResource(R.drawable.election_card);
        pd.setCancelable(false);
    }

    public static void hidePd() {
        pd.hide();
    }
    public static void showPd() {
        pd.show();
    }
}