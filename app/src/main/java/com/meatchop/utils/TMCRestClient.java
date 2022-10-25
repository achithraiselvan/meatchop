package com.meatchop.utils;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.Paymenttransaction;
import com.meatchop.data.TMCUser;
import com.meatchop.data.TMCUserAddress;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.entity.StringEntity;

public class TMCRestClient {
    public static final String AWS_GETAPPRELEASEHISTORY = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/record/list?modulename=AndroidVersion";
    public static final String AWS_GETAPPDATA = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/record/list?modulename=Mobile";
    public static final String AWS_ADDUSERDETAILS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/customer?modulename=Customer";
    public static final String AWS_GETUSERDETAILS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/customerdetailsformobileno";
    public static final String AWS_UPDATEUSERDETAILS = "https://cex0daaea6.execute-api.ap-south-1.amazonaws.com/dev/update/customerdetails?modulename=Customer";

    public static final String AWS_ADDUSERDETAILS_TMCUSER = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/tmcuser";
    public static final String AWS_GETUSERDETAILS_TMCUSER = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/tmcuserforusermobileno";
    public static final String AWS_UPDATEUSERDETAILS_TMCUSER = "https://cex0daaea6.execute-api.ap-south-1.amazonaws.com/dev/update/tmcuser";

    public static final String AWS_GETVENDORRECORDS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/record/list?modulename=Store";
    public static final String AWS_GETVENDORFORID = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/storedetailsforid";

    public static final String AWS_GETTMCCTGYRECORDS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/tmcctgy";
    public static final String AWS_GETTMCSUBCTGYRECORDS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/record/list?modulename=SubCategory";
    public static final String AWS_GETTMCMENUITEMS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/menuforstoreandsubctgy";
    public static final String AWS_GETTMCMENUITEMSFORSTORE = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/menuforstore";
    public static final String AWS_GETMARINADEMEATITEMFORSTORE = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/marinademenuforstore";
    public static final String AWS_GETTMCMENUITEMAVLDETAILSFORSTORE = "https://69na3bh009.execute-api.ap-south-1.amazonaws.com/Dev/get/menuitemstockavldetails/vendorkey";

    public static final String AWS_GETADDRESS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/address";
    public static final String AWS_GETADDRESS_FROMKEY = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/addressforkey?addressid=";
    public static final String AWS_ADDADDRESS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/address?modulename=Adrs";
    public static final String AWS_UPDATEADDRESS = "https://cex0daaea6.execute-api.ap-south-1.amazonaws.com/dev/update/address?modulename=Adrs";
    public static final String AWS_DELETEADDRESS = "https://k8l94iweoc.execute-api.ap-south-1.amazonaws.com/dev/delete/addressforid";

 // public static final String AWS_GETTILEDATA = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/record/list?modulename=Tile";
    public static final String AWS_GETTILEDATA = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/gettmctileforstoreid";
    public static final String AWS_GETDELSLOTDETAILS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/slotsforstoreid";

    public static final String AWS_GETCOUPONDETAILS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/record/list?modulename=Coupon";
    public static final String AWS_GETCOUPONTRANSDETAILS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/coupontranslistforuserid";
    public static final String AWS_GETCOUPONUSERGROUPDETAILS =
                               "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/couponusergrouplistforuserid?userid=";

    public static final String PAYTM_CHECKSUM_URL = "https://us-central1-dosavillage-acc39.cloudfunctions.net/generateCheckSumForTmc";
    public static final String RAZORPAY_GENERATEORDERID_URL = "https://us-central1-dosavillage-acc39.cloudfunctions.net/generaterazorpayorderidfortmc";

    public static final String AWS_ADDPAYMENTTRANS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/payment?modulename=Payment";
    public static final String AWS_UPDATEPAYMENTTRANS = "https://cex0daaea6.execute-api.ap-south-1.amazonaws.com/dev/update/paymenttrans?modulename=Payment";

