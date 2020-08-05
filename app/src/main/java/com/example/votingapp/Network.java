package com.example.votingapp;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.adapters.ElectionsAdapter;
import com.example.votingapp.fragments.ElectionDetailsFragment;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.fragments.ElectionFragment;
import com.example.votingapp.fragments.FriendFragment;
import com.example.votingapp.fragments.FriendRequestFragment;
import com.example.votingapp.fragments.FriendUserFragment;
import com.example.votingapp.fragments.InfoFragment;
import com.example.votingapp.fragments.LocationsFragment;
import com.example.votingapp.fragments.ProfileFragment;
import com.example.votingapp.fragments.RepsFragment;
import com.example.votingapp.models.Action;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.Location;
import com.example.votingapp.models.User;
import com.parse.FindCallback;
import com.parse.Parse;
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
    public static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    public static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String TAG = "Network";
    public static final String[] ACTION_NAMES = {"registered to vote", "sent in absentee ballot", "voted"};

//    public static Integer primaryElectionId = 0;
//    public static List<Election> allElections = new ArrayList<>();
//    public static List<Election> usersElections = new ArrayList<>();

    public static AsyncHttpClient client = new AsyncHttpClient();
    public static String apiKey = BuildConfig.GOOGLE_API_KEY;
    public static final String mapsApiKey = BuildConfig.GOOGLE_MAPS_API_KEY;
    public static final String distanceMatrixApiKey = BuildConfig.DISTANCE_MATRIX_API_KEY;
    public static String userState = "";

    // method to get the coordinates from address
    public static void getCoordinates(String address, final Location location) {
        RequestParams params = new RequestParams();
        params.put("address", address);
        params.put("key", mapsApiKey);
        Log.i(TAG, "Address:  " + address);
        Log.i(TAG, "Network call url: " + GEOCODE_URL);
        client.get(GEOCODE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "successfully got the coordinates for location: " + location.getName());
                try {
                    JSONObject loc = json.jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    double lat = loc.getDouble("lat");
                    double lng = loc.getDouble("lng");
                    location.setLatLng(lat, lng);
                    LocationsFragment.addLatLng(lat, lng, location);
                    List<Location> locations = new ArrayList<>();
                    locations.add(location);
                    getDistancesFrom(User.getAddress(ParseUser.getCurrentUser()), locations);
                    Log.i(TAG, "location: " + json);
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
    public static void getElectionDetails(Election election) {
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
                    JSONArray contests = json.jsonObject.getJSONArray("contests");
                    ElectionDetailsFragment.addContests(contests);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray pollingLocations = json.jsonObject.getJSONArray("pollingLocations");
                    ElectionDetailsFragment.addLocations(pollingLocations, "Polling Location");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray earlyVoteSites = json.jsonObject.getJSONArray("earlyVoteSites");
                    ElectionDetailsFragment.addLocations(earlyVoteSites, "Early Voting Site");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray dropOffLocations = json.jsonObject.getJSONArray("dropOffLocations");
                    ElectionDetailsFragment.addLocations(dropOffLocations, "Drop Off Location");
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
                    MainActivity.hidePd();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to getElections, " + statusCode + ", " + response, throwable);
                MainActivity.hidePd();
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
        Log.i(TAG, "ElectionId" + election.getId());
        Log.i(TAG, "Network call url: " + VOTER_INFO_URL + "?key=" + apiKey);
        client.get(VOTER_INFO_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                boolean added = false;
                try {
                    JSONArray array = json.jsonObject.getJSONArray("contests");
                    if (array != null) {
                        ElectionFragment.addUserElection(election);
                        added = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!added) {
                    try {
                        JSONArray array = json.jsonObject.getJSONArray("earlyVoteSites");
                        if (array != null) {
                            ElectionFragment.addUserElection(election);
                            added = true;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    public static void queryActions(final Election election) {
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
                ElectionsAdapter.handleParseActions(actions, election);
            }
        });
    }

    // for new elections, method will create the three deadlines/checkboxes/actions that are unfinished
    public static void createElectionActions(Election election) {
        final List<Action> actions = new ArrayList<>();
        for (String name : ACTION_NAMES) {
            Log.i(TAG, "action names: " + name);
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
        ElectionsAdapter.addActions(actions, election);
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

    public static void queryUserActions(ParseUser user) {
        Log.i(TAG, "queryUserActions");
        ParseQuery<Action> query = ParseQuery.getQuery(Action.class);
        query.include(Action.KEY_USER);
        query.whereEqualTo(Action.KEY_STATUS, "done");
        query.whereEqualTo(Action.KEY_USER, user);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<Action>() {
            @Override
            public void done(List<Action> actions, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting actions", e);
                }
                if (actions != null) {
                    ProfileFragment.handleParseActions(actions);
                }
            }
        });
    }

    public static void queryFriendActions(ParseUser currentUser) {
        Log.i(TAG, "queryFriendActions");

        final List<String> friendIds = new ArrayList<>();
        friendIds.addAll(User.getFriends(currentUser));

        ParseQuery<Action> query = ParseQuery.getQuery(Action.class);
        query.include(Action.KEY_USER);
        query.whereEqualTo(Action.KEY_STATUS, "done");
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<Action>() {
            @Override
            public void done(List<Action> actions, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting actions", e);
                }
                if (actions != null) {
                    for (int i = 0; i < actions.size(); i++) {
                        if (friendIds.contains(actions.get(i).getUser().getUsername())) {
                            FriendFragment.addFriendAction(actions.get(i));
                        }
                    }
                }
            }
        });
    }

    public static void searchFriends(final ParseUser currentUser, final String friendUsername) {
        Log.i(TAG, "searchFriends");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContains("username", friendUsername);
        query.whereNotEqualTo("username", currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting usernames", e);
                }
                if (users != null && !users.isEmpty()) {
                    FriendFragment.showSearchResults(users);
                }
            }
        });
    }

    public static void findFriend(final ParseUser currentUser, final String friendUsername) {
        Log.i(TAG, "findFriend");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("username", friendUsername);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting usernames", e);
                }
                if (users != null && !users.isEmpty()) {
                    addFriend(currentUser, users.get(0));
                } else {
                    FriendFragment.noFriendAlert(friendUsername);
                }
            }
        });
    }

    public static void addFriend(ParseUser currentUser, final ParseUser friend) {
        Log.i(TAG, "queryFriendActions");

        final List<String> friendIds = new ArrayList<>();
        friendIds.addAll(User.getFriends(currentUser));
        friendIds.add(friend.getUsername());
        User.setFriends(currentUser, friendIds);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                MainActivity.goUserProfile(friend);
            }
        });
    }

    public static void getDistancesFrom(String origin, final List<Location> locations) {
        RequestParams params = new RequestParams();
        String destinationString = "";
        for (Location location : locations) {
            destinationString += location.getAddress() + "|";
        }
        destinationString.substring(0,destinationString.length()-1);

        String callString = String.format("%sunits=imperial&origins=%s&destinations=%s&key=%s", DISTANCE_MATRIX_URL, origin, destinationString, distanceMatrixApiKey);
        Log.i(TAG, "Network call: " + callString);

        client.get(callString, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.i(TAG, "got the distances");
                    JSONArray elements = json.jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
                    for (int i = 0; i< elements.length(); i++) {
                        Long distance = elements.getJSONObject(i).getJSONObject("distance").getLong("value");
                        locations.get(i).setDistanceHome(distance);
                    }
                    LocationsFragment.sortLocations();
                } catch (JSONException e) {
                    Log.i(TAG, "failure to get the distances");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to getDistancesFrom, " + statusCode + ", " + response, throwable);
            }
        });
    }

    public static void queryUserFriends(ParseUser currentUser) {
        Log.i(TAG, "queryUserFriends");
        List<String> friendUsernames = User.getFriends(currentUser);
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContainedIn("username", friendUsernames);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting usernames", e);
                }
                if (users != null && !users.isEmpty()) {
                    FriendFragment.addUserFriends(users);
                }
            }
        });
    }

    public static void queryFriendRequests(ParseUser currentUser) {
        Log.i(TAG, "queryFriendRequests");
        List<String> friendUsernames = User.getFriends(currentUser);
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContains("friends", currentUser.getUsername());
        query.whereNotContainedIn("username", friendUsernames);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting usernames", e);
                }
                if (users != null && !users.isEmpty()) {
                    FriendFragment.addRequests(users);
                }
            }
        });
    }
}
