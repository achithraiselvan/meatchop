package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.meatchop.R;
import com.meatchop.adapters.ShoppingBagAdapter;
import com.meatchop.adapters.StoreAddressDetailsAdapter;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.services.FetchAddressIntentService;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactusActivity extends AppCompatActivity {

    private String TAG = "ContactusActivity";
    private RecyclerView storeaddressdetails_recyclerview;
    private StoreAddressDetailsAdapter storeAddressDetailsAdapter;
    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private BottomNavigationView bottomNavigationView;

    private static final int TMCSEARCH_ACT_REQCODE = 0;
    private static final int TMCHOME_ACT_REQCODE = 1;
    private static final int MOREACTIVITY_REQ_CODE = 2;
    private View callsupport_layout;
    private View writetous_layout;
    private View callhelpsupport_layout;

    private SettingsUtil settingsUtil;
    private String mappedvendorkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_contactus);

        settingsUtil = new SettingsUtil(this);

        try {
            String defaultaddress = settingsUtil.getDefaultAddress();
            if ((defaultaddress != null) && !(TextUtils.isEmpty(defaultaddress))) {
                TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
                mappedvendorkey = tmcUserAddress.getVendorkey();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        storeaddressdetails_recyclerview = (RecyclerView) findViewById(R.id.storeaddressdetails_recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        storeaddressdetails_recyclerview.setLayoutManager(mLayoutManager);
        storeaddressdetails_recyclerview.setItemAnimator(new DefaultItemAnimator());

        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.app_bar_contactus).setChecked(true);
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
                } else if (item.getTitle().equals("ACCOUNT")) {
                    startMoreActivity();
                }
                return true;
            }
        });

        callsupport_layout = findViewById(R.id.callsupport_layout);
        writetous_layout = findViewById(R.id.writetous_layout);
        callhelpsupport_layout = findViewById(R.id.callhelpsupport_layout);
        callsupport_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supportmobileno = settingsUtil.getSupportMobileNo();
                startCallActivity(supportmobileno);
            }
        });

        writetous_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        getVendorDetails();
    }

    private void startMoreActivity() {
        Intent intent = new Intent(ContactusActivity.this, MoreActivity.class);
        startActivityForResult(intent, MOREACTIVITY_REQ_CODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }


    private void startHomeScreenActivity() {
        Intent intent = new Intent(ContactusActivity.this, HomeScreenActivity.class);
        startActivityForResult(intent, TMCHOME_ACT_REQCODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startTMCSearchActivity() {
        Intent intent = new Intent(ContactusActivity.this, TMCSearchActivity.class);
        startActivityForResult(intent, TMCSEARCH_ACT_REQCODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void showLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
             // loadinganimmask_layout.setVisibility(View.VISIBLE);
                loadinganim_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
             // loadinganimmask_layout.setVisibility(View.GONE);
                loadinganim_layout.setVisibility(View.GONE);
            }
        });
    }

    private ArrayList<TMCVendor> tmcVendorArrayList;
    private HashMap<String, TMCVendor> tmcVendorHashMap;
    private void getVendorDetails() {
        showLoadingAnim();
        // Log.d(TAG, "getVendordetails method");
        String url = TMCRestClient.AWS_GETVENDORRECORDS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            // Log.d(TAG, "getVendorDetails jsonObject " + jsonObject); //No I18N
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                tmcVendorArrayList = new ArrayList<TMCVendor>();
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String key = jsonObject1.getString("key");
                                    boolean istestvendor = false;
                                    if (jsonObject1.has("istestvendor")) {
                                        istestvendor = jsonObject1.getBoolean("istestvendor");
                                        if (istestvendor) {
                                            continue;
                                        }
                                    }

                                    String vendortype = jsonObject1.getString("vendortype");
                                    if (vendortype.equalsIgnoreCase("WAREHOUSE")) {
                                        continue;
                                    }

                                    TMCVendor tmcVendor = new TMCVendor(jsonObject1);
                                    if (mappedvendorkey != null) {
                                        if (tmcVendor.getKey().equalsIgnoreCase(mappedvendorkey)) {
                                            tmcVendorArrayList.add(0, tmcVendor);
                                        } else {
                                            tmcVendorArrayList.add(tmcVendor);
                                        }
                                    } else {
                                        tmcVendorArrayList.add(tmcVendor);
                                    }

                                 // tmcVendorHashMap.put(tmcVendor.getKey(), tmcVendor);
                                }

                                storeAddressDetailsAdapter = new StoreAddressDetailsAdapter
                                                                 (ContactusActivity.this, tmcVendorArrayList, mappedvendorkey);
                                storeaddressdetails_recyclerview.setAdapter(storeAddressDetailsAdapter);
                                storeAddressDetailsAdapter.setHandler(createHandlerForStoreAddressDetailsAdapter());
                                storeaddressdetails_recyclerview.setVisibility(View.VISIBLE);
                                callhelpsupport_layout.setVisibility(View.VISIBLE);
                                hideLoadingAnim();
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

    private Handler createHandlerForStoreAddressDetailsAdapter() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("callstoreclicked")) {
                    String storeno = bundle.getString("storeno");
                    startCallActivity(storeno);
                } else if (menutype.equalsIgnoreCase("getdirectionsclicked")) {
                    String googlemaplocationurl = bundle.getString("googlemaplocationurl");
                    double loclat = bundle.getDouble("loclat");
                    double loclong = bundle.getDouble("loclong");
                    getGoogleAddressUsingMapApi(loclat, loclong);
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private void startCallActivity(String callno) {
        Intent callIntent = new Intent(
                Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + callno)); // NO I18N
        startActivity(callIntent);
    }

    private void openGooglemaps(String url) {
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(url));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void getGoogleAddressUsingMapApi(double customerlatitude, double customerLongitutde) {
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(customerlatitude+","+customerLongitutde));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

}