package com.example.votingapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Contest {
    String ballotTitle;
    String level;
    String district;
    List<Candidate> candidates;

    public Contest() {
    }

    public Contest(JSONObject json) {
        try {
            ballotTitle = json.getString("ballotTitle");
            level = json.getJSONArray("level").get(0).toString();
            district = json.getJSONObject("district").getString("name");
            candidates = Candidate.fromJSON(json.getJSONArray("candidates"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static List<Contest> fromJSON(JSONArray array) throws JSONException {
        List<Contest> contests = new ArrayList<>();
        for (int i = 0 ; i< array.length(); i++) {
            contests.add(new Contest(array.getJSONObject(i)));
        }
        return contests;
    }

    public String getBallotTitle() {
        return ballotTitle;
    }

    public String getLevel() {
        return level;
    }

    public String getDistrict() {
        return district;
    }

    public List<Candidate> getCandidates() {
        if (candidates == null) {
            return new ArrayList<>();
        }
        return candidates;
    }


}
