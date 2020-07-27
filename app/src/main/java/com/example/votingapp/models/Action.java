package com.example.votingapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Action")
public class Action extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_ELECTION_ID = "electionId";
    public static final String KEY_NAME = "name";
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATE = "dateCompleted";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NOTES = "notes";


    public Action() {}

    // getters for the actions
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public int getElectionId() {
        return getInt(KEY_ELECTION_ID);
    }
    public String getName() {
        return getString(KEY_NAME);
    }
    public String getStatus() {
        return getString(KEY_STATUS);
    }
    public String getDate() {
        return getString(KEY_DATE);
    }
    public String getNotes() {
        return getString(KEY_NOTES);
    }
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    // setters for the actions
    public void setUser(ParseUser user) {put(KEY_USER, user);}
    public void setElectionId(Integer id) {put(KEY_ELECTION_ID, id);}
    public void setName(String name) {put(KEY_NAME, name);}
    public void setStatus(String status) {put(KEY_STATUS, status);}
    public void setDate(String date) {put(KEY_DATE, date);}
    public void setImage(ParseFile image) {put(KEY_IMAGE, image);}
    public void setNotes(String notes) {put(KEY_NOTES, notes);}
}
