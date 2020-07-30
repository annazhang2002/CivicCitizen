package com.example.votingapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.adapters.ActionAdapter;
import com.example.votingapp.adapters.SearchAdapter;
import com.example.votingapp.models.Action;
import com.example.votingapp.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FriendFragment extends Fragment {

    RecyclerView rvActions;
//    Button btnAdd;
//    static EditText etFind;
    SearchView svSearch;
    ListView lvSearch;
    static List<ParseUser> usersFound;
    static List<Action> actions;
    static SearchAdapter searchAdapter;
    static ActionAdapter adapter;
    static Context context;
    boolean returning = false;


    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendFragment() {
        // Required empty public constructor
    }

    public FriendFragment(boolean returning) {
        this.returning = returning;
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
        context = getContext();
        actions = new ArrayList<>();
        usersFound = new ArrayList<>();
        rvActions = view.findViewById(R.id.rvFriendActions);
        svSearch = view.findViewById(R.id.svSearch);
        lvSearch = view.findViewById(R.id.lvSearch);
        searchAdapter = new SearchAdapter(getActivity(), usersFound);
        lvSearch.setAdapter(searchAdapter);
//        btnAdd = view.findViewById(R.id.btnAdd);
//        etFind = view.findViewById(R.id.etFind);
        rvActions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActionAdapter(getContext(), actions, getFragmentManager());
        rvActions.setAdapter(adapter);
        if (!returning) {
            Network.queryFriendActions(ParseUser.getCurrentUser());
        }
        lvSearch.setVisibility(View.GONE);

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

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String username = etFind.getText().toString();
//                if (!User.getFriends(ParseUser.getCurrentUser()).contains(username)) {
//                    Network.findFriend(ParseUser.getCurrentUser(), username);
//                } else {
//                    Toast.makeText(getContext(), "@" + username + " is already your friend!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    public static void showSearchResults(List<ParseUser> users) {
        usersFound.clear();
        usersFound.addAll(users);
        searchAdapter.notifyDataSetChanged();
    }

    public static void addFriendAction(Action action) {
        actions.add(action);
        adapter.notifyDataSetChanged();
    }

    public static void noFriendAlert(String username) {
//        etFind.setText("");
        new MaterialAlertDialogBuilder(context)
                .setTitle("No User Found")
                .setMessage("There is no user @" + username + " on Civic Citzen. Please try your search again with a valid username")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("OK", null)
                .show();
    }
}