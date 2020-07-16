package com.example.votingapp.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.example.votingapp.R;
import com.example.votingapp.activities.EditProfileActivity;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.activities.OpeningActivity;
import com.example.votingapp.models.User;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {
    ParseUser user;

    ImageView ivProfilePic;
    TextView tvName;
    TextView tvUsername;
    TextView tvAddress;
    Button btnEdit;
    Button btnLogout;

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