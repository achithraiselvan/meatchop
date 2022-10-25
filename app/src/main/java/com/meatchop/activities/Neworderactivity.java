package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.amazonaws.mobile.client.AWSMobileClient;
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
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.TMCDetailOrderAdapter;
import com.meatchop.data.Coupontransactions;
import com.meatchop.data.Deliveryslots;
import com.meatchop.data.ItemWeightDetails;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.Ordertrackingdetails;
import com.meatchop.data.Paymenttransaction;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.helpers.DatabaseManager;
import com.meatchop.sqlitedata.Orderdetailslocal;
import com.meatchop.sqlitedata.Ordertrackingdetailslocal;
import com.meatchop.sqlitedata.Ratingorderdetailslocal;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.widget.PaymentEngine;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

    /*
            Add entry in PaymentTransaction with just orderid(No Merchantorderid)
            Add entry in OrderDetails with COD as payment mode
                    |
                    |
            When a customer clicks Razorpay button and merchantorderid is created,
            new entry has to be added in payment transaction table again with Created status
                    |
                    |
        Open Razorpay or Paytm Page---------------------------------------------- No Response
        (Start a scheduler thread to check          |                               |
        razorpay/paytm orderstatus after 3 mins)   |                                |
                    |                              |                    Scheduler thread will check the status
                    |                              |                               |
             Response: success             Response: Failure                       |
                   |                              |                                |
                   |                              |   ----------------------------/
                   |                              |  |
              Check RazorpayOrderstatus or PaytmOrderstatus ---------------------------
                   |                                                             |
                   |                                                             |
             Status: Paid                                                     Status: Failed ----- Show Payment Failure Alert
                   |                                                             |
                   |                                                          Update payment status in Payment transaction
            Update payment status in Payment transaction and Orderdetails

     */


public class Neworderactivity extends BaseActivity {

    private static final int PAYONLINE_ACT_REQ_CODE = 0;
    private static final int RAZORPAY_ACT_REQCODE = 1;
    private static final int PAYTM_ACT_REQCODE = 2;

    private String TAG = "Neworderactivity";
    private Orderdetails orderdetails;
    private String slotdesp;
    private Paymenttransaction paymenttransaction;
    private SettingsUtil settingsUtil;
    private String paymentmode;

    private String orderdetailskey;
    private boolean isaddneworder;
    private String orderid;

    private boolean callfromtmcordersactivity;
    private boolean callfromhomescreenactivity;

    private View loadinganimmask_layout;
    private View loadinganim_layout;

    private boolean ischeckforprevioustransaction = false;

    private View back_icon;
    private TMCTextView orderid_desp;
    private TMCTextView slotdetails_desp;
    private TMCTextView totalbillvalue_text;
    private TMCTextView deliveryaddressdetails_textview;
    private TMCTextView selectedpaymentmethod_text;
    private View selectedpaymentmethod_title;
    private View payonline_btn;
    private View onlinemodepayment_layout;
    private TMCTextView onlinemodepayment_desp;
    private TMCTextView onlinemodepaymenttype_desp;
    private View paymentdetails_layout;

    private View paymentpageloadinganim_layout;

    private View outfordeliveryselected_image;
    private View outfordeliveryunselected_image;
    private View orderoutfordeliverytextdetails_layout;
    private View orderdeliveredselected_image;
    private View orderdeliveredunselected_image;
    private View orderdeliveredtextdetails_layout;
    private TMCTextView outfordeliverytitle_textview;
    private TMCTextView outfordeliverydesp_textview;
    private TMCTextView orderdeliveredtitle_textview;
    private TMCTextView orderdelivereddesp_textview;

    private RecyclerView orderitems_recyclerview;
    private TMCDetailOrderAdapter tmcDetailOrderAdapter;
    private TMCTextView itemtotal_textview;
    private TMCTextView deliveryfee_textview;
    private TMCTextView taxesandcharges_textview;
    private TMCTextView amountpaid_textview;
    private TMCTextView tokenno_desp;
    private View tokenno_layout;
    private View callbutton_layout;
    private View callusbtnsmask_layout;
    private View callusbtns_layout;
    private View callstore_layout;
    private View callsupport_layout;
    private View calldeliverypartner_layout;

    private String currentorderstatus;
    private String deliverypartnermobileno;
    private String storemobileno;
    private String supportmobileno;

    private View paymentstatusloadinganim_layout;
    private View orderstatus_layout;
    private View orderstatusloadinganim_layout;

    private TMCTextView orderreceivedtitle_textview;
    private TMCTextView orderreceiveddesp_textview;

    private boolean isTransactionInProgress = false;

    private View itemdiscount_layout;
    private TMCTextView itemdiscountvalue_textview;

    private View coupondiscount_layout;
    private TMCTextView coupondiscountvalue_textview;

    private View cancelorder_layout;
    private View cancelorder_btn;
    private TMCTextView ordercanceldetails_desp;
    private View orderplacedloading_layout;

