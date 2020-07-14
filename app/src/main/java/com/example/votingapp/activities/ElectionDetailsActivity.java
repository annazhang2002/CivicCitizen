package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.BuildConfig;
import com.example.votingapp.R;
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
    private String apiKey = BuildConfig.GOOGLE_API_KEY;
    AsyncHttpClient client;
    Election election;
    List<Contest> contests;

    TextView tvElectionDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_details);

        election = Parcels.unwrap(getIntent().getParcelableExtra(Election.class.getSimpleName()));
        client = new AsyncHttpClient();
        contests = new ArrayList<>();
        getSupportActionBar().setTitle(election.getName());

        tvElectionDay = findViewById(R.id.tvElectionDay);
        tvElectionDay.setText(election.getElectionDay());

        getVoterQuery(election);
    }

    public void getVoterQuery(Election election) {
        RequestParams params = new RequestParams();
        String address = User.getAddress(ParseUser.getCurrentUser());
        params.put("address", address);
        params.put("electionId", election.getId());
        Log.i(TAG, "Address:  " + address);
        Log.i(TAG, "Network call url: " + Election.VOTER_INFO_URL + "?key=" + apiKey);
        client.get(Election.VOTER_INFO_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                try {
                    JSONArray array = json.jsonObject.getJSONArray("contests");
                    contests.addAll(Contest.fromJSON(array));
                    Log.d(TAG, "onSuccess to getVoterQuery: " + contests.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to getVoterQuery, " + statusCode + ", " + response, throwable);
            }
        });
    }
}
