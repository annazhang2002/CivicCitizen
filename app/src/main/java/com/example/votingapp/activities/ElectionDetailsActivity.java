package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.BuildConfig;
import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.ReminderBroadcast;
import com.example.votingapp.adapters.ContestAdapter;
import com.example.votingapp.adapters.ElectionsAdapter;
import com.example.votingapp.adapters.LocationAdapter;
import com.example.votingapp.fragments.ActionCompleteFragment;
import com.example.votingapp.fragments.ContestsFragment;
import com.example.votingapp.fragments.LocationsFragment;
import com.example.votingapp.models.Action;
import com.example.votingapp.models.Contest;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.Location;
import com.example.votingapp.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;

import static com.example.votingapp.MethodLibrary.API_DATE_FORMAT;
import static com.example.votingapp.MethodLibrary.openUrl;

public class ElectionDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ElectionDetailsActivity";
    public static FragmentManager fragmentManager;
    static Election election;
    static ProgressDialog pd;
    static List<Action> actions;
    static List<Location> locations;
    static List<Contest> contests;
    public static CheckBox[] cbDeadlines;
    TextView tvRegisterDeadline;
    TextView tvAbsenteeDeadline;
    TextView tvLocations;
    TextView tvContests;

    TextView tvElectionDay;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_details);

        context = this;
        fragmentManager = getSupportFragmentManager();
        election = Parcels.unwrap(getIntent().getParcelableExtra(Election.class.getSimpleName()));

        createProgressDialog();
        pd.show();
        actions = new ArrayList<>();
        locations = new ArrayList<>();
        contests = new ArrayList<>();
        cbDeadlines = new CheckBox[3];
        cbDeadlines[0] = findViewById(R.id.cbRegisterVote);
        cbDeadlines[1] = findViewById(R.id.cbAbsentee);
        cbDeadlines[2] = findViewById(R.id.cbVote);
        tvElectionDay = findViewById(R.id.tvElectionDay);
        tvRegisterDeadline = findViewById(R.id.tvRegisterDeadline);
        tvAbsenteeDeadline = findViewById(R.id.tvAbsenteeDeadline);
        tvContests = findViewById(R.id.tvContests);
        tvLocations = findViewById(R.id.tvLocations);
        tvLocations.setBackgroundColor(getResources().getColor(R.color.white));
        tvContests.setBackgroundColor(getResources().getColor(R.color.inactive_tab));

        getSupportActionBar().setTitle(election.getName());
        tvElectionDay.setText(election.getSimpleElectionDay() + "");
        cbDeadlines[0].setText("Register to Vote (" + election.getRegisterDeadline() + ")");
        cbDeadlines[1].setText("Send in Absentee Ballot Application (" + election.getAbsenteeDeadline() + ")");
        cbDeadlines[2].setText("Vote!! (" + election.getVoteDeadline() + ")");

        // set click listeners for locations and contest toggle
        tvLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.flContainer, LocationsFragment.newInstance(context, election, locations)).commit();
                tvLocations.setBackgroundColor(getResources().getColor(R.color.white));
                tvContests.setBackgroundColor(getResources().getColor(R.color.inactive_tab));
            }
        });
        tvContests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.flContainer, ContestsFragment.newInstance(context, election, contests)).commit();
                tvLocations.setBackgroundColor(getResources().getColor(R.color.inactive_tab));
                tvContests.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        // set notification if not voted yet
        if (!cbDeadlines[2].isChecked()) {
            long miliSecsDate = milliseconds("2020-07-21");

//            long miliSecsDate = milliseconds(election.getElectionReminderDate());
            scheduleNotification(miliSecsDate);
        }

        tvRegisterDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl("https://www.vote.org/voter-registration-deadlines/#" + Network.userState, context);
            }
        });
        tvAbsenteeDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl("https://www.vote.org/absentee-ballot-deadlines/#" + Network.userState, context);
            }
        });
        // set onchecklisteners for checkboxes
        for (int i = 0 ; i<cbDeadlines.length; i++) {
            final int finalI = i;
            cbDeadlines[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckDeadline(finalI);
                }
            });
        }

        Network.getElectionDetails(election);
        Network.queryActions(election);
    }

    public void scheduleNotification(long millis) {
        Log.i(TAG, "notification scheduled");
        Intent intent = new Intent(this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                millis,
                pendingIntent);
    }

    public static long milliseconds(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT);
        try
        {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            Log.i(TAG, "Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }



    public void onCheckDeadline(Integer cbIndex) {
        if (cbDeadlines[cbIndex].isChecked()) {
            Log.i(TAG, "button checked");
            actions.get(cbIndex).setStatus("done");
            openCongratsFragment(actions.get(cbIndex));
        } else {
            Log.i(TAG, "button unchecked");
            actions.get(cbIndex).setStatus("unfinished");
        }
        Network.updateAction(actions.get(cbIndex));
    }

    public void openCongratsFragment(Action action) {
        FragmentManager fm = getSupportFragmentManager();
        ActionCompleteFragment actionCompleteFragment = ActionCompleteFragment.newInstance(this, action);
        actionCompleteFragment.show(fm, "fragment_compose");
    }

    public static void addActions(List<Action> newActions) {
        actions.addAll(newActions);
        pd.hide();
    }

    public static void handleParseActions(List<Action> actions) {
        Log.i(TAG, "handleParseActions: "  + actions);

        // if there are no actions for this election + user yet, then make them!
        if (actions.size() == 0) {
            Network.createElectionActions(election);
        } else {
            addActions(actions);
            for (Action action : actions) {
                Log.i(TAG, "here: Action: " + action.getName());
                // check if each action is completed or not
                for (int i = 0 ; i< Network.ACTION_NAMES.length; i++) {
                    if (action.getName().equals(Network.ACTION_NAMES[i])) {
                        if (action.getStatus().equals("done")) {
                            cbDeadlines[i].setChecked(true);
                        } else {
                            cbDeadlines[i].setChecked(false);
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void addLocations(JSONArray pollingLocations, String type) {
        try {
            locations.addAll(Location.fromJSON(pollingLocations, type));
            Log.i(TAG, String.valueOf(locations));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, LocationsFragment.newInstance(context, election, locations)).commit();
    }

    public static void addContests(JSONArray array) {
        try {
            contests.addAll(Contest.fromJSON(array));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, ContestsFragment.newInstance(context, election, contests)).commit();
    }

    public void createProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setCancelable(false);
    }
}
