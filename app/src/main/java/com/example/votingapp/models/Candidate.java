package com.example.votingapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Candidate {
    String name;
    String party;
    String photoUrl;

    public Candidate() {}

    public Candidate(JSONObject json) {
        try {
            name = json.getString("name");
            party = json.getString("party");
            photoUrl = json.getString("photoUrl");
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public static List<Candidate> fromJSON(JSONArray array) throws JSONException {
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 0 ; i< array.length(); i++) {
            candidates.add(new Candidate(array.getJSONObject(i)));
        }
        return candidates;
    }
}
