package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Coupondetails implements Comparable<Coupondetails>, Parcelable {

    // Coupon Types
    public static final String TYPE_FLATVALUE = "FLATVALUE";
    public static final String TYPE_FLATPERCENT = "FLATPERCENTAGE";
    public static final String TYPE_SKUDISCOUNT = "SKUDISCOUNT";

    public static final String TYPE_CASHBACKVAL = "CASHBACKVALUE";
    public static final String TYPE_CASHBACKPERCENT = "CASHBACKPERCENTAGE";
    public static final String USERTYPE_NEW = "NEW";
    public static final String USERTYPE_ALL = "ALL";
    public static final String USERTYPE_GROUP = "USERGROUP";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String TYPE_SKUFLATVALUE = "SKUFLATVALUE";
    public static final String TYPE_SKUFLATPERCENT = "SKUFLATPERCENTAGE";

    private String key;
    private String code;
    private String title;
    private String subtitle;
    private double value;
    private String type;
    private String sponsor;
    private String usertype;
    private int redeemcount;
    private double maxdiscountvalue;
    private double minimumordervalue;
    private String createddate;
    private String status;
    private String expirydate;
    private String facilityqrcode;
    private int displayno;
    private boolean companypaidcoupon;
    private String vendornameqrcodes;
    private String usergroupname;
    private String cafeteria;
    private String couponcode;
    private String availfrequency;
    private String menuitemkeys;
    private String alertmessage;
    private String vendorkeys;
    private String skudetails;


    public Coupondetails() {

    }

    public Coupondetails(JSONObject jsonObject) {
        try {
            if (jsonObject.has("maxdiscountvalue")) {
                this.maxdiscountvalue = jsonObject.getDouble("maxdiscountvalue");
            }
            if (jsonObject.has("redeemcount")) {
                this.redeemcount = jsonObject.getInt("redeemcount");
            }
            if (jsonObject.has("expirydate")) {
                this.expirydate = jsonObject.getString("expirydate");
            }
            if (jsonObject.has("status")) {
                this.status = jsonObject.getString("status");
            }
            if (jsonObject.has("displayno")) {
                this.displayno = jsonObject.getInt("displayno");
            }
            if (jsonObject.has("subtitle")) {
                this.subtitle = jsonObject.getString("subtitle");
            }
            if (jsonObject.has("createddate")) {
                this.createddate = jsonObject.getString("createddate");
            }
            if (jsonObject.has("value")) {
                this.value = jsonObject.getDouble("value");
            }
            if (jsonObject.has("minimumordervalue")) {
                this.minimumordervalue = jsonObject.getDouble("minimumordervalue");
            }
            if (jsonObject.has("couponcode")) {
                this.couponcode = jsonObject.getString("couponcode");
            }
            if (jsonObject.has("usertype")) {
                this.usertype = jsonObject.getString("usertype");
            }
            if (jsonObject.has("availfrequency")) {
                this.availfrequency = jsonObject.getString("availfrequency");
            }
            if (jsonObject.has("key")) {
                this.key = jsonObject.getString("key");
            }
            if (jsonObject.has("title")) {
                this.title = jsonObject.getString("title");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("menuitemkeys")) {
                this.menuitemkeys = jsonObject.getString("menuitemkeys");
            }
            if (jsonObject.has("alertmessage")) {
                this.alertmessage = jsonObject.getString("alertmessage");
            }
            if (jsonObject.has("vendorkeys")) {
                this.vendorkeys = jsonObject.getString("vendorkeys");
            }
            if (jsonObject.has("skudetails")) {
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("skudetails");
                    this.skudetails = jsonObject1.toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Coupondetails(String id, String code, String title, double value, String type, String sponsor,
                         String usertype, int redeemcount, double maxdiscountvalue, double minimumordervalue,
                         String createddate, String status, String expirydate, String facilityqrcode) {
        this.code = code;
        this.title = title;
        this.value = value;
        this.type = type;
        this.sponsor = sponsor;
        this.usertype = usertype;
        this.redeemcount = redeemcount;
        this.maxdiscountvalue = maxdiscountvalue;
        this.minimumordervalue = minimumordervalue;
        this.createddate = createddate;
        this.status = status;
        this.expirydate = expirydate;
        this.facilityqrcode = facilityqrcode;
    }

    protected Coupondetails(Parcel in) {
        key = in.readString();
        code = in.readString();
        title = in.readString();
        subtitle = in.readString();
        value = in.readDouble();
        type = in.readString();
        sponsor = in.readString();
        usertype = in.readString();
        redeemcount = in.readInt();
        maxdiscountvalue = in.readDouble();
        minimumordervalue = in.readDouble();
        createddate = in.readString();
        status = in.readString();
        expirydate = in.readString();
        facilityqrcode = in.readString();
        displayno = in.readInt();
        companypaidcoupon = in.readByte() != 0;
        vendornameqrcodes = in.readString();
        usergroupname = in.readString();
        cafeteria = in.readString();
        couponcode = in.readString();
        availfrequency = in.readString();
        menuitemkeys = in.readString();
        alertmessage = in.readString();
        vendorkeys = in.readString();
        skudetails = in.readString();
    }

    public static final Creator<Coupondetails> CREATOR = new Creator<Coupondetails>() {
        @Override
        public Coupondetails createFromParcel(Parcel in) {
            return new Coupondetails(in);
        }

        @Override
        public Coupondetails[] newArray(int size) {
            return new Coupondetails[size];
        }
    };

    public String getKey() { return key; }
    public void setKey(String id) { this.key = key; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSponsor() { return sponsor; }
    public void setSponsor(String sponsor) { this.sponsor = sponsor; }

    public String getUsertype() { return usertype; }
    public void setUsertype(String usertype) { this.usertype = usertype; }

    public int getRedeemcount() { return redeemcount; }
    public void setRedeemcount(int redeemcount) { this.redeemcount = redeemcount; }

    public double getMaxdiscountvalue() { return maxdiscountvalue; }
    public void setMaxdiscountvalue(double maxdiscountvalue) { this.maxdiscountvalue = maxdiscountvalue; }

    public double getMinimumordervalue() { return minimumordervalue; }
    public void setMinimumordervalue(double minimumordervalue) { this.minimumordervalue = minimumordervalue; }

    public String getCreateddate() { return createddate; }
    public void setCreateddate(String createddate) { this.createddate = createddate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getExpirydate() { return expirydate; }
    public void setExpirydate(String expirydate) { this.expirydate = expirydate; }

    public String getFacilityqrcode() { return facilityqrcode; }
    public void setFacilityqrcode(String facilityqrcode) { this.facilityqrcode = facilityqrcode; }

    public int getDisplayno() { return displayno; }
    public void setDisplayno(int displayno) { this.displayno = displayno; }

    public boolean getCompanypaidcoupon() { return companypaidcoupon; }
    public void setCompanypaidcoupon(boolean companypaidcoupon) { this.companypaidcoupon = companypaidcoupon; }

    public String getVendornameqrcodes() { return vendornameqrcodes; }
    public void setVendornameqrcodes(String vendornameqrcodes) { this.vendornameqrcodes = vendornameqrcodes; }

    public String getUsergroupname() { return usergroupname; }
    public void setUsergroupname(String usergroupname) { this.usergroupname = usergroupname; }

    public String getCafeteria() { return cafeteria; }
    public void setCafeteria(String cafeteria) { this.cafeteria = cafeteria; }

    public String getCouponcode() { return couponcode; }
    public void setCouponcode(String couponcode) { this.couponcode = couponcode; }

    public String getAvailfrequency() { return availfrequency; }
    public void setAvailfrequency(String availfrequency) { this.availfrequency = availfrequency; }

    public String getSkudetails() { return skudetails; }
    public void setSkudetails(String skudetails) { this.skudetails = skudetails; }

    public String getMenuitemkeys() { return menuitemkeys; }
    public String getAlertmessage() { return alertmessage; }
    public String getVendorkeys() { return this.vendorkeys; }

    public ArrayList<String> getMenuItemKeysList() {
        if ((menuitemkeys == null) || (TextUtils.isEmpty(menuitemkeys))) {
            return null;
        }
        String[] keys = menuitemkeys.split(",");
        ArrayList<String> menuitemkeyslist = new ArrayList<String>();
        for (int i=0; i<keys.length; i++) {
            String key = keys[i];
            menuitemkeyslist.add(key);
        }
        return menuitemkeyslist;
    }

    @Override
    public int compareTo(Coupondetails coupondetails) {
        long objno = coupondetails.getDisplayno();
        long currobjno = this.getDisplayno();
        return (int) (currobjno - objno);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(code);
        parcel.writeString(title);
        parcel.writeString(subtitle);
        parcel.writeDouble(value);
        parcel.writeString(type);
        parcel.writeString(sponsor);
        parcel.writeString(usertype);
        parcel.writeInt(redeemcount);
        parcel.writeDouble(maxdiscountvalue);
        parcel.writeDouble(minimumordervalue);
        parcel.writeString(createddate);
        parcel.writeString(status);
        parcel.writeString(expirydate);
        parcel.writeString(facilityqrcode);
        parcel.writeInt(displayno);
        parcel.writeByte((byte) (companypaidcoupon ? 1 : 0));
        parcel.writeString(vendornameqrcodes);
        parcel.writeString(usergroupname);
        parcel.writeString(cafeteria);
        parcel.writeString(couponcode);
        parcel.writeString(availfrequency);
        parcel.writeString(menuitemkeys);
        parcel.writeString(alertmessage);
        parcel.writeString(vendorkeys);
        parcel.writeString(skudetails);
    }
}
