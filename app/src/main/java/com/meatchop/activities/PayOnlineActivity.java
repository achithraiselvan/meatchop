package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;

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
import com.meatchop.data.Orderdetails;
import com.meatchop.data.Ordertrackingdetails;
import com.meatchop.data.Paymenttransaction;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.helpers.DatabaseManager;
import com.meatchop.sqlitedata.Orderdetailslocal;
import com.meatchop.sqlitedata.Ordertrackingdetailslocal;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class PayOnlineActivity extends BaseActivity {

    private String TAG = "PayOnlineActivity";

    private final int RAZORPAY_ACT_REQCODE = 0;
    private final int PAYTM_ACT_REQCODE = 1;

    private View back_icon;
    private TMCTextView billtotal_textview;
    private View cashondelivery_layout;
    private View option2_razorpaylayout;
    private View option3_paytmlayout;

    private double billvalue;
    private String orderid;
    private boolean optioncodbtnclicked = false;
    private boolean optiononerazorpaybtnclicked = false;
    private boolean optionpaytmbtnclicked = false;
    private String paymenttransactionstatus;
    private String paymentmode;

    private View loadinganim_layout;
    private View loadinganimmask_layout;

    private boolean isStopSchedulerThread = false;
    private SettingsUtil settingsUtil;
    private Tracker tmcTracker;
    private String merchantorderid;
    private String paymenttransactionkey;
    private String orderdetailskey;
    private boolean showcodoption = false;

    private TMCTextView razorpaydesp_textview;
    private TMCTextView paytmdesp_textview;
    private String paymenttype;

    private boolean transactioninprogress = false;
    private View orderplacedsuccessfully_desp;

    private View cancelorder_layout;
    private View cancelorder_btn;
    private TMCTextView ordercanceldetails_desp;
    private View orderconfirmationgotit_btn;
    private View orderplaced_popup;
    private View orderplacedmask_layout;
    private boolean isOrderCancelled = false;

    private String supportmobileno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_online);

        if (getPackageName().equals("com.meatchop")) {
            billvalue = getIntent().getDoubleExtra("billvalue", 0);
            orderid = getIntent().getStringExtra("orderid");
            orderdetailskey = getIntent().getStringExtra("orderdetailskey");
            showcodoption = getIntent().getBooleanExtra("showcodoption", false);
        }

        settingsUtil = new SettingsUtil(this);
        supportmobileno = settingsUtil.getSupportMobileNo();

        cancelorder_layout = findViewById(R.id.cancelorder_layout);
        cancelorder_btn = findViewById(R.id.cancelorder_btn);
        ordercanceldetails_desp = (TMCTextView) findViewById(R.id.ordercanceldetails_desp);
        orderplaced_popup = findViewById(R.id.orderplaced_popup);
        orderconfirmationgotit_btn = findViewById(R.id.orderconfirmationgotit_btn);
        orderplacedmask_layout = findViewById(R.id.orderplacedmask_layout);

        back_icon = findViewById(R.id.back_icon);
        billtotal_textview = (TMCTextView) findViewById(R.id.billtotal_textview);
        cashondelivery_layout = findViewById(R.id.cashondelivery_layout);
        option2_razorpaylayout = findViewById(R.id.option2_razorpaylayout);
        option3_paytmlayout = findViewById(R.id.option3_paytmlayout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);

        razorpaydesp_textview = (TMCTextView) findViewById(R.id.razorpaydesp_textview);
        paytmdesp_textview = (TMCTextView) findViewById(R.id.paytmdesp_textview);

        orderplacedsuccessfully_desp = findViewById(R.id.orderplacedsuccessfully_desp);

        String rs = getResources().getString(R.string.Rs);
        billtotal_textview.setText("Bill Total: " + rs + String.format("%.2f", billvalue));
        if (showcodoption) {
            cashondelivery_layout.setVisibility(View.VISIBLE);
            razorpaydesp_textview.setText("Option 2 (Razorpay Gateway)");
            paytmdesp_textview.setText("Option 3 (Paytm Gateway)");
         // orderplacedsuccessfully_desp.setVisibility(View.VISIBLE);
            orderplaced_popup.setVisibility(View.VISIBLE);
            orderplacedmask_layout.setVisibility(View.VISIBLE);
        } else {
            cashondelivery_layout.setVisibility(View.GONE);
            razorpaydesp_textview.setText("Option 1 (Razorpay Gateway)");
            paytmdesp_textview.setText("Option 2 (Paytm Gateway)");
         // orderplacedsuccessfully_desp.setVisibility(View.GONE);
            orderplaced_popup.setVisibility(View.GONE);
            orderplacedmask_layout.setVisibility(View.GONE);
        }

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cashondelivery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingAnim();
                if (optioncodbtnclicked) {
                    return;
                }
                optioncodbtnclicked = true;
                paymentmode = "CASH ON DELIVERY";
                paymenttransactionstatus = "Success";
                addPaymentTransactionInAWS();
                closeActivity(paymentmode, "", "success");
            }
        });

        option2_razorpaylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingAnim();
                if (optiononerazorpaybtnclicked) {
                    return;
                }
                paymenttransactionstatus = "Created";
                optiononerazorpaybtnclicked = true;
                paymentmode = "RAZORPAY";

             // isStopSchedulerThread = false;
             // startSchedulerThreadForCheckingTransaction();
                isCheckPaymentStatusMethodCalled = false;

                generateOrderIdForRazorpay(billvalue);
            }
        });

        option3_paytmlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingAnim();
                if (optionpaytmbtnclicked) {
                    return;
                }
                optionpaytmbtnclicked = true;
                paymentmode = "PAYTM";
                merchantorderid = generatePaytmOrderId();
                saveLastTransactionDetailsInSharedPref();
                paymenttransactionstatus = "Created";
                addPaymentTransactionInAWS();

             // isStopSchedulerThread = false;
             // startSchedulerThreadForCheckingTransaction();
                isCheckPaymentStatusMethodCalled = false;

                Intent intent = new Intent(PayOnlineActivity.this, PaytmActivity.class);
                intent.putExtra("transamount", String.format("%.2f", billvalue));
                intent.putExtra("mobileno", settingsUtil.getMobile());
                intent.putExtra("paytmorderid", merchantorderid);
                startPaytmActivity(intent);
            }
        });


        orderplacedmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderplaced_popup.setVisibility(View.GONE);
                orderplacedmask_layout.setVisibility(View.GONE);
            }
        });

        orderconfirmationgotit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderplaced_popup.setVisibility(View.GONE);
                orderplacedmask_layout.setVisibility(View.GONE);
            }
        });

        if ((orderdetailskey == null) || (TextUtils.isEmpty(orderdetailskey))) {
            try {
                String mobileno = settingsUtil.getMobile();
                if ((mobileno != null) && (!TextUtils.isEmpty(mobileno))) {
                    mobileno = mobileno.substring(2);
                    orderdetailskey = orderid + "_" + mobileno;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

     // Orderdetailslocal orderdetailslocal = getOrderdetailsLocal(orderid);
     /* cancelorder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isordercancellationpossible = false;
                if (orderdetailslocal != null) {
                    isordercancellationpossible = isShowCancelOrderLayout(orderdetailslocal.getOrderplacedtime());
                }
                if (isordercancellationpossible) {
                    showConfirmationAlertForOrderCancellation(R.string.ordercancelledalert_title, R.string.ordercancelledalert_desp);
                } else {
                    showOrderCancelNotPossibleAlert(R.string.ordercancelnotpossible_title, R.string.ordercancelnotpossible_desp);
                }
            }
        }); */

     /* if ((orderdetailslocal.getTokenno() == null) || (TextUtils.isEmpty(orderdetailslocal.getTokenno()))) {
            isShowCancelOrderLayout(orderdetailslocal.getOrderplacedtime());
        } else {
            cancelorder_layout.setVisibility(View.GONE);
        } */
    }

    @Override
    public void onBackPressed() {
        if (transactioninprogress) {
            return;
        }
        Intent intent = new Intent();
        if (isOrderCancelled) {
            intent.putExtra("ordercancelled", true);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_OK);
        }
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_right);
    }

    private void startRazorpayActivity(Intent intent) {
        startActivityForResult(intent, RAZORPAY_ACT_REQCODE);
        overridePendingTransition(0, 0);
    }

    private void startPaytmActivity(Intent intent) {
        startActivityForResult(intent, PAYTM_ACT_REQCODE);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RAZORPAY_ACT_REQCODE :
                boolean isRazorpayPaymentSuccess = false;
                isStopSchedulerThread = true;
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String trxnstatus = data.getStringExtra("trxnstatus");
                        if (trxnstatus.equalsIgnoreCase("success")) {
                            isRazorpayPaymentSuccess = true;
                            String merchantpaymentid = data.getStringExtra("merchantpaymentid");
                            paymentmode = "RAZORPAY";
                            getRazorpayPaymentTypeAndUpdateInPaymentTrans("SUCCESS", merchantpaymentid);
                            updatePaymentModeInOrderdetails();
                            updatePaymentModeInOrderDetailsLocal(orderid, merchantorderid, paymentmode);
                        }
                    }
                }
                if (isRazorpayPaymentSuccess) {
                    closeActivity(paymentmode, "", "success");
                } else {
                    transactioninprogress = true;
                    optiononerazorpaybtnclicked = false;
                    paymentmode = "RAZORPAY";
                    String action = "Paymentmode:" + paymentmode + "merchantorderid: " + merchantorderid;
                    logEventInGAnalytics("Razorpay Recheck payment status", action, settingsUtil.getMobile());
                    checkPaymentStatusFromMerchantorderid(merchantorderid);
                }

                break;

            case PAYTM_ACT_REQCODE :
                boolean isPaytmPaymentSuccess = false;
                isStopSchedulerThread = true;
                String paymenttype = ""; String merchantpaymentid = "";
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String trxnstatus = data.getStringExtra("trxnstatus");
                        merchantpaymentid = data.getStringExtra("paymentid");
                        String paytmorderid = data.getStringExtra("paytmorderid");
                        String paytmpaymentmode = data.getStringExtra("paymentmode");
                        String bankname = data.getStringExtra("bankname");
                        String gatewayname = data.getStringExtra("gatewayname");

                        Log.d(TAG, "paymentmode "+paymentmode+" bankname "+bankname+" status "+trxnstatus);
                        // Log.d(TAG, "data extra "+data.getExtras().toString());

                        if (trxnstatus.equalsIgnoreCase("success")) {
                            isPaytmPaymentSuccess = true;
                            try {
                                if (paytmpaymentmode != null) {
                                    if (paytmpaymentmode.equalsIgnoreCase("CC")) {
                                        paymenttype = bankname + " Credit Card";
                                    } else if (paytmpaymentmode.equalsIgnoreCase("DC")) {
                                        paymenttype = bankname + " Debit Card";
                                    } else if (paytmpaymentmode.equalsIgnoreCase("NB")) {
                                        paymenttype = bankname + " Netbanking";
                                    } else if (paytmpaymentmode.equalsIgnoreCase("PPI")) {
                                        paymenttype = "Paytm Wallet";
                                    } else if (paytmpaymentmode.equalsIgnoreCase("UPI")) {
                                        if ((bankname == null) || (TextUtils.isEmpty(bankname))) {
                                            if ((gatewayname != null) && (!TextUtils.isEmpty(gatewayname))) {
                                                paymenttype = gatewayname + " UPI Payment";
                                            } else {
                                                paymenttype = "UPI Payment";
                                            }
                                        } else {
                                            paymenttype = bankname + " UPI Payment";
                                        }
                                    } else {
                                        paymenttype = paytmpaymentmode;
                                    }
                                } else {
                                    paymenttype = paytmpaymentmode;
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            paymentmode = "PAYTM";
                            updatePaymentTransactionInAws("SUCCESS", paymenttype, merchantpaymentid);
                            updatePaymentModeInOrderdetails();
                            updatePaymentModeInOrderDetailsLocal(orderid, merchantorderid, paymentmode);
                        }
                    }
                }

                if (isPaytmPaymentSuccess) {
                    closeActivity(paymentmode, paymenttype, "success");
                } else {
                    transactioninprogress = true;
                    optionpaytmbtnclicked = false;
                    checkPaymentStatusFromMerchantorderid(merchantorderid);
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void closeActivity(String paymentmode, String paymenttype, String trxnstatus) {
        Handler closeActivityThread = new Handler();
        closeActivityThread.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("paymentmode", paymentmode);
                intent.putExtra("paymenttype", paymenttype);
                intent.putExtra("trxnstatus", trxnstatus);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
            }
        }, 2000);
    }

 /* private void startSchedulerThreadForCheckingTransaction() {
        int paymentschedulerdelaytime = settingsUtil.getPaymentschedulerdelaytime() * 1000;
        Handler schedulerThread = new Handler();
        schedulerThread.postDelayed(new Runnable() {
            public void run() {
                if (isStopSchedulerThread) {
                    Log.d(TAG, "schedulerThread stopped ");
                    return;
                }
                Log.d(TAG, "schedulerThread started ");
                String action = TMCUtil.getInstance().getCurrentTime();
                logEventInGAnalytics("scheduler thread started", action, settingsUtil.getMobile());
                checkPaymentStatusFromMerchantorderid(merchantorderid);
            }
        }, paymentschedulerdelaytime);
    } */

    private String generatePaytmOrderId() {
        String random1 = "paytmorder_" + System.nanoTime();
        String action = random1 + ":" + TMCUtil.getInstance().getCurrentTime();
        logEventInGAnalytics("Paytm orderid generated", action, settingsUtil.getMobile());
        return random1;
    }

    private void generateOrderIdForRazorpay(double billvalue) {
        double razorpayamountinpaisa = 0;
        if (billvalue > 0) {
            razorpayamountinpaisa = billvalue * 100;
        }
        int finalbillvalue = (int) razorpayamountinpaisa;
        String billamt = "" + finalbillvalue;

        TMCRestClient.generateOrderId(billamt, generateTransactionId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    // Log.d(TAG, "TMCRestClient.generateOrderId "+jsonObject);
                    merchantorderid = jsonObject.getString("id");
                    saveLastTransactionDetailsInSharedPref();
                    String action = merchantorderid + ":" + TMCUtil.getInstance().getCurrentTime();
                    logEventInGAnalytics("Razorpay orderid generated", action, settingsUtil.getMobile());
                    addPaymentTransactionInAWS();
                    Intent intent = new Intent(PayOnlineActivity.this, RazorpayActivity.class);
                    intent.putExtra("merchantorderid", merchantorderid);
                    intent.putExtra("usermobile", settingsUtil.getMobile());
                    intent.putExtra("payableamount", billvalue);
                    startRazorpayActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d("PaymentModeActivity", "generateOrderIdForRazorpay failed0 jsonObject "+jsonObject+ " throwable "+throwable); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d("PaymentModeActivity", "generateOrderIdForRazorpay failed1 String "+s+ " throwable "+throwable); //No i18n
            }
        });
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

    private void addPaymentTransactionInAWS() {
        // Log.d(TAG, "addPaymentTransactionInAWS method");
        // String mobileno = "+91" + orderdetails.getUsermobile();
        String mobileno = "+" + settingsUtil.getMobile();
        String currenttime = TMCUtil.getInstance().getCurrentTime();
        Paymenttransaction paymenttransaction = new Paymenttransaction(mobileno, orderid, merchantorderid, currenttime, paymentmode,
                billvalue, paymenttransactionstatus);

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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private boolean isCheckPaymentStatusMethodCalled = false;
    private void checkPaymentStatusFromMerchantorderid(String merchantorderid) {
        // Log.d(TAG, "isCheckPaymentStatusMethodCalled "+isCheckPaymentStatusMethodCalled);
     /* if (isCheckPaymentStatusMethodCalled) {
            logEventInGAnalytics("checkPaymentStatusFromMerchantorderid", "isCheckPaymentStatusMethodCalled true", settingsUtil.getMobile());
            return;
        } */
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
                                    closeActivity(paymentmode,"", "success");
                                } else {
                                    updatePaymentTransactionInAws( "FAILED", "","");
                                    showTMCAlert(R.string.paymentfailedalert_title, R.string.paymentfailed_alert);
                                    settingsUtil.setLastTransactionDetails("");
                                    hideLoadingAnim();
                                }
                            }
                            transactioninprogress = false;
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
                        transactioninprogress = false;
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
        Volley.newRequestQueue(this).add(jsonObjectRequest);
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
                                    closeActivity(paymentmode,"",  "success");
                                } else {
                                    updatePaymentTransactionInAws("FAILED", "", "");
                                    showTMCAlert(R.string.paymentfailedalert_title, R.string.paymentfailed_alert);
                                    settingsUtil.setLastTransactionDetails("");
                                    hideLoadingAnim();
                                }
                            }
                            transactioninprogress = false;
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
                        transactioninprogress = false;
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
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getRazorpayPaymentTypeAndUpdateInPaymentTrans(String paymentstatus, String merchantpaymentid) {
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
                                    updatePaymentTransactionInAws(paymentstatus, paymenttype, merchantpaymentid);
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
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void updatePaymentTransactionInAws(String status, String paymenttype, String merchantpaymentid) {
        String paymenttranskey = settingsUtil.getLastPaymentTransKey();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", paymenttranskey);
            jsonObject.put("status", status);
            jsonObject.put("merchantpaymentid", merchantpaymentid);
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
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void updatePaymentModeInOrderdetails() {
     // String orderdetailskey = settingsUtil.getLastOrderdetailsKey();
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
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showOrderCancelledAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(PayOnlineActivity.this, title, message,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                startHomeScreenActivity();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void startHomeScreenActivity() {
        TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showConfirmationAlertForOrderCancellation(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(PayOnlineActivity.this, title, message,
                        R.string.yes_capt, R.string.no_capt,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                getOrderTrackingDetailsAndUpdateOrderStatus(orderid, Orderdetails.ORDERSTATUS_CANCELLED);
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void showOrderCancelNotPossibleAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(PayOnlineActivity.this, title, message,
                        R.string.contactsupport_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                if ((supportmobileno != null) && !(TextUtils.isEmpty(supportmobileno))) {
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + supportmobileno)); // NO I18N
                                    startActivity(callIntent);
                                }
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }


    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(PayOnlineActivity.this, title, message,
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

    private void logEventInGAnalytics(String category, String action, String label) {
        try {
            String label1 = label.substring(2);
            if (tmcTracker == null) {
                tmcTracker = ((TMCApplication) getApplication()).getDefaultTracker();
            }
            tmcTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action).setLabel(label1)
                    .build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getOrderDetailsKeyAndUpdateInSqlite(String orderid) {
        String url = TMCRestClient.AWS_GETORDERDETAILSFROMORDERID + "?orderid=" + orderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getOrderDetailsKeyAndUpdateInSqlite jsonObject "+response);
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String serverkey = jsonObject1.getString("key");
                                    updateOrderDetailsKeyInSqlite(orderid, serverkey);
                                    orderdetailskey = serverkey;
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

    private void updateOrderDetailsKeyInSqlite(String orderid, String serverkey) {
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        Orderdetailslocal orderdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                                               getOrderdetailsFromSqlite(helper, orderid);
        orderdetailslocal.setServerkey(serverkey);
        orderdetailslocal.update(helper);
    }

    public Orderdetailslocal getOrderdetailsLocal(String orderid) {
        try {
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            Orderdetailslocal orderdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                    getOrderdetailsFromSqlite(helper, orderid);
            return orderdetailslocal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean isShowCancelOrderLayout(String orderplacedtime) {
        long orderplacedtimeinlong = TMCUtil.getInstance().getTimeInLong(orderplacedtime);
        long currenttimeinlong = TMCUtil.getInstance().getCurrentTimeInLong();
     // Log.d(TAG, "orderplacedtimeinlong "+orderplacedtimeinlong+" currenttimeinlong "+currenttimeinlong);
        long difftime = currenttimeinlong - orderplacedtimeinlong;
     // Log.d(TAG, "difftime "+difftime);
        if (difftime <= 180000) {  // 3 mins
            cancelorder_layout.setVisibility(View.VISIBLE);
            return true;
        } else {
            cancelorder_layout.setVisibility(View.GONE);
            return false;
        }
    }

    private void getOrderTrackingDetailsAndUpdateOrderStatus(String orderid, String orderstatus) {
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETORDERTRACKINGDETAILS + "?orderid=" + orderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "getOrderTrackingDetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                String ordertrackingdetailskey = "";
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject ordertrackingjsonobj = jsonArray.getJSONObject(i);
                                    ordertrackingdetailskey = ordertrackingjsonobj.getString("key");
                                }

                                Log.d(TAG, "ordertrackingdetailskey "+ordertrackingdetailskey);
                                if ((ordertrackingdetailskey != null) && !(TextUtils.isEmpty(ordertrackingdetailskey))) {
                                    updateStatusInOrderTrackingDetailsAWS(ordertrackingdetailskey, orderid, orderstatus);
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(9000, 1,
                                               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void updateStatusInOrderTrackingDetailsAWS(String ordertrackingdetailskey, String orderid, String orderstatus) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", ordertrackingdetailskey);
            jsonObject.put("orderstatus", orderstatus);
            jsonObject.put("ordercancelledtime", TMCUtil.getInstance().getCurrentTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     // Log.d(TAG, "updateStatusInOrderTrackingDetailsAWS jsonobject "+jsonObject);
        String action = ordertrackingdetailskey+"-" + TMCUtil.getInstance().getCurrentTime();
        logEventInGAnalytics("ConfirmationActivity Before Cancel Order", action, settingsUtil.getMobile());
        String url = TMCRestClient.AWS_UPDATESTATUSINORDERTRACKING + "?modulename=TrackOrder";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    String message = response.getString("message");
                    Log.d(TAG, "updateStatusInOrderTrackingDetailsAWS status "+message);
                    if (message.equalsIgnoreCase("success")) {
                        cancelorder_btn.setVisibility(View.GONE);
                        ordercanceldetails_desp.setText("ORDER CANCELLED");
                        isOrderCancelled = true;
                        updateOrderStatusInOrderTrackingDetailsLocal(orderid, orderstatus);
                        showOrderCancelledAlert(R.string.ordercancelledsuccessalert_title,
                                                         R.string.ordercancelledsuccessalert_desp);
                        String action1 = ordertrackingdetailskey+"-" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("Order Cancelled Successfully", action1 , settingsUtil.getMobile());
                    }
                    hideLoadingAnim();
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void updateOrderStatusInOrderTrackingDetailsLocal(String orderid, String orderstatus) {
        Ordertrackingdetailslocal ordertrackingdetailslocal = getOrderTrackingDetailsLocal(orderid);
        try {
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            ordertrackingdetailslocal.setOrderstatus(orderstatus);
            ordertrackingdetailslocal.save(helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Ordertrackingdetailslocal getOrderTrackingDetailsLocal(String orderid) {
        try {
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            Ordertrackingdetailslocal ordertrackingdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                                                                      getOrderTrackingdetailsFromSqlite(helper, orderid);
            return ordertrackingdetailslocal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void updatePaymentModeInOrderDetailsLocal(String orderid, String merchantorderid, String paymentmode) {
        try {
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            Orderdetailslocal orderdetails = DatabaseManager.getInstance(getApplicationContext()).
                                                     getOrderdetailsFromSqlite(helper, orderid);
            orderdetails.setMerchantorderid(merchantorderid);
            orderdetails.setPaymentmode(paymentmode);
            orderdetails.update(helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }



}