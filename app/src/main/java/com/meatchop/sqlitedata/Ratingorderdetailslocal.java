package com.meatchop.sqlitedata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.utils.TMCUtil;

import org.json.JSONObject;

@DatabaseTable(tableName = "Ratingorderdetailslocal")
public class Ratingorderdetailslocal {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String serverkey;
    @DatabaseField
    private String createdtime;
    @DatabaseField
    private int deliveryrating;
    @DatabaseField
    private String feedback;
    @DatabaseField
    private String itemrating;
    @DatabaseField
    private String orderid;
    @DatabaseField
    private String userkey;
    @DatabaseField
    private String usermobileno;
    @DatabaseField
    private String vendorkey;
    @DatabaseField
    private int qualityrating;

    public Ratingorderdetailslocal() {

    }

    public Ratingorderdetailslocal(JSONObject jsonObject) {
        try {
            if (!jsonObject.isNull("key")) {
                this.serverkey = jsonObject.getString("key");
            }
            if (!jsonObject.isNull("createdtime")) {
                this.createdtime = jsonObject.getString("createdtime");
            }
            if (!jsonObject.isNull("deliveryrating")) {
                this.deliveryrating = jsonObject.getInt("deliveryrating");
            }
            if (!jsonObject.isNull("feedback")) {
                this.feedback = jsonObject.getString("feedback");
            }
            if (!jsonObject.isNull("itemrating")) {
                this.itemrating = jsonObject.getString("itemrating");
            }
            if (!jsonObject.isNull("orderid")) {
                this.orderid = jsonObject.getString("orderid");
            }
            if (!jsonObject.isNull("userkey")) {
                this.userkey = jsonObject.getString("userkey");
            }
            if (!jsonObject.isNull("usermobileno")) {
                this.usermobileno = jsonObject.getString("usermobileno");
            }
            if (!jsonObject.isNull("vendorkey")) {
                this.vendorkey = jsonObject.getString("vendorkey");
            }
            if (!jsonObject.isNull("qualityrating")) {
                this.qualityrating = jsonObject.getInt("qualityrating");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setServerkey(String serverkey) { this.serverkey = serverkey; }
    public String getServerkey() { return serverkey; }

    public void setCreatedtime(String createdtime) { this.createdtime = createdtime; }
    public String getCreatedtime() { return createdtime; }

    public void setDeliveryrating(int deliveryrating) { this.deliveryrating = deliveryrating; }
    public int getDeliveryrating() { return deliveryrating; }

    public void setFeedback(String feedback) { this.feedback = feedback; }
    public String getFeedback() { return feedback; }

    public void setItemrating(String itemrating) { this.itemrating = itemrating; }
    public String getItemrating() { return itemrating; }

    public void setOrderid(String orderid) { this.orderid = orderid; }
    public String getOrderid() { return orderid; }

    public void setUserkey(String userkey) { this.userkey = userkey; }
    public String getUserkey() { return userkey; }

    public void setUsermobileno(String usermobileno) { this.usermobileno = usermobileno; }
    public String getUsermobileno() { return usermobileno; }

    public void setVendorkey(String vendorkey) { this.vendorkey = vendorkey; }
    public String getVendorkey() { return vendorkey; }

    public void setQualityrating(int qualityrating) { this.qualityrating = qualityrating; }
    public int getQualityrating() { return qualityrating; }

    public void update(DatabaseHelper dbHelper) {
        try {
            dbHelper.getRatingOrderDetailsLocalDao().update(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(DatabaseHelper dbHelper) {
        try {
            dbHelper.getRatingOrderDetailsLocalDao().createOrUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(DatabaseHelper dbHelper) {
        try {
            dbHelper.getRatingOrderDetailsLocalDao().delete(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
