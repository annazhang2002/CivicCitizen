package com.example.votingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingapp.R;
import com.example.votingapp.models.Election;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ElectionsAdapter extends RecyclerView.Adapter<ElectionsAdapter.ViewHolder> {

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvElectionDay;
        TextView tvDistrict;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvElectionDay = itemView.findViewById(R.id.tvElectionDay);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);

        }

        public void bind(Election election) {
            tvName.setText(election.getName());
            tvElectionDay.setText(election.getElectionDay());
            tvDistrict.setText(election.getDivision());
        }
    }
}
