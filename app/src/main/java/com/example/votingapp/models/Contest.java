package com.example.votingapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Contest {
    String ballotTitle;
    String level;
    String district;
    List<Candidate> candidates;

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
        return candidates;
    }


}

class Candidate {
    String name;
    String party;

    public Candidate(JSONObject json) {
        try {
            name = json.getString("name");
            party = json.getString("party");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public static List<Candidate> fromJSON(JSONArray array) throws JSONException {
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 0 ; i< array.length(); i++) {
            candidates.add(new Candidate(array.getJSONObject(i)));
        }
        return candidates;
    }
}
