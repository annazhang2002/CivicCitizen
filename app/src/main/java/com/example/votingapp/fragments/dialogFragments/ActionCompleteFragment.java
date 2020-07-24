package com.example.votingapp.fragments.dialogFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.models.Action;

import org.parceler.Parcels;

public class ActionCompleteFragment extends DialogFragment {

    private static Context context1;
    Action action;
    ImageView ivClose;
    ImageView ivShare;
    TextView tvMessage;

    public ActionCompleteFragment() {
        // Required empty public constructor
    }

    public static ActionCompleteFragment newInstance(Context context, Action action) {
        ActionCompleteFragment frag = new ActionCompleteFragment();
        Bundle args = new Bundle();
        args.putParcelable("action", Parcels.wrap(action));
        frag.setArguments(args);
        context1 = context;
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_action_complete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        action = Parcels.unwrap(getArguments().getParcelable("action"));

        ivClose = view.findViewById(R.id.ivClose);
        ivShare = view.findViewById(R.id.ivShare);
        tvMessage = view.findViewById(R.id.tvMessage);

        tvMessage.setText("Great work! You just " + action.getName() + "! Thanks for being a Civic Citizen");

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MethodLibrary.shareContentHTML(context1, "<p>Hey, I just " + action.getName() + "! Make sure you do before the election deadline!</p>");
            }
        });
    }
}