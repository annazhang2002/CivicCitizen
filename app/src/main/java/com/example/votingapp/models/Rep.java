package com.example.votingapp.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Rep extends Candidate {
    public static final String KEY_POSITION = "name";
    public static final String KEY_INDEX = "officialIndices";
    private static final String TAG = "Rep";

    String webUrl;
    String position;
    Integer officeIndex;

    public Rep(JSONObject person, String position, Integer officeIndex) {
        super(person);
        try {
            this.webUrl = person.getJSONArray("urls").get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.position = position;
        this.officeIndex = officeIndex;
    }

    public static List<Rep> fromJSON(JSONArray offices, JSONArray people) throws JSONException {
        List<Rep> reps = new ArrayList<>();
        for (int i=0 ; i<offices.length(); i++) {
            JSONObject office = offices.getJSONObject(i);
            String position = office.getString(KEY_POSITION);
            JSONArray indices = office.getJSONArray(KEY_INDEX);
            for (int j =0 ; j< indices.length(); j++) {
                Integer index = indices.getInt(j);
                Log.i(TAG, "rep #" + index + " is " + position);
                reps.add(new Rep(people.getJSONObject(index), position, index));
            }
        }
        return reps;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getPosition() {
        return position;
    }
}
