package com.example.votingapp.models;

import com.example.votingapp.MethodLibrary;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.votingapp.MethodLibrary.API_DATE_FORMAT;
import static com.example.votingapp.MethodLibrary.NUMBER_DATE_FORMAT;
import static com.example.votingapp.MethodLibrary.getFormattedDate;

public class Location {
    String name;
    String address;
    String startDate;
    String endDate;
    String pollingHours;
    String type;
    String notes;
    LatLng latLng;

    public Location(JSONObject jsonObject, String type) {
        try {
            this.type = type;
            JSONObject addObj = jsonObject.getJSONObject("address");
            name = addObj.getString("locationName");
            address = MethodLibrary.parseAddress(addObj);
            startDate = jsonObject.getString("startDate");
            endDate = jsonObject.getString("endDate");
            pollingHours = jsonObject.getString("pollingHours");
            notes = jsonObject.getString("notes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public double getLatitude() {
        return latLng.latitude;
    }
    public double getLongitude() {
        return latLng.longitude;
    }
}
