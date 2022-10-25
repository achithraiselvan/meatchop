package com.meatchop.activities;


import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.meatchop.R;
import com.meatchop.services.CurrentLocationService;
import com.meatchop.services.TMCMessagingService;
import com.meatchop.utils.SettingsUtil;

import io.sentry.Sentry;


public class MainActivity extends BaseActivity {

    private static final int LOGIN_ACT_REQ_CODE = 0;
    private static final int HOMESCREEN_ACT_REQ_CODE = 1;

    private String TAG = "MainActivity";
    private Handler splashHandler;
    private String tmcSubctgykey;
    private String tmcSubctgyname;
    private String tmcctgyname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View nonetwork_view = findViewById(R.id.no_network_view);
        View nointernetretry_btn = findViewById(R.id.nointernetretry_btn);
        // Sentry.captureMessage("testing SDK setup");

     // startService(new Intent(MainActivity.this, TMCMessagingService.class));
        clearNotifications();

        tmcSubctgykey = getIntent().getStringExtra("tmcsubctgykey");
        tmcSubctgyname = getIntent().getStringExtra("tmcsubctgyname");
        tmcctgyname = getIntent().getStringExtra("tmcctgyname");

        isHomeScreenActivityCalled = false;

        nointernetretry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nonetwork_view.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (isOnline()) {
                            nonetwork_view.setVisibility(View.GONE);
                            splashHandler.postDelayed(new Runnable() {
                                public void run() {
                                    initializeAWSMobileClient();
                                }
                            }, 200);
                        } else {
                            nonetwork_view.setVisibility(View.VISIBLE);
                        }
                    }
                }, 1000);
            }
        });

        splashHandler = new Handler();
        if (!isOnline()) {
            nonetwork_view.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "starthomescreenactivity method");
            nonetwork_view.setVisibility(View.GONE);
            splashHandler.postDelayed(new Runnable() {
                public void run() {
                    initializeAWSMobileClient();
                    Log.d(TAG, "splashHandler startHomeScreenActivity");
                }
            }, 200);

        }
    }

    private void initializeAWSMobileClient() {
        AWSMobileClient awsMobileClient = AWSMobileClient.getInstance();
        awsMobileClient.initialize(getApplicationContext(), new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                        Log.d("INIT", "onResult: " + userStateDetails.getUserState());
                     /* if (awsMobileClient.isSignedIn()) {
                            startHomeScreenActivity();
                        } else {
                            startLoginActivity();
                        }  */
                        startHomeScreenActivity();

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("INIT", "Initialization error.", e);
                    }
                }
        );
    }

 /* private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("isCallFromMainActivity", true);
        startActivityForResult(intent, LOGIN_ACT_REQ_CODE);
     // overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
        overridePendingTransition(0, 0);
    } */

    private boolean isHomeScreenActivityCalled = false;
    private void startHomeScreenActivity() {

        Log.d(TAG, "startHomeScreenActivity method");
        if (isHomeScreenActivityCalled) {
            return;
        }
        isHomeScreenActivityCalled = true;
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        if (tmcSubctgyname != null) {
            intent.putExtra("tmcSubctgyname", tmcSubctgyname);
        }
        if (tmcSubctgykey != null) {
            intent.putExtra("tmcSubctgykey", tmcSubctgykey);
        }
        if (tmcctgyname != null) {
            intent.putExtra("tmcctgyname", tmcctgyname);
        }
        intent.putExtra("showlandingpage", true);
        startActivityForResult(intent, HOMESCREEN_ACT_REQ_CODE);
     // overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
        overridePendingTransition(0, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOGIN_ACT_REQ_CODE :
                boolean isStartHomeActivity = data.getBooleanExtra("isstarthomeactivity", false);
                if (isStartHomeActivity) {
                    if (AWSMobileClient.getInstance().isSignedIn()) {
                        isHomeScreenActivityCalled = false;
                        startHomeScreenActivity();
                        return;
                    }
                } else {
                    finish();
                }

                break;

            case HOMESCREEN_ACT_REQ_CODE :
                Log.d(TAG, "HOMESCREEN_ACT_REQ_CODE onActivity Result");
             /* if (AWSMobileClient.getInstance().isSignedIn()) {
                    finish();
                } else {
                    startLoginActivity();
                } */
                ActivityCompat.finishAffinity(MainActivity.this);
             // finish();
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearNotifications() {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
