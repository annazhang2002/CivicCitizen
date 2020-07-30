package com.example.votingapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.votingapp.MethodLibrary;
import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.fragments.dialogFragments.ActionCompleteFragment;
import com.example.votingapp.fragments.dialogFragments.EditActionDialogFragment;
import com.example.votingapp.models.Action;
import com.parse.Parse;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    private static final String TAG = "ActionAdapter";
    List<Action> actions;
    Context context;
    FragmentManager fragmentManager;
    ParseUser user;

    public ActionAdapter(Context context, List<Action> actions, FragmentManager fragmentManager) {
        this.context = context;
        this.actions = actions;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_action, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Action action = actions.get(position);
        holder.bind(action);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        ImageView ivIcon;
        ImageView ivShare;
        ImageView ivEdit;
        TextView tvDate;
        TextView tvNotes;
        ImageView ivImage;


        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivShare = itemView.findViewById(R.id.ivShare);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            ivImage = itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(this);
        }

        public void bind(final Action action) {
            user = action.getUser();
            if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                tvName.setText("I " + action.getName());
                ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MethodLibrary.shareContentHTML(context, "<p>Hey, I just " + action.getName() + "! Make sure you do before the deadline!</p>");
                    }
                });
                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openEditDialog();
                    }
                });
            } else {
                tvName.setText(user.getString("name") + " " + action.getName());
                ivShare.setVisibility(View.GONE);
                ivEdit.setVisibility(View.GONE);
            }
            tvDate.setText(action.getDate());
            if (action.getNotes() == null || action.getNotes().isEmpty()) {
                tvNotes.setVisibility(View.GONE);
            } else {
                tvNotes.setText(action.getNotes());
            }
            if (action.getImage() == null) {
                ivImage.setVisibility(View.GONE);
            } else {
                Glide.with(context).load(action.getImage().getUrl()).transform(new RoundedCornersTransformation(30, 0)).into(ivImage);
            }
            if (action.getName().equals(Network.ACTION_NAMES[0])) {
                Glide.with(context).load(R.drawable.ic_how_to_reg_24px).into(ivIcon);
            } else if (action.getName().equals(Network.ACTION_NAMES[1])) {
                Glide.with(context).load(R.drawable.ic_ballot_24px).into(ivIcon);
            } else if (action.getName().equals(Network.ACTION_NAMES[2])) {
                Glide.with(context).load(R.drawable.ic_how_to_vote_24px).into(ivIcon);
            }
        }

        public void openEditDialog() {
            Integer position = getAdapterPosition();
            // making sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                final Action action = actions.get(position);
                EditActionDialogFragment editActionDialogFragment = EditActionDialogFragment.newInstance(context.getPackageManager(), action);
                editActionDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        refreshAction(action);
                    }
                });
                editActionDialogFragment.show(fragmentManager, "fragment_edit_action");
            }
        }

        public void refreshAction(Action action) {
            if (action.getNotes() == null) {
                tvNotes.setVisibility(View.GONE);
            } else {
                tvNotes.setText(action.getNotes());
            }
            if (action.getImage() == null) {
                ivImage.setVisibility(View.GONE);
            } else {
                ivImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(action.getImage().getUrl()).transform(new RoundedCornersTransformation(30, 0)).into(ivImage);
            }
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick adapter action item");
            if (!user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                MainActivity.goUserProfile(user);
            }

        }
    }
}
