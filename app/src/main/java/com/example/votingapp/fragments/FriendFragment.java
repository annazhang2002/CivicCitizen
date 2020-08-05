package com.example.votingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.adapters.SearchAdapter;
import com.example.votingapp.models.Action;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {

    SearchView svSearch;
    ListView lvSearch;
    static List<ParseUser> usersFound;
    static List<ParseUser> friends;
    static List<ParseUser> requests;
    static SearchAdapter searchAdapter;
    static Context context;
    FrameLayout flContainer;
    static String currentFragment = "timeline";
    TextView tvTimeline;
    TextView tvFriends;
    static FragmentManager fragmentManager;
    static List<Action> actions;
    boolean returning = false;


    public FriendFragment() {
        // Required empty public constructor
    }

    public FriendFragment(boolean returning, String startFragment) {
        // Required empty public constructor
        this.returning = returning;
        this.currentFragment = startFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Your Friends");
        fragmentManager = getChildFragmentManager();
        context = getContext();
        usersFound = new ArrayList<>();
        friends = new ArrayList<>();
        actions = new ArrayList<>();
        requests = new ArrayList<>();
        usersFound.clear();
        friends.clear();
        actions.clear();
        requests.clear();
        svSearch = view.findViewById(R.id.svSearch);
        lvSearch = view.findViewById(R.id.lvSearch);
        tvFriends = view.findViewById(R.id.tvFriends);
        tvTimeline = view.findViewById(R.id.tvTimeline);
        flContainer = view.findViewById(R.id.flContainer);
        searchAdapter = new SearchAdapter(getActivity(), usersFound);
        lvSearch.setAdapter(searchAdapter);
        lvSearch.setVisibility(View.GONE);
        if (!returning) {
            Network.queryFriendActions(ParseUser.getCurrentUser());
            Network.queryUserFriends(ParseUser.getCurrentUser());
            Network.queryFriendRequests(ParseUser.getCurrentUser());
        }

        if (currentFragment.equals("timeline")) {
            fragmentManager.beginTransaction().replace(R.id.flContainer,
                    TimelineFragment.newInstance(actions)).commit();
            tvFriends.setBackgroundColor(getResources().getColor(R.color.lightLightBlue));
            tvTimeline.setBackgroundColor(getResources().getColor(R.color.whiteBlue));
        } else {
            fragmentManager.beginTransaction().replace(R.id.flContainer,
                    FriendUserFragment.newInstance(friends, requests)).commit();
            tvFriends.setBackgroundColor(getResources().getColor(R.color.whiteBlue));
            tvTimeline.setBackgroundColor(getResources().getColor(R.color.lightLightBlue));
        }

        // set click listeners for locations and contest toggle
        tvTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentFragment.equals("timeline")) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.flContainer,
                            TimelineFragment.newInstance(actions)).commit();
                    tvFriends.setBackgroundColor(getResources().getColor(R.color.lightLightBlue));
                    tvTimeline.setBackgroundColor(getResources().getColor(R.color.whiteBlue));
                    currentFragment = "timeline";
                }
            }
        });
        tvFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentFragment.equals("friends")) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContainer,
                            FriendUserFragment.newInstance(friends, requests)).commit();
                    tvFriends.setBackgroundColor(getResources().getColor(R.color.whiteBlue));
                    tvTimeline.setBackgroundColor(getResources().getColor(R.color.lightLightBlue));
                    currentFragment = "friends";
                }
            }
        });

        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                lvSearch.setVisibility(View.GONE);
                return false;
            }
        });

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    usersFound.clear();
                    searchAdapter.notifyDataSetChanged();
                    lvSearch.setVisibility(View.GONE);
                } else {
                    lvSearch.setVisibility(View.VISIBLE);
                    Network.searchFriends(ParseUser.getCurrentUser(), s);
                }
                return false;
            }
        });

    }

    public static void goRequests() {
        if (requests.size() != 0) {
            fragmentManager.beginTransaction().replace(R.id.flContainer,
                    FriendRequestFragment.newInstance(requests)).commit();
            currentFragment = "requests";
        } else {
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.flContainer,
                    FriendUserFragment.newInstance(friends, requests)).commit();
            currentFragment = "friends";
        }
    }

    public static void goRequests(String transition) {
        if (requests.size() != 0) {
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContainer,
                    FriendRequestFragment.newInstance(requests)).commit();
            currentFragment = "requests";
        } else {
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.flContainer,
                    FriendUserFragment.newInstance(friends, requests)).commit();
            currentFragment = "friends";
        }
    }


    public static void addRequests(List<ParseUser> users) {
        requests.clear();
        requests.addAll(users);
    }

    public static void addFriendAction(Action action) {
        actions.add(action);
        fragmentManager.beginTransaction().replace(R.id.flContainer,
                TimelineFragment.newInstance(actions)).commit();
    }

    public static void addUserFriends(List<ParseUser> users) {
        friends.addAll(users);
    }

    public static void showSearchResults(List<ParseUser> users) {
        usersFound.clear();
        usersFound.addAll(users);
        searchAdapter.notifyDataSetChanged();
    }

    public static void noFriendAlert(String username) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("No User Found")
                .setMessage("There is no user @" + username + " on Civic Citzen. Please try your search again with a valid username")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("OK", null)
                .show();
    }

    public static void refreshFriendActions() {
        actions.clear();
        Network.queryFriendActions(ParseUser.getCurrentUser());
    }

    public static void removeFriendRequest(ParseUser user) {
        requests.remove(user);
        friends.add(0, user);
        goRequests();
    }

}