package com.example.votingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.fragments.ElectionDetailsFragment;
import com.example.votingapp.models.Action;
import com.example.votingapp.models.Election;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ElectionsAdapter extends RecyclerView.Adapter<ElectionsAdapter.ViewHolder> {

    private static final String TAG = "ElectionsAdapter";
    List<Election> elections;
    Context context;
    static ProgressBar pbCompletion;
    static TextView tvPercentage;

    public ElectionsAdapter(Context context, List<Election> elections) {
        this.context = context;
        this.elections = elections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_election, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Election election = elections.get(position);
        holder.bind(election);
    }

    @Override
    public int getItemCount() {
        return elections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        TextView tvElectionDay;
//        TextView tvDistrict;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            pbCompletion = itemView.findViewById(R.id.pbCompletion);
            tvElectionDay = itemView.findViewById(R.id.tvElectionDay);
            tvPercentage = itemView.findViewById(R.id.tvPercentage);
//            tvDistrict = itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        public void bind(Election election) {
            tvName.setText(election.getName());
            tvElectionDay.setText(election.getShortElectionDay() + "");
            if (election.getActions() == null) {
                Network.queryActions(election);
            } else {
                setProgress(election);
            }
//            tvDistrict.setText(election.getDivision());
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick adapter item");
            Integer position = getAdapterPosition();
            // making sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                Election election = elections.get(position);
                MainActivity.goElectionDetails(election);
            }

        }
    }

    public static void addActions(List<Action> listActions, Election election) {
        HashMap<String, Action> actions = new HashMap<>();
        for (Action action : listActions) {
            actions.put(action.getName(), action);
        }
        election.setActions(actions);
        setProgress(election);
    }

    public static void setProgress(Election election) {
        pbCompletion.setProgress(election.getProgress());
        tvPercentage.setText(election.getProgress() + "%");
        MainActivity.hidePd();
    }

    public static void handleParseActions(List<Action> listActions, Election election) {
        Log.i(TAG, "handleParseActions: "  + listActions);

        // if there are no listActions for this election + user yet, then make them!
        if (listActions.size() == 0) {
            Network.createElectionActions(election);
        } else {
            addActions(listActions, election);
        }
    }
}
