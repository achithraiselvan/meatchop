package com.meatchop.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.data.TMCVendor;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCDataCatalog;
import com.meatchop.widget.TMCEditText;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import cz.msebera.android.httpclient.io.HttpMessageParser;


public class MoreActivity extends BaseActivity implements View.OnClickListener{

    private TMCTextView helloname_text;
    private View profile_layout;
    private View notifications_layout;
    private View myorders_layout;
    private View help_layout;
    private View loginlogout_layout;
    private View version_layout;
    private TMCTextView version_text;

    private BottomNavigationView bottomNavigationView;
    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private TMCTextView loginlogout_text;

    private static final int TMCSEARCH_ACT_REQCODE = 0;
    private static final int TMCHOME_ACT_REQCODE = 1;
    private static final int LOGIN_ACT_REQ_CODE = 2;
    private static final int VERIFYOTP_ACT_REQ_CODE = 3;
    private static final int HELP_ACT_REQ_CODE = 4;
    private static final int PROFILE_ACT_REQ_CODE = 5;
    private static final int TMCORDERS_ACT_REQ_CODE = 6;
    private static final int MANAGEADDRESS_ACT_REQ_CODE = 7;
    private static final int CONTACTUS_ACT_REQ_CODE = 8;

    private SettingsUtil settingsUtil;

