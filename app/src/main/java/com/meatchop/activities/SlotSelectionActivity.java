package com.meatchop.activities;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.data.Deliveryslotdetails;
import com.meatchop.data.Deliveryslots;
import com.meatchop.data.TMCTile;
import com.meatchop.data.TMCUser;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.widget.StaticSlotSelectionPane;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SlotSelectionActivity extends AppCompatActivity {

    private String TAG = "SlotSelectionActivity";

    private View back_icon;
    private LinearLayout detail_fields_area;
    private View confirmslot_btn;
    private TMCTextView confirmslot_text;
    private View loadinganimmask_layout;
    private View loadinganim_layout;

 // private Deliveryslots defaultDeliverySlotDetails;
    private ArrayList<Deliveryslots> deliveryslotdetailsArrayList;
    private StaticSlotSelectionPane deliverySlotPane;
    private HashMap<String, ArrayList<Deliveryslots>> deliverySlotDetailsMap;
    private int deliverytype;

    private Deliveryslots selecteddeliveryslotdetails;
    private String selectedsessiontype;
    private String vendorkey;
    private double newdeliverycharges;

    private TMCUserAddress tmcUserAddress;
    private SettingsUtil settingsUtil;
    private double userdeliverydistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_slot_selection);

        if (getPackageName().equals("com.meatchop")) {
            try {
                vendorkey = getIntent().getStringExtra("vendorkey");
             // defaultDeliverySlotDetails = getIntent().getParcelableExtra("defaultdeliveryslotdetails");
                deliveryslotdetailsArrayList = getIntent().getParcelableArrayListExtra("deliveryslotdetailsarraylist");
                selecteddeliveryslotdetails = getIntent().getParcelableExtra("selecteddeliveryslotdetails");
                deliverytype = getIntent().getIntExtra("deliverytype", OrderSummaryActivity.DELIVERYTYPE_HOMEDELIVERY);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        settingsUtil = new SettingsUtil(this);
        try {
            tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
            userdeliverydistance = tmcUserAddress.getDeliverydistance();
            if (vendorkey == null) {
                vendorkey = tmcUserAddress.getVendorkey();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        back_icon = findViewById(R.id.back_icon);
        detail_fields_area = (LinearLayout) findViewById(R.id.detail_fields_area);
        confirmslot_btn = findViewById(R.id.confirmslot_btn);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        confirmslot_text = (TMCTextView) findViewById(R.id.confirmslot_text);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        confirmslot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selecteddeliveryslotdetails == null) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("isslotselected", true);
                intent.putExtra("selecteddeliveryslotdetails", selecteddeliveryslotdetails);
                intent.putExtra("selectedsessiontype", selectedsessiontype);
                if (newdeliverycharges > 0) {
                    intent.putExtra("newdeliverycharges", newdeliverycharges);
                }
                closeActivity(intent);
            }
        });
        new DeliverySlotDetailsAsyncTask().execute();
     // getDeliverySlotDetails(vendorkey);
    }

    private void closeActivity(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
    }


    private class DeliverySlotDetailsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (deliverySlotPane != null) {
                deliverySlotPane.removeAllViews();
                detail_fields_area.removeView(deliverySlotPane);
                deliverySlotPane = null;
            }
            deliverySlotPane = new StaticSlotSelectionPane(getApplicationContext());
            deliverySlotPane.setHandler(createDeliverySlotPaneHandler());
            deliverySlotPane.setSelectedDeliverySlotDetails(selecteddeliveryslotdetails);
            deliverySlotPane.setUserdeliverydistance(userdeliverydistance);
            deliverySlotPane.setDeliverytype(deliverytype);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (deliverySlotPane != null) {
             // ArrayList<Deliveryslots> defaultDeliverySlotDetailsList = new ArrayList<Deliveryslots>();
             // defaultDeliverySlotDetailsList.add(defaultDeliverySlotDetails);
             // deliverySlotPane.addFormItem(defaultDeliverySlotDetails.getSlotname(), defaultDeliverySlotDetailsList);
                ArrayList<Deliveryslots> expressDelList = new ArrayList<Deliveryslots>();
                ArrayList<Deliveryslots> todayList = new ArrayList<Deliveryslots>();
                ArrayList<Deliveryslots> tomorrowList = new ArrayList<Deliveryslots>();
                ArrayList<Deliveryslots> spldaypreorderList = new ArrayList<>();
                Date currenttime = TMCUtil.getInstance().getCurrentTimeForDeliverySlot();

                ArrayList<Deliveryslots> dummyList = new ArrayList<Deliveryslots>();
                dummyList.addAll(deliveryslotdetailsArrayList);

                String spldaypreorderslotname = "";
                for (int i=0; i<dummyList.size(); i++) {
                    Deliveryslots deliveryslotdetails = dummyList.get(i);
                    if (deliveryslotdetails.getSlotdatetype().equalsIgnoreCase("Today")) {
                        if (deliveryslotdetails.getSlotname().equalsIgnoreCase("Express Delivery")) {
                            expressDelList.add(deliveryslotdetails);
                        } else {
                            Date deliveryslotslottime = TMCUtil.getInstance().getDateFromTime(deliveryslotdetails.getSlotstarttime());
                            if (currenttime.compareTo(deliveryslotslottime) < 0) {
                                todayList.add(deliveryslotdetails);
                            }
                        }

                    } else if (deliveryslotdetails.getSlotdatetype().equalsIgnoreCase("Tomorrow")) {
                        tomorrowList.add(deliveryslotdetails);
                    }
                    if (deliveryslotdetails.getSlotname().equalsIgnoreCase(Deliveryslots.SLOTNAME_SPLDAYPREORDER)) {
                        spldaypreorderslotname = deliveryslotdetails.getSlotdate();
                        spldaypreorderList.add(deliveryslotdetails);
                    }
                }

             /* for (int j=0; j<deliveryslotdetailsArrayList.size(); j++) {
                    Deliveryslotdetails deliveryslotdetails = deliveryslotdetailsArrayList.get(j);
                    tomorrowList.add(deliveryslotdetails);
                } */

                if ((expressDelList != null) && (expressDelList.size() > 0)) {
                    deliverySlotPane.addFormItem("Express Delivery", expressDelList);
                }
                if ((todayList != null) && (todayList.size() > 0)) {
                    deliverySlotPane.addFormItem("Today", todayList);
                }
                if ((tomorrowList != null) && (tomorrowList.size() > 0)) {
                    deliverySlotPane.addFormItem("Tomorrow", tomorrowList);
                }
                if ((spldaypreorderList != null) && (spldaypreorderList.size() > 0)) {
                    deliverySlotPane.addFormItem(spldaypreorderslotname, spldaypreorderList);
                }
                if (deliverySlotPane.getParent() == null) {
                    detail_fields_area.addView(deliverySlotPane);
                }
            }
        }
    }

    private Handler createDeliverySlotPaneHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("menutype");
                if (data.equalsIgnoreCase("deliveryslotselected")) {
                    selecteddeliveryslotdetails = (Deliveryslots) bundle.getParcelable("deliveryslotdetails");
                    selectedsessiontype = bundle.getString("selectedsessiontype");
                    newdeliverycharges = bundle.getDouble("newdeliverycharges");
                    confirmslot_btn.setBackgroundColor(getResources().getColor(R.color.meatchopbg_red));
                    confirmslot_text.setTextColor(getResources().getColor(R.color.white));
                    confirmslot_btn.setClickable(true);
                } else if (data.equalsIgnoreCase("deliveryslotunselected")) {
                    confirmslot_btn.setBackgroundColor(getResources().getColor(R.color.secondary_text_color));
                    confirmslot_text.setTextColor(getResources().getColor(R.color.white));
                    confirmslot_btn.setClickable(false);
                }
                return false;
            }
        };
        return new Handler(callback);
    }

 /* private void getDeliverySlotDetails(String vendorkey) {
        showLoadingAnim();
        TMCRestClient.getDeliverySlotDetails(vendorkey, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    String message = jsonObject.getString("message");

                    if (message.equalsIgnoreCase("success")) {
                        deliverySlotDetailsMap = new HashMap<String, ArrayList<Deliveryslotdetails>>();
                        deliveryslotdetailsArrayList = new ArrayList<Deliveryslotdetails>();
                        defaultDeliverySlotDetails = null;
                        JSONArray jsonArray = jsonObject.getJSONArray("content");
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject deliveryjsonobj = jsonArray.getJSONObject(i);
                            boolean isdefault = deliveryjsonobj.getBoolean("isdefault");
                            Deliveryslotdetails deliveryslotdetails = new Deliveryslotdetails(deliveryjsonobj);
                            if (isdefault) {
                                defaultDeliverySlotDetails = deliveryslotdetails;
                            } else {
                                deliveryslotdetailsArrayList.add(deliveryslotdetails);
                            }
                        }

                        new DeliverySlotDetailsAsyncTask().execute();
                        hideLoadingAnim();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "getDeliverySlotDetails fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "getDeliverySlotDetails fetch failed"); //No i18n
            }
        });

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



}
