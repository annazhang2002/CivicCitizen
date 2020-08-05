package com.example.votingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.votingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class OpeningActivity extends AppCompatActivity {

    private static final String TAG = "OpeningActivity";
    Button btnLogin;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ParseUser.getCurrentUser() != null) {
            saveInstallation();
            goMainActivity();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        getSupportActionBar().hide();

        // accessing resources from layout
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // firing intents from onclick listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireIntent("login");
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireIntent("signup");
            }
        });
    }

    private void saveInstallation() {
        ParseInstallation currInstall = ParseInstallation.getCurrentInstallation();
        currInstall.put("user", ParseUser.getCurrentUser());
        currInstall.saveInBackground();

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        // Log and toast
//                        Log.d(TAG, "Token is " + token);
//                    }
//                });

    }

    // method to fre intent to login activity
    // takes in the type which is either login or signup
    public void fireIntent(String type) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("type", type);
        this.startActivity(intent);
    }

    public void goMainActivity() {
        // navigate to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("frgToLoad","home");
        this.startActivity(intent);
        finish();
    }
}