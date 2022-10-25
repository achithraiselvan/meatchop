package com.meatchop.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.TMCCtgyBottomUpAdapter;
import com.meatchop.data.TMCCtgy;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCMenuItemStockAvlDetails;
import com.meatchop.data.TMCSubCtgy;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.smsreceiver.SmsBroadcastReceiver;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCconstants;
import com.meatchop.widget.AnimatedExpandableListView;
import com.meatchop.widget.StaticMealKitPane;
import com.meatchop.widget.StaticMenuItemPane;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCDataCatalog;
import com.meatchop.widget.TMCEditText;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import cz.msebera.android.httpclient.Header;

public class TMCMenuItemActivity extends BaseActivity implements View.OnClickListener {

    private static final int ORDERSUMMARY_ACT_REQ_CODE = 0;
    private static final int MEALINGRED_ACT_REQ_CODE = 1;
    private static final int VERIFYOTP_ACT_REQ_CODE = 2;
    private static final int LOCATIONSERVICES_REQ_CODE = 3;
    private static final int SEARCHACTIVITY_REQ_CODE = 4;
    private static final int MARINADE_ACT_REQ_CODE = 5;
    private static final int MENUITEMCUSTOMIZATION_ACT_REQ_CODE = 6;
    private String TAG = "TMCMenuItemActivity";
    private View back_icon;
    private View changemenuctgy_layout;
    private TMCTextView selectedctgy_text;
    private View searchbtn_layout;
    private View cartbtn_layout;

    private HashMap<String, TMCCtgy> tmcCtgyHashMap;
    private ArrayList<String> tmcCtgyNames;

    private AnimatedExpandableListView expandableListView;

    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private View menuctgymask_layout;
    private View menuctgy_layout;

    private TMCCtgyBottomUpAdapter tmcCtgyAdapter;
    private int expandablelistviewgroupposition = 0;

    private LinearLayout detailFields;
    private StaticMenuItemPane menuItemPane;
    private ArrayList<TMCMenuItem> tmcMenuItems;
    private HashMap<String, TMCMenuItem> tmcMenuItemHashMap;

    private LinearLayout mealkit_detailsFields;
    private StaticMealKitPane mealKitPane;

    private View cartitemcount_layout;
    private TMCTextView cartitemcount_text;

    private String vendorkey;
    private String selectedtmcctgyname;
    private String selectedtmcsubctgyname;
    private String selectedtmcsubctgykey;

    private HorizontalScrollView tmcsubctgy_horizontalscrollview;
    private LinearLayout tmcsubctgy_fields_area;

    private View loginscreenmask_layout;
    private View loginscreen_layout;
    private View loginsignupbtn_layout;
    private ImageView loginimageview;
    private TMCEditText mobile_editext;
    private View logincontinuebutton_layout;
    private boolean isUserLoggedIn = false;
    private boolean isLocationUpdated = false;
    private View selectlocationbtn_layout;
    private SettingsUtil settingsUtil;

