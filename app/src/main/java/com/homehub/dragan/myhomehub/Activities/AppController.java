package com.homehub.dragan.myhomehub.Activities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppController extends Application {
    private static AppController mInstance;
    private static SharedPreferences sharedPref;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public SharedPreferences getSharedPref() {
        if (sharedPref == null) {
            Log.d("Yo","NewSharedPref made");
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        return sharedPref;
    }
}
