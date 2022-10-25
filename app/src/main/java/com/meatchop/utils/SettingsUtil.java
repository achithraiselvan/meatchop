package com.meatchop.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class SettingsUtil {

    private Context context;
    private SharedPreferences pref;

    public SettingsUtil(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("SettingsPrefs", 0); //NO I18N
    }

    public void saveName(String name) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("name", name);
        edit.commit();
    }
    public String getName() {
        return pref.getString("name", "");
    }

    public void saveMobile(String mobile) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("mobile", mobile);
        edit.commit();
    }

    public String getMobile() {
        return pref.getString("mobile", "");
    }

    public void saveEmail(String email) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("email", email);
        edit.commit();
    }

    public String getEmail() {
        return pref.getString("email", "");
    }

    public void setLoggedIn() {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("loggedin", true);
        edit.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean("loggedin", false);
    }

    public boolean isMobileNumberVerified() {
        return pref.getBoolean("mobverified", false);
    }

    public void setMobVerification(boolean ismobverified) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("mobverified", ismobverified);
        edit.commit();
    }

    public void setLastAccessedTime(long time) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putLong("lastaccessedtime", time);
        edit.commit();
    }

    public long getLastAccessedTime() {
        return pref.getLong("lastaccessedtime", 0);
    }

    public boolean isSMSPermissionGranted() {
        return pref.getBoolean("smspermissiongranted", false);
    }
    public void setSMSPermissionGranted(boolean smspermissiongranted) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("smspermissiongranted", smspermissiongranted);
        edit.commit();
    }

    public String getDefaultUserLocationLat() {
        return pref.getString("userlocationlat", "");
    }
    public void setDefaultUserLocationLat(String userlocationlat) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("userlocationlat", userlocationlat);
        edit.commit();
    }

    public String getDefaultUserLocationLong() {
        return pref.getString("userlocationlong", "");
    }
    public void setDefaultUserLocationLong(String userlocationlong) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("userlocationlong", userlocationlong);
        edit.commit();
    }

    public int getDeliveryLocationRadius() {
        return pref.getInt("deliverylocradius", 5);
    }
    public void setDeliveryLocationRadius(int deliverylocradius) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("deliverylocradius", deliverylocradius);
        edit.commit();
    }

    public String getUserkey() {
        return pref.getString("userkey", "");
    }
    public void setUserkey(String userkey) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("userkey", userkey);
        edit.commit();
    }
    public String getUserCreatedTime() {
        return pref.getString("usercreatedtime", "");
    }
    public void setUsercreatedtime(String usercreatedtime) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("usercreatedtime", usercreatedtime);
        edit.commit();
    }

    public String getDefaultAddress() { return pref.getString("addressjson", ""); }
    public void setDefaultAddress(String addressjson) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("addressjson", addressjson);
        edit.commit();
    }

    public String getDefaultVendor() { return pref.getString("vendorjson", ""); }
    public void setDefaultVendor(String vendorjson) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("vendorjson", vendorjson);
        edit.commit();
    }

    public String getDefaultVendorkey() { return pref.getString("defaultvendorkey", ""); }
    public void setDefaultVendorkey(String defaultvendorkey) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("defaultvendorkey", defaultvendorkey);
        edit.commit();
    }

    public String getSupportMobileNo() { return pref.getString("supportmobileno", ""); }
    public void setSupportMobileNo(String supportmobileno) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("supportmobileno", supportmobileno);
        edit.commit();
    }

    public String getSupportMailid() { return pref.getString("supportmailid", ""); }
    public void setSupportMailid(String supportmailid) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("supportmailid", supportmailid);
        edit.commit();
    }

    public String getExpressdeliverydetails() { return pref.getString("expressdeliverydetails", ""); }
    public void setExpressdeliverydetails(String expressdeliverydetails) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("expressdeliverydetails", expressdeliverydetails);
        edit.commit();
    }

    public String getLastOrderIdForRating() { return pref.getString("lastorderidforrating", ""); }
    public void setLastOrderIdForRating(String lastorderidforrating) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lastorderidforrating", lastorderidforrating);
        edit.commit();
    }

    public String getLastPlacedOrderId() { return pref.getString("lastplacedorderid", ""); }
    public void setLastPlacedOrderId(String lastplacedorderid) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lastplacedorderid", lastplacedorderid);
        edit.commit();
    }

    public String getLastPlacedOrderSlotname() { return pref.getString("lastplacedorderslotname", ""); }
    public void setLastPlacedOrderslotname(String lastplacedorderslotname) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lastplacedorderslotname", lastplacedorderslotname);
        edit.commit();
    }


    public int getMinimumOrderForDelivery() {
        return pref.getInt("minimumorderfordelivery", 0);
    }
    public void setMinimumOrderForDelivery(int minimumorderfordelivery) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("minimumorderfordelivery", minimumorderfordelivery);
        edit.commit();
    }

    public String getFreshproducectgykey() { return pref.getString("freshproducectgykey", ""); }
    public void setFreshproducectgykey(String freshproducectgykey) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("freshproducectgykey", freshproducectgykey);
        edit.commit();
    }

    public void setLastUpdatedAppVersion(String lastUpdatedAppVersion) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lastupdatedappversion", lastUpdatedAppVersion);
        edit.commit();
    }

    public String getLastUpdatedAppVersion() {
        return pref.getString("lastupdatedappversion", "");
    }

    public int getPaymentschedulerdelaytime() { return pref.getInt("paymentschedulerdelaytime", 120); }
    public void setPaymentschedulerdelaytime(int paymentschedulerdelaytime) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("paymentschedulerdelaytime", paymentschedulerdelaytime);
        edit.commit();
    }

    public boolean isFCMTokenTopicSubscribed() { return pref.getBoolean("isfcmtokensubscribed", false); }
    public void setFCMTokenTopicSubscribed() {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("isfcmtokensubscribed", true);
        edit.commit();
    }

    public String getFCMTOken() { return pref.getString("fcmtoken", ""); }
    public void setFCMToken(String fcmtoken) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("fcmtoken", fcmtoken);
        edit.commit();
    }

    public boolean isPlaceOrderAndPayFeatureEnabled() { return pref.getBoolean("placeorderandpayfeature", false); }
    public void setPlaceOrderAndPayFeature(boolean placeorderandpayfeature) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("placeorderandpayfeature", placeorderandpayfeature);
        edit.commit();
    }

    public String getLastTransactionDetails() { return pref.getString("lasttransactiondetails", ""); }
    public void setLastTransactionDetails(String lasttransactiondetails) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lasttransactiondetails", lasttransactiondetails);
        edit.commit();
    }

    public String getLastOrderdetailsKey() { return pref.getString("lastorderdetailskey", ""); }
    public void setLastOrderdetailsKey(String lastorderdetailskey) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lastorderdetailskey", lastorderdetailskey);
        edit.commit();
    }

    public String getLastPaymentTransKey() { return pref.getString("paymenttranskey", ""); }
    public void setLastPaymentTransKey(String paymenttranskey) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("paymenttranskey", paymenttranskey);
        edit.commit();
    }

    public void clearLastTransactionDetails() {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lasttransactiondetails", "");
        edit.putString("lastorderdetailskey", "");
        edit.putString("paymenttranskey", "");
    }

    public boolean isOrderdetailsloadedinsqlite() { return pref.getBoolean("orderdetailsloadedinsqlite", false); }
    public void setIsOrderdetailsloadedinsqlite(boolean orderdetailsloadedinsqlite) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("orderdetailsloadedinsqlite", orderdetailsloadedinsqlite);
        edit.commit();
    }

    public boolean isOrderTrackingdetailsloadedinsqlite() { return pref.getBoolean("ordertrackingdetailsloadedinsqlite", false); }
    public void setIsOrderTrackingdetailsloadedinsqlite(boolean ordertrackingdetailsloadedinsqlite) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("ordertrackingdetailsloadedinsqlite", ordertrackingdetailsloadedinsqlite);
        edit.commit();
    }


    public boolean isRefreshVendorMapping() { return pref.getBoolean("isrefreshvendormapping", false); }
    public void setRefreshVendorMapping(boolean isrefreshvendormapping) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("isrefreshvendormapping", isrefreshvendormapping);
        edit.commit();
    }

    public void clearAllData() {
        SharedPreferences mPrefs_delete = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_delete = mPrefs_delete.edit();
        editor_delete.clear().commit();
    }

    public boolean isInventoryCheck() { return pref.getBoolean("isinventorycheck", false); }
    public void setInventoryCheck(boolean isinventorycheck) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("isinventorycheck", isinventorycheck);
        edit.commit();
    }

    public boolean isOrderdetailsNewSchema() {
        return pref.getBoolean("isorderdetailsnewschema", false);
    }
    public void setOrderDetailsNewSchema(boolean isorderdetailsnewschema) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("isorderdetailsnewschema", isorderdetailsnewschema);
        edit.commit();
    }

    public String getLastPlacedOrderMenuItems() {
        return pref.getString("lastordermenuitems", "");
    }
    public void setLastPlacedOrderMenuItems(String lastordermenuitems) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lastordermenuitems", lastordermenuitems);
        edit.commit();
    }

    public String getAddressKeysForSharedPrefRefresh() {
        return pref.getString("addresskeysforshprefrefresh", "");
    }
    public void setAddressKeysForSharedPrefRefresh(String addresskeysforshprefrefresh) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("addresskeysforshprefrefresh", addresskeysforshprefrefresh);
        edit.commit();
    }

    public String getUserStatus() {
        return pref.getString("userstatus", "");
    }

    public void setUserStatus(String userstatus) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("userstatus", userstatus);
        edit.commit();
    }



}
