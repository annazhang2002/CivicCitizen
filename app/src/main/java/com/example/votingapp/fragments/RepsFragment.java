package com.example.votingapp.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.adapters.RepAdapter;
import com.example.votingapp.models.Candidate;
import com.example.votingapp.models.Rep;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RepsFragment extends Fragment {

    private static final String TAG = "RepsFragment";
    public static List<Rep> reps;
    public static RepAdapter adapter;
    public static RecyclerView rvReps;
    PackageManager packageManager;

    public RepsFragment() {
        // Required empty public constructor
    }
    public RepsFragment(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.showPd();
        reps = new ArrayList<>();
        rvReps = view.findViewById(R.id.rvReps);
        adapter = new RepAdapter(getContext(), reps, packageManager,getFragmentManager());
        rvReps.setAdapter(adapter);
        rvReps.setLayoutManager(new LinearLayoutManager(getContext()));

        Network.getReps();

    }

    public static void parseNetworkRequest(JSONArray offices, JSONArray people) {
        try {
            reps.addAll(Rep.fromJSON(offices, people));
            adapter.notifyDataSetChanged();
            MainActivity.hidePd();
            Log.i(TAG, "retrieved reps: " + reps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}