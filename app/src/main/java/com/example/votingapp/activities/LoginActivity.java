package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votingapp.R;
import com.example.votingapp.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    // Keys for user login
    public static final String KEY_NAME = "name";
    public static final String KEY_ADD1 = "addressLine1";
    public static final String KEY_CITY = "addressCity";
    public static final String KEY_STATE = "addressState";
    public static final String KEY_ZIP = "addressZip";

    private static final String TAG = "LoginActivity";
    EditText etUsername;
    EditText etName;
    EditText etPassword;
    EditText etAddressLine1;
    EditText etAddressCity;
    EditText etAddressState;
    EditText etAddressZip;
    Button btnEnter;
    TextView tvError;
    ProgressDialog pd;

    // string for whether it is login or signup
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        type = getIntent().getStringExtra("type");

        createProgressDialog();
        etUsername = findViewById(R.id.etUsername);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etAddressLine1 = findViewById(R.id.etAddressLine1);
        etAddressCity = findViewById(R.id.etAddressCity);
        etAddressState = findViewById(R.id.etAddressState);
        etAddressZip = findViewById(R.id.etAddressZip);
        btnEnter = findViewById(R.id.btnEnter);
        tvError = findViewById(R.id.tvError);

        // if the user is logging in hide most of the fields
        if (type.equals("login")) {
            etName.setVisibility(View.GONE);
            etAddressLine1.setVisibility(View.GONE);
            etAddressCity.setVisibility(View.GONE);
            etAddressState.setVisibility(View.GONE);
            etAddressZip.setVisibility(View.GONE);
            btnEnter.setText("Login");
        }

        // onclicklistener when the user presses enter
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                // if it is a user login
                if (type.equals("login")) {
                    loginUser(username, password);
                }
                // if it is a sign up
                else {
                    String name = etName.getText().toString();
                    String add1 = etAddressLine1.getText().toString();
                    String city = etAddressCity.getText().toString();
                    String state = etAddressState.getText().toString();
                    String zip = etAddressZip.getText().toString();

                    createUser(username, password, name, add1, city, state, zip);
                }

            }
        });
    }

    private void createUser(String username, String password, String name, String add1, String city, String state, String zip) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        //  Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.put(KEY_NAME, name);
        user.put(KEY_ADD1, add1);
        user.put(KEY_CITY, city);
        user.put(KEY_STATE, state);
        user.put(KEY_ZIP, zip);
        List<Integer> locationPreferences = new ArrayList<>();
        locationPreferences.add(5);
        locationPreferences.add(3);
        locationPreferences.add(1);
        user.put(User.KEY_LOCATION_WEIGHTS, locationPreferences);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with signup", e);
                    tvError.setText(e.getMessage());
                    pd.hide();
                    return;
                }
                tvError.setText("");
                goMainActivity();
                pd.hide();
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        // check if the credentials are correct
        pd.show();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    tvError.setText(e.getMessage());
                    return;
                }
                tvError.setText("");
                goMainActivity();
            }
        });
    }

    public void createProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setCancelable(false);
    }

    public void goMainActivity() {
        // navigate to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("frgToLoad", "home");
        this.startActivity(intent);
    }
}