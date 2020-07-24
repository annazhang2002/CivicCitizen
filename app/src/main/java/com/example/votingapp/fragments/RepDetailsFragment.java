package com.example.votingapp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.activities.MainActivity;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.Location;
import com.example.votingapp.models.Rep;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Callback;

public class RepDetailsFragment extends Fragment {
    static Rep rep;
    static Context context;
    static PackageManager packageManager;
    static Integer position;

    TextView tvName;
    TextView tvParty;
    ImageView ivImage;
    TextView tvUrl;
    TextView tvPosition;
    TextView tvAddressLabel;
    TextView tvAddress;
    TextView tvPhoneLabel;
    TextView tvPhone;
    TextView tvEmailLabel;
    TextView tvEmail;
    LinearLayout llChannels;
    ImageView ivTwitter;
    ImageView ivFacebook;
    ImageView ivYoutube;
    Button btnMessage;
    Button btnBack;


    public RepDetailsFragment() {
        // Required empty public constructor
    }

    public static RepDetailsFragment newInstance(Context context1, Rep rep1, PackageManager packageManager1, Integer position1) {
        RepDetailsFragment fragment = new RepDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Rep.class.getSimpleName(), Parcels.wrap(rep));
        context = context1;
        rep = rep1;
        packageManager = packageManager1;
        position = position1;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rep_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views
        tvName = view.findViewById(R.id.tvName);
        tvParty = view.findViewById(R.id.tvParty);
        ivImage = view.findViewById(R.id.ivImage);
        tvUrl = view.findViewById(R.id.tvUrl);
        tvPosition = view.findViewById(R.id.tvPosition);
        tvAddressLabel = view.findViewById(R.id.tvAddressLabel);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvPhoneLabel = view.findViewById(R.id.tvPhoneLabel);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmailLabel = view.findViewById(R.id.tvEmailLabel);
        tvEmail = view.findViewById(R.id.tvEmail);
        llChannels = view.findViewById(R.id.llChannels);
        ivTwitter = view.findViewById(R.id.ivTwitter);
        ivFacebook = view.findViewById(R.id.ivFacebook);
        ivYoutube = view.findViewById(R.id.ivYoutube);
        btnMessage = view.findViewById(R.id.btnMessage);
        btnBack = view.findViewById(R.id.btnBack);

        // set values for each view
        tvName.setText(rep.getName());
        tvParty.setText(rep.getParty());
        if (rep.getPhotoUrl() == null) {
            Glide.with(this).load(R.drawable.default_profile).transform(new RoundedCornersTransformation(20, 0)).into(ivImage);
        } else {
            Glide.with(this).load(rep.getPhotoUrl()).transform(new RoundedCornersTransformation(20, 0)).into(ivImage);
        }
        String url = rep.getWebUrl();
        if (url != null) {
            tvUrl.setText(url);
        } else {
            tvUrl.setVisibility(View.GONE);
        }
        tvPosition.setText(rep.getPosition());
        if (rep.getAddress() == null) {
            tvAddressLabel.setVisibility(View.GONE);
            tvAddress.setVisibility(View.GONE);
        } else {
            tvAddress.setText(rep.getAddress());
            tvAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MethodLibrary.openUrl(MethodLibrary.MAPS_BASE_URL + rep.getAddress(), context);
                }
            });
        }
        if (rep.getPhone() == null) {
            tvPhoneLabel.setVisibility(View.GONE);
            tvPhone.setVisibility(View.GONE);
        } else {
            tvPhone.setText(rep.getPhone());
            tvPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MethodLibrary.openDialer(rep.getPhone(), context);
                }
            });
        }
        if (rep.getEmail() == null) {
            tvEmailLabel.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
            btnMessage.setVisibility(View.GONE);
        } else {
            tvEmail.setText(rep.getEmail());
            tvEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MethodLibrary.sendEmail(rep.getEmail(), "", "", packageManager, context);
                }
            });
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MethodLibrary.showRepMessageDialog(getFragmentManager(), context, packageManager, rep);
                }
            });
        }
        if (rep.getChannels().size() == 0) {
            llChannels.setVisibility(View.GONE);
        } else {
            setChannels(rep.getChannels());
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backToReps(position);
            }
        });
    }

    public void setChannels(final HashMap<String, String> channels) {
        if (channels.get("Facebook") != null) {
            ivFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MethodLibrary.openUrl(MethodLibrary.FACEBOOK_BASE_URL + channels.get("Facebook"), context);
                }
            });
        } else {
            ivFacebook.setVisibility(View.GONE);
        }
        if (channels.get("Twitter") != null) {
            ivTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MethodLibrary.openUrl(MethodLibrary.TWITTER_BASE_URL + channels.get("Twitter"), context);
                }
            });
        } else {
            ivTwitter.setVisibility(View.GONE);
        }
        if (channels.get("Youtube") != null) {
            ivYoutube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MethodLibrary.openUrl(MethodLibrary.YOUTUBE_BASE_URL + channels.get("Youtube"), context);
                }
            });
        } else {
            ivYoutube.setVisibility(View.GONE);
        }
    }

}