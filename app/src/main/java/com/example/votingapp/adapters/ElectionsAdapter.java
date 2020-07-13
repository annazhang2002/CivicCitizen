package com.example.votingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingapp.R;
import com.example.votingapp.activities.ElectionDetailsActivity;
import com.example.votingapp.models.Election;

import org.parceler.Parcels;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ElectionsAdapter extends RecyclerView.Adapter<ElectionsAdapter.ViewHolder> {

    private static final String TAG = "ElectionsAdapter";
    List<Election> elections;
    Context context;

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
        TextView tvDistrict;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvElectionDay = itemView.findViewById(R.id.tvElectionDay);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);

            itemView.setOnClickListener(this);
        }

        public void bind(Election election) {
            tvName.setText(election.getName());
            tvElectionDay.setText(election.getElectionDay());
            tvDistrict.setText(election.getDivision());
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick adapter item");
            Integer position = getAdapterPosition();
            // making sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                Election election = elections.get(position);
                Intent intent = new Intent(context, ElectionDetailsActivity.class);
                intent.putExtra(Election.class.getSimpleName(), Parcels.wrap(election));
                context.startActivity(intent);
            }

        }
    }
}
