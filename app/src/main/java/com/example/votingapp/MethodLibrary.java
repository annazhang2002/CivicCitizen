package com.example.votingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.fragment.app.FragmentManager;

import com.example.votingapp.fragments.dialogFragments.ComposeDialogFragment;
import com.example.votingapp.models.Rep;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MethodLibrary {
    private static final String TAG = "MethodLibrary";

    public static final String FACEBOOK_BASE_URL = "https://www.facebook.com/";
    public static final String TWITTER_BASE_URL = "https://twitter.com/";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/";
    public static final String MAPS_BASE_URL = "http://maps.google.co.in/maps?q=";

    public static final String API_DATE_FORMAT = "yyyy-MM-dd";
    public static final String M_D_Y_FORMAT = "MMMM d, yyyy";
    public static final String M_D_FORMAT = "MMMM d";
    public static final String NUMBER_DATE_FORMAT = "MM/dd";
    public static final String DATE_OBJ_FORMAT = "EEE MMM dd hh:mm:ss zzz yyyy";

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

    public static void shareContentHTML(Context context, String html) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(html));
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
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

    public static String parseAddressDistanceMatrix(JSONObject addObj) throws JSONException {
        return addObj.getString("line1") + ", " + addObj.getString("city") + ", " + addObj.getString("state") + " " + addObj.getString("zip");
    }

    public static String getTodayActionDate() {
        Date c = Calendar.getInstance().getTime();
        return getFormattedDate(c.toString(), DATE_OBJ_FORMAT, M_D_Y_FORMAT);

    }

    public static void pushNotification(String receiverId, String message) {
        HashMap<String, String> params = new HashMap<>();
        params.put("receiverId", receiverId);
        params.put("message", message);

        try {
            Log.i(TAG, "trying to push notification to " + receiverId + " with message :" + message + ": from " + ParseUser.getCurrentUser().getObjectId());
            ParseCloud.callFunctionInBackground("sendPushNotification", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object object, com.parse.ParseException e) {
                    if (e !=  null) {
                        Log.i(TAG, "Error with the return from parse cloud " + e.toString());
                    } else {
                        Log.i(TAG, "Successfully completed!");
                    }

                }
            });
        } catch (Exception e) {
            Log.i(TAG, "unable to push notif: " + e);
            e.printStackTrace();
        }
    }

    public static void testNotification() {

//        JSONObject payload = new JSONObject();
//
//        try {
//            Log.i(TAG, "installation id: " + ParseInstallation.getCurrentInstallation().getInstallationId());
//            payload.put("sender", ParseInstallation.getCurrentInstallation().getInstallationId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        HashMap<String, String> data = new HashMap<>();
//        data.put("customData", payload.toString());
//
//        ParseCloud.callFunctionInBackground("pingReply", data);

        HashMap<String, String> params = new HashMap<>();
        params.put("customData", "hello this is custom data");

        try {
            Log.i(TAG, "trying to push notification");
            ParseCloud.callFunctionInBackground("pushChannelTest", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object object, com.parse.ParseException e) {
                    if (e !=  null) {
                        Log.i(TAG, "Error with the return from parse cloud " + e.toString());
                    } else {
                        Log.i(TAG, "Successfully completed! " + object.toString());

                    }

                }
            });
        } catch (Exception e) {
            Log.i(TAG, "unable to push notif: " + e);
            e.printStackTrace();
        }
    }



}
