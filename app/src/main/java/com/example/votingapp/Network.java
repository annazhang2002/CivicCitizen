package com.example.votingapp;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.activities.ElectionDetailsActivity;
import com.example.votingapp.fragments.ElectionFragment;
import com.example.votingapp.fragments.InfoFragment;
import com.example.votingapp.fragments.RepsFragment;
import com.example.votingapp.models.Action;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    public static final String REPS_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static final String TAG = "Network";
    public static final String[] ACTION_NAMES = {"Registered to Vote", "Absentee Ballot", "Voted"};

//    public static Integer primaryElectionId = 0;
//    public static List<Election> allElections = new ArrayList<>();
//    public static List<Election> usersElections = new ArrayList<>();

    public static AsyncHttpClient client = new AsyncHttpClient();
    public static String apiKey = BuildConfig.GOOGLE_API_KEY;
    public static String userState = "";

    // method to get the information from state on voting
    public static void getStateInfo(Integer electionId) {
        RequestParams params = new RequestParams();
        String address = User.getAddress(ParseUser.getCurrentUser());
        params.put("address", address);
        params.put("electionId", electionId);
        Log.i(TAG, "Address:  " + address);
        Log.i(TAG, "Network call url: " + VOTER_INFO_URL + "?key=" + apiKey);
        client.get(VOTER_INFO_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    // retrieve the state object
                    JSONObject state = json.jsonObject.getJSONArray("state").getJSONObject(0);
                    userState = state.getString("name").toLowerCase();
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
        Log.i(TAG, "Network call url: " + VOTER_INFO_URL + "?key=" + apiKey);
        client.get(VOTER_INFO_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
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

    public static void getReps() {
        RequestParams params = new RequestParams();
        String address = User.getAddress(ParseUser.getCurrentUser());
        params.put("address", address);
        Log.i(TAG, "Network call url: " + REPS_URL + "?key=" + apiKey);
        client.get(REPS_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                try {
                    JSONArray offices = json.jsonObject.getJSONArray("offices");
                    JSONArray people = json.jsonObject.getJSONArray("officials");
                    RepsFragment.parseNetworkRequest(offices, people);

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

    // query the action/checkboxes for a particular election
    public static void queryActions(Election election) {
        Log.i(TAG, "queryActions");
        ParseQuery<Action> query = ParseQuery.getQuery(Action.class);
        query.include(Action.KEY_USER);
        query.whereEqualTo(Action.KEY_ELECTION_ID, election.getId());
        query.whereEqualTo(Action.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Action>() {
            @Override
            public void done(List<Action> actions, ParseException e) {
                Log.i(TAG, "here");
                if (e != null) {
                    Log.e(TAG, "Issue with getting actions", e);
                }
                ElectionDetailsActivity.handleParseActions(actions);
            }
        });
    }

    public static void createElectionActions(Election election) {
        final List<Action> actions = new ArrayList<>();
        for (String name : ACTION_NAMES) {
            final Action action = new Action();
            action.setUser(ParseUser.getCurrentUser());
            action.setElectionId(election.getId());
            action.setName(name);
            action.setStatus("unfinished");
            action.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue saving the action" , e);
                        return;
                    }
                    actions.add(action);
                    Log.i(TAG, "Action was saved!!");
                }
            });
        }
        ElectionDetailsActivity.addActions(actions);
        Log.i(TAG, "Added all actions: " + actions);
    }

    public static void updateAction(Action action) {
        action.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue saving the user" , e);
                    return;
                }

                Log.i(TAG, "User changes were saved!!");
            }
        });
    }
}
