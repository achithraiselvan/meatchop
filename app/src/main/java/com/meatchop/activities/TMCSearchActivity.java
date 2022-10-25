package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.TMCSearchListAdapter;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCMenuItemStockAvlDetails;
import com.meatchop.data.TMCTile;
import com.meatchop.data.TMCUser;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCDataCatalog;
import com.meatchop.widget.TMCEditText;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TMCSearchActivity extends AppCompatActivity {

    private TMCEditText tmcsearchbar_edittext;
    private View back_icon;

    private RecyclerView tmcsearchdetails_recyclerview;
    private TMCSearchListAdapter tmcSearchListAdapter;
    private BottomNavigationView bottomNavigationView;
    private View tmcsearchdetails_layout;

    private View tmccategories_layout;
    private FlexboxLayout categories_flexboxlayout;

    private static final int HOMEACTIVITY_REQ_CODE = 0;
    private static final int MOREACTIVITY_REQ_CODE = 1;
    private static final int TMCMENUITEMACT_REQ_CODE = 2;
    private static final int LOCATIONSERVICES_REQ_CODE = 3;
    private static final int VERIFYOTP_ACT_REQ_CODE = 4;
    private static final int MARINADE_ACT_REQ_CODE = 5;
    private static final int ORDERSUMMARY_ACT_REQ_CODE = 6;
    private static final int MEALINGRED_ACT_REQ_CODE = 7;
    private static final int CONTACTUS_ACT_REQ_CODE = 8;
    private static final int MENUITEMCUSTOMIZATION_ACT_REQ_CODE = 9;

    private String TAG = "TMCSearchActivity";
    private ArrayList<TMCTile> tmcTileArrayList;
    private boolean islocationupdated;
    private boolean isUserLoggedIn;
    private SettingsUtil settingsUtil;
    private String vendorkey;
    private ArrayList<TMCMenuItem> tmcMenuItems;
    private boolean isTMCMenuItemsLoaded = false;
    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private String defaultvendorkey;

    private boolean iscallfromtmcmenuitemactivity = false;

    private View loginscreenmask_layout;
    private View loginscreen_layout;
    private View logincontinuebutton_layout;
    private TMCEditText mobile_editext;;

    private String searchstring;
    private View cartitemcount_layout;
    private TMCTextView cartitemcount_text;
    private View cartbtn_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmcsearch);

        getSupportActionBar().hide();
        if (getPackageName().equals("com.meatchop")) {
            islocationupdated = getIntent().getBooleanExtra("islocationupdated", false);
            iscallfromtmcmenuitemactivity = getIntent().getBooleanExtra("iscallfromtmcmenuitemactivity", false);
        }

        settingsUtil = new SettingsUtil(this);
        try {
            TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
            vendorkey = tmcUserAddress.getVendorkey();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if ((vendorkey == null) || (TextUtils.isEmpty(vendorkey))) {
            vendorkey = settingsUtil.getDefaultVendorkey();
        }

        back_icon = findViewById(R.id.back_icon);
        tmcsearchbar_edittext = (TMCEditText) findViewById(R.id.tmcsearchbar_edittext);
        tmcsearchdetails_recyclerview = (RecyclerView) findViewById(R.id.tmcsearchdetails_recyclerview);
        tmcsearchdetails_layout = findViewById(R.id.tmcsearchdetails_layout);

        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganimmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tmccategories_layout = findViewById(R.id.tmccategories_layout);
        categories_flexboxlayout = (FlexboxLayout) findViewById(R.id.categories_flexboxlayout);
        tmcMenuItems = TMCMenuItemCatalog.getInstance().getMenuItemList();
        tmcTileArrayList = TMCDataCatalog.getInstance().getTMCTileList();
        getTMCMenuItemAvlDetailsFromAWS();
     /* if (tmcMenuItems == null) {
            getTMCMenuItems();
        } else {
            createAdapterForMenuItems();
        } */

        addViewToFlexBoxLayout();

        tmcsearchbar_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
             /* if (adapter != null) {
                   adapter.filter(cs.toString());
                } */

                if (!iscallfromtmcmenuitemactivity) {
                    if (TextUtils.isEmpty(cs.toString())) {
                        tmcsearchdetails_layout.setVisibility(View.GONE);
                        tmccategories_layout.setVisibility(View.VISIBLE);
                    } else {
                        tmcsearchdetails_layout.setVisibility(View.VISIBLE);
                        tmccategories_layout.setVisibility(View.GONE);
                    }
                } else {
                    if (TextUtils.isEmpty(cs.toString())) {
                        tmcsearchdetails_layout.setVisibility(View.GONE);
                    } else {
                        tmcsearchdetails_layout.setVisibility(View.VISIBLE);
                    }
                }

             /* if (cs.toString().length() <= 2) {
                    return;
                } */

                if (tmcSearchListAdapter != null) {
                    tmcSearchListAdapter.filter(cs.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.app_bar_search).setChecked(true);
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
                    startHomeScreenActivity();
                } else if (item.getTitle().equals("SEARCH")) {

                } else if (item.getTitle().equals("CART")) {

                } else if (item.getTitle().equals("ACCOUNT")) {
                    startMoreActivity();
                } else if (item.getTitle().equals("SHARE US")) {
                    logEventInGAnalytics("Search Screen Share us clicked", "SHARE US", settingsUtil.getMobile());
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
        if (iscallfromtmcmenuitemactivity) {
            bottomNavigationView.setVisibility(View.GONE);
            tmccategories_layout.setVisibility(View.GONE);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            tmcsearchbar_edittext.requestFocus();
            showKeyboard(tmcsearchbar_edittext);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
            tmccategories_layout.setVisibility(View.VISIBLE);
        }

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        cartbtn_layout = findViewById(R.id.cartbtn_layout);
        cartitemcount_layout = findViewById(R.id.cartitemcount_layout);
        cartitemcount_text = (TMCTextView) findViewById(R.id.cartitemcount_text);

        isUserLoggedIn = AWSMobileClient.getInstance().isSignedIn();
        if (isUserLoggedIn) {
            String mobile = settingsUtil.getMobile();
            String userkey = settingsUtil.getUserkey();
            if ((mobile == null) || (TextUtils.isEmpty(mobile))) {
                AWSMobileClient.getInstance().signOut();
                isUserLoggedIn = false;
            }
            if ((userkey == null) || (TextUtils.isEmpty(userkey))) {
                AWSMobileClient.getInstance().signOut();
                isUserLoggedIn = false;
            }
        }
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

        int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
        if (cartsize > 0) {
            cartitemcount_layout.setVisibility(View.VISIBLE);
            cartitemcount_text.setText("" + cartsize);
        } else {
            cartitemcount_layout.setVisibility(View.GONE);
        }

        loginscreen_layout = findViewById(R.id.loginscreen_layout);
        loginscreenmask_layout = findViewById(R.id.loginscreenmask_layout);
        logincontinuebutton_layout = findViewById(R.id.logincontinuebutton_layout);
        mobile_editext = (TMCEditText) findViewById(R.id.mobile_editext);
        logincontinuebutton_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mobile_editext.getText().toString();
                hideKeyboard(mobile_editext);
                if (TextUtils.isEmpty(text)) {
                    showTMCAlert(R.string.invalidmobile_title, R.string.mobilenoinvalid_msg);
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
        });

        loginscreenmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLoginScreen();
            }
        });
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(tmcsearchbar_edittext);
        if (iscallfromtmcmenuitemactivity) {
            setResult(RESULT_OK);
            finish();
        } else {
            startHomeScreenActivity();
        }
    }

    private void startContactusActivity() {
        Intent intent = new Intent(TMCSearchActivity.this, ContactusActivity.class);
        startActivityForResult(intent, CONTACTUS_ACT_REQ_CODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

    private void startMoreActivity() {
        Intent intent = new Intent(TMCSearchActivity.this, MoreActivity.class);
        startActivityForResult(intent, MOREACTIVITY_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    private void startHomeScreenActivity() {
        Intent intent = new Intent(TMCSearchActivity.this, HomeScreenActivity.class);
        startActivityForResult(intent, HOMEACTIVITY_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(TMCSearchActivity.this, title, message,
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


    private Handler createHandlerForSearchListAdapter() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("hidekeyboard")) {
                    hideKeyboard(tmcsearchbar_edittext);
                } else if (menutype.equalsIgnoreCase("openloginscreen")) {
                    hideKeyboard(tmcsearchbar_edittext);
                    showLoginScreen();
                } else if (menutype.equalsIgnoreCase("openlocationscreen")) {
                    hideKeyboard(tmcsearchbar_edittext);
                    startLocationServicesActivity();
                } else if (menutype.equalsIgnoreCase("openmarinadescreen")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    String marinadelinkedcodes = bundle.getString("marinadelinkedcodes");
                    if ((marinadelinkedcodes != null) && (!TextUtils.isEmpty(marinadelinkedcodes))) {
                        startMarinadeActivity(menuitemkey, marinadelinkedcodes);
                    }
                } else if (menutype.equalsIgnoreCase("multiplecustomizationsalert")) {
                    showMultipleCustomizationAlert();
                } else if (menutype.equalsIgnoreCase("openmealkitscreen")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    startMealkitActivity(menuitemkey);
                } else if (menutype.equalsIgnoreCase("changecartcount")) {
                    hideKeyboard(tmcsearchbar_edittext);
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartitemcount_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText("" + cartsize);
                    } else {
                        cartitemcount_layout.setVisibility(View.GONE);
                    }
                } else if (menutype.equalsIgnoreCase("openmenuitemcustomizationactivity")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    startMenuItemCustomizationActivity(menuitemkey);
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private void startMenuItemCustomizationActivity(String menuitemkey) {
        Intent intent = new Intent(TMCSearchActivity.this, MenuItemCustomizationActivity.class);
        intent.putExtra("vendorkey", vendorkey);
        intent.putExtra("menuitemkey", menuitemkey);
        startActivityForResult(intent, MENUITEMCUSTOMIZATION_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void startMealkitActivity(String menuitemkey) {
        Intent intent = new Intent(TMCSearchActivity.this, MealingredientsActivity.class);
        intent.putExtra("menuitemkey", menuitemkey);
        startActivityForResult(intent, MEALINGRED_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void startMarinadeActivity(String menuitemkey, String marinadelinkedcodes) {
        Intent intent = new Intent(TMCSearchActivity.this, MarinadeActivity.class);
        intent.putExtra("menuitemkey", menuitemkey);
        intent.putExtra("marinadelinkedcodes", marinadelinkedcodes);
        startActivityForResult(intent, MARINADE_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }


    private void startTMCMenuItemActivity(String tmcctgyname, String tmcsubctgyname, String tmcsubctgykey) {
        Intent intent = new Intent(TMCSearchActivity.this, TMCMenuItemActivity.class);
        intent.putExtra("tmcctgyname", tmcctgyname);
        intent.putExtra("tmcsubctgyname", tmcsubctgyname);
        intent.putExtra("tmcsubctgykey", tmcsubctgykey);
        intent.putExtra("islocationupdated", islocationupdated);
        intent.putExtra("defaultvendorkey", defaultvendorkey);
        startActivityForResult(intent, TMCMENUITEMACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

    private HashMap<String, TMCTile> tmcTileHashMap;
    private void addViewToFlexBoxLayout() {
        if ((tmcTileArrayList == null) || (tmcTileArrayList.size() <= 0)) {
            return;
        }
        Collections.sort(tmcTileArrayList);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tmcTileHashMap = new HashMap<String, TMCTile>();
        for (int i=0; i<tmcTileArrayList.size(); i++) {
            TMCTile tmcTile = tmcTileArrayList.get(i);
            tmcTileHashMap.put(tmcTile.getTmcsubctgyname(), tmcTile);
        }

        for (int i=0; i<tmcTileArrayList.size(); i++) {
            TMCTile tmcTile = tmcTileArrayList.get(i);
            if (tmcTile.getName().equalsIgnoreCase("Offers")) {
                continue;
            }
            String tmcsubctgyname = tmcTile.getTmcsubctgyname();
            View flexboxitemview = layoutInflater.inflate(R.layout.tmcctgy_flexboxitem, null);
            View subctgyname_layout = flexboxitemview.findViewById(R.id.subctgyname_layout);
            TMCTextView subctgyname_text = flexboxitemview.findViewById(R.id.subctgyname_text);
            subctgyname_text.setText(tmcTile.getName());
            subctgyname_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tmcTileHashMap != null) {
                        TMCTile tmcTile1 = tmcTileHashMap.get(tmcsubctgyname);
                        startTMCMenuItemActivity(tmcTile1.getTmcctgyname(), tmcsubctgyname, tmcTile1.getTmcsubctgykey());
                    }

                }
            });
            categories_flexboxlayout.addView(flexboxitemview);
        }
    }

    private void createAdapterForMenuItems() {
        if (tmcMenuItems == null) { return; }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        tmcsearchdetails_recyclerview.setLayoutManager(layoutManager);
        ArrayList<TMCMenuItem> tmcMenuItemsForAdapter = new ArrayList<TMCMenuItem>();
        for (int i=0; i<tmcMenuItems.size(); i++) {
            TMCMenuItem tmcMenuItem = tmcMenuItems.get(i);
            if ((tmcMenuItem != null) && (tmcMenuItem.isShowInApp())) {
                tmcMenuItemsForAdapter.add(tmcMenuItem);
            }
        }

        tmcSearchListAdapter = new TMCSearchListAdapter(getApplicationContext(), tmcMenuItemsForAdapter);
        tmcSearchListAdapter.setHandler(createHandlerForSearchListAdapter());
        tmcsearchdetails_recyclerview.setAdapter(tmcSearchListAdapter);
        if (tmcsearchbar_edittext != null) {
            String searchstring = tmcsearchbar_edittext.getText().toString();
            if ((searchstring != null) && !(TextUtils.isEmpty(searchstring))) {
                tmcSearchListAdapter.filter(searchstring);
            }
        }
    }

    private void getTMCMenuItemAvlDetailsFromAWS() {
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETTMCMENUITEMAVLDETAILSFORSTORE + "?vendorkey=" + vendorkey;
     // Log.d(TAG, "getTMCMenuItemAvlDetailsFromAWS url "+url);
        TMCMenuItemCatalog.getInstance().clearMenuItemStockAvlDetails();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TMCMenuItemStockAvlDetails menuItemStockAvlDetails = new TMCMenuItemStockAvlDetails(jsonObject1);
                                    TMCMenuItemCatalog.getInstance().addMenuItemStockAvlDetails(menuItemStockAvlDetails);
                                }
                                int size = TMCMenuItemCatalog.getInstance().menuItemSize();
                                if (size <= 0) {
                                    TMCMenuItemCatalog.getInstance().clearMenuItems();
                                    getTMCMenuItems();
                                } else {
                                    Collections.sort(tmcMenuItems);
                                    createAdapterForMenuItems();
                                    hideLoadingAnim();
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
                        getTMCMenuItemAvlDetailsFromAWS();
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

    private void getTMCMenuItems() {
        showLoadingAnim();
        // String url = TMCRestClient.AWS_GETTMCMENUITEMS + "?storeid=" + vendorkey + "&subctgyid=" + selectedtmcsubctgykey;
        String url = TMCRestClient.AWS_GETTMCMENUITEMSFORSTORE + "?storeid=" + vendorkey;
     // Log.d(TAG, "getTMCMenuItems url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                         // Log.d(TAG, "getTMCMenuItems jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            tmcMenuItems = new ArrayList<TMCMenuItem>();
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TMCMenuItem tmcMenuItem = new TMCMenuItem(jsonObject1);
                                    // tmcMenuItems.add(tmcMenuItem);
                                    TMCMenuItemCatalog.getInstance().addMenuItem(tmcMenuItem);
                                }
                                tmcMenuItems = TMCMenuItemCatalog.getInstance().getMenuItemList();
                                hideLoadingAnim();
                                Collections.sort(tmcMenuItems);
                                createAdapterForMenuItems();

                     /* isTMCMenuItemsLoaded = true;
                        if (isSearchMenuItems) {
                            if (tmcSearchListAdapter != null) {
                                tmcSearchListAdapter.filter(searchString);
                            }
                            isSearchMenuItems = false;
                        } */
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganim_layout.setVisibility(View.VISIBLE);
                loadinganimmask_layout.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
    }

    private void hideLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.GONE);
                loadinganim_layout.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }

    private void showMultipleCustomizationAlert() {
        int title = R.string.removeitemfromcarttitle;
        int message = R.string.multiplecustomizationsalert;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(TMCSearchActivity.this, title, message,
                        R.string.yes_capt, R.string.no_capt,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                startOrderSummaryActivity();
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
        bottomNavigationView.setVisibility(View.GONE);
        loginscreen_layout.setVisibility(View.VISIBLE);
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
        bottomNavigationView.setVisibility(View.VISIBLE);
        loginscreen_layout.startAnimation(bottomDown);
        loginscreenmask_layout.setVisibility(View.GONE);
    }

    private void startOrderSummaryActivity() {
        Intent intent = new Intent(TMCSearchActivity.this, OrderSummaryActivity.class);
        intent.putExtra("vendorkey", vendorkey);
        startActivityForResult(intent, ORDERSUMMARY_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }


    private void startLocationServicesActivity() {
        showLoadingAnim();
        Intent intent =new Intent(TMCSearchActivity.this, LocationServicesActivity.class);
        intent.putExtra("calltype", LocationServicesActivity.CALLTYPE_TMCSEARCHSCREEN);
        startActivityForResult(intent, LOCATIONSERVICES_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    private void startVerifyOTPActivity(String mobileno) {
        Intent intent = new Intent(TMCSearchActivity.this, VerifyOTPActivity.class);
        intent.putExtra("mobileno", mobileno);
        startActivityForResult(intent, VERIFYOTP_ACT_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case VERIFYOTP_ACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    boolean loginsuccess = data.getBooleanExtra("loginsuccess", false);
                 // Log.d(TAG, "VERIFYOTP_ACT_REQ_CODE loginsuccess "+loginsuccess);
                    if (loginsuccess) {
                        startLocationServicesActivity();
                    }
                }
                hideLoadingAnim();
                hideLoginScreen();
                break;

            case LOCATIONSERVICES_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean isLocationUpdated = data.getBooleanExtra("isLocationUpdated", false);
                        if (isLocationUpdated) {
                            String newvendorkey = data.getStringExtra("vendorkey");
                            if ((newvendorkey != null) && (!TextUtils.isEmpty(newvendorkey))) {
                                vendorkey = newvendorkey;
                             // getTMCMenuItems();
                                getTMCMenuItemAvlDetailsFromAWS();
                            }
                        } else {
                            hideLoadingAnim();
                        }
                    } else {
                        hideLoadingAnim();
                    }
                } else {
                    hideLoadingAnim();
                }
                break;

            case MARINADE_ACT_REQ_CODE :
                if (data != null) {
                    Log.d(TAG, "onActivityResult MARINADE_ACT_REQ_CODE ");
                    boolean additemclicked = data.getBooleanExtra("additemclicked", false);
                    if (additemclicked) {
                        String marinademenuitemkey = data.getStringExtra("marinademenuitemkey");
                        // Log.d(TAG, "marinademenuitemkey "+marinademenuitemkey);
                        TMCMenuItem marinadetmcMenuItem = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(marinademenuitemkey);

                        if (tmcSearchListAdapter != null) {
                            if (marinadetmcMenuItem != null) {
                                int selqty = TMCMenuItemCatalog.getInstance().getMarinadeSelectedQty(marinadetmcMenuItem.getAwsKey());
                                tmcSearchListAdapter.updateLastSelectedCartCountTextView(selqty);
                            }
                        }
                        int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                        if (cartsize > 0) {
                            cartitemcount_layout.setVisibility(View.VISIBLE);
                            cartitemcount_text.setText("" + cartsize);
                        } else {
                            cartitemcount_layout.setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case MEALINGRED_ACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    boolean additemclicked = data.getBooleanExtra("additemclicked", false);
                    if (additemclicked) {
                        int itemcount = data.getIntExtra("itemcount", 0);
                        if (tmcSearchListAdapter != null) {
                            tmcSearchListAdapter.updateLastSelectedCartCountTextView(itemcount);
                        }

                        int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                        if (cartsize > 0) {
                            cartitemcount_layout.setVisibility(View.VISIBLE);
                            cartitemcount_text.setText("" + cartsize);
                        } else {
                            cartitemcount_layout.setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case ORDERSUMMARY_ACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean quantitychanged = data.getBooleanExtra("quantitychanged", false);
                        boolean forcelogin = data.getBooleanExtra("forcelogin", false);
                        if (forcelogin) {
                            showLoginScreen();
                            hideLoadingAnim();
                            return;
                        }
                        // Log.d(TAG, "quantitychanged " +quantitychanged);
                        if (quantitychanged) {
                            getTMCMenuItems();
                        }
                    }
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartitemcount_text.setText("" + cartsize);
                    } else {
                        cartitemcount_text.setText("");
                    }
                }
                break;

            case MENUITEMCUSTOMIZATION_ACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean cutsweightitemadded = data.getBooleanExtra("cutsweightitemadded", false);
                        if (cutsweightitemadded) {
                            String cutsandweightmenuitemkey = data.getStringExtra("cutsandweightmenuitemkey");
                            TMCMenuItem cutsweightmenuitem = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(cutsandweightmenuitemkey);
                            if (tmcSearchListAdapter != null) {
                                if (cutsweightmenuitem != null) {
                                    int selqty = TMCMenuItemCatalog.getInstance().getCutsAndWeightSelectedQty(cutsweightmenuitem.getAwsKey());
                                    tmcSearchListAdapter.updateLastSelectedCartCountTextView(selqty);
                                }
                            }
                        }
                    }
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartitemcount_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText("" + cartsize);
                    } else {
                        cartitemcount_layout.setVisibility(View.GONE);
                    }

                }
                break;


            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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


 /* private void preLoadTMCMenuItems() {
        String url1 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickenskinless_menuitem.jpg?alt=media&token=07947ac4-737a-4df1-8e0b-c6f25c081e0d";
        String url2 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickendrumstick_menuitem.jpg?alt=media&token=8ee49528-503f-4246-b953-94999e4645bd";
        String url3 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickenmince_menuitem.jpg?alt=media&token=8b923a8c-5f24-462b-a7de-d7ab572561cb";
        String url4 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickenbreast_menuitem.jpg?alt=media&token=d2af0421-2e48-4ef0-806c-cb414b6e7eb4";
        String url5 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickenlollipop_menuitem.jpg?alt=media&token=ddb89820-234f-4503-9bf2-bade4d98d799";
        tmcMenuItems = new ArrayList<TMCMenuItem>();
        TMCMenuItem tmcmenuItem1 = new TMCMenuItem();
        tmcmenuItem1.setKey("1");
        tmcmenuItem1.setItemname("Chicken Skinless");
        tmcmenuItem1.setNetweight("500g");
        tmcmenuItem1.setItemcalories(410);
        tmcmenuItem1.setTmcprice(185);
        tmcmenuItem1.setApplieddiscpercentage(20);
        tmcmenuItem1.setImageurl(url1);

        TMCMenuItem tmcmenuItem2 = new TMCMenuItem();
        tmcmenuItem2.setKey("2");
        tmcmenuItem2.setItemname("Chicken Drumstick");
        tmcmenuItem2.setNetweight("500g");
        tmcmenuItem2.setItemcalories(380);
        tmcmenuItem2.setTmcprice(210);
        tmcmenuItem2.setApplieddiscpercentage(20);
        tmcmenuItem2.setImageurl(url2);

        TMCMenuItem tmcmenuItem3 = new TMCMenuItem();
        tmcmenuItem3.setKey("3");
        tmcmenuItem3.setItemname("Chicken Mince");
        tmcmenuItem3.setNetweight("300g");
        tmcmenuItem3.setItemcalories(410);
        tmcmenuItem3.setTmcprice(210);
        tmcmenuItem3.setApplieddiscpercentage(20);
        tmcmenuItem3.setImageurl(url3);


        tmcMenuItems.add(tmcmenuItem1); tmcMenuItems.add(tmcmenuItem2);
        tmcMenuItems.add(tmcmenuItem3);
        Collections.sort(tmcMenuItems);
    }
*/


}
