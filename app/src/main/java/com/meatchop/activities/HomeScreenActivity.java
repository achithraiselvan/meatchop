package com.meatchop.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.SphericalUtil;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.TMCTileRecyclerViewAdapter;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.Ordertrackingdetails;
import com.meatchop.data.TMCCtgy;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCSubCtgy;
import com.meatchop.data.TMCTile;
import com.meatchop.data.TMCUser;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.helpers.DatabaseManager;
import com.meatchop.models.Bannerdetails;
import com.meatchop.slider.EventBannerSliderAdapter;
import com.meatchop.sqlitedata.Orderdetailslocal;
import com.meatchop.sqlitedata.Ordertrackingdetailslocal;
import com.meatchop.sqlitedata.Ratingorderdetailslocal;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCDataCatalog;
import com.meatchop.widget.TMCEditText;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;
import com.smarteist.autoimageslider.SliderView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeScreenActivity extends BaseActivity implements View.OnClickListener {

    private static final int MOREACTIVITY_REQ_CODE = 0;
    private static final int TMCMENUACT_REQ_CODE = 1;
    private static final int LOCATIONSERVICES_REQ_CODE = 2;
    private static final int MEALKIT_REQ_CODE = 3;
    private static final int SEARCHACTIVITY_REQ_CODE = 4;
    private static final int VERIFYOTP_ACT_REQ_CODE = 5;
    private static final int ORDERSUMMARY_ACT_REQ_CODE = 6;
    private static final int TRACKORDER_ACT_REQ_CODE = 7;
    private static final int ORDERCONFIRMATION_ACT_REQ_CODE = 8;
    private static final int DETAILORDER_ACT_REQ_CODE = 9;
    private static final int CONTACTUS_ACT_REQ_CODE = 10;

    private static final int APPUPDATETYPE_OPTIONAL = 0;
    private static final int APPUPDATETYPE_MANDATORY = 1;

    private String TAG = "HomeScreenActivity";
    private SliderView sliderView;
    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private EventBannerSliderAdapter sliderAdapter;

    private ArrayList<Bannerdetails> bannerdetailsList;

    private BottomNavigationView bottomNavigationView;

    private LinearLayout detailimage_fields;

    private ArrayList<String> tmcCtgyNames;           // {String(TMCCtgy name)}
    private HashMap<String, TMCCtgy> tmcCtgyHashMap;  // {TMCCtgyName -- TMCCtgy}
    private View location_layout;

    private HorizontalScrollView banners_scrollview;

    private CardView chicken_layout;

    private RecyclerView tileRecyclerView;
    boolean isBottomNavigationMenuVisible = true;

    private boolean isGetAppDataResultReceived = false;
    private TMCTextView delivertoaddress_textview;
    private View selectedlocation_layout;
    private View chooselocation_layout;
    private SettingsUtil settingsUtil;
    private View loadingdesp_text;
 // private String selectedvendorkey;
 // private String selectedvendorname;
    private boolean islocationupdated = false;

    private View locationdetails_layout;
    private View locationloadinganim_layout;

    private View loginscreenmask_layout;
    private View loginscreen_layout;
    private ImageView loginimageview;
    private TMCEditText mobile_editext;
    private View logincontinuebutton_layout;
    private boolean isUserLoggedIn = false;
    private View cartitemcount_layout;
    private TMCTextView cartitemcount_text;

    private View trackorder_layout;
    private View trackorderloadinganim_layout;
    private TMCTextView orderstatus_desp;
    private boolean isShowOrderTrackingDetails = false;
    private TMCTextView calldesp_text;
    private View callbutton_layout;

    private String storemobileno;
    private String deliverypartnermobileno;

    private View bottombar_layout;

    private boolean isShowAppUpdateAlert = false;
    private int appupdatetype = APPUPDATETYPE_OPTIONAL;

    private View appupdatealert_layout;
    private TMCTextView releasenotes_desp;
    private View updateapp_btn;

    private View mandatoryappupdatealert_layout;
    private TMCTextView mandatoryreleasenotes_desp;
    private View mandatoryupdateapp_btn;
    private View appupdateanimmask_layout;
    private String mandatoryreleasenotes;

    private View bannerscreenmask_layout;
    private View banner_layout;
    private View bannerclose_btn;
    private ImageView banner_imageview;

    private Tracker tmcTracker;

    // Rating screen variables
    private View ratelastorder_layout;
    private View ratingmask_layout;
    private View ratingscreen_layout;
    private TMCTextView itemdetails_desp;
    private View rqlayout1;
    private View rqstar1;
    private View rqstar1_sel;
    private View rqlayout2;
    private View rqstar2;
    private View rqstar2_sel;
    private View rqlayout3;
    private View rqstar3;
    private View rqstar3_sel;
    private View rqlayout4;
    private View rqstar4;
    private View rqstar4_sel;
    private View rqlayout5;
    private View rqstar5;
    private View rqstar5_sel;

    private View rdlayout1;
    private View rdstar1;
    private View rdstar1_sel;
    private View rdlayout2;
    private View rdstar2;
    private View rdstar2_sel;
    private View rdlayout3;
    private View rdstar3;
    private View rdstar3_sel;
    private View rdlayout4;
    private View rdstar4;
    private View rdstar4_sel;
    private View rdlayout5;
    private View rdstar5;
    private View rdstar5_sel;
    private View ratingsubmit_btn;
    private TMCEditText feedbackdetails_edittext;
    private String selectedorderidforrating;
    private int selecteddeliveryrating = 0;
    private int selectedqualityrating = 0;
    private View feedbackclose_btn;

    private String userkey;
    private String vendorkey;
    private String lastplacedorderid;

    private String mappedvendorkey;
    private String mappedvendorname;
    private String lastMappedVendorName;
    private View applaunchimage_layout;

    private boolean isDataLoaded = false;
    private boolean loadtmcmenuitems = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
     // getSupportActionBar().hide();
        Toast.makeText(getApplicationContext(), "ac", Toast.LENGTH_LONG).show();
        setContentView(R.layout.home_screen_activity);
        settingsUtil = new SettingsUtil(this);

        String tmcSubctgykeyfromnotfcn = getIntent().getStringExtra("tmcSubctgykey");
        String tmcSubctgynamefromnotfcn = getIntent().getStringExtra("tmcSubctgyname");
        String tmcctgynamefromnotfcn = getIntent().getStringExtra("tmcctgyname");

        applaunchimage_layout = findViewById(R.id.applaunchimage_layout);
        boolean showlandingpage = getIntent().getBooleanExtra("showlandingpage", false);
        if (showlandingpage) {
            applaunchimage_layout.setVisibility(View.VISIBLE);
        } else {
            showLoadingAnimWithDesp();
            applaunchimage_layout.setVisibility(View.GONE);
        }

        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadingdesp_text = findViewById(R.id.loadingdesp_text);
        loadinganimmask_layout.setOnClickListener(this);
        bottombar_layout = findViewById(R.id.bottombar_layout);

        cartitemcount_layout = findViewById(R.id.cartitemcount_layout);
        cartitemcount_text = (TMCTextView) findViewById(R.id.cartitemcount_text);
        int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
        if (cartsize > 0) {
            cartitemcount_layout.setVisibility(View.VISIBLE);
            cartitemcount_text.setText("" + cartsize);
        } else {
            cartitemcount_layout.setVisibility(View.GONE);
        }

        location_layout = findViewById(R.id.location_layout);
        location_layout.setOnClickListener(this);
        // chicken_layout = (CardView) findViewById(R.id.chicken_layout);
        tileRecyclerView = findViewById(R.id.recyclerView);

        locationdetails_layout = findViewById(R.id.locationdetails_layout);
        locationloadinganim_layout = findViewById(R.id.locationloadinganim_layout);

        bottombar_layout = findViewById(R.id.bottombar_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.app_bar_home).setChecked(true);
        // disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // handle desired action here
                // One possibility of action is to replace the contents above the nav bar
                // return true if you want the item to be displayed as the selected item

                if (loadinganimmask_layout.getVisibility() == View.VISIBLE) {
                    return false;
                }
                if (item.getTitle().equals("HOME")) {

                } else if (item.getTitle().equals("SEARCH")) {
                    if (isShowAppUpdateAlert && (appupdatetype == APPUPDATETYPE_MANDATORY)) {
                        showMandatoryAppUpdateAlert();
                    } else {
                        startTMCSearchActivity();
                    }
                } else if (item.getTitle().equals("CART")) {

                } else if (item.getTitle().equals("ACCOUNT")) {
                    if (isShowAppUpdateAlert && (appupdatetype == APPUPDATETYPE_MANDATORY)) {
                        showMandatoryAppUpdateAlert();
                    } else {
                        startMoreActivity();
                    }
                } else if (item.getTitle().equals("SHARE US")) {
                    logEventInGAnalytics("Home Screen Share us clicked", "Share us", settingsUtil.getMobile());
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

        delivertoaddress_textview = findViewById(R.id.delivertoaddress_textview);
        selectedlocation_layout = findViewById(R.id.selectedlocation_layout);
        chooselocation_layout = findViewById(R.id.chooselocation_layout);

        loginscreenmask_layout = findViewById(R.id.loginscreenmask_layout);
        loginscreen_layout = findViewById(R.id.loginscreen_layout);
        loginimageview = findViewById(R.id.loginimageview);
        mobile_editext = findViewById(R.id.mobile_editext);
        logincontinuebutton_layout = findViewById(R.id.logincontinuebutton_layout);
        loginscreenmask_layout.setOnClickListener(this);
        logincontinuebutton_layout.setOnClickListener(this);
        loginscreen_layout.setOnClickListener(this);

     // showLoadingAnimWithDesp();
        try {
            isUserLoggedIn = AWSMobileClient.getInstance().isSignedIn();
        } catch (Exception ex) {

        }

        if (isUserLoggedIn) {
            String mobile = settingsUtil.getMobile();
            userkey = settingsUtil.getUserkey();
            if ((mobile == null) || (TextUtils.isEmpty(mobile))) {
                AWSMobileClient.getInstance().signOut();
                isUserLoggedIn = false;
            }
            if ((userkey == null) || (TextUtils.isEmpty(userkey))) {
                AWSMobileClient.getInstance().signOut();
                isUserLoggedIn = false;
            }
        }

        appupdatealert_layout = findViewById(R.id.appupdatealert_layout);
        releasenotes_desp = findViewById(R.id.releasenotes_desp);
        updateapp_btn = findViewById(R.id.updateapp_btn);

        mandatoryappupdatealert_layout = findViewById(R.id.mandatoryappupdatealert_layout);
        mandatoryreleasenotes_desp = (TMCTextView) findViewById(R.id.mandatoryreleasenotes_desp);
        mandatoryupdateapp_btn = findViewById(R.id.mandatoryupdateapp_btn);
        appupdateanimmask_layout = findViewById(R.id.appupdateanimmask_layout);
        appupdateanimmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        startSchedulerThreadForLandingPageHide();
        loadtmcmenuitems = true;
        getAppData();   // GetAppData ---->  initAddressAndVendorDetails

     /* if (tmctilessize <= 0) {
         // getTMCTileData();
            getTMCTileDetails("");
        } else {
            if (isUserLoggedIn) {
                String addressjson = settingsUtil.getDefaultAddress();
             // Log.d(TAG, "addressjson "+addressjson+" userkey "+settingsUtil.getUserkey());
                if ((addressjson != null) && (!TextUtils.isEmpty(addressjson))) {
                    fillLocationDetails(addressjson);
                } else {
                    getLocationDetails(settingsUtil.getUserkey());
                }
            }
            getVendorDetails("");
            tmcTileHashMap = TMCDataCatalog.getInstance().getTileMap();
            tmcTileArrayList = TMCDataCatalog.getInstance().getTMCTileList();
            createTMCTileRecyclerAdapter();
        } */

        // Intent data from Notification
        if ((tmcSubctgykeyfromnotfcn != null) && (tmcSubctgynamefromnotfcn != null)
              && (tmcctgynamefromnotfcn != null)) {
            String vendorkey = "";
            if ((mappedvendorkey == null) || (TextUtils.isEmpty(mappedvendorkey))) {
                vendorkey = settingsUtil.getDefaultVendorkey();
            } else {
                vendorkey = mappedvendorkey;
            }
            startTMCMenuItemActivity(vendorkey, tmcctgynamefromnotfcn, tmcSubctgynamefromnotfcn, tmcSubctgykeyfromnotfcn);
        }

        View cartbtn_layout = findViewById(R.id.cartbtn_layout);
        cartbtn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserLoggedIn && islocationupdated) {
                    int cartcount = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartcount > 0) {
                        startOrderSummaryActivity();
                    }
                }
            }
        });

        trackorder_layout = findViewById(R.id.trackorder_layout);
        trackorderloadinganim_layout = findViewById(R.id.trackorderloadinganim_layout);
        orderstatus_desp = (TMCTextView) findViewById(R.id.orderstatus_desp);
        calldesp_text = (TMCTextView) findViewById(R.id.calldesp_text);
        callbutton_layout = findViewById(R.id.callbutton_layout);
        callbutton_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "";
                if (ordertrackingdetails != null) {
                    status = ordertrackingdetails.getOrderstatus();
                }
                String mobileno = "";
                if (status.equalsIgnoreCase(Orderdetails.ORDERSTATUS_PICKEDUP)) {
                    mobileno = deliverypartnermobileno;
                } else {
                    mobileno = storemobileno;
                }

                if ((mobileno != null) && !(TextUtils.isEmpty(mobileno))) {
                    Intent callIntent = new Intent(
                            Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + mobileno)); // NO I18N
                    startActivity(callIntent);
                }
            }
        });

        lastplacedorderid = settingsUtil.getLastPlacedOrderId();
        trackorder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDetailOrderActivity();
            }
        });
        trackorderloadinganim_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailOrderActivity();
            }
        });

        initializeRatingViews();

        updateapp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPlayStore();
            }
        });

        mandatoryupdateapp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPlayStore();
            }
        });
        bannerscreenmask_layout = findViewById(R.id.bannerscreenmask_layout);
        banner_layout = findViewById(R.id.banner_layout);
        bannerclose_btn = findViewById(R.id.bannerclose_btn);
        banner_imageview = findViewById(R.id.banner_imageview);
        bannerscreenmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideBannerDetails();
            }
        });
        bannerclose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideBannerDetails();
            }
        });

     // checkOrderTrackingDetails();
     // calculateDistanceAndUpdateInAddress();  // One Time Only. Remove this code in next release

        String addressjson = settingsUtil.getDefaultAddress();
        try {
            if ((addressjson != null) && !(TextUtils.isEmpty(addressjson))) {
                TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(addressjson));
                if (tmcUserAddress.getDeliverydistance() <= 0) {
                    fetchDistanceFromAWS(tmcUserAddress);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String fcmtoken = settingsUtil.getFCMTOken();
        if ((fcmtoken == null) || (TextUtils.isEmpty(fcmtoken))) {
            generateFCMToken();
        }
        getReleaseHistoryAndCheckForAppUpdate();
    }

    private void initAddressAndVendorDetails() {
        try {
            if (isUserLoggedIn) {
                String defaultaddressjson = settingsUtil.getDefaultAddress();
                if ((defaultaddressjson != null) && (!TextUtils.isEmpty(defaultaddressjson))) {
                    TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(defaultaddressjson));
                    mappedvendorkey = tmcUserAddress.getVendorkey();
                    mappedvendorname = tmcUserAddress.getVendorname();
                    if (!settingsUtil.isFCMTokenTopicSubscribed()) {
                        subscribeToFCMTopic(mappedvendorname);
                    }
                    fillLocationDetails(defaultaddressjson);
                    getTMCTileDetails(mappedvendorkey);
                    getVendorDetails(mappedvendorkey);
                } else {
                    getLocationDetails(settingsUtil.getUserkey());
                             // Filllocationdetails, GetTMCTileDetails and getVendorDetails called in this method
                }
            } else {   // Location details will not be filled
                getTMCTileDetails(settingsUtil.getDefaultVendorkey());
                getVendorDetails(settingsUtil.getDefaultVendorkey());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkOrderTrackingDetails();
    }

    private void checkOrderTrackingDetails() {
        if (settingsUtil == null) {
            settingsUtil = new SettingsUtil(this);
        }
        if ((settingsUtil.getLastPlacedOrderId() != null) && !(TextUtils.isEmpty(settingsUtil.getLastPlacedOrderId())) ) {
            if (!isShowAppUpdateAlert) {
                trackorder_layout.setVisibility(View.VISIBLE);
                trackorderloadinganim_layout.setVisibility(View.VISIBLE);
                orderstatus_desp.setVisibility(View.GONE);
            }
            getOrderTrackingDetails(settingsUtil.getLastPlacedOrderId());
        } else {
            checkOrderForRatingView();
            trackorder_layout.setVisibility(View.GONE);
        }
    }

    private void startOrderSummaryActivity() {
        Intent intent = new Intent(HomeScreenActivity.this, OrderSummaryActivity.class);
        startActivityForResult(intent, ORDERSUMMARY_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

    private void startLocationServicesActivity() {
        Intent intent = new Intent(HomeScreenActivity.this, LocationServicesActivity.class);
        startActivityForResult(intent, LOCATIONSERVICES_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    private void startMoreActivity() {
        Intent intent = new Intent(HomeScreenActivity.this, MoreActivity.class);
        startActivityForResult(intent, MOREACTIVITY_REQ_CODE);
     // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startContactusActivity() {
        Intent intent = new Intent(HomeScreenActivity.this, ContactusActivity.class);
        startActivityForResult(intent, CONTACTUS_ACT_REQ_CODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startTMCSearchActivity() {
        Intent intent = new Intent(HomeScreenActivity.this, TMCSearchActivity.class);
        intent.putExtra("tmctilelist", tmcTileArrayList);
        intent.putExtra("islocationupdated", islocationupdated);
        startActivityForResult(intent, SEARCHACTIVITY_REQ_CODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startTMCMenuItemActivity(String vendorkey, String tmcctgyname, String tmcsubctgyname, String tmcsubctgykey) {
        if (isShowAppUpdateAlert && (appupdatetype == APPUPDATETYPE_MANDATORY)) {
            showMandatoryAppUpdateAlert();
            return;
        }
        Intent intent = new Intent(HomeScreenActivity.this, TMCMenuItemActivity.class);
        if ((vendorkey == null) || (TextUtils.isEmpty(vendorkey))) {
            return;
        }
        intent.putExtra("islocationupdated", islocationupdated);
        intent.putExtra("defaultvendorkey", vendorkey);
        intent.putExtra("tmcctgyname", tmcctgyname);
        intent.putExtra("tmcsubctgyname", tmcsubctgyname);
        intent.putExtra("tmcsubctgykey", tmcsubctgykey);
        intent.putExtra("vendorname", mappedvendorname);
        startActivityForResult(intent, TMCMENUACT_REQ_CODE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
     // overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        if (loginscreen_layout.getVisibility() == View.VISIBLE) {
            hideLoginScreen();
            hideLoadingAnim();
            return;
        }
        if (banner_layout.getVisibility() == View.VISIBLE) {
            hideBannerDetails();
            return;
        }
     // Log.d(TAG, "onBackPressed");
     // finish();
        confirmExitAlert();

     // ActivityCompat.finishAffinity(this);
     // overridePendingTransition(0, 0);
        return;
    }

    private void confirmExitAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(HomeScreenActivity.this, R.string.app_name, R.string.exitconfirmation_string,
                        R.string.ok_capt, R.string.cancel_capt,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                TMCMenuItemCatalog.getInstance().clear();
                                TMCDataCatalog.getInstance().clear();
                                ActivityCompat.finishAffinity(HomeScreenActivity.this);
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.location_layout) {
         // Log.d(TAG, "location_layout clicked");
            isUserLoggedIn = AWSMobileClient.getInstance().isSignedIn();
            if (isUserLoggedIn) {
                showLoadingAnim();
                startLocationServicesActivity();
            } else {
                showLoginScreen();
            }
        } else if (id == R.id.loginscreenmask_layout) {
            hideLoginScreen();
        } else if (id == R.id.logincontinuebutton_layout) {
            String text = mobile_editext.getText().toString();
            hideKeyboard(mobile_editext);
            if (TextUtils.isEmpty(text)) {
                showTMCAlert(R.string.invalidemailmobile_title, R.string.mobilenoinvalid_msg);
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
            case MOREACTIVITY_REQ_CODE :
             // Log.d(TAG, "onActivityResult MOREACTIVITY_REQ_CODE");
             // Log.d(TAG, "onActivityResult MOREACTIVITY_REQ_CODE signin aws " +AWSMobileClient.getInstance().isSignedIn());
                if (!AWSMobileClient.getInstance().isSignedIn()) {
                    finish();
                    setResult(RESULT_OK);
                 // ActivityCompat.finishAffinity(HomeScreenActivity.this);
                    return;
                }
                checkOrderTrackingDetails();
                if (bottomNavigationView != null) {
                    bottomNavigationView.getMenu().findItem(R.id.app_bar_home).setChecked(true);
                }
                break;

            case LOCATIONSERVICES_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean forcelogin = data.getBooleanExtra("forcelogin", false);
                        if (forcelogin) {
                            showLoginScreen();
                            hideLoadingAnim();
                            return;
                        }
                        boolean isaddressclicked = data.getBooleanExtra("addressclicked", false);
                        boolean isLocationUpdated = data.getBooleanExtra("isLocationUpdated", false);
                        if (isaddressclicked || isLocationUpdated) {
                            mappedvendorkey = data.getStringExtra("vendorkey");
                            String addressline1 = data.getStringExtra("addressline1");
                            String addressline2 = data.getStringExtra("addressline2");
                            mappedvendorname = data.getStringExtra("vendorname");

                            try {
                                TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
                                delivertoaddress_textview.setText(tmcUserAddress.getAddressline1() + ", " + tmcUserAddress.getAddressline2());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            islocationupdated = true;
                            chooselocation_layout.setVisibility(View.GONE);
                            selectedlocation_layout.setVisibility(View.VISIBLE);
                         // TMCMenuItemCatalog.getInstance().clearMenuItems();
                            TMCMenuItemCatalog.getInstance().clear();
                            TMCDataCatalog.getInstance().clear();
                            initAddressAndVendorDetails();
                        }
                        int cartcount = TMCMenuItemCatalog.getInstance().getCartCount();
                        if (cartcount <= 0) {
                            cartitemcount_layout.setVisibility(View.GONE);
                        } else {
                            cartitemcount_text.setText(cartcount + "");
                            cartitemcount_layout.setVisibility(View.VISIBLE);
                        }
                    }
                }
                hideLoadingAnim();
                break;

            case ORDERSUMMARY_ACT_REQ_CODE :
                int cartsize_ordersumm = TMCMenuItemCatalog.getInstance().getCartCount();
                if (cartsize_ordersumm > 0) {
                    cartitemcount_layout.setVisibility(View.VISIBLE);
                    cartitemcount_text.setText("" + cartsize_ordersumm);
                } else {
                    cartitemcount_layout.setVisibility(View.GONE);
                }
                break;

            case TMCMENUACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    if (!islocationupdated) {
                        String userkey = settingsUtil.getUserkey();
                        if ((userkey != null) && (!TextUtils.isEmpty(userkey))) {
                            getLocationDetails(userkey);
                        }
                    }
                    checkOrderTrackingDetails();
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartitemcount_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText("" + cartsize);
                    } else {
                        cartitemcount_layout.setVisibility(View.GONE);
                    }
                }
                break;

            case VERIFYOTP_ACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    boolean loginsuccess = data.getBooleanExtra("loginsuccess", false);
                 // Log.d(TAG, "VERIFYOTP_ACT_REQ_CODE loginsuccess "+loginsuccess);
                    isUserLoggedIn = AWSMobileClient.getInstance().isSignedIn();
                    if (loginsuccess) {
                        isUserLoggedIn = true;
                        startLocationServicesActivity();
                        hideLoginScreen();
                    }

                }
                break;

            case SEARCHACTIVITY_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartitemcount_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText("" + cartsize);
                    } else {
                        cartitemcount_layout.setVisibility(View.GONE);
                    }
                }

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createTMCTileRecyclerAdapter() {
     // ArrayList<TMCTile> tmcTiles = getTMCTileList();
        if ((tmcTileArrayList == null) || (tmcTileArrayList.size() <= 0)) { return; }
        Collections.sort(tmcTileArrayList);
        TMCTileRecyclerViewAdapter staggeredRecyclerViewAdapter = new TMCTileRecyclerViewAdapter(this, tmcTileArrayList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        tileRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        staggeredRecyclerViewAdapter.setHandler(createTileRecyclerHandler());
        tileRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int verticalscrolloffset = recyclerView.computeVerticalScrollOffset();
                if (verticalscrolloffset <= 120) {
                    showBottomNavigationMenu();
                } else {
                    hideBottomNavigationMenu();
                }
            }

        });
        tileRecyclerView.setAdapter(staggeredRecyclerViewAdapter);
        tileRecyclerView.setVisibility(View.VISIBLE);
        hideLoadingAnim();
    }

    private ArrayList<TMCTile> getTMCTileList() {
        if ((tmcTileHashMap == null) || (tmcTileHashMap.size() <= 0)) {
            return null;
        }
        Object[] values = tmcTileHashMap.values().toArray();
        ArrayList<TMCTile> tmcTileList = new ArrayList<>();
        for (int i=0; i<values.length; i++) {
            TMCTile tmcTile = (TMCTile) values[i];
            tmcTileList.add(tmcTile);
        }
        Collections.sort(tmcTileList);
        return tmcTileList;
    }

    private void fillLocationDetails(String addressjson) {
        if ((addressjson == null) || (TextUtils.isEmpty(addressjson))) {
            return;
        }
        try {
            TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(addressjson));

            delivertoaddress_textview.setText(tmcUserAddress.getAddressline1() + ", " +
                    tmcUserAddress.getAddressline2());
            chooselocation_layout.setVisibility(View.GONE);
            selectedlocation_layout.setVisibility(View.VISIBLE);
            islocationupdated = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private TMCUserAddress defaulttmcuseraddress;
    private void getLocationDetails(String userkey) {
     // Log.d(TAG, "userkey "+userkey);
        if ((userkey == null) || (TextUtils.isEmpty(userkey))) {
            return;
        }
        showLocationLoadingAnim();
        String url = TMCRestClient.AWS_GETADDRESS + "?userid=" + userkey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                     // Log.d(TAG, "Volley response "+response.toString());
                        try {
                         // Log.d(TAG, "getLocationDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                boolean isLocationDataFetched = false;
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject addressobj = jsonArray.getJSONObject(i);
                                    String addresstype = addressobj.getString("addresstype");
                                    if (addresstype.equalsIgnoreCase("Home")) {
                                        defaulttmcuseraddress = new TMCUserAddress(addressobj);
                                     // selectedvendorkey = defaulttmcuseraddress.getVendorkey();
                                        mappedvendorkey = defaulttmcuseraddress.getVendorkey();
                                        islocationupdated = true;
                                        mappedvendorname = defaulttmcuseraddress.getVendorname();
                                        settingsUtil.setDefaultAddress(defaulttmcuseraddress.getJsonString());
                                        delivertoaddress_textview.setText(defaulttmcuseraddress.getAddressline1() +
                                                defaulttmcuseraddress.getAddressline2());
                                        chooselocation_layout.setVisibility(View.GONE);
                                        selectedlocation_layout.setVisibility(View.VISIBLE);
                                        isLocationDataFetched = true;
                                    }
                                }

                                if (isLocationDataFetched) {
                                    getTMCTileDetails(mappedvendorkey);
                                    getVendorDetails(mappedvendorkey);
                                    fillLocationDetails(defaulttmcuseraddress.getJsonString());
                                } else {   // Location details will not be filled
                                    getTMCTileDetails(settingsUtil.getDefaultVendorkey());
                                    getVendorDetails(settingsUtil.getDefaultVendorkey());
                                }
                            }
                            hideLocationLoadingAnim();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                                               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


 /* private void addUserData() {
        TMCUser tmcUser = new TMCUser("9790935376");
        tmcUser.setName("Kalyan");
        tmcUser.setEmail("kalyan.nit@gmail.com");
        TMCRestClient.addUserDetails(getApplicationContext(), tmcUser, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "addUserData jsonObject " + jsonObject); //No I18N
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "addUserData push failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "addUserData push failed"); //No i18n
            }
        });
    }

    public void populateTMCCtgyList() {
        String url1 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Ftmc_freshmeat.png?alt=media&token=cf2ac25e-d166-47ee-9b8b-327dcd448b5b";
        String url2 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Ftmc_freshproduce.png?alt=media&token=001ffd2c-8a51-4db7-b058-84962fee8289";
        String url3 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Ftmczeroeffortbox.png?alt=media&token=a2e785e7-0927-4970-96db-9e9070888e89";

        ArrayList subctgys1 = new ArrayList<String>();
        subctgys1.add("Chicken"); subctgys1.add("Goat Meat"); subctgys1.add("Fish"); subctgys1.add("A1 Milk & Eggs");

        ArrayList subctgys2 = new ArrayList<String>();
        subctgys2.add("Basics"); subctgys2.add("Cut & Whole Vegetables"); subctgys2.add("Puree"); subctgys2.add("Spices");

        ArrayList subctgys3 = new ArrayList<String>();
        subctgys3.add("Meal Kit - Chicken"); subctgys3.add("Meal Kit - Mutton"); subctgys3.add("Meal Kit - Fish");

        String ctgyname1 = "Fresh Meat";
        String ctgyname2 = "Fresh Produce";
        String ctgyname3 = "TMC Meal Kits";

        TMCCtgy tmcCtgy1 = new TMCCtgy(ctgyname1, url1, subctgys1);
        TMCCtgy tmcCtgy2 = new TMCCtgy(ctgyname2, url2, subctgys2);
        TMCCtgy tmcCtgy3 = new TMCCtgy(ctgyname3, url3, subctgys3);
        ArrayList<TMCCtgy> ctgyList = new ArrayList<TMCCtgy>();
        ctgyList.add(tmcCtgy1); ctgyList.add(tmcCtgy2); ctgyList.add(tmcCtgy3);

        tmcCtgyNames = new ArrayList<String>();
        tmcCtgyNames.add(0, ctgyname1); tmcCtgyNames.add(1, ctgyname2); tmcCtgyNames.add(2, ctgyname3);

        tmcCtgyHashMap = new HashMap<String, TMCCtgy>();
        tmcCtgyHashMap.put(ctgyname1, tmcCtgy1);
        tmcCtgyHashMap.put(ctgyname2, tmcCtgy2);
        tmcCtgyHashMap.put(ctgyname3, tmcCtgy3);

        ArrayList<String> ctgyNames = new ArrayList<String>();

        TMCTileRecyclerViewAdapter staggeredRecyclerViewAdapter = new TMCTileRecyclerViewAdapter(this, null);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        tileRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        staggeredRecyclerViewAdapter.setHandler(createTileRecyclerHandler());
        tileRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int verticalscrolloffset = recyclerView.computeVerticalScrollOffset();
                if (verticalscrolloffset <= 120) {
                    showBottomNavigationMenu();
                } else {
                    hideBottomNavigationMenu();
                }
            }

        });
        tileRecyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }
*/
    private void showLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.VISIBLE);
                loadinganim_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoadingAnimWithDesp() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganim_layout.setVisibility(View.VISIBLE);
                loadingdesp_text.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.GONE);
                loadinganim_layout.setVisibility(View.GONE);
                loadingdesp_text.setVisibility(View.GONE);
            }
        });
    }

    private void showLocationLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                locationloadinganim_layout.setVisibility(View.VISIBLE);
                locationdetails_layout.setVisibility(View.GONE);
            }
        });
    }

    private void hideLocationLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                locationloadinganim_layout.setVisibility(View.GONE);
                locationdetails_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showBottomNavigationMenu() {
        if (isBottomNavigationMenuVisible) {
            return;
        }
        isBottomNavigationMenuVisible = true;
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
            // bottomNavigationView.setVisibility(View.VISIBLE);
               bottombar_layout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottomnavigation_bottomup);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(200);
        bottombar_layout.startAnimation(bottomUp);
     // bottomNavigationView.startAnimation(bottomUp);
    }

    private void hideBottomNavigationMenu() {
        if (!isBottomNavigationMenuVisible) {
            return;
        }
        isBottomNavigationMenuVisible = false;
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
             // bottomNavigationView.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottomnavigation_bottomdown);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(300);
     // bottomNavigationView.startAnimation(bottomDown);
     // bottomNavigationView.setVisibility(View.GONE);
        bottombar_layout.startAnimation(bottomDown);
        bottombar_layout.setVisibility(View.GONE);
    }

    private Handler createTileRecyclerHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("tileitemclick")) {
                    String tmcsubctgykey = bundle.getString("tmcsubctgykey");
                    String tmcsubctgyname = bundle.getString("tmcsubctgyname");
                    String tmcctgyname = bundle.getString("tmcctgyname");
                 // Log.d(TAG, "tmcsubctgykey "+tmcsubctgykey+" tmcsubctgyname "+tmcsubctgyname+" tmcctgyname "+tmcctgyname);
                    String vendorkey = "";
                    if ((mappedvendorkey == null) || (TextUtils.isEmpty(mappedvendorkey))) {
                        vendorkey = settingsUtil.getDefaultVendorkey();
                    } else {
                        vendorkey = mappedvendorkey;
                    }

                    startTMCMenuItemActivity(vendorkey, tmcctgyname, tmcsubctgyname, tmcsubctgykey);
                } else if (menutype.equalsIgnoreCase("hideloadinganimation")) {
                    hideLoadingAnim();
                }

                return false;
            }
        };
        return new Handler(callback);
    }