    private TMCTextView cart_totalamount;
    private View cartbottombtns_layout;
    private Tracker tmcTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tmcmenu_item);

        settingsUtil = new SettingsUtil(this);

        try {
            String defaultaddress = settingsUtil.getDefaultAddress();
            if ((defaultaddress != null) && !(TextUtils.isEmpty(defaultaddress))) {
                TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
                vendorkey = tmcUserAddress.getVendorkey();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if ((vendorkey == null) || (TextUtils.isEmpty(vendorkey))) {
            vendorkey = settingsUtil.getDefaultVendorkey();
        }

     // Log.d(TAG, "package name "+getCallingActivity().getPackageName());

        if (getPackageName().equals("com.meatchop")) {
            selectedtmcctgyname = getIntent().getStringExtra("tmcctgyname");
            selectedtmcsubctgyname = getIntent().getStringExtra("tmcsubctgyname");
            selectedtmcsubctgykey = getIntent().getStringExtra("tmcsubctgykey");
            isLocationUpdated = getIntent().getBooleanExtra("islocationupdated", false);
        }

        back_icon = findViewById(R.id.back_icon);
        changemenuctgy_layout = findViewById(R.id.changemenuctgy_layout);
        selectedctgy_text = (TMCTextView) findViewById(R.id.selectedctgy_text);
        selectedctgy_text.setText(selectedtmcctgyname);
        menuctgy_layout = findViewById(R.id.menuctgy_layout);
        menuctgymask_layout = findViewById(R.id.menuctgymask_layout);
        searchbtn_layout = findViewById(R.id.searchbtn_layout);
        cartbtn_layout = findViewById(R.id.cartbtn_layout);
        expandableListView = (AnimatedExpandableListView) findViewById(R.id.expandableListView);
        menuctgymask_layout.setOnClickListener(this);
        back_icon.setOnClickListener(this);
        detailFields = (LinearLayout) findViewById(R.id.detail_fields_area);
        mealkit_detailsFields = (LinearLayout) findViewById(R.id.mealkit_detail_fields_area);

        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout.setOnClickListener(this);

        back_icon.setOnClickListener(this);
        changemenuctgy_layout.setOnClickListener(this);
        searchbtn_layout.setOnClickListener(this);
        cartbtn_layout.setOnClickListener(this);

        cartitemcount_layout = findViewById(R.id.cartitemcount_layout);
        cartitemcount_text = (TMCTextView) findViewById(R.id.cartitemcount_text);
        cart_totalamount = (TMCTextView) findViewById(R.id.cart_totalamount);
        cartbottombtns_layout = findViewById(R.id.cartbottombtns_layout);

        int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
        if (cartsize > 0) {
            cartbottombtns_layout.setVisibility(View.VISIBLE);
            cartitemcount_text.setText(cartsize + " Items");
            double totalamount = TMCMenuItemCatalog.getInstance().getCartAmount();
            if (totalamount > 0) {
                setTotalAmountToCartTextView(totalamount);
            }
        } else {
            cartbottombtns_layout.setVisibility(View.GONE);
        }

        tmcsubctgy_horizontalscrollview = (HorizontalScrollView) findViewById(R.id.tmcsubctgy_horizontalscrollview);
        tmcsubctgy_fields_area = (LinearLayout) findViewById(R.id.tmcsubctgy_fields_area);

        loginscreenmask_layout = findViewById(R.id.loginscreenmask_layout);
        loginscreen_layout = findViewById(R.id.loginscreen_layout);
        loginimageview = findViewById(R.id.loginimageview);
        mobile_editext = findViewById(R.id.mobile_editext);
        logincontinuebutton_layout = findViewById(R.id.logincontinuebutton_layout);
        loginscreenmask_layout.setOnClickListener(this);
        logincontinuebutton_layout.setOnClickListener(this);
        loginscreen_layout.setOnClickListener(this);
        selectlocationbtn_layout = findViewById(R.id.selectlocationbtn_layout);
        selectlocationbtn_layout.setOnClickListener(this);

        try {
            if ((settingsUtil.getMobile() == null) || (TextUtils.isEmpty(settingsUtil.getMobile()))) {
                isUserLoggedIn = false;
            } else {
                isUserLoggedIn = AWSMobileClient.getInstance().isSignedIn();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

     /* if (isUserLoggedIn) {
            loginsignupbtn_layout.setVisibility(View.GONE);
            if (isLocationUpdated) {
                selectlocationbtn_layout.setVisibility(View.GONE);
            } else {
                selectlocationbtn_layout.setVisibility(View.VISIBLE);
            }
        } else {
            loginsignupbtn_layout.setVisibility(View.VISIBLE);
            selectlocationbtn_layout.setVisibility(View.GONE);
        } */

        showLoadingAnim();
        getTMCSubCtgyList();

     /* if (tmcctgyname.equalsIgnoreCase("TMC Meal Kits")) {
            tmcmealkit_ctgyview.setVisibility(View.VISIBLE);
            detailFields.setVisibility(View.GONE);
            mealkit_detailsFields.setVisibility(View.VISIBLE);
            new MealKitDetailsAsyncTask().execute();
        } else {
            tmcmealkit_ctgyview.setVisibility(View.GONE);
            detailFields.setVisibility(View.VISIBLE);
            mealkit_detailsFields.setVisibility(View.GONE);
            new MenuItemDetailsAsyncTask().execute();
        } */
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                 // Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show ();
                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (menuctgy_layout.getVisibility() == View.VISIBLE) {
            hideMenuCtgyLayout();
            return;
        }
        if (loginscreen_layout.getVisibility() == View.VISIBLE) {
            hideLoginScreen();
            return;
        }
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(0, 0);
     // slideToRight();
    }


    private void clearMenuItemsFromStaticMenuItemPane() {
        if (menuItemPane != null) {
            menuItemPane.removeAllViews();
            detailFields.removeView(menuItemPane);
            menuItemPane = null;
        }
    }

    private class MenuItemDetailsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (menuItemPane != null) {
                menuItemPane.removeAllViews();
                detailFields.removeView(menuItemPane);
                menuItemPane = null;
            }
            menuItemPane = new StaticMenuItemPane(getApplicationContext());
         /* if ((isUserLoggedIn) && (isLocationUpdated)) {
                menuItemPane.setisAllowUserToAddCart(true);
            } else {
                menuItemPane.setisAllowUserToAddCart(false);
            } */

            ArrayList<TMCMenuItem> tmcMenuItems = TMCMenuItemCatalog.getInstance().getMenuItemList(selectedtmcsubctgykey);
            if ((tmcMenuItems == null) || (tmcMenuItems.size() <= 0)) {
                return;
            }
            ArrayList<TMCMenuItem> availableItems = new ArrayList<TMCMenuItem>();
            ArrayList<TMCMenuItem> notAvailableItems = new ArrayList<TMCMenuItem>();
            for (int i=0; i<tmcMenuItems.size(); i++) {
                TMCMenuItem tmcMenuItem = tmcMenuItems.get(i);
                if (tmcMenuItem.isItemavailability(settingsUtil.isInventoryCheck())) {
                    availableItems.add(tmcMenuItem);
                } else {
                    notAvailableItems.add(tmcMenuItem);
                }
            }
            Collections.sort(availableItems);

         // Collections.sort(tmcMenuItems);
            for (int i=0; i<availableItems.size(); i++) {
                TMCMenuItem tmcMenuItem = availableItems.get(i);
                if ((tmcMenuItem != null) && (tmcMenuItem.isShowInApp())) {
                    menuItemPane.addFormItem(tmcMenuItem);
                }
            }
            for (int i=0; i<notAvailableItems.size(); i++) {
                TMCMenuItem tmcMenuItem = notAvailableItems.get(i);
                if ((tmcMenuItem != null) && (tmcMenuItem.isShowInApp())) {
                    menuItemPane.addFormItem(tmcMenuItem);
                }
            }
            menuItemPane.setHandler(createStaticMenuItemPaneHandler());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (menuItemPane != null) {
                if (menuItemPane.getParent() == null) {
                    detailFields.addView(menuItemPane);
                }
            }
            hideLoadingAnim();
        }
    }

    private HashMap<String, View> subCtgyHorViewItemMap;
    private void initTMCSubctgyHorizontalScrollView() {
        if (tmcsubctgy_fields_area != null) {
            tmcsubctgy_fields_area.removeAllViews();
        }
        if (subCtgyHorViewItemMap != null) {
            subCtgyHorViewItemMap.clear();
            subCtgyHorViewItemMap = null;
        }
        LayoutInflater mInflater = LayoutInflater.from(this);
        ArrayList<TMCSubCtgy> tmcSubCtgyArrayList = TMCDataCatalog.getInstance().getTMCSubCtgyList(selectedtmcctgyname);
        subCtgyHorViewItemMap = new HashMap<String, View>();
        for (int i=0; i<tmcSubCtgyArrayList.size(); i++) {
            String tmcsubctgy = tmcSubCtgyArrayList.get(i).getSubctgyname();
            View view = mInflater.inflate(R.layout.horscrollview_tmcsubctgy_list_item, tmcsubctgy_fields_area, false);
            TMCTextView tmcsubctgytextview = view.findViewById(R.id.tmcsubctgyname_textview);
            View tmcsubctgyborder_layout = view.findViewById(R.id.tmcsubctgyborder_layout);
            tmcsubctgytextview.setText(tmcsubctgy);
            if (tmcsubctgy.equalsIgnoreCase(selectedtmcsubctgyname)) {
                tmcsubctgyborder_layout.setBackground(getResources().getDrawable(R.drawable.tmcsubctgyselected_border));
                tmcsubctgytextview.setTextColor(getResources().getColor(R.color.white));
            } else {
                tmcsubctgyborder_layout.setBackground(getResources().getDrawable(R.drawable.tmcsubctgy_border));
                tmcsubctgytextview.setTextColor(getResources().getColor(R.color.primary_text_color));
            }
            tmcsubctgy_fields_area.addView(view);
            subCtgyHorViewItemMap.put(tmcsubctgy, view);
            tmcsubctgyborder_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, TMCSubCtgy> tmcSubCtgyMap = TMCDataCatalog.getInstance().getTmcSubctgyMap();
                    if (tmcSubCtgyMap != null) {
                        TMCSubCtgy tmcSubCtgy = tmcSubCtgyMap.get(tmcsubctgy);
                        if (selectedtmcsubctgyname.equalsIgnoreCase(tmcsubctgy)) {
                            return;
                        }
                        selectedtmcsubctgykey = tmcSubCtgy.getSubctgykey();
                        selectedtmcsubctgyname = tmcSubCtgy.getSubctgyname();
                     // Log.d(TAG, "selectedtmcsubctgykey "+selectedtmcsubctgykey+" tmcsubctgy name "+tmcsubctgy);
                        showLoadingAnim();
                        clearMenuItemsFromStaticMenuItemPane();
                        getTMCMenuItems();
                     // Log.d(TAG, "subCtgyHorViewItemMap "+subCtgyHorViewItemMap);
                        if ((subCtgyHorViewItemMap != null) && (subCtgyHorViewItemMap.size() > 0)) {
                            Object[] keys = subCtgyHorViewItemMap.keySet().toArray();
                            for (int i=0; i<keys.length; i++) {
                                String keysubctgyname = (String) keys[i];
                             // Log.d(TAG, "keysubctgyname "+keysubctgyname+" selectedtmcsubctgyname "+selectedtmcsubctgyname);
                                View subctgyview = (View) subCtgyHorViewItemMap.get(keysubctgyname);
                                View tmcsubctgyborder_layout = subctgyview.findViewById(R.id.tmcsubctgyborder_layout);
                                TMCTextView tmcsubctgytextview = subctgyview.findViewById(R.id.tmcsubctgyname_textview);
                                if (keysubctgyname.equalsIgnoreCase(selectedtmcsubctgyname)) {
                                    tmcsubctgyborder_layout.setBackground(getResources().getDrawable(R.drawable.tmcsubctgyselected_border));
                                    tmcsubctgytextview.setTextColor(getResources().getColor(R.color.white));
                                } else {
                                    tmcsubctgyborder_layout.setBackground(getResources().getDrawable(R.drawable.tmcsubctgy_border));
                                    tmcsubctgytextview.setTextColor(getResources().getColor(R.color.primary_text_color));

                                }
                            }
                        }
                    }
                }
            });
        }
     // Log.d(TAG, "selectedtmcsubctgyname "+selectedtmcsubctgyname);

        View selectedview = subCtgyHorViewItemMap.get(selectedtmcsubctgyname);
        if (selectedview != null) {
            focusOnView(tmcsubctgy_horizontalscrollview, selectedview, selectedtmcsubctgyname);
        }
    }

    private final void focusOnView(final HorizontalScrollView scroll, final View view, String subctgyname) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        int vLeft = view.getLeft();
                        int vRight = view.getRight();
                        int sWidth = scroll.getWidth();
                        scroll.smoothScrollTo(((vLeft + vRight - sWidth) / 2), 0);
                    }
                }, 100L);

            }
        });
    }

 /* private class MealKitDetailsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mealKitPane != null) {
                mealKitPane.removeAllViews();
                mealkit_detailsFields.removeView(mealKitPane);
                mealKitPane = null;
            }
            mealKitPane = new StaticMealKitPane(getApplicationContext());
            for (int i=0; i<mealKitItems.size(); i++) {
                TMCMenuItem tmcMenuItem = mealKitItems.get(i);
                if (tmcMenuItem != null) {
                    mealKitPane.addFormItem(tmcMenuItem);
                }
            }
            mealKitPane.setHandler(createStaticMealKitPaneHandler());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mealKitPane != null) {
                if (mealKitPane.getParent() == null) {
                    mealkit_detailsFields.addView(mealKitPane);
                }
            }
            hideLoadingAnim();

        }
    } */

    private void createTMCCtgyBottomAdapter() {
        ArrayList<String> tmcCtgyNameList = TMCDataCatalog.getInstance().getTmcCtgyNameList();
        TreeMap<String, ArrayList<TMCSubCtgy>> tmcCtgySubCtgyMap = TMCDataCatalog.getInstance().getTmcCtgySubCtgyMap();
     // Log.d(TAG, "tmcctgyname "+selectedtmcctgyname+" tmcsubctgyname "+selectedtmcsubctgyname);
     // Log.d(TAG, "tmcCtgyNameList "+tmcCtgyNameList+" tmcCtgySubCtgyMap "+tmcCtgySubCtgyMap);
        tmcCtgyAdapter = new TMCCtgyBottomUpAdapter(this, selectedtmcctgyname, selectedtmcsubctgyname,
                tmcCtgyNameList, tmcCtgySubCtgyMap);

        tmcCtgyAdapter.setHandler(createTMCCtgyBottomUpHandler());
        expandableListView.setAdapter(tmcCtgyAdapter);
        expandableListView.setGroupIndicator(null);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.collapseGroup(groupPosition);
                    tmcCtgyAdapter.setSelectedGroupPosition(-1);
                    tmcCtgyAdapter.notifyDataSetChanged();
                } else {
                    expandableListView.expandGroup(groupPosition, false);
                    // expandableListView.expandGroupWithAnimation(groupPosition);
                    tmcCtgyAdapter.setSelectedGroupPosition(groupPosition);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            tmcCtgyAdapter.notifyDataSetChanged();
                        }
                    }, 300);
                }

                return true;
            }
        });

    }

    private Handler createTMCCtgyBottomUpHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("itemclick")) {
                    selectedtmcsubctgyname = bundle.getString("subctgyname");
                    selectedtmcctgyname = bundle.getString("tmcctgyname");
                    selectedctgy_text.setText(selectedtmcctgyname);
                    HashMap<String, TMCSubCtgy> tmcSubCtgyMap = TMCDataCatalog.getInstance().getTmcSubctgyMap();
                    selectedtmcsubctgykey = tmcSubCtgyMap.get(selectedtmcsubctgyname).getSubctgykey();
                 // Log.d("TMCMenuItemAct", "createTMCCtgyBottomUpHandler selectedtmcsubctgyname "+selectedtmcsubctgyname
                 //         +" selectedtmcctgyname "+selectedtmcctgyname+" selectedtmcsubctgykey "+selectedtmcsubctgykey);
                 // showLoadingAnim();
                 /* if (selectedtmcctgyname.equalsIgnoreCase("TMC Meal Kits")) {
                        detailFields.setVisibility(View.GONE);
                        mealkit_detailsFields.setVisibility(View.VISIBLE);
                     // new MealKitDetailsAsyncTask().execute();
                    } else {
                        detailFields.setVisibility(View.VISIBLE);
                        mealkit_detailsFields.setVisibility(View.GONE);
                     // new MenuItemDetailsAsyncTask().execute();
                    } */
                    expandableListView.collapseGroup(expandablelistviewgroupposition);
                    clearMenuItemsFromStaticMenuItemPane();
                    getTMCMenuItems();
                    initTMCSubctgyHorizontalScrollView();
                    hideMenuCtgyLayout();
                }

                return false;
            }
        };
        return new Handler(callback);
    }

    private Handler createStaticMenuItemPaneHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("cartupdated")) {
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                 // Log.d(TAG, "cartsize "+cartsize);
                    if (cartsize > 0) {
                        cartbottombtns_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText(cartsize + " Items");
                        double totalamount = TMCMenuItemCatalog.getInstance().getCartAmount();
                        if (totalamount > 0) {
                            setTotalAmountToCartTextView(totalamount);
                        }
                    } else {
                        cartbottombtns_layout.setVisibility(View.GONE);
                    }
                } else if (menutype.equalsIgnoreCase("menuitemclicked")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    // startMealingredientsActivity();
                } else if (menutype.equalsIgnoreCase("openmarinadescreen")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    String marinadelinkedcodes = bundle.getString("marinadelinkedcodes");
                    if ((marinadelinkedcodes != null) && (!TextUtils.isEmpty(marinadelinkedcodes))) {
                        startMarinadeActivity(menuitemkey, marinadelinkedcodes);
                    }
                } else if (menutype.equalsIgnoreCase("openmealkitscreen")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    startMealkitActivity(menuitemkey);
                } else if (menutype.equalsIgnoreCase("openloginscreen")) {
                    showLoginScreen();
                } else if (menutype.equalsIgnoreCase("openlocationscreen")) {
                    startLocationServicesActivity();
                } else if (menutype.equalsIgnoreCase("multiplecustomizationsalert")) {
                    showMultipleCustomizationAlert();
                } else if (menutype.equalsIgnoreCase("openmenuitemcustomizationactivity")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    startMenuItemCustomizationActivity(menuitemkey);
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private void showMenuCtgyLayout() {
        ArrayList<String> tmcCtgyNameList = TMCDataCatalog.getInstance().getTmcCtgyNameList();
        TreeMap<String, ArrayList<TMCSubCtgy>> tmcCtgySubCtgyMap = TMCDataCatalog.getInstance().getTmcCtgySubCtgyMap();
        if (tmcCtgyAdapter == null) {
            tmcCtgyAdapter = new TMCCtgyBottomUpAdapter(this, selectedtmcctgyname, selectedtmcsubctgyname,
                                                                                 tmcCtgyNameList, tmcCtgySubCtgyMap);
            tmcCtgyAdapter.setHandler(createTMCCtgyBottomUpHandler());
            expandableListView.setAdapter(tmcCtgyAdapter);
        } else {
            try {
                expandablelistviewgroupposition = tmcCtgyNameList.indexOf(selectedtmcctgyname);
             // Log.d(TAG, "showMenuCtgyLayout position "+expandablelistviewgroupposition+" tmcctgyname "+selectedtmcctgyname);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            expandableListView.expandGroup(expandablelistviewgroupposition, true);
            tmcCtgyAdapter.notifyDataSetChanged();
        }
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                menuctgymask_layout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(300);
        menuctgy_layout.startAnimation(bottomUp);
        menuctgy_layout.setVisibility(View.VISIBLE);
    }

    private void hideMenuCtgyLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                menuctgy_layout.setVisibility(View.GONE);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(200);
        menuctgy_layout.startAnimation(bottomDown);
        menuctgymask_layout.setVisibility(View.GONE);
    }

    private String selectedmenuitemkey;
    private Handler createStaticMealKitPaneHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("cartupdated")) {
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartbottombtns_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText(cartsize + " Items");
                        double totalamount = TMCMenuItemCatalog.getInstance().getCartAmount();
                        if (totalamount > 0) {
                            setTotalAmountToCartTextView(totalamount);
                        }
                    } else {
                        cartbottombtns_layout.setVisibility(View.GONE);
                    }
                } else if (menutype.equalsIgnoreCase("additemclicked")) {
                    selectedmenuitemkey = bundle.getString("menuitemkey");
                    TMCMenuItem tmcMenuItem = tmcMenuItemHashMap.get(selectedmenuitemkey);
                 // Log.d("TMCMenuItemAct", "tmcMenuItem "+tmcMenuItem+" selectedmenuitemkey "+selectedmenuitemkey);
                    startMealingredientsActivity(tmcMenuItem);
                }

                return false;
            }
        };
        return new Handler(callback);
    }

    private void startMarinadeActivity(String menuitemkey, String marinadelinkedcodes) {
        Intent intent = new Intent(TMCMenuItemActivity.this, MarinadeActivity.class);
        intent.putExtra("menuitemkey", menuitemkey);
        intent.putExtra("marinadelinkedcodes", marinadelinkedcodes);
        startActivityForResult(intent, MARINADE_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void startMealkitActivity(String menuitemkey) {
        Intent intent = new Intent(TMCMenuItemActivity.this, MealingredientsActivity.class);
        intent.putExtra("menuitemkey", menuitemkey);
        startActivityForResult(intent, MEALINGRED_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void startMealingredientsActivity(TMCMenuItem tmcMenuItem) {
        Intent intent = new Intent(TMCMenuItemActivity.this, MealingredientsActivity.class);
        intent.putExtra("tmcmenuitem", tmcMenuItem);
        intent.putExtra("menuitemkey", tmcMenuItem.getKey());
        startActivityForResult(intent, MEALINGRED_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void startMenuItemCustomizationActivity(String menuitemkey) {
        Intent intent = new Intent(TMCMenuItemActivity.this, MenuItemCustomizationActivity.class);
        intent.putExtra("vendorkey", vendorkey);
        intent.putExtra("menuitemkey", menuitemkey);
        startActivityForResult(intent, MENUITEMCUSTOMIZATION_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void startOrderSummaryActivity() {
        Intent intent = new Intent(TMCMenuItemActivity.this, OrderSummaryActivity.class);
        intent.putExtra("vendorkey", vendorkey);
        startActivityForResult(intent, ORDERSUMMARY_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

    private void startTMCSearchActivity() {
        Intent intent = new Intent(TMCMenuItemActivity.this, TMCSearchActivity.class);
        intent.putExtra("islocationupdated", isLocationUpdated);
        intent.putExtra("iscallfromtmcmenuitemactivity", true);
        startActivityForResult(intent, SEARCHACTIVITY_REQ_CODE);
        // overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
        overridePendingTransition(0, 0);
    }

 /* private boolean checkPackageNameAndClassName(Intent intent) {
        ComponentName name = intent.resolveActivity(getPackageManager());
        if (name != null) {
            Log.d(TAG, "package name "+name.getPackageName());
            Log.d(TAG, "class name "+name.getClassName());
        }
        if ( (name.getPackageName().equals("com.google.android.gms")) &&
                (name.getClassName().equals("com.google.android.gms.auth.api.phone.ui.UserConsentPromptActivity")) ) {

        }
        return true;
    } */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MEALINGRED_ACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    boolean additemclicked = data.getBooleanExtra("additemclicked", false);
                    int itemcount = data.getIntExtra("itemcount", 0);
                    menuItemPane.updateLastSelectedCartCountTextView(itemcount);
                    if (additemclicked) {
                        int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                        if (cartsize > 0) {
                            cartbottombtns_layout.setVisibility(View.VISIBLE);
                            cartitemcount_text.setText(cartsize + " Items");
                            double totalamount = TMCMenuItemCatalog.getInstance().getCartAmount();
                            if (totalamount > 0) {
                                setTotalAmountToCartTextView(totalamount);
                            }
                        } else {
                            cartbottombtns_layout.setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case VERIFYOTP_ACT_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    boolean loginsuccess = data.getBooleanExtra("loginsuccess", false);
                    Log.d(TAG, "VERIFYOTP_ACT_REQ_CODE loginsuccess "+loginsuccess);
                    if (loginsuccess) {
                        isUserLoggedIn = true;
                        if (isLocationUpdated) {
                            getTMCMenuItems();
                         // new MenuItemDetailsAsyncTask().execute();
                            selectlocationbtn_layout.setVisibility(View.GONE);
                        } else {
                            selectlocationbtn_layout.setVisibility(View.VISIBLE);
                            startLocationServicesActivity();
                        }
                    }
                }
                hideLoadingAnim();
                hideLoginScreen();
                break;

            case LOCATIONSERVICES_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        isLocationUpdated = data.getBooleanExtra("isLocationUpdated", false);
                        if (isLocationUpdated) {
                            selectlocationbtn_layout.setVisibility(View.GONE);
                            String newvendorkey = data.getStringExtra("vendorkey");
                            if ((newvendorkey != null) && (!TextUtils.isEmpty(newvendorkey))) {
                                vendorkey = newvendorkey;
                                getTMCMenuItems();
                            }
                        } else {
                            hideLoadingAnim();
                            // selectlocationbtn_layout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        hideLoadingAnim();
                    }
                } else {
                    hideLoadingAnim();
                }
                break;

            case ORDERSUMMARY_ACT_REQ_CODE :
                 if (resultCode == RESULT_OK) {
                     if (data != null) {
                         boolean quantitychanged = data.getBooleanExtra("quantitychanged", false);
                         boolean forcelogin = data.getBooleanExtra("forcelogin", false);
                         Log.d(TAG, "quantitychanged "+quantitychanged);
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
                         cartbottombtns_layout.setVisibility(View.VISIBLE);
                         cartitemcount_text.setText(cartsize + " Items");
                         double totalamount = TMCMenuItemCatalog.getInstance().getCartAmount();
                         if (totalamount > 0) {
                             setTotalAmountToCartTextView(totalamount);
                         }
                     } else {
                         cartbottombtns_layout.setVisibility(View.GONE);
                     }
                 }
                 break;

            case SEARCHACTIVITY_REQ_CODE :
                if (resultCode == RESULT_OK) {
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartbottombtns_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText(cartsize + " Items");
                        double totalamount = TMCMenuItemCatalog.getInstance().getCartAmount();
                        if (totalamount > 0) {
                            setTotalAmountToCartTextView(totalamount);
                        }
                    } else {
                        cartbottombtns_layout.setVisibility(View.GONE);
                    }
                }
                break;

            case MARINADE_ACT_REQ_CODE :
                if (data != null) {
                    boolean additemclicked = data.getBooleanExtra("additemclicked", false);
                    if (additemclicked) {
                        String marinademenuitemkey = data.getStringExtra("marinademenuitemkey");
                        // Log.d(TAG, "marinademenuitemkey "+marinademenuitemkey);
                        TMCMenuItem marinadetmcMenuItem = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(marinademenuitemkey);

                      //  Log.d(TAG, "marinadetmcMenuItem "+marinadetmcMenuItem.toString());
                        if (menuItemPane != null) {
                            if (marinadetmcMenuItem != null) {
                                int selqty = TMCMenuItemCatalog.getInstance().getMarinadeSelectedQty(marinadetmcMenuItem.getAwsKey());
                                menuItemPane.updateLastSelectedCartCountTextView(selqty);
                            }
                        }
                        int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                        if (cartsize > 0) {
                            cartbottombtns_layout.setVisibility(View.VISIBLE);
                            cartitemcount_text.setText(cartsize + " Items");
                            double totalamount = TMCMenuItemCatalog.getInstance().getCartAmount();
                            if (totalamount > 0) {
                                setTotalAmountToCartTextView(totalamount);
                            }
                        } else {
                            cartbottombtns_layout.setVisibility(View.GONE);
                        }
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
                            if (menuItemPane != null) {
                                if (cutsweightmenuitem != null) {
                                    int selqty = TMCMenuItemCatalog.getInstance().getCutsAndWeightSelectedQty(cutsweightmenuitem.getAwsKey());
                                    menuItemPane.updateLastSelectedCartCountTextView(selqty);
                                }
                            }
                        }
                    }
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartbottombtns_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText(cartsize + " Items");
                        double totalamount = TMCMenuItemCatalog.getInstance().getCartAmount();
                        if (totalamount > 0) {
                            setTotalAmountToCartTextView(totalamount);
                        }
                    } else {
                        cartbottombtns_layout.setVisibility(View.GONE);
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

 /* private void addTMCCtgyAndSubctgy(String tmcctgyname, TMCSubCtgy tmcSubCtgy) {
        if (tmcCtgyNameList == null) {
            tmcCtgyNameList = new ArrayList<String>();
        }
        if (tmcCtgySubCtgyMap == null) {
            tmcCtgySubCtgyMap = new HashMap<String, ArrayList<TMCSubCtgy>>();
        }
        if (tmcSubCtgyMap == null) {
            tmcSubCtgyMap = new HashMap<String, TMCSubCtgy>();
        }
        if (!tmcCtgyNameList.contains(tmcctgyname)) {
            tmcCtgyNameList.add(tmcctgyname);
        }

        tmcSubCtgyMap.put(tmcSubCtgy.getSubctgyname(), tmcSubCtgy);
        ArrayList<TMCSubCtgy> subctgylist = tmcCtgySubCtgyMap.get(tmcctgyname);
        if (subctgylist == null) {
            subctgylist = new ArrayList<TMCSubCtgy>();
            subctgylist.add(tmcSubCtgy);
            tmcCtgySubCtgyMap.put(tmcctgyname, subctgylist);
        } else {
            subctgylist.add(tmcSubCtgy);
        }
    }

    private ArrayList<TMCSubCtgy> getTMCSubCtgyList(String tmcctgyname) {
        if ((tmcCtgySubCtgyMap == null) || (tmcCtgySubCtgyMap.size() <= 0)) {
            return null;
        }
        ArrayList<TMCSubCtgy> tmcSubCtgyList = tmcCtgySubCtgyMap.get(tmcctgyname);
        Collections.sort(tmcSubCtgyList);
        return tmcSubCtgyList;
    } */

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

    // Login Activity Code starts here
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.changemenuctgy_layout) {
            showMenuCtgyLayout();
        } else if (id == R.id.menuctgymask_layout) {
            hideMenuCtgyLayout();
        } else if (id == R.id.back_icon) {
            onBackPressed();
        } else if (id == R.id.cartbtn_layout) {
            int cartcount = TMCMenuItemCatalog.getInstance().getCartCount();
            if (cartcount > 0) {
                startOrderSummaryActivity();
            }
        } else if (id == R.id.loginsignupbtn_layout) {
            showLoginScreen();
        } else if (id == R.id.loginscreenmask_layout) {
            hideLoginScreen();
        } else if (id == R.id.logincontinuebutton_layout) {
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
        } else if (id == R.id.selectlocationbtn_layout) {
            startLocationServicesActivity();
        } else if (id == R.id.searchbtn_layout) {
            startTMCSearchActivity();
        }
    }

    private void startLocationServicesActivity() {
        showLoadingAnim();
        Intent intent =new Intent(TMCMenuItemActivity.this, LocationServicesActivity.class);
        intent.putExtra("callfromtmcmenitemactivity", true);
        intent.putExtra("calltype", LocationServicesActivity.CALLTYPE_TMCMENUITEM);
        startActivityForResult(intent, LOCATIONSERVICES_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    private void startVerifyOTPActivity(String mobileno) {
        Intent intent = new Intent(TMCMenuItemActivity.this, VerifyOTPActivity.class);
        intent.putExtra("mobileno", mobileno);
        startActivityForResult(intent, VERIFYOTP_ACT_REQ_CODE);
        overridePendingTransition(0, 0);
    }

    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(TMCMenuItemActivity.this, title, message,
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

    private void showMultipleCustomizationAlert() {
        int title = R.string.removeitemfromcarttitle;
        int message = R.string.multiplecustomizationsalert;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(TMCMenuItemActivity.this, title, message,
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
        loginscreen_layout.startAnimation(bottomDown);
        loginscreenmask_layout.setVisibility(View.GONE);
    }

    public void hideKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }

    private void getTMCMenuItems() {
        if (settingsUtil.isInventoryCheck()) {
            getTMCMenuItemAvlDetailsFromAWS();
        } else {
            int size = TMCMenuItemCatalog.getInstance().tmcSubctgyMenuItemSize();
            int subctgymenuitemssize = TMCMenuItemCatalog.getInstance().tmcSubctgyMenuItemSize(selectedtmcsubctgykey);
            if ((size <= 0) || (subctgymenuitemssize <= 0)) {
                getTMCMenuItemsFromAWS();
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        new MenuItemDetailsAsyncTask().execute();
                    }
                }, 200);
            }
        }
    }

 // private ArrayList<String> tmcCtgyNameList;
 // private HashMap<String, ArrayList<TMCSubCtgy>> tmcCtgySubCtgyMap;  // {tmcctgyname -- Arraylist of subctgy}
 // private HashMap<String, TMCSubCtgy> tmcSubCtgyMap;
 // private HashMap<String, ArrayList<TMCMenuItem>> tmcSubCtgyMenuItemMap;  // {tmcsubctgykey -- ArrayList of TMCMenuItem}
    private void getTMCMenuItemsFromAWS() {
     // Log.d(TAG, "getTMCMenuItemsFromAWS showLoadingAnim");
        showLoadingAnim();
     // String url = TMCRestClient.AWS_GETTMCMENUITEMS + "?storeid=" + vendorkey + "&subctgyid=" + selectedtmcsubctgykey;
        String url = TMCRestClient.AWS_GETTMCMENUITEMSFORSTORE + "?storeid=" + vendorkey;
        Log.d(TAG, "getTMCMenuItems url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                     // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            tmcMenuItems = new ArrayList<TMCMenuItem>();
                            if (message.equalsIgnoreCase("success")) {
                                TMCMenuItemCatalog.getInstance().clearMenuItems();
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                 /* if (jsonObject1.has("showinapp")) {
                                        boolean showinapp = jsonObject1.getBoolean("showinapp");
                                        if (!showinapp) {
                                            continue;
                                        }
                                    } */
                                    TMCMenuItem tmcMenuItem = new TMCMenuItem(jsonObject1);
                                    tmcMenuItems.add(tmcMenuItem);
                                    TMCMenuItemCatalog.getInstance().addMenuItem(tmcMenuItem);
                                }
                                Collections.sort(tmcMenuItems);
                                new MenuItemDetailsAsyncTask().execute();
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
                        getTMCMenuItemsFromAWS();
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
     // jsonObjectRequest.setShouldCache(false);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void getTMCMenuItemAvlDetailsFromAWS() {
        String url = TMCRestClient.AWS_GETTMCMENUITEMAVLDETAILSFORSTORE + "?vendorkey=" + vendorkey;
        Log.d(TAG, "getTMCMenuItemAvlDetailsFromAWS url "+url);
     // TMCMenuItemCatalog.getInstance().clearMenuItemStockAvlDetails();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                TMCMenuItemCatalog.getInstance().clearMenuItemStockAvlDetails();
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TMCMenuItemStockAvlDetails menuItemStockAvlDetails = new TMCMenuItemStockAvlDetails(jsonObject1);
                                    TMCMenuItemCatalog.getInstance().addMenuItemStockAvlDetails(menuItemStockAvlDetails);
                                }
                                int size = TMCMenuItemCatalog.getInstance().tmcSubctgyMenuItemSize();
                                int subctgymenuitemssize = TMCMenuItemCatalog.getInstance().tmcSubctgyMenuItemSize(selectedtmcsubctgykey);
                                if ((size <= 0) || (subctgymenuitemssize <= 0)) {
                                    getTMCMenuItemsFromAWS();
                                } else {
                                    new MenuItemDetailsAsyncTask().execute();
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }

    private void getTMCSubCtgyList() {
        int size = TMCDataCatalog.getInstance().tmcCtgySubCtgyMapSize();
        if (size <= 0) {
            getTMCSubCtgyListFromAWS();
        } else {
            createTMCCtgyBottomAdapter();
            getTMCMenuItems();
            initTMCSubctgyHorizontalScrollView();
        }
    }

    private void getTMCSubCtgyListFromAWS() {
        Log.d(TAG, "getTMCSubCtgyListFromAWS method ");
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
                                Log.d(TAG, "getTMCSubCtgyListFromAWS tmcCtgySubCtgyMapSize "+
                                                         TMCDataCatalog.getInstance().getTmcCtgySubCtgyMap().size());
                                createTMCCtgyBottomAdapter();
                            }
                            getTMCMenuItems();
                            initTMCSubctgyHorizontalScrollView();
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

    private void setTotalAmountToCartTextView(double cartamount) {
        String rs = getResources().getString(R.string.Rs);
        String carttotalamt = String.format("%.2f", cartamount);
        cart_totalamount.setText(rs + carttotalamt);
    }


}