    public static final String AWS_UPDATEORDERDETAILSPAYMENTMODE = "https://cex0daaea6.execute-api.ap-south-1.amazonaws.com/dev/update/orderdetails?modulename=PlaceOrder";

    public static final String RAZORPAY_GETORDERSTATUS_URL = "https://us-central1-dosavillage-acc39.cloudfunctions.net/getrazorpayorderstatusfortmc";
    public static final String PAYTM_GETORDERSTATUS_URL = "https://us-central1-dosavillage-acc39.cloudfunctions.net/getpaytmorderstatusfortmc";
    public static final String RAZORPAY_GETPAYMENT_URL = "https://us-central1-dosavillage-acc39.cloudfunctions.net/getrzpaymentfortmc";
    public static final String RAZORPAY_GETPAYMENTCARDINFO_URL = "https://us-central1-dosavillage-acc39.cloudfunctions.net/getrazorpaycardinfofortmc";

    public static final String AWS_GETORDERDETAILSFROMORDERID = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/orderdetails";

    public static final String AWS_UPDATESTATUSINORDERTRACKING = "https://cex0daaea6.execute-api.ap-south-1.amazonaws.com/dev/update/trackingorderdetails";
    public static final String AWS_GETORDERTRACKINGDETAILS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/trackorderdetails";
    public static final String AWS_GETORDERTRACKINGDETAILSWITHINDEX =
                                           "https://pna7nowovg.execute-api.ap-south-1.amazonaws.com/dev/trackorderdetailswithorderid";

    public static final String AWS_GETORDERTRACKINGDETAILSFROMMOBILENO =
                                      "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/trackorderdetailsformobileno";

    public static final String AWS_GETORDERDETAILS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/orderhistory";
    public static final String AWS_GETRATINGORDERDETAILS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/ratingdetailsformobileno";

    public static final String AWS_GETPAYMENTTRANSACTION = "https://wh72246j61.execute-api.ap-south-1.amazonaws.com/stage/resource";
    public static final String AWS_GETPAYMENTTRANSACTIONFROMORDERID = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/getpaymenttransationfororderid";
    public static final String AWS_GETPAYMENTTRANSACTIONFROMORDERIDWITHINDEX =
                                               "https://pna7nowovg.execute-api.ap-south-1.amazonaws.com/dev/paymenttransactionwithorderid";

    public static final String AWS_GETMEALKITINGRED = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/mealkitingredientslistformenuitemkey";
    public static final String AWS_GETMEALKITRECIPES = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/mealkitrecipeformenuid";
    public static final String AWS_GETDELIVERYSLABDETAILS = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/deliveryslabdetailsforvendorkey";

    public static final String AWS_ADDRATING =  "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/rating?modulename=Rating";

    public static final String AWS_GETSTOCKOUTGOINGDETAILS = "https://69na3bh009.execute-api.ap-south-1.amazonaws.com/Dev/get/inventorystockoutgoingdetails/stockincomingkey";
    public static final String AWS_UPDATESTOCKBALANCE = "https://3ve1c962t7.execute-api.ap-south-1.amazonaws.com/dev/update/menustockavldetails";
    public static final String AWS_ADDSTOCKOUTGOINGDETAILS = "https://f0zf5ui0u5.execute-api.ap-south-1.amazonaws.com/dev/add/inventorystockoutgoingdetails";
    public static final String AWS_STOCKBALTRANSACTIONHISTORY = "https://f0zf5ui0u5.execute-api.ap-south-1.amazonaws.com/dev/add/stockbalancetransactionhistory";
    public static final String AWS_ADDMENUAVLTRANSACTIONS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/menuavailabilitytransactionnew?modulename=MenuAvailTransNew";

    private static final int DEFAULT_TIMEOUT = 40 * 1000;

    private static AsyncHttpClient client = new AsyncHttpClient();