// Location related code
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String CustomerLatitude, CustomerLongitude;
    public boolean checkLocationPermissionforOurApp() {
        Log.d(TAG, "checkLocationPermissionforOurApp");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                       != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                          (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "checkLocationPermissionforOurApp PERMISSION Not GRANTED");
            // Asking user if explanation is needed
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))&&(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION))) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                Log.d(TAG, "checkLocationPermissionforOurApp Request permission 1");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                Log.d(TAG, "checkLocationPermissionforOurApp Request permission 2");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;

        } else {
            Log.d(TAG, "checkLocationPermissionforOurApp PERMISSION GRANTED");
         // Toast.makeText(this, "permission Granted", Toast.LENGTH_LONG).show();

         // SwitchOnGps(HomeScreenActivity.this);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_LONG).show();

                    Log.d(TAG, "onRequestPermissionsResult Location Permission Granted");
                 // SwitchOnGps(HomeScreenActivity.this);


                    // permission was granted. Do the
                    // contacts-related task you need to do.


                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

 /* private void showAppDataReloadAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(HomeScreenActivity.this, title, message,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                getAppData();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    } */

 /* private void showLocationDetailsReloadAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(HomeScreenActivity.this, title, message,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                getLocationDetails(settingsUtil.getUserkey());
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    } */

 /* private void showTileReloadAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(HomeScreenActivity.this, title, message,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                getTMCTileDetails();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    } */

    // Login Screen Related Code
    private void startVerifyOTPActivity(String mobileno) {
        Intent intent = new Intent(HomeScreenActivity.this, VerifyOTPActivity.class);
        intent.putExtra("mobileno", mobileno);
        startActivityForResult(intent, VERIFYOTP_ACT_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(HomeScreenActivity.this, title, message,
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
     // bottomNavigationView.setVisibility(View.GONE);
        bottombar_layout.setVisibility(View.GONE);
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
        bottombar_layout.setVisibility(View.VISIBLE);
     // bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void hideKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }


 /* private void getTMCTileData() {
        TMCRestClient.getTileDetails(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    // tmcTileHashMap = new HashMap<String, TMCTile>();
                    // tmcTileArrayList = new ArrayList<TMCTile>();
                    Log.d(TAG, "getTMCSubCtgy jsonObject " + jsonObject); //No I18N
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("content");
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject tilejsonobj = jsonArray.getJSONObject(i);
                            TMCTile tmcTile = new TMCTile(tilejsonobj);
                            // tmcTileHashMap.put(tmcTile.getKey(), tmcTile);
                            // tmcTileArrayList.add(tmcTile);

                            TMCDataCatalog.getInstance().addTMCTile(tmcTile);
                        }
                    }
                    tmcTileHashMap = TMCDataCatalog.getInstance().getTileMap();
                    tmcTileArrayList = TMCDataCatalog.getInstance().getTMCTileList();
                    if (isUserLoggedIn) {
                        String addressjson = settingsUtil.getDefaultAddress();
                        if ((addressjson != null) && (!TextUtils.isEmpty(addressjson))) {
                            fillLocationDetails(addressjson);
                        } else {
                            getLocationDetails(settingsUtil.getUserkey());
                        }
                    }
                    getAppData();
                    createTMCTileRecyclerAdapter();
                    hideLoadingAnim();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                // getTMCTileData();
                showTileReloadAlert(R.string.app_name, R.string.tilenotloaded_alertmsg);
                Log.d(TAG, "getTMCSubCtgy fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                // getTMCTileData();
                showTileReloadAlert(R.string.app_name, R.string.tilenotloaded_alertmsg);
                Log.d(TAG, "getTMCSubCtgy fetch failed"); //No i18n
            }
        });
    }
*/
    private HashMap<String, TMCTile> tmcTileHashMap;
    private ArrayList<TMCTile> tmcTileArrayList;
    private void getTMCTileDetails(String vendorkey) {
        int tmctilessize = TMCDataCatalog.getInstance().tmcTilesSize();
        String mappedvendorkeyfromtmcdatactlg = TMCDataCatalog.getInstance().getMappedvendorkey();
        if ((tmctilessize > 0) && (vendorkey.equalsIgnoreCase(mappedvendorkeyfromtmcdatactlg))) {
            tmcTileHashMap = TMCDataCatalog.getInstance().getTileMap();
            tmcTileArrayList = TMCDataCatalog.getInstance().getTMCTileList();
            createTMCTileRecyclerAdapter();
            isDataLoaded = true;
            applaunchimage_layout.setVisibility(View.GONE);
            return;
        }

        TMCDataCatalog.getInstance().clear();
        String url = TMCRestClient.AWS_GETTILEDATA + "?storeid=" + vendorkey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            // tmcTileHashMap = new HashMap<String, TMCTile>();
                            // tmcTileArrayList = new ArrayList<TMCTile>();
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject tilejsonobj = jsonArray.getJSONObject(i);
                                    TMCTile tmcTile = new TMCTile(tilejsonobj);
                                    // tmcTileHashMap.put(tmcTile.getKey(), tmcTile);
                                    // tmcTileArrayList.add(tmcTile);

                                    TMCDataCatalog.getInstance().addTMCTile(tmcTile);
                                }
                                TMCDataCatalog.getInstance().setMappedvendorkey(vendorkey);
                            }
                            tmcTileHashMap = TMCDataCatalog.getInstance().getTileMap();
                            tmcTileArrayList = TMCDataCatalog.getInstance().getTMCTileList();
                         /* if (isUserLoggedIn) {
                                String addressjson = settingsUtil.getDefaultAddress();
                                if ((addressjson != null) && (!TextUtils.isEmpty(addressjson))) {
                                    fillLocationDetails(addressjson);
                                } else {
                                    getLocationDetails(settingsUtil.getUserkey());
                                }
                            } */

                            createTMCTileRecyclerAdapter();
                            getTMCSubCtgyListFromAWS();
                            isDataLoaded = true;
                         // applaunchimage_layout.setVisibility(View.GONE);
                         // hideLoadingAnim();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        final Map<String, String> params = new HashMap<>();
                        params.put("modulename", "Store");
                        return params;
                    }

                    @NonNull
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        final Map<String, String> header = new HashMap<>();
                        header.put("Content-Type", "application/json");

                        return header;
                    }
            };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                                              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }

    private String defaultvendorkey; private int deliverylocradius = 0;
 /* private void getAppData() {
        TMCRestClient.getAppData(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "getAppData jsonObject " + jsonObject); //No I18N
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("content");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        defaultvendorkey = jsonObject1.getString("defaultvendorkey");
                        settingsUtil.setDefaultVendorkey(defaultvendorkey);
                        deliverylocradius = jsonObject1.getInt("deliverylocationradius");
                        String supportmailid = jsonObject1.getString("supportmailid");
                        String supportmobileno = jsonObject1.getString("supportmobileno");
                        settingsUtil.setSupportMobileNo(supportmobileno);
                        settingsUtil.setSupportMailid(supportmailid);
                        settingsUtil.setDeliveryLocationRadius(deliverylocradius);
                        Log.d(TAG, "supportmailid "+supportmailid+" supportmobileno "+supportmobileno);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                showAppDataReloadAlert(R.string.app_name, R.string.tilenotloaded_alertmsg);
                Log.d(TAG, "getAppData fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                showAppDataReloadAlert(R.string.app_name, R.string.tilenotloaded_alertmsg);
                Log.d(TAG, "getAppData fetch failed"); //No i18n
            }
        });
    } */

    private int getVersioncode() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void showMandatoryAppUpdateAlert() {
        appupdateanimmask_layout.setVisibility(View.VISIBLE);
        mandatoryappupdatealert_layout.setVisibility(View.VISIBLE);
        mandatoryreleasenotes_desp.setText(mandatoryreleasenotes);
    }

    private void goToPlayStore() {
        final String appPackageName = getPackageName();
        try {
            showLoadingAnim();
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id="
                            + appPackageName))); // NO
            // i18n
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" // NO I18N
                            + appPackageName))); // No
            // i18n
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                hideLoadingAnim();

            }
        }, 3000);
    }

    private void getReleaseHistoryAndCheckForAppUpdate() {
        String url = TMCRestClient.AWS_GETAPPRELEASEHISTORY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            Log.d(TAG, "getReleaseHistoryAndCheckForAppUpdate jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            int currentversioncode = getVersioncode();
                            String releasenotes = "";
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                String newversioncode = jsonObject1.getString("versioncode");
                                String newreleasenote = jsonObject1.getString("releasenote");
                                String newversiontype = jsonObject1.getString("versiontype");
                                mandatoryreleasenotes = jsonObject1.getString("mandatoryreleasenotes");

                                int newversioncodeint = Integer.parseInt(newversioncode);
                                if (newversioncodeint > currentversioncode) {
                                    isShowAppUpdateAlert = true;
                                    if (newversiontype.equalsIgnoreCase("Mandatory")) {
                                        appupdatetype = APPUPDATETYPE_MANDATORY;
                                        releasenotes = newreleasenote;
                                    } else {
                                        appupdatetype = APPUPDATETYPE_OPTIONAL;
                                        releasenotes = newreleasenote;
                                        JSONArray releasehistory = jsonObject1.getJSONArray("releasehistory");
                                        for (int i=0; i<releasehistory.length(); i++) {
                                            JSONObject oldreleaseobjs = releasehistory.getJSONObject(i);
                                            String oldreleaseversioncode = oldreleaseobjs.getString("versioncode");
                                            int oldrelversioncode = Integer.parseInt(oldreleaseversioncode);
                                            if (oldrelversioncode > currentversioncode) {
                                                String oldversiontype = oldreleaseobjs.getString("versiontype");
                                                if (oldversiontype.equalsIgnoreCase("Mandatory")) {
                                                    appupdatetype = APPUPDATETYPE_MANDATORY;
                                                    releasenotes = oldreleaseobjs.getString("releasenote");
                                                }
                                            }
                                        }
                                    }
                                }

                                if (isShowAppUpdateAlert) {
                                    releasenotes_desp.setText(releasenotes);
                                    appupdatealert_layout.setVisibility(View.VISIBLE);
                                    trackorder_layout.setVisibility(View.GONE);
                                }
                                if ((settingsUtil.getMobile() != null) &&
                                        (!TextUtils.isEmpty(settingsUtil.getMobile()))) {
                                    getTMCUserDataAndUpdateAppVersion(settingsUtil.getMobile());
                                }

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private boolean refreshvendormapping;
    private void getAppData() {
        String url = TMCRestClient.AWS_GETAPPDATA;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            Log.d(TAG, "getAppData jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                defaultvendorkey = jsonObject1.getString("defaultvendorkey");
                                settingsUtil.setDefaultVendorkey(defaultvendorkey);
                                deliverylocradius = jsonObject1.getInt("deliverylocationradius");
                                boolean isshowappbanner = jsonObject1.getBoolean("isshowappbanner");
                                String supportmailid = jsonObject1.getString("supportmailid");
                                String supportmobileno = jsonObject1.getString("supportmobileno");
                                int minimumordervaluefordelivery = jsonObject1.getInt("minimumordervaluefordelivery");
                                String freshproducectgykey = jsonObject1.getString("freshproducectgykey");
                                int paymentschdelaytime = jsonObject1.getInt("paymentschedulerdelaytime");
                                boolean placeorderandpayfeature = jsonObject1.getBoolean("placeorderandpayfeature");
                                boolean isorderdetailsnewschema = jsonObject1.getBoolean("orderdetailsnewschema");
                             // boolean inventorycheck = jsonObject1.getBoolean("inventorycheck");
                             // JSONObject expressdeliveryslotdetails
                             //               = jsonObject1.getJSONObject("expressdeliveryslotdetails");
                             // settingsUtil.setExpressdeliverydetails(expressdeliveryslotdetails.toString());
                                settingsUtil.setSupportMobileNo(supportmobileno);
                                settingsUtil.setSupportMailid(supportmailid);
                                settingsUtil.setDeliveryLocationRadius(deliverylocradius);
                                settingsUtil.setMinimumOrderForDelivery(minimumordervaluefordelivery);
                                settingsUtil.setFreshproducectgykey(freshproducectgykey);
                                settingsUtil.setPaymentschedulerdelaytime(paymentschdelaytime);
                                settingsUtil.setPlaceOrderAndPayFeature(placeorderandpayfeature);

                                refreshvendormapping = jsonObject1.getBoolean("refreshvendormapping");
                                settingsUtil.setRefreshVendorMapping(refreshvendormapping);
                                settingsUtil.setOrderDetailsNewSchema(isorderdetailsnewschema);

                                if (jsonObject1.has("tableforsharedprefrefresh")) {
                                    String tableforsharedprefrefresh = jsonObject1.getString("tableforsharedprefrefresh");
                                    if (tableforsharedprefrefresh.equalsIgnoreCase("ADDRESS")) {
                                        String addresskeysforsharedprefrefresh =
                                                         jsonObject1.getString("addresskeysforsharedprefrefresh");
                                        settingsUtil.setAddressKeysForSharedPrefRefresh(addresskeysforsharedprefrefresh);
                                    }
                                }

                             // settingsUtil.setInventoryCheck(inventorycheck);
                             /* if (isshowappbanner) {
                                    String bannerurl = jsonObject1.getString("bannerurl");
                                    showBannerDetails(bannerurl);
                                } */
                             // getVendorDetails();
                            }

                            boolean iscallvendormapping = false;
                            TMCUserAddress tmcUserAddress = null;
                         // Log.d(TAG, "refreshvendormapping "+refreshvendormapping);
                            if (refreshvendormapping) {
                                if (isUserLoggedIn) {
                                    String defaultaddressjson = settingsUtil.getDefaultAddress();
                                    if ((defaultaddressjson != null) && (!TextUtils.isEmpty(defaultaddressjson))) {
                                        tmcUserAddress = new TMCUserAddress(new JSONObject(defaultaddressjson));
                                        lastMappedVendorName = tmcUserAddress.getVendorname();
                                        if (tmcUserAddress.getDeliverydistance() > 10) {
                                            iscallvendormapping = true;
                                        } else {
                                            iscallvendormapping = false;
                                        }
                                    }
                                } else {
                                    iscallvendormapping = false;
                                }
                            } else {
                                iscallvendormapping = false;
                            }

                            if (iscallvendormapping) {
                                fetchAddressDetailsFromAWS(tmcUserAddress.getKey());
                            } else {
                                initAddressAndVendorDetails();
                            }

                            checkForAddressRefresh();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                                                  DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void getVendorDetails(String vendorkey) {
     /* String vendorkey = settingsUtil.getDefaultVendorkey();
        if ((vendorkey == null) || (TextUtils.isEmpty(vendorkey))) {
            return;
        } */
        String url = TMCRestClient.AWS_GETVENDORFORID + "?storeid=" + vendorkey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                 // Log.d(TAG, "getVendorDetails jsonObject1 "+jsonObject1);
                                    String previousvendorjson = settingsUtil.getDefaultVendor();
                                    try {
                                     /* TMCVendor prevVendor = null;
                                        Log.d(TAG, "previousvendorjson "+previousvendorjson);
                                        if (previousvendorjson != null) {
                                            prevVendor = new TMCVendor(new JSONObject(previousvendorjson));
                                        }
                                        if ((prevVendor != null) && !(prevVendor.getVendormobile().equalsIgnoreCase(vendormobile))) {
                                            prevVendor.setVendormobile(vendormobile);
                                            settingsUtil.setDefaultVendor(prevVendor.getJsonObjString());
                                        } */
                                        TMCVendor tmcVendor = new TMCVendor(jsonObject1);
                                        settingsUtil.setDefaultVendor(tmcVendor.getJsonObjString());
                                        settingsUtil.setInventoryCheck(tmcVendor.isInventorycheck());
                                        boolean isshowappbanner = tmcVendor.isShowbanner();
                                        if (isshowappbanner) {
                                            String bannerurl = tmcVendor.getBannerurl();
                                            showBannerDetails(bannerurl);
                                        }
                                        storemobileno = tmcVendor.getVendormobile();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                }

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                                       DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }


    private void showBannerDetails(String bannerurl) {
        if ((bannerurl != null) && !(TextUtils.isEmpty(bannerurl))) {
            Glide.with(getApplicationContext())
                    .load(bannerurl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(banner_imageview);
        }
        bannerscreenmask_layout.setVisibility(View.VISIBLE);
        banner_layout.setVisibility(View.VISIBLE);
    }

    private void hideBannerDetails() {
        bannerscreenmask_layout.setVisibility(View.GONE);
        banner_layout.setVisibility(View.GONE);
    }

    private Ordertrackingdetails ordertrackingdetails;
    private boolean ordertrackingdetailslocalchanged;
    private DatabaseHelper helper;

    private void getOrderTrackingDetails(String orderid) {
        boolean isorderdetailsnewschema = settingsUtil.isOrderdetailsNewSchema();
        if (isorderdetailsnewschema) {
            getCustOrderTrackingDetails(orderid);
        } else {
            getOrderTrackingDetailsOld(orderid);
        }
    }

    private void getOrderTrackingDetailsOld(String orderid) {
        isShowOrderTrackingDetails = false;
        helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        Ordertrackingdetailslocal ordertrackingdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                                         getOrderTrackingdetailsFromSqlite(helper, orderid);
        ordertrackingdetailslocalchanged = false;
        String url = TMCRestClient.AWS_GETORDERTRACKINGDETAILSWITHINDEX + "?orderid=" + orderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                     // Log.d(TAG, "getOrderTrackingDetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray == null) || (jsonArray.length() <= 0)) {
                                    isShowOrderTrackingDetails = false;
                                    trackorder_layout.setVisibility(View.GONE);
                                    return;
                                }

                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject ordertrackingjsonobj = jsonArray.getJSONObject(i);
                                    ordertrackingdetails = new Ordertrackingdetails(ordertrackingjsonobj);
                                }
                                lastplacedorderid = settingsUtil.getLastPlacedOrderId();
                                if (ordertrackingdetails != null) {
                                    String orderstatus = ordertrackingdetails.getOrderstatus();
                                    if ((ordertrackingdetailslocal != null) &&
                                                  (!(orderstatus.equalsIgnoreCase(ordertrackingdetailslocal.getOrderstatus()))) ) {
                                        ordertrackingdetailslocal.setOrderstatus(orderstatus);
                                        ordertrackingdetailslocalchanged = true;
                                    }
                                    if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_NEW)) {
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER RECEIVED");
                                        calldesp_text.setText("Call Store");
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CONFIRMED)) {
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER CONFIRMED");
                                     // calldesp_text.setText("Call Store");
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_READYFORPICKUP)) {
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER READY FOR PICKUP");
                                     // calldesp_text.setText("Call Store");
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_PICKEDUP)) {
                                        deliverypartnermobileno = ordertrackingdetails.getDeliveryusermobileno();
                                        ordertrackingdetailslocal.setDeliveryusermobileno(deliverypartnermobileno);
                                        ordertrackingdetailslocalchanged = true;
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER PICKED UP");
                                     // calldesp_text.setText("Call Delivery Partner");
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_DELIVERED)) {
                                        deliverypartnermobileno = ordertrackingdetails.getDeliveryusermobileno();
                                        ordertrackingdetailslocal.setDeliveryusermobileno(deliverypartnermobileno);
                                        ordertrackingdetailslocalchanged = true;
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER DELIVERED");
                                        settingsUtil.setLastTransactionDetails("");
                                        settingsUtil.setLastPlacedOrderMenuItems("");
                                     // calldesp_text.setText("Call Delivery Partner");
                                        clearLastPlacedOrderId();
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CANCELLED)) {
                                        isShowOrderTrackingDetails = false;
                                        settingsUtil.setLastOrderIdForRating("");
                                        clearLastPlacedOrderId();
                                    }
                                }
                                if (isShowOrderTrackingDetails) {
                                    if (!isShowAppUpdateAlert) {
                                        trackorderloadinganim_layout.setVisibility(View.GONE);
                                        orderstatus_desp.setVisibility(View.VISIBLE);
                                        trackorder_layout.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    trackorder_layout.setVisibility(View.GONE);
                                }

                                if (ordertrackingdetailslocalchanged) {
                                    ordertrackingdetailslocal.update(helper);
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
                RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getCustOrderTrackingDetails(String orderid) {
        isShowOrderTrackingDetails = false;
        helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        Ordertrackingdetailslocal ordertrackingdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                getOrderTrackingdetailsFromSqlite(helper, orderid);
        ordertrackingdetailslocalchanged = false;
        String url = TMCRestClient.AWS_GETCUSTORDTRACKDETFROMORDERIDANDMOBILE + "?usermobileno=" + "%2B" + settingsUtil.getMobile()
                                                                                      + "&orderid=" + orderid;
        Log.d(TAG, "getCustOrderTrackingDetails url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "getOrderTrackingDetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONObject ordertrackingjsonobj = response.getJSONObject("content");
                                if (ordertrackingjsonobj == null) {
                                    isShowOrderTrackingDetails = false;
                                    trackorder_layout.setVisibility(View.GONE);
                                    return;
                                }
                                ordertrackingdetails = new Ordertrackingdetails(ordertrackingjsonobj);
                                lastplacedorderid = settingsUtil.getLastPlacedOrderId();
                                if (ordertrackingdetails != null) {
                                    String orderstatus = ordertrackingdetails.getOrderstatus();
                                    if ((ordertrackingdetailslocal != null) &&
                                            (!(orderstatus.equalsIgnoreCase(ordertrackingdetailslocal.getOrderstatus()))) ) {
                                        ordertrackingdetailslocal.setOrderstatus(orderstatus);
                                        ordertrackingdetailslocalchanged = true;
                                    }
                                    if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_NEW)) {
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER RECEIVED");
                                        calldesp_text.setText("Call Store");
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CONFIRMED)) {
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER CONFIRMED");
                                        // calldesp_text.setText("Call Store");
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_READYFORPICKUP)) {
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER READY FOR PICKUP");
                                        // calldesp_text.setText("Call Store");
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_PICKEDUP)) {
                                        deliverypartnermobileno = ordertrackingdetails.getDeliveryusermobileno();
                                        ordertrackingdetailslocal.setDeliveryusermobileno(deliverypartnermobileno);
                                        ordertrackingdetailslocalchanged = true;
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER PICKED UP");
                                        // calldesp_text.setText("Call Delivery Partner");
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_DELIVERED)) {
                                        deliverypartnermobileno = ordertrackingdetails.getDeliveryusermobileno();
                                        ordertrackingdetailslocal.setDeliveryusermobileno(deliverypartnermobileno);
                                        ordertrackingdetailslocalchanged = true;
                                        isShowOrderTrackingDetails = true;
                                        orderstatus_desp.setText("ORDER DELIVERED");
                                        settingsUtil.setLastTransactionDetails("");
                                        settingsUtil.setLastPlacedOrderMenuItems("");
                                        // calldesp_text.setText("Call Delivery Partner");
                                        clearLastPlacedOrderId();
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CANCELLED)) {
                                        isShowOrderTrackingDetails = false;
                                        settingsUtil.setLastOrderIdForRating("");
                                        clearLastPlacedOrderId();
                                    }
                                }
                                if (isShowOrderTrackingDetails) {
                                    if (!isShowAppUpdateAlert) {
                                        trackorderloadinganim_layout.setVisibility(View.GONE);
                                        orderstatus_desp.setVisibility(View.VISIBLE);
                                        trackorder_layout.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    trackorder_layout.setVisibility(View.GONE);
                                }

                                if (ordertrackingdetailslocalchanged) {
                                    ordertrackingdetailslocal.update(helper);
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    private void clearLastPlacedOrderId() {
     /* Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

            }
        }, 90000); */
        settingsUtil.setLastPlacedOrderId("");
        settingsUtil.setLastPlacedOrderslotname("");
    }

    private void startTrackOrderActivity() {
        Intent intent = new Intent(HomeScreenActivity.this, TrackOrderActivity.class);
        String orderid = settingsUtil.getLastPlacedOrderId();
        String slotname = settingsUtil.getLastPlacedOrderSlotname();
        if ((orderid != null) && !(TextUtils.isEmpty(orderid))) {
            intent.putExtra("orderid", settingsUtil.getLastPlacedOrderId());
        }
        if ((slotname != null) && !(TextUtils.isEmpty(slotname))) {
            intent.putExtra("slotname", settingsUtil.getLastPlacedOrderSlotname());
        }
        startActivityForResult(intent, TRACKORDER_ACT_REQ_CODE);
    }

    private void startDetailOrderActivity() {
        if ((lastplacedorderid == null) || (TextUtils.isEmpty(lastplacedorderid))) {
            return;
        }
        Intent intent = new Intent(HomeScreenActivity.this, DetailOrderActivity.class);
        String slotname = settingsUtil.getLastPlacedOrderSlotname();
        intent.putExtra("orderid", lastplacedorderid);
        if ((slotname != null) && !(TextUtils.isEmpty(slotname))) {
            intent.putExtra("slotname", settingsUtil.getLastPlacedOrderSlotname());
        }
        intent.putExtra("callfromhomescreenactivity", true);
        startActivityForResult(intent, DETAILORDER_ACT_REQ_CODE);
    }

    private boolean isShowBanner() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        String currentdatestr = TMCUtil.getInstance().getCurrentDate();
        String maxdatestr = "Mon, 01 Feb 2021";
        try {
            Date currdate = sdf.parse(currentdatestr);
            Date maxdate = sdf.parse(maxdatestr);
            int comparetovalue = currdate.compareTo(maxdate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

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

    private String getVersionname() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void updateUserDataForAppVersion(String lastupdatedappversion, String previoususertype) {
     // String lastupdatedappversion = settingsUtil.getLastUpdatedAppVersion();
        String newversion = getVersionname();
        Log.d(TAG, "lastupdatedappversion "+lastupdatedappversion+ " newversion "+newversion);
        if ((lastupdatedappversion != null) && (lastupdatedappversion.equalsIgnoreCase(newversion))) {
            return;
        }
        if ((settingsUtil.getMobile() == null) || (TextUtils.isEmpty(settingsUtil.getMobile()))) {
            return;
        }
        if ((settingsUtil.getUserkey() == null) || (TextUtils.isEmpty(settingsUtil.getUserkey()))) {
            return;
        }

        TMCUser tmcUser = new TMCUser(settingsUtil.getMobile());
        tmcUser.setKey(settingsUtil.getUserkey());
        tmcUser.setAppversion(newversion);
        tmcUser.setDeviceos("Android");
        tmcUser.setUpdatedtime(TMCUtil.getInstance().getCurrentTime());

        generateFCMToken("Android", newversion, lastupdatedappversion, previoususertype);

     // String fcmtoken = settingsUtil.getFCMTOken();
     // updateUserDetailsInAWS("Android", newversion,
     //                                   TMCUtil.getInstance().getCurrentTime(), fcmtoken, settingsUtil.getUserkey());

     /* TMCRestClient.updateUserDetails(getApplicationContext(), tmcUser, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "updateuserdata jsonObject " + jsonObject); //No I18N
                    Log.d(TAG, "version updated");
                    settingsUtil.setLastUpdatedAppVersion(newversion);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "updateuserdata push failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "updateuserdata push failed"); //No i18n
            }
        }); */
    }

    private void fetchAddressDetailsFromAWS(String tmcuseraddresskey) {
        String url = TMCRestClient.AWS_GETADDRESS_FROMKEY + tmcuseraddresskey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            // Log.d(TAG, "getAddressDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    // Object deldistanceobj = jsonObject1.get("deliverydistance");

                                    if (jsonObject1.has("isvendorremapping")) {
                                        boolean isvendorremapping = false;
                                        try {
                                            isvendorremapping = jsonObject1.getBoolean("isvendorremapping");
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                        Log.d(TAG, "isvendorremapping "+isvendorremapping);
                                        if (isvendorremapping) {
                                            initAddressAndVendorDetails();
                                        } else {
                                            double locationlat = jsonObject1.getDouble("locationlat");
                                            double locationlong = jsonObject1.getDouble("locationlong");
                                            getVendorDetailsAndMapCorrectVendor(new LatLng(locationlat, locationlong));
                                        }
                                    } else {
                                        double locationlat = jsonObject1.getDouble("locationlat");
                                        double locationlong = jsonObject1.getDouble("locationlong");
                                        getVendorDetailsAndMapCorrectVendor(new LatLng(locationlat, locationlong));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void fetchDistanceFromAWS(TMCUserAddress tmcUserAddress) {
        String url = TMCRestClient.AWS_GETADDRESS_FROMKEY + tmcUserAddress.getKey();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                         // Log.d(TAG, "getAddressDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                 // Object deldistanceobj = jsonObject1.get("deliverydistance");
                                    String deldiststring = jsonObject1.getString("deliverydistance");
                                    double deliverydistance = Double.parseDouble(deldiststring);
                                    tmcUserAddress.setDeliverydistance(deliverydistance);
                                    settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());

                                 /* if (deldistanceobj instanceof Double) {
                                     // double deliverydistance = jsonObject1.getDouble("deliverydistance");

                                    } else {

                                    } */
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void generateFCMToken() {
        //getting the FCM token for the user device to send the notification

        try {
         /* FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task.isSuccessful()) {
                        String fcmtoken = task.getResult().getToken();
                        settingsUtil.setFCMToken(fcmtoken);
                        updateUserDetailsInAWS("", "", TMCUtil.getInstance().getCurrentTime(),
                                                                                         fcmtoken, settingsUtil.getUserkey());
                        Log.i("tag","fcmtoken:  "+fcmtoken);
                    }
                }
            }); */

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    String fcmtoken= task.getResult();
                    settingsUtil.setFCMToken(fcmtoken);
                    Log.d(TAG, "fcmtoken "+fcmtoken);
                    updateUserDetailsInAWS("", "", TMCUtil.getInstance().getCurrentTime(),
                            fcmtoken, "", "");
                    Log.i("tag","fcmtoken:  "+fcmtoken);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void generateFCMToken(String deviceos, String appversion, String lastupdatedappversion, String previoususertype) {
        //getting the FCM token for the user device to send the notification
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    String fcmtoken= task.getResult();
                    settingsUtil.setFCMToken(fcmtoken);
                    updateUserDetailsInAWS(deviceos, appversion, TMCUtil.getInstance().getCurrentTime(),
                            fcmtoken, lastupdatedappversion, previoususertype);
                    Log.i("tag","fcmtoken:  "+fcmtoken);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void calculateDistanceAndUpdateInAddress() {
        String defaultaddress = settingsUtil.getDefaultAddress();
     // Log.d(TAG, "default address "+defaultaddress);
        if ((defaultaddress == null) || (TextUtils.isEmpty(defaultaddress))) {
            return;
        }
        String defaultvendor = settingsUtil.getDefaultVendor();
        if ((defaultvendor == null) || (TextUtils.isEmpty(defaultvendor))) {
            return;
        }
        try {
            TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
         // Log.d(TAG, "settingsUtil.getDefaultAddress() "+settingsUtil.getDefaultAddress());
            if (tmcUserAddress.getDeliverydistance() <= 0) {
                String action = "deldistance: 0 kms";
                logEventInGAnalytics("calculateDistanceAndUpdateInAddress" , action, settingsUtil.getMobile());
                TMCVendor tmcVendor = new TMCVendor(new JSONObject(defaultvendor));
                LatLng vendorlatlng = new LatLng(tmcVendor.getLocationlat(), tmcVendor.getLocationlong());
                LatLng userlatlng = new LatLng(tmcUserAddress.getLocationlat(), tmcUserAddress.getLocationlong());
                getDistanceInKms(userlatlng, vendorlatlng, tmcUserAddress);
            }
        } catch (Exception ex) {
            hideLoadingAnim();
            ex.printStackTrace();
        }
    }

    private void getDistanceInKms(LatLng LatLangofUser, LatLng LatLangofshop, TMCUserAddress tmcUserAddress) throws JSONException {
        Log.i("Tag", "Latlangcal");
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + LatLangofUser.latitude + "," +
                 LatLangofUser.longitude + "&destinations=" + LatLangofshop.latitude + "," + LatLangofshop.longitude +
                        "&mode=driving&language=en-EN&sensor=false&key=AIzaSyDYkZGOckF609Cjt6mnyNX9QhTY9-kAqGY";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "response " + response.toString());

                    JSONArray rowsArray = (JSONArray) response.get("rows");
                 // Log.d(TAG, "rows" + rowsArray.toString());
                    JSONObject elements = rowsArray.getJSONObject(0);
                 // Log.d(TAG, "elements" + elements.toString());

                    JSONArray elementsArray = (JSONArray) elements.get("elements");
                 // Log.d(TAG, "elementsArray" + elementsArray.toString());

                    JSONObject distance = elementsArray.getJSONObject(0);
                 // Log.d(TAG, "distance" + distance.toString());
                    JSONObject jsondistance = distance.getJSONObject("distance");
                 // Log.d(TAG, "jsondistance :" + jsondistance);

                    String distanceinString = jsondistance.getString("text");
                 // Log.d(TAG, "distanceinString :" + distanceinString);
                    double dist = 0;
                    if (distanceinString.contains("km")) {
                        dist = Double.parseDouble(distanceinString.replace(" km",""));
                    } else if (distanceinString.contains("m")) {
                        dist = Double.parseDouble(distanceinString.replace(" m",""));
                        dist = dist / 1000;
                    }
                 // Log.d(TAG, "dist :" + dist+" deliverylocradius "+settingsUtil.getDeliveryLocationRadius());
                    updateAddressDetailsInAWS(tmcUserAddress, dist);
                } catch (JSONException e) {
                    hideLoadingAnim();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error1: " + error.getLocalizedMessage());
                Log.d(TAG, "Error2: " + error.getMessage());
                Log.d(TAG, "Error3: " + error.toString());

                hideLoadingAnim();
                error.printStackTrace();
            }
        });
        // Make the request
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void updateUserDetailsInAWS(String deviceos, String appversion,
                                         String updatedtime, String fcmtoken, String lastupdatedversion, String previoususertype) {
        JSONObject jsonObject = new JSONObject();
        try {
         // jsonObject.put("key", userkey);
            jsonObject.put("mobileno", "+" + settingsUtil.getMobile());
            if ((deviceos != null) && !(TextUtils.isEmpty(deviceos))) {
                jsonObject.put("deviceos", deviceos);
            }
            if ((appversion != null) && !(TextUtils.isEmpty(appversion))) {
                jsonObject.put("appversion", appversion);
            }
            if ((fcmtoken != null) && !(TextUtils.isEmpty(fcmtoken))) {
                jsonObject.put("fcmtoken", fcmtoken);
            }
            if ((updatedtime != null) && !(TextUtils.isEmpty(updatedtime))) {
                jsonObject.put("updatedtime", updatedtime);
            }
            if ((lastupdatedversion != null) && !(TextUtils.isEmpty(lastupdatedversion))) {
                jsonObject.put("previousappversion", lastupdatedversion);
            }

            if ((previoususertype == null) || (TextUtils.isEmpty(previoususertype))) {
                jsonObject.put("usertype", "APP");
            } else if (!previoususertype.contains("APP")) {
                String newusertype = "APP_" + previoususertype;
                jsonObject.put("usertype", newusertype);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String url = TMCRestClient.AWS_UPDATEUSERDETAILS_TMCUSER;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                    Log.d(TAG, "updateUserDetails jsonobject " + response);
                    String message = response.getString("message");
                    settingsUtil.setLastUpdatedAppVersion(appversion);
                    if (message.equalsIgnoreCase("success")) {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                                             DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void updateAddressDetailsInAWS(TMCUserAddress tmcUserAddress, double deliverydistance) {
        if (tmcUserAddress == null) {
            hideLoadingAnim();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            String key = tmcUserAddress.getKey();
            if ((key != null) && (!TextUtils.isEmpty(key))) {
                jsonObject.put("key", key);
            }
            if (deliverydistance > 0) {
                jsonObject.put("deliverydistance", deliverydistance);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tmcUserAddress.setDeliverydistance(deliverydistance);
        settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
        String url = TMCRestClient.AWS_UPDATEADDRESS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                 // Log.d(TAG, "updateAddressDetails jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

     /* TMCRestClient.addAddressDetails(getApplicationContext(), tmcUserAddress, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "addAddressDetails jsonObject " + jsonObject); //No I18N
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("content");
                     // TMCUserAddress tmcUserAddress1 = new TMCUserAddress(jsonObject1);
                        String key = jsonObject1.getString("key");
                        tmcUserAddress.setKey(key);
                        settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
                        Intent intent = new Intent();
                        intent.putExtra("newlocationsaved", true);
                        intent.putExtra("deliverableVendorKey", deliverableVendorKey);
                        closeActivity(intent);
                        Log.d(TAG, "settingsutil default address "+settingsUtil.getDefaultAddress());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "addAddressDetails fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "addAddressDetails fetch failed"); //No i18n
            }
        }); */
    }

    private void initializeRatingViews() {
        ratelastorder_layout = findViewById(R.id.ratelastorder_layout);
        ratelastorder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderdetailslocalforrating == null) {
                    return;
                }
                showRatingOrderScreen(orderdetailslocalforrating);

            }
        });
        ratingscreen_layout = findViewById(R.id.ratingscreen_layout);
        ratingmask_layout = findViewById(R.id.ratingmask_layout);
        ratingsubmit_btn = findViewById(R.id.ratingsubmit_btn);

        rqlayout1 = findViewById(R.id.rqlayout1);
        rqstar1 = findViewById(R.id.rqstar1);
        rqstar1_sel = findViewById(R.id.rqstar1_sel);

        rqlayout2 = findViewById(R.id.rqlayout2);
        rqstar2 = findViewById(R.id.rqstar2);
        rqstar2_sel = findViewById(R.id.rqstar2_sel);

        rqlayout3 = findViewById(R.id.rqlayout3);
        rqstar3 = findViewById(R.id.rqstar3);
        rqstar3_sel = findViewById(R.id.rqstar3_sel);

        rqlayout4 = findViewById(R.id.rqlayout4);
        rqstar4 = findViewById(R.id.rqstar4);
        rqstar4_sel = findViewById(R.id.rqstar4_sel);

        rqlayout5 = findViewById(R.id.rqlayout5);
        rqstar5 = findViewById(R.id.rqstar5);
        rqstar5_sel = findViewById(R.id.rqstar5_sel);

        rdlayout1 = findViewById(R.id.rdlayout1);
        rdstar1 = findViewById(R.id.rdstar1);
        rdstar1_sel = findViewById(R.id.rdstar1_sel);

        rdlayout2 = findViewById(R.id.rdlayout2);
        rdstar2 = findViewById(R.id.rdstar2);
        rdstar2_sel = findViewById(R.id.rdstar2_sel);

        rdlayout3 = findViewById(R.id.rdlayout3);
        rdstar3 = findViewById(R.id.rdstar3);
        rdstar3_sel = findViewById(R.id.rdstar3_sel);

        rdlayout4 = findViewById(R.id.rdlayout4);
        rdstar4 = findViewById(R.id.rdstar4);
        rdstar4_sel = findViewById(R.id.rdstar4_sel);

        rdlayout5 = findViewById(R.id.rdlayout5);
        rdstar5 = findViewById(R.id.rdstar5);
        rdstar5_sel = findViewById(R.id.rdstar5_sel);

        feedbackdetails_edittext = (TMCEditText) findViewById(R.id.feedbackdetails_edittext);
        feedbackclose_btn = findViewById(R.id.feedbackclose_btn);

        itemdetails_desp = (TMCTextView) findViewById(R.id.itemdetails_desp);

        feedbackclose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideRatingOrderScreen();
                ratelastorder_layout.setVisibility(View.GONE);
                updateRatingDetailsInOrderDetailsLocal(true, false);
            }
        });

        ratingmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideRatingOrderScreen();
            }
        });

        ratingscreen_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ratingsubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedqualityrating <= 0) {
                    showTMCAlert(R.string.app_name, R.string.providequalityrating_alert);
                    return;
                }
                if (selecteddeliveryrating <= 0) {
                    showTMCAlert(R.string.app_name, R.string.providedeliveryrating_alert);
                    return;
                }
                hideKeyboard(feedbackdetails_edittext);
                showLoadingAnim();
                addOrderRating();
                ratelastorder_layout.setVisibility(View.GONE);
            }
        });
        setRatingOnClickListeners();

    }

    private void setRatingOnClickListeners() {
        rqlayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedqualityrating = 1;
                if (rqstar1.getVisibility() == View.VISIBLE) {
                    // Unselect STAR1,STAR2,STAR3,STAR4
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);
                    rqstar4_sel.setVisibility(View.GONE);
                    rqstar4.setVisibility(View.VISIBLE);
                    rqstar3_sel.setVisibility(View.GONE);
                    rqstar3.setVisibility(View.VISIBLE);
                    rqstar2_sel.setVisibility(View.GONE);
                    rqstar2.setVisibility(View.VISIBLE);

                    // SELECT STAR1
                    rqstar1_sel.setVisibility(View.VISIBLE);
                    rqstar1.setVisibility(View.GONE);
                } else {
                    selectedqualityrating = 0;
                    if (rqstar2_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR1
                        rqstar1_sel.setVisibility(View.VISIBLE);
                        rqstar1.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR1
                        rqstar1_sel.setVisibility(View.GONE);
                        rqstar1.setVisibility(View.VISIBLE);
                    }

                    // Unselect STAR2,STAR3,STAR4,STAR5
                    rqstar2_sel.setVisibility(View.GONE);
                    rqstar2.setVisibility(View.VISIBLE);
                    rqstar3_sel.setVisibility(View.GONE);
                    rqstar3.setVisibility(View.VISIBLE);
                    rqstar4_sel.setVisibility(View.GONE);
                    rqstar4.setVisibility(View.VISIBLE);
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);
                }
            }
        });

        rqlayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedqualityrating = 2;
                if (rqstar2.getVisibility() == View.VISIBLE) {
                    // Unselect STAR3,STAR4,STAR5
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);
                    rqstar4_sel.setVisibility(View.GONE);
                    rqstar4.setVisibility(View.VISIBLE);
                    rqstar3_sel.setVisibility(View.GONE);
                    rqstar3.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2
                    rqstar2_sel.setVisibility(View.VISIBLE);
                    rqstar2.setVisibility(View.GONE);
                    rqstar1_sel.setVisibility(View.VISIBLE);
                    rqstar1.setVisibility(View.GONE);
                } else {
                    selectedqualityrating = 1;
                    if (rqstar3_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR2
                        rqstar2_sel.setVisibility(View.VISIBLE);
                        rqstar2.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR2
                        rqstar2_sel.setVisibility(View.GONE);
                        rqstar2.setVisibility(View.VISIBLE);
                    }

                    // Unselect STAR3,STAR4,STAR5
                    rqstar3_sel.setVisibility(View.GONE);
                    rqstar3.setVisibility(View.VISIBLE);
                    rqstar4_sel.setVisibility(View.GONE);
                    rqstar4.setVisibility(View.VISIBLE);
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        rqlayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedqualityrating = 3;
                if (rqstar3.getVisibility() == View.VISIBLE) {
                    // Unselect STAR4,STAR5
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);
                    rqstar4_sel.setVisibility(View.GONE);
                    rqstar4.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2,STAR3
                    rqstar3_sel.setVisibility(View.VISIBLE);
                    rqstar3.setVisibility(View.GONE);
                    rqstar2_sel.setVisibility(View.VISIBLE);
                    rqstar2.setVisibility(View.GONE);
                    rqstar1_sel.setVisibility(View.VISIBLE);
                    rqstar1.setVisibility(View.GONE);
                } else {
                    selectedqualityrating = 2;
                    if (rqstar4_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR3
                        rqstar3_sel.setVisibility(View.VISIBLE);
                        rqstar3.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR3
                        rqstar3_sel.setVisibility(View.GONE);
                        rqstar3.setVisibility(View.VISIBLE);
                    }
                    // Unselect STAR4,STAR5
                    rqstar4_sel.setVisibility(View.GONE);
                    rqstar4.setVisibility(View.VISIBLE);
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        rqlayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedqualityrating = 4;
                if (rqstar4.getVisibility() == View.VISIBLE) {
                    // Unselect STAR5
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2,STAR3,STAR4
                    rqstar4_sel.setVisibility(View.VISIBLE);
                    rqstar4.setVisibility(View.GONE);
                    rqstar3_sel.setVisibility(View.VISIBLE);
                    rqstar3.setVisibility(View.GONE);
                    rqstar2_sel.setVisibility(View.VISIBLE);
                    rqstar2.setVisibility(View.GONE);
                    rqstar1_sel.setVisibility(View.VISIBLE);
                    rqstar1.setVisibility(View.GONE);
                } else {
                    selectedqualityrating = 3;
                    if (rqstar5_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR4
                        rqstar4_sel.setVisibility(View.VISIBLE);
                        rqstar4.setVisibility(View.GONE);
                    } else {
                        // Unselect STAR4
                        rqstar4_sel.setVisibility(View.GONE);
                        rqstar4.setVisibility(View.VISIBLE);
                    }
                    // Unselect STAR5
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        rqlayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedqualityrating = 5;
                if (rqstar5.getVisibility() == View.VISIBLE) {
                    rqstar5_sel.setVisibility(View.VISIBLE);
                    rqstar5.setVisibility(View.GONE);
                    rqstar4_sel.setVisibility(View.VISIBLE);
                    rqstar4.setVisibility(View.GONE);
                    rqstar3_sel.setVisibility(View.VISIBLE);
                    rqstar3.setVisibility(View.GONE);
                    rqstar2_sel.setVisibility(View.VISIBLE);
                    rqstar2.setVisibility(View.GONE);
                    rqstar1_sel.setVisibility(View.VISIBLE);
                    rqstar1.setVisibility(View.GONE);
                } else {
                    selectedqualityrating = 4;
                    // Unselect STAR5
                    rqstar5_sel.setVisibility(View.GONE);
                    rqstar5.setVisibility(View.VISIBLE);
                }
            }
        });

        rdlayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecteddeliveryrating = 1;
                if (rdstar1.getVisibility() == View.VISIBLE) {
                    // Unselect STAR1,STAR2,STAR3,STAR4
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);
                    rdstar4_sel.setVisibility(View.GONE);
                    rdstar4.setVisibility(View.VISIBLE);
                    rdstar3_sel.setVisibility(View.GONE);
                    rdstar3.setVisibility(View.VISIBLE);
                    rdstar2_sel.setVisibility(View.GONE);
                    rdstar2.setVisibility(View.VISIBLE);

                    // SELECT STAR1
                    rdstar1_sel.setVisibility(View.VISIBLE);
                    rdstar1.setVisibility(View.GONE);
                } else {
                    selecteddeliveryrating = 0;
                    if (rdstar2_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR1
                        rdstar1_sel.setVisibility(View.VISIBLE);
                        rdstar1.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR1
                        rdstar1_sel.setVisibility(View.GONE);
                        rdstar1.setVisibility(View.VISIBLE);
                    }

                    // Unselect STAR2,STAR3,STAR4,STAR5
                    rdstar2_sel.setVisibility(View.GONE);
                    rdstar2.setVisibility(View.VISIBLE);
                    rdstar3_sel.setVisibility(View.GONE);
                    rdstar3.setVisibility(View.VISIBLE);
                    rdstar4_sel.setVisibility(View.GONE);
                    rdstar4.setVisibility(View.VISIBLE);
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);
                }
            }
        });

        rdlayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecteddeliveryrating = 2;
                if (rdstar2.getVisibility() == View.VISIBLE) {

                    // Unselect STAR3,STAR4,STAR5
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);
                    rdstar4_sel.setVisibility(View.GONE);
                    rdstar4.setVisibility(View.VISIBLE);
                    rdstar3_sel.setVisibility(View.GONE);
                    rdstar3.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2
                    rdstar2_sel.setVisibility(View.VISIBLE);
                    rdstar2.setVisibility(View.GONE);
                    rdstar1_sel.setVisibility(View.VISIBLE);
                    rdstar1.setVisibility(View.GONE);
                } else {
                    selecteddeliveryrating = 1;
                    if (rdstar3_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR2
                        rdstar2_sel.setVisibility(View.VISIBLE);
                        rdstar2.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR2
                        rdstar2_sel.setVisibility(View.GONE);
                        rdstar2.setVisibility(View.VISIBLE);
                    }

                    // Unselect STAR3,STAR4,STAR5
                    rdstar3_sel.setVisibility(View.GONE);
                    rdstar3.setVisibility(View.VISIBLE);
                    rdstar4_sel.setVisibility(View.GONE);
                    rdstar4.setVisibility(View.VISIBLE);
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        rdlayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecteddeliveryrating = 3;
                if (rdstar3.getVisibility() == View.VISIBLE) {

                    // Unselect STAR4,STAR5
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);
                    rdstar4_sel.setVisibility(View.GONE);
                    rdstar4.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2,STAR3
                    rdstar3_sel.setVisibility(View.VISIBLE);
                    rdstar3.setVisibility(View.GONE);
                    rdstar2_sel.setVisibility(View.VISIBLE);
                    rdstar2.setVisibility(View.GONE);
                    rdstar1_sel.setVisibility(View.VISIBLE);
                    rdstar1.setVisibility(View.GONE);
                } else {
                    selecteddeliveryrating = 2;
                    if (rdstar4_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR3
                        rdstar3_sel.setVisibility(View.VISIBLE);
                        rdstar3.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR3
                        rdstar3_sel.setVisibility(View.GONE);
                        rdstar3.setVisibility(View.VISIBLE);
                    }
                    // Unselect STAR4,STAR5
                    rdstar4_sel.setVisibility(View.GONE);
                    rdstar4.setVisibility(View.VISIBLE);
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        rdlayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecteddeliveryrating = 4;
                if (rdstar4.getVisibility() == View.VISIBLE) {
                    // Unselect STAR5
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2,STAR3,STAR4
                    rdstar4_sel.setVisibility(View.VISIBLE);
                    rdstar4.setVisibility(View.GONE);
                    rdstar3_sel.setVisibility(View.VISIBLE);
                    rdstar3.setVisibility(View.GONE);
                    rdstar2_sel.setVisibility(View.VISIBLE);
                    rdstar2.setVisibility(View.GONE);
                    rdstar1_sel.setVisibility(View.VISIBLE);
                    rdstar1.setVisibility(View.GONE);
                } else {
                    selecteddeliveryrating = 3;
                    if (rdstar5_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR4
                        rdstar4_sel.setVisibility(View.VISIBLE);
                        rdstar4.setVisibility(View.GONE);
                    } else {
                        // Unselect STAR4
                        rdstar4_sel.setVisibility(View.GONE);
                        rdstar4.setVisibility(View.VISIBLE);
                    }
                    // Unselect STAR5
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        rdlayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecteddeliveryrating = 5;
                if (rdstar5.getVisibility() == View.VISIBLE) {
                    rdstar5_sel.setVisibility(View.VISIBLE);
                    rdstar5.setVisibility(View.GONE);
                    rdstar4_sel.setVisibility(View.VISIBLE);
                    rdstar4.setVisibility(View.GONE);
                    rdstar3_sel.setVisibility(View.VISIBLE);
                    rdstar3.setVisibility(View.GONE);
                    rdstar2_sel.setVisibility(View.VISIBLE);
                    rdstar2.setVisibility(View.GONE);
                    rdstar1_sel.setVisibility(View.VISIBLE);
                    rdstar1.setVisibility(View.GONE);
                } else {
                    // Unselect STAR5
                    selecteddeliveryrating = 4;
                    rdstar5_sel.setVisibility(View.GONE);
                    rdstar5.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addOrderRating() {
        String feedback = feedbackdetails_edittext.getText().toString();
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        selectedorderidforrating = settingsUtil.getLastOrderIdForRating();
        Orderdetailslocal orderdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                getOrderdetailsFromSqlite(helper, selectedorderidforrating);
        JSONObject ratingJson = null;
        try {
            ratingJson = new JSONObject();
            ratingJson.put("usermobileno", "+" + settingsUtil.getMobile());
            ratingJson.put("qualityrating", selectedqualityrating);
            ratingJson.put("deliveryrating", selecteddeliveryrating);
            ratingJson.put("orderid", selectedorderidforrating);
            ratingJson.put("createdtime", TMCUtil.getInstance().getCurrentTime());
            ratingJson.put("feedback", feedback);
            if (orderdetailslocal != null) {
                ratingJson.put("userkey", orderdetailslocal.getUserkey());
                ratingJson.put("vendorkey", orderdetailslocal.getVendorkey());
                ratingJson.put("vendorname", orderdetailslocal.getVendorname());
                JSONArray jsonArray = orderdetailslocal.getItemDespForRating();
                ratingJson.put("itemrating", jsonArray);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Ratingorderdetailslocal ratingorderdetailslocal = new Ratingorderdetailslocal(ratingJson);
        ratingorderdetailslocal.save(helper);
        updateRatingDetailsInOrderDetailsLocal(false, true);
        String url = TMCRestClient.AWS_ADDRATING;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                ratingJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "addOrderRating jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(29000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

        hideRatingOrderScreen();
        Handler schedulerThread = new Handler();
        schedulerThread.postDelayed(new Runnable() {
            public void run() {
                showTMCAlert(R.string.app_name, R.string.thanksforrating_message);
                hideLoadingAnim();
            }
        }, 2000);
    }

    private void showRatingOrderScreen(Orderdetailslocal orderdetails) {
        if (orderdetails == null) {
            return;
        }
        logEventInGAnalytics("Rate Order in Home Screen button clicked", "Rate Order", settingsUtil.getMobile());
        String itemdesp = orderdetails.getConsolidatedItemDespWithQty();
        itemdetails_desp.setText(itemdesp);
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                ratingmask_layout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(400);
        ratingscreen_layout.startAnimation(bottomUp);
        ratingscreen_layout.setVisibility(View.VISIBLE);
    }

    private void hideRatingOrderScreen() {
        ratingmask_layout.setVisibility(View.GONE);
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                ratingscreen_layout.setVisibility(View.GONE);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(300);
        ratingscreen_layout.startAnimation(bottomDown);

        // ratingscreen_layout.setVisibility(View.GONE);
    }

    private Orderdetailslocal orderdetailslocalforrating;
    private void checkOrderForRatingView() {
        String orderid = settingsUtil.getLastOrderIdForRating();
        if ((orderid == null) && (TextUtils.isEmpty(orderid)) ) {
            return;
        }
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        orderdetailslocalforrating = DatabaseManager.getInstance(getApplicationContext()).
                                                              getOrderdetailsFromSqlite(helper, orderid);
        Ordertrackingdetailslocal ordertrackingdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                                                                  getOrderTrackingdetailsFromSqlite(helper, orderid);
        if ((orderdetailslocalforrating != null) && (ordertrackingdetailslocal != null)) {
            if (ordertrackingdetailslocal.getOrderstatus().equalsIgnoreCase("DELIVERED")) {
                boolean isratingskipped = orderdetailslocalforrating.isRatingskipped();
                boolean isratingprovided = orderdetailslocalforrating.isRatingprovided();
                if (isratingprovided || isratingskipped) {
                    // Do Nothing
                    ratelastorder_layout.setVisibility(View.GONE);
                } else {
                    ratelastorder_layout.setVisibility(View.VISIBLE);
                }
            } else {
                ratelastorder_layout.setVisibility(View.GONE);
            }
        }

    }

    private void updateRatingDetailsInOrderDetailsLocal(boolean isratingskipped, boolean isratingprovided) {
        if (orderdetailslocalforrating == null) {
            return;
        }
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        orderdetailslocalforrating.setRatingskipped(isratingskipped);
        orderdetailslocalforrating.setRatingprovided(isratingprovided);
        orderdetailslocalforrating.update(helper);
    }



    private ArrayList<TMCVendor> tmcVendorArrayList;
    private HashMap<Integer, TMCVendor> latlngvendorkeyList; // {Hashcode of latlng, Vendorkey}
    private HashMap<String, TMCVendor> tmcVendorHashMap;
    private void getVendorDetailsAndMapCorrectVendor(LatLng CustomerLatlng) {
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETVENDORRECORDS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                         // Log.d(TAG, "getVendorDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                tmcVendorArrayList = new ArrayList<TMCVendor>();
                                latlngvendorkeyList = new HashMap<Integer, TMCVendor>();
                                tmcVendorHashMap = new HashMap<String, TMCVendor>();
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String key = jsonObject1.getString("key");
                                    boolean istestvendor = false;
                                    if (jsonObject1.has("istestvendor")) {
                                        istestvendor = jsonObject1.getBoolean("istestvendor");
                                    }
                                    if (istestvendor) {
                                        continue;
                                    }
                                    String vendortype = jsonObject1.getString("vendortype");
                                    if (vendortype.equalsIgnoreCase("WAREHOUSE")) {
                                        continue;
                                    }

                                    // Log.d(TAG, "fetched vendorkey "+key);
                                    double locationlat = jsonObject1.getDouble("locationlat");
                                    double locationlong = jsonObject1.getDouble("locationlong");
                                    TMCVendor tmcVendor = new TMCVendor(jsonObject1);
                                    tmcVendorArrayList.add(tmcVendor);
                                    LatLng latlng = new LatLng(locationlat, locationlong);
                                    latlngvendorkeyList.put(new Integer(latlng.hashCode()), tmcVendor);
                                    tmcVendorHashMap.put(tmcVendor.getKey(), tmcVendor);
                                }

                                if ((CustomerLatlng != null) && (tmcVendorArrayList != null)) {
                                    drawSphericalRound(CustomerLatlng, tmcVendorArrayList, false);
                                }
                             // Log.d(TAG, "getVendorDetails results fetched");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private HashMap<String, TMCVendor> deliverableVendorMap;
    private HashMap<Double, String> delDistanceVendorKeyMap;
    private List<MarkerOptions> markers = new ArrayList<>();
    private int locationapicalls = 0;
    private int locationapiresponses = 0;
    private void drawSphericalRound(LatLng latLngofuser, List<TMCVendor> vendorList, boolean isAddUpdateAddress) {
        deliverableVendorMap = new HashMap<String, TMCVendor>();
        delDistanceVendorKeyMap = new HashMap<Double, String>();
        for (int i=0; i<vendorList.size(); i++) {
            TMCVendor tmcVendor = vendorList.get(i);
            LatLng vendorlatLng = new LatLng(tmcVendor.getLocationlat(), tmcVendor.getLocationlong());
            MarkerOptions marker1 = new MarkerOptions().position(vendorlatLng);
            markers.add(marker1);
        }

        locationapicalls = 0; locationapiresponses = 0;
     // Log.d(TAG, "markers size "+markers.size());
        for (int j=0; j<markers.size(); j++) {
            MarkerOptions marker = markers.get(j);
         // int dellocinmeters = settingsUtil.getDeliveryLocationRadius() * 1000;
            int dellocinmeters = 30 * 1000;  // Hardcoded value
            double computedistinbetween = SphericalUtil.computeDistanceBetween(latLngofuser, marker.getPosition());
            if (computedistinbetween < dellocinmeters) {
                locationapicalls = locationapicalls + 1;
                LatLng latLngofshopfromSphericalMarker = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
                try {
                    calculateDistanceviaApi(latLngofuser,latLngofshopfromSphericalMarker,marker,isAddUpdateAddress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (j == (markers.size()-1)) {
             // Log.d(TAG, "locationapicalls "+locationapicalls + " locationapiresponses" +locationapiresponses);
                startSchedulerThreadForGettingMappedVendors();
            }
        }
    }

    private void calculateDistanceviaApi(LatLng LatLangofUser, LatLng LatLangofshop,MarkerOptions marker,
                                         boolean isAddUpdateAddress) throws JSONException {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + LatLangofUser.latitude + "," + LatLangofUser.longitude + "&destinations=" + LatLangofshop.latitude + "," + LatLangofshop.longitude + "&mode=driving&language=en-EN&sensor=false&key=AIzaSyDYkZGOckF609Cjt6mnyNX9QhTY9-kAqGY";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                    locationapiresponses = locationapiresponses + 1;
                    JSONArray rowsArray = (JSONArray) response.get("rows");
                    JSONObject elements = rowsArray.getJSONObject(0);

                    JSONArray elementsArray = (JSONArray) elements.get("elements");

                    JSONObject distance = elementsArray.getJSONObject(0);
                    JSONObject jsondistance = distance.getJSONObject("distance");

                    String distanceinString = jsondistance.getString("text");
                    double dist = 0;
                    if (distanceinString.contains("km")) {
                        dist = Double.parseDouble(distanceinString.replace(" km",""));
                    } else if (distanceinString.contains("m")) {
                        dist = Double.parseDouble(distanceinString.replace(" m",""));
                        dist = dist / 1000;
                    }
                    int dellocradius = 30;  // Hard coded value
                    if(dist < dellocradius) {
                        if (latlngvendorkeyList != null) {
                            TMCVendor tmcVendor = latlngvendorkeyList.get(LatLangofshop.hashCode());
                            deliverableVendorMap.put(tmcVendor.getKey(), tmcVendor);
                            delDistanceVendorKeyMap.put(new Double(dist), tmcVendor.getKey());
                        }
                    } else {
                        // Log.d(TAG, "calculateDistanceviaApi completedlocationapicalls "+completedlocationapicalls+ " tmcVendorArrayList.size() "+tmcVendorArrayList.size());
                    }
                } catch (JSONException e) {
                    hideLoadingAnim();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error1: " + error.getLocalizedMessage());
                Log.d(TAG, "Error2: " + error.getMessage());
                Log.d(TAG, "Error3: " + error.toString());

                error.printStackTrace();
            }
        });
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private int schthreadcount = 0;
    private int schthreadmaxcount = 15;
    private void startSchedulerThreadForGettingMappedVendors() {
        if (locationapicalls == locationapiresponses) {
            mapDeliverableVendor();
        } else {
            if (schthreadcount > schthreadmaxcount) {
                return;
            }
            schthreadcount = schthreadcount + 1;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startSchedulerThreadForGettingMappedVendors();
                }
            }, 1500);
        }
    }

    private void mapDeliverableVendor() {
     // Log.d(TAG, "mapDeliverableVendor "+deliverableVendorMap);
        if ((deliverableVendorMap == null) || (deliverableVendorMap.size() <= 0)) {
            return;
        }
        if ((delDistanceVendorKeyMap == null) || (delDistanceVendorKeyMap.size() <= 0)) {
            return;
        }
        double shorterdistance = 0; String deliverablevendorkey = null;
        Object[] delDistanceKeys = delDistanceVendorKeyMap.keySet().toArray();
        for (int i=0; i<delDistanceKeys.length; i++) {
            if (i == 0) {
                Double deldistance = (Double) delDistanceKeys[i];
                shorterdistance = deldistance.doubleValue();
                deliverablevendorkey = delDistanceVendorKeyMap.get(deldistance);
            } else {
                Double deldistance = (Double) delDistanceKeys[i];
                double deldistindouble = deldistance.doubleValue();
                if (shorterdistance < deldistindouble) {
                    // shorterdistance = shorterdistance
                } else {
                    shorterdistance = deldistindouble;
                    deliverablevendorkey = delDistanceVendorKeyMap.get(deldistance);
                }
            }
        }
        TMCVendor deliverableVendor = deliverableVendorMap.get(deliverablevendorkey);
        Log.d(TAG, "deliverableVendor "+deliverableVendor.getKey() + " name "+deliverableVendor.getName());
        updateVendorDetailsInUserAddress(deliverableVendor, shorterdistance);
     // TMCMenuItemCatalog.getInstance().clearMenuItems();
        TMCMenuItemCatalog.getInstance().clear();
     // TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
        TMCDataCatalog.getInstance().clear();
        initAddressAndVendorDetails();
        if ((lastMappedVendorName == null) || (TextUtils.isEmpty(lastMappedVendorName))) {
            subscribeToFCMTopic(deliverableVendor.getName());
        } else {
            if (deliverableVendor.getName().equalsIgnoreCase(lastMappedVendorName)) {
                // Do Nothing
                subscribeToFCMTopic(deliverableVendor.getName());
            } else {
                subscribeToFCMTopic(deliverableVendor.getName());
                unsubscribeFromFCMTopic(lastMappedVendorName);
            }
        }

    }

    private void updateVendorDetailsInUserAddress(TMCVendor tmcVendor, double deliverydistance) {
        String defaultaddressjson = settingsUtil.getDefaultAddress();
        if ((defaultaddressjson == null) || (TextUtils.isEmpty(defaultaddressjson))) {
            return;
        }
        try {
            TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(defaultaddressjson));
         /* if (tmcUserAddress.getVendorkey().equalsIgnoreCase(tmcVendor.getKey())) {
                settingsUtil.setRefreshVendorMappingCompleted();
                return;
            } */

            tmcUserAddress.setDeliverydistance(deliverydistance);
            tmcUserAddress.setVendorkey(tmcVendor.getKey());
            tmcUserAddress.setVendorname(tmcVendor.getName());
            settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", tmcUserAddress.getKey());
            jsonObject.put("deliverydistance", deliverydistance);
            jsonObject.put("vendorkey", tmcVendor.getKey());
            jsonObject.put("vendorname", tmcVendor.getName());
            jsonObject.put("isvendorremapping", true);
         // settingsUtil.setRefreshVendorMappingCompleted();
            updateUserAddressDetailsInAWS(jsonObject);
            initAddressAndVendorDetails();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateUserAddressDetailsInAWS(JSONObject jsonObject) {
        String url = TMCRestClient.AWS_UPDATEADDRESS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                    // Log.d(TAG, "updateAddressDetails jsonobject " + response);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void subscribeToFCMTopic(String vendorname) {
        try {
            String vname = vendorname.replaceAll(" ", "");
            vname = vname.toUpperCase();
            Log.d(TAG, "subscribeToFCMTopic vname "+vname);
            FirebaseMessaging.getInstance().subscribeToTopic(vname)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String action = vendorname + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("Subscribe to topic successful", action, settingsUtil.getMobile());
                                Log.d(TAG, "Subscribe to topic successful");
                                settingsUtil.setFCMTokenTopicSubscribed();
                            } else {
                                String action = vendorname + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("Subscribe to topic failed", action, settingsUtil.getMobile());
                                Log.d(TAG, "Subscribe to topic failed");
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void unsubscribeFromFCMTopic(String vendorname) {
        try {
            String vname = vendorname.replaceAll(" ", "");
            vname = vname.toUpperCase();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(vname)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String action = vendorname + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("UnSubscribed from topic successful", action, settingsUtil.getMobile());
                                Log.d(TAG, "UnSubscribed from topic successful");
                            } else {
                                String action = vendorname + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("UnSubscribed from topic failed", action, settingsUtil.getMobile());
                                Log.d(TAG, "UnSubscribed from topic failed");
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getTMCSubCtgyListFromAWS() {
        String url = TMCRestClient.AWS_GETTMCSUBCTGYRECORDS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            // tmcCtgyNameList = new ArrayList<String>();
                            // tmcSubCtgyMap = new HashMap<String, TMCSubCtgy>();
                            // tmcCtgySubCtgyMap = new HashMap<String, ArrayList<TMCSubCtgy>>();
                            if (message.equalsIgnoreCase("success")) {
                                TMCDataCatalog.getInstance().clearTMCSubCtgyItems();
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String tmcctgyname = jsonObject1.getString("tmcctgyname");
                                    String tmcsubctgyname = jsonObject1.getString("subctgyname");
                                    String tmcsubctgykey = jsonObject1.getString("key");
                                    int displayno = jsonObject1.getInt("displayno");
                                    TMCSubCtgy tmcSubCtgy = new TMCSubCtgy(tmcsubctgykey, displayno, tmcsubctgyname);
                                    // addTMCCtgyAndSubctgy(tmcctgyname, tmcSubCtgy);
                                    TMCDataCatalog.getInstance().addTMCCtgyAndSubctgy(tmcctgyname, tmcSubCtgy);
                                }
                            }
                            if (loadtmcmenuitems) {
                                getTMCMenuItemsFromAWS();
                            }
                            hideLoadingAnim();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                                                      DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void getTMCMenuItemsFromAWS() {
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETTMCMENUITEMSFORSTORE + "?storeid=" + mappedvendorkey;
        Log.d(TAG, "getTMCMenuItems url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                TMCMenuItemCatalog.getInstance().clearMenuItems();
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TMCMenuItem tmcMenuItem = new TMCMenuItem(jsonObject1);
                                    TMCMenuItemCatalog.getInstance().addMenuItem(tmcMenuItem);
                                }
                            }
                         // Log.d(TAG, "getTMCMenuItemsFromAWS size "+TMCMenuItemCatalog.getInstance().menuItemSize());
                            applaunchimage_layout.setVisibility(View.GONE);
                            hideLoadingAnim();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());
                        applaunchimage_layout.setVisibility(View.GONE);
                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void startSchedulerThreadForLandingPageHide() {
        int delaytime = 7000;
        Handler schedulerThread = new Handler();

        schedulerThread.postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG, "isDataLoaded "+isDataLoaded);
                if (isDataLoaded) {
                    applaunchimage_layout.setVisibility(View.GONE);
                } else {
                    applaunchimage_layout.setVisibility(View.GONE);
                    loadtmcmenuitems = false;
                    showLoadingAnim();
                    getAppData();
                }

            }
        }, delaytime);
    }


    private void checkForAddressRefresh() {
        String addressjson = settingsUtil.getDefaultAddress();
        String addresskey = "";
        try {
            if ((addressjson != null) && !(TextUtils.isEmpty(addressjson))) {
                TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(addressjson));
                addresskey = tmcUserAddress.getKey();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String addresskeysforrefresh = settingsUtil.getAddressKeysForSharedPrefRefresh();
        if ((addresskeysforrefresh == null) || (TextUtils.isEmpty(addresskeysforrefresh))) {
            return;
        }
        if ((addresskeysforrefresh.equalsIgnoreCase("ALL")) || (addresskeysforrefresh.contains(addresskey))) {
            fetchAddressDetailsFromAWS(addresskey);
        } else {
            return;
        }
    }

    private void getTMCUserDataAndUpdateAppVersion(String mobileno) {
        String newmobileno = "%2B" + mobileno;

        String url = TMCRestClient.AWS_GETUSERDETAILS_TMCUSER + "?mobileno=" + newmobileno;
        Log.d(TAG, "addorupdateUserDataInTMCUser url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getUserDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            JSONArray jsonArray = response.getJSONArray("content");
                            Log.d(TAG, "getUserDetails jsonArray " + jsonArray); //No I18N
                            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                // String key = jsonObject1.getString("key");
                                String lastupdatedappversion = jsonObject1.getString("appversion");
                                if (jsonObject1.has("status")) {
                                    String status = jsonObject1.getString("status");
                                    settingsUtil.setUserStatus(status);
                                }
                                String previoususertype = jsonObject1.getString("usertype");
                                if (jsonObject1.has("forcelogout")) {
                                    boolean forcelogout = jsonObject1.getBoolean("forcelogout");
                                    if (forcelogout) {
                                        logoutAndOpenLoginScreen();
                                    }
                                }
                                updateUserDataForAppVersion(lastupdatedappversion, previoususertype);
                            } else {

                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void logoutAndOpenLoginScreen() {
        try {
            if (AWSMobileClient.getInstance().isSignedIn()) {
                String mobileno = settingsUtil.getMobile();
                updateUserData(mobileno, false);
                AWSMobileClient.getInstance().signOut();
                settingsUtil.clearAllData();
                TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
                TMCMenuItemCatalog.getInstance().clear();
                TMCDataCatalog.getInstance().clear();
                showLoginScreen();
                isUserLoggedIn = false;
                initAddressAndVendorDetails();
                chooselocation_layout.setVisibility(View.VISIBLE);
                selectedlocation_layout.setVisibility(View.GONE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateUserData(String mobileno, boolean forcelogout) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobileno", "+" + mobileno);
            jsonObject.put("forcelogout", forcelogout);
            Log.d(TAG, "updateUserData jsonobject forcelogout "+jsonObject);

            String url = TMCRestClient.AWS_UPDATEUSERDETAILS_TMCUSER;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        Log.d(TAG, "updateUserData jsonobject forcelogout " + response);
                        String message = response.getString("message");
                        if (message.equalsIgnoreCase("success")) {

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    Log.d(TAG, "Error: " + error.getLocalizedMessage());
                    Log.d(TAG, "Error: " + error.getMessage());
                    Log.d(TAG, "Error: " + error.toString());

                    error.printStackTrace();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");

                    return params;
                }
            };
            // Make the request
            RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(mRetryPolicy);
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }



}
