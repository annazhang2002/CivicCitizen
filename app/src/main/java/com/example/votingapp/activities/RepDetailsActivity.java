package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.ReminderBroadcast;
import com.example.votingapp.fragments.ComposeDialogFragment;
import com.example.votingapp.models.Rep;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.HashMap;

import static androidx.core.content.ContextCompat.getSystemService;

public class RepDetailsActivity extends AppCompatActivity {
    Rep rep;

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
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_details);

        // unwrap parcelable extra from intent
        rep = Parcels.unwrap(getIntent().getParcelableExtra(Rep.class.getSimpleName()));
        context = this;
        // find views
        tvName = findViewById(R.id.tvName);
        tvParty = findViewById(R.id.tvParty);
        ivImage = findViewById(R.id.ivImage);
        tvUrl = findViewById(R.id.tvUrl);
        tvPosition = findViewById(R.id.tvPosition);
        tvAddressLabel = findViewById(R.id.tvAddressLabel);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhoneLabel = findViewById(R.id.tvPhoneLabel);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmailLabel = findViewById(R.id.tvEmailLabel);
        tvEmail = findViewById(R.id.tvEmail);
        llChannels = findViewById(R.id.llChannels);
        ivTwitter = findViewById(R.id.ivTwitter);
        ivFacebook = findViewById(R.id.ivFacebook);
        ivYoutube = findViewById(R.id.ivYoutube);
        btnMessage = findViewById(R.id.btnMessage);

        // set values for each view
        tvName.setText(rep.getName());
        tvParty.setText(rep.getParty());
        if (rep.getPhotoUrl() == null) {
            Glide.with(this).load(R.drawable.default_profile).into(ivImage);
        } else {
            Glide.with(this).load(rep.getPhotoUrl()).into(ivImage);
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
                    MethodLibrary.sendEmail(rep.getEmail(), "", "", getPackageManager(), context);
                }
            });
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MethodLibrary.showRepMessageDialog(getSupportFragmentManager(), context, getPackageManager(), rep);
                }
            });
        }
        if (rep.getChannels().size() == 0) {
            llChannels.setVisibility(View.GONE);
        } else {
            setChannels(rep.getChannels());
        }
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