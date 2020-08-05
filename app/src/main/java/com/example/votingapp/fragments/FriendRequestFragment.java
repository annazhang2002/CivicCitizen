package com.example.votingapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.adapters.RequestAdapter;
import com.example.votingapp.adapters.SearchAdapter;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestFragment extends Fragment {
    private String mParam1;
    private String mParam2;

    static List<ParseUser> requests;
    private static RequestAdapter adapter;
    private RecyclerView rvFriendRequests;

    public FriendRequestFragment() {
        // Required empty public constructor
    }

    public static FriendRequestFragment newInstance(List<ParseUser> users) {
        FriendRequestFragment fragment = new FriendRequestFragment();
        Bundle args = new Bundle();
        requests = new ArrayList<>();
        requests.clear();
        requests.addAll(users);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFriendRequests = view.findViewById(R.id.rvFriendRequests);
        adapter = new RequestAdapter(getContext(), requests);
        rvFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriendRequests.setAdapter(adapter);
    }

}
