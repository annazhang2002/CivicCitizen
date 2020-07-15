package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.BuildConfig;
import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.adapters.ContestAdapter;
import com.example.votingapp.adapters.ElectionsAdapter;
import com.example.votingapp.models.Contest;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.User;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ElectionDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ElectionDetailsActivity";
    Election election;
    static List<Contest> contests;
    RecyclerView rvContests;
    static ContestAdapter adapter;

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

        tvElectionDay = findViewById(R.id.tvElectionDay);

        getSupportActionBar().setTitle(election.getName());
        tvElectionDay.setText(election.getSimpleElectionDay() + "");

        Network.getContests(election);
    }
//
//    public void getVoterQuery(Election election) {
//        RequestParams params = new RequestParams();
//        String address = User.getAddress(ParseUser.getCurrentUser());
//        params.put("address", address);
//        params.put("electionId", election.getId());
//        Log.i(TAG, "Address:  " + address);
//        Log.i(TAG, "Network call url: " + Election.VOTER_INFO_URL + "?key=" + apiKey);
//        Network.client.get(Election.VOTER_INFO_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                // Access a JSON array response with `json.jsonArray`
//                try {
//                    JSONArray array = json.jsonObject.getJSONArray("contests");
//                    addContests(array);
//                    Log.d(TAG, "onSuccess to getVoterQuery: " + contests.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.d(TAG, "onFailure to getVoterQuery, " + statusCode + ", " + response, throwable);
//            }
//        });
//    }

    public static void addContests(JSONArray array) {
        try {
            contests.addAll(Contest.fromJSON(array));
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
