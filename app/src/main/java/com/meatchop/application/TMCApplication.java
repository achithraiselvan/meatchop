
package com.meatchop.application;


import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


public class TMCApplication extends Application {

    private static final String TAG = "TheMeatChop"; // NO I18N
    private static Context context;


    /**
     * Logging tag.
     */
    @Override
    public void onCreate() {
        Log.v(TMCApplication.TAG, "onCreate()"); // NO I18N
        super.onCreate();


        // Fabric.with(this, new Crashlytics());

    }


    private void setContext(Context c) { context = c; }
    public static Context getContext() { return context; }

    @Override
    public void onTerminate() {
        Log.v(TMCApplication.TAG, "onTerminate()");  // NO I18N
        super.onTerminate();
    }

}
