package com.example.votingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.votingapp.AnimationLibrary;
import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.models.FAQ;

import org.parceler.Parcels;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {

    private static final String TAG = "FAQAdapter";
    List<FAQ> faqs;
    Context context;

    public FAQAdapter(Context context, List<FAQ> faqs) {
        this.context = context;
        this.faqs = faqs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_faq, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FAQ faq = faqs.get(position);
        holder.bind(faq);
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvQuestion;
        TextView tvAnswer;
        TextView tvUrl;
        ImageView ivArrow;
        RelativeLayout rlContainer;
        boolean isOpen;

        public ViewHolder(View itemView) {
            super(itemView);

            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            tvUrl = itemView.findViewById(R.id.tvUrl);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            rlContainer = itemView.findViewById(R.id.rlContainter);
            isOpen = false;

            itemView.setOnClickListener(this);
        }

        public void bind(FAQ faq) {
            tvAnswer.setVisibility(View.GONE);
            tvUrl.setVisibility(View.GONE);

            tvQuestion.setText(faq.getQuestion());
            tvAnswer.setText(faq.getAnswer());
            tvUrl.setText(faq.getUrl());
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick adapter item");
            Integer position = getAdapterPosition();
            // making sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                if (isOpen) {
                    AnimationLibrary.collapse(rlContainer);
                    AnimationLibrary.rotate(ivArrow, 0);
                    tvAnswer.setVisibility(View.GONE);
                    tvUrl.setVisibility(View.GONE);
//                    Glide.with(context).load(R.drawable.ic_arrow_drop_down_black_18dp).into(ivArrow);
                } else {
                    tvAnswer.setVisibility(View.VISIBLE);
                    tvUrl.setVisibility(View.VISIBLE);
                    AnimationLibrary.rotate(ivArrow, 180);
                    AnimationLibrary.expand(rlContainer);
//                    Glide.with(context).load(R.drawable.ic_arrow_drop_up_black_18dp).into(ivArrow);
                }
                isOpen = !isOpen;
            }

        }
    }
}