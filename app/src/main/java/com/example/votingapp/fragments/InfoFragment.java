package com.example.votingapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.adapters.FAQAdapter;
import com.example.votingapp.adapters.LocationAdapter;
import com.example.votingapp.models.FAQ;
import com.example.votingapp.models.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class InfoFragment extends Fragment {
    private static final String TAG = "InfoFragment";
//    private String apiKey = BuildConfig.GOOGLE_API_KEY;
//    AsyncHttpClient client;
    public static final String[] QUESTIONS = {"Where do I find information about my state's elections?"};
    public static final String[] ANSWERS = {"You learn more about any upcoming general elections in your state at the following link."};
    public static final String[] URL_KEYS = {"electionInfoUrl"};

    // variables with information about the state urls
    Integer electionId;
    static String electionInfoUrl;
    static String stateName;

    static TextView tvName;
    static TextView tvElectionInfoUrl;
    static TextView tvElectionRegistrationUrl;
    static TextView tvElectionRegistrationConfirmationUrl;
    static TextView tvAbsenteeVotingUrl;
    static TextView tvLocationFinderUrl;
    static TextView tvBallotInfoUrl;
    static ConstraintLayout cl1;
    static ConstraintLayout cl2;
    static ConstraintLayout cl3;
    static ConstraintLayout cl4;
    static ConstraintLayout cl5;
    static ConstraintLayout cl6;

    static List<FAQ> faqs;
    RecyclerView rvFAQs;
    static FAQAdapter faqAdapter;


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
        tvName = view.findViewById(R.id.tvName);
        faqs = new ArrayList<>();
        rvFAQs = view.findViewById(R.id.rvFAQs);
        faqAdapter = new FAQAdapter(getContext(), faqs);
        rvFAQs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFAQs.setAdapter(faqAdapter);

        tvElectionInfoUrl = view.findViewById(R.id.tvUrl);
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
        electionId = 0;
        Network.getStateInfo(electionId);
    }

    public static List<FAQ> getFAQs(JSONObject stateurls) {
        List<FAQ> faqs = new ArrayList<>();

        for (int i =0 ; i< QUESTIONS.length; i++) {
            try {
                String electionInfoUrl = stateurls.getString("electionInfoUrl");
                tvElectionInfoUrl.setText(electionInfoUrl);
            } catch (JSONException e) {
                e.printStackTrace();
                cl1.setVisibility(View.GONE);
            }
            faqs.add(new FAQ(QUESTIONS[i], ANSWERS[i], URL_KEYS[i]));
        }

        return faqs;
    }

    public static void parseStateObject(JSONObject state) {
        try {
            stateName = state.getString("name");
            JSONObject stateurls = state.getJSONObject("electionAdministrationBody");
            // set the set of variables in the layout
            tvName.setText(stateName + " Voting Information");
            getUrls(stateurls);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // method to retrieve all of the urls from the JSONObject
    public static void getUrls(JSONObject stateurls) {
        // try catches for all of the urls
        try {
            String electionInfoUrl = stateurls.getString("electionInfoUrl");
            tvElectionInfoUrl.setText(electionInfoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            cl1.setVisibility(View.GONE);
        }
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
}