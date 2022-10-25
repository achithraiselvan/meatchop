package com.meatchop.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

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
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.activities.PayOnlineActivity;
import com.meatchop.activities.RazorpayActivity;
import com.meatchop.data.Paymenttransaction;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.helpers.DatabaseManager;
import com.meatchop.sqlitedata.Orderdetailslocal;
import com.meatchop.sqlitedata.Ordertrackingdetailslocal;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class PaymentEngine {
    private String TAG = "PaymentEngine";

    private Context context;
    private Handler handler;

    private double billvalue;
    private String orderid;
    private boolean optioncodbtnclicked = false;
    private boolean optiononerazorpaybtnclicked = false;
    private boolean optionpaytmbtnclicked = false;
    private String paymenttransactionstatus;
    private String paymentmode;

    private SettingsUtil settingsUtil;
    private Tracker tmcTracker;
    private String merchantorderid;
    private String paymenttransactionkey;
    private String orderdetailskey;
    private String vendorkey;
    private boolean showcodoption = false;
    private boolean isCODPaymentTransactionUpdated = false;


    public PaymentEngine(String orderid, String orderdetailskey, Context context, Handler handler) {
        this.orderid = orderid;
        this.orderdetailskey = orderdetailskey;
        this.context = context;
        this.handler = handler;
        settingsUtil = new SettingsUtil(context);
    }

    private void sendHandlerMessage(Bundle bundle) {
        if (handler != null) {
            Message msg =  new Message();
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    public void setVendorkey(String vendorkey) {
        this.vendorkey = vendorkey;
    }

    public void setOrderdetailskey(String orderdetailskey) {
        this.orderdetailskey = orderdetailskey;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }


    public boolean isCODPaymentTransactionUpdated() {
        return isCODPaymentTransactionUpdated;
    }

    public String generatePaytmOrderId(double billval) {
        String random1 = "paytmorder_" + System.nanoTime();
        String action = random1 + ":" + TMCUtil.getInstance().getCurrentTime();
        logEventInGAnalytics("Paytm orderid generated", action, settingsUtil.getMobile());
        billvalue = billval;
        paymentmode = "PAYTM";
        merchantorderid = random1;
        saveLastTransactionDetailsInSharedPref();
        paymenttransactionstatus = "Created";
        addPaymentTransactionInAWS(paymentmode, billvalue, paymenttransactionstatus);

        return random1;
    }

    public void generateOrderIdForRazorpay(double billval) {
        paymenttransactionstatus = "Created";
        paymentmode = "RAZORPAY";
        isCheckPaymentStatusMethodCalled = false;
        this.billvalue = billval;
        double razorpayamountinpaisa = 0;
        if (billval > 0) {
            razorpayamountinpaisa = billval * 100;
        }
        int finalbillvalue = (int) razorpayamountinpaisa;
        String billamt = "" + finalbillvalue;

        Log.d(TAG, "generateOrderIdForRazorpay billval "+billval);
        String razorpayurl = TMCRestClient.RAZORPAY_GENERATEORDERID_URL + "?transactionid="+ generateTransactionId() +
                                                                                                "&billamount="+billamt;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, razorpayurl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "generateOrderIdForRazorpay jsonobject " + response);
                    merchantorderid = response.getString("id");
                    saveLastTransactionDetailsInSharedPref();
                    String action = merchantorderid + ":" + TMCUtil.getInstance().getCurrentTime();
                    logEventInGAnalytics("Razorpay orderid generated", action, settingsUtil.getMobile());
                    addPaymentTransactionInAWS(paymentmode, billvalue, paymenttransactionstatus);
                    Log.d(TAG, "generateOrderIdForRazorpay merchantorderid "+merchantorderid);
                    Message msg =  new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("menutype", "startRazorpayActivity");
                    bundle.putString("merchantorderid", merchantorderid);
                    bundle.putDouble("billvalue", billvalue);
                    Log.d(TAG, "generateOrderIdForRazorpay bundle "+bundle.toString());
                    msg.setData(bundle);
                    handler.sendMessage(msg);
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
                logEventInGAnalytics("addPaymentTransaction failure", error.getMessage(), settingsUtil.getMobile());
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Make the request
        Volley.newRequestQueue(context).add(jsonObjectRequest);
     /* TMCRestClient.generateOrderId(billamt, generateTransactionId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    // Log.d(TAG, "TMCRestClient.generateOrderId "+jsonObject);
                    merchantorderid = jsonObject.getString("id");
                    saveLastTransactionDetailsInSharedPref();
                    String action = merchantorderid + ":" + TMCUtil.getInstance().getCurrentTime();
                    logEventInGAnalytics("Razorpay orderid generated", action, settingsUtil.getMobile());
                    addPaymentTransactionInAWS(paymentmode, billvalue, paymenttransactionstatus);
                    Log.d(TAG, "generateOrderIdForRazorpay merchantorderid "+merchantorderid);
                    Message msg =  new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("menutype", "startRazorpayActivity");
                    bundle.putString("merchantorderid", merchantorderid);
                    bundle.putDouble("billvalue", billvalue);
                    Log.d(TAG, "generateOrderIdForRazorpay bundle "+bundle.toString());
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                generateOrderIdForRazorpay(billval);
                Log.d("PaymentModeActivity", "generateOrderIdForRazorpay failed0 jsonObject "+jsonObject+ " throwable "+throwable); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                generateOrderIdForRazorpay(billval);
                Log.d("PaymentModeActivity", "generateOrderIdForRazorpay failed1 String "+s+ " throwable "+throwable); //No i18n
            }
        }); */


    }

    private void saveLastTransactionDetailsInSharedPref() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderid", orderid);
            jsonObject.put("merchantorderid", merchantorderid);
            jsonObject.put("paymentmode", paymentmode);
            String lasttransactiondetails = jsonObject.toString();
            settingsUtil.setLastTransactionDetails(lasttransactiondetails);
            Log.d(TAG, "last transaction details "+lasttransactiondetails);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String generateTransactionId() {
        Random r = new Random(System.currentTimeMillis());
        int randomvalue = 10000 + r.nextInt(20000);
        String transactionid = "trxn_" + randomvalue;
        return transactionid;
    }

    public void addPaymentTransactionInAWS(String paymentmode, double billvalue, String paymenttransactionstatus) {
        String mobileno = "+" + settingsUtil.getMobile();
        String currenttime = TMCUtil.getInstance().getCurrentTime();
        Paymenttransaction paymenttransaction = new Paymenttransaction(mobileno, orderid, merchantorderid, currenttime, paymentmode,
                billvalue, paymenttransactionstatus);
        if (paymentmode.equalsIgnoreCase("CASH ON DELIVERY")) {
            isCODPaymentTransactionUpdated = true;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobileno", paymenttransaction.getMobileno());
            jsonObject.put("orderid", paymenttransaction.getOrderid());
            jsonObject.put("merchantorderid", paymenttransaction.getMerchantorderid());
            jsonObject.put("paymentmode", paymenttransaction.getPaymentmode());
            jsonObject.put("transactiontime", paymenttransaction.getTransactiontime());
            jsonObject.put("transactionamount", paymenttransaction.getTransactionamount());
            jsonObject.put("userkey", settingsUtil.getUserkey());
            jsonObject.put("status", paymenttransaction.getStatus());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.d(TAG, "addPaymentTransactionInAWS orderid "+orderid);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_ADDPAYMENTTRANS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "addPaymentTrans jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = response.getJSONObject("content");
                        String key = jsonObject1.getString("key");
                        Log.d(TAG, "addPaymentTransactionInAWS key "+key);
                        String statusorderid = key + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("addPaymentTransaction success", statusorderid, settingsUtil.getMobile());
                        paymenttransaction.setKey(key);
                        paymenttransactionkey = key;
                        if ((paymentmode.equalsIgnoreCase("RAZORPAY"))
                                || (paymentmode.equalsIgnoreCase("PAYTM"))) {
                            settingsUtil.setLastPaymentTransKey(paymenttransactionkey);
                        }
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
                logEventInGAnalytics("addPaymentTransaction failure", error.getMessage(), settingsUtil.getMobile());
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Make the request
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    private boolean isCheckPaymentStatusMethodCalled = false;
    public void checkPaymentStatusFromMerchantorderid(String paymentmode, String merchantorderid) {
        isCheckPaymentStatusMethodCalled = true;
        if (paymentmode != null) {
            if (paymentmode.equalsIgnoreCase("RAZORPAY")) {
                getRazorpayOrderstatus(merchantorderid);
            } else if (paymentmode.equalsIgnoreCase("PAYTM")) {
                getPaytmOrderStatus(merchantorderid);
            }
        }
    }

    private void getRazorpayOrderstatus(String merchantorderid) {
        String url = TMCRestClient.RAZORPAY_GETORDERSTATUS_URL + "?orderid=" + merchantorderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getRazorpayOrderstatus jsonobject "+response);
                            if (response != null) {
                                String status = response.getString("status");
                                String statusorderid = merchantorderid + "-" + status + ":" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("getRazorpayOrderstatus", statusorderid, settingsUtil.getMobile());
                                Log.d(TAG, "getRazorpayOrderstatus status "+status);
                                if (status.equalsIgnoreCase("paid")) {
                                    paymentmode = "RAZORPAY";
                                    updatePaymentTransactionInAws("SUCCESS", "","");
                                    updatePaymentModeInOrderdetails();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("menutype", "changePaymentStatusInUI");
                                    bundle.putString("trxnstatus", "success");
                                    bundle.putString("paymentmode", paymentmode);
                                    sendHandlerMessage(bundle);
                                    settingsUtil.setLastTransactionDetails("");
                                } else {
                                    updatePaymentTransactionInAws( "FAILED", "","");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("menutype", "changePaymentStatusInUI");
                                    bundle.putString("trxnstatus", "failed");
                                    sendHandlerMessage(bundle);
                                    settingsUtil.setLastTransactionDetails("");
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
                        String action = merchantorderid + "-" + "onfailure" + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("checkRazorpayorderstatus onfailure", action, settingsUtil.getMobile());
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
// Add the request to the RequestQueue
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    private void getPaytmOrderStatus(String merchantorderid) {
        String url = TMCRestClient.PAYTM_GETORDERSTATUS_URL + "?orderid=" + merchantorderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getPaytmOrderStatus jsonobject "+response);
                            if (response != null) {
                                String status = response.getString("STATUS");
                                String merchantpaymentid = response.getString("TXNID");
                                String action = merchantorderid + "-" + status + ":" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("checkPaytmOrderStatus", action, settingsUtil.getMobile());
                                if (status.equalsIgnoreCase("TXN_SUCCESS")) {
                                    paymentmode = "PAYTM";
                                    updatePaymentModeInOrderdetails();
                                    updatePaymentTransactionInAws("SUCCESS", "", merchantpaymentid);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("menutype", "changePaymentStatusInUI");
                                    bundle.putString("trxnstatus", "success");
                                    bundle.putString("paymentmode", paymentmode);
                                    sendHandlerMessage(bundle);
                                    settingsUtil.setLastTransactionDetails("");
                                } else {
                                    updatePaymentTransactionInAws("FAILED", "", "");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("menutype", "changePaymentStatusInUI");
                                    bundle.putString("trxnstatus", "failed");
                                    sendHandlerMessage(bundle);
                                    settingsUtil.setLastTransactionDetails("");
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
                        String action = merchantorderid + "-" + "onfailure" + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("getPaytmOrderStatus onfailure", action, settingsUtil.getMobile());
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
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    private String paymenttype;
    public void getRazorpayPaymentTypeAndUpdateInPaymentTrans(String paymentstatus, String merchantpaymentid,
                                                                              String orderid, DatabaseHelper helper) {
        String url = TMCRestClient.RAZORPAY_GETPAYMENT_URL + "?paymentid=" + merchantpaymentid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getRazorpayOrderstatus jsonobject "+response);
                            if (response != null) {
                                String status = response.getString("status");
                                if (status.equalsIgnoreCase("captured")) {
                                    String method = response.getString("method");
                                    if (method.equalsIgnoreCase("wallet")) {
                                        String walletname = response.getString("wallet");
                                        paymenttype = walletname + " " + method;
                                    } else if (method.equalsIgnoreCase("netbanking")) {
                                        String bank = response.getString("bank");
                                        paymenttype = bank + " " + method;
                                    } else {
                                        paymenttype = method;
                                    }
                                    paymenttype = paymenttype.toUpperCase();
                                    updatePaymentTransactionInAws(paymentstatus, paymenttype, "");
                                    updatePaymentModeInOrderDetailsLocal(orderid, "", "", paymenttype, helper);
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
                        String action = merchantorderid + "-" + "onfailure" + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("checkRazorpayorderstatus onfailure", action, settingsUtil.getMobile());
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void updatePaymentTransactionInAws(String status, String paymenttype, String merchantpaymentid) {
        String paymenttranskey = settingsUtil.getLastPaymentTransKey();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", paymenttranskey);
            jsonObject.put("status", status);
            if ((merchantpaymentid != null) && !(TextUtils.isEmpty(merchantpaymentid))) {
                jsonObject.put("merchantpaymentid", merchantpaymentid);
            }
            if ((paymenttype != null) && !(TextUtils.isEmpty(paymenttype))) {
                jsonObject.put("paymenttype", paymenttype);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d(TAG, "updatePaymentTransactionInAws orderid "+merchantpaymentid);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_UPDATEPAYMENTTRANS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "addPaymentTrans jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {

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
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void updatePaymentModeInOrderdetails() {
        if (settingsUtil.isOrderdetailsNewSchema()) {
            updatePaymentModeInOrderdetailsNew();
        } else {
            updatePaymentModeInOrderdetailsOld();
        }
    }

    public void updatePaymentModeInOrderdetailsOld() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", orderdetailskey);
            jsonObject.put("merchantorderid", merchantorderid);
            jsonObject.put("paymentmode", paymentmode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Log.d(TAG, "updatePaymentModeInOrderdetails jsonObject "+jsonObject);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_UPDATEORDERDETAILSPAYMENTMODE,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "addPaymentTrans jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {

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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Make the request
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void updatePaymentModeInOrderdetailsNew() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderid", orderid);
            jsonObject.put("merchantorderid", merchantorderid);
            jsonObject.put("paymentmode", paymentmode);
            jsonObject.put("usermobileno" ,"+" + settingsUtil.getMobile());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d(TAG, "updatePaymentModeInOrderdetails jsonObject "+jsonObject);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_UPDATEPAYMODEINCUSTORDDETAILS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "updatePaymentModeInCustOrderdetailsNew jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {

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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Make the request
        Volley.newRequestQueue(context).add(jsonObjectRequest);

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("orderid", orderid);
            jsonObject1.put("merchantorderid", merchantorderid);
            jsonObject1.put("paymentmode", paymentmode);
            jsonObject1.put("vendorkey", vendorkey);
            jsonObject1.put("usermobileno" ,"+" + settingsUtil.getMobile());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d(TAG, "updatePaymentModeInOrderdetails jsonObject1 "+jsonObject1);
        // Log.d(TAG, "updatePaymentModeInOrderdetails jsonObject "+jsonObject);
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_UPDATEPAYMODEINVENDORDDETAILS,
                jsonObject1, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "updatePaymentModeInVendorOrderdetailsNew jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {

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
        RetryPolicy mRetryPolicy1 = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest1.setRetryPolicy(mRetryPolicy1);
        // Make the request
        Volley.newRequestQueue(context).add(jsonObjectRequest1);
    }


    private void logEventInGAnalytics(String category, String action, String label) {
        Bundle bundle = new Bundle();
        bundle.putString("menutype", "logEventInGanalytics");
        bundle.putString("category", category);
        bundle.putString("action", action);
        bundle.putString("label", label);
        sendHandlerMessage(bundle);
    }

    public void updatePaymentModeInOrderDetailsLocal(String orderid, String merchantorderid, String paymentmode, String paymenttype,
                                                                                        DatabaseHelper helper) {
        try {
            if (helper == null) {
                helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            }
            Orderdetailslocal orderdetails = DatabaseManager.getInstance(context).getOrderdetailsFromSqlite(helper, orderid);
            if ((paymentmode != null) && (!TextUtils.isEmpty(paymentmode))) {
                orderdetails.setPaymentmode(paymentmode);
            }
            if ((paymenttype != null) && (!TextUtils.isEmpty(paymenttype))) {
                orderdetails.setPaymenttype(paymenttype);
            }
            if ((merchantorderid != null) && (!TextUtils.isEmpty(merchantorderid))) {
                orderdetails.setMerchantorderid(merchantorderid);
            }

            orderdetails.update(helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }



}
