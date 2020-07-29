package com.example.votingapp.models;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class User {
    public static final String KEY_PROFILEPIC = "profilePic";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS1 = "addressLine1";
    public static final String KEY_CITY = "addressCity";
    public static final String KEY_STATE = "addressState";
    public static final String KEY_ZIP = "addressZip";
    private static final String KEY_LOCATION_WEIGHTS = "locationPreferences";
    private static final String TAG = "User";

    public static String getAddress(ParseUser user) {
        String add1 = user.getString(KEY_ADDRESS1);
        String city = user.getString(KEY_CITY);
        String state = user.getString(KEY_STATE);
        String zip = user.getString(KEY_ZIP);
        return add1 + ", " + city + ", " + state + " " + zip;
    }

    public static Integer getLocationPreference(ParseUser user, Integer index) {
        JSONArray jsonWeights = user.getJSONArray(KEY_LOCATION_WEIGHTS);
        try {
            return jsonWeights.getInt(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<String> getPreferenceOrder(ParseUser user) throws JSONException {
        JSONArray jsonWeights = user.getJSONArray(KEY_LOCATION_WEIGHTS);
        List<String> preferenceOrder = new ArrayList<>();
        preferenceOrder.add(" ");
        preferenceOrder.add(" ");
        preferenceOrder.add(" ");
        for (int i = 0; i<jsonWeights.length(); i++) {
            Integer weight = jsonWeights.getInt(i);
            if (weight == 1) {
                preferenceOrder.set(2, Location.LOCATION_NAMES[i]);
            } else if (weight == 3) {
                preferenceOrder.set(1, Location.LOCATION_NAMES[i]);
            } else {
                preferenceOrder.set(0, Location.LOCATION_NAMES[i]);
            }
        }
        return preferenceOrder;
    }

    public static List<Integer> getPreferenceWeights(ParseUser user) {
        JSONArray jsonWeights = user.getJSONArray(KEY_LOCATION_WEIGHTS);
        List<Integer> weight = new ArrayList<>();
        for (int i = 0; i < jsonWeights.length(); i++) {
            try {
                weight.add(jsonWeights.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return weight;
    }

    public static void setWeights(ParseUser user, List<Integer> weights) {
        user.put(KEY_LOCATION_WEIGHTS, weights);
    }

}
