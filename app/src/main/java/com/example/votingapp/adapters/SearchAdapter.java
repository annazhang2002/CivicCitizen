package com.example.votingapp.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.models.User;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends ArrayAdapter<ParseUser> {
    List<ParseUser> usersFound = new ArrayList<>();

    public SearchAdapter(Context context, List<ParseUser> usersFound) {
        super(context, 0, usersFound);
        this.usersFound.addAll(usersFound);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final ParseUser user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search, parent, false);

            TextView tvName = convertView.findViewById(R.id.tvName);
            TextView tvUsername = convertView.findViewById(R.id.tvUsername);
            RelativeLayout rlLayout = convertView.findViewById(R.id.rlLayout);
            ImageView ivImage = convertView.findViewById(R.id.ivImage);

            tvName.setText(user.getString("name"));
            tvUsername.setText("@" + user.getUsername());

            if (user.getParseFile(User.KEY_PROFILEPIC) == null) {
                Glide.with(getContext()).load(R.drawable.default_profile).circleCrop().into(ivImage);
            } else {
                Glide.with(getContext()).load(user.getParseFile(User.KEY_PROFILEPIC).getUrl()).circleCrop().into(ivImage);
            }


            rlLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.goUserProfile(user);
                }
            });
        }
        // Return the completed view to render on screen
        return convertView;
    }
}