package com.example.votingapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.adapters.ActionAdapter;
import com.example.votingapp.models.Action;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment {

    static List<Action> actions = new ArrayList<>();
    static Context context;
    RecyclerView rvActions;
    static ActionAdapter adapter;
    TextView tvNoActions;
    public SwipeRefreshLayout swipeContainer;

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance( List<Action> inActions) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        actions.clear();
        actions.addAll(inActions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        rvActions = view.findViewById(R.id.rvFriendActions);
        rvActions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActionAdapter(getContext(), actions, getFragmentManager());
        rvActions.setAdapter(adapter);
        tvNoActions = view.findViewById(R.id.tvNoActions);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshActions();
            }
        });

        if (actions.size() == 0 ) {
            tvNoActions.setVisibility(View.VISIBLE);
        } else {
            tvNoActions.setVisibility(View.GONE);
        }
    }

    public void refreshActions() {
        swipeContainer.setRefreshing(false);
        FriendFragment.refreshFriendActions();
    }

}