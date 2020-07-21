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

import com.example.votingapp.R;
import com.example.votingapp.adapters.LocationAdapter;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class LocationsFragment extends Fragment {

    private static final String ARG_ELECTION = "election";

    // TODO: Rename and change types of parameters
    Election election;
    static Context context;
    static List<Location> locations;
    RecyclerView rvLocations;
    static LocationAdapter locationAdapter;

    public LocationsFragment() {
        // Required empty public constructor
    }

    public static LocationsFragment newInstance(Context context1, Election election, List<Location> inLocations) {
        LocationsFragment fragment = new LocationsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ELECTION, Parcels.wrap(election));
        context = context1;
        locations = new ArrayList<>();
        locations.addAll(inLocations);
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
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvLocations = view.findViewById(R.id.rvLocations);
        locationAdapter = new LocationAdapter(context, locations);
        rvLocations.setLayoutManager(new LinearLayoutManager(context));
        rvLocations.setAdapter(locationAdapter);

    }


}