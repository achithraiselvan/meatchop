package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

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
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.CouponListAdapter;
import com.meatchop.data.CouponUserGroupDetails;
import com.meatchop.data.Coupondetails;
import com.meatchop.data.Coupontransactions;
import com.meatchop.data.Deliveryslotdetails;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.widget.TMCAlert;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CouponsActivity extends BaseActivity {

    private String TAG = "CouponsActivity";

    private View back_icon;
    private ListView coupons_list;
    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private CouponListAdapter couponListAdapter;
    private View couponsempty_view;

    private SettingsUtil settingsUtil;
    private int previouscouponappliedcount = 0;
    private String mappedvendorkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coupons);

        settingsUtil = new SettingsUtil(this);

        mappedvendorkey = getIntent().getStringExtra("mappedvendorkey");

        back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        coupons_list = findViewById(R.id.coupons_list);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        couponsempty_view = findViewById(R.id.couponsempty_view);
        getCouponUserGroupDetails();
     // getCoupondetails();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    private void createAdapter() {
        if ((coupondetailsArrayList == null) || (coupondetailsArrayList.size() <= 0)) {
            couponsempty_view.setVisibility(View.VISIBLE);
            hideLoadingAnim();
            return;
        }
        Collections.sort(coupondetailsArrayList);
        couponListAdapter = new CouponListAdapter(this, coupondetailsArrayList, false);
        couponListAdapter.setHandler(createCouponListHandler());
        coupons_list.setAdapter(couponListAdapter);
        hideLoadingAnim();
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

    private ArrayList<String> couponKeysList;
    private void getCouponUserGroupDetails() {
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETCOUPONUSERGROUPDETAILS + settingsUtil.getUserkey();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "getCouponUserGroupDetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                couponKeysList = new ArrayList<String>();
                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray == null) || (jsonArray.length() <= 0)) {
                                    getCoupondetails();
                                    return;
                                }
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject couponjsonobj = jsonArray.getJSONObject(i);
                                    CouponUserGroupDetails couponUserGroupDetails = new CouponUserGroupDetails(couponjsonobj);
                                    if (!couponUserGroupDetails.getStatus().equalsIgnoreCase("ACTIVE")) {
                                        continue;
                                    }
                                    couponKeysList.add(couponUserGroupDetails.getCouponkey());
                                }
                                Log.d(TAG, "couponKeysList "+couponKeysList);
                                getCoupondetails();
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
                        getCouponUserGroupDetails();
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private ArrayList<Coupondetails> coupondetailsArrayList;
    private HashMap<String, Coupondetails> coupondetailsHashMap;
    private void getCoupondetails() {
        String url = TMCRestClient.AWS_GETCOUPONDETAILS;
        Log.d(TAG, "getCoupondetails url "+url);
        showLoadingAnim();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "getCoupondetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                coupondetailsArrayList = new ArrayList<Coupondetails>();
                                coupondetailsHashMap = new HashMap<String, Coupondetails>();
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject couponjsonobj = jsonArray.getJSONObject(i);
                                    Coupondetails coupondetails = new Coupondetails(couponjsonobj);
                                    if (!coupondetails.getVendorkeys().equalsIgnoreCase("All")) {
                                        if (!coupondetails.getVendorkeys().contains(mappedvendorkey)) {
                                            continue;
                                        }
                                    }
                                    if (!(coupondetails.getStatus().equalsIgnoreCase("Active"))) { continue; }
                                    String expirydate = coupondetails.getExpirydate();
                                    if (!isCouponValid(coupondetails.getKey(), expirydate)) { continue; }

                                    if (coupondetails.getUsertype().equalsIgnoreCase(Coupondetails.USERTYPE_GROUP)) {
                                        if (!(couponKeysList.contains(coupondetails.getKey()))) {
                                            continue;
                                        }
                                    }
                                    coupondetailsArrayList.add(coupondetails);
                                    coupondetailsHashMap.put(coupondetails.getKey(), coupondetails);
                                 // Log.d(TAG, "coupondetailsArrayList "+coupondetailsArrayList);
                                }
                                createAdapter();
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
                        getCoupondetails();
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getCouponTransactions(Coupondetails coupondetails) {
        String userkey = settingsUtil.getUserkey();
        String url = TMCRestClient.AWS_GETCOUPONTRANSDETAILS +"?userid=" + userkey;
        Log.d(TAG, "getCouponTransactions url "+url);
        showLoadingAnim();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                     // Log.d(TAG, "getCoupondetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                previouscouponappliedcount = 0;
                                JSONArray jsonArray = response.getJSONArray("content");
                                if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                    for (int i=0; i<jsonArray.length(); i++) {
                                        JSONObject couponjsonobj = jsonArray.getJSONObject(i);
                                        Coupontransactions coupontransactions = new Coupontransactions(couponjsonobj);
                                        if (coupontransactions.getCouponkey().equalsIgnoreCase(coupondetails.getKey())) {
                                            previouscouponappliedcount = previouscouponappliedcount + 1;
                                        }
                                    }
                                }

                                if (coupondetails.getRedeemcount() > previouscouponappliedcount) {
                                    parseCouponDetailsAndCloseActivity(true, coupondetails);
                                } else {
                                    showTMCAlert(R.string.couponnotapplied_alerttitle, R.string.couponlimitexceeded_alertmessage);
                                    // Show Coupon cannot be applied alert
                                }
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
                        getCouponTransactions(coupondetails);
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

    private void showTMCAlert(int alerttitle, int alertmessage) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                new TMCAlert(CouponsActivity.this, alerttitle, alertmessage,
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

    private Handler createCouponListHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("applybuttonclicked")) {
                    showLoadingAnim();
                    String couponkey = bundle.getString("coupondetailsid");
                    Log.d(TAG, "couponkey "+couponkey);
                    if (coupondetailsHashMap != null) {
                        Coupondetails coupondetails = coupondetailsHashMap.get(couponkey);
                        if (coupondetails.getUsertype().equalsIgnoreCase(Coupondetails.USERTYPE_NEW)) {
                            boolean isNewUser = isNewUser(coupondetails.getCreateddate(), settingsUtil.getUserCreatedTime());
                            if (!isNewUser) {
                                showTMCAlert(R.string.couponnotapplied_alerttitle, R.string.couponerrormessage_newuser);
                                hideLoadingAnim();
                                return false;
                            }
                        }
                        getCouponTransactions(coupondetails);
                     /* if (coupondetails.getUsertype().equalsIgnoreCase(Coupondetails.USERTYPE_NEW)) {
                            getCouponTransactions(coupondetails);
                        } else {
                            parseCouponDetailsAndCloseActivity(true, coupondetails);
                        } */
                    }
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private void parseCouponDetailsAndCloseActivity(boolean applycouponclicked, Coupondetails coupondetails) {
        Intent intent = new Intent();
        intent.putExtra("applycouponclicked", applycouponclicked);
        intent.putExtra("coupondetails", coupondetails);
        setResult(RESULT_OK, intent);
        hideLoadingAnim();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_right);
    }

    private boolean isNewUser(String couponcreateddate, String usercreateddate) {
        try {
            if ((usercreateddate == null) || (TextUtils.isEmpty(usercreateddate))) {
                return false;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
            Date coupondate = sdf.parse(couponcreateddate);
            Date userdate = sdf.parse(usercreateddate);
            boolean isAfterDate = userdate.after(coupondate);
            return isAfterDate;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean isCouponValid(String couponkey, String expirydate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
            String timstr = sdf.format(new Date());
            Date currDate = sdf.parse(timstr);
            Date expiryDate = sdf.parse(expirydate);
            currDate = removeTime(currDate);
            expiryDate = removeTime(expiryDate);

            // Log.d("CouponsActivity", "isDateEqual "+isDateEqual);
            boolean isDateEqual = currDate.equals(expiryDate);
            boolean isBeforeDate = currDate.before(expiryDate);

            if (!isDateEqual && !isBeforeDate) {
                String action = couponkey + " Curr:" + currDate.toString() + " Exp:" + expiryDate.toString();
                logEventInGAnalytics("Coupon not valid", action, settingsUtil.getMobile());
            }
            return isDateEqual || isBeforeDate;
         // return isDateEqual;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        // cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Tracker tmcTracker;
    private void logEventInGAnalytics(String category, String action, String label) {
        try {
            String label1 = label.substring(2);
            if (tmcTracker == null) {
                tmcTracker = ((TMCApplication) getApplication()).getDefaultTracker();
            }
            tmcTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action).setLabel(label1)
                    .build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
