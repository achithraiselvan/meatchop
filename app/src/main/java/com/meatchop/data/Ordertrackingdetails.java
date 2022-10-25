package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class Ordertrackingdetails implements Parcelable {

    private String orderreadytime;
    private String orderpickeduptime;
    private String ordercancelledtime;
    private String orderid;
    private double deliveryuserlat = 0;
    private double deliveryuserlong = 0;
    private String orderstatus;
    private String deliveryusermobileno;
    private String deliveryuserkey;
    private String orderconfirmedtime;
    private String orderplacedtime;
    private String vendorkey;
    private String deliveryusername;
    private String key;
    private String orderdeliverytime;
    private String jsonobject;
    private String slotdate;

    public Ordertrackingdetails(JSONObject jsonObject) {
        try {
            if (!jsonObject.isNull("orderreadytime")) {
                this.orderreadytime = jsonObject.getString("orderreadytime");
            }
            if (!jsonObject.isNull("orderpickeduptime")) {
                this.orderpickeduptime = jsonObject.getString("orderpickeduptime");
            }
            if (!jsonObject.isNull("ordercancelledtime")) {
                this.ordercancelledtime = jsonObject.getString("ordercancelledtime");
            }
            if (!jsonObject.isNull("deliveryusermobileno")) {
                this.deliveryusermobileno = jsonObject.getString("deliveryusermobileno");
            }
            if (!jsonObject.isNull("deliveryuserlong")) {
                this.deliveryuserlong = jsonObject.getDouble("deliveryuserlong");
            }
            if (!jsonObject.isNull("deliveryuserlat")) {
                this.deliveryuserlat = jsonObject.getDouble("deliveryuserlat");
            }
            if (!jsonObject.isNull("deliveryuserkey")) {
                this.deliveryuserkey = jsonObject.getString("deliveryuserkey");
            }
            if (!jsonObject.isNull("orderconfirmedtime")) {
                this.orderconfirmedtime = jsonObject.getString("orderconfirmedtime");
            }
            if (!jsonObject.isNull("deliveryusername")) {
                this.deliveryusername = jsonObject.getString("deliveryusername");
            }
            if (!jsonObject.isNull("orderdeliverytime")) {
                this.orderdeliverytime = jsonObject.getString("orderdeliverytime");
            }
            this.orderid = jsonObject.getString("orderid");
            this.orderstatus = jsonObject.getString("orderstatus");
            if (!jsonObject.isNull("orderplacedtime")) {
                this.orderplacedtime = jsonObject.getString("orderplacedtime");
            }
            if (!jsonObject.isNull("vendorkey")) {
                this.vendorkey = jsonObject.getString("vendorkey");
            }
            if (!jsonObject.isNull("slotdate")) {
                this.slotdate = jsonObject.getString("slotdate");
            }
            if (!jsonObject.isNull("key")) {
                this.key = jsonObject.getString("key");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Ordertrackingdetails(String orderid, String orderstatus, String orderplacedtime, String vendorkey) {
        this.orderid = orderid;
        this.orderstatus = orderstatus;
        this.orderplacedtime = orderplacedtime;
        this.vendorkey = vendorkey;
    }

    protected Ordertrackingdetails(Parcel in) {
        orderreadytime = in.readString();
        orderpickeduptime = in.readString();
        ordercancelledtime = in.readString();
        orderid = in.readString();
        deliveryuserlat = in.readDouble();
        deliveryuserlong = in.readDouble();
        orderstatus = in.readString();
        deliveryusermobileno = in.readString();
        deliveryuserkey = in.readString();
        orderconfirmedtime = in.readString();
        orderplacedtime = in.readString();
        vendorkey = in.readString();
        deliveryusername = in.readString();
        key = in.readString();
        orderdeliverytime = in.readString();
        slotdate = in.readString();
    }

    public String getJSONString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderreadytime", orderreadytime);
            jsonObject.put("orderpickeduptime", orderpickeduptime);
            jsonObject.put("ordercancelledtime", ordercancelledtime);
            jsonObject.put("orderid", orderid);
            jsonObject.put("deliveryuserlat", deliveryuserlat);
            jsonObject.put("deliveryuserlong", deliveryuserlong);
            jsonObject.put("orderstatus", orderstatus);
            jsonObject.put("deliveryusermobileno", deliveryusermobileno);
            jsonObject.put("deliveryuserkey", deliveryuserkey);
            jsonObject.put("orderconfirmedtime", orderconfirmedtime);
            jsonObject.put("orderplacedtime", orderplacedtime);
            jsonObject.put("vendorkey", vendorkey);
            jsonObject.put("deliveryusername", deliveryusername);
            jsonObject.put("key", key);
            jsonObject.put("orderdeliverytime", orderdeliverytime);
            jsonObject.put("slotdate", slotdate);
            return jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static final Creator<Ordertrackingdetails> CREATOR = new Creator<Ordertrackingdetails>() {
        @Override
        public Ordertrackingdetails createFromParcel(Parcel in) {
            return new Ordertrackingdetails(in);
        }

        @Override
        public Ordertrackingdetails[] newArray(int size) {
            return new Ordertrackingdetails[size];
        }
    };

    public void setOrderreadytime(String orderreadytime) { this.orderreadytime = orderreadytime; }
    public String getOrderreadytime() { return orderreadytime; }

    public void setOrderpickeduptime(String orderpickeduptime) { this.orderpickeduptime = orderpickeduptime; }
    public String getOrderpickeduptime() { return orderpickeduptime; }

    public void setOrdercancelledtime(String ordercancelledtime) { this.ordercancelledtime = ordercancelledtime; }
    public String getOrdercancelledtime() { return ordercancelledtime; }

    public void setOrderid(String orderid) { this.orderid = orderid; }
    public String getOrderid() { return orderid; }

    public void setDeliveryuserlat(double deliveryuserlat) { this.deliveryuserlat = deliveryuserlat; }
    public double getDeliveryuserlat() { return deliveryuserlat; }

    public void setDeliveryuserlong(double deliveryuserlong) { this.deliveryuserlong = deliveryuserlong; }
    public double getDeliveryuserlong() { return deliveryuserlong; }

    public void setOrderstatus(String orderstatus) { this.orderstatus = orderstatus; }
    public String getOrderstatus() { return orderstatus; }

    public void setDeliveryusermobileno(String deliveryusermobileno) { this.deliveryusermobileno = deliveryusermobileno; }
    public String getDeliveryusermobileno() { return deliveryusermobileno; }

    public void setDeliveryuserkey(String deliveryuserkey) { this.deliveryuserkey = deliveryuserkey; }
    public String getDeliveryuserkey() { return deliveryuserkey; }

    public void setOrderconfirmedtime(String orderconfirmedtime) { this.orderconfirmedtime = orderconfirmedtime; }
    public String getOrderconfirmedtime() { return orderconfirmedtime; }

    public void setOrderplacedtime(String orderplacedtime) { this.orderplacedtime = orderplacedtime; }
    public String getOrderplacedtime() { return orderplacedtime; }

    public void setVendorkey(String vendorkey) { this.vendorkey = vendorkey; }
    public String getVendorkey() { return vendorkey; }

    public void setDeliveryusername(String deliveryusername) { this.deliveryusername = deliveryusername; }
    public String getDeliveryusername() { return deliveryusername; }

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setOrderdeliverytime(String orderdeliverytime) { this.orderdeliverytime = orderdeliverytime; }
    public String getOrderdeliverytime() { return orderdeliverytime; }

    public void setSlotdate(String slotdate) { this.slotdate = slotdate; }
    public String getSlotdate() { return slotdate; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(orderreadytime);
        parcel.writeString(orderpickeduptime);
        parcel.writeString(ordercancelledtime);
        parcel.writeString(orderid);
        parcel.writeDouble(deliveryuserlat);
        parcel.writeDouble(deliveryuserlong);
        parcel.writeString(orderstatus);
        parcel.writeString(deliveryusermobileno);
        parcel.writeString(deliveryuserkey);
        parcel.writeString(orderconfirmedtime);
        parcel.writeString(orderplacedtime);
        parcel.writeString(vendorkey);
        parcel.writeString(deliveryusername);
        parcel.writeString(key);
        parcel.writeString(orderdeliverytime);
        parcel.writeString(slotdate);
    }
}
