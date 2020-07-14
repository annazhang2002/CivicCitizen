package com.example.votingapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.votingapp.R;
import com.example.votingapp.models.Candidate;

import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {

    private static final String TAG = "CandidateAdapter";
    List<Candidate> candidates;
    Context context;

    public CandidateAdapter(Context context, List<Candidate> candidates) {
        this.context = context;
        this.candidates = candidates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_candidate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidate candidate = candidates.get(position);
        holder.bind(candidate);
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        TextView tvParty;
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvParty = itemView.findViewById(R.id.tvParty);
            ivImage = itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(this);
        }

        public void bind(Candidate candidate) {
            tvName.setText(candidate.getName());
            tvParty.setText(candidate.getParty());
            if (candidate.getPhotoUrl() == null) {
                Glide.with(context).load(R.drawable.default_profile).into(ivImage);
            } else {
                Glide.with(context).load(candidate.getPhotoUrl()).into(ivImage);
            }
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick adapter candidate item");
//            Integer position = getAdapterPosition();
//            // making sure the position is valid
//            if (position != RecyclerView.NO_POSITION) {
//                Candidate election = candidates.get(position);
//                Intent intent = new Intent(context, CandidateDetailActivity.class);
//                intent.putExtra(Candidate.class.getSimpleName(), Parcels.wrap(election));
//                context.startActivity(intent);
//            }

        }
    }
}
