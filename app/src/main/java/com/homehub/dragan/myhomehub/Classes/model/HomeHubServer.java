package com.homehub.dragan.myhomehub.Classes.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.homehub.dragan.myhomehub.Activities.LoginActivity;
import com.homehub.dragan.myhomehub.R;


public class HomeHubServer {

    public static HomeHubServer newInstance(SharedPreferences mSharedPref) {
        String baseUrl = mSharedPref.getString(LoginActivity.EXTRA_FULL_URI, null);
        String password = mSharedPref.getString(LoginActivity.EXTRA_PASSWORD, null);
        HomeHubServer instance = new HomeHubServer(baseUrl, password);
        return instance;
    }


    public HomeHubServer(String baseurl, String password) {
        //this.connectionId = 1;
        this.name = "MyHomeHub";
        this.baseurl = baseurl;
        this.password = password;
    }

    @SerializedName("connectionId")
    public Integer connectionId;

    @SerializedName("name")
    public String name;

    @SerializedName("baseurl")
    public String baseurl;

    @SerializedName("password")
    public String password;

    @Override
    public String toString() {
        return (new Gson()).toJson(this);
    }

    public String getName() {
        return name;
    }

    public Uri getBaseUri() {
        return Uri.parse(baseurl);
    }

    public String getBaseUrl() {
        String result = baseurl;
        if (result != null) {
            if (result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }

    public String getPassword() {
        return password;
    }

    public String getBearerHeader() {
        return "Bearer " + password;
    }

    public String getWebsocketUrl() {
        return "ws" + getBaseUrl().substring(4) + "/api/websocket";
    }


    public CharSequence getLine(Context context) {
        Spannable wordtoSpan1 = new SpannableString(getName());
        wordtoSpan1.setSpan(new RelativeSizeSpan(1.2f), 0, wordtoSpan1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Spannable wordtoSpan2 = new SpannableString(baseurl);
        wordtoSpan2.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null)), 0, wordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan2.setSpan(new RelativeSizeSpan(0.8f), 0, wordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return TextUtils.concat(wordtoSpan1, " \n", wordtoSpan2);
    }

    public static HomeHubServer getInstance(Cursor cursor) {
        HomeHubServer server = null;
        try {

            String baseUrl = cursor.getString(cursor.getColumnIndex("BASE_URL"));
            String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
            server = new HomeHubServer(baseUrl, password);
            server.connectionId = cursor.getInt(cursor.getColumnIndex("CONNECTION_ID"));
            server.name = cursor.getString(cursor.getColumnIndex("CONNECTION_NAME"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return server;
    }
}
