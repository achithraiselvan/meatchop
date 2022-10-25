package com.meatchop.data;

import android.util.Log;


import org.json.JSONObject;

public class Coupontransactions {

    private double couponcashbackamount;
    private double coupondiscountamount;
    private String couponkey;
    private String coupontype;
    private String key;
    private String mobileno;
    private String orderid;
    private String transactiondate;
    private String transactiontime;
    private String userkey;
    private double vendordiscountamount;
    private String vendorkey;

    public Coupontransactions() {

    }

    public Coupontransactions(JSONObject jsonObject) {
        try {
            if (jsonObject.has("couponcashbackamount")) {
                Object obj1 = jsonObject.get("couponcashbackamount");
                if (obj1 instanceof Double) {
                    this.couponcashbackamount = jsonObject.getDouble("couponcashbackamount");
                }

            }
            if (jsonObject.has("coupondiscountamount")) {
                Object obj1 = jsonObject.get("coupondiscountamount");
                if (obj1 instanceof Double) {
                    this.coupondiscountamount = jsonObject.getDouble("coupondiscountamount");
                }
            }
            if (jsonObject.has("couponkey")) {
                this.couponkey = jsonObject.getString("couponkey");
            }
            if (jsonObject.has("coupontype")) {
                this.coupontype = jsonObject.getString("coupontype");
            }
            if (jsonObject.has("key")) {
                this.key = jsonObject.getString("key");
            }
            if (jsonObject.has("mobileno")) {
                this.mobileno = jsonObject.getString("mobileno");
            }
            if (jsonObject.has("orderid")) {
                this.orderid = jsonObject.getString("orderid");
            }
            if (jsonObject.has("transactiondate")) {
                this.transactiondate = jsonObject.getString("transactiondate");
            }
            if (jsonObject.has("transactiontime")) {
                this.transactiontime = jsonObject.getString("transactiontime");
            }
            if (jsonObject.has("userkey")) {
                this.userkey = jsonObject.getString("userkey");
            }
            if (jsonObject.has("vendorkey")) {
                this.vendorkey = jsonObject.getString("vendorkey");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public double getCouponcashbackamount() { return couponcashbackamount; }
    public void setCouponcashbackamount(double couponcashbackamount) { this.couponcashbackamount = couponcashbackamount; }

    public double getCoupondiscountamount() { return coupondiscountamount; }
    public void setCoupondiscountamount(double coupondiscountamount) { this.coupondiscountamount = coupondiscountamount; }

    public String getCouponkey() { return couponkey; }
    public void setCouponkey(String couponkey) { this.couponkey = couponkey; }

    public String getCoupontype() { return coupontype; }
    public void setCoupontype(String coupontype) { this.coupontype = coupontype; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getMobileno() { return mobileno; }
    public void setMobileno(String mobileno) { this.mobileno = mobileno; }

    public String getOrderid() { return orderid; }
    public void setOrderid(String orderid) { this.orderid = orderid; }

    public String getTransactiondate() { return transactiondate; }
    public void setTransactiondate(String transactiondate) { this.transactiondate = transactiondate; }

    public String getTransactiontime() { return transactiontime; }
    public void setTransactiontime(String transactiontime) { this.transactiontime = transactiontime; }

    public String getUserkey() { return userkey; }
    public void setUserkey(String userkey) { this.userkey = userkey; }

    public String getVendorkey() { return vendorkey; }
    public void setVendorkey(String vendorkey) { this.vendorkey = vendorkey; }

    public double getVendordiscountamount() { return vendordiscountamount; }
    public void setVendordiscountamount(double vendordiscountamount) { this.vendordiscountamount = vendordiscountamount; }

    public JSONObject getJSONObjectForInsert() {
        JSONObject jsonObject = new JSONObject();
        try {

            if (couponkey != null) {
                jsonObject.put("couponkey", couponkey);
            }
            if (coupontype != null) {
                jsonObject.put("coupontype", coupontype);
            }
            if (mobileno != null) {
                jsonObject.put("mobileno", mobileno);
            }
            if (orderid != null) {
                jsonObject.put("orderid", orderid);
            }
            if (transactiondate != null) {
                jsonObject.put("transactiondate", transactiondate);
            }
            if (transactiontime != null) {
                jsonObject.put("transactiontime", transactiontime);
            }
            if (userkey != null) {
                jsonObject.put("userkey", userkey);
            }
            if (vendorkey != null) {
                jsonObject.put("vendorkey", vendorkey);
            }
            if (coupondiscountamount > 0) {
                jsonObject.put("coupondiscountamount", coupondiscountamount);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
}
