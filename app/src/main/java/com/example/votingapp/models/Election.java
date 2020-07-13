package com.example.votingapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

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

    public String getName() {
        return name;
    }

    public String getElectionDay() {
        return electionDay;
    }

    public Integer getId() {
        return id;
    }

    public String getDivision() {
        return division;
    }
}
