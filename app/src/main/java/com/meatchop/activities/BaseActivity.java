package com.meatchop.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.meatchop.R;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.widget.TMCMenuItemCatalog;

public class BaseActivity extends OrmLiteBaseActivity<DatabaseHelper> {


    protected boolean isReturnFromLoading = false;
    private DatabaseHelper helper = null;

    private boolean activityfinished = false;
    private SettingsUtil settingsUtil;

 /* protected DatabaseHelper getHelper() {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return helper;
    } */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsUtil = new SettingsUtil(this);
    }

    protected void slideFromLeft() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    protected void slideToLeft() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    protected void slideFromRight() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    protected void slideToRight() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    protected void slideFromBottom() {
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    protected void slideToBottom() {
        overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
    }
    protected int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (settingsUtil == null) {
            settingsUtil = new SettingsUtil(this);
        }
        if (!activityfinished) {
            settingsUtil.setLastAccessedTime(System.currentTimeMillis());
        } else {
            settingsUtil.setLastAccessedTime(0);
        }
    }

    @Override
    public void finish() {
        super.finish();
        activityfinished = true;
        overridePendingTransition(0, 0);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private static final int APP_IDLE_TIME = 180000;

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsUtil == null) {
            settingsUtil = new SettingsUtil(this);
        }
        long lastAccessedTime = settingsUtil.getLastAccessedTime();
        long idletime = System.currentTimeMillis() - lastAccessedTime;
        long appidletime = APP_IDLE_TIME;
     /* if ((settingsUtil.getLatestMerchantOrderId() != null) && (!TextUtils.isEmpty(settingsUtil.getLatestMerchantOrderId())) ) {
            appidletime = 600000;
        } */

        if ((lastAccessedTime > 0) && (idletime > appidletime)) {
            Log.d("BaseActivity", "cart and menu items cleared");
         // TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
            TMCMenuItemCatalog.getInstance().clear();
            startHomeActivity();
        }
     /* if ((lastAccessedTime > 0) && (idletime > appidletime)) {
            Log.d("BaseActivity", "cart and menu items cleared");
            TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
            startHomeActivity();
        } */
    }

    private void clearCartAndMenuItems() {
        try {
            SettingsUtil settingsUtil = new SettingsUtil(this);
         // settingsUtil.clearCart();
         // settingsUtil.clearSelectedVendorName();
         // getHelper().clearMenuItemTable();
         // MenuItemCatalog.getInstance().clear();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

}