    private View loginscreenmask_layout;
    private View loginscreen_layout;
    private ImageView loginimageview;
    private TMCEditText mobile_editext;
    private View logincontinuebutton_layout;
    private View manageaddress_layout;
    private View rateourapp_layout;
    private View rateusingoogle_layout;
    private ImageView accountscreenheader_image;
    private String accountscreenurl;
    private String googlebusinessurl;
    private View accountscreen_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_more);
        settingsUtil = new SettingsUtil(this);
        helloname_text = (TMCTextView) findViewById(R.id.helloname_text);
        profile_layout = findViewById(R.id.profile_layout);
        notifications_layout = findViewById(R.id.notifications_layout);
        myorders_layout = findViewById(R.id.myorders_layout);
        help_layout = findViewById(R.id.help_layout);
        loginlogout_layout = findViewById(R.id.loginlogout_layout);
        version_layout = findViewById(R.id.version_layout);
        version_text = (TMCTextView) findViewById(R.id.version_text);
        loginlogout_text = (TMCTextView) findViewById(R.id.loginlogout_text);
        manageaddress_layout = findViewById(R.id.manageaddress_layout);
        rateourapp_layout = findViewById(R.id.rateourapp_layout);
        rateusingoogle_layout = findViewById(R.id.rateusingoogle_layout);
        accountscreen_progressbar = findViewById(R.id.accountscreen_progressbar);
        accountscreenheader_image = (ImageView) findViewById(R.id.accountscreenheader_image);

        String versionname = getVersionname();
        if ((versionname != null) && !(TextUtils.isEmpty(versionname))) {
            version_text.setText("Version: " + versionname);
        }

        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout.setOnClickListener(this);

        boolean isLoggedIn = false;
        try {
            isLoggedIn = AWSMobileClient.getInstance().isSignedIn();
            if (isLoggedIn) {
                String mobile = settingsUtil.getMobile();
                String userkey = settingsUtil.getUserkey();
                if ((mobile == null) || (TextUtils.isEmpty(mobile))) {
                    AWSMobileClient.getInstance().signOut();
                    isLoggedIn = false;
                }
                if ((userkey == null) || (TextUtils.isEmpty(userkey))) {
                    AWSMobileClient.getInstance().signOut();
                    isLoggedIn = false;
                }
                String defaultvendor = settingsUtil.getDefaultVendor();
                TMCVendor tmcVendor = new TMCVendor(new JSONObject(defaultvendor));
                if (tmcVendor != null) {
                    accountscreenurl = tmcVendor.getAccountscreenurl();
                    googlebusinessurl = tmcVendor.getGooglebusinessurl();
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (isLoggedIn) {
            manageaddress_layout.setVisibility(View.VISIBLE);
            myorders_layout.setVisibility(View.VISIBLE);
            profile_layout.setVisibility(View.VISIBLE);
            loginlogout_text.setText(getResources().getString(R.string.logout));
        } else {
            manageaddress_layout.setVisibility(View.GONE);
            myorders_layout.setVisibility(View.GONE);
            profile_layout.setVisibility(View.GONE);
            loginlogout_text.setText(getResources().getString(R.string.login));
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.app_bar_more).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // handle desired action here
                // One possibility of action is to replace the contents above the nav bar
                // return true if you want the item to be displayed as the selected item

                if (item.getTitle().equals("HOME")) {
                 // finish();
                 // overridePendingTransition(0, 0);
                    startHomeScreenActivity();
                } else if (item.getTitle().equals("SEARCH")) {
                    startTMCSearchActivity();
                } else if (item.getTitle().equals("CART")) {

                } else if (item.getTitle().equals("ACCOUNT")) {

                } else if (item.getTitle().equals("SHARE US")) {
                    logEventInGAnalytics("Accounts Screen Share us clicked", "SHARE US", settingsUtil.getMobile());
                    String subject = getResources().getString(R.string.shareussubject_message);
                    String body = getResources().getString(R.string.shareusbody_message);
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                } else if (item.getTitle().equals("CONTACT US")) {
                    startContactusActivity();
                }
                return true;
            }
        });

        loginlogout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AWSMobileClient.getInstance().isSignedIn()) {
                    showTMCAlertForLogoutConfirmation(R.string.logoutconfirmation_title, R.string.logoutconfirmation_string);
                } else {
                    showLoginScreen();
                 // startLoginActivity();
                }

            }
        });

        myorders_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, TMCOrdersActivity.class);
                startActivityForResult(intent, TMCORDERS_ACT_REQ_CODE);
                overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
            }
        });

        manageaddress_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, ManageAddressActivity.class);
                startActivityForResult(intent, MANAGEADDRESS_ACT_REQ_CODE);
                overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
            }
        });

        loginscreenmask_layout = findViewById(R.id.loginscreenmask_layout);
        loginscreen_layout = findViewById(R.id.loginscreen_layout);
        loginimageview = findViewById(R.id.loginimageview);
        mobile_editext = findViewById(R.id.mobile_editext);
        logincontinuebutton_layout = findViewById(R.id.logincontinuebutton_layout);
        loginscreenmask_layout.setOnClickListener(this);
        logincontinuebutton_layout.setOnClickListener(this);
        loginscreen_layout.setOnClickListener(this);
        accountscreen_progressbar = findViewById(R.id.accountscreen_progressbar);

        help_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHelpActivity();
            }
        });

        profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProfileActivity();
            }
        });

        rateourapp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    logEventInGAnalytics("Rate Us in app button clicked", "RATE IN APP", settingsUtil.getMobile());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        rateusingoogle_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logEventInGAnalytics("Rate Us in google button clicked", "RATE IN GOOGLE", settingsUtil.getMobile());
                if ((googlebusinessurl != null) && (!TextUtils.isEmpty(googlebusinessurl))) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(googlebusinessurl)));
                }
            }
        });

        if ((accountscreenurl != null) && (!TextUtils.isEmpty(accountscreenurl))) {
            Glide.with(getApplicationContext())
                    .load(accountscreenurl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            accountscreen_progressbar.setVisibility(View.GONE);
                            accountscreenheader_image.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            accountscreen_progressbar.setVisibility(View.GONE);
                            accountscreenheader_image.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(accountscreenheader_image);
        } else {
            accountscreen_progressbar.setVisibility(View.GONE);
            accountscreenheader_image.setVisibility(View.VISIBLE);
        }

    }

    private void startProfileActivity() {
        Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
        startActivityForResult(intent, PROFILE_ACT_REQ_CODE);
        // overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startHelpActivity() {
        Intent intent = new Intent(MoreActivity.this, HelpActivity.class);
        startActivityForResult(intent, HELP_ACT_REQ_CODE);
        // overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startContactusActivity() {
        Intent intent = new Intent(MoreActivity.this, ContactusActivity.class);
        startActivityForResult(intent, CONTACTUS_ACT_REQ_CODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startHomeScreenActivity() {
        Intent intent = new Intent(MoreActivity.this, HomeScreenActivity.class);
        startActivityForResult(intent, TMCHOME_ACT_REQCODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startTMCSearchActivity() {
        Intent intent = new Intent(MoreActivity.this, TMCSearchActivity.class);
        startActivityForResult(intent, TMCSEARCH_ACT_REQCODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    public void onBackPressed() {
        if (loginscreen_layout.getVisibility() == View.VISIBLE) {
            loginscreen_layout.setVisibility(View.GONE);
            return;
        }
        startHomeScreenActivity();
     // finish();
     // overridePendingTransition(0, 0);
    }

    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(MoreActivity.this, title, message,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {

                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void showTMCAlertForLogoutConfirmation(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(MoreActivity.this, title, message,
                        R.string.ok_capt, R.string.cancel_capt,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                             // showLoadingAnim();
                                AWSMobileClient.getInstance().signOut();
                                loginlogout_text.setText(getResources().getString(R.string.login));
                                manageaddress_layout.setVisibility(View.GONE);
                                myorders_layout.setVisibility(View.GONE);
                                profile_layout.setVisibility(View.GONE);
                                settingsUtil.clearAllData();
                             // settingsUtil.setUserkey("");

                                TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
                                TMCMenuItemCatalog.getInstance().clear();
                                TMCDataCatalog.getInstance().clear();
                             // finish();
                             // setResult(RESULT_OK);
                             // overridePendingTransition(0, 0 );
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }


    private void showLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.VISIBLE);
                loadinganim_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.GONE);
                loadinganim_layout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.loginscreenmask_layout) {
            hideLoginScreen();
        } else if (id == R.id.logincontinuebutton_layout) {
            String text = mobile_editext.getText().toString();
            hideKeyboard(mobile_editext);
            if (TextUtils.isEmpty(text)) {
                showTMCAlert(R.string.invalidmobile_title, R.string.invalidemailorphoneno);
                return;
            }
            showLoadingAnim();
            String mobileno = text;
            if (mobileno.length() != 10) {
                showTMCAlert(R.string.invalidmobile_title, R.string.mobilenoinvalid_msg);
                hideLoadingAnim();
                return;
            }
            startVerifyOTPActivity(mobileno);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOGIN_ACT_REQ_CODE :
                if (AWSMobileClient.getInstance().isSignedIn()) {
                    manageaddress_layout.setVisibility(View.VISIBLE);
                    myorders_layout.setVisibility(View.VISIBLE);
                    profile_layout.setVisibility(View.VISIBLE);
                    loginlogout_text.setText(getResources().getString(R.string.logout));
                } else {
                    manageaddress_layout.setVisibility(View.GONE);
                    myorders_layout.setVisibility(View.GONE);
                    profile_layout.setVisibility(View.GONE);
                    loginlogout_text.setText(getResources().getString(R.string.login));
                }
                break;

            case VERIFYOTP_ACT_REQ_CODE :
                hideLoginScreen();
                hideLoadingAnim();
                if (AWSMobileClient.getInstance().isSignedIn()) {
                    manageaddress_layout.setVisibility(View.VISIBLE);
                    myorders_layout.setVisibility(View.VISIBLE);
                    profile_layout.setVisibility(View.VISIBLE);
                    loginlogout_text.setText(getResources().getString(R.string.logout));
                    TMCDataCatalog.getInstance().clear();
                    TMCMenuItemCatalog.getInstance().clear();
                 // TMCMenuItemCatalog.getInstance().clearMenuItems();
                } else {
                    manageaddress_layout.setVisibility(View.GONE);
                    myorders_layout.setVisibility(View.GONE);
                    profile_layout.setVisibility(View.GONE);
                    loginlogout_text.setText(getResources().getString(R.string.login));
                }
                break;

            case TMCORDERS_ACT_REQ_CODE :
                break;

            case MANAGEADDRESS_ACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean forcelogin = data.getBooleanExtra("forcelogin", false);
                        if (forcelogin) {
                            manageaddress_layout.setVisibility(View.GONE);
                            myorders_layout.setVisibility(View.GONE);
                            profile_layout.setVisibility(View.GONE);
                            loginlogout_text.setText(getResources().getString(R.string.login));
                            showLoginScreen();
                            hideLoadingAnim();
                            return;
                        }
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startVerifyOTPActivity(String mobileno) {
        Intent intent = new Intent(MoreActivity.this, VerifyOTPActivity.class);
        intent.putExtra("mobileno", mobileno);
        startActivityForResult(intent, VERIFYOTP_ACT_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    // Login Related Code
    private void showLoginScreen() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                loginscreenmask_layout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.loginbtn_anim_bottomup);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(200);
        loginscreen_layout.startAnimation(bottomUp);
        loginscreen_layout.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
    }

    private void hideLoginScreen() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                loginscreen_layout.setVisibility(View.GONE);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(200);
        loginscreen_layout.startAnimation(bottomDown);
        loginscreenmask_layout.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);

    }

    public void hideKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }

    private String getVersionname() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersioncode() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Tracker tmcTracker;
    private void logEventInGAnalytics(String category, String action, String label) {
        try {
            if (tmcTracker == null) {
                tmcTracker = ((TMCApplication) getApplication()).getDefaultTracker();
            }
            String label1 = label.substring(2);
            tmcTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action).setLabel(label1)
                    .build());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
