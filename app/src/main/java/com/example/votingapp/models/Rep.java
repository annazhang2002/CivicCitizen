package com.example.votingapp.models;

import android.util.Log;

import com.example.votingapp.MethodLibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Parcel
public class Rep extends Candidate {
    public static final String KEY_POSITION = "name";
    public static final String KEY_INDEX = "officialIndices";
    private static final String TAG = "Rep";

    String webUrl;
    String position;
    Integer officeIndex;
    String phone;
    String address;
    HashMap<String, String> channels;
    String email;

    public Rep() {
    }

    public Rep(JSONObject person, String position, Integer officeIndex) {
        super(person);
        try {
            this.webUrl = person.getJSONArray("urls").get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.position = position;
        this.officeIndex = officeIndex;
        try {
            this.address = MethodLibrary.parseAddress(person.getJSONArray("address").getJSONObject(0));
            this.phone = person.getJSONArray("phones").getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            channels = new HashMap<String, String>();
            JSONArray jsonChannels = person.getJSONArray("channels");
            for (int i = 0 ; i<jsonChannels.length(); i++) {
                String type = jsonChannels.getJSONObject(i).getString("type");
                String id = jsonChannels.getJSONObject(i).getString("id");
                channels.put(type, id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.email = person.getJSONArray("emails").getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<String, String> getChannels() {
        return channels;
    }

    public String getEmail() {
        return email;
    }
}
