package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.votingapp.adapters.ContestAdapter;
import com.example.votingapp.adapters.ElectionsAdapter;
import com.example.votingapp.models.Action;
import com.example.votingapp.models.Contest;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ElectionDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ElectionDetailsActivity";
    static Election election;
    static List<Contest> contests;
    static List<Action> actions;
    RecyclerView rvContests;
    static ContestAdapter adapter;
    public static CheckBox[] cbDeadlines;
    TextView tvRegisterDeadline;
    TextView tvAbsenteeDeadline;

    TextView tvElectionDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_details);

        election = Parcels.unwrap(getIntent().getParcelableExtra(Election.class.getSimpleName()));
        contests = new ArrayList<>();
        rvContests = findViewById(R.id.rvContests);
        adapter = new ContestAdapter(this, contests);
        rvContests.setLayoutManager(new LinearLayoutManager(this));
        rvContests.setAdapter(adapter);

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
        cbDeadlines[1].setText("Send in Absentee Ballot Application (" + election.getAbsenteeDeadline() + ")");
        cbDeadlines[2].setText("Vote!! (" + election.getVoteDeadline() + ")");

        tvRegisterDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl("https://www.vote.org/voter-registration-deadlines/#" + Network.userState);
            }
        });
        tvAbsenteeDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl("https://www.vote.org/absentee-ballot-deadlines/#" + Network.userState);
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

        Network.getContests(election);
        Network.queryActions(election);
    }

    public static void addContests(JSONArray array) {
        try {
            contests.addAll(Contest.fromJSON(array));
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
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
}
