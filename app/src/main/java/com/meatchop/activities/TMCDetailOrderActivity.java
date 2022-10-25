package com.meatchop.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.meatchop.R;
import com.meatchop.adapters.TMCDetailOrderAdapter;
import com.meatchop.data.Deliveryslotdetails;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.Ordertrackingdetails;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Unused Activity
public class TMCDetailOrderActivity extends AppCompatActivity {
    private RecyclerView orderdetails_recyclerview;
    private Orderdetails orderdetails;
    private String orderid;
    private TMCDetailOrderAdapter tmcDetailOrderAdapter;
    private String TAG = "TMCDetailOrderAct";

    private View orderstatusdetails_layout;
    private TMCTextView orderstatustime_text;
    private TMCTextView orderstatus_text;
    private TMCTextView addressline_text;
    private TMCTextView itemtotal_textview;
    private TMCTextView deliveryfee_textview;
    private TMCTextView taxesandcharges_textview;
    private TMCTextView amountpaid_textview;
    private TMCTextView orderid_text;


    private SettingsUtil settingsUtil;
    private View loadinganim_layout;
    private View loadinganimmask_layout;
    private Ordertrackingdetails ordertrackingdetails;
    private TMCTextView slotdetails_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tmcdetailorders);
        getSupportActionBar().hide();

        settingsUtil = new SettingsUtil(this);
        if (getPackageName().equals("com.meatchop")) {
            orderdetails = getIntent().getParcelableExtra("orderdetails");
         // orderid = orderdetails.getOrderid();
            orderid = getIntent().getStringExtra("orderid");
        }

     // Log.d(TAG, "orderdetails "+orderdetails.getKey());

        orderstatusdetails_layout = findViewById(R.id.orderstatusdetails_layout);
        orderstatustime_text = (TMCTextView) findViewById(R.id.orderstatustime_text);
        orderstatus_text = (TMCTextView) findViewById(R.id.orderstatus_text);
        addressline_text = (TMCTextView) findViewById(R.id.addressline_text);
        itemtotal_textview = (TMCTextView) findViewById(R.id.itemtotal_textview);
        deliveryfee_textview = (TMCTextView) findViewById(R.id.deliveryfee_textview);
        taxesandcharges_textview = (TMCTextView) findViewById(R.id.taxesandcharges_textview);
        amountpaid_textview = (TMCTextView) findViewById(R.id.amountpaid_textview);
        orderid_text = (TMCTextView) findViewById(R.id.orderid_text);
        slotdetails_text = (TMCTextView) findViewById(R.id.slotdetails_text);


        View back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        orderdetails_recyclerview = (RecyclerView) findViewById(R.id.orderdetails_recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        orderdetails_recyclerview.setLayoutManager(mLayoutManager);
        orderdetails_recyclerview.setItemAnimator(new DefaultItemAnimator());

        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);

        if (orderdetails == null) {
            orderid_text.setText("Order: #" + orderid);
            getOrderDetails(orderid);
        } else {
            orderid_text.setText("Order: #" + orderdetails.getOrderid());
            getOrderTrackingDetails(orderdetails.getOrderid());
        }
    }

    private void fillOrderstatusDetailsAndCreateAdapter() {
        if (ordertrackingdetails != null) {
            String orderstatus = ordertrackingdetails.getOrderstatus();
            if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_NEW)) {
                orderstatus_text.setText("Pending");
                orderstatustime_text.setText(orderdetails.getOrderplacedtime());
            } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CONFIRMED)) {
                orderstatus_text.setText("Confirmed");
                orderstatustime_text.setText(orderdetails.getOrderplacedtime());
            } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_READYFORPICKUP)) {
                orderstatus_text.setText("Ready For Pickup");
                orderstatustime_text.setText(orderdetails.getOrderplacedtime());
            } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_PICKEDUP)) {
                orderstatus_text.setText("Picked Up");
                orderstatustime_text.setText(orderdetails.getOrderplacedtime());
            } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_DELIVERED)) {
                orderstatus_text.setText("Delivered");
                orderstatustime_text.setText(orderdetails.getOrderplacedtime());
            } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CANCELLED)) {
                orderstatus_text.setText("Cancelled");
                orderstatustime_text.setText(orderdetails.getOrderplacedtime());
            }
        }

        if (orderdetails != null) {
            try {
                String rs = getResources().getString(R.string.Rs);
                itemtotal_textview.setText(rs + String.format("%.2f", orderdetails.getItemtotal()));
                deliveryfee_textview.setText(rs + String.format("%.2f", orderdetails.getDeliveryamount()));
                taxesandcharges_textview.setText(rs + String.format("%.2f", orderdetails.getGstamount()));
                amountpaid_textview.setText(rs + String.format("%.2f", orderdetails.getPayableamount()));
                String itemdetails = orderdetails.getItemdesp();
                Log.d(TAG, "itemdetails "+itemdetails);
                JSONObject itemdespjson = new JSONObject(itemdetails);
                JSONArray jsonArray = itemdespjson.getJSONArray("itemdesp");
                tmcDetailOrderAdapter = new TMCDetailOrderAdapter(getApplicationContext(), jsonArray);
                orderdetails_recyclerview.setAdapter(tmcDetailOrderAdapter);
                Log.d(TAG," useraddress "+orderdetails.getUseraddress());
                addressline_text.setText(orderdetails.getUseraddress());
                String slotname = orderdetails.getSlotname();
                boolean isPreOrderSlot = false;
                if (slotname != null) {
                    if (slotname.equalsIgnoreCase("Express Delivery")) {
                        isPreOrderSlot = false;
                    } else {
                        isPreOrderSlot = true;
                    }
                } else {
                    isPreOrderSlot = false;
                }
                if (isPreOrderSlot) {
                    slotdetails_text.setVisibility(View.VISIBLE);
                    slotdetails_text.setText("Slot details: " + orderdetails.getSlotdate() + " " + orderdetails.getSlottimerange());
                } else {
                    slotdetails_text.setVisibility(View.GONE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        hideLoadingAnim();

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
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

    private void getOrderDetails(String orderid) {
        String url = TMCRestClient.AWS_GETORDERDETAILSFROMORDERID + "?orderid=" + orderid;
        Log.d(TAG, "getOrderDetails url "+url);
        showLoadingAnim();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getOrderDetailsFromMobileno jsonObject "+response);
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    orderdetails = new Orderdetails(jsonObject1);
                                }
                            }
                            getOrderTrackingDetails(orderid);

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

    private void getOrderTrackingDetails(String orderid) {
        String url = TMCRestClient.AWS_GETORDERTRACKINGDETAILS + "?orderid=" + orderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject ordertrackingjsonobj = jsonArray.getJSONObject(i);
                                    ordertrackingdetails = new Ordertrackingdetails(ordertrackingjsonobj);
                                }
                                fillOrderstatusDetailsAndCreateAdapter();

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



}
