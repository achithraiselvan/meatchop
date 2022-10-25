package com.meatchop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.meatchop.R;
import com.meatchop.data.ItemCutDetails;
import com.meatchop.data.ItemWeightDetails;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.widget.ItemCutSelectionPane;
import com.meatchop.widget.ItemWeightSelectionPane;
import com.meatchop.widget.StaticMenuItemPane;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MenuItemCustomizationActivity extends BaseActivity {

    private String TAG = "MenuItemCustActivity";
    private ItemWeightSelectionPane itemWeightSelectionPane;
    private LinearLayout detail_weightfields_area;
    private LinearLayout detail_cutfields_area;

    private ItemCutSelectionPane itemCutSelectionPane;

    private ArrayList<ItemCutDetails> itemCutDetailsArrayList;
    private ArrayList<ItemWeightDetails> itemWeightDetailsArrayList;

    private HashMap<String, ItemCutDetails> itemCutDetailsHashMap;
    private HashMap<String, ItemWeightDetails> itemWeightDetailsHashMap;

    private String menuitemkey;

    private TMCTextView itemdesp_text;
    private TMCTextView itemname_text;
    private View additemsbtn_layout;
    private View updateitemsbtn_layout;
    private TMCTextView selectedweightdetails_textview;
    private TMCTextView selectedcutdetails_textview;
    private ItemWeightDetails selectedItemWeightDetails;
    private ItemCutDetails selectedItemCutDetails;

    private double itempriceperkg;
    private boolean callfromordersummaryactivity = false;
    private TMCMenuItem tmcMenuItem;

    private String oldtmcMenuItemKey;
    private String olditemcutkey;
    private String olditemweightkey;
    private View screenclose_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu_item_customization);

        itemname_text = (TMCTextView) findViewById(R.id.itemname_text);
        itemdesp_text = (TMCTextView) findViewById(R.id.itemdesp_text);
        detail_weightfields_area = (LinearLayout) findViewById(R.id.detail_weightfields_area);
        detail_cutfields_area = (LinearLayout) findViewById(R.id.detail_cutfields_area);
        additemsbtn_layout = findViewById(R.id.additemsbtn_layout);
        updateitemsbtn_layout = findViewById(R.id.updateitemsbtn_layout);
        selectedweightdetails_textview = (TMCTextView) findViewById(R.id.selectedweightdetails_textview);
        selectedcutdetails_textview = (TMCTextView) findViewById(R.id.selectedcutdetails_textview);

        screenclose_btn = findViewById(R.id.screenclose_btn);

        menuitemkey = getIntent().getStringExtra("menuitemkey");
        callfromordersummaryactivity = getIntent().getBooleanExtra("callfromordersummaryactivity", false);
        if (callfromordersummaryactivity) {
            oldtmcMenuItemKey = menuitemkey;
            additemsbtn_layout.setVisibility(View.GONE);
            updateitemsbtn_layout.setVisibility(View.VISIBLE);
            tmcMenuItem = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(menuitemkey);
            if (tmcMenuItem != null) {
                itempriceperkg = tmcMenuItem.getTmcpriceperkg();
                loadItemWeightAndCutSelectedDetails(tmcMenuItem);
            }
        } else {
            additemsbtn_layout.setVisibility(View.VISIBLE);
            updateitemsbtn_layout.setVisibility(View.GONE);
            tmcMenuItem = TMCMenuItemCatalog.getInstance().getTMCMenuItem(menuitemkey);
            if (tmcMenuItem != null) {
                itempriceperkg = tmcMenuItem.getTmcpriceperkg();
                loadItemWeightAndCutDetails(tmcMenuItem);
            }
        }
        if (tmcMenuItem != null) {
            itemname_text.setText(tmcMenuItem.getItemname());
            String itemdesp = tmcMenuItem.getItemdesp();
            if ((itemdesp != null) && !(TextUtils.isEmpty(itemdesp))) {
                itemdesp_text.setText(itemdesp);
            }
        }

        additemsbtn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tmcMenuItem == null) {
                    return;
                }
                TMCMenuItem newTMCMenuItem = new TMCMenuItem(tmcMenuItem.getJSONObjectForClone());
                if (newTMCMenuItem == null) {
                    return;
                }
                if (selectedItemCutDetails != null) {
                    newTMCMenuItem.setSelecteditemcut(selectedItemCutDetails.getJSONString());
                    double netwtingrams = selectedItemCutDetails.getNetweightingrams();
                    if (netwtingrams > 0) {
                     // String netwt = String.format("%.0f", netwtingrams);
                        newTMCMenuItem.setNetweight(selectedItemCutDetails.getNetweight());
                    }
                    String portionsize = selectedItemCutDetails.getPortionsize();
                    if ((portionsize != null) && !(TextUtils.isEmpty(portionsize))) {
                        newTMCMenuItem.setPortionsize(portionsize);
                    }
                }
                if (selectedItemWeightDetails != null) {
                    newTMCMenuItem.setSelecteditemweight(selectedItemWeightDetails.getJSONString());
                    double netwtingrams = selectedItemWeightDetails.getNetweightingrams();
                    if (netwtingrams > 0) {
                     // String netwt = String.format("%.0f", netwtingrams);
                        newTMCMenuItem.setNetweight(selectedItemWeightDetails.getNetweight());
                    }
                    double grosswtingrams = selectedItemWeightDetails.getGrossweightingrams();
                    if (grosswtingrams > 0) {
                     // String grosswt = String.format("%.0f", grosswtingrams);
                        newTMCMenuItem.setGrossweight(selectedItemWeightDetails.getGrossweight());
                        newTMCMenuItem.setGrossweightingrams(grosswtingrams);
                    }
                    String portionsize = selectedItemWeightDetails.getPortionsize();
                    if ((portionsize != null) && !(TextUtils.isEmpty(portionsize))) {
                        newTMCMenuItem.setPortionsize(portionsize);
                    }
                    newTMCMenuItem.setTmcprice(selectedItemWeightDetails.getItemprice());
                }

                TMCMenuItem cartTMCMenuItem = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(newTMCMenuItem.getKey());
                int oldqty = 0;
                if (cartTMCMenuItem != null) {
                    oldqty = cartTMCMenuItem.getSelectedqty();
                }
                int newqty = oldqty + 1;
                newTMCMenuItem.setSelectedqty(newqty);
                TMCMenuItemCatalog.getInstance().updateMenuItemInCart(newTMCMenuItem);
                closeActivity(true, newTMCMenuItem.getKey(), false);
            }
        });

        updateitemsbtn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValuesChanged = false;
                if ((olditemcutkey != null) && !(olditemcutkey.equalsIgnoreCase(selectedItemCutDetails.getCutkey()))) {
                    isValuesChanged = true;
                }
                if ((olditemweightkey != null) && !(olditemweightkey.equalsIgnoreCase(selectedItemWeightDetails.getWeightkey()))) {
                    isValuesChanged = true;
                }
                if (!isValuesChanged) {
                    closeActivity(false, null, false);
                    return;
                }

                // tmcMenuItem - MenuItem from cart
                if (selectedItemCutDetails != null) {
                    tmcMenuItem.setSelecteditemcut(selectedItemCutDetails.getJSONString());
                    double netwtingrams = selectedItemCutDetails.getNetweightingrams();
                    if (netwtingrams > 0) {
                     // String netwt = String.format("%.0f", netwtingrams);
                        tmcMenuItem.setNetweight(selectedItemCutDetails.getNetweight());
                    }
                    String portionsize = selectedItemCutDetails.getPortionsize();
                    if ((portionsize != null) && !(TextUtils.isEmpty(portionsize))) {
                        tmcMenuItem.setPortionsize(portionsize);
                    }
                }
                if (selectedItemWeightDetails != null) {
                    tmcMenuItem.setSelecteditemweight(selectedItemWeightDetails.getJSONString());
                    double netwtingrams = selectedItemWeightDetails.getNetweightingrams();
                    if (netwtingrams > 0) {
                     // String netwt = String.format("%.0f", netwtingrams);
                        tmcMenuItem.setNetweight(selectedItemWeightDetails.getNetweight());
                    }
                    double grosswtingrams = selectedItemWeightDetails.getGrossweightingrams();
                    if (grosswtingrams > 0) {
                     // String grosswt = String.format("%.0f", grosswtingrams);
                        tmcMenuItem.setGrossweight(selectedItemWeightDetails.getGrossweight());
                        tmcMenuItem.setGrossweightingrams(grosswtingrams);
                    }
                    String portionsize = selectedItemWeightDetails.getPortionsize();
                    if ((portionsize != null) && !(TextUtils.isEmpty(portionsize))) {
                        tmcMenuItem.setPortionsize(portionsize);
                    }
                    tmcMenuItem.setTmcprice(selectedItemWeightDetails.getItemprice());
                }

                int selqtyincart = TMCMenuItemCatalog.getInstance().getCutsAndWeightSelectedQty(tmcMenuItem.getKey());
             // Log.d(TAG, "selqtyincart "+selqtyincart + " tmcMenuItem.getSelectedqty() "+tmcMenuItem.getSelectedqty());
                tmcMenuItem.setSelectedqty(selqtyincart + tmcMenuItem.getSelectedqty());
                TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);

                TMCMenuItemCatalog.getInstance().removeMenuItemInCart(oldtmcMenuItemKey);
                closeActivity(false, tmcMenuItem.getKey(), true);
            }
        });

        screenclose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void closeActivity(boolean cutsweightitemadded, String menuitemkey, boolean isvalueschanged) {
        Intent intent = new Intent();
        intent.putExtra("cutsweightitemadded", cutsweightitemadded);
        if (menuitemkey != null) {
            intent.putExtra("cutsandweightmenuitemkey", menuitemkey);
        }
        if (callfromordersummaryactivity) {
            intent.putExtra("isvalueschanged", isvalueschanged);
        }
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
    }

    private void loadItemWeightAndCutSelectedDetails(TMCMenuItem tmcMenuItem) {  // Call from OrderSummaryActivity
        if (tmcMenuItem == null) {
            return;
        }
        String itemcutdetails = tmcMenuItem.getItemcutdetails();
        String itemweightdetails = tmcMenuItem.getItemweightdetails();
        String selectedcutdetails = tmcMenuItem.getSelecteditemcut();
        String selectedweightdetails = tmcMenuItem.getSelecteditemweight();

        if ((itemcutdetails != null) && (!TextUtils.isEmpty(itemcutdetails))) {
            try {
                JSONObject selectedcutjson = new JSONObject(selectedcutdetails);
                ItemCutDetails selectedItemCut = new ItemCutDetails(selectedcutjson);
                olditemcutkey = selectedItemCut.getCutkey();
                JSONObject jsonObject = new JSONObject(itemcutdetails);
                JSONArray jsonArray = jsonObject.getJSONArray("itemcutdetails");
                itemCutDetailsArrayList = new ArrayList<ItemCutDetails>();
                itemCutDetailsHashMap = new HashMap<String, ItemCutDetails>();
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    ItemCutDetails itemCutDetails = new ItemCutDetails(jsonObject1);
                    if (itemCutDetails.getCutkey().equalsIgnoreCase(selectedItemCut.getCutkey())) {
                        itemCutDetails.setIsdefault(true);
                    } else {
                        itemCutDetails.setIsdefault(false);
                    }

                    if (itemCutDetails.isIsdefault()) {
                        selectedItemCutDetails = itemCutDetails;
                        selectedcutdetails_textview.setText(itemCutDetails.getCutname());
                        selectedcutdetails_textview.setVisibility(View.VISIBLE);
                    }
                    itemCutDetailsArrayList.add(itemCutDetails);
                    itemCutDetailsHashMap.put(itemCutDetails.getCutkey(), itemCutDetails);
                }
                Collections.sort(itemCutDetailsArrayList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if ((itemweightdetails != null) && (!TextUtils.isEmpty(itemweightdetails))) {
            try {
                JSONObject selectedweightjson = new JSONObject(selectedweightdetails);
                ItemWeightDetails selectedItemWeight = new ItemWeightDetails(selectedweightjson);
                olditemweightkey = selectedItemWeight.getWeightkey();

                JSONObject jsonObject = new JSONObject(itemweightdetails);
                JSONArray jsonArray = jsonObject.getJSONArray("itemweightdetails");
                itemWeightDetailsArrayList = new ArrayList<ItemWeightDetails>();
                itemWeightDetailsHashMap = new HashMap<String, ItemWeightDetails>();
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    ItemWeightDetails itemWeightDetails = new ItemWeightDetails(jsonObject2);
                    if (selectedItemWeight.getWeightkey().equalsIgnoreCase(itemWeightDetails.getWeightkey())) {
                        itemWeightDetails.setIsdefault(true);
                    } else {
                        itemWeightDetails.setIsdefault(false);
                    }
                    if (itemWeightDetails.isIsdefault()) {
                        selectedItemWeightDetails = itemWeightDetails;
                     // String grosswtingrams = String.format("%.0f", itemWeightDetails.getGrossweightingrams());
                        selectedweightdetails_textview.setText("Gross wt. " + itemWeightDetails.getGrossweight());
                        selectedweightdetails_textview.setVisibility(View.VISIBLE);
                    }
                    itemWeightDetails.setItempriceperkg(itempriceperkg);
                    itemWeightDetailsArrayList.add(itemWeightDetails);
                    itemWeightDetailsHashMap.put(itemWeightDetails.getWeightkey(), itemWeightDetails);
                }
                Collections.sort(itemWeightDetailsArrayList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if ((itemCutDetailsArrayList != null) && (itemCutDetailsArrayList.size() > 0)) {
            new ItemCutFieldsAsyncTask().execute();
        }
        if ((itemWeightDetailsArrayList != null) && (itemWeightDetailsArrayList.size() > 0)) {
            new ItemWeightFieldsAsyncTask().execute();
        }
    }


    private void loadItemWeightAndCutDetails(TMCMenuItem tmcMenuItem) {
        if (tmcMenuItem == null) {
            return;
        }
        String itemcutdetails = tmcMenuItem.getItemcutdetails();
        String itemweightdetails = tmcMenuItem.getItemweightdetails();

        if ((itemcutdetails != null) && (!TextUtils.isEmpty(itemcutdetails))) {
            try {
                JSONObject jsonObject = new JSONObject(itemcutdetails);
                JSONArray jsonArray = jsonObject.getJSONArray("itemcutdetails");
                itemCutDetailsArrayList = new ArrayList<ItemCutDetails>();
                itemCutDetailsHashMap = new HashMap<String, ItemCutDetails>();

                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    ItemCutDetails itemCutDetails = new ItemCutDetails(jsonObject1);
                    if (itemCutDetails.isIsdefault()) {
                        selectedItemCutDetails = itemCutDetails;
                        selectedcutdetails_textview.setText(itemCutDetails.getCutname());
                        selectedcutdetails_textview.setVisibility(View.VISIBLE);
                    }
                    itemCutDetailsArrayList.add(itemCutDetails);
                    itemCutDetailsHashMap.put(itemCutDetails.getCutkey(), itemCutDetails);
                }
                Collections.sort(itemCutDetailsArrayList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if ((itemweightdetails != null) && (!TextUtils.isEmpty(itemweightdetails))) {
            try {
                JSONObject jsonObject = new JSONObject(itemweightdetails);
                JSONArray jsonArray = jsonObject.getJSONArray("itemweightdetails");
                itemWeightDetailsArrayList = new ArrayList<ItemWeightDetails>();
                itemWeightDetailsHashMap = new HashMap<String, ItemWeightDetails>();
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    ItemWeightDetails itemWeightDetails = new ItemWeightDetails(jsonObject2);
                    if (itemWeightDetails.isIsdefault()) {
                        selectedItemWeightDetails = itemWeightDetails;
                     // String grosswtingrams = String.format("%.0f", itemWeightDetails.getGrossweightingrams());
                        selectedweightdetails_textview.setText("Gross wt. " + itemWeightDetails.getGrossweight());
                        selectedweightdetails_textview.setVisibility(View.VISIBLE);
                    }
                    itemWeightDetails.setItempriceperkg(itempriceperkg);
                    itemWeightDetailsArrayList.add(itemWeightDetails);
                    itemWeightDetailsHashMap.put(itemWeightDetails.getWeightkey(), itemWeightDetails);
                }
                Collections.sort(itemWeightDetailsArrayList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if ((itemCutDetailsArrayList != null) && (itemCutDetailsArrayList.size() > 0)) {
            new ItemCutFieldsAsyncTask().execute();
        }
        if ((itemWeightDetailsArrayList != null) && (itemWeightDetailsArrayList.size() > 0)) {
            new ItemWeightFieldsAsyncTask().execute();
        }
    }

    private class ItemWeightFieldsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (itemWeightSelectionPane != null) {
                itemWeightSelectionPane.removeAllViews();
                detail_weightfields_area.removeView(itemWeightSelectionPane);
                itemWeightSelectionPane = null;
            }
            itemWeightSelectionPane = new ItemWeightSelectionPane(getApplicationContext());

            itemWeightSelectionPane.setHandler(createItemWeightPaneHandler());
            itemWeightSelectionPane.addFormItem("Choose your weight", itemWeightDetailsArrayList);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (itemWeightSelectionPane != null) {
                if (itemWeightSelectionPane.getParent() == null) {
                    detail_weightfields_area.addView(itemWeightSelectionPane);
                }
            }
        }
    }

    private Handler createItemCutsPaneHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("itemcutclicked")) {
                    String cutkey = bundle.getString("itemcutkey");
                    String cutname = bundle.getString("itemcutname");
                    selectedcutdetails_textview.setText(cutname);
                    selectedcutdetails_textview.setVisibility(View.VISIBLE);
                    ItemCutDetails itemCutDetails = itemCutDetailsHashMap.get(cutkey);
                    selectedItemCutDetails = itemCutDetails;
                 // selecteditemcuts = itemCutDetails.getJSONString();
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private Handler createItemWeightPaneHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("itemweightclicked")) {
                    String weightkey = bundle.getString("weightkey");
                    double grosswtingrams = bundle.getDouble("grosswtingrams");
                    String grosswtingramsstr = String.format("%.0f", grosswtingrams);
                    selectedweightdetails_textview.setText("Gross wt. "+grosswtingramsstr + "g");
                    selectedweightdetails_textview.setVisibility(View.VISIBLE);
                    ItemWeightDetails itemWeightDetails = itemWeightDetailsHashMap.get(weightkey);

                    selectedItemWeightDetails = itemWeightDetails;
                 // selecteditemweight = itemWeightDetails.getJSONString();
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private class ItemCutFieldsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (itemCutSelectionPane != null) {
                itemCutSelectionPane.removeAllViews();
                detail_cutfields_area.removeView(itemCutSelectionPane);
                itemCutSelectionPane = null;
            }
            itemCutSelectionPane = new ItemCutSelectionPane(getApplicationContext());

            itemCutSelectionPane.setHandler(createItemCutsPaneHandler());
            itemCutSelectionPane.addFormItem("Choose your cuts", itemCutDetailsArrayList);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (itemCutSelectionPane != null) {
                if (itemCutSelectionPane.getParent() == null) {
                    detail_cutfields_area.addView(itemCutSelectionPane);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
        // slideToRight();
    }


}