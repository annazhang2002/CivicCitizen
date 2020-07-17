package com.example.votingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.votingapp.fragments.ComposeDialogFragment;
import com.example.votingapp.models.Rep;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MethodLibrary {
    private static final String TAG = "MethodLibrary";

    public static final String FACEBOOK_BASE_URL = "https://www.facebook.com/";
    public static final String TWITTER_BASE_URL = "https://twitter.com/";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/";
    public static final String MAPS_BASE_URL = "http://maps.google.co.in/maps?q=";

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

    public static void openDialer(String phone, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    public static void sendEmail(String emailTo, String subject, String body, PackageManager packageManager, Context context) {
        String uriText =
                "mailto:" + emailTo +
                        "?subject=" + Uri.encode(subject) +
                        "&body=" + Uri.encode(body);

        Uri uri = Uri.parse(uriText);

        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uri);
        if (sendIntent.resolveActivity(packageManager) != null) {
            context.startActivity(Intent.createChooser(sendIntent, "Send email"));
        }
    }

    public static void showRepMessageDialog(FragmentManager fragmentManager, Context context, PackageManager packageManager, Rep rep) {
        FragmentManager fm = fragmentManager;
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(context, packageManager);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Rep.class.getSimpleName(), Parcels.wrap(rep));
        composeDialogFragment.setArguments(bundle);
        composeDialogFragment.show(fm, "fragment_compose_dialog");
    }

    public static String parseAddress(JSONObject addObj) throws JSONException {
        return addObj.getString("line1") + ", " + addObj.getString("city") + ", " + addObj.getString("state") + " " + addObj.getString("zip");
    }
}
