package com.example.votingapp.models;

import com.parse.Parse;
import com.parse.ParseUser;

public class User {
    private static final String KEY_ADDRESS_LINE1 = "addressLine1";
    private static final String KEY_ADDRESS_CITY = "addressCity";
    private static final String KEY_ADDRESS_STATE = "addressState";
    private static final String KEY_ADDRESS_ZIP = "addressZip";

    public static String getAddress(ParseUser user) {
        String add1 = user.getString(KEY_ADDRESS_LINE1);
        String city = user.getString(KEY_ADDRESS_CITY);
        String state = user.getString(KEY_ADDRESS_STATE);
        String zip = user.getString(KEY_ADDRESS_ZIP);
        return add1 + ", " + city + ", " + state + " " + zip;
    }
}
