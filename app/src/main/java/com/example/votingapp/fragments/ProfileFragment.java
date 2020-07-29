package com.example.votingapp.fragments;

import android.content.Intent;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.votingapp.BuildConfig;
import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.activities.EditProfileActivity;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.activities.OpeningActivity;
import com.example.votingapp.adapters.ActionAdapter;
import com.example.votingapp.adapters.CandidateAdapter;
import com.example.votingapp.models.Action;
import com.example.votingapp.models.Candidate;
import com.example.votingapp.models.User;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    ParseUser user;

    ImageView ivProfilePic;
    TextView tvName;
    TextView tvUsername;
    TextView tvAddress;
    Button btnEdit;
    Button btnLogout;
    RecyclerView rvActions;
    static List<Action> actions;
    static ActionAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();
        actions = new ArrayList<>();
        rvActions = view.findViewById(R.id.rvActions);
        rvActions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActionAdapter(getContext(), actions, getFragmentManager());
        rvActions.setAdapter(adapter);
        Network.queryUserActions(ParseUser.getCurrentUser());

        getActivity().setTitle(user.getString("name") + "'s Profile");
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);

        // set values for all of the views
        tvName.setText(user.getString("name"));
        tvUsername.setText("@" + user.getUsername());
        tvAddress.setText(User.getAddress(user));
        if (user.getParseFile(User.KEY_PROFILEPIC) == null) {
            Glide.with(getContext()).load(R.drawable.default_profile).circleCrop().into(ivProfilePic);
        } else {
            Glide.with(getContext()).load(user.getParseFile(User.KEY_PROFILEPIC).getUrl()).circleCrop().into(ivProfilePic);
        }
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goEditProfile();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goOpening();
                ParseUser.logOut();
            }
        });
    }

    public static void handleParseActions(List<Action> newActions) {
        actions.clear();
        actions.addAll(newActions);
        adapter.notifyDataSetChanged();
    }

    private void goOpening() {
        MainActivity.showPd();
        Intent intent = new Intent(getContext(), OpeningActivity.class);
        getContext().startActivity(intent);
    }
    private void goEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        getContext().startActivity(intent);
    }
}