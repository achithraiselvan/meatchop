package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.adapters.TMCProfileAddressListAdapter;
import com.meatchop.adapters.TMCUserAddressListAdapter;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCUser;
import com.meatchop.data.TMCUserAddress;
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
import java.util.Map;
import java.util.regex.Pattern;

public class ProfileActivity extends BaseActivity {

    private static final int LOCATIONSERVICES_ACT_REQ_CODE = 0;
    private static final int LOCATIONMAP_ACT_REQ_CODE = 1;

    private String TAG = "ProfileActivity";
    private View back_icon;
    private TMCEditText name_edittext;
    private TMCTextView phone_textview;
    private TMCEditText email_edittext;
    private View loadinganimmask_layout;
    private View loadinganim_layout;

    private SettingsUtil settingsUtil;

    private View savedetails_layout;
    private String existingname = "";
    private String existingemail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

        settingsUtil = new SettingsUtil(this);

        name_edittext = (TMCEditText) findViewById(R.id.name_edittext);
        phone_textview = (TMCTextView) findViewById(R.id.phone_textview);
        email_edittext = (TMCEditText) findViewById(R.id.email_edittext);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        savedetails_layout = findViewById(R.id.savedetails_layout);

        String mobile = settingsUtil.getMobile();
        phone_textview.setText("+" + settingsUtil.getMobile());

        savedetails_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingAnim();
                String name = name_edittext.getText().toString();
                String email = email_edittext.getText().toString();
                hideKeyboard(name_edittext); hideKeyboard(email_edittext);
                if (TextUtils.isEmpty(name)) {
                    showTMCAlert(R.string.app_name, R.string.invalidname_title);
                    hideLoadingAnim();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    showTMCAlert(R.string.app_name, R.string.emailinvalid_msg);
                    hideLoadingAnim();
                    return;
                }
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                boolean isEmail = pattern.matcher(email).matches();
                if (!isEmail) {
                    showTMCAlert(R.string.app_name, R.string.emailinvalid_msg);
                    hideLoadingAnim();
                    return;
                }

                if ((name.equalsIgnoreCase(existingname)) && (email.equalsIgnoreCase(existingemail))) {
                    closeActivity();
                } else {
                    updateUserData(mobile, name, email);
                }
            }
        });
        getUserDetailsFromAWS(settingsUtil.getMobile());
    }

    private void closeActivity() {
        setResult(RESULT_OK);
        finish();
    }

    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(ProfileActivity.this, title, message,
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

    public void hideKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
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

    private void updateUserData(String mobileno, String name, String email) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobileno", "+" + mobileno);
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            Log.d(TAG, "updateUserData jsonobject "+jsonObject);

            String url = TMCRestClient.AWS_UPDATEUSERDETAILS_TMCUSER;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        Log.d(TAG, "updateUserData jsonobject " + response);
                        String message = response.getString("message");
                        if (message.equalsIgnoreCase("success")) {
                            closeActivity();
                            hideLoadingAnim();
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

    private void getUserDetailsFromAWS(String mobileno) {
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETUSERDETAILS_TMCUSER + "?mobileno=%2B" + mobileno;
        Log.d(TAG, "getAddressDetailsFromAWS url "+url);
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
                            String userkey = "";
                            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                             // userkey = jsonObject1.getString("key");
                                existingname = jsonObject1.getString("name");
                                existingemail = jsonObject1.getString("email");
                                if (existingname != null) {
                                    name_edittext.setText(existingname);
                                }
                                if (existingemail != null) {
                                    email_edittext.setText(existingemail);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        hideLoadingAnim();
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


}
