package com.example.votingapp;

import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.activities.ElectionDetailsActivity;
import com.example.votingapp.fragments.ElectionFragment;
import com.example.votingapp.fragments.InfoFragment;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.User;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

// a class of static network methods to the Google Civic Information API
public class Network {
    public static final String ELECTION_URL = "https://www.googleapis.com/civicinfo/v2/elections";
    public static final String VOTER_INFO_URL = "https://www.googleapis.com/civicinfo/v2/voterinfo";
    private static final String TAG = "Network";

//    public static Integer primaryElectionId = 0;
//    public static List<Election> allElections = new ArrayList<>();
//    public static List<Election> usersElections = new ArrayList<>();

    public static AsyncHttpClient client = new AsyncHttpClient();
    public static String apiKey = BuildConfig.GOOGLE_API_KEY;

    // method to get the information from state on voting
    public static void getStateInfo(Integer electionId) {
        RequestParams params = new RequestParams();
        String address = User.getAddress(ParseUser.getCurrentUser());
        params.put("address", address);
        params.put("electionId", electionId);
        Log.i(TAG, "Address:  " + address);
        Log.i(TAG, "Network call url: " + Election.VOTER_INFO_URL + "?key=" + Network.apiKey);
        Network.client.get(Election.VOTER_INFO_URL + "?key=" + Network.apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    // retrieve the state object
                    JSONObject state = json.jsonObject.getJSONArray("state").getJSONObject(0);
                    InfoFragment.parseStateObject(state);

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

    // method to get the specific details of an election (i.e. the contests)
    public static void getContests(Election election) {
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
                    ElectionDetailsActivity.addContests(array);
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


    // method to get all of the elections from the API
    public static void getElections() {
        RequestParams params = new RequestParams();
        Log.i(TAG, "Network call url: " + ELECTION_URL + "?key=" + apiKey);
        client.get(ELECTION_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                Log.i(TAG, "onSuccess to getElections");
                try {
                    JSONArray array = json.jsonObject.getJSONArray("elections");
                    Log.d(TAG, "onSuccess to getElections: " + array.toString());
                    array.remove(0);

                    // go through and get individual elections that the user is in
                    for (int i =0; i<array.length(); i++) {
                        Election election = new Election(array.getJSONObject(i));
                        getVoterQuery(election);
                    }

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

    // method that checks if the user can vote in the given election
    public static void getVoterQuery(final Election election) {
        RequestParams params = new RequestParams();
        String address = User.getAddress(ParseUser.getCurrentUser());
        params.put("address", address);
        params.put("electionId", election.getId());
        Log.i(TAG, "Address:  " + address);
        Log.i(TAG, "Network call url: " + VOTER_INFO_URL + "?key=" + apiKey);
        client.get(VOTER_INFO_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                try {
                    JSONArray array = json.jsonObject.getJSONArray("contests");
                    if (array != null) {
                        ElectionFragment.addUserElection(election);
                    }

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
//
//    // GETTERS FOR OUR VARIABLES
//
//    public static Integer getPrimaryElectionId() {
//        return primaryElectionId;
//    }
//
//    public static List<Election> getAllElections() {
//        return allElections;
//    }
//
//    public static List<Election> getUsersElections() {
//        return usersElections;
//    }
}