    // Payonline layout views
    private View payonlinemask_layout;
    private View payonline_layout;
    private View orderconfirmed_layout;
    private View cashondelivery_layout;
    private View option2_razorpaylayout;
    private View option3_paytmlayout;
    private TMCTextView razorpaydesp_textview;
    private TMCTextView paytmdesp_textview;
    private PaymentEngine paymentEngine;
    private boolean codpaymenttransactionadded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ordertracking_layout);

        settingsUtil = new SettingsUtil(this);

        if (getPackageName().equals("com.meatchop")) {
            orderdetails = getIntent().getParcelableExtra("orderdetails");
            slotdesp = getIntent().getStringExtra("slotdesp");
            isaddneworder = getIntent().getBooleanExtra("isaddneworder", false);
            orderid = getIntent().getStringExtra("orderid");
            callfromtmcordersactivity = getIntent().getBooleanExtra("callfromtmcordersactivity", false);
            callfromhomescreenactivity = getIntent().getBooleanExtra("callfromhomescreenactivity", false);
            ischeckforprevioustransaction = getIntent().getBooleanExtra("ischeckforprevioustransaction", false);
        }

        try {
            TMCVendor defaultVendor = new TMCVendor(new JSONObject(settingsUtil.getDefaultVendor()));
            storemobileno = defaultVendor.getVendormobile();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        supportmobileno = settingsUtil.getSupportMobileNo();

        paymentEngine = new PaymentEngine(orderdetails.getOrderid(), "", this, createHandlerForPaymentEngine());
        paymentEngine.setVendorkey(orderdetails.getVendorkey());

        cancelorder_layout = findViewById(R.id.cancelorder_layout);
        cancelorder_btn = findViewById(R.id.cancelorder_btn);
        ordercanceldetails_desp = (TMCTextView) findViewById(R.id.ordercanceldetails_desp);
        orderplacedloading_layout = findViewById(R.id.orderplacedloading_layout);

     /* cancelorder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isordercancellationpossible = false;
                if (orderdetails != null) {
                    isordercancellationpossible = isShowCancelOrderLayout(orderdetails.getOrderplacedtime());
                }
                if (isordercancellationpossible) {
                    showConfirmationAlertForOrderCancellation(R.string.ordercancelledalert_title, R.string.ordercancelledalert_desp);
                } else {
                    showOrderCancelNotPossibleAlert(R.string.ordercancelnotpossible_title, R.string.ordercancelnotpossible_desp);
                }
            }
        }); */

        back_icon = findViewById(R.id.back_icon);
        orderid_desp = (TMCTextView) findViewById(R.id.orderid_desp);
        slotdetails_desp = (TMCTextView) findViewById(R.id.slotdetails_desp);
        totalbillvalue_text = (TMCTextView) findViewById(R.id.totalbillvalue_text);
        deliveryaddressdetails_textview = (TMCTextView) findViewById(R.id.deliveryaddressdetails_textview);
        selectedpaymentmethod_text = (TMCTextView) findViewById(R.id.selectedpaymentmethod_text);
        payonline_btn = findViewById(R.id.payonline_btn);
        selectedpaymentmethod_title = findViewById(R.id.selectedpaymentmethod_title);

        paymentpageloadinganim_layout = findViewById(R.id.paymentpageloadinganim_layout);

        outfordeliveryselected_image = findViewById(R.id.outfordeliveryselected_image);
        outfordeliveryunselected_image = findViewById(R.id.outfordeliveryunselected_image);
        orderoutfordeliverytextdetails_layout = findViewById(R.id.orderoutfordeliverytextdetails_layout);
        orderdeliveredselected_image = findViewById(R.id.orderdeliveredselected_image);
        orderdeliveredunselected_image = findViewById(R.id.orderdeliveredunselected_image);
        orderdeliveredtextdetails_layout = findViewById(R.id.orderdeliveredtextdetails_layout);
        outfordeliverytitle_textview = (TMCTextView) findViewById(R.id.outfordeliverytitle_textview);
        outfordeliverydesp_textview = (TMCTextView) findViewById(R.id.outfordeliverydesp_textview);
        orderdeliveredtitle_textview = (TMCTextView) findViewById(R.id.orderdeliveredtitle_textview);
        orderdelivereddesp_textview = (TMCTextView) findViewById(R.id.orderdelivereddesp_textview);
        orderreceivedtitle_textview = (TMCTextView) findViewById(R.id.orderreceivedtitle_textview);
        orderreceiveddesp_textview = (TMCTextView) findViewById(R.id.orderreceiveddesp_textview);

        itemtotal_textview = (TMCTextView) findViewById(R.id.itemtotal_textview);
        deliveryfee_textview = (TMCTextView) findViewById(R.id.deliveryfee_textview);
        taxesandcharges_textview = (TMCTextView) findViewById(R.id.taxesandcharges_textview);
        amountpaid_textview = (TMCTextView) findViewById(R.id.amountpaid_textview);
        tokenno_desp = (TMCTextView) findViewById(R.id.tokenno_desp);
        tokenno_layout = findViewById(R.id.tokenno_layout);

        itemdiscount_layout = findViewById(R.id.itemdiscount_layout);
        itemdiscountvalue_textview = findViewById(R.id.itemdiscountvalue_textview);

        coupondiscount_layout = findViewById(R.id.coupondiscount_layout);
        coupondiscountvalue_textview = (TMCTextView) findViewById(R.id.coupondiscountvalue_textview);

        orderitems_recyclerview = findViewById(R.id.orderitems_recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        orderitems_recyclerview.setLayoutManager(mLayoutManager);
        orderitems_recyclerview.setItemAnimator(new DefaultItemAnimator());

        callusbtnsmask_layout = findViewById(R.id.callusbtnsmask_layout);
        callusbtns_layout = findViewById(R.id.callusbtns_layout);
        callstore_layout = findViewById(R.id.callstore_layout);
        callsupport_layout = findViewById(R.id.callsupport_layout);
        calldeliverypartner_layout = findViewById(R.id.calldeliverypartner_layout);

        onlinemodepayment_layout = findViewById(R.id.onlinemodepayment_layout);
        onlinemodepayment_desp = (TMCTextView) findViewById(R.id.onlinemodepayment_desp);
        onlinemodepaymenttype_desp = (TMCTextView) findViewById(R.id.onlinemodepaymenttype_desp);
        paymentdetails_layout = findViewById(R.id.paymentdetails_layout);
        paymentstatusloadinganim_layout = findViewById(R.id.paymentstatusloadinganim_layout);

        initiatePayOnlineViews();

        orderstatus_layout = findViewById(R.id.orderstatus_layout);
        orderstatusloadinganim_layout = findViewById(R.id.orderstatusloadinganim_layout);

        callbutton_layout = findViewById(R.id.callbutton_layout);
        callbutton_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCallBtnsLayout();
            }
        });

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        callstore_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((storemobileno != null) && !(TextUtils.isEmpty(storemobileno))) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + storemobileno)); // NO I18N
                    startActivity(callIntent);
                    hideCallBtnsLayout();
                }
            }
        });

        callsupport_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((supportmobileno != null) && !(TextUtils.isEmpty(supportmobileno))) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + supportmobileno)); // NO I18N
                    startActivity(callIntent);
                    hideCallBtnsLayout();
                }
            }
        });

        calldeliverypartner_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((deliverypartnermobileno != null) && !(TextUtils.isEmpty(deliverypartnermobileno))) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + deliverypartnermobileno)); // NO I18N
                    startActivity(callIntent);
                    hideCallBtnsLayout();
                }
            }
        });

        callusbtnsmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCallBtnsLayout();
            }
        });

        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganimmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

     /* payonline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayOnlineActivity(false);
            }
        }); */

        payonline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showPaymentLoadingAnim();
                // startPayOnlineActivity(false);
                orderconfirmed_layout.setVisibility(View.GONE);
                showPayOnlineLayout(300, false);
            }
        });

        isTransactionInProgress = true;
        // showPaymentLoadingAnim();
        // showOrderstatusLoadingAnim();
        orderid_desp.setText("#" + orderdetails.getOrderid());
        String slotname = orderdetails.getSlotname();
        if (slotname.equalsIgnoreCase(Orderdetails.SLOTNAME_PREORDER)) {
            slotdetails_desp.setText(orderdetails.getSlotdate() + " " + orderdetails.getSlottimerange());
        } else if (slotname.equalsIgnoreCase(Orderdetails.SLOTNAME_EXPRESSDELIVERY)) {
            slotdetails_desp.setText(slotname + " - " + orderdetails.getSlottimerange());
        } else if (slotname.equalsIgnoreCase(Orderdetails.SLOTNAME_SPECIALDAYPREORDER)) {
            slotdetails_desp.setText(orderdetails.getSlotdate() + " " + orderdetails.getSlottimerange());
        }

        String rs = getResources().getString(R.string.Rs);
        totalbillvalue_text.setText(rs + orderdetails.getPayableamount());
        deliveryaddressdetails_textview.setText(orderdetails.getUseraddress());
        selectedpaymentmethod_text.setText("CASH ON DELIVERY (CASH / UPI)");
        selectedpaymentmethod_title.setVisibility(View.VISIBLE);

        Orderdetailslocal orderdetailslocal = getOrderdetailsLocal(orderdetails.getOrderid());
        if (orderdetailslocal == null) {
            settingsUtil.setLastTransactionDetails("");
            addOrderDetails();
            // startSchedulerThreadForPayOnlineActivity(true);
        } else {
            fillItemAndOrderDetailsInUI();
            changeStatusLayout(Orderdetails.ORDERSTATUS_NEW);
        }

     /* if (isaddneworder) {

        } else {
            if (orderdetails == null) {
                getOrderDetails(orderid);
            } else {
                fillItemAndOrderDetailsInUI();
                getPaymentTransactionAndUpdatePaymentType(orderdetails.getMerchantorderid());
            }
            getOrderTrackingDetails(orderid);
        }

        String lasttransactiondetails = settingsUtil.getLastTransactionDetails();
        Log.d(TAG, "lasttransactiondetails "+lasttransactiondetails);
        if ((lasttransactiondetails != null) && (!TextUtils.isEmpty(lasttransactiondetails))) {
            try {
                JSONObject jsonObject = new JSONObject(lasttransactiondetails);
                String merchantorderid = jsonObject.getString("merchantorderid");
                String orderid = jsonObject.getString("orderid");
                String paymentmode = jsonObject.getString("paymentmode");
                paymentdetails_layout.setVisibility(View.GONE);
                onlinemodepayment_layout.setVisibility(View.GONE);
                paymentstatusloadinganim_layout.setVisibility(View.VISIBLE);
                if (paymentmode.equalsIgnoreCase("RAZORPAY")) {
                    getRazorpayOrderstatus(paymentmode, merchantorderid);
                } else if (paymentmode.equalsIgnoreCase("paytm")) {
                    getPaytmOrderStatus(paymentmode, merchantorderid);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } */
    }

    private void showCallBtnsLayout() {
        if ((deliverypartnermobileno == null) || (TextUtils.isEmpty(deliverypartnermobileno))) {
            calldeliverypartner_layout.setVisibility(View.GONE);
        } else {
            calldeliverypartner_layout.setVisibility(View.VISIBLE);
        }

        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                callusbtnsmask_layout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(400);
        callusbtns_layout.startAnimation(bottomUp);
        callusbtns_layout.setVisibility(View.VISIBLE);
    }

    private void hideCallBtnsLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                callusbtns_layout.setVisibility(View.GONE);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(300);
        callusbtns_layout.startAnimation(bottomDown);
        callusbtnsmask_layout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (isTransactionInProgress) {
            if ((merchantorderid != null) && !(TextUtils.isEmpty(merchantorderid))) {
                return;
            } else {
                hidePaymentLoadingAnim();
                return;
            }
        }
        if (callusbtns_layout.getVisibility() == View.VISIBLE) {
            hideCallBtnsLayout();
            return;
        }
        if (payonline_layout.getVisibility() == View.VISIBLE) {
            if ((paymentEngine != null) && !(paymentEngine.isCODPaymentTransactionUpdated())) {
                paymentmode = "CASH ON DELIVERY";
                paymentEngine.addPaymentTransactionInAWS(paymentmode, orderdetails.getPayableamount(), "Success");
            }
            hidePayOnlineLayout();
            return;
        }
        if (callfromtmcordersactivity || callfromhomescreenactivity) {
            setResult(RESULT_OK);
            finish();
        } else {
            startHomeScreenActivity();
        }
    }

    private void startHomeScreenActivity() {
        TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void fillItemAndOrderDetailsInUI() {
        if (orderdetails != null) {
            try {
                orderid_desp.setText("#" + orderdetails.getOrderid());
                String slotname = orderdetails.getSlotname();
                if (slotname.equalsIgnoreCase(Orderdetails.SLOTNAME_PREORDER)) {
                    slotdetails_desp.setText(orderdetails.getSlotdate() + " " + orderdetails.getSlottimerange());
                } else if (slotname.equalsIgnoreCase(Orderdetails.SLOTNAME_EXPRESSDELIVERY)) {
                    slotdetails_desp.setText(slotname + " - " + orderdetails.getSlottimerange());
                } else if (slotname.equalsIgnoreCase(Orderdetails.SLOTNAME_SPECIALDAYPREORDER)) {
                    slotdetails_desp.setText(orderdetails.getSlotdate() + " " + orderdetails.getSlottimerange());
                }

                String rs = getResources().getString(R.string.Rs);
                totalbillvalue_text.setText(rs + orderdetails.getPayableamount());
                deliveryaddressdetails_textview.setText(orderdetails.getUseraddress());
                paymentmode = orderdetails.getPaymentmode();
                changePaymentStatusInUI(paymentmode);

                if ((orderdetails.getTokenno() != null) && !(TextUtils.isEmpty(orderdetails.getTokenno()))) {
                    tokenno_desp.setText(orderdetails.getTokenno());
                    tokenno_layout.setVisibility(View.VISIBLE);
                } else {
                    tokenno_layout.setVisibility(View.GONE);
                }

                if (orderdetails.getDiscountamount() > 0) {
                    double discountamount = orderdetails.getDiscountamount();
                    double itemtotalplusdiscamount = orderdetails.getItemtotal() + discountamount;
                    itemdiscountvalue_textview.setText(rs + String.format("%.2f", discountamount));
                    itemtotal_textview.setText(rs + String.format("%.2f", itemtotalplusdiscamount));
                    itemdiscount_layout.setVisibility(View.VISIBLE);
                } else {
                    itemdiscount_layout.setVisibility(View.GONE);
                    itemtotal_textview.setText(rs + String.format("%.2f", orderdetails.getItemtotal()));
                }

                if (orderdetails.getCoupondiscount() > 0) {
                    double coupondiscount = orderdetails.getCoupondiscount();
                    coupondiscountvalue_textview.setText(rs + String.format("%.2f", coupondiscount));
                    coupondiscount_layout.setVisibility(View.VISIBLE);
                } else {
                    coupondiscount_layout.setVisibility(View.GONE);
                }

                orderid_desp.setText("#" + orderdetails.getOrderid());
                deliveryfee_textview.setText(rs + String.format("%.2f", orderdetails.getDeliveryamount()));
                taxesandcharges_textview.setText(rs + String.format("%.2f", orderdetails.getGstamount()));
                amountpaid_textview.setText(rs + String.format("%.2f", orderdetails.getPayableamount()));

                String itemdetails = orderdetails.getItemdesp();
                JSONObject itemdespjson = new JSONObject(itemdetails);
                JSONArray jsonArray = itemdespjson.getJSONArray("itemdesp");
                tmcDetailOrderAdapter = new TMCDetailOrderAdapter(getApplicationContext(), jsonArray);
                orderitems_recyclerview.setAdapter(tmcDetailOrderAdapter);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void changeStatusLayout(String status) {
        Typeface opensansboldface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        Typeface opensansregularface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");

        if (status.equalsIgnoreCase(Orderdetails.ORDERSTATUS_NEW)) {
            outfordeliveryselected_image.setVisibility(View.GONE);
            outfordeliveryunselected_image.setVisibility(View.VISIBLE);
            orderdeliveredselected_image.setVisibility(View.GONE);
            orderdeliveredunselected_image.setVisibility(View.VISIBLE);

            orderreceivedtitle_textview.setText("Order Received");
            orderreceiveddesp_textview.setText("We have received your order");

            outfordeliverytitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            outfordeliverydesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            outfordeliverytitle_textview.setTypeface(opensansregularface);

            orderdeliveredtitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdelivereddesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdeliveredtitle_textview.setTypeface(opensansregularface);
        } else if ((status.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CONFIRMED)) ||
                (status.equalsIgnoreCase(Orderdetails.ORDERSTATUS_READYFORPICKUP))) {
            outfordeliveryselected_image.setVisibility(View.GONE);
            outfordeliveryunselected_image.setVisibility(View.VISIBLE);
            orderdeliveredselected_image.setVisibility(View.GONE);
            orderdeliveredunselected_image.setVisibility(View.VISIBLE);

            orderreceivedtitle_textview.setText("Order Confirmed");
            orderreceiveddesp_textview.setText("We have confirmed your order");

            outfordeliverytitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            outfordeliverydesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            outfordeliverytitle_textview.setTypeface(opensansregularface);

            orderdeliveredtitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdelivereddesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdeliveredtitle_textview.setTypeface(opensansregularface);

        } else if (status.equalsIgnoreCase("PICKED UP")) {
            outfordeliveryselected_image.setVisibility(View.VISIBLE);
            outfordeliveryunselected_image.setVisibility(View.GONE);
            orderdeliveredselected_image.setVisibility(View.GONE);
            orderdeliveredunselected_image.setVisibility(View.VISIBLE);

            outfordeliverytitle_textview.setTextColor(getResources().getColor(R.color.black));
            outfordeliverydesp_textview.setTextColor(getResources().getColor(R.color.primary_text_color));
            outfordeliverytitle_textview.setTypeface(opensansboldface);

            orderdeliveredtitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdelivereddesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdeliveredtitle_textview.setTypeface(opensansregularface);

        } else if (status.equalsIgnoreCase("DELIVERED")) {
            outfordeliveryselected_image.setVisibility(View.VISIBLE);
            outfordeliveryunselected_image.setVisibility(View.GONE);

            orderdeliveredselected_image.setVisibility(View.VISIBLE);
            orderdeliveredunselected_image.setVisibility(View.GONE);

            outfordeliverytitle_textview.setTextColor(getResources().getColor(R.color.black));
            outfordeliverydesp_textview.setTextColor(getResources().getColor(R.color.primary_text_color));
            outfordeliverytitle_textview.setTypeface(opensansboldface);

            orderdeliveredtitle_textview.setTextColor(getResources().getColor(R.color.black));
            orderdelivereddesp_textview.setTextColor(getResources().getColor(R.color.primary_text_color));
            orderdeliveredtitle_textview.setTypeface(opensansboldface);
            payonline_btn.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("CANCELLED")) {
            outfordeliveryselected_image.setVisibility(View.GONE);
            outfordeliveryunselected_image.setVisibility(View.VISIBLE);
            orderdeliveredselected_image.setVisibility(View.GONE);
            orderdeliveredunselected_image.setVisibility(View.VISIBLE);

            orderreceivedtitle_textview.setText("Order Cancelled");
            orderreceiveddesp_textview.setText("Your order has been cancelled");

            outfordeliverytitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            outfordeliverydesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            outfordeliverytitle_textview.setTypeface(opensansregularface);

            orderdeliveredtitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdelivereddesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdeliveredtitle_textview.setTypeface(opensansregularface);
            payonline_btn.setVisibility(View.GONE);
        }
    }

    private boolean startschedulerthreadforpayonlineactivity = false;
    private void startSchedulerThreadForPayOnlineActivity(boolean showcodoption) {
        int delaytime = 3000;
        Handler schedulerThread = new Handler();

        schedulerThread.postDelayed(new Runnable() {
            public void run() {
                if (startschedulerthreadforpayonlineactivity) {
                    return;
                }
                startschedulerthreadforpayonlineactivity = true;
                hideOrderPlacedLoadingPage();
                hidePaymentLoadingAnim();
                // startPayOnlineActivity(showcodoption);
                isTransactionInProgress = false;
                showPayOnlineLayout(500, true);

            }
        }, delaytime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PAYONLINE_ACT_REQ_CODE :
                hidePaymentLoadingAnim();
                hideOrderPlacedLoadingPage();
                boolean ordercancelled = false;
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        showLoadingAnim();
                        ordercancelled = data.getBooleanExtra("ordercancelled", false);
                        if (ordercancelled) {
                            changeStatusLayout("CANCELLED");
                        }
                        String trxnstatus = data.getStringExtra("trxnstatus");
                        if (trxnstatus.equalsIgnoreCase("success")) {
                            String paymenttype = data.getStringExtra("paymenttype");
                            String paymentmode = data.getStringExtra("paymentmode");
                            if ((paymentmode.equalsIgnoreCase("RAZORPAY")) ||
                                    (paymentmode.equalsIgnoreCase("PAYTM"))) {
                                showTMCAlert(R.string.onlinepaymentsuccessalert_title, R.string.onlinepaymentsuccessalert_desp);
                            }
                            changePaymentStatusInUI(paymentmode, paymenttype);
                        } else {
                            showTMCAlert(R.string.paymentfailedalert_title, R.string.paymentfailed_alert);
                            changePaymentStatusInUI("", "");
                        }
                        settingsUtil.setLastTransactionDetails("");
                    }
                } else {
                    showTMCAlert(R.string.paymentfailedalert_title, R.string.paymentfailed_alert);
                    changePaymentStatusInUI("", "");
                    settingsUtil.setLastTransactionDetails("");
                }
                // getOrderTrackingDetails(orderid);
             /* if (!ordercancelled) {
                    isShowCancelOrderLayout(orderdetails.getOrderplacedtime());
                } */
                hideLoadingAnim();
                break;

            case RAZORPAY_ACT_REQCODE :
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String trxnstatus = data.getStringExtra("trxnstatus");
                        if (trxnstatus.equalsIgnoreCase("success")) {
                            String merchantpaymentid = data.getStringExtra("merchantpaymentid");
                            paymentmode = "RAZORPAY";
                            DatabaseHelper helper = getHelper();
                            if (helper == null) {
                                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
                            }
                            paymentEngine.updatePaymentModeInOrderdetails();
                            paymentEngine.updatePaymentTransactionInAws("SUCCESS", "", merchantpaymentid);
                            paymentEngine.getRazorpayPaymentTypeAndUpdateInPaymentTrans("SUCCESS",
                                    merchantpaymentid, orderid, helper);
                            paymentEngine.updatePaymentModeInOrderDetailsLocal(orderid,
                                    merchantorderid, paymentmode, "", helper);
                            showTMCAlert(R.string.onlinepaymentsuccessalert_title, R.string.onlinepaymentsuccessalert_desp);
                            changePaymentStatusInUI(paymentmode, "");
                            hidePaymentLoadingAnim();
                            isTransactionInProgress = false;
                            settingsUtil.setLastTransactionDetails("");
                        } else {
                            isTransactionInProgress = true;
                            optiononerazorpaybtnclicked = false;
                            paymentmode = "RAZORPAY";
                            String action = "Paymentmode:" + paymentmode + "merchantorderid: " + merchantorderid;
                            logEventInGAnalytics("Razorpay Recheck payment status", action, settingsUtil.getMobile());
                            paymentEngine.checkPaymentStatusFromMerchantorderid(paymentmode, merchantorderid);
                        }
                    }
                }

                break;

            case PAYTM_ACT_REQCODE :
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
                            paymentEngine.updatePaymentTransactionInAws("SUCCESS", paymenttype, merchantpaymentid);
                            paymentEngine.updatePaymentModeInOrderdetails();
                            paymentEngine.updatePaymentModeInOrderDetailsLocal(orderid, merchantorderid,
                                    paymentmode, paymenttype, getDatabaseHelper());
                            showTMCAlert(R.string.onlinepaymentsuccessalert_title, R.string.onlinepaymentsuccessalert_desp);
                            isTransactionInProgress = false;
                            changePaymentStatusInUI(paymentmode, paymenttype);
                            hidePaymentLoadingAnim();
                            settingsUtil.setLastTransactionDetails("");
                        } else {
                            isTransactionInProgress = true;
                            optionpaytmbtnclicked = false;
                            paymentmode = "PAYTM";
                            String action = "Paymentmode:" + paymentmode + "merchantorderid: " + merchantorderid;
                            logEventInGAnalytics("Paytm Recheck payment status", action, settingsUtil.getMobile());
                            paymentEngine.checkPaymentStatusFromMerchantorderid(paymentmode, merchantorderid);
                        }
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changePaymentStatusInUI(String paymentmode) {
        changePaymentStatusInUI(paymentmode, "");
    }

    private void changePaymentStatusInUI(String paymentmode, String paymenttype) {
        paymentstatusloadinganim_layout.setVisibility(View.GONE);
        if (paymentmode.equalsIgnoreCase("RAZORPAY")) {
            paymentdetails_layout.setVisibility(View.GONE);
            onlinemodepayment_layout.setVisibility(View.VISIBLE);
            onlinemodepayment_desp.setText("Paid Online (RAZORPAY)");
            if ((paymenttype != null) && !(TextUtils.isEmpty(paymenttype))) {
                onlinemodepaymenttype_desp.setVisibility(View.VISIBLE);
                onlinemodepaymenttype_desp.setText("Mode of Payment: " + paymenttype);
            } else {
                onlinemodepaymenttype_desp.setVisibility(View.GONE);
            }
        } else if (paymentmode.equalsIgnoreCase("PAYTM")) {
            paymentdetails_layout.setVisibility(View.GONE);
            onlinemodepayment_layout.setVisibility(View.VISIBLE);
            onlinemodepayment_desp.setText("Paid Online (PAYTM)");
            if ((paymenttype != null) && !(TextUtils.isEmpty(paymenttype))) {
                onlinemodepaymenttype_desp.setVisibility(View.VISIBLE);
                onlinemodepaymenttype_desp.setText("Mode of Payment: " + paymenttype);
            } else {
                onlinemodepaymenttype_desp.setVisibility(View.GONE);
            }
        } else {
            paymentdetails_layout.setVisibility(View.VISIBLE);
            onlinemodepayment_layout.setVisibility(View.GONE);
            selectedpaymentmethod_text.setText("CASH ON DELIVERY (CASH / UPI)");
            selectedpaymentmethod_title.setVisibility(View.VISIBLE);
            payonline_btn.setVisibility(View.VISIBLE);
            if ((paymenttype != null) && !(TextUtils.isEmpty(paymenttype))) {
                onlinemodepaymenttype_desp.setVisibility(View.VISIBLE);
                onlinemodepaymenttype_desp.setText("Mode of Payment: " + paymenttype);
            } else {
                onlinemodepaymenttype_desp.setVisibility(View.GONE);
            }
        }
    }

    private void startPayOnlineActivity(boolean showcodoption) {
        Intent intent = new Intent(Neworderactivity.this, PayOnlineActivity.class);
        intent.putExtra("billvalue", orderdetails.getPayableamount());
        intent.putExtra("orderid", orderdetails.getOrderid());
        intent.putExtra("orderdetailskey", orderdetails.getKey());
        intent.putExtra("showcodoption", showcodoption);
        intent.putExtra("showordercanceloption", true);
        startActivityForResult(intent, PAYONLINE_ACT_REQ_CODE);
        // overridePendingTransition(0, 0);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

    private void addPaymentTransactionInAWS() {
        // Log.d(TAG, "addPaymentTransactionInAWS method");
        // String mobileno = "+91" + orderdetails.getUsermobile();
        String mobileno = orderdetails.getUsermobile();
        double transactionamount = orderdetails.getPayableamount();
        String currenttime = TMCUtil.getInstance().getCurrentTime();
        paymentmode = "CASH ON DELIVERY";
        paymenttransaction = new Paymenttransaction(mobileno, orderid, "", currenttime, paymentmode,
                transactionamount, "Success");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobileno", paymenttransaction.getMobileno());
            jsonObject.put("orderid", paymenttransaction.getOrderid());
            jsonObject.put("paymentmode", paymenttransaction.getPaymentmode());
            jsonObject.put("transactiontime", paymenttransaction.getTransactiontime());
            jsonObject.put("transactionamount", paymenttransaction.getTransactionamount());
            jsonObject.put("userkey", settingsUtil.getUserkey());
            jsonObject.put("status", paymenttransaction.getStatus());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_ADDPAYMENTTRANS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    //  Log.d(TAG, "addPaymentTrans jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = response.getJSONObject("content");
                        String key = jsonObject1.getString("key");
                        Log.d(TAG, "addPaymentTransactionInAWS key "+key);
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

    private boolean isAddOrderDetailsCalled = false;
    private void addOrderDetails() {
        if (isAddOrderDetailsCalled) {
            logEventInGAnalytics("addOrderDetails", "isAddOrderDetailsCalled true. Duplicate entry", settingsUtil.getMobile());
            return;
        }
        isAddOrderDetailsCalled = true;
        // String orderid = generateOrderid();
        // orderdetails.setOrderid(orderid);
        orderdetails.setOrderplacedtime(TMCUtil.getInstance().getCurrentTime());
        orderdetails.setOrderplaceddatenew(TMCUtil.getInstance().getCurrentDateNew());
        orderdetails.setUserstatus(settingsUtil.getUserStatus());
        paymentmode = "CASH ON DELIVERY";
        orderdetails.setPaymentmode(paymentmode);
        String statusorderid = "Before adding Order in AWS" + "-" + orderid + ":" + TMCUtil.getInstance().getCurrentTime();
        logEventInGAnalytics("addOrderDetails", statusorderid, settingsUtil.getMobile());

        addOrderDetailsToAWS();
    }

    private void addOrderDetailsToAWS() {
        String url = TMCRestClient.AWS_ADDCUSTORDDETAILS;
        JSONObject jsonObject = null;
        String itemdesp = orderdetails.getItemdesp();
        boolean isShowOrderPlacedFailureAlert = false;
        try {
            jsonObject = new JSONObject(orderdetails.getJSONStringNew());
        } catch (Exception ex) {
            logEventInGAnalytics("addOrderDetails exception", ex.getMessage(), settingsUtil.getMobile());
            logEventInGAnalytics("addOrderDetails exception itemdesp", itemdesp, settingsUtil.getMobile());
            ex.printStackTrace();
            isShowOrderPlacedFailureAlert = true;
        }
        if (jsonObject == null) {
            logEventInGAnalytics("addOrderDetails json object null", itemdesp, settingsUtil.getMobile());
            isShowOrderPlacedFailureAlert = true;
        }
        if (isShowOrderPlacedFailureAlert) {
            showOrderNotPlacedAlertAndClearCart();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                 // Log.d(TAG, "addOrderDetails jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        startSchedulerThreadForPayOnlineActivity(true);
                        JSONObject jsonObject1 = response.getJSONObject("content");
                     // orderdetailskey = jsonObject1.getString("key");
                        String orderid = jsonObject1.getString("orderid");
                        orderdetails.setKey(orderdetailskey);
                        if (paymentEngine != null) {
                         // paymentEngine.setOrderdetailskey(orderdetailskey);
                            paymentEngine.setOrderid(orderid);
                        }
                        completeOrderCompletionProcesses();
                        settingsUtil.setLastOrderdetailsKey(orderdetailskey);
                        // updateServerKeyInOrderDetailsLocal(orderid, orderdetailskey);
                        // Log.d(TAG, "addPaymentTransactionInAWS key "+orderdetailskey);
                        String orderidkey = orderid + "-" + orderdetailskey;
                        String orderidaction = orderidkey + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("addCustOrdDetToAWS", orderidaction, settingsUtil.getMobile());
                    } else {
                        logEventInGAnalytics("addCustOrdDetToAWS failed", "Order not inserted", settingsUtil.getMobile());
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
                logEventInGAnalytics("addCustOrdDetToAWS error", error.getMessage(), settingsUtil.getMobile());
                addOrderDetailsToAWS();

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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

        String vendororderdetailsurl = TMCRestClient.AWS_ADDVENDORDDETAILS;
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, vendororderdetailsurl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                    // Log.d(TAG, "addOrderDetails jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        String orderidkey = orderid + "-" + orderdetailskey;
                        String orderidaction = orderidkey + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("addVendOrdDetToAWS", orderidaction, settingsUtil.getMobile());
                    } else {
                        logEventInGAnalytics("addVendOrdDetToAWS failed", "Order not inserted", settingsUtil.getMobile());
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
                logEventInGAnalytics("addVendOrdDetToAWS error", error.getMessage(), settingsUtil.getMobile());

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
        RetryPolicy mRetryPolicy1 = new DefaultRetryPolicy(15000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest1.setRetryPolicy(mRetryPolicy1);
        Volley.newRequestQueue(this).add(jsonObjectRequest1);

    }

    private void completeOrderCompletionProcesses() {
        addOrderdetailsToSqlite(orderdetails);
        if (orderdetails.getCoupondiscount() > 0) {
            addCouponTransToAWS();
        }
        sendOrderConfirmationSMS(settingsUtil.getMobile(), orderdetails.getOrderid(), orderdetails.getPayableamount());
        sendOrderConfirmationNotification(orderid, orderdetails.getPayableamount());
        // sendSMSToVendor(vendormobile, settingsUtil.getMobile(), orderdetails.getOrderid());

     /* if (!(orderdetails.getVendorkey().equalsIgnoreCase("vendor_2"))) {
            sendOrderConfirmationMail();
        } */

        settingsUtil.setLastPlacedOrderId(orderdetails.getOrderid());
        settingsUtil.setLastOrderIdForRating(orderdetails.getOrderid());
        settingsUtil.setLastPlacedOrderslotname(orderdetails.getSlotname());
        String itemdesp = orderdetails.getItemdesp();
        if (settingsUtil.isInventoryCheck()) {
            updateStockDetailsInMenuItems(orderdetails.getMenustockdetails());
        }

        try {
            JSONObject itemdespjson = new JSONObject(itemdesp);
            JSONArray jsonArray = itemdespjson.getJSONArray("itemdesp");
            String vendorkey = orderdetails.getVendorkey();
            String orderplacedtime = orderdetails.getOrderplacedtime();
            String menuitemids = "";
            // Log.d(TAG, "addOrderDetailsToAWS jsonArray "+jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                String menuitemid = jsonobject.getString("menuitemid");
                if (i == 0) {
                    menuitemids = menuitemid;
                } else {
                    menuitemids = menuitemids + "," + menuitemid;
                }
                String itemname = jsonobject.getString("itemname");
                String netweight = jsonobject.getString("netweight");
                String portionsize = jsonobject.getString("portionsize");
                double gstamount = jsonobject.getDouble("gstamount");
                int quantity = jsonobject.getInt("quantity");
                double baseamount = jsonobject.getDouble("tmcprice");
                double appmarkuppercentage = 0;
                if (!jsonobject.isNull("appmarkuppercentage")) {
                    appmarkuppercentage = jsonobject.getDouble("appmarkuppercentage");
                }

                JSONObject orderItemJsonObj = new JSONObject();
                orderItemJsonObj.put("orderid", orderid);
                orderItemJsonObj.put("itemname", itemname);
                orderItemJsonObj.put("netweight", netweight);
                orderItemJsonObj.put("quantity", quantity);
                orderItemJsonObj.put("gstamount", gstamount);
                orderItemJsonObj.put("tmcprice", baseamount);
                orderItemJsonObj.put("vendorkey", vendorkey);
                orderItemJsonObj.put("orderplacedtime", orderplacedtime);
                orderItemJsonObj.put("portionsize", portionsize);
                orderItemJsonObj.put("slotname", orderdetails.getSlotname());
                orderItemJsonObj.put("slotdate", orderdetails.getSlotdatenew());
                if (appmarkuppercentage > 0) {
                    orderItemJsonObj.put("appmarkuppercentage", appmarkuppercentage);
                }

                if (jsonobject.has("cutname")) {
                    String cutname = jsonobject.getString("cutname");
                    orderItemJsonObj.put("cutname", cutname);
                }

                if (jsonobject.has("applieddiscpercentage")) {
                    double applieddiscpercentage = jsonobject.getDouble("applieddiscpercentage");
                    orderItemJsonObj.put("applieddiscountpercentage", applieddiscpercentage);
                }
                if (jsonobject.has("discountamount")) {
                    double discountamount = jsonobject.getDouble("discountamount");
                    orderItemJsonObj.put("discountamount", discountamount);
                }

                String tmcsubctgykey = jsonobject.getString("tmcsubctgykey");
                if (jsonobject.has("grossweightingrams")) {
                    String grosswtinggramsstr = jsonobject.getString("grossweightingrams");
                    if ((grosswtinggramsstr != null) && (!TextUtils.isEmpty(grosswtinggramsstr))) {
                        double grossweightingrams = Double.parseDouble(grosswtinggramsstr);
                        orderItemJsonObj.put("grossweightingrams", grossweightingrams);
                    }
                }

                orderItemJsonObj.put("tmcsubctgykey", tmcsubctgykey);
                orderItemJsonObj.put("vendorname", orderdetails.getVendorname());

                // Log.d(TAG, "orderItemJsonObj "+orderItemJsonObj);
                addOrderSubDetails(orderItemJsonObj);
            }

            JSONObject lastplacedordermenuitems = new JSONObject();
            lastplacedordermenuitems.put("orderid", orderid);
            lastplacedordermenuitems.put("orderplacedtime", orderdetails.getOrderplacedtime());
            lastplacedordermenuitems.put("menuitemids", menuitemids);
            settingsUtil.setLastPlacedOrderMenuItems(lastplacedordermenuitems.toString());
            Log.d(TAG, "lastplacedordermenuitems "+settingsUtil.getLastPlacedOrderMenuItems());

        } catch (Exception ex) {
            logEventInGAnalytics("addOrderSubDetails exception", ex.getMessage(), settingsUtil.getMobile());
            logEventInGAnalytics("addOrderSubDetails exception itemdesp", itemdesp, settingsUtil.getMobile());
            ex.printStackTrace();
        }


        try {
            TMCUserAddress userAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
            // Log.d(TAG, "useraddress key "+userAddress.getKey());

            JSONObject trackingjson = new JSONObject();
            String orderstatus = "";
            trackingjson.put("orderid", orderdetails.getOrderid());
            trackingjson.put("orderplacedtime", orderdetails.getOrderplacedtime());
            if (orderdetails.getSlotname().equalsIgnoreCase(Orderdetails.SLOTNAME_PREORDER)) {
                orderstatus = Orderdetails.ORDERSTATUS_CONFIRMED;
                trackingjson.put("orderconfirmedtime", orderdetails.getOrderplacedtime());
            } else {
                orderstatus = Orderdetails.ORDERSTATUS_NEW;
            }
            // orderstatus = Orderdetails.ORDERSTATUS_NEW;

            currentorderstatus = orderstatus;
            fillItemAndOrderDetailsInUI();
            changeStatusLayout(orderstatus);
            trackingjson.put("orderstatus", orderstatus);
            trackingjson.put("vendorkey", orderdetails.getVendorkey());
            trackingjson.put("useraddresskey", userAddress.getKey());
            trackingjson.put("useraddresslat", userAddress.getLocationlat());
            trackingjson.put("useraddresslong", userAddress.getLocationlong());
            trackingjson.put("usermobileno", "+" + settingsUtil.getMobile());
            trackingjson.put("deliverydistanceinkm", userAddress.getDeliverydistance());
            trackingjson.put("slotdate", orderdetails.getSlotdatenew());
            addCustOrderTrackingDetails(trackingjson);
            addVendorOrderTrackingDetails(trackingjson);
            addOrdertrackingdetailsToSqlite(trackingjson);
            TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();

            if (isTransactionInProgress) {
                startschedulerthreadforpayonlineactivity = true;
                hidePaymentLoadingAnim();
                isTransactionInProgress = false;
                showPayOnlineLayout(300, true);
            }

        } catch (Exception ex) {
            logEventInGAnalytics("addOrderTrackingDetails exception", ex.getMessage(), settingsUtil.getMobile());
            ex.printStackTrace();
        }
    }

    private void addCouponTransToAWS() {
        double couponamount = orderdetails.getCoupondiscount();
        if ((couponamount <= 0) || (orderdetails.getCouponkey() == null)) {
            return;
        }
        Coupontransactions coupontransactions = new Coupontransactions();
        coupontransactions.setCoupondiscountamount(couponamount);
        coupontransactions.setCouponkey(orderdetails.getCouponkey());
        coupontransactions.setMobileno("+" + orderdetails.getUsermobile());
        coupontransactions.setOrderid(orderdetails.getOrderid());
        coupontransactions.setTransactiondate(orderdetails.getOrderplaceddate());
        coupontransactions.setTransactiontime(orderdetails.getOrderplacedtime());
        coupontransactions.setUserkey(orderdetails.getUserkey());
        coupontransactions.setVendorkey(orderdetails.getVendorkey());
        coupontransactions.setCoupontype(orderdetails.getCoupontype());
        JSONObject jsonObject = coupontransactions.getJSONObjectForInsert();
        if (jsonObject == null) {
            return;
        }
        String url = TMCRestClient.AWS_ADDCOUPONTRANSACTIONS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                    // Log.d(TAG, "addCouponTransactions jsonobject " + response);
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
     // Volley.newRequestQueue(this).add(jsonObjectRequest);
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void addOrderSubDetails(JSONObject orderItemJsonObj) {
        String url = TMCRestClient.AWS_ADDORDERSUBDETAILS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                orderItemJsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    // Log.d(TAG, "addOrderSubDetails jsonobject " + response);
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
                Log.d(TAG, "onErrorResponse " + error.getMessage());
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(29000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void addCustOrderTrackingDetails(JSONObject orderTrackingJsonObj) {
        String custordertrackdetailsurl = TMCRestClient.AWS_ADDCUSTORDTRACKDETAILS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, custordertrackdetailsurl,
                orderTrackingJsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "addCustOrderTrackingDetails jsonobject " + response);
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(29000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void addVendorOrderTrackingDetails(JSONObject orderTrackingJsonObj) {
        String custordertrackdetailsurl = TMCRestClient.AWS_ADDVENDORDTRACKDETAILS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, custordertrackdetailsurl,
                orderTrackingJsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                 // Log.d(TAG, "addVendorOrderTrackingDetails jsonobject " + response);
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(29000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    private void sendOrderConfirmationNotification(String orderid, double payableamount) {
        try {
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                    PendingIntent.FLAG_ONE_SHOT);
            String channelId  = "Notification";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            //String imageUrl = remoteMessage.getNotification().getImageUrl().toString();
            // Bitmap bitmap = getBitmapfromUrl(imageUrl);

            String title = "Congrats! Order placed successfully! ";
            String message = "You may complete the payment or pay 'Cash on Delivery'. " + "Your Bill Value is Rs. " + payableamount + ". " ;
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)

                            .setSmallIcon(R.mipmap.ic_tmc_notification)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSound(defaultSoundUri)
                            .setColor(ContextCompat.getColor(this, R.color.meatchopbg_red))

                            .setPriority(2)
                            //.setSubText("This is a Subtext 123456789012345678901234567890")
                            /*  .setStyle(new NotificationCompat.BigPictureStyle()
                                      .bigPicture(bitmap)
                                      .bigLargeIcon(BitmapFactory.decodeResource(getResources(),
                                              R.drawable.tmcicon_launcher))
                              )


                             */
                            .setContentIntent(pendingIntent);





            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //special notification connfiguration for the oreo device
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0, notificationBuilder.build());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendOrderConfirmationSMS(String customermobile, String orderid, double payableamount) {
        customermobile = customermobile.replaceAll("91" , "");
        // String message = "Your order placed successfully. Your Order-id is " +
        //         orderid+", BILL VALUE: Rs."+payableamount;
        String message = "Congrats! Your order is placed successfully! Bill Value: Rs. " + payableamount +
                ". You may complete the payment or pay 'Cash on Delivery'" + "\n" + " - THE MEAT CHOP";
        String url = TMCRestClient.SMS_URL + "&to=" + customermobile + "&message=" +message;
        Log.d(TAG, "sendOrderConfirmationSMS url "+url);
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                    Log.d(TAG, "sendOrderConfirmationSMS jsonobject " + response);
                    // String message = response.getString("message");
                 /* if (message.equalsIgnoreCase("success")) {

                    } */

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

     /* TMCRestClient.sendSolutionsInfiniSms(customermobile, message1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                Log.d("PaymentModeActivity", "sendSolutionsInfiniSms jsonObject " + jsonObject); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d("PaymentModeActivity", "sendSolutionsInfiniSms Record push failed0"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d("PaymentModeActivity", "sendSolutionsInfiniSms Record push failed1"); //No i18n
            }
        }); */
    }

    private void sendOrderConfirmationMail() {
        // String mobileno = orderdetails.getUsermobile().replaceAll("91", "");
        String mobileno = orderdetails.getUsermobile().substring(2);
        String slotdatetime = "";
        String slotname = orderdetails.getSlotname();
        if (slotname.equalsIgnoreCase(Orderdetails.SLOTNAME_EXPRESSDELIVERY)) {

        } else {
            slotdatetime = orderdetails.getSlotdate() + " " + orderdetails.getSlottimerange();
        }
        String itemdesp = orderdetails.getConsolidatedItemDespWithQty();
        TMCRestClient.sendMail(orderdetails.getOrderid(), mobileno, orderdetails.getOrderplacedtime(),
                slotname, slotdatetime, itemdesp, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                        super.onSuccess(statusCode, headers, jsonObject);
                        try {
                            Log.d(TAG, "Record Pushed jsonObject " + jsonObject); //No I18N
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                        super.onFailure(statusCode, headers, throwable, jsonObject);
                        Log.d(TAG, "Record push failed"); //No i18n
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                        super.onFailure(statusCode, headers, s, throwable);
                        Log.d(TAG, "Record push failed"); //No i18n
                    }
                });
    }

 /* private String generateOrderid() {
        return "" + System.nanoTime();
    } */

    private Tracker tmcTracker;
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

    private void showOrderstatusLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                orderstatusloadinganim_layout.setVisibility(View.VISIBLE);
                orderstatus_layout.setVisibility(View.GONE);
            }
        });
    }

    private void hideOrderstatusLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                orderstatus_layout.setVisibility(View.VISIBLE);
                orderstatusloadinganim_layout.setVisibility(View.GONE);
            }
        });
    }

    private void showOrderPlacedLoadingPage() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                orderplacedloading_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideOrderPlacedLoadingPage() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                orderplacedloading_layout.setVisibility(View.GONE);
            }
        });
    }

    private void showPaymentLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                paymentpageloadinganim_layout.setVisibility(View.VISIBLE);
            }
        });
    }


    private void hidePaymentLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                paymentpageloadinganim_layout.setVisibility(View.GONE);
            }
        });
    }

    /* private void getRazorpayOrderstatus(String paymentmode, String merchantorderid) {
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
                                       showTMCAlert(R.string.onlinepaymentsuccessalert_title, R.string.lastpaymentsuccessalert_desp);
                                       changePaymentStatusInUI("RAZORPAY");
                                       updatePaymentTransactionInAws("SUCCESS", "");
                                       updatePaymentModeInOrderdetails(paymentmode, merchantorderid, settingsUtil.getLastOrderdetailsKey());
                                   } else {
                                       changePaymentStatusInUI("CASH ON DELIVERY");
                                       updatePaymentTransactionInAws( "FAILED", "");
                                       showTMCAlert(R.string.lasttransactionfailedalert_title, R.string.lasttransactionfailedalert_desp);
                                   }
                                   clearLastTransactionDetailsFromSharedPref();
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
                           changePaymentStatusInUI("CASH ON DELIVERY");
                           showTMCAlert(R.string.lasttransactionfailedalert_title, R.string.lasttransactionfailedalert_desp);
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

       private void getPaytmOrderStatus(String paymentmode, String merchantorderid) {
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
                                       showTMCAlert(R.string.onlinepaymentsuccessalert_title, R.string.lastpaymentsuccessalert_desp);
                                       changePaymentStatusInUI("PAYTM");
                                       updatePaymentModeInOrderdetails(paymentmode, merchantorderid, settingsUtil.getLastOrderdetailsKey());
                                       updatePaymentTransactionInAws("SUCCESS", merchantpaymentid);
                                   } else {
                                       changePaymentStatusInUI("CASH ON DELIVERY");
                                       updatePaymentTransactionInAws("FAILED", "");
                                       showTMCAlert(R.string.lasttransactionfailedalert_title, R.string.lasttransactionfailedalert_desp);
                                   }
                                   clearLastTransactionDetailsFromSharedPref();
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
                           changePaymentStatusInUI("CASH ON DELIVERY");
                           showTMCAlert(R.string.lasttransactionfailedalert_title, R.string.lasttransactionfailedalert_desp);
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
   */
    private void getPaymentTransactionAndUpdatePaymentType(String merchantorderid) {
        String url = TMCRestClient.AWS_GETPAYMENTTRANSACTION + "?merchantorderid=" + merchantorderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "getOrderTrackingDetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            Log.d(TAG, "getPaymentTransactionAndUpdatePaymentType message "+message);
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String paymenttype = jsonObject1.getString("paymenttype");
                                    if ((paymenttype != null) && !(TextUtils.isEmpty(paymenttype))) {
                                        onlinemodepaymenttype_desp.setText("Mode of Payment: " + paymenttype);
                                        onlinemodepaymenttype_desp.setVisibility(View.VISIBLE);
                                    }
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

    private void updatePaymentTransactionInAws(String status, String merchantpaymentid) {
        String paymentmodeorderdetailsaction = merchantpaymentid + "-" +
                settingsUtil.getLastPaymentTransKey() + ":" + TMCUtil.getInstance().getCurrentTime();
        logEventInGAnalytics("updatePaymentTransactionInAws", paymentmodeorderdetailsaction, settingsUtil.getMobile());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", settingsUtil.getLastPaymentTransKey());
            jsonObject.put("status", status);
            jsonObject.put("merchantpaymentid", merchantpaymentid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(9000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Volley.newRequestQueue(this).add(jsonObjectRequest);
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

 /* private void getOrderDetailsKeyAndUpdatePaymentMode(String paymentmode, String merchantorderid, String orderid) {
        String url = TMCRestClient.AWS_GETORDERDETAILSFROMORDERID + "?orderid=" + orderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            // Log.d(TAG, "getOrderDetailsForOrderid response "+response);
                            boolean isOrderInDB = false;
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    orderdetailskey = jsonObject1.getString("key");
                                    updatePaymentModeInOrderdetails(paymentmode, merchantorderid, orderdetailskey);
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

    private void updatePaymentModeInOrderdetails(String paymentmode, String merchantorderid, String orderdetailskey) {
        String paymentmodeorderdetailsaction = paymentmode + "-" + orderdetailskey + ":" + TMCUtil.getInstance().getCurrentTime();
        logEventInGAnalytics("updatePaymentModeInOrderdetails", paymentmodeorderdetailsaction, settingsUtil.getMobile());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", orderdetailskey);
            jsonObject.put("merchantorderid", merchantorderid);
            jsonObject.put("paymentmode", paymentmode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(9000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        // Volley.newRequestQueue(this).add(jsonObjectRequest);

        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
*/
    private void showOrderCancelledAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(Neworderactivity.this, title, message,
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

    private void showConfirmationAlertForOrderCancellation(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(Neworderactivity.this, title, message,
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
                new TMCAlert(Neworderactivity.this, title, message,
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
                new TMCAlert(Neworderactivity.this, title, message,
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

    private void clearLastTransactionDetailsFromSharedPref() {
        int delaytime = 3000;
        Handler schedulerThread = new Handler();

        schedulerThread.postDelayed(new Runnable() {
            public void run() {
                settingsUtil.setLastTransactionDetails("");
            }
        }, delaytime);
    }

    private void addOrderdetailsToSqlite(Orderdetails orderdetails) {
        try {
            Orderdetailslocal orderdetailslocal = new Orderdetailslocal(new JSONObject(orderdetails.getJSONString()));
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            orderdetailslocal.save(helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addOrdertrackingdetailsToSqlite(JSONObject ordertrackingjson) {
        try {
            Ordertrackingdetailslocal ordertrackingdetailslocal = new Ordertrackingdetailslocal(ordertrackingjson);
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            ordertrackingdetailslocal.save(helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateServerKeyInOrderDetailsLocal(String orderid, String serverkey) {
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        Orderdetailslocal orderdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                getOrderdetailsFromSqlite(helper, orderid);
        orderdetailslocal.setServerkey(serverkey);
        orderdetailslocal.update(helper);
    }

    private void getOrderTrackingDetailsAndUpdateOrderStatus(String orderid, String orderstatus) {
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETORDERTRACKINGDETAILS + "?orderid=" + orderid;
     // Log.d(TAG, "getOrderTrackingDetailsAndUpdateOrderStatus url "+url);
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
        String action = ordertrackingdetailskey+"-" + TMCUtil.getInstance().getCurrentTime();
        logEventInGAnalytics("ConfirmationActivity Before Cancel Order", action, settingsUtil.getMobile());
        Log.d(TAG, "updateStatusInOrderTrackingDetailsAWS jsonobject "+jsonObject);
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
                        updateOrderStatusInOrderTrackingDetailsLocal(orderid, orderstatus);
                        showOrderCancelledAlert(R.string.ordercancelledsuccessalert_title,
                                R.string.ordercancelledsuccessalert_desp);
                        String action1 = ordertrackingdetailskey+"-" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("Order Cancelled Successfully", action1 , settingsUtil.getMobile());
                        changeStatusLayout("CANCELLED");
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

    private void updateStockDetailsInMenuItems(String menustockdetails) {
        try {
            if (menustockdetails == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject(menustockdetails);
            JSONArray menustockdetailsArray = jsonObject.getJSONArray("menustockdetails");
            for (int i=0; i<menustockdetailsArray.length(); i++) {
                JSONObject menustockitems = menustockdetailsArray.getJSONObject(i);
                getStockOutgoingdetailsAndUpdateInInventory(menustockitems);
                if ((i+1) == menustockdetailsArray.length()) {
                    // Dont do anything
                } else {
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            handler.postDelayed(this, 500);
                        }
                    };
                    handler.post(runnable);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //  Get sales/wastage details from stockoutgoingtable --> Update stockbalance in stockavldetails --> Add outgoingsalesdetails entry
//  Add an entry in stockoutgoing details
    private void getStockOutgoingdetailsAndUpdateInInventory(JSONObject menustockitems) {
        try {
            String stockincomingkey = menustockitems.getString("stockincomingkey");
            double receivedstock = menustockitems.getDouble("receivedstock");
            double orderedqty = menustockitems.getDouble("orderedqty");
            String url = TMCRestClient.AWS_GETSTOCKOUTGOINGDETAILS + "?stockincomingkey=" + stockincomingkey;
            Log.d(TAG, "getStockOutgoingdetailsAndUpdateInInventory  url "+url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(@NonNull JSONObject response) {
                            // Display the first 500 characters of the response string.
                            try {
                                String message = response.getString("message");
                                Log.d(TAG, "getStockOutgoingdetailsAndUpdateInInventory  message "+message);
                                if (message.equalsIgnoreCase("success")) {
                                    double newstockbalance = 0; double oldstockbalance = 0;
                                    JSONArray jsonArray = response.getJSONArray("content");
                                    if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                        double totaloutgoingqty = 0;
                                        for (int i=0; i<jsonArray.length(); i++) {
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            String salesorderid = jsonObject1.getString("salesorderid");
                                            if (salesorderid.equalsIgnoreCase(orderdetails.getOrderid())) {
                                                continue;
                                            }
                                            double outgoingqty = 0;
                                            try {
                                                String outgoingqtystr = jsonObject1.getString("outgoingqty");
                                                outgoingqty = Double.parseDouble(outgoingqtystr);
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                continue;
                                            }
                                            String outgoingtype = jsonObject1.getString("outgoingtype");
                                            if (outgoingtype.equalsIgnoreCase("SALES_CANCELLED")) {
                                                continue;
                                            }
                                            if (outgoingtype.equalsIgnoreCase("SUPPLYGAP")) {
                                                totaloutgoingqty = totaloutgoingqty - outgoingqty;
                                            } else {
                                                totaloutgoingqty = totaloutgoingqty + outgoingqty;
                                            }

                                        }
                                        oldstockbalance = receivedstock - totaloutgoingqty;
                                        newstockbalance =  oldstockbalance - orderedqty;
                                    } else {
                                        oldstockbalance = receivedstock;
                                        newstockbalance =  oldstockbalance - orderedqty;
                                    }
                                    Log.d(TAG, "newstockbalance "+newstockbalance+" oldstockbalance "+oldstockbalance);
                                    updateStockBalanceInMenuItem(menustockitems, newstockbalance, oldstockbalance);
                                    addStockOutgoingDetailsToAWS(menustockitems);
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
         // Volley.newRequestQueue(this).add(jsonObjectRequest);

            RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
                                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(mRetryPolicy);
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateStockBalanceInMenuItem(JSONObject menustockitems, double newstockbalance, double oldstockbalance) {
        JSONObject jsonObject = new JSONObject();
        try {
            String stockincomingkey = menustockitems.getString("stockincomingkey");
            String tmcmenuavlkey = menustockitems.getString("tmcmenuavlkey");
            boolean allownegativestock = menustockitems.getBoolean("allownegativestock");

            jsonObject.put("key", tmcmenuavlkey);
            jsonObject.put("stockincomingkey", stockincomingkey);
            jsonObject.put("stockbalance", newstockbalance);
            jsonObject.put("lastupdatedtime", TMCUtil.getInstance().getCurrentTime());
            if (newstockbalance <= 0) {
                if (!allownegativestock) {
                    jsonObject.put("itemavailability", false);
                }
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_UPDATESTOCKBALANCE,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String message = response.getString("message");
                        if (message.equalsIgnoreCase("success")) {
                            String action1 = "menuitemavlkey " + tmcmenuavlkey +"-" + TMCUtil.getInstance().getCurrentTime();
                            logEventInGAnalytics("Stock balance updated", action1 , settingsUtil.getMobile());
                            Log.d(TAG, "Stockbalance updated ");
                            addStockBalanceTransactionHistory(menustockitems, newstockbalance, oldstockbalance);
                            if (jsonObject.has("itemavailability")) {
                                String itemavlstr = jsonObject.getString("itemavailability");
                                boolean itemavl = Boolean.parseBoolean(itemavlstr);
                                if (!itemavl) {
                                    addMenuAvlTransactions(menustockitems);
                                }
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addMenuAvlTransactions(JSONObject menustockitems) {
        try {
            String tmcmenuavlkey = menustockitems.getString("tmcmenuavlkey");
            String itemname = menustockitems.getString("itemname");
            String subctgykey = menustockitems.getString("tmcsubctgykey");
            String menuitemkey = menustockitems.getString("menuitemkey");
            boolean allownegativestock = menustockitems.getBoolean("allownegativestock");

            JSONObject menuavltrans = new JSONObject();
            menuavltrans.put("itemname", itemname);
            menuavltrans.put("tmcsubctgykey", subctgykey);
            menuavltrans.put("transactiontime", TMCUtil.getInstance().getCurrentTime());
            menuavltrans.put("mobileno", settingsUtil.getMobile());
            menuavltrans.put("vendorkey", orderdetails.getVendorkey());
            menuavltrans.put("menuitemkey", menuitemkey);
            menuavltrans.put("menuitemstockavldetailskey", tmcmenuavlkey);
            menuavltrans.put("allownegativestock", allownegativestock);
            menuavltrans.put("status", false);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_ADDMENUAVLTRANSACTIONS,
                    menuavltrans, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        //  Log.d(TAG, "addPaymentTrans jsonobject " + response);
                        String message = response.getString("message");
                        if (message.equalsIgnoreCase("success")) {
                            JSONObject jsonObject1 = response.getJSONObject("content");
                            String key = jsonObject1.getString("key");
                            String action1 = "MenuAvlTransaction key " + key +"-" + TMCUtil.getInstance().getCurrentTime();
                            logEventInGAnalytics("MenuAvlTransaction entry added", action1 , settingsUtil.getMobile());
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
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void addStockOutgoingDetailsToAWS(JSONObject menustockdetails) {
        try {
            String stockincomingkey = menustockdetails.getString("stockincomingkey");
            String tmcctgykey = menustockdetails.getString("tmcctgykey");
            String tmcsubctgykey = menustockdetails.getString("tmcsubctgykey");
            String itemname = menustockdetails.getString("itemname");
            String barcode = menustockdetails.getString("barcode");
            String stocktype = menustockdetails.getString("stocktype");
            double orderedqty = menustockdetails.getDouble("orderedqty");

            JSONObject newmenustockitems = new JSONObject();
            newmenustockitems.put("stockincomingkey", stockincomingkey);
            newmenustockitems.put("tmcctgykey", tmcctgykey);
            newmenustockitems.put("tmcsubctgykey", tmcsubctgykey);
            newmenustockitems.put("itemname", itemname);
            newmenustockitems.put("barcode", barcode);
            newmenustockitems.put("stocktype", stocktype);
            newmenustockitems.put("vendorkey", orderdetails.getVendorkey());
            newmenustockitems.put("userkey", orderdetails.getUserkey());
            newmenustockitems.put("outgoingtype", "SALES_ALLOCATED");
            newmenustockitems.put("salesorderid", orderdetails.getOrderid());
            newmenustockitems.put("inventoryusermobileno", settingsUtil.getMobile());
            newmenustockitems.put("outgoingdate", TMCUtil.getInstance().getCurrentTime());
            newmenustockitems.put("outgoingqty", orderedqty);
            Log.d(TAG, "newmenustockitems "+newmenustockitems);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_ADDSTOCKOUTGOINGDETAILS,
                    newmenustockitems, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        //  Log.d(TAG, "addPaymentTrans jsonobject " + response);
                        String message = response.getString("message");
                        if (message.equalsIgnoreCase("success")) {
                            JSONObject jsonObject1 = response.getJSONObject("content");
                            String key = jsonObject1.getString("key");
                            String action1 = "stockoutgoing key " + key +"-" + TMCUtil.getInstance().getCurrentTime();
                            logEventInGAnalytics("Outgoing entry added", action1 , settingsUtil.getMobile());
                            Log.d(TAG, "Stockoutgoing details added key "+key);
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
            RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(mRetryPolicy);
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private void addStockBalanceTransactionHistory(JSONObject menustockdetails, double newstockbalance, double oldstockbalance) {
        try {
            String barcode = menustockdetails.getString("barcode");
            String itemname = menustockdetails.getString("itemname");
            String menuitemkey = menustockdetails.getString("menuitemkey");
            String stockincomingkey = menustockdetails.getString("stockincomingkey");

            JSONObject stockbaltranshistoryjson = new JSONObject();
            stockbaltranshistoryjson.put("barcode", barcode);
            stockbaltranshistoryjson.put("itemname", itemname);
            stockbaltranshistoryjson.put("menuitemkey", menuitemkey);
            stockbaltranshistoryjson.put("stockincomingkey", stockincomingkey);
            stockbaltranshistoryjson.put("newstockbalance", newstockbalance);
            stockbaltranshistoryjson.put("oldstockbalance", oldstockbalance);
            stockbaltranshistoryjson.put("usermobileno", settingsUtil.getMobile());
            stockbaltranshistoryjson.put("transactiontime", TMCUtil.getInstance().getCurrentTime());
            stockbaltranshistoryjson.put("vendorkey", orderdetails.getVendorkey());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TMCRestClient.AWS_STOCKBALTRANSACTIONHISTORY,
                    stockbaltranshistoryjson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        //  Log.d(TAG, "addPaymentTrans jsonobject " + response);
                        String message = response.getString("message");
                        if (message.equalsIgnoreCase("success")) {
                            JSONObject jsonObject1 = response.getJSONObject("content");
                            String key = jsonObject1.getString("key");
                            String action1 = "stockoutgoing key " + key +"-" + TMCUtil.getInstance().getCurrentTime();
                            logEventInGAnalytics("Outgoing entry added", action1 , settingsUtil.getMobile());
                            Log.d(TAG, "Stockbalance transaction history added key "+key);

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
            RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, 1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(mRetryPolicy);
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean optiononerazorpaybtnclicked = false;
    private boolean optionpaytmbtnclicked = false;
    private boolean optioncodbtnclicked = false;
    private void initiatePayOnlineViews() {
        payonlinemask_layout = findViewById(R.id.payonlinemask_layout);
        payonline_layout = findViewById(R.id.payonline_layout);
        orderconfirmed_layout = findViewById(R.id.orderconfirmed_layout);
        cashondelivery_layout = findViewById(R.id.cashondelivery_layout);
        option2_razorpaylayout = findViewById(R.id.option2_razorpaylayout);
        option3_paytmlayout = findViewById(R.id.option3_paytmlayout);
        razorpaydesp_textview = (TMCTextView) findViewById(R.id.razorpaydesp_textview);
        paytmdesp_textview = (TMCTextView) findViewById(R.id.paytmdesp_textview);

        payonlinemask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePayOnlineLayout();
                if ((paymentEngine != null) && !(paymentEngine.isCODPaymentTransactionUpdated())) {
                    paymentmode = "CASH ON DELIVERY";
                    paymentEngine.addPaymentTransactionInAWS(paymentmode, orderdetails.getPayableamount(), "Success");
                }
            }
        });

        payonline_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cashondelivery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentLoadingAnim();
                if (optioncodbtnclicked) {
                    return;
                }
                optioncodbtnclicked = true;
                paymentmode = "CASH ON DELIVERY";
                String paymenttransactionstatus = "Success";
                paymentEngine.addPaymentTransactionInAWS(paymentmode, orderdetails.getPayableamount(), paymenttransactionstatus);
                hidePayOnlineLayout();
                changePaymentStatusInUI(paymentmode, "");
                hidePaymentLoadingAnim();
            }
        });

        option2_razorpaylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optiononerazorpaybtnclicked) {
                    return;
                }
                isTransactionInProgress = true;
                optiononerazorpaybtnclicked = true;
                hidePayOnlineLayout();
                showPaymentLoadingAnim();
                paymentEngine.generateOrderIdForRazorpay(orderdetails.getPayableamount());
            }
        });

        option3_paytmlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionpaytmbtnclicked) {
                    return;
                }
                optionpaytmbtnclicked = true;
                isTransactionInProgress = true;
                hidePayOnlineLayout();
                showPaymentLoadingAnim();
                paymentmode = "PAYTM";
                merchantorderid = paymentEngine.generatePaytmOrderId(orderdetails.getPayableamount());
                Intent intent = new Intent(Neworderactivity.this, PaytmActivity.class);
                intent.putExtra("transamount", String.format("%.2f", orderdetails.getPayableamount()));
                intent.putExtra("mobileno", settingsUtil.getMobile());
                intent.putExtra("paytmorderid", merchantorderid);
                startPaytmActivity(intent);
            }
        });
    }

    private void showPayOnlineLayout(int delaytime, boolean codoption) {
        if (codoption) {
            cashondelivery_layout.setVisibility(View.VISIBLE);
            razorpaydesp_textview.setText("Option 2: Razorpay Gateway");
            paytmdesp_textview.setText("Option 3: Paytm Gateway");
        } else {
            cashondelivery_layout.setVisibility(View.GONE);
            razorpaydesp_textview.setText("Option 1: Razorpay Gateway");
            paytmdesp_textview.setText("Option 2: Paytm Gateway");
        }
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                payonlinemask_layout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.loginbtn_anim_bottomup);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(delaytime);
        payonline_layout.startAnimation(bottomUp);
        payonline_layout.setVisibility(View.VISIBLE);
    }

    private void hidePayOnlineLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                payonline_layout.setVisibility(View.GONE);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(200);
        payonline_layout.startAnimation(bottomDown);
        payonlinemask_layout.setVisibility(View.GONE);
    }

    private String merchantorderid;
    private Handler createHandlerForPaymentEngine() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("startRazorpayActivity")) {
                    isTransactionInProgress = true;
                    merchantorderid = bundle.getString("merchantorderid");
                    double billvalue = bundle.getDouble("billvalue");
                    Intent intent = new Intent(Neworderactivity.this, RazorpayActivity.class);
                    intent.putExtra("merchantorderid", merchantorderid);
                    intent.putExtra("usermobile", settingsUtil.getMobile());
                    intent.putExtra("payableamount", billvalue);
                    startRazorpayActivity(intent);
                } else if (menutype.equalsIgnoreCase("changePaymentStatusInUI")) {
                    String trxnstatus = bundle.getString("trxnstatus");
                    if (trxnstatus.equalsIgnoreCase("success")) {
                        paymentEngine.updatePaymentModeInOrderDetailsLocal(orderid, merchantorderid, paymentmode, "",
                                getDatabaseHelper());
                        showTMCAlert(R.string.onlinepaymentsuccessalert_title, R.string.onlinepaymentsuccessalert_desp);
                        changePaymentStatusInUI(paymentmode, "");
                    } else {
                        showTMCAlert(R.string.paymentfailedalert_title, R.string.paymentfailed_alert);
                        changePaymentStatusInUI("", "");
                    }
                    isTransactionInProgress = false;
                    hidePaymentLoadingAnim();
                } else if (menutype.equalsIgnoreCase("logEventInGanalytics")) {
                    String category = bundle.getString("category");
                    String action = bundle.getString("action");
                    String label = bundle.getString("label");
                    logEventInGAnalytics(category, action, label);
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private void startRazorpayActivity(Intent intent) {
        startActivityForResult(intent, RAZORPAY_ACT_REQCODE);
        overridePendingTransition(0, 0);
    }

    private void startPaytmActivity(Intent intent) {
        startActivityForResult(intent, PAYTM_ACT_REQCODE);
        overridePendingTransition(0, 0);
    }

    private DatabaseHelper getDatabaseHelper() {
        DatabaseHelper helper = getHelper();
        if (helper == null) {
            helper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
        }
        return helper;
    }


 /* private void getOrderDetails(String orderid) {
        showLoadingAnim();
        String url = TMCRestClient.AWS_GETORDERDETAILSFROMORDERID + "?orderid=" + orderid;
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
                            fillItemAndOrderDetailsInUI();
                            getPaymentTransactionAndUpdatePaymentType(orderdetails.getMerchantorderid());
                            hideLoadingAnim();
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
        showOrderstatusLoadingAnim();
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
                                Ordertrackingdetails ordertrackingdetails = null;
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject ordertrackingjsonobj = jsonArray.getJSONObject(i);
                                    ordertrackingdetails = new Ordertrackingdetails(ordertrackingjsonobj);
                                }
                                if (ordertrackingdetails != null) {
                                    changeStatusLayout(ordertrackingdetails.getOrderstatus());
                                }
                                currentorderstatus = ordertrackingdetails.getOrderstatus();
                                deliverypartnermobileno = ordertrackingdetails.getDeliveryusermobileno();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        hideOrderstatusLoadingAnim();
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
        // Make the request
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
*/

    private void showOrderNotPlacedAlertAndClearCart() {
        String title = getResources().getString(R.string.servererror_alert);
        String message = getResources().getString(R.string.orderfailure_alert) + " " + settingsUtil.getSupportMobileNo();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(Neworderactivity.this, title, message,
                        getResources().getString(R.string.ok_capt), "",
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


}
