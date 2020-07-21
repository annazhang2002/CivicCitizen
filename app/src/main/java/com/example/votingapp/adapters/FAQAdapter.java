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

        public ViewHolder(View itemView) {
            super(itemView);

            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            tvUrl = itemView.findViewById(R.id.tvUrl);

            itemView.setOnClickListener(this);
        }

        public void bind(FAQ faq) {
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
//                FAQ faq = faqs.get(position);
//                Intent intent = new Intent(context, FAQDetailActivity.class);
//                intent.putExtra(FAQ.class.getSimpleName(), Parcels.wrap(faq));
//                context.startActivity(intent);
            }

        }
    }
}