package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.votingapp.R;
import com.example.votingapp.adapters.CandidateAdapter;
import com.example.votingapp.models.Candidate;
import com.example.votingapp.models.Contest;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ContestDetailActivity extends AppCompatActivity {

    RecyclerView rvCandidates;
    List<Candidate> candidates;
    CandidateAdapter adapter;
    TextView tvTitle;
    TextView tvLevel;
    TextView tvDistrict;

    Contest contest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_detail);

        contest = Parcels.unwrap(getIntent().getParcelableExtra(Contest.class.getSimpleName()));

        tvTitle = findViewById(R.id.tvTitle);
        tvLevel = findViewById(R.id.tvLevel);
        tvDistrict = findViewById(R.id.tvDistrict);


        candidates = new ArrayList<>();
        rvCandidates = findViewById(R.id.rvCandidates);
        rvCandidates.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CandidateAdapter(this, candidates);
        rvCandidates.setAdapter(adapter);

        candidates.addAll(contest.getCandidates());
        adapter.notifyDataSetChanged();

        tvTitle.setText(contest.getBallotTitle());
        tvLevel.setText(contest.getLevel());
        tvDistrict.setText(contest.getDistrict());
    }
}