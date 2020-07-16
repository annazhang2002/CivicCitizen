package com.example.votingapp.models;

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

    public Location(JSONObject jsonObject, String type) {
        try {
            JSONObject addObj = jsonObject.getJSONObject("address");
            name = addObj.getString("locationName");
            address = addObj.getString("line1") + ", " + addObj.getString("city") + ", " + addObj.getString("state") + " " + addObj.getString("zip");
            startDate = jsonObject.getString("startDate");
            endDate = jsonObject.getString("endDate");
            pollingHours = jsonObject.getString("pollingHours");
            this.type = type;
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
}
