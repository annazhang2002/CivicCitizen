package com.example.votingapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.ConstraintTableLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.BuildConfig;
import com.example.votingapp.R;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.User;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;


public class InfoFragment extends Fragment {
    private static final String TAG = "InfoFragment";
    private String apiKey = BuildConfig.GOOGLE_API_KEY;
    AsyncHttpClient client;

    // variables with information about the state urls
    Integer electionId;
    String electionInfoUrl;
    String stateName;

    TextView tvName;
    TextView tvElectionInfoUrl;
    TextView tvElectionRegistrationUrl;
    TextView tvElectionRegistrationConfirmationUrl;
    TextView tvAbsenteeVotingUrl;
    TextView tvLocationFinderUrl;
    TextView tvBallotInfoUrl;
    ConstraintLayout cl1;
    ConstraintLayout cl2;
    ConstraintLayout cl3;
    ConstraintLayout cl4;
    ConstraintLayout cl5;
    ConstraintLayout cl6;


    public InfoFragment() {
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
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = new AsyncHttpClient();

        tvName = view.findViewById(R.id.tvName);
        tvElectionInfoUrl = view.findViewById(R.id.tvElectionInfoUrl);
        tvElectionRegistrationUrl = view.findViewById(R.id.tvElectionRegistrationUrl);
        tvElectionRegistrationConfirmationUrl = view.findViewById(R.id.tvElectionRegistrationConfirmationUrl);
        tvAbsenteeVotingUrl = view.findViewById(R.id.tvAbsenteeVotingUrl);
        tvLocationFinderUrl = view.findViewById(R.id.tvLocationFinderUrl);
        tvBallotInfoUrl = view.findViewById(R.id.tvBallotInfoUrl);
        cl1 = view.findViewById(R.id.cl1);
        cl2 = view.findViewById(R.id.cl12);
        cl3 = view.findViewById(R.id.cl3);
        cl4 = view.findViewById(R.id.cl4);
        cl5 = view.findViewById(R.id.cl5);
        cl6 = view.findViewById(R.id.cl6);

        getElectionId();
    }

    // method to get the election id of the first election (just for the voterquery request)
    public void getElectionId() {
        RequestParams params = new RequestParams();
        Log.i(TAG, "Network call url: " + Election.ELECTION_URL + "?key=" + apiKey);
        client.get(Election.ELECTION_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                Log.i(TAG, "onSuccess to getElectionId");
                try {
                    JSONArray array = json.jsonObject.getJSONArray("elections");
                    Log.d(TAG, "onSuccess to getElectionId: " + array.toString());
                    electionId = array.getJSONObject(0).getInt("id");
                    Log.d(TAG, "onSuccess to getElectionId: " + electionId);
                    getStateInfo(electionId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to getElectionId, " + statusCode + ", " + response, throwable);
            }
        });
    }

    // method to get the information from state on voting
    public void getStateInfo(Integer electionId) {
        RequestParams params = new RequestParams();
        String address = User.getAddress(ParseUser.getCurrentUser());
        params.put("address", address);
        params.put("electionId", electionId);
        Log.i(TAG, "Address:  " + address);
        Log.i(TAG, "Network call url: " + Election.VOTER_INFO_URL + "?key=" + apiKey);
        client.get(Election.VOTER_INFO_URL + "?key=" + apiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                JSONObject stateurls = null;
                try {
                    // retrieve the state object
                    JSONObject state = json.jsonObject.getJSONArray("state").getJSONObject(0);
                    stateName = state.getString("name");
                    stateurls = state.getJSONObject("electionAdministrationBody");
                    Log.i(TAG, "electionInfoUrl: " + electionInfoUrl);

                    // set the set of variables in the layout
                    tvName.setText(stateName + " Voting Information");
                    String electionInfoUrl = stateurls.getString("electionInfoUrl");
                    if (electionInfoUrl != null) {
                        tvElectionInfoUrl.setText(electionInfoUrl);
                    } else {
                        cl1.setVisibility(View.GONE);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // try catches for all of the urls
                try {
                    String electionRegistrationUrl = stateurls.getString("electionRegistrationUrl");
                    tvElectionRegistrationUrl.setText(electionRegistrationUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    cl2.setVisibility(View.GONE);
                }
                try {
                    String electionRegistrationConfirmationUrl = stateurls.getString("electionRegistrationConfirmationUrl");
                    tvElectionRegistrationConfirmationUrl.setText(electionRegistrationConfirmationUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    cl3.setVisibility(View.GONE);
                }
                try {
                    String absenteeVotingInfoUrl = stateurls.getString("absenteeVotingInfoUrl");
                    tvAbsenteeVotingUrl.setText(absenteeVotingInfoUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    cl4.setVisibility(View.GONE);
                }
                try {
                    String locationFinderUrl = stateurls.getString("votingLocationFinderUrl");
                    tvLocationFinderUrl.setText(locationFinderUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    cl5.setVisibility(View.GONE);
                }
                try {
                    String ballotInfoUrl = stateurls.getString("ballotInfoUrl");
                    tvBallotInfoUrl.setText(ballotInfoUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    cl5.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to getVoterQuery, " + statusCode + ", " + response, throwable);
            }
        });
    }
}