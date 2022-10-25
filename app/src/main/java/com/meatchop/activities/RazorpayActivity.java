package com.meatchop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCUtil;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

public class RazorpayActivity extends BaseActivity implements PaymentResultWithDataListener {

    private String merchantorderid;
    private String usermobile;
    private double payableamount;
    private Tracker tmcTracker;

    private final String TAG = "RazorpayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razorpay);

        if (getPackageName().equals("com.meatchop")) {
            merchantorderid = getIntent().getStringExtra("merchantorderid");
            usermobile = getIntent().getStringExtra("usermobile");
            payableamount = getIntent().getDoubleExtra("payableamount", 0);
        }

     // Log.d(TAG, "merchantorderid "+merchantorderid+" usermobile "+usermobile+" payableamount "+payableamount);

        double razorpayamountinpaisa = 0;
        if (payableamount > 0) {
            razorpayamountinpaisa = payableamount * 100;
            int finalbillvalue = (int) razorpayamountinpaisa;
            startPayment(finalbillvalue);
            Checkout.preload(getApplicationContext());
        } else {
            finish();
        }

    }

    // Razorpay code
    public void startPayment(int finalbillvalue) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        Log.d("PaymentModeActivity", "finalbillvalue "+finalbillvalue);

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "The Meat Chop");
            options.put("description", "Order Placing");
            //You can omit the image option to fetch the image from dashboard
            // options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", ""+finalbillvalue);
            if ((merchantorderid != null) && !(TextUtils.isEmpty(merchantorderid))) {
                options.put("order_id", merchantorderid);
            }

            JSONObject preFill = new JSONObject();
            preFill.put("email", "techsupport@themeatchop.in");
            preFill.put("contact", usermobile);

            JSONObject notesobj = new JSONObject();
            String mobile = usermobile.substring(2);
            notesobj.put("mobile", mobile);
            options.put("notes", notesobj);

            options.put("prefill", preFill);

            SettingsUtil settingsUtil = new SettingsUtil(this);
         /* if (settingsUtil.getUpiGatewayStatus().equalsIgnoreCase("OFF")) {
                JSONObject paymentmethod = new JSONObject();
                paymentmethod.put("card", true);
                paymentmethod.put("netbanking", true);
                paymentmethod.put("upi", false);
                paymentmethod.put("wallet", true);
                options.put("method", paymentmethod);
            } */

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @Override
    public void onPaymentSuccess(String razorpayPaymentID, PaymentData data) {
        try {
            // Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            // capturePayment(razorpayPaymentID);
            // Log.d("PaymentServiceActivity", "paymentid "+razorpayPaymentID+" paymentdata id "+data.getPaymentId()+" data orderid "+
            //                                       data.getOrderId()+" data signature "+data.getSignature());
            String razorpayOrderId = "";
            if ((data != null) && (data.getOrderId() != null)) {
                razorpayOrderId = data.getOrderId();
            }
            String paymentidandtime = razorpayPaymentID + ":" + TMCUtil.getInstance().getCurrentTime();
            logEventInGAnalytics("Razorpay Payment Success", paymentidandtime, usermobile);
            Intent intent = new Intent();
            intent.putExtra("trxnstatus", "success");
            intent.putExtra("merchantpaymentid", razorpayPaymentID);
            setResult(RESULT_OK, intent);
            finish();

        } catch (Exception e) {
            logEventInGAnalytics("Razorpay Success Exception", e.getMessage(), usermobile);
            Log.d(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response, PaymentData paymentData) {
        try {
            Log.d("PaymentServiceActivity", "onPaymentError method");
            String responseorderid = response + "-" + TMCUtil.getInstance().getCurrentTime();
            logEventInGAnalytics("Razorpay Payment Error", responseorderid, usermobile);
            Intent intent = new Intent();
            intent.putExtra("trxnstatus", "failed");
            setResult(RESULT_OK, intent);
            finish();

        } catch (Exception e) {
            logEventInGAnalytics("Razorpay Error Exception", e.getMessage(), usermobile);
            Log.e(TAG, "Exception in onPaymentError", e);
        }
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


}
