package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.adapters.TMCProfileAddressListAdapter;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCEditText;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageAddressActivity extends BaseActivity {

    private static final int LOCATIONSERVICES_ACT_REQ_CODE = 0;
    private static final int LOCATIONMAP_ACT_REQ_CODE = 1;

    private String TAG = "ManageAddressActivity";

    private View loadinganimmask_layout;
    private View loadinganim_layout;

    private SettingsUtil settingsUtil;

    private RecyclerView savedaddresses_recyclerview;
    private View addnewaddress_layout;
    private View noaddresses_layout;

    private View back_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_manage_address);

        settingsUtil = new SettingsUtil(this);

        back_icon = findViewById(R.id.back_icon);

        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        addnewaddress_layout = findViewById(R.id.addnewaddress_layout);
        noaddresses_layout = findViewById(R.id.noaddresses_layout);

        savedaddresses_recyclerview = findViewById(R.id.savedaddresses_recyclerview);

        savedaddresses_recyclerview = (RecyclerView) findViewById(R.id.savedaddresses_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        savedaddresses_recyclerview.setLayoutManager(layoutManager);

        addnewaddress_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationServicesActivity();
            }
        });

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if ((settingsUtil.getUserkey() != null) && !(TextUtils.isEmpty(settingsUtil.getUserkey()))) {
            getAddressDetailsFromAWS(settingsUtil.getUserkey());
        } else {
            showForceLoginAlert();
        }
    }

    private void closeActivityAndSendForceLoginHandler() {
        Intent intent = new Intent();
        intent.putExtra("forcelogin", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showForceLoginAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(ManageAddressActivity.this, R.string.app_name, R.string.forcelogin_alert,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                closeActivityAndSendForceLoginHandler();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_right);
    }

    private void startLocationServicesActivity() {
        Intent intent = new Intent(ManageAddressActivity.this, LocationServicesActivity.class);
        intent.putExtra("callfromprofileactivity", true);
        intent.putExtra("calltype", LocationServicesActivity.CALLTYPE_MANAGEADDRESS);
        startActivityForResult(intent, LOCATIONSERVICES_ACT_REQ_CODE);
    }

    private void startLocationMapActivity(TMCUserAddress tmcUserAddress) {
        Intent intent = new Intent(ManageAddressActivity.this, LocationMapActivity.class);
        intent.putExtra("callfromprofileactivity", true);
        intent.putExtra("tmcuseraddress", tmcUserAddress);
        startActivityForResult(intent, LOCATIONMAP_ACT_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATIONSERVICES_ACT_REQ_CODE :
                hideLoadingAnim();
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean isLocationUpdated = data.getBooleanExtra("isLocationUpdated", false);
                        if (isLocationUpdated) {
                            getAddressDetailsFromAWS(settingsUtil.getUserkey());
                        }
                    }
                }
                break;

            case LOCATIONMAP_ACT_REQ_CODE :
                hideLoadingAnim();
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean isLocationUpdated = data.getBooleanExtra("isLocationUpdated", false);
                        if (isLocationUpdated) {
                            getAddressDetailsFromAWS(settingsUtil.getUserkey());
                        }
                    }
                }
                break;


            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private Handler createProfileAddressHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("editbtnclicked")) {
                    showLoadingAnim();
                    String useraddresskey = bundle.getString("useraddresskey");
                    TMCUserAddress tmcUserAddress = tmcUserAddressHashMap.get(useraddresskey);
                    startLocationMapActivity(tmcUserAddress);
                    Log.d(TAG, "createUserAddressHandler tmcUseraddresskey "+tmcUserAddress.getKey());
                } else if (menutype.equalsIgnoreCase("deletebtnclicked")) {
                    String useraddresskey = bundle.getString("useraddresskey");
                    confirmDeleteAlert(useraddresskey);
                }

                return false;
            }
        };
        return new Handler(callback);
    }

    private void confirmDeleteAlert(String useraddresskey) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(ManageAddressActivity.this, R.string.app_name, R.string.deleteconfirmation_string,
                        R.string.ok_capt, R.string.cancel_capt,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                             // deleteAddressDetailsFromAWS(useraddresskey);
                                deleteAddressDetails(useraddresskey);
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }


    private ArrayList<TMCUserAddress> tmcUserAddressArrayList;
    private HashMap<String, TMCUserAddress> tmcUserAddressHashMap;  // {Key -- TMCUserAddress}
    private void getAddressDetailsFromAWS(String userkey) {
        if ((userkey == null) || (TextUtils.isEmpty(userkey))) {
            noaddresses_layout.setVisibility(View.VISIBLE);
            savedaddresses_recyclerview.setVisibility(View.GONE);
            return;
        }
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETADDRESS + "?userid=" + userkey;
        Log.d(TAG, "getAddressDetailsFromAWS url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getAddressDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            tmcUserAddressArrayList = new ArrayList<TMCUserAddress>();
                            tmcUserAddressHashMap = new HashMap<String, TMCUserAddress>();
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TMCUserAddress tmcUserAddress = new TMCUserAddress(jsonObject1);
                                    String addresstype = jsonObject1.getString("addresstype");

                                    tmcUserAddressArrayList.add(tmcUserAddress);
                                    tmcUserAddressHashMap.put(tmcUserAddress.getKey(), tmcUserAddress);
                                }
                                if ((tmcUserAddressArrayList != null) && (tmcUserAddressArrayList.size() > 0)) {
                                    noaddresses_layout.setVisibility(View.GONE);
                                    savedaddresses_recyclerview.setVisibility(View.VISIBLE);
                                    TMCProfileAddressListAdapter tmcprofileAddressListAdapter =
                                            new TMCProfileAddressListAdapter(getApplicationContext(), tmcUserAddressArrayList);
                                    tmcprofileAddressListAdapter.setHandler(createProfileAddressHandler());
                                    savedaddresses_recyclerview.setAdapter(tmcprofileAddressListAdapter);
                                } else {
                                    noaddresses_layout.setVisibility(View.VISIBLE);
                                    savedaddresses_recyclerview.setVisibility(View.GONE);
                                }
                            }
                            hideLoadingAnim();
                            Log.d(TAG, "tmcuseraddressarrlist size "+tmcUserAddressArrayList.size());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        getAddressDetailsFromAWS(userkey);
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

    private void deleteAddressDetailsFromAWS(String addresskey) {
        showLoadingAnim();
        String url = TMCRestClient.AWS_DELETEADDRESS + "?addressid=" + addresskey;
     // Log.d(TAG, "deleteAddressDetailsFromAWS url "+url);
        TMCRestClient.deleteUserAddress(getApplicationContext(), addresskey, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "deleteAddressDetailsFromAWS jsonObject "+jsonObject);
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        getAddressDetailsFromAWS(settingsUtil.getUserkey());
                        String addressjson = settingsUtil.getDefaultAddress();
                        TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(addressjson));
                        if (tmcUserAddress.getKey().equalsIgnoreCase(addresskey)) {
                            settingsUtil.setDefaultAddress("");
                        }
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d("ProfileActivity", "deleteAddressDetailsFromAWS failed0 jsonObject "+jsonObject+ " throwable "+throwable); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d("ProfileActivity", "deleteAddressDetailsFromAWS failed1 String "+s+ " throwable "+throwable); //No i18n
            }
        });
    }

    private void deleteAddressDetails(String addresskey) {
        String url = TMCRestClient.AWS_DELETEADDRESS + "?addressid=" + addresskey;
     // String url = TMCRestClient.AWS_DELETEADDRESS;
        Log.d(TAG, "url "+url);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("addressid", addresskey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "deleteAddressDetailsFromAWS jsonObject "+response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        getAddressDetailsFromAWS(settingsUtil.getUserkey());
                        String addressjson = settingsUtil.getDefaultAddress();
                        TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(addressjson));
                        if (tmcUserAddress.getKey().equalsIgnoreCase(addresskey)) {
                            settingsUtil.setDefaultAddress("");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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


}