 // private static final String SMS_APIKEY = "Ae428c4a11138a24e41b45a0fce3576fb";
    private static final String SMS_APIKEY = "A7a39e3e85b5ed221cfed9438e83fdab9";
    // private static final String SMS_PASSWORD = "123456";
    private static final String SMS_SENDERID = "TMCHOP";
 // public static final String SMS_URL = "https://alerts.sinfini.com/api/web2sms.php?workingkey=" + SMS_APIKEY+ "&sender=" + SMS_SENDERID;
    public static final String SMS_URL = "https://api-alerts.kaleyra.com/v4/?api_key=" + SMS_APIKEY+ "&method=sms" +"&sender=" + SMS_SENDERID;

    public static void sendSolutionsInfiniSms(String mobileno, String message, JsonHttpResponseHandler asyncHttpResponseHandler) {
        Log.d("TMCRestClient", "sendSolutionsInfiniSms method");
        String url = SMS_URL + "?workingkey=" + SMS_APIKEY + "&sender=" + SMS_SENDERID + "&to=" + mobileno + "&message=" +message;

        RequestParams params = new RequestParams();
        params.put("workingkey", SMS_APIKEY);
        params.put("sender", SMS_SENDERID);
        params.put("to", mobileno);
        params.put("message", message);

        Log.d("TMCRestClient", "sendSolutionsInfiniSms method url " + url);
        client.post(url, params, asyncHttpResponseHandler);
    }

    private static final String ZOHOCREATOR_ADD_RECORD = "https://creator.zoho.com/api/%s/json/%s/form/%s/record/add/";
    public static final String ZOHOCREATOR_OWNER_NAME = "yasararafath";
    public static final String ZOHOCREATOR_APP_NAME  = "dosavillage";
    public static final String ZOHOCREATOR_FORM_NAME = "TMC_Order_Details";
    public static final String ZOHOCREATOR_AUTHTOKEN = "19572f1fb5abf5ba64fda461f4878b05";

    public static void sendMail(String orderid, String mobile, String orderplacedtime, String slottype,
                                   String slotdatetime, String itemdetails, JsonHttpResponseHandler asyncHttpResponseHandler) {
        String url = String.format(ZOHOCREATOR_ADD_RECORD, ZOHOCREATOR_OWNER_NAME,
                                                           ZOHOCREATOR_APP_NAME, ZOHOCREATOR_FORM_NAME);

        RequestParams params = new RequestParams();
        params.put("authtoken", ZOHOCREATOR_AUTHTOKEN);
        params.put("scope", "creatorapi");
        params.put("Orderid", orderid);
        params.put("Mobileno", mobile);
        params.put("Order_Placed_Time", orderplacedtime);
        params.put("Slottype", slottype);
        params.put("Slotdatetime", slotdatetime);
        params.put("Item_Details", itemdetails);
        Log.d("TMCRestClient", "sendMail url "+url);
        client.post(url, params, asyncHttpResponseHandler);
    }

    public static void getMealkitingred(String menuitemid, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 10 * 1000;
        client.setTimeout(timeout);
        client.setMaxRetriesAndTimeout(1, 20000);
        client.setResponseTimeout(20000);
        client.setConnectTimeout(20000);
        String url = AWS_GETMEALKITINGRED + "?menuid=" + menuitemid;
        Log.d("TMCRestClient", "getMealkitingred url "+url);
        client.get(url, params, asyncHttpResponseHandler);
    }

