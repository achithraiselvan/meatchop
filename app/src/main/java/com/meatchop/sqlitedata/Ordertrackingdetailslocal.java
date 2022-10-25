package com.meatchop.sqlitedata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.utils.TMCUtil;

import org.json.JSONArray;
import org.json.JSONObject;

@DatabaseTable(tableName = "Ordertrackingdetailslocal")
public class Ordertrackingdetailslocal {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String serverkey;
    @DatabaseField
    private String orderreadytime;
    @DatabaseField
    private String orderpickeduptime;
    @DatabaseField
    private String ordercancelledtime;
    @DatabaseField(unique = true)
    private String orderid;
    @DatabaseField
    private double deliveryuserlat = 0;
    @DatabaseField
    private double deliveryuserlong = 0;
    @DatabaseField
    private String orderstatus;
    @DatabaseField
    private String deliveryusermobileno;
    @DatabaseField
    private String deliveryuserkey;
    @DatabaseField
    private String orderconfirmedtime;
    @DatabaseField
    private String orderplacedtime;
    @DatabaseField
    private String vendorkey;
    @DatabaseField
    private String deliveryusername;
    @DatabaseField
    private String orderdeliverytime;
    @DatabaseField
    private long orderplacedtimeinlong;

    public Ordertrackingdetailslocal() {

    }

    public Ordertrackingdetailslocal(JSONObject jsonObject) {
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
            if (!jsonObject.isNull("orderid")) {
                this.orderid = jsonObject.getString("orderid");
            }
            if (!jsonObject.isNull("orderstatus")) {
                this.orderstatus = jsonObject.getString("orderstatus");
            }
            if (!jsonObject.isNull("orderplacedtime")) {
                this.orderplacedtime = jsonObject.getString("orderplacedtime");
                this.orderplacedtimeinlong = TMCUtil.getInstance().getTimeInLong(orderplacedtime);
            }
            if (!jsonObject.isNull("vendorkey")) {
                this.vendorkey = jsonObject.getString("vendorkey");
            }
            if (!jsonObject.isNull("key")) {
                this.serverkey = jsonObject.getString("key");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateJsonObject(JSONObject jsonObject) {
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
            if (!jsonObject.isNull("orderstatus")) {
                this.orderstatus = jsonObject.getString("orderstatus");
            }
            if (!jsonObject.isNull("vendorkey")) {
                this.vendorkey = jsonObject.getString("vendorkey");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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

    public void setServerkey(String serverkey) { this.serverkey = serverkey; }
    public String getServerkey() { return serverkey; }

    public void setOrderdeliverytime(String orderdeliverytime) { this.orderdeliverytime = orderdeliverytime; }
    public String getOrderdeliverytime() { return orderdeliverytime; }

    public void update(DatabaseHelper dbHelper) {
        try {
            dbHelper.getOrdertrackingDetailsLocalDao().update(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(DatabaseHelper dbHelper) {
        try {
            dbHelper.getOrdertrackingDetailsLocalDao().createOrUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(DatabaseHelper dbHelper) {
        try {
            dbHelper.getOrdertrackingDetailsLocalDao().delete(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
