package com.example.votingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MethodLibrary {
    private static final String TAG = "MethodLibrary";

    public static final String API_DATE_FORMAT = "yyyy-MM-dd";
    public static final String M_D_Y_FORMAT = "MMMM d, yyyy";
    public static final String M_D_FORMAT = "MMMM d, yyyy";
    public static final String NUMBER_DATE_FORMAT = "MM/dd";

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

    public static void openUrl(String url, Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }
}
