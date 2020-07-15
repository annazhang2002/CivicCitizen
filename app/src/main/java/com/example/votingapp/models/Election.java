package com.example.votingapp.models;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Parcel
public class Election {

    public static final String ELECTION_URL = "https://www.googleapis.com/civicinfo/v2/elections";
    public static final String VOTER_INFO_URL = "https://www.googleapis.com/civicinfo/v2/voterinfo";

    String name;
    String electionDay;
    int id;
    String division;

    // no-arg, empty constructor required for Parceler
    public Election() {}

    public Election(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        electionDay = jsonObject.getString("electionDay");
        id = jsonObject.getInt("id");
        division = jsonObject.getString("ocdDivisionId");
    }

    public static List<Election> fromJsonArray(JSONArray electionsJSONArray) throws JSONException {
        // create a list of elections we get
        List<Election> elections = new ArrayList<>();

        // for each element in the given json array, add the object to the elections array
        for (int i = 0; i < electionsJSONArray.length(); i++) {
            elections.add(new Election(electionsJSONArray.getJSONObject(i)));
        }
        return elections;
    }

    public static String getSimpleDate(String rawJsonDate) {
        String apiFormat = "yyyy-MM-dd";
        String newFormat = "MMMM d, yyyy";
        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat(apiFormat);
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat(newFormat);
        String resultDate = "";
        try {
            resultDate=outputFormat.format(inputFormat.parse(rawJsonDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("Election", "newDate: " + resultDate);
        return resultDate;
    }

    public static String getShortDate(String rawJsonDate) {
        String apiFormat = "yyyy-MM-dd";
        String newFormat = "MMMM d";
        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat(apiFormat);
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat(newFormat);
        String resultDate = "";
        try {
            resultDate=outputFormat.format(inputFormat.parse(rawJsonDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("Election", "newDate: " + resultDate);
        return resultDate;
    }

    public String getName() {
        return name;
    }

    public String getSimpleElectionDay() {
        return getSimpleDate(electionDay);
    }

    public String getShortElectionDay() {
        return getShortDate(electionDay);
    }

    public Integer getId() {
        return id;
    }

    public String getDivision() {
        return division;
    }
}
