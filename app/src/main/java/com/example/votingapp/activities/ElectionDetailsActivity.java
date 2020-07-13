package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.votingapp.R;
import com.example.votingapp.models.Election;

import org.parceler.Parcels;

public class ElectionDetailsActivity extends AppCompatActivity {

    Election election;

    TextView tvElectionDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_details);

        election = Parcels.unwrap(getIntent().getParcelableExtra(Election.class.getSimpleName()));

        getSupportActionBar().setTitle(election.getName());

        tvElectionDay = findViewById(R.id.tvElectionDay);
        tvElectionDay.setText(election.getElectionDay());
    }
}