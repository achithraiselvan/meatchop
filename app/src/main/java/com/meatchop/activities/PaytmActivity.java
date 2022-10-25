package com.meatchop.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class PaytmActivity extends BaseActivity {

    public int randomnumber = 67543;
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private String tranactionamount;
    private String orderid;
    private String custid;
    private String mobileno;

    private String mid;
    private String checksumhash = null;
    private Tracker tmcTracker;

 // private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_paytm);

        // Obtain the shared Tracker instance.
     // TMCApplication application = (TMCApplication) getApplication();
     // mTracker = application.getDefaultTracker();
        if (getPackageName().equals("com.meatchop")) {
            tranactionamount = getIntent().getStringExtra("transamount");
            mobileno = getIntent().getStringExtra("mobileno");
            orderid = getIntent().getStringExtra("paytmorderid");
        }

        custid = generateCustomerId();

        generateCheckSum();
    }

    //This is to refresh the order id: Only for the Sample App's purpose.
    @Override
    protected void onStart(){
        super.onStart();
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    public String generateCustomerId() {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(4);
        for(int i=0;i<4;++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        String randomCust = "CUST" + sb.toString();
        System.out.println("Paytm custid: "+ randomCust);
        return randomCust;
    }

    public void onStartTransaction(String checksumhash) {
        // DVPaytmPGService Service = DVPaytmPGService.getProductionService();
        PaytmPGService Service = PaytmPGService.getProductionService();

        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.

        mid = "moNDtk42524109812446";
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID" , "moNDtk42524109812446");
        paramMap.put("ORDER_ID" , orderid);
        paramMap.put("CUST_ID" , custid);
        paramMap.put("INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put("CHANNEL_ID" , "WAP");
        paramMap.put("TXN_AMOUNT" , tranactionamount);
        paramMap.put("WEBSITE" , "DEFAULT");
        paramMap.put("MOBILE_NO" , mobileno);
        // paramMap.put("CALLBACK_URL" , "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
        // paramMap.put("CALLBACK_URL" , "https://securegw.paytm.in/theia/paytmCallback.jsp");
        paramMap.put("CALLBACK_URL" , "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+orderid);
        paramMap.put("CHECKSUMHASH" , checksumhash);
        System.out.println("Paytm Payload: "+ paramMap);

        PaytmOrder Order = new PaytmOrder(paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction : " + inResponse);

                        String status = inResponse.getString("STATUS");
                        String txnid = inResponse.getString("TXNID");
                        String paymentmode = inResponse.getString("PAYMENTMODE");
                        String bankname = inResponse.getString("BANKNAME");
                        String gatewayname = inResponse.getString("GATEWAYNAME");
                        Log.d("LOG", "Payment Transaction : " + status + " txnid "+txnid);


                        if (status.equalsIgnoreCase("TXN_SUCCESS")) {
                            String action = txnid + ":" + TMCUtil.getInstance().getCurrentTime();
                            logEventInGAnalytics("Paytm Transaction Success", action, mobileno);

                         // logEventInGAnalytics("Paytm Transaction Success", txnidtime, mobileno);
                            // Answers.getInstance().logCustom(new CustomEvent("Paytm Transaction Success").putCustomAttribute("mobileno", mobileno));
                            // fireTransactionAPI(orderid);
                            Intent intent = new Intent();
                            intent.putExtra("paytmorderid", orderid);
                            intent.putExtra("trxnstatus", "success");
                            intent.putExtra("trxnid", txnid);
                            intent.putExtra("paymentid", txnid);
                            intent.putExtra("paymentmode", paymentmode);
                            intent.putExtra("bankname", bankname);
                            intent.putExtra("gatewayname", gatewayname);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            String action = "Transaction Failed:" + TMCUtil.getInstance().getCurrentTime();
                            logEventInGAnalytics("Paytm Transaction Failure", action, mobileno);
                            // logEventInGAnalytics("Paytm Transaction Failure", "Transaction Failed-" + getCurrentTime(), mobileno);
                            // Answers.getInstance().logCustom(new CustomEvent("Paytm Transaction Failure").putCustomAttribute("mobileno", mobileno));
                            Intent intent = new Intent();
                            intent.putExtra("trxnstatus", "failure");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        // Toast.makeText(getApplicationContext(), "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        String action = "No Network" + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("Paytm Network Not Available", action, mobileno);
                        // Answers.getInstance().logCustom(new CustomEvent("Paytm Network Not Available").putCustomAttribute("mobileno", mobileno));
                        // If network is not
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                     // logEventInGAnalytics("Paytm clientAuthenticationFailed", inErrorMessage, mobileno);
                        String action = "clientAuthenticationFailed" + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("Paytm clientAuthenticationFailed", action, mobileno);

                        // Answers.getInstance().logCustom(new CustomEvent("Paytm clientAuthenticationFailed").
                        //         putCustomAttribute("mobileno", mobileno).putCustomAttribute("errormessage", inErrorMessage));
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        String action = "onErrorLoadingWebPage" + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("Paytm onErrorLoadingWebPage", action, mobileno);
                        // logEventInGAnalytics("Paytm onErrorLoadingWebPage", inErrorMessage, mobileno);
                        // Answers.getInstance().logCustom(new CustomEvent("Paytm onErrorLoadingWebPage").
                        //         putCustomAttribute("mobileno", mobileno).putCustomAttribute("errormessage", inErrorMessage));

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                        String action = "onBackPressed by user" + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("Paytm Transaction Cancelled", action, mobileno);

                        Intent intent = new Intent();
                        intent.putExtra("trxnstatus", "failure");
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                     // logEventInGAnalytics("Paytm onTransactionCancel", inErrorMessage, mobileno);
                        // Answers.getInstance().logCustom(new CustomEvent("Paytm onTransactionCancel").
                        //         putCustomAttribute("mobileno", mobileno).putCustomAttribute("errormessage", inErrorMessage));
                        String action = "Transaction Cancelled" + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("Paytm Transaction Cancelled", action, mobileno);

                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Intent intent = new Intent();
                        intent.putExtra("trxnstatus", "failure");
                        setResult(RESULT_OK, intent);
                        finish();
                        // Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        String currentTime = sdf.format(new Date());
        return currentTime;
    }

    private void generateCheckSum() {
        TMCRestClient.generatePaytmCheckSumValue(orderid, custid, tranactionamount, mobileno, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                Log.d("PaytmActivity", "generate checksum jsonobj "+jsonObject);

                try {
                    if ((jsonObject != null) && (jsonObject.has("CHECKSUMHASH"))) {
                        String checksumhash = jsonObject.getString("CHECKSUMHASH");
                        onStartTransaction(checksumhash);
                        String action = checksumhash + ":" + TMCUtil.getInstance().getCurrentTime();
                        logEventInGAnalytics("Paytm generateCheckSum", action, mobileno);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d("PaytmActivity", "generateCheckSum failed0 jsonObject "+jsonObject+ " throwable "+throwable); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d("PaytmActivity", "generateCheckSum failed1 String "+s+ " throwable "+throwable); //No i18n
            }
        });
    }

    @Override
    public void onBackPressed() {
     // SettingsUtil settingsUtil = new SettingsUtil(this);
     // settingsUtil.setPaymentFailed(true);
        finish();
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
