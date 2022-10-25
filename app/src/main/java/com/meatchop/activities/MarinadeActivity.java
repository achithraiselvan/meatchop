package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.meatchop.R;
import com.meatchop.adapters.MarindesListViewAdapter;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCSubCtgy;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.widget.AnimatedExpandableListView;
import com.meatchop.widget.MarinadeExpandableListView;
import com.meatchop.widget.StaticMarinadeSelectionPane;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCDataCatalog;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MarinadeActivity extends BaseActivity {

    private final String TAG = "MarinadeActivity";

    private View back_icon;
    private MarinadeExpandableListView marinades_listview;
    private MarindesListViewAdapter marindesListViewAdapter;

    private String marinademenuitemkey;
    private String meatmenuitemkey;
    private String marinadelinkedcodes;
    private String selectedmarinadelinkcode;

    private View meatnotrequired_layout;
    private View itemnotselected_view;
    private View itemselected_view;

    private View additem_layout;
    private boolean isNoMeatSelected = false;
    private boolean callfromordersummaryactivity = false;
    private TMCTextView itemtotal_textview;
    private double marinadetmcprice;
    private String rupeessign;
    private TMCTextView additem_text;

    private View loadinganimmask_layout;
    private View loadinganim_layout;

    private String vendorkey;
    private LinearLayout detail_fields_area;
    private StaticMarinadeSelectionPane marinadeSelectionPane;
    private double marinadeprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marinade);

        if (getPackageName().equals("com.meatchop")) {
            marinademenuitemkey = getIntent().getStringExtra("menuitemkey");
            marinadelinkedcodes = getIntent().getStringExtra("marinadelinkedcodes");
            selectedmarinadelinkcode = getIntent().getStringExtra("selectedmarinadelinkedcode");
            callfromordersummaryactivity = getIntent().getBooleanExtra("callfromordersummaryactivity", false);
        }
     // Log.d("MarinadeActivity", "selectedmarinadelinkcode "+selectedmarinadelinkcode);

        SettingsUtil settingsUtil = new SettingsUtil(this);
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
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        meatnotrequired_layout = findViewById(R.id.meatnotrequired_layout);
        itemnotselected_view = findViewById(R.id.itemnotselected_view);
        itemselected_view = findViewById(R.id.itemselected_view);
        itemtotal_textview = (TMCTextView) findViewById(R.id.itemtotal_textview);
        additem_text = (TMCTextView) findViewById(R.id.additem_text);

        if (callfromordersummaryactivity) {
            additem_text.setText("Update Item");
        }

        TMCMenuItem tmcMenuItem = null;
        if (callfromordersummaryactivity) {
            tmcMenuItem = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(marinademenuitemkey);
        } else {
            tmcMenuItem = TMCMenuItemCatalog.getInstance().getTMCMenuItem(marinademenuitemkey);
        }
        rupeessign = getResources().getString(R.string.Rs);
        marinadetmcprice = tmcMenuItem.getTmcprice();
        marinadeprice = tmcMenuItem.getMarinadePrice();
        itemtotal_textview.setText("Item Total: " + rupeessign + marinadetmcprice);

     // Log.d(TAG, "marinadedlinkedcodes "+marinadelinkedcodes);
        back_icon = findViewById(R.id.back_icon);
        detail_fields_area = (LinearLayout) findViewById(R.id.detail_fields_area);
     // marinades_listview = (MarinadeExpandableListView) findViewById(R.id.marinades_listview);

     // HashMap<String, ArrayList<TMCMenuItem>> tmcSubCtgyMenuItemMap =
     //                            TMCMenuItemCatalog.getInstance().getTMCMenuItemsFromUniqueCode(marinadelinkedcodes);

        getMarinadeMeatMenuItemsFromAWS();

        meatnotrequired_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meatmenuitemkey = "";
                isNoMeatSelected = true;
                itemtotal_textview.setText("Item total: " + rupeessign + marinadetmcprice);
             // marindesListViewAdapter.unSelectItems();
                if (marinadeSelectionPane != null) {
                    marinadeSelectionPane.unselectItems("");
                }
                itemselected_view.setVisibility(View.VISIBLE);
                itemnotselected_view.setVisibility(View.GONE);
            }
        });

        additem_layout = findViewById(R.id.additem_layout);
        additem_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             // Log.d("MarinadeActivity", "add item clicked");
                if (callfromordersummaryactivity) {
                    updateMarinadeMenuItemAndCloseActivity();
                } else {
                    if (!isNoMeatSelected) {
                        if ((meatmenuitemkey == null) || (TextUtils.isEmpty(meatmenuitemkey))) {
                            showTMCAlert(R.string.app_name, R.string.marinade_nooptionselected_alert);
                            return;
                        }
                    }
                    addMarinadeMenuItemAndCloseActivity();
                }

            }
        });

        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganimmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void loadMarinadeMeatItems() {
        HashMap<String, ArrayList<TMCMenuItem>> tmcSubCtgyMenuItemMap = getMarinadeMeatItemsFromUniqueCode(marinadelinkedcodes);
     // Log.d(TAG, "tmcSubCtgyMenuItemMap "+tmcSubCtgyMenuItemMap.toString());

        int tmcCtgySubCtgyMapSize = TMCDataCatalog.getInstance().tmcCtgySubCtgyMapSize();
        if ((tmcSubCtgyMenuItemMap != null) && (tmcSubCtgyMenuItemMap.size() >= 0)) {
            if (tmcCtgySubCtgyMapSize > 0) {
                createMarinadeListViewAdapter(tmcSubCtgyMenuItemMap);
            } else {
                getTMCSubCtgyListFromAWS(tmcSubCtgyMenuItemMap);
            }
        }
    }


    private void updateMarinadeMenuItemAndCloseActivity() {
        TMCMenuItem meattmcMenuItem = null;
        if (marinadeMeatKeyItemMap != null) {
            meattmcMenuItem = marinadeMeatKeyItemMap.get(meatmenuitemkey);
        }

        String newmarinadelinkcode = "";
        if (meattmcMenuItem != null) {
            newmarinadelinkcode = meattmcMenuItem.getItemuniquecode();
        }
        Intent intent = new Intent();
        if ((TextUtils.isEmpty(newmarinadelinkcode) || newmarinadelinkcode.equalsIgnoreCase(selectedmarinadelinkcode))) {
           // Do nothing
        } else {
            TMCMenuItem oldmarinadeMenuItem = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(marinademenuitemkey);
            TMCMenuItem newMarinadeMenuItem = new TMCMenuItem(oldmarinadeMenuItem.getJSONObjectForClone());

            if (meattmcMenuItem != null) {
                newMarinadeMenuItem.setMarinadeMeatTMCMenuItem(meattmcMenuItem);
                newMarinadeMenuItem.setSelectedqty(oldmarinadeMenuItem.getSelectedqty());
            }
         /* TMCMenuItem marinadeMenuItemFromCatalog = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(newMarinadeMenuItem.getKey());
            if (marinadeMenuItemFromCatalog != null) {
                int prevqty = marinadeMenuItemFromCatalog.getSelectedqty();
                newMarinadeMenuItem.setSelectedqty(prevqty + 1);
            } else {
                newMarinadeMenuItem.setSelectedqty(1);
            } */

            TMCMenuItemCatalog.getInstance().updateMenuItemInCart(newMarinadeMenuItem);

            // Remove old marinademenuitem
            oldmarinadeMenuItem.setSelectedqty(0);
            TMCMenuItemCatalog.getInstance().updateMenuItemInCart(oldmarinadeMenuItem);
            intent.putExtra("itemupdated", true);
        }
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
    }

    private void addMarinadeMenuItemAndCloseActivity() {
        TMCMenuItem meattmcMenuItem = null;
        if (marinadeMeatKeyItemMap != null) {
            meattmcMenuItem = marinadeMeatKeyItemMap.get(meatmenuitemkey);
        }
        TMCMenuItem marinadeMenuItem = TMCMenuItemCatalog.getInstance().getTMCMenuItem(marinademenuitemkey);
        TMCMenuItem newMarinadeMenuItem = new TMCMenuItem(marinadeMenuItem.getJSONObjectForClone());
     // Log.d("MarinadeActivity", "meattmcMenuItem "+meattmcMenuItem + " meatmenuitemkey "+meatmenuitemkey);

        if (meattmcMenuItem != null) {
            newMarinadeMenuItem.setMarinadeMeatTMCMenuItem(meattmcMenuItem);
        }
        TMCMenuItem marinadeMenuItemFromCatalog = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(newMarinadeMenuItem.getKey());
        if (marinadeMenuItemFromCatalog != null) {
            int prevqty = marinadeMenuItemFromCatalog.getSelectedqty();
            newMarinadeMenuItem.setSelectedqty(prevqty + 1);
        } else {
            newMarinadeMenuItem.setSelectedqty(1);
        }

        TMCMenuItemCatalog.getInstance().updateMenuItemInCart(newMarinadeMenuItem);

        // TMCMenuItemCatalog.getInstance().updateMarinadeMeatMenuItemInCart(marinadeMenuItem);

     /* JSONArray jsonArray = marinadeMenuItem.getMarinadeitemdesp();
        JSONObject meattmcmenuitemdesp = meattmcMenuItem.getJSONObjectStringForOrderDetails();

        if (jsonArray == null) {
            jsonArray = new JSONArray();
            jsonArray.put(meattmcmenuitemdesp);
            marinadeMenuItem.setMarinadeitemdesp(jsonArray);
        } else {
            jsonArray.put(meattmcmenuitemdesp);
        }
        marinadeMenuItem.setMarinadeitemdesp(jsonArray);
        int oldqty = marinadeMenuItem.getSelectedqty();
        int newqty = oldqty + 1;
        marinadeMenuItem.setSelectedqty(newqty); */

        Intent intent = new Intent();
        intent.putExtra("additemclicked", true);
        intent.putExtra("marinademenuitemkey", newMarinadeMenuItem.getKey());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
    }


    private class MarinadeSelectionViewAsyncTask extends AsyncTask<Void, Void, Void> {
        private HashMap<String, ArrayList<TMCMenuItem>> tmcSubCtgyMenuItemMap;

        private void setMenuItemMap(HashMap<String, ArrayList<TMCMenuItem>> menuItemMap) {
            this.tmcSubCtgyMenuItemMap = menuItemMap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (marinadeSelectionPane != null) {
                marinadeSelectionPane.removeAllViews();
                detail_fields_area.removeView(marinadeSelectionPane);
                marinadeSelectionPane = null;
            }
            marinadeSelectionPane = new StaticMarinadeSelectionPane(getApplicationContext());
            marinadeSelectionPane.setHandler(createMarinadeListViewHandler());
            marinadeSelectionPane.setSelectedMarinadeLinkedCode(selectedmarinadelinkcode);
            detail_fields_area.addView(marinadeSelectionPane);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (marinadeSelectionPane != null) {
                if ((tmcSubCtgyMenuItemMap == null) || (tmcSubCtgyMenuItemMap.size() <= 0)) {
                    return;
                }
                Object[] keys = tmcSubCtgyMenuItemMap.keySet().toArray();
                for (int i=0; i<keys.length; i++) {
                    String tmcsubctgyname = (String) keys[i];
                    ArrayList<TMCMenuItem> menuItems = tmcSubCtgyMenuItemMap.get(tmcsubctgyname);
                 // Log.d(TAG, "tmcsubctgyname "+tmcsubctgyname+" menuitems size "+ menuItems.size());
                    marinadeSelectionPane.addFormItem(tmcsubctgyname, menuItems);
                }

            }
        }
    }

    private void createMarinadeListViewAdapter(HashMap<String, ArrayList<TMCMenuItem>> tmcSubCtgyMenuItemMap) {
        MarinadeSelectionViewAsyncTask marinadeTask = new MarinadeSelectionViewAsyncTask();
        marinadeTask.setMenuItemMap(tmcSubCtgyMenuItemMap);
        marinadeTask.execute();

     /* marindesListViewAdapter = new MarindesListViewAdapter(getApplicationContext(), tmcSubCtgyMenuItemMap);
        marindesListViewAdapter.setHandler(createMarinadeListViewHandler());
        marindesListViewAdapter.setSelectedMarinadeLinkedCode(selectedmarinadelinkcode);
        marinades_listview.setAdapter(marindesListViewAdapter);
        marinades_listview.setGroupIndicator(null);
        for (int i=0; i<tmcSubCtgyMenuItemMap.size(); i++) {
            marinades_listview.expandGroup(i, false);
        } */
        hideLoadingAnim();
    }

    private Handler createMarinadeListViewHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("itemclick")) {
                    meatmenuitemkey = bundle.getString("tmcmenuitemkey");

                    double meatitemprice = bundle.getDouble("meatmenuitemprice");
                 // Log.d(TAG, "meatitemprice "+meatitemprice+" marinadetmcprice "+marinadetmcprice);
                 // Log.d(TAG, "meatitemprice "+meatitemprice+" marinadetmcprice "+marinadetmcprice);
                    double totalprice = marinadeprice + meatitemprice;
                    itemtotal_textview.setText("Item total: " + rupeessign + totalprice);
                    isNoMeatSelected = false;
                    itemselected_view.setVisibility(View.GONE);
                    itemnotselected_view.setVisibility(View.VISIBLE);
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(MarinadeActivity.this, title, message,
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

    private void getTMCSubCtgyListFromAWS(final HashMap<String, ArrayList<TMCMenuItem>> tmcSubCtgyMenuItemMap) {
        showLoadingAnim();
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
                                createMarinadeListViewAdapter(tmcSubCtgyMenuItemMap);
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

 // private ArrayList<TMCMenuItem> marinadeMeatMenuItems;
    private HashMap<String, TMCMenuItem> marinadeMeatKeyItemMap;
    private HashMap<String, TMCMenuItem> marinadeMeatMenuItemMap;
    private void getMarinadeMeatMenuItemsFromAWS() {
        showLoadingAnim();
        // String url = TMCRestClient.AWS_GETTMCMENUITEMS + "?storeid=" + vendorkey + "&subctgyid=" + selectedtmcsubctgykey;
        String url = TMCRestClient.AWS_GETMARINADEMEATITEMFORSTORE + "?storeid=" + vendorkey;
     // Log.d(TAG, "getMarinadeMenuItemsFromAWS url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            marinadeMeatKeyItemMap = new HashMap<String, TMCMenuItem>();
                            marinadeMeatMenuItemMap = new HashMap<String, TMCMenuItem>();
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TMCMenuItem tmcMenuItem = new TMCMenuItem(jsonObject1);
                                    if (!tmcMenuItem.isItemavailability(false)) {
                                        continue;
                                    }
                                 // Log.d(TAG, "menuitem name "+tmcMenuItem.getItemname() + " item code "+tmcMenuItem.getItemuniquecode());
                                    marinadeMeatKeyItemMap.put(tmcMenuItem.getKey(), tmcMenuItem);
                                    marinadeMeatMenuItemMap.put(tmcMenuItem.getItemuniquecode(), tmcMenuItem);
                                }
                            }
                            loadMarinadeMeatItems();

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

    public HashMap<String, ArrayList<TMCMenuItem>> getMarinadeMeatItemsFromUniqueCode(String uniquecodes) {
        if ((marinadeMeatMenuItemMap == null) || (marinadeMeatMenuItemMap.size() <= 0)) {
            return null;
        }
     // Log.d(TAG, "marinadeMeatMenuItemMap "+marinadeMeatMenuItemMap);

        HashMap<String, ArrayList<TMCMenuItem>> tmcSubCtgyMenuItemMap = new HashMap<String, ArrayList<TMCMenuItem>>();
        String[] codelist = uniquecodes.split(",");
        for (int i=0; i<codelist.length; i++) {
            String code = codelist[i].trim();

            TMCMenuItem mariandeMeatMenuItem = (TMCMenuItem) marinadeMeatMenuItemMap.get(code);
         // Log.d(TAG, "code "+code + " mariandeMeatMenuItem "+mariandeMeatMenuItem);
            if (mariandeMeatMenuItem == null) {
                continue;
            }
            ArrayList<TMCMenuItem> menuItemArrayList = tmcSubCtgyMenuItemMap.get(mariandeMeatMenuItem.getTmcsubctgykey());
            if (menuItemArrayList == null) {
                menuItemArrayList = new ArrayList<TMCMenuItem>();
                menuItemArrayList.add(mariandeMeatMenuItem);
                tmcSubCtgyMenuItemMap.put(mariandeMeatMenuItem.getTmcsubctgykey(), menuItemArrayList);
            } else {
                menuItemArrayList.add(mariandeMeatMenuItem);
            }
        }
     // Log.d(TAG, "tmcSubCtgyMenuItemMap "+tmcSubCtgyMenuItemMap);

        return tmcSubCtgyMenuItemMap;
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




}
