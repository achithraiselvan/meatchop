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
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.TMCDetailOrderAdapter;
import com.meatchop.data.Coupontransactions;
import com.meatchop.data.Deliveryslots;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.Ordertrackingdetails;
import com.meatchop.data.Paymenttransaction;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.helpers.DatabaseManager;
import com.meatchop.sqlitedata.Orderdetailslocal;
import com.meatchop.sqlitedata.Ordertrackingdetailslocal;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.widget.PaymentEngine;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class DetailOrderActivity extends BaseActivity {

    private static final int PAYONLINE_ACT_REQ_CODE = 0;
    private static final int RAZORPAY_ACT_REQCODE = 1;
    private static final int PAYTM_ACT_REQCODE = 2;

    private String TAG = "DetailOrderActivity";
    private Orderdetailslocal orderdetails;
    private String slotdesp;
    private Paymenttransaction paymenttransaction;
    private SettingsUtil settingsUtil;
    private String paymentmode;

    private String orderdetailskey;
    private String orderid;

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
    private Ordertrackingdetailslocal ordertrackingdetailslocal;
    private boolean ordertrackingdetailschanged = false;

    private View itemdiscount_layout;
    private TMCTextView itemdiscountvalue_textview;
    private double payableamount;
    private String orderdetailsserverkey;

    private View cancelorder_layout;
    private View cancelorder_btn;
    private TMCTextView ordercanceldetails_desp;

    private View coupondiscount_layout;
    private TMCTextView coupondiscountvalue_textview;

    // Payonline layout views
    private View payonlinemask_layout;
    private View payonline_layout;
    private View option2_razorpaylayout;
    private View option3_paytmlayout;
    private TMCTextView razorpaydesp_textview;
    private TMCTextView paytmdesp_textview;
    private PaymentEngine paymentEngine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detailorder_layout);

        settingsUtil = new SettingsUtil(this);

        if (getPackageName().equals("com.meatchop")) {
         // orderdetails = getIntent().getParcelableExtra("orderdetails");
            slotdesp = getIntent().getStringExtra("slotdesp");
            orderid = getIntent().getStringExtra("orderid");
            ischeckforprevioustransaction = getIntent().getBooleanExtra("ischeckforprevioustransaction", false);
        }

        paymentEngine = new PaymentEngine(orderid, "", this, createHandlerForPaymentEngine());
        orderdetails = getOrderdetailsLocal(orderid);


        try {
            TMCVendor defaultVendor = new TMCVendor(new JSONObject(settingsUtil.getDefaultVendor()));
            storemobileno = defaultVendor.getVendormobile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        supportmobileno = settingsUtil.getSupportMobileNo();

        cancelorder_layout = findViewById(R.id.cancelorder_layout);
        cancelorder_btn = findViewById(R.id.cancelorder_btn);
        ordercanceldetails_desp = (TMCTextView) findViewById(R.id.ordercanceldetails_desp);

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

        initiatePayOnlineViews();

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

        itemdiscount_layout = findViewById(R.id.itemdiscount_layout);
        itemdiscountvalue_textview = findViewById(R.id.itemdiscountvalue_textview);
        coupondiscount_layout = findViewById(R.id.coupondiscount_layout);
        coupondiscountvalue_textview = (TMCTextView) findViewById(R.id.coupondiscountvalue_textview);

        itemtotal_textview = (TMCTextView) findViewById(R.id.itemtotal_textview);
        deliveryfee_textview = (TMCTextView) findViewById(R.id.deliveryfee_textview);
        taxesandcharges_textview = (TMCTextView) findViewById(R.id.taxesandcharges_textview);
        amountpaid_textview = (TMCTextView) findViewById(R.id.amountpaid_textview);
        tokenno_desp = (TMCTextView) findViewById(R.id.tokenno_desp);
        tokenno_layout = findViewById(R.id.tokenno_layout);

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

        payonline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             // startPayOnlineActivity(false);
                showPayOnlineLayout(300);
            }
        });

        fillItemAndOrderDetailsInUI();

        try {
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            ordertrackingdetailslocal = DatabaseManager.getInstance(getApplicationContext()).
                                            getOrderTrackingdetailsFromSqlite(helper, orderdetails.getOrderid());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (orderdetails != null) {
            if ((ordertrackingdetailslocal != null) &&
                    (ordertrackingdetailslocal.getOrderstatus().equalsIgnoreCase("DELIVERED"))) {
                changeStatusLayout(ordertrackingdetailslocal.getOrderstatus());
            } else {
                getOrderTrackingDetails(orderdetails.getOrderid());
            }
            String paymentmode = orderdetails.getPaymentmode();
            if (paymentmode.equalsIgnoreCase("CASH ON DELIVERY")) {
                getPaymentTransactionAndUpdatePaymentTypeInUI(orderdetails.getOrderid());
            }
         /* if ((orderdetails.getTokenno() == null) || (TextUtils.isEmpty(orderdetails.getTokenno()))) {
                isShowCancelOrderLayout(orderdetails.getOrderplacedtime());
            } else {
                cancelorder_layout.setVisibility(View.GONE);
            } */
        }

        String lasttransactiondetails = settingsUtil.getLastTransactionDetails();
        Log.d(TAG, "lasttransactiondetails "+lasttransactiondetails);
        if ((lasttransactiondetails != null) && (!TextUtils.isEmpty(lasttransactiondetails))) {
            try {
                JSONObject jsonObject = new JSONObject(lasttransactiondetails);
                String merchantorderid = jsonObject.getString("merchantorderid");
                String lastorderid = jsonObject.getString("orderid");
                String paymentmode = jsonObject.getString("paymentmode");
                if ((orderdetails != null) && (lastorderid.equalsIgnoreCase(orderdetails.getOrderid()))) {
                    paymentdetails_layout.setVisibility(View.GONE);
                    onlinemodepayment_layout.setVisibility(View.GONE);
                    paymentstatusloadinganim_layout.setVisibility(View.VISIBLE);
                    if (paymentEngine != null) {
                        paymentEngine.checkPaymentStatusFromMerchantorderid(paymentmode, merchantorderid);
                    }
                 /* if (paymentmode.equalsIgnoreCase("RAZORPAY")) {
                        getRazorpayOrderstatus(paymentmode, merchantorderid);
                    } else if (paymentmode.equalsIgnoreCase("paytm")) {
                        getPaytmOrderStatus(paymentmode, merchantorderid);
                    } */
                } else {

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

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
            hidePayOnlineLayout();
            return;
        }
        Intent intent = new Intent();
        if (ordertrackingdetailslocal != null) {
            intent.putExtra("ordertrackingdetailschanged", ordertrackingdetailschanged);
            intent.putExtra("orderstatus", ordertrackingdetailslocal.getOrderstatus());
        }
        if (orderdetails != null) {
            intent.putExtra("orderid", orderid);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void fillItemAndOrderDetailsInUI() {
        if (orderdetails != null) {
            try {
                payableamount = orderdetails.getPayableamount();
                orderdetailsserverkey = orderdetails.getServerkey();
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
                String paymenttype = orderdetails.getPaymenttype();
                changePaymentStatusInUI(paymentmode, paymenttype);

                if ((orderdetails.getTokenno() != null) && !(TextUtils.isEmpty(orderdetails.getTokenno()))) {
                    tokenno_desp.setText("" + orderdetails.getTokenno());
                    tokenno_layout.setVisibility(View.VISIBLE);
                } else {
                    getTokenNoFromOrderDetailsAndUpdate(orderdetails);
                 // tokenno_layout.setVisibility(View.GONE);
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
            settingsUtil.setLastPlacedOrderMenuItems("");
         // payonline_btn.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("CANCELLED")) {
            outfordeliveryselected_image.setVisibility(View.GONE);
            outfordeliveryunselected_image.setVisibility(View.VISIBLE);
            orderdeliveredselected_image.setVisibility(View.GONE);
            orderdeliveredunselected_image.setVisibility(View.VISIBLE);

            orderreceivedtitle_textview.setText("Order Cancelled");
            orderreceiveddesp_textview.setText("We have cancelled your order");

            outfordeliverytitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            outfordeliverydesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            outfordeliverytitle_textview.setTypeface(opensansregularface);

            orderdeliveredtitle_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdelivereddesp_textview.setTextColor(getResources().getColor(R.color.secondary_text_color));
            orderdeliveredtitle_textview.setTypeface(opensansregularface);
            payonline_btn.setVisibility(View.GONE);
            settingsUtil.setLastPlacedOrderMenuItems("");
        }
    }

 /* private boolean startschedulerthreadforpayonlineactivity = false;
    private void startSchedulerThreadForPayOnlineActivity(boolean showcodoption) {
        int delaytime = 3000;
        Handler schedulerThread = new Handler();

        schedulerThread.postDelayed(new Runnable() {
            public void run() {
                if (startschedulerthreadforpayonlineactivity) {
                    return;
                }
                startschedulerthreadforpayonlineactivity = true;
                startPayOnlineActivity(showcodoption);
                isTransactionInProgress = false;
            }
        }, delaytime);
    } */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PAYONLINE_ACT_REQ_CODE :
                hidePaymentLoadingAnim();
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                     // showLoadingAnim();
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
                            paymentEngine.updatePaymentModeInOrderDetailsLocal(orderid, merchantorderid, paymentmode, "", helper);
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
                            paymentEngine.updatePaymentModeInOrderDetailsLocal(orderid, merchantorderid, paymentmode,
                                                                               paymenttype, getDatabaseHelper());
                            showTMCAlert(R.string.onlinepaymentsuccessalert_title, R.string.onlinepaymentsuccessalert_desp);
                            changePaymentStatusInUI(paymentmode, paymenttype);
                            hidePaymentLoadingAnim();
                            isTransactionInProgress = false;
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

    private void changePaymentStatusInUI(String paymentmode, String paymenttype) {
        paymentstatusloadinganim_layout.setVisibility(View.GONE);
        if (paymentmode.equalsIgnoreCase("RAZORPAY")) {
            payonline_btn.setVisibility(View.GONE);
            paymentdetails_layout.setVisibility(View.GONE);
            onlinemodepayment_layout.setVisibility(View.VISIBLE);
            onlinemodepayment_desp.setText("Paid Online (RAZORPAY)");
            Log.d(TAG, "changePaymentStatusInUI paymenttype "+paymenttype);
            if ((paymenttype != null) && !(TextUtils.isEmpty(paymenttype))) {
                onlinemodepaymenttype_desp.setVisibility(View.VISIBLE);
                onlinemodepaymenttype_desp.setText("Mode of Payment: " + paymenttype);
            } else {
                onlinemodepaymenttype_desp.setVisibility(View.GONE);
            }
        } else if (paymentmode.equalsIgnoreCase("PAYTM")) {
            payonline_btn.setVisibility(View.GONE);
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
            payonline_btn.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(DetailOrderActivity.this, PayOnlineActivity.class);
        intent.putExtra("orderid", orderid);
        if (payableamount <= 0) {
            getOrderdetailsLocal(orderid);
            fillItemAndOrderDetailsInUI();
            return;
        }
        intent.putExtra("billvalue", payableamount);
        intent.putExtra("orderdetailskey", orderdetailsserverkey);
        intent.putExtra("showcodoption", showcodoption);
        startActivityForResult(intent, PAYONLINE_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

 /* private void addPaymentTransactionInAWS() {
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
*/
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

    private void getOrderTrackingDetails(String orderid) {
        if (settingsUtil.isOrderdetailsNewSchema()) {
            getCustOrderTrackingDetails(orderid);
        } else {
            getOrderTrackingDetailsOld(orderid);
        }
    }

    private void getCustOrderTrackingDetails(String orderid) {
        showOrderstatusLoadingAnim();
        String url = TMCRestClient.AWS_GETCUSTORDTRACKDETFROMORDERIDANDMOBILE + "?usermobileno=" + "%2B" + settingsUtil.getMobile()
                                                                                                      + "&orderid=" + orderid;
        Log.d(TAG, "getCustOrderTrackingDetails url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "getOrderTrackingDetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONObject ordertrackingjsonobj = response.getJSONObject("content");
                                if (ordertrackingjsonobj == null) {
                                    return;
                                }
                                Ordertrackingdetails ordertrackingdetails = new Ordertrackingdetails(ordertrackingjsonobj);
                                if (ordertrackingdetails != null) {
                                    changeStatusLayout(ordertrackingdetails.getOrderstatus());
                                    currentorderstatus = ordertrackingdetails.getOrderstatus();
                                    deliverypartnermobileno = ordertrackingdetails.getDeliveryusermobileno();
                                }
                                if (ordertrackingdetailslocal != null) {
                                    if (!(currentorderstatus.equalsIgnoreCase(ordertrackingdetailslocal.getOrderstatus()))) {
                                        ordertrackingdetailschanged = true;
                                        updateOrderTrackingDetailsInSqlite(ordertrackingdetails);
                                    }
                                }

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
                        Log.d(TAG, "Ordertrackingdetails Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Ordertrackingdetails Error: " + error.getMessage());
                        Log.d(TAG, "Ordertrackingdetails Error: " + error.toString());
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

    private void getOrderTrackingDetailsOld(String orderid) {
        showOrderstatusLoadingAnim();
        String url = TMCRestClient.AWS_GETORDERTRACKINGDETAILSWITHINDEX + "?orderid=" + orderid;
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
                                    currentorderstatus = ordertrackingdetails.getOrderstatus();
                                    deliverypartnermobileno = ordertrackingdetails.getDeliveryusermobileno();
                                }
                                if (ordertrackingdetailslocal != null) {
                                    if (!(currentorderstatus.equalsIgnoreCase(ordertrackingdetailslocal.getOrderstatus()))) {
                                        ordertrackingdetailschanged = true;
                                        updateOrderTrackingDetailsInSqlite(ordertrackingdetails);
                                    }
                                }

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
                        Log.d(TAG, "Ordertrackingdetails Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Ordertrackingdetails Error: " + error.getMessage());
                        Log.d(TAG, "Ordertrackingdetails Error: " + error.toString());
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

    private void getPaymentTransactionAndUpdatePaymentTypeInUI(String orderid) {
        paymentdetails_layout.setVisibility(View.GONE);
        payonline_btn.setVisibility(View.GONE);
        paymentstatusloadinganim_layout.setVisibility(View.VISIBLE);
        String url = TMCRestClient.AWS_GETPAYMENTTRANSACTIONFROMORDERIDWITHINDEX + "?orderid=" + orderid;
     // Log.d(TAG, "getPaymentTransactionAndUpdatePaymentTypeInUI url "+url);
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
                                boolean isonlinepaymentsuccess = false;
                                String onlinepaymentmode = "";
                                String onlinepaymenttype = ""; String codpaymenttype = "CASH ON DELIVERY";
                                String onlinemerchantorderid = "";
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String paymenttype = ""; String paymentmode = "";
                                    String paymentstatus = ""; String merchantorderid = "";
                                    if (!jsonObject1.isNull("paymenttype")) {
                                        paymenttype = jsonObject1.getString("paymenttype");
                                    }
                                    if (!jsonObject1.isNull("paymentmode")) {
                                        paymentmode = jsonObject1.getString("paymentmode");
                                    }
                                    if (!jsonObject1.isNull("status")) {
                                        paymentstatus = jsonObject1.getString("status");
                                    }
                                    if (!jsonObject1.isNull("merchantorderid")) {
                                        merchantorderid = jsonObject1.getString("merchantorderid");
                                    }

                                    if (paymentmode.equalsIgnoreCase("RAZORPAY")) {
                                        if (paymentstatus.equalsIgnoreCase("SUCCESS")) {
                                            isonlinepaymentsuccess = true;
                                            onlinepaymentmode = paymentmode; onlinepaymenttype = paymenttype;
                                            onlinemerchantorderid = merchantorderid;
                                        }
                                    } else if (paymentmode.equalsIgnoreCase("PAYTM")) {
                                        if (paymentstatus.equalsIgnoreCase("SUCCESS")) {
                                            isonlinepaymentsuccess = true;
                                            onlinepaymentmode = paymentmode; onlinepaymenttype = paymenttype;
                                            onlinemerchantorderid = merchantorderid;
                                        }
                                    } else {
                                        codpaymenttype = paymenttype;
                                    }
                                }
                                if (isonlinepaymentsuccess) {
                                    changePaymentStatusInUI(onlinepaymentmode, onlinepaymenttype);
                                    updatePaymentModeInOrderDetailsLocal(orderid, onlinemerchantorderid, onlinepaymentmode);
                                } else {
                                    changePaymentStatusInUI("", codpaymenttype);
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
                        Log.d(TAG, "getPaymentTransactionAndUpdatePaymentTypeInUI Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "getPaymentTransactionAndUpdatePaymentTypeInUI Error: " + error.getMessage());
                        Log.d(TAG, "getPaymentTransactionAndUpdatePaymentTypeInUI Error: " + error.toString());

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

 /* private void updatePaymentTransactionInAws(String status, String merchantpaymentid) {
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
*/
    private void getTokenNoFromOrderDetailsAndUpdate(Orderdetailslocal orderdetailslocal) {
        String url = TMCRestClient.AWS_GETORDERDETAILSFROMORDERID + "?orderid=" + orderdetailslocal.getOrderid();
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
                                    String tokenno = jsonObject1.getString("tokenno");
                                    if ((tokenno != null) && !(TextUtils.isEmpty(tokenno))) {
                                        saveTokenNoToOrderDetails(orderdetailslocal, tokenno);
                                        tokenno_desp.setText(tokenno);
                                        tokenno_layout.setVisibility(View.VISIBLE);
                                    } else {
                                        tokenno_layout.setVisibility(View.GONE);
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
                        Log.d(TAG, "getTokenNoFromOrderDetailsAndUpdate Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "getTokenNoFromOrderDetailsAndUpdate Error: " + error.getMessage());
                        Log.d(TAG, "getTokenNoFromOrderDetailsAndUpdate Error: " + error.toString());
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(4000, 1,
                                                         DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void saveTokenNoToOrderDetails(Orderdetailslocal orderdetailslocal, String tokenno) {
        if (orderdetailslocal == null) {
            return;
        }
        try {
            orderdetailslocal.setTokenno(tokenno);
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            orderdetailslocal.update(helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

 /* private void updatePaymentModeInOrderdetails(String paymentmode, String merchantorderid, String orderdetailskey) {
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);

        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
*/
    private void showOrderCancelNotPossibleAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(DetailOrderActivity.this, title, message,
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
                new TMCAlert(DetailOrderActivity.this, title, message,
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

    public void updateOrderTrackingDetailsInSqlite(Ordertrackingdetails ordertrackingdetails) {
        if (ordertrackingdetails == null) {
            return;
        }
        try {
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            ordertrackingdetailslocal.updateJsonObject(new JSONObject(ordertrackingdetails.getJSONString()));
            ordertrackingdetailslocal.update(helper);
        } catch (Exception ex) {
            ex.printStackTrace();
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
            if (paymentEngine != null) {
                paymentEngine.setOrderdetailskey(orderdetailslocal.getServerkey());
                paymentEngine.setOrderid(orderdetailslocal.getOrderid());
                paymentEngine.setVendorkey(orderdetailslocal.getVendorkey());
            }
            return orderdetailslocal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void getOrderTrackingDetailsAndUpdateOrderStatus(String orderid, String orderstatus) {
     // showLoadingAnim();
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
                        Log.d(TAG, "getOrderTrackingDetailsAndUpdateOrderStatus Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "getOrderTrackingDetailsAndUpdateOrderStatus Error: " + error.getMessage());
                        Log.d(TAG, "getOrderTrackingDetailsAndUpdateOrderStatus Error: " + error.toString());

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
     // Log.d(TAG, "updateStatusInOrderTrackingDetailsAWS jsonobject "+jsonObject);
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 1,
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

    private void showOrderCancelledAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(DetailOrderActivity.this, title, message,
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
                new TMCAlert(DetailOrderActivity.this, title, message,
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

    private void startHomeScreenActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    private void updatePaymentModeInOrderDetailsLocal(String orderid, String merchantorderid, String paymentmode) {
        try {
            DatabaseHelper helper = getHelper();
            if (helper == null) {
                helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            }
            if (orderdetails != null) {
                orderdetails = getOrderdetailsLocal(orderid);
            }
            orderdetails.setMerchantorderid(merchantorderid);
            orderdetails.setPaymentmode(paymentmode);
            orderdetails.update(helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Get orderdetails payment mode and merchantorderid from Orderdetails table. If no merchantorderid, dont do anything.
    // if paymentmode is success, fetch paymentmode from payment transactions. Update orderdetailslocal.

    private boolean optiononerazorpaybtnclicked = false;
    private boolean optionpaytmbtnclicked;
    private void initiatePayOnlineViews() {
        payonlinemask_layout = findViewById(R.id.payonlinemask_layout);
        payonline_layout = findViewById(R.id.payonline_layout);
        option2_razorpaylayout = findViewById(R.id.option2_razorpaylayout);
        option3_paytmlayout = findViewById(R.id.option3_paytmlayout);
        razorpaydesp_textview = (TMCTextView) findViewById(R.id.razorpaydesp_textview);
        paytmdesp_textview = (TMCTextView) findViewById(R.id.paytmdesp_textview);

        payonlinemask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePayOnlineLayout();
            }
        });

        View payonline_layout = findViewById(R.id.payonline_layout);
        payonline_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Log.d(TAG, "option3_paytmlayout new1");
                showPaymentLoadingAnim();
                paymentmode = "PAYTM";
                merchantorderid = paymentEngine.generatePaytmOrderId(orderdetails.getPayableamount());
                Intent intent = new Intent(DetailOrderActivity.this, PaytmActivity.class);
                intent.putExtra("transamount", String.format("%.2f", orderdetails.getPayableamount()));
                intent.putExtra("mobileno", settingsUtil.getMobile());
                intent.putExtra("paytmorderid", merchantorderid);
                startPaytmActivity(intent);
            }
        });
    }

    private void showPayOnlineLayout(int delaytime) {
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
                Log.d(TAG, "bundle tostring "+bundle.toString());
                if (menutype.equalsIgnoreCase("startRazorpayActivity")) {
                    merchantorderid = bundle.getString("merchantorderid");
                    double billvalue = bundle.getDouble("billvalue");
                    Intent intent = new Intent(DetailOrderActivity.this, RazorpayActivity.class);
                    intent.putExtra("merchantorderid", merchantorderid);
                    intent.putExtra("usermobile", settingsUtil.getMobile());
                    intent.putExtra("payableamount", billvalue);
                    startRazorpayActivity(intent);
                } else if (menutype.equalsIgnoreCase("changePaymentStatusInUI")) {
                    String trxnstatus = bundle.getString("trxnstatus");
                    hidePaymentLoadingAnim();
                    if (trxnstatus.equalsIgnoreCase("success")) {
                        paymentEngine.updatePaymentModeInOrderDetailsLocal(orderid,
                                                                           merchantorderid, paymentmode, "", getDatabaseHelper());
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

  /*  private void getRazorpayOrderstatus(String paymentmode, String merchantorderid) {
        String url = TMCRestClient.RAZORPAY_GETORDERSTATUS_URL + "?orderid=" + merchantorderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley response "+response.toString());
                        try {
                            if (response != null) {
                                String status = response.getString("status");
                                String statusorderid = merchantorderid + "-" + status + ":" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("getRazorpayOrderstatus", statusorderid, settingsUtil.getMobile());
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
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
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(7000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
*/




}
