package com.example.votingapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.BuildConfig;
import com.example.votingapp.adapters.ElectionsAdapter;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.User;
import com.example.votingapp.R;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ElectionFragment extends Fragment {

    private static final String TAG = "ElectionFragment";
    List<Election> elections;
    AsyncHttpClient client;
    RecyclerView rvElections;
    ElectionsAdapter adapter;

    private String apiKey = BuildConfig.GOOGLE_API_KEY;

    public ElectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_election, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = new AsyncHttpClient();
        elections = new ArrayList<>();
        rvElections = view.findViewById(R.id.rvElections);
        adapter = new ElectionsAdapter(getContext(), elections);
        rvElections.setLayoutManager(new LinearLayoutManager(getContext()));
        rvElections.setAdapter(adapter);
        getElections();

//        getVoterQuery();
    }

    public void getElections() {
        RequestParams params = new RequestParams();
        Log.i(TAG, "Network call url: " + Election.ELECTION_URL + "?key=" + apiKey);
        client.get(Election.ELECTION_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                Log.i(TAG, "onSuccess to getElections");
                try {
                    JSONArray array = json.jsonObject.getJSONArray("elections");
                    Log.d(TAG, "onSuccess to getElections: " + array.toString());
//
//                    for (int i =0; i<array.length(); i++) {
//                        Election election = new Election(array.getJSONObject(i));
//                        getVoterQuery(election);
//                    }

                    array.remove(0);
                    elections.addAll(Election.fromJsonArray(array));
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to getElections, " + statusCode + ", " + response, throwable);
            }
        });
    }

    public void getVoterQuery(Election election) {
        RequestParams params = new RequestParams();
        String address = User.getAddress(ParseUser.getCurrentUser());
        Log.i(TAG, "current user: :  " + ParseUser.getCurrentUser());
        params.put("address", address);
        params.put("electionId", election.getId());
        Log.i(TAG, "Address:  " + address);
        Log.i(TAG, "Network call url: " + Election.VOTER_INFO_URL + "?key=" + apiKey);
        client.get(Election.VOTER_INFO_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                try {
                    JSONArray array = json.jsonObject.getJSONArray("elections");
                    Log.d(TAG, "onSuccess to getVoterQuery: " + array.toString());
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