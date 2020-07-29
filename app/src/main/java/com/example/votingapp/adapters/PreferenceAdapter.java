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
import com.example.votingapp.activities.ContestDetailActivity;
import com.example.votingapp.models.Contest;

import org.parceler.Parcels;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class PreferenceAdapter extends RecyclerView.Adapter<PreferenceAdapter.ViewHolder> {

    private static final String TAG = "PreferenceAdapter";
    List<String> preferences;
    Context context;

    public PreferenceAdapter(Context context, List<String> preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_preference, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String preference = preferences.get(position);
        holder.bind(preference, position);
    }

    @Override
    public int getItemCount() {
        return preferences.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        TextView tvNum;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvNum = itemView.findViewById(R.id.tvNum);

            itemView.setOnClickListener(this);
        }

        public void bind(String preference, int position) {
            tvName.setText(preference);
            tvNum.setText((position + 1) + ". ");
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick adapter item");
//            Integer position = getAdapterPosition();
//            // making sure the position is valid
//            if (position != RecyclerView.NO_POSITION) {
//                Preference preference = preferences.get(position);
//                Intent intent = new Intent(context, PreferenceDetailActivity.class);
//                intent.putExtra(Preference.class.getSimpleName(), Parcels.wrap(preference));
//                context.startActivity(intent);
//            }

        }
    }
}