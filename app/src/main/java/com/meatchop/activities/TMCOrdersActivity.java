package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.table.TableUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.TMCOrdersAdapter;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.Ordertrackingdetails;
import com.meatchop.data.TMCTile;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.helpers.DatabaseManager;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMCOrdersActivity extends BaseActivity {

    private String TAG = "TMCOrdersActivity";
    private static final int TMCDETAILORDER_ACT_REQCODE = 0;
    private static final int TRACKORDER_ACT_REQCODE = 1;
    private static final int ORDERCONFIRMATION_ACT_REQ_CODE = 2;
    private static final int DETAILORDER_ACT_REQ_CODE = 3;

    private RecyclerView orders_recyclerview;
    private TMCOrdersAdapter tmcOrdersAdapter;

  //  private HashMap<String, Orderdetails> orderDetailsMap;
  // private ArrayList<Orderdetails> orderdetailsArrayList;
  // private HashMap<String, Ordertrackingdetails> orderTrackingDetailsMap;

    private HashMap<String, Orderdetailslocal> orderDetailsLocalMap;
    private ArrayList<Orderdetailslocal> orderdetailsLocalArrayList;
    private HashMap<String, Ordertrackingdetailslocal> orderTrackingDetailsLocalMap;
    private HashMap<String, Ratingorderdetailslocal> ratingorderdetailslocalHashMap;

    private SettingsUtil settingsUtil;

    private View loadinganimmask_layout;
    private View loadinganim_layout;

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
    private View noorders_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tmc_orders);
     // getSupportActionBar().hide();

        settingsUtil = new SettingsUtil(this);
        try {
            TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
            userkey = tmcUserAddress.getUserkey();
            vendorkey = tmcUserAddress.getVendorkey();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        View back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        orders_recyclerview = (RecyclerView) findViewById(R.id.orders_recyclerview);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganimmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        noorders_layout = findViewById(R.id.noorders_layout);
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
            }
        });

        setRatingOnClickListeners();

        if (!settingsUtil.isOrderdetailsloadedinsqlite()) {
            showLoadingAnim();
            getOrderTrackingDetailsList(getDatabaseHelper());
            getRatingOrderDetails(getDatabaseHelper());
            loadOrderDetailsInSqlite();
        } else {
            getOrderDetailsListFromSqlite();
        }


     // getOrderDetailsList();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
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

    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(TMCOrdersActivity.this, title, message,
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


    private void showRatingOrderScreen(Orderdetailslocal orderdetails) {
        if (orderdetails == null) {
            return;
        }
        clearRatingViews();
        selectedqualityrating = 0; selecteddeliveryrating = 0;
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

    private void createAdapterForOrderDetails() {
        if ((orderdetailsLocalArrayList == null) || (orderdetailsLocalArrayList.size() <= 0)) {
            noorders_layout.setVisibility(View.VISIBLE);
            orders_recyclerview.setVisibility(View.GONE);
            return;
        }
        noorders_layout.setVisibility(View.GONE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        orders_recyclerview.setLayoutManager(mLayoutManager);
        orders_recyclerview.setItemAnimator(new DefaultItemAnimator());
        tmcOrdersAdapter = new TMCOrdersAdapter(TMCOrdersActivity.this,
                                 orderdetailsLocalArrayList, orderTrackingDetailsLocalMap, ratingorderdetailslocalHashMap);
      // tmcOrdersAdapter = new TMCOrdersAdapter(TMCOrdersActivity.this, orderdetailsArrayList);
        tmcOrdersAdapter.setHandler(createHandlerForRecyclerView());
        orders_recyclerview.setAdapter(tmcOrdersAdapter);
    }

    private Handler createHandlerForRecyclerView() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("itemclicked")) {
                    String orderkey = bundle.getString("orderkey");
                    String orderid = bundle.getString("orderid");
                    Orderdetailslocal orderdetails = orderDetailsLocalMap.get(orderid);
                    startDetailOrderActivity(orderdetails);
                } else if (menutype.equalsIgnoreCase("trackorder")) {
                    String orderkey = bundle.getString("orderkey");
                 // Orderdetails orderdetails = orderDetailsMap.get(orderkey);
                 // startTrackOrderActivity(orderdetails);
                } else if (menutype.equalsIgnoreCase("rateorder")) {
                    logEventInGAnalytics("Rate Order in Orders Screen clicked", "RATE ORDER", settingsUtil.getMobile());
                    selectedqualityrating = 0;
                    selecteddeliveryrating = 0;
                    feedbackdetails_edittext.setText("");
                    String orderkey = bundle.getString("orderkey");
                    selectedorderidforrating = bundle.getString("orderid");
                    Orderdetailslocal orderdetails = orderDetailsLocalMap.get(selectedorderidforrating);
                    showRatingOrderScreen(orderdetails);
                } else if (menutype.equalsIgnoreCase("tellusmore")) {
                    try {
                        String supportmailid = settingsUtil.getSupportMailid();
                        String extratext = "Customer Mobile No: +" + settingsUtil.getMobile();
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{supportmailid});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "The Meat Chop feedback");
                        intent.putExtra(Intent.EXTRA_TEXT, extratext);
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // ToastUtil.showShortToast(getActivity(), "There are no email client installed on your device.");
                    }
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private void startDetailOrderActivity(Orderdetailslocal orderdetails) {
        Intent intent = new Intent(TMCOrdersActivity.this, DetailOrderActivity.class);
        String orderid = orderdetails.getOrderid();
        String slotname = orderdetails.getSlotname();
        if ((orderid != null) && !(TextUtils.isEmpty(orderid))) {
            intent.putExtra("orderid", orderid);
        }
        if ((slotname != null) && !(TextUtils.isEmpty(slotname))) {
            intent.putExtra("slotname", slotname);
        }
        intent.putExtra("orderdetails", orderdetails);
        intent.putExtra("isaddneworder", false);
        intent.putExtra("callfromtmcordersactivity", true);
        startActivityForResult(intent, DETAILORDER_ACT_REQ_CODE);
    }

 /* private void startTMCDetailOrderActivity(Orderdetails orderdetails) {
        Intent intent = new Intent(TMCOrdersActivity.this, TMCDetailOrderActivity.class);
        intent.putExtra("orderdetails", orderdetails);
        startActivityForResult(intent, TMCDETAILORDER_ACT_REQCODE);
    } */

    private void startTrackOrderActivity(Orderdetails orderdetails) {
        Intent intent = new Intent(TMCOrdersActivity.this, TrackOrderActivity.class);
        intent.putExtra("orderid", orderdetails.getOrderid());
        intent.putExtra("slotname", orderdetails.getSlotname());
        intent.putExtra("callfromtmcordersactivity", true);
        startActivityForResult(intent, TRACKORDER_ACT_REQCODE);
    }

 /* private void getOrderDetailsList() {
        String mobileno = settingsUtil.getMobile();
        showLoadingAnim();
        orderdetailsArrayList = new ArrayList<Orderdetails>();
        orderDetailsMap = new HashMap<String, Orderdetails>();
        TMCRestClient.getOrderDetailsFromMobileno(mobileno, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "getOrderDetailsFromMobileno jsonObject "+jsonObject);
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("content");
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Orderdetails orderdetails = new Orderdetails(jsonObject1);
                            Log.d(TAG, "orderdetails orderid "+orderdetails.getOrderid() + " time "+orderdetails.getOrderplacedtime());
                            orderdetailsArrayList.add(orderdetails);
                            orderDetailsMap.put(orderdetails.getKey(), orderdetails);
                        }
                        createAdapterForOrderDetails();
                        hideLoadingAnim();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);

                Log.d("PaymentModeActivity", "getOrderDetailsForOrderid failed0 jsonObject "+jsonObject+ " throwable "+throwable); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d("PaymentModeActivity", "getOrderDetailsForOrderid failed1 String "+s+ " throwable "+throwable); //No i18n
            }
        });
    } */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DETAILORDER_ACT_REQ_CODE :
                // Log.d(TAG, "onActivityResult MOREACTIVITY_REQ_CODE");
                // Log.d(TAG, "onActivityResult MOREACTIVITY_REQ_CODE signin aws " +AWSMobileClient.getInstance().isSignedIn());
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean ordertrackingdetailschanged =
                                        data.getBooleanExtra("ordertrackingdetailschanged", false);

                        if (ordertrackingdetailschanged) {
                            String orderid = data.getStringExtra("orderid");
                            String orderstatus = data.getStringExtra("orderstatus");
                            if (tmcOrdersAdapter != null) {
                                tmcOrdersAdapter.updateOrderStatusInAdapter(orderid, orderstatus);
                            }
                        }
                    }
                }
                break;


            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


 /* private void getOrderDetailsList() {
        String mobile = "%2B" + settingsUtil.getMobile();
        String url = TMCRestClient.AWS_GETORDERDETAILS + "?mobileno=" + mobile;
        showLoadingAnim();
        orderdetailsArrayList = new ArrayList<Orderdetails>();
        orderDetailsMap = new HashMap<String, Orderdetails>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Orderdetails orderdetails = new Orderdetails(jsonObject1);
                                    orderdetailsArrayList.add(orderdetails);
                                    orderDetailsMap.put(orderdetails.getKey(), orderdetails);
                                }
                             // createAdapterForOrderDetails();
                                hideLoadingAnim();
                                getOrderTrackingDetailsList();
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

    private void getOrderTrackingDetailsList() {
        String mobile = "%2B" + settingsUtil.getMobile();
        String url = TMCRestClient.AWS_GETORDERTRACKINGDETAILSFROMMOBILENO + "?mobileno=" + mobile;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray == null) || (jsonArray.length() <= 0)) {
                                    return;
                                }

                                orderTrackingDetailsMap = new HashMap<String, Ordertrackingdetails>();
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject ordertrackingjsonobj = jsonArray.getJSONObject(i);
                                    Ordertrackingdetails ordertrackingdetails = new Ordertrackingdetails(ordertrackingjsonobj);
                                    orderTrackingDetailsMap.put(ordertrackingdetails.getOrderid(), ordertrackingdetails);
                                }
                             // createAdapterForOrderDetails();
                                if (tmcOrdersAdapter != null) {
                                    tmcOrdersAdapter.updateOrderStatusDetails(orderTrackingDetailsMap);
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

    private void hideLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.GONE);
                loadinganim_layout.setVisibility(View.GONE);
            }
        });
    }

    private void addOrderRating() {
        String feedback = feedbackdetails_edittext.getText().toString();
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
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
        if (selectedorderidforrating.equalsIgnoreCase(settingsUtil.getLastOrderIdForRating())) {
            settingsUtil.setLastOrderIdForRating("");
        }
        String url = TMCRestClient.AWS_ADDRATING;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                                            ratingJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
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
                if (tmcOrdersAdapter != null) {
                    tmcOrdersAdapter.updateRatingStatusInHolder(true, selectedorderidforrating);
                }
                hideLoadingAnim();
            }
        }, 2000);
    }

    private DatabaseHelper getDatabaseHelper() {
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return helper;
    }

    private void loadOrderDetailsInSqlite() {
        try {
            TableUtils.clearTable(getConnectionSource(), Orderdetailslocal.class);
            getOrderDetailsList(getDatabaseHelper());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getOrderDetailsList(DatabaseHelper helper) {
        if (settingsUtil == null) {
            settingsUtil = new SettingsUtil(this);
        }
        if (settingsUtil.isOrderdetailsNewSchema()) {
            getCustOrderDetailsList(helper);
        } else {
            getOrderDetailsListOld(helper);
        }
    }

    private void getCustOrderDetailsList(DatabaseHelper helper) {
        String mobile = "%2B" + settingsUtil.getMobile();
        String url = TMCRestClient.AWS_GETCUSTORDDETAILSFROMMOBILENO + "?mobileno=" + mobile;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                    for (int i=0; i<jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        Orderdetailslocal orderdetailslocal = new Orderdetailslocal(jsonObject1);
                                        orderdetailslocal.save(helper);
                                        // orderdetailsLocalArrayList.add(orderdetailslocal);
                                        // orderDetailsLocalMap.put(orderdetailslocal.getServerkey(), orderdetailslocal);
                                    }
                                    settingsUtil.setIsOrderdetailsloadedinsqlite(true);
                                }
                            }
                            getOrderDetailsListOld(helper);

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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getOrderDetailsListOld(DatabaseHelper helper) {
        String mobile = "%2B" + settingsUtil.getMobile();
        String url = TMCRestClient.AWS_GETORDERDETAILS + "?mobileno=" + mobile;
        orderdetailsLocalArrayList = new ArrayList<Orderdetailslocal>();
        orderDetailsLocalMap = new HashMap<String, Orderdetailslocal>();
     // orderTrackingDetailsLocalMap = new HashMap<String, Ordertrackingdetailslocal>();
        ratingorderdetailslocalHashMap = new HashMap<String, Ratingorderdetailslocal>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                    for (int i=0; i<jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        Orderdetailslocal orderdetailslocal = new Orderdetailslocal(jsonObject1);
                                        orderdetailslocal.save(helper);
                                     // orderdetailsLocalArrayList.add(orderdetailslocal);
                                     // orderDetailsLocalMap.put(orderdetailslocal.getServerkey(), orderdetailslocal);
                                    }
                                    settingsUtil.setIsOrderdetailsloadedinsqlite(true);
                                }
                            }
                            getOrderDetailsListFromSqlite();

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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 2,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getOrderTrackingDetailsList(DatabaseHelper helper) {
        String mobile = "%2B" + settingsUtil.getMobile();
        String url = TMCRestClient.AWS_GETORDERTRACKINGDETAILSFROMMOBILENO + "?mobileno=" + mobile;
        Log.d(TAG, "getOrderTrackingDetailsList url "+url);
        try {
            TableUtils.clearTable(getConnectionSource(), Ordertrackingdetailslocal.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (settingsUtil.isOrderdetailsNewSchema()) {
            getCustOrderTrackingDetailsList(helper);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                    for (int i=0; i<jsonArray.length(); i++) {
                                        JSONObject ordertrackingjsonobj = jsonArray.getJSONObject(i);
                                        Ordertrackingdetailslocal ordertrackingdetailslocal =
                                                          new Ordertrackingdetailslocal(ordertrackingjsonobj);
                                        ordertrackingdetailslocal.save(helper);
                                     // orderTrackingDetailsLocalMap.put(ordertrackingdetailslocal.getOrderid(), ordertrackingdetailslocal);
                                    }
                                    settingsUtil.setIsOrderTrackingdetailsloadedinsqlite(true);
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getCustOrderTrackingDetailsList(DatabaseHelper helper) {
        String mobile = "%2B" + settingsUtil.getMobile();
        String url = TMCRestClient.AWS_GETCUSTORDTRACKDETFROMMOBILENO + "?mobileno=" + mobile;
        Log.d(TAG, "getCustOrderTrackingDetailsList url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {

                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                    for (int i=0; i<jsonArray.length(); i++) {
                                        JSONObject ordertrackingjsonobj = jsonArray.getJSONObject(i);
                                        Ordertrackingdetailslocal ordertrackingdetailslocal =
                                                new Ordertrackingdetailslocal(ordertrackingjsonobj);
                                        ordertrackingdetailslocal.save(helper);
                                        // orderTrackingDetailsLocalMap.put(ordertrackingdetailslocal.getOrderid(), ordertrackingdetailslocal);
                                    }
                                    settingsUtil.setIsOrderTrackingdetailsloadedinsqlite(true);
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getRatingOrderDetails(DatabaseHelper helper) {
        String mobile = "%2B" + settingsUtil.getMobile();
        String url = TMCRestClient.AWS_GETRATINGORDERDETAILS + "?mobileno=" + mobile;
        try {
            TableUtils.clearTable(getConnectionSource(), Ratingorderdetailslocal.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                    for (int i=0; i<jsonArray.length(); i++) {
                                        JSONObject orderratingjsonobj = jsonArray.getJSONObject(i);
                                        Ratingorderdetailslocal ratingorderdetailslocal =
                                                            new Ratingorderdetailslocal(orderratingjsonobj);
                                        ratingorderdetailslocal.save(helper);
                                        ratingorderdetailslocalHashMap.put(ratingorderdetailslocal.getOrderid(), ratingorderdetailslocal);
                                    }
                                }
                            }
                         // createAdapterForOrderDetails();
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
                                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);    }

    private void getOrderDetailsListFromSqlite() {
        showLoadingAnim();
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        orderdetailsLocalArrayList = new ArrayList<Orderdetailslocal>();
        orderDetailsLocalMap = new HashMap<String, Orderdetailslocal>();
        orderTrackingDetailsLocalMap = new HashMap<String, Ordertrackingdetailslocal>();
        ratingorderdetailslocalHashMap = new HashMap<String, Ratingorderdetailslocal>();

        List<Orderdetailslocal> orderdetailslocalList =
                DatabaseManager.getInstance(getApplicationContext()).getAllOrdersFromSqlite(helper, 50);
        if (orderdetailslocalList != null) {
            for (int i=0; i<orderdetailslocalList.size(); i++) {
                Orderdetailslocal orderdetailslocal = (Orderdetailslocal) orderdetailslocalList.get(i);
                orderdetailsLocalArrayList.add(orderdetailslocal);
                orderDetailsLocalMap.put(orderdetailslocal.getOrderid(), orderdetailslocal);
            }
        }

        List<Ordertrackingdetailslocal> ordertrackingdetailslocalList =
                DatabaseManager.getInstance(getApplicationContext()).getAllOrderTrackingDetailsFromSqlite(helper, 75);
        if (ordertrackingdetailslocalList != null) {
            for (int i=0; i<ordertrackingdetailslocalList.size(); i++) {
                Ordertrackingdetailslocal ordertrackingdetailslocal = ordertrackingdetailslocalList.get(i);
                orderTrackingDetailsLocalMap.put(ordertrackingdetailslocal.getOrderid(), ordertrackingdetailslocal);
            }
        }

        List<Ratingorderdetailslocal> ratingorderdetailslocallist =
                DatabaseManager.getInstance(getApplicationContext()).getAllRatingOrderDetailsFromSqlite(helper);
        if (ratingorderdetailslocallist != null) {
            for (int i=0; i<ratingorderdetailslocallist.size(); i++) {
                Ratingorderdetailslocal ratingorderdetailslocal = ratingorderdetailslocallist.get(i);
                ratingorderdetailslocalHashMap.put(ratingorderdetailslocal.getOrderid(), ratingorderdetailslocal);
            }
        }

        createAdapterForOrderDetails();
        hideLoadingAnim();
    }

    private void getOrderTrackingDetailsFromSqlite() {
        showLoadingAnim();
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        orderTrackingDetailsLocalMap = new HashMap<String, Ordertrackingdetailslocal>();
        ratingorderdetailslocalHashMap = new HashMap<String, Ratingorderdetailslocal>();

        List<Ordertrackingdetailslocal> ordertrackingdetailslocalList =
                DatabaseManager.getInstance(getApplicationContext()).getAllOrderTrackingDetailsFromSqlite(helper, 75);
        if (ordertrackingdetailslocalList != null) {
            for (int i=0; i<ordertrackingdetailslocalList.size(); i++) {
                Ordertrackingdetailslocal ordertrackingdetailslocal = ordertrackingdetailslocalList.get(i);
                orderTrackingDetailsLocalMap.put(ordertrackingdetailslocal.getOrderid(), ordertrackingdetailslocal);
            }
        }

        List<Ratingorderdetailslocal> ratingorderdetailslocallist =
                DatabaseManager.getInstance(getApplicationContext()).getAllRatingOrderDetailsFromSqlite(helper);
        if (ratingorderdetailslocallist != null) {
            for (int i=0; i<ratingorderdetailslocallist.size(); i++) {
                Ratingorderdetailslocal ratingorderdetailslocal = ratingorderdetailslocallist.get(i);
                ratingorderdetailslocalHashMap.put(ratingorderdetailslocal.getOrderid(), ratingorderdetailslocal);
            }
        }

        createAdapterForOrderDetails();
        hideLoadingAnim();
    }

    private void clearRatingViews() {
        rqstar5_sel.setVisibility(View.GONE);
        rqstar5.setVisibility(View.VISIBLE);
        rqstar4_sel.setVisibility(View.GONE);
        rqstar4.setVisibility(View.VISIBLE);
        rqstar3_sel.setVisibility(View.GONE);
        rqstar3.setVisibility(View.VISIBLE);
        rqstar2_sel.setVisibility(View.GONE);
        rqstar2.setVisibility(View.VISIBLE);
        rqstar1_sel.setVisibility(View.GONE);
        rqstar1.setVisibility(View.VISIBLE);

        rdstar5_sel.setVisibility(View.GONE);
        rdstar5.setVisibility(View.VISIBLE);
        rdstar4_sel.setVisibility(View.GONE);
        rdstar4.setVisibility(View.VISIBLE);
        rdstar3_sel.setVisibility(View.GONE);
        rdstar3.setVisibility(View.VISIBLE);
        rdstar2_sel.setVisibility(View.GONE);
        rdstar2.setVisibility(View.VISIBLE);
        rdstar1_sel.setVisibility(View.GONE);
        rdstar1.setVisibility(View.VISIBLE);
    }

    public void hideKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
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
