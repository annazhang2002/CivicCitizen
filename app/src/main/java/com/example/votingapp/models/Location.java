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
    static Integer typeIndex;
    String notes;
    LatLng latLng;
    Integer pillColor;
    Integer weight;
    static Long distanceHome;
    Marker marker;
    JSONObject addObj;

    public Location(JSONObject jsonObject, String type1) {
        try {
            type = type1;
            addObj = jsonObject.getJSONObject("address");
            name = addObj.getString("locationName");
            address = MethodLibrary.parseAddress(addObj);
            startDate = jsonObject.getString("startDate");
            endDate = jsonObject.getString("endDate");
            pollingHours = jsonObject.getString("pollingHours");
            pillColor = setPillColor(type);
            notes = jsonObject.getString("notes");
            getDistanceFrom(address, User.getAddress(ParseUser.getCurrentUser()));
//            weight = calculateWeight();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Integer calculateWeight() {
        int locationPreferWeight;
//        long distance = distanceHome;
        long distance = 0;
        locationPreferWeight = User.getLocationPreference(ParseUser.getCurrentUser(), typeIndex);

        return locationPreferWeight + (int) distance;
    }

    public void parseDistance(long distance) {
        distanceHome = distance;
        weight = calculateWeight();
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
        return getFormattedDate(startDate, API_DATE_FORMAT, NUMBER_DATE_FORMAT);
    }

    public String getEndDate() {
        return getFormattedDate(endDate, API_DATE_FORMAT, NUMBER_DATE_FORMAT);
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

    public static Integer getTypeIndex() {
        return typeIndex;
    }


    public void getDistanceFrom(String origin, String destination) {
        RequestParams params = new RequestParams();
        params.put("origin", origin);
        params.put("destination", destination);
        Network.client.get(DISTANCE_MATRIX_URL + "key=" + Network.distanceMatrixApiKey, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    // retrieve the state object
                    Long distance = json.jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getLong("value");
                    parseDistance(distance);
                    LocationsFragment.sortLocations();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to getDistanceFrom, " + statusCode + ", " + response, throwable);
            }
        });
    }

    @Override
    public int compareTo(Location location) {
        Log.i(TAG, location.getName() + "'s weight: " + location.getWeight() + " and " + name + "'s weight: " + weight);
        return location.getWeight().compareTo(weight);
    }
}
