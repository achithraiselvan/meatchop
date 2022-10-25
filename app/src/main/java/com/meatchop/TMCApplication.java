package com.meatchop;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class TMCApplication extends Application {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;


    @Override
    public void onCreate() {
        Log.v("TMCApplication", "onCreate()"); // NO I18N
        super.onCreate();
        sAnalytics = GoogleAnalytics.getInstance(this);
     // new FlurryAgent.Builder()
     //         .withLogEnabled(true)
     //         .build(this, "2RGGSS7482VZH3SBPK35");

    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }


}
