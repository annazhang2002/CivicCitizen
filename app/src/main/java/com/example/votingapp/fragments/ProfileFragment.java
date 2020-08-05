package com.example.votingapp.fragments;

import android.content.Intent;
import android.media.Image;
import android.media.VolumeAutomation;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.votingapp.MethodLibrary;
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
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParsePush;

import org.json.JSONObject;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    ParseUser user;

    ImageView ivProfilePic;
    TextView tvName;
    TextView tvUsername;
    TextView tvAddress;
    TextView tvAddressText;
    Button btnEdit;
    Button btnLogout;
    ImageView ivBack;
    RecyclerView rvActions;
    static List<Action> actions;
    static ActionAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
        user = ParseUser.getCurrentUser();
    }

    public ProfileFragment(ParseUser user) {
        this.user = user;
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
        actions = new ArrayList<>();
        rvActions = view.findViewById(R.id.rvActions);
        rvActions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActionAdapter(getContext(), actions, getFragmentManager(), user);
        rvActions.setAdapter(adapter);
        Network.queryUserActions(user);

        getActivity().setTitle(user.getString("name") + "'s Profile");
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvAddressText = view.findViewById(R.id.tvAddressText);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivBack = view.findViewById(R.id.ivBack);

        // set values for all of the views
        tvName.setText(user.getString("name"));
        tvUsername.setText("@" + user.getUsername());

        // if the user is the current user
        if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            tvAddress.setText(User.getAddress(user));
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
            ivBack.setVisibility(View.GONE);
        }
        // if we are viewing someone else's profile
        else {
            tvAddress.setVisibility(View.GONE);
            tvAddressText.setVisibility(View.GONE);
            if (User.getFriends(user).contains(ParseUser.getCurrentUser().getUsername())) {
                btnEdit.setText("Poke (Remind)");
                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        poke();
                    }
                });
            } else {
                btnEdit.setVisibility(View.GONE);
            }
            // if the current user is friends with this user
            if (User.getFriends(ParseUser.getCurrentUser()).contains(user.getUsername())) {
                btnLogout.setText("Unfriend");
            } else {
                btnLogout.setText("Friend");
            }

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendClick();
                }
            });
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.goFriends("timeline");
                }
            });
        }
        if (user.getParseFile(User.KEY_PROFILEPIC) == null) {
            Glide.with(getContext()).load(R.drawable.default_profile).circleCrop().into(ivProfilePic);
        } else {
            Glide.with(getContext()).load(user.getParseFile(User.KEY_PROFILEPIC).getUrl()).circleCrop().into(ivProfilePic);
        }

    }

    private void poke() {
        MethodLibrary.testNotification();
//        MethodLibrary.pushNotification(user.getObjectId(), ParseUser.getCurrentUser().get("name") + " poked you and wants to remind you of the upcoming election deadlines!");
    }

    private void friendClick() {
        // if the person is already user's friend, then unfriend
        List<String> friends = new ArrayList<>();
        friends.addAll(User.getFriends(ParseUser.getCurrentUser()));
        if (friends.contains(user.getUsername())) {
            friends.remove(friends.indexOf(user.getUsername()));
            btnLogout.setText("Friend");
        }
        // else friend them
        else {
            friends.add(user.getUsername());
            btnLogout.setText("Unfriend");
        }
        ParseUser.getCurrentUser().put(User.KEY_FRIENDS, friends);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i(TAG, "User's friends updates");
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