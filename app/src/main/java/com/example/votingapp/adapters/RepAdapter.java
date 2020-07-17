package com.example.votingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.activities.RepDetailsActivity;
import com.example.votingapp.models.Candidate;
import com.example.votingapp.models.Rep;

import org.parceler.Parcels;

import java.util.List;

public class RepAdapter extends RecyclerView.Adapter<RepAdapter.ViewHolder> {

    private static final String TAG = "RepAdapter";
    List<Rep> reps;
    Context context;
    PackageManager packageManager;
    FragmentManager fragmentManager;

    public RepAdapter(Context context, List<Rep> reps, PackageManager packageManager, FragmentManager fragmentManager) {
        this.context = context;
        this.reps = reps;
        this.packageManager = packageManager;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rep rep = reps.get(position);
        holder.bind(rep);
    }

    @Override
    public int getItemCount() {
        return reps.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rep, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        TextView tvParty;
        ImageView ivImage;
        TextView tvUrl;
        Button btnMessage;
        TextView tvMore;
        TextView tvPosition;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvParty = itemView.findViewById(R.id.tvParty);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvUrl = itemView.findViewById(R.id.tvUrl);
            btnMessage = itemView.findViewById(R.id.btnMessage);
            tvMore = itemView.findViewById(R.id.tvMore);
            tvPosition = itemView.findViewById(R.id.tvPosition);

            itemView.setOnClickListener(this);
        }

        public void bind(final Rep rep) {
            tvName.setText(rep.getName());
            tvParty.setText(rep.getParty());
            if (rep.getPhotoUrl() == null) {
                Glide.with(context).load(R.drawable.default_profile).into(ivImage);
            } else {
                Glide.with(context).load(rep.getPhotoUrl()).into(ivImage);
            }
            String url = rep.getWebUrl();
            if (url != null) {
                tvUrl.setText(url);
            } else {
                tvUrl.setVisibility(View.GONE);
            }
            tvPosition.setText(rep.getPosition());
            if (rep.getEmail() != null) {
                btnMessage.setText("Send email");
                btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MethodLibrary.showRepMessageDialog(fragmentManager, context, packageManager, rep);
                    }
                });
            } else if (rep.getPhone() != null) {
                btnMessage.setText("call");
                btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MethodLibrary.openDialer(rep.getPhone(), context);
                    }
                });
            } else if (rep.getAddress() != null) {
                btnMessage.setText("visit");
                btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MethodLibrary.openUrl(MethodLibrary.MAPS_BASE_URL + rep.getAddress(), context);
                    }
                });
            } else {
                btnMessage.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick adapter rep item");
            Integer position = getAdapterPosition();
            // making sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                Rep rep = reps.get(position);
                Intent intent = new Intent(context, RepDetailsActivity.class);
                intent.putExtra(Rep.class.getSimpleName(), Parcels.wrap(rep));
                context.startActivity(intent);
            }

        }
    }
}
