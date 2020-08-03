package com.example.votingapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.votingapp.R;
import com.example.votingapp.adapters.SearchAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendUserFragment extends Fragment {
    private String mParam1;
    private String mParam2;

    static List<ParseUser> userFriends;
    private SearchAdapter searchAdapter;
    private ListView lvFriends;
    TextView tvNoFriends;
    TextView tvFriendRequests;

    public FriendUserFragment() {
        // Required empty public constructor
    }

    public static FriendUserFragment newInstance(List<ParseUser> friends) {
        FriendUserFragment fragment = new FriendUserFragment();
        Bundle args = new Bundle();
        userFriends = new ArrayList<>();
        userFriends.clear();
        userFriends.addAll(friends);
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
        return inflater.inflate(R.layout.fragment_friend_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvFriends = view.findViewById(R.id.lvFriends);
        searchAdapter = new SearchAdapter(getActivity(), userFriends);
        lvFriends.setAdapter(searchAdapter);
        tvNoFriends = view.findViewById(R.id.tvNoFriends);
        tvFriendRequests = view.findViewById(R.id.tvFriendRequests);

        if (userFriends.size() == 0) {
            tvNoFriends.setVisibility(View.VISIBLE);
        } else {
            tvNoFriends.setVisibility(View.GONE);
        }
    }
}
