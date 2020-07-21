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
    public static final String[] QUESTIONS = {
            "Where do I find information about my state's elections?",
            "How do I register to vote?",
            "Where can I check if I am registered to vote?",
            "I will not be in my home state during the election. Can I still vote?",
            "Where do I go to vote?",
            "Where can I find information about the election ballot?",
            "What is the deadline to register to vote?",
            "What is the deadline to get an absentee ballot?"


    };
    public static final String[] ANSWERS = {
            "You learn more about any upcoming general elections in your state at the following link.",
            "Using the link below, you can access your state's website with a form that will allow you to register to vote. An alternative is also to visit https://www.vote.org/register-to-vote/",
            "Your state has provided the url below to confirm your registration. An alternative is also to visit https://www.vote.org/am-i-registered-to-vote/ which is one of the fastest ways to check you voter registration.",
            "Yes you can! You just need to get an absentee ballot that will be mailed to your current address. The link below has more information about your state's absentee voting policies",
            "There are many polling locations for each election. To look up where your polling locations are, click the link below",
            "The link below has a lot of information about the ballot in your specific state. You can learn about ballot measures, deadlines, and candidates.",
            "Voting deadlines vary between states, but the deadline is generally around 30 days before an election. The link below is a great resource to find your state's registration deadline. ",
            "Absentee application deadlines vary between states, but generally applications must be mailed in around 15 days before an election. The link below is a great resource to find your state's registration deadline. "

    };
    public static final String[] URL_KEYS = {
            "electionInfoUrl",
            "electionRegistrationUrl",
            "electionRegistrationConfirmationUrl",
            "absenteeVotingInfoUrl",
            "votingLocationFinderUrl",
            "ballotInfoUrl"
    };

    public static final String[] EXTRA_URLS = {
            "https://www.vote.org/voter-registration-deadlines/",
            "https://www.vote.org/absentee-ballot-deadlines/"
    };

    // variables with information about the state urls
    Integer electionId;
    static String electionInfoUrl;
    static String stateName;

    static TextView tvName;

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

        electionId = 0;
        Network.getStateInfo(electionId);
    }

    public static void getFAQs(JSONObject stateurls) {
        // get the urls from the API
        for (int i =0 ; i< URL_KEYS.length; i++) {
            try {
                String url = stateurls.getString(URL_KEYS[i]);
                faqs.add(new FAQ(QUESTIONS[i], ANSWERS[i], url));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // get a list of the extra urls
        for (int i = 0; i< EXTRA_URLS.length; i++) {
            Integer qaIndex = i + URL_KEYS.length;
            faqs.add(new FAQ(QUESTIONS[qaIndex], ANSWERS[qaIndex], EXTRA_URLS[i]));
        }
        faqAdapter.notifyDataSetChanged();
    }

    public static void parseStateObject(JSONObject state) {
        try {
            stateName = state.getString("name");
            JSONObject stateurls = state.getJSONObject("electionAdministrationBody");
            // set the set of variables in the layout
            tvName.setText(stateName + " Voting Information");
            getFAQs(stateurls);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}