package com.example.votingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingapp.R;
import com.example.votingapp.models.Contest;
import com.example.votingapp.activities.ContestDetailActivity;

import org.parceler.Parcels;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ContestAdapter extends RecyclerView.Adapter<ContestAdapter.ViewHolder> {

    private static final String TAG = "ContestAdapter";
    List<Contest> contests;
    Context context;

    public ContestAdapter(Context context, List<Contest> contests) {
        this.context = context;
        this.contests = contests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contest contest = contests.get(position);
        holder.bind(contest);
    }

    @Override
    public int getItemCount() {
        return contests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle;
        TextView tvLevel;
        TextView tvDistrict;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvDistrict);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvDistrict = itemView.findViewById(R.id.tvTitle);

            itemView.setOnClickListener(this);
        }

        public void bind(Contest contest) {
            tvTitle.setText(contest.getBallotTitle());
            tvLevel.setText(contest.getLevel());
            tvDistrict.setText(contest.getDistrict());

        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick adapter item");
            Integer position = getAdapterPosition();
            // making sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                Contest contest = contests.get(position);
                Intent intent = new Intent(context, ContestDetailActivity.class);
                intent.putExtra(Contest.class.getSimpleName(), Parcels.wrap(contest));
                context.startActivity(intent);
            }

        }
    }
}