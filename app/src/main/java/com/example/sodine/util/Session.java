package com.example.sodine.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUser(String username) {
        prefs.edit().putString("username", username).apply();
    }

    public String getUser() {
        return prefs.getString("username","");
    }

    public void setJWT(String jwt) {
        prefs.edit().putString("jwt", jwt).apply();
    }

    public String getJWT() {
        return prefs.getString("jwt","");
    }

    public void clearSession(Context ctx)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }
}
