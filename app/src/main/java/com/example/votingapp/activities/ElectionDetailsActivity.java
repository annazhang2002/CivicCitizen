package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
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

import static com.example.votingapp.MethodLibrary.openUrl;

public class ElectionDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ElectionDetailsActivity";
    static Election election;
    static ProgressDialog pd;
    static List<Contest> contests;
    static List<Location> locations;
    static List<Action> actions;
    RecyclerView rvContests;
    static ContestAdapter contestAdapter;
    RecyclerView rvLocations;
    static LocationAdapter locationAdapter;
    public static CheckBox[] cbDeadlines;
    TextView tvRegisterDeadline;
    TextView tvAbsenteeDeadline;

    TextView tvElectionDay;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_details);

        context = this;
        election = Parcels.unwrap(getIntent().getParcelableExtra(Election.class.getSimpleName()));
        contests = new ArrayList<>();
        rvContests = findViewById(R.id.rvContests);
        contestAdapter = new ContestAdapter(this, contests);
        rvContests.setLayoutManager(new LinearLayoutManager(this));
        rvContests.setAdapter(contestAdapter);
        locations = new ArrayList<>();
        rvLocations = findViewById(R.id.rvLocations);
        locationAdapter = new LocationAdapter(this, locations);
        rvLocations.setLayoutManager(new LinearLayoutManager(this));
        rvLocations.setAdapter(locationAdapter);

        createProgressDialog();
        pd.show();
        actions = new ArrayList<>();
        cbDeadlines = new CheckBox[3];
        cbDeadlines[0] = findViewById(R.id.cbRegisterVote);
        cbDeadlines[1] = findViewById(R.id.cbAbsentee);
        cbDeadlines[2] = findViewById(R.id.cbVote);
        tvElectionDay = findViewById(R.id.tvElectionDay);
        tvRegisterDeadline = findViewById(R.id.tvRegisterDeadline);
        tvAbsenteeDeadline = findViewById(R.id.tvAbsenteeDeadline);

        getSupportActionBar().setTitle(election.getName());
        tvElectionDay.setText(election.getSimpleElectionDay() + "");
        cbDeadlines[0].setText("Register to Vote (" + election.getRegisterDeadline() + ")");
        if (!cbDeadlines[0].isChecked()) {
            long miliSecsDate = milliseconds(election.getRegisterDeadline());
            scheduleNotification(miliSecsDate);
        }
        cbDeadlines[1].setText("Send in Absentee Ballot Application (" + election.getAbsenteeDeadline() + ")");
        cbDeadlines[2].setText("Vote!! (" + election.getVoteDeadline() + ")");

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
            cbDeadlines[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
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

    public static void addContests(JSONArray array) {
        try {
            contests.addAll(Contest.fromJSON(array));
            contestAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void addLocations(JSONArray pollingLocations, String type) {
        try {
            locations.addAll(Location.fromJSON(pollingLocations, type));
            locationAdapter.notifyDataSetChanged();
            pd.hide();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onCheckDeadline(Integer cbIndex) {
        if (cbDeadlines[cbIndex].isChecked()) {
            Log.i(TAG, "button checked");
            actions.get(cbIndex).setStatus("done");
        } else {
            Log.i(TAG, "button unchecked");
            actions.get(cbIndex).setStatus("unfinished");
        }
        Network.updateAction(actions.get(cbIndex));
    }
    public static void addActions(List<Action> newActions) {
        actions.addAll(newActions);
    }

    public static void handleParseActions(List<Action> actions) {
        Log.i(TAG, "handleParseActions: "  + actions);

        // if there are no actions for this election + user yet, then make them!
        if (actions.size() == 0) {
            Network.createElectionActions(election);
        } else {
            addActions(actions);
            for (Action action : actions) {
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

    public void createProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setCancelable(false);
    }
}
