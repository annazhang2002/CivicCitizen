package com.example.votingapp.models;

import com.parse.Parse;
import com.parse.ParseUser;

public class User {
    public static final String KEY_PROFILEPIC = "profilePic";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS1 = "addressLine1";
    public static final String KEY_CITY = "addressCity";
    public static final String KEY_STATE = "addressState";
    public static final String KEY_ZIP = "addressZip";

    public static String getAddress(ParseUser user) {
        String add1 = user.getString(KEY_ADDRESS1);
        String city = user.getString(KEY_CITY);
        String state = user.getString(KEY_STATE);
        String zip = user.getString(KEY_ZIP);
        return add1 + ", " + city + ", " + state + " " + zip;
    }
}
