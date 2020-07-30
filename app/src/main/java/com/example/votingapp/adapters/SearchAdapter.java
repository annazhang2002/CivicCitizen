package com.example.votingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
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

            tvName.setText(user.getString("name"));
            tvUsername.setText("@" + user.getUsername());
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