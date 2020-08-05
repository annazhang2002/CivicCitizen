package com.example.votingapp.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.fragments.FriendFragment;
import com.example.votingapp.models.Rep;
import com.example.votingapp.models.User;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private static final String TAG = "RequestAdapter";
    List<ParseUser> requests;
    Context context;

    public RequestAdapter(Context context, List<ParseUser> requests) {
        this.context = context;
        this.requests = requests;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvUsername;
        ImageView ivImage;
        Button btnAccept;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            btnAccept = itemView.findViewById(R.id.btnAccept);
        }

        public void bind(final ParseUser request) {
            tvName.setText(request.getString("name"));
            tvUsername.setText(request.getUsername());
            if (request.getParseFile(User.KEY_PROFILEPIC) == null) {
                Glide.with(context).load(R.drawable.default_profile).circleCrop().into(ivImage);
            } else {
                Glide.with(context).load(request.getParseFile(User.KEY_PROFILEPIC).getUrl()).circleCrop().into(ivImage);
            }

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFriend(request);
                }
            });
        }

        private void addFriend(ParseUser request) {
            List<String> newFriends = new ArrayList<>();
            newFriends.addAll(User.getFriends(ParseUser.getCurrentUser()));
            newFriends.add(request.getUsername());
            User.setFriends(ParseUser.getCurrentUser(), newFriends);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.i(TAG, "Saved user accepted friend request");
                }
            });

            requests.remove(getAdapterPosition());
            FriendFragment.removeFriendRequest(request);
        }
    }
}