    public static void updateUserDetails(Context context, TMCUser user, JsonHttpResponseHandler asyncHttpResponseHandler) {
        String name = user.getName(); String email = user.getEmail();
        String mobileno = "91" + user.getMobileno(); String createdtime = user.getCreatedtime();
        String updatedtime = user.getUpdatedtime(); String authorizationcode = user.getAuthorizationcode();
        String key = user.getKey();
        String deviceos = user.getDeviceos(); String appversion = user.getAppversion();
        try {
            JSONObject jsonObject = new JSONObject();
            if ((key != null) && (!TextUtils.isEmpty(key))) {
                jsonObject.put("key", key);
            }
            if ((name != null) && (!TextUtils.isEmpty(name))) {
                jsonObject.put("name", name);
            }
            if ((email != null) && (!TextUtils.isEmpty(email))) {
                jsonObject.put("email", email);
            }
            if ((mobileno != null) && (!TextUtils.isEmpty(mobileno))) {
                jsonObject.put("mobileno", mobileno);
            }
            if ((createdtime != null) && (!TextUtils.isEmpty(createdtime))) {
                jsonObject.put("createdtime", createdtime);
            }
            if ((updatedtime != null) && (!TextUtils.isEmpty(updatedtime))) {
                jsonObject.put("updatedtime", updatedtime);
            }
            if ((authorizationcode != null) && (!TextUtils.isEmpty(authorizationcode))) {
                jsonObject.put("authorizationcode", authorizationcode);
            }
            if ((deviceos != null) && (!TextUtils.isEmpty(deviceos))) {
                jsonObject.put("deviceos", deviceos);
            }
            if ((appversion != null) && (!TextUtils.isEmpty(appversion))) {
                jsonObject.put("appversion", appversion);
            }
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setTimeout(DEFAULT_TIMEOUT);
            client.post(context, AWS_UPDATEUSERDETAILS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void updateUserDetails(Context context, String name, String email, String userkey,
                                                                         JsonHttpResponseHandler asyncHttpResponseHandler) {
        String updatedtime = TMCUtil.getInstance().getCurrentTime();
        try {
            JSONObject jsonObject = new JSONObject();
            if ((userkey != null) && (!TextUtils.isEmpty(userkey))) {
                jsonObject.put("key", userkey);
            }
            if ((name != null) && (!TextUtils.isEmpty(name))) {
                jsonObject.put("name", name);
            }
            if ((email != null) && (!TextUtils.isEmpty(email))) {
                jsonObject.put("email", email);
            }
            if ((updatedtime != null) && (!TextUtils.isEmpty(updatedtime))) {
                jsonObject.put("updatedtime", updatedtime);
            }
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setTimeout(DEFAULT_TIMEOUT);
            client.post(context, AWS_UPDATEUSERDETAILS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void updateAddressDetails(Context context, TMCUserAddress userAddress, JsonHttpResponseHandler asyncHttpResponseHandler) {
        String userkey = userAddress.getUserkey(); String addresstype = userAddress.getAddresstype();
        boolean isdefault = userAddress.getIsdefault();
        String addressline1 = userAddress.getAddressline1(); String addressline2 = userAddress.getAddressline2();
        String landmark = userAddress.getLandmark();
        double locationlat = userAddress.getLocationlat(); double locationlong = userAddress.getLocationlong();
        String vendorkey = userAddress.getVendorkey(); String vendorname = userAddress.getVendorname();

        try {
            JSONObject jsonObject = new JSONObject();

            if ((userkey != null) && (!TextUtils.isEmpty(userkey))) {
                jsonObject.put("userkey", userkey);
            }
            if ((addresstype != null) && (!TextUtils.isEmpty(addresstype))) {
                jsonObject.put("addresstype", addresstype);
            }
            jsonObject.put("isdefault", isdefault);
            if ((addressline1 != null) && (!TextUtils.isEmpty(addressline1))) {
                jsonObject.put("addressline1", addressline1);
            }
            if ((addressline2 != null) && (!TextUtils.isEmpty(addressline2))) {
                jsonObject.put("addressline2", addressline2);
            }
            if ((landmark != null) && (!TextUtils.isEmpty(landmark))) {
                jsonObject.put("landmark", landmark);
            }
            if ((vendorkey != null) && (!TextUtils.isEmpty(vendorkey))) {
                jsonObject.put("vendorkey", vendorkey);
            }
            if ((vendorname != null) && (!TextUtils.isEmpty(vendorname))) {
                jsonObject.put("vendorname", vendorname);
            }
            jsonObject.put("locationlat", locationlat);
            jsonObject.put("locationlong", locationlong);
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setTimeout(DEFAULT_TIMEOUT);
            client.post(context, AWS_UPDATEADDRESS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void getAddressDetails(String userkey, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        String url = AWS_GETADDRESS + "?userid=" + userkey;
        client.setTimeout(DEFAULT_TIMEOUT);
        client.get(url, params, asyncHttpResponseHandler);
    }

    public static void getVendorDetails(JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        client.setTimeout(DEFAULT_TIMEOUT);
        Log.d("TMCRestClient", "getVendordetails method AWS_GETVENDORRECORDS"+AWS_GETVENDORRECORDS);
        client.get(AWS_GETVENDORRECORDS, params, asyncHttpResponseHandler);
    }


    public static void getDeliverySlotDetails(String vendorkey, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        client.setTimeout(DEFAULT_TIMEOUT);
        String url = AWS_GETDELSLOTDETAILS + "?storeid=" + vendorkey;
        Log.d("TMCRestClient", "getDeliverySlotDetails url "+url);
        client.get(url, params, asyncHttpResponseHandler);
    }

    public static void generateOrderId(String billamount, String transactionid, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("transactionid", transactionid);
        params.put("billamount", billamount);
        client.get(RAZORPAY_GENERATEORDERID_URL, params, asyncHttpResponseHandler);
        // Log.d("DVRestClient", "generateorderid method url " + client.getUrlWithQueryString(RAZORPAY_AUTOCAPTURE_URL, params));
    }

    public static void generatePaytmCheckSumValue(String orderid, String customerid,
                                                  String transamount, String mobileno, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("orderid", orderid);
        params.put("customerid", customerid);
        params.put("transamount", transamount);
        params.put("mobileno", mobileno);
        client.get(PAYTM_CHECKSUM_URL, params, asyncHttpResponseHandler);
    }

    public static void getRazorpayOrderStatus(String merchantorderid, JsonHttpResponseHandler asyncHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("orderid", merchantorderid);
        int timeout = 40 * 1000;
        client.setTimeout(timeout);
        Log.d("TMCRestClient", "getRazorpayOrderStatus merchantorderid "+merchantorderid);
        client.get(RAZORPAY_GETORDERSTATUS_URL, params, asyncHttpResponseHandler);

        // Log.d("DVRestClient", "getRazorpayOrderStatus method url " + client.getUrlWithQueryString(RAZORPAY_GETORDERSTATUS_URL, params));
    }

    public static void getPaytmOrderStatus(String orderid, JsonHttpResponseHandler asyncHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("orderid", orderid);
        int timeout = 40 * 1000;
        client.setTimeout(timeout);
        Log.d("TMCRestClient", "getRazorpayOrderStatus merchantorderid "+orderid);
        client.get(PAYTM_GETORDERSTATUS_URL, params, asyncHttpResponseHandler);

        // Log.d("DVRestClient", "getPaytmOrderStatus method url " + client.getUrlWithQueryString(PAYTM_GETORDERSTATUS_URL, params));
    }

    public static void getRazorpayPayment(String paymentid, JsonHttpResponseHandler asyncHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("paymentid", paymentid);
        int timeout = 40 * 1000;
        client.setTimeout(timeout);
        client.get(RAZORPAY_GETPAYMENT_URL, params, asyncHttpResponseHandler);

        // Log.d("DVRestClient", "getRazorpayPayment method url " + client.getUrlWithQueryString(RAZORPAY_GETPAYMENT_URL, params));
    }

    public static void getRazorpayPaymentCardInfo(String paymentid, JsonHttpResponseHandler asyncHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("paymentid", paymentid);
        int timeout = 40 * 1000;
        client.setTimeout(timeout);
        client.get(RAZORPAY_GETPAYMENTCARDINFO_URL, params, asyncHttpResponseHandler);
        // Log.d("DVRestClient", "getRazorpayPaymentCardInfo method url " + client.getUrlWithQueryString(RAZORPAY_GETPAYMENT_URL, params));
    }

    public static void getOrderDetailsFromOrderid(String orderid, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 10 * 1000;
        client.setTimeout(timeout);
        client.setMaxRetriesAndTimeout(1, 20000);
        client.setResponseTimeout(20000);
        client.setConnectTimeout(20000);
        String url = AWS_GETORDERDETAILSFROMORDERID + "?orderid=" + orderid;
        Log.d("TMCRestClient", "getOrderDetailsFromOrderid url "+url);
        client.get(url, params, asyncHttpResponseHandler);
    }

    public static final String AWS_ADDCOUPONTRANSACTIONS =
                                        "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/coupontrans?modulename=CouponTrans";
    public static final String AWS_ADDORDERDETAILS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/placeorder?modulename=PlaceOrder";
    public static final String AWS_ADDORDERSUBDETAILS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/orderitem?modulename=OrderItem";
    public static final String AWS_ADDORDERTRACKINGDETAILS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/ordertracking?modulename=TrackOrder";

    public static final String AWS_ADDCUSTORDDETAILS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/customerorderdetails";
    public static final String AWS_ADDVENDORDDETAILS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/vendororderdetails";
    public static final String AWS_ADDCUSTORDTRACKDETAILS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/customerordertrackingdetails";
    public static final String AWS_ADDVENDORDTRACKDETAILS = "https://08klj9r8hb.execute-api.ap-south-1.amazonaws.com/dev/add/vendorordertrackingdetails";
    public static final String AWS_GETCUSTORDDETAILSFROMMOBILENO = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/customerorderdetailsforusermobileno";
    public static final String AWS_GETCUSTORDTRACKDETFROMORDERIDANDMOBILE = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/customerordertrackingdetailsforusermobilenowithorderid";
    public static final String AWS_GETCUSTORDTRACKDETFROMMOBILENO = "https://l5tqeb0rof.execute-api.ap-south-1.amazonaws.com/dev/get/customerordertrackingdetailsforusermobileno";

    public static final String AWS_UPDATEPAYMODEINCUSTORDDETAILS = "https://cex0daaea6.execute-api.ap-south-1.amazonaws.com/dev/update/customerorderdetails";
    public static final String AWS_UPDATEPAYMODEINVENDORDDETAILS = "https://cex0daaea6.execute-api.ap-south-1.amazonaws.com/dev/update/vendororderdetails";

 /* public static void addOrderDetails(Context context, Orderdetails orderdetails, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 40 * 1000;
        client.setTimeout(timeout);
        client.setMaxConnections(6);
        try {
            StringEntity entity = new StringEntity(orderdetails.getJSONString());
            Log.d("TMCRestClient", "orderdetails jsonstring "+orderdetails.getJSONString());
            client.post(context, AWS_ADDORDERDETAILS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } */

    public static void addOrderItemDetails(Context context, JSONObject orderItemJSON, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 40 * 1000;
        client.setTimeout(timeout);
        try {
            StringEntity entity = new StringEntity(orderItemJSON.toString());
            Log.d("TMCRestClient", "addOrderItemDetails url "+AWS_ADDORDERSUBDETAILS+" jsonstr "+orderItemJSON.toString());
            client.post(context, AWS_ADDORDERSUBDETAILS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addOrderTrackingdetails(Context context, JSONObject orderTrackingJSON, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 40 * 1000;
        client.setTimeout(timeout);
        try {
            StringEntity entity = new StringEntity(orderTrackingJSON.toString());
            Log.d("TMCRestClient", "addOrderItemDetails url "+AWS_ADDORDERTRACKINGDETAILS+" jsonstr "+orderTrackingJSON.toString());
            client.post(context, AWS_ADDORDERTRACKINGDETAILS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void getOrderTrackingDetails(String orderid, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 10 * 1000;
        client.setTimeout(timeout);
        client.setMaxRetriesAndTimeout(1, 20000);
        client.setResponseTimeout(20000);
        client.setConnectTimeout(20000);
        String url = AWS_GETORDERTRACKINGDETAILS + "?orderid=" + orderid;
        Log.d("TMCRestClient", "getOrderTrackingDetails url "+url);
        client.get(url, params, asyncHttpResponseHandler);
    }

    public static void getOrderDetailsFromMobileno(String mobileno, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 10 * 1000;
        client.setTimeout(timeout);
        client.setMaxRetriesAndTimeout(1, 20000);
        client.setResponseTimeout(20000);
        client.setConnectTimeout(20000);
        mobileno = "%2B" + mobileno;
        String url = AWS_GETORDERDETAILS + "?mobileno=" + mobileno;
        Log.d("TMCRestClient", "getOrderTrackingDetails url "+url);
        client.get(url, params, asyncHttpResponseHandler);
    }

    public static void deleteUserAddress(Context context, String useraddresskey, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        String url = TMCRestClient.AWS_DELETEADDRESS + "?addressid=" + useraddresskey;
        Log.d("TMCRestClient", "deleteUserAddress url "+url);
        client.delete(context, url, asyncHttpResponseHandler);
    }

    public static void getTileDetails(JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 15 * 1000;
        client.setTimeout(timeout);
        client.setMaxRetriesAndTimeout(1, 20000);
        client.setResponseTimeout(20000);
        client.setConnectTimeout(20000);
        Log.d("TMCRestClient", "gettiledetails url "+AWS_GETTILEDATA);
        client.get(AWS_GETTILEDATA, params, asyncHttpResponseHandler);
    }

    public static void getTMCMMenuItems(String vendorkey, String tmcsubctgykey, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        String url = AWS_GETTMCMENUITEMS + "?storeid=" + vendorkey + "&subctgyid=" + tmcsubctgykey;
        client.setTimeout(DEFAULT_TIMEOUT);
        client.setConnectTimeout(20000);
        client.setResponseTimeout(20000);
        Log.d("TMCRestClient", "getmenuitems url "+url);
        client.get(url, params, asyncHttpResponseHandler);
    }

    public static void getTMCMMenuItemsForStore(String vendorkey, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        String url = AWS_GETTMCMENUITEMSFORSTORE + "?storeid=" + vendorkey;
        client.setTimeout(DEFAULT_TIMEOUT);
        client.setConnectTimeout(20000);
        client.setResponseTimeout(20000);
        Log.d("TMCRestClient", "getTMCMMenuItemsForStore url "+url);
        client.get(url, params, asyncHttpResponseHandler);
    }


    public static void getAppData(JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        int timeout = 30 * 1000;
        Log.d("TMCRestClient", "getAppData API Call");
        client.setTimeout(timeout);
        client.get(AWS_GETAPPDATA, params, asyncHttpResponseHandler);

    }

    public static void getTMCSubCtgyDetails(JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        client.setTimeout(DEFAULT_TIMEOUT);
        client.get(AWS_GETTMCSUBCTGYRECORDS, params, asyncHttpResponseHandler);
    }

    public static void addUserDetails(Context context, TMCUser user, JsonHttpResponseHandler asyncHttpResponseHandler) {
        String name = user.getName(); String email = user.getEmail();
        String mobileno = "+" + user.getMobileno(); String createdtime = user.getCreatedtime();
        String updatedtime = user.getUpdatedtime(); String authorizationcode = user.getAuthorizationcode();
        try {
            JSONObject jsonObject = new JSONObject();

            if ((name != null) && (!TextUtils.isEmpty(name))) {
                jsonObject.put("name", name);
            }
            if ((email != null) && (!TextUtils.isEmpty(email))) {
                jsonObject.put("email", email);
            }
            if ((mobileno != null) && (!TextUtils.isEmpty(mobileno))) {
                jsonObject.put("mobileno", mobileno);
            }
            if ((createdtime != null) && (!TextUtils.isEmpty(createdtime))) {
                jsonObject.put("createdtime", createdtime);
            }
            if ((updatedtime != null) && (!TextUtils.isEmpty(updatedtime))) {
                jsonObject.put("updatedtime", updatedtime);
            }
            if ((authorizationcode != null) && (!TextUtils.isEmpty(authorizationcode))) {
                jsonObject.put("authorizationcode", authorizationcode);
            }
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setTimeout(DEFAULT_TIMEOUT);
            client.post(context, AWS_ADDUSERDETAILS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void getUserDetails(String mobileno, JsonHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        String url = AWS_GETUSERDETAILS + "?mobileno=" + mobileno;
        client.setTimeout(DEFAULT_TIMEOUT);
        Log.d("TMCRestClient", "url "+url);
        client.get(url, params, asyncHttpResponseHandler);
    }

    public static void addAddressDetails(Context context, TMCUserAddress userAddress, JsonHttpResponseHandler asyncHttpResponseHandler) {
        String userkey = userAddress.getUserkey(); String addresstype = userAddress.getAddresstype();
        String addressline1 = userAddress.getAddressline1(); String addressline2 = userAddress.getAddressline2();
        String landmark = userAddress.getLandmark();  String pincode = userAddress.getPincode();
        double locationlat = userAddress.getLocationlat(); double locationlong = userAddress.getLocationlong();
        String vendorkey = userAddress.getVendorkey(); String vendorname = userAddress.getVendorname();
        try {
            JSONObject jsonObject = new JSONObject();

            if ((userkey != null) && (!TextUtils.isEmpty(userkey))) {
                jsonObject.put("userkey", userkey);
            }
            if ((addresstype != null) && (!TextUtils.isEmpty(addresstype))) {
                jsonObject.put("addresstype", addresstype);
            }
            if ((addressline1 != null) && (!TextUtils.isEmpty(addressline1))) {
                jsonObject.put("addressline1", addressline1);
            }
            if ((addressline2 != null) && (!TextUtils.isEmpty(addressline2))) {
                jsonObject.put("addressline2", addressline2);
            }
            if ((landmark != null) && (!TextUtils.isEmpty(landmark))) {
                jsonObject.put("landmark", landmark);
            }
            if ((pincode != null) && (!TextUtils.isEmpty(pincode))) {
                jsonObject.put("pincode", pincode);
            }
            if ((vendorkey != null) && (!TextUtils.isEmpty(vendorkey))) {
                jsonObject.put("vendorkey", vendorkey);
            }
            if ((vendorname != null) && (!TextUtils.isEmpty(vendorname))) {
                jsonObject.put("vendorname", vendorname);
            }
            jsonObject.put("locationlat", locationlat);
            jsonObject.put("locationlong", locationlong);
            StringEntity entity = new StringEntity(jsonObject.toString());
            Log.d("TMCRestClient", "jsonObject toString "+jsonObject.toString());
            client.setTimeout(DEFAULT_TIMEOUT);
            client.post(context, AWS_ADDADDRESS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addPaymenttransaction(Context context, Paymenttransaction paymenttransaction,
                                             JsonHttpResponseHandler asyncHttpResponseHandler) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("mobileno", paymenttransaction.getMobileno());
            jsonObject.put("orderid", paymenttransaction.getOrderid());
            jsonObject.put("merchantorderid", paymenttransaction.getMerchantorderid());
            jsonObject.put("paymentmode", paymenttransaction.getPaymentmode());
            jsonObject.put("transactiontime", paymenttransaction.getTransactiontime());
            jsonObject.put("transactionamount", paymenttransaction.getTransactionamount());
            jsonObject.put("status", paymenttransaction.getStatus());

            Log.d("TMCRestClient", "addPaymenttransaction jsonObject string "+jsonObject.toString());
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setTimeout(DEFAULT_TIMEOUT);
            client.post(context, AWS_ADDPAYMENTTRANS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void updatePaymentTransaction(Context context, String key, String status, String merchantpaymentid,
                                                JsonHttpResponseHandler asyncHttpResponseHandler) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", key);
            jsonObject.put("status", status);
            jsonObject.put("merchantpaymentid", merchantpaymentid);
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setTimeout(DEFAULT_TIMEOUT);
            client.post(context, AWS_UPDATEPAYMENTTRANS, entity, "application/json", asyncHttpResponseHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
