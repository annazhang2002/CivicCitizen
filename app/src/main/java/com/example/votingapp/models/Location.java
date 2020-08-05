package com.example.votingapp.models;

import android.util.Log;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.votingapp.MethodLibrary;
import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.fragments.LocationsFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static com.example.votingapp.MethodLibrary.API_DATE_FORMAT;
import static com.example.votingapp.MethodLibrary.NUMBER_DATE_FORMAT;
import static com.example.votingapp.MethodLibrary.getFormattedDate;
import static com.example.votingapp.Network.DISTANCE_MATRIX_URL;

public class Location implements Comparable<Location>{
    public static final String[] LOCATION_NAMES = {"Early Voting Site", "Drop Off Location", "Polling Location"};
    private static final String TAG = "Locations";

    String name;
    String address;
    String startDate;
    String endDate;
    String pollingHours;
    String type;
    Integer typeIndex;
    String notes;
    LatLng latLng;
    Integer pillColor;
    Integer weight = 0;
    Long distanceHome;
    Marker marker;
    JSONObject addObj;
    Integer locationPreferWeight;

    public Location(JSONObject jsonObject, String type1) {
        try {
            type = type1;
            addObj = jsonObject.getJSONObject("address");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            name = addObj.getString("locationName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            address = MethodLibrary.parseAddress(addObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            startDate = jsonObject.getString("startDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            endDate = jsonObject.getString("endDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            pollingHours = jsonObject.getString("pollingHours");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pillColor = setPillColor(type);
        try {
            notes = jsonObject.getString("notes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Integer calculateWeight() {
        long distance = distanceHome;
        locationPreferWeight = User.getLocationPreference(ParseUser.getCurrentUser(), typeIndex);
        return (int) (distance / locationPreferWeight);
    }

    public Integer setPillColor(String type) {
        if (type.equals("Polling Location")) {
            typeIndex = 2;
            return R.color.pill_polling;
        } else if (type.equals("Drop Off Location")) {
            typeIndex = 1;
            return  R.color.pill_dropoff;
        } else {
            typeIndex = 0;
            return R.color.pill_early;
        }
    }

    public Integer getPillColor() {
        return pillColor;
    }

    public static List<Location> fromJSON(JSONArray json, String type) throws JSONException {
        List<Location> locations = new ArrayList<>();
        for (int i = 0 ; i< json.length(); i++) {
            locations.add(new Location(json.getJSONObject(i), type) );
        }
        return locations;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getStartDate() {
        return startDate;
//        return getFormattedDate(startDate, API_DATE_FORMAT, NUMBER_DATE_FORMAT);
    }

    public String getEndDate() {
        return endDate;
//        return getFormattedDate(endDate, API_DATE_FORMAT, NUMBER_DATE_FORMAT);
    }

    public String getPollingHours() {
        return pollingHours;
    }

    public String getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }

    public void setLatLng(double lat, double lng) {
        latLng = new LatLng(lat, lng);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Integer getWeight() {
        return weight;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Integer getTypeIndex() {
        return typeIndex;
    }

    public Long getDistanceHome() {
        return distanceHome;
    }

    public void setDistanceHome(Long distanceHome) {
        this.distanceHome = distanceHome;
        weight = calculateWeight();
    }

    public Integer getLocationPreferWeight() {
        return locationPreferWeight;
    }

    public void setLocationPreferWeight(Integer locationPreferWeight) {
        this.locationPreferWeight = locationPreferWeight;
    }

    @Override
    public int compareTo(Location location) {
//        Log.i(TAG, location.getName() + "'s weight: " + location.getWeight() + " and " + name + "'s weight: " + weight);
        return weight.compareTo(location.getWeight());
    }
}
