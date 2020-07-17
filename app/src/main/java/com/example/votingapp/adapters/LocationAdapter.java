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

import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.models.Location;
import com.example.votingapp.activities.ContestDetailActivity;

import org.parceler.Parcels;

import java.util.List;

import static com.example.votingapp.MethodLibrary.openUrl;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private static final String TAG = "LocationAdapter";
    List<Location> locations;
    Context context;

    public LocationAdapter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        TextView tvAddress;
        TextView tvDates;
        TextView tvPollingHours;
        TextView tvType;
        TextView tvNotes;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvPollingHours = itemView.findViewById(R.id.tvPollingHours);
            tvType = itemView.findViewById(R.id.tvType);
            tvNotes = itemView.findViewById(R.id.tvNotes);

            itemView.setOnClickListener(this);
        }

        public void bind(Location location) {
            tvName.setText(location.getName());
            tvAddress.setText(location.getAddress());
            tvDates.setText(location.getStartDate() + " - " + location.getEndDate());
            tvPollingHours.setText(location.getPollingHours());
            tvType.setText(location.getType());
            if (!location.getNotes().equals("NULL")) {
                tvNotes.setText(location.getNotes());
            } else {
                tvNotes.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            openUrl(MethodLibrary.MAPS_BASE_URL + tvAddress.getText().toString(), context);
        }
    }
}