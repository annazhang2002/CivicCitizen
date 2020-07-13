package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.votingapp.R;
import com.parse.ParseUser;

public class OpeningActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

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
//        intent.putExtra()
        this.startActivity(intent);
    }
}