package com.example.votingapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.adapters.ContestAdapter;
import com.example.votingapp.models.Contest;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.Contest;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ContestsFragment extends Fragment {

    private static final String ARG_ELECTION = "election";

    // TODO: Rename and change types of parameters
    Election election;
    static Context context;
    static List<Contest> contests;
    RecyclerView rvContests;
    static ContestAdapter contestAdapter;
    TextView tvNone;
    Button btnNone;

    public ContestsFragment() {
        // Required empty public constructor
    }

    public static ContestsFragment newInstance(Context context1, Election election, List<Contest> inContests) {
        ContestsFragment fragment = new ContestsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ELECTION, Parcels.wrap(election));
        context = context1;
        contests = new ArrayList<>();
        contests.addAll(inContests);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            election = Parcels.unwrap(getArguments().getParcelable(ARG_ELECTION));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvContests = view.findViewById(R.id.rvContests);
        contestAdapter = new ContestAdapter(context, contests);
        rvContests.setLayoutManager(new LinearLayoutManager(context));
        rvContests.setAdapter(contestAdapter);
        btnNone = view.findViewById(R.id.btnNone);
        tvNone = view.findViewById(R.id.tvNone);
        if (contests.size() == 0) {
            btnNone.setVisibility(View.VISIBLE);
            tvNone.setVisibility(View.VISIBLE);
            btnNone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.goInfo();
                }
            });
        }

    }


}