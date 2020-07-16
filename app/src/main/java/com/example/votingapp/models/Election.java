package com.example.votingapp.models;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Parcel
public class Election {

    public static final int REGISTER_DAYS_BEFORE = -30;
    private static final int ABSENTEE_DAYS_BEFORE = -7;
    public static final String API_DATE_FORMAT = "yyyy-MM-dd";
    public static final String M_D_Y_FORMAT = "MMMM d, yyyy";
    public static final String M_D_FORMAT = "MMMM d, yyyy";
    public static final String NUMBER_DATE_FORMAT = "MM/dd";
    private static final String TAG = "Election";
    String name;
    String electionDay;
    int id;
    String division;

    // no-arg, empty constructor required for Parceler
    public Election() {}

    public Election(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        electionDay = jsonObject.getString("electionDay");
        id = jsonObject.getInt("id");
        division = jsonObject.getString("ocdDivisionId");
    }
//
//    public static List<Election> fromJsonArray(JSONArray electionsJSONArray) throws JSONException {
//        // create a list of elections we get
//        List<Election> elections = new ArrayList<>();
//
//        // for each element in the given json array, add the object to the elections array
//        for (int i = 0; i < electionsJSONArray.length(); i++) {
//            elections.add(new Election(electionsJSONArray.getJSONObject(i)));
//        }
//        return elections;
//    }

    // method to get the deadline date given an input date and the number of days you want to subtract
    public static String getDaysBeforeDate(String rawDate, int daysBefore, String givenFormat, String newFormat ) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(givenFormat, Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(rawDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE,daysBefore);
        String wantedDate = sdf.format(cal.getTime());
        wantedDate = getFormattedDate(wantedDate, givenFormat, newFormat);
        Log.d(TAG, wantedDate);
        return wantedDate;
    }

    public static String getFormattedDate(String rawJsonDate, String oldFormat, String newFormat) {
        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat(oldFormat);
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat(newFormat);
        String resultDate = "";
        try {
            resultDate=outputFormat.format(inputFormat.parse(rawJsonDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "newDate: " + resultDate);
        return resultDate;
    }

    public String getName() {
        return name;
    }

    public String getRegisterDeadline() {
        return getDaysBeforeDate(electionDay, REGISTER_DAYS_BEFORE, API_DATE_FORMAT, NUMBER_DATE_FORMAT);
    }

    public String getAbsenteeDeadline() {
        return getDaysBeforeDate(electionDay, ABSENTEE_DAYS_BEFORE, API_DATE_FORMAT, NUMBER_DATE_FORMAT);
    }

    public String getVoteDeadline() {
        return getFormattedDate(electionDay, API_DATE_FORMAT, NUMBER_DATE_FORMAT);
    }

    public String getSimpleElectionDay() {
        return getFormattedDate(electionDay, API_DATE_FORMAT, M_D_Y_FORMAT);
    }

    public String getShortElectionDay() {
        return getFormattedDate(electionDay, API_DATE_FORMAT, M_D_FORMAT);
    }

    public Integer getId() {
        return id;
    }

    public String getDivision() {
        return division;
    }
}
