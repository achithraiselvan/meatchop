package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.meatchop.utils.TMCUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Deliveryslotdetails implements Parcelable, Comparable<Deliveryslotdetails> {

    private String key;
    private String slotstarttime;
    private String slotendtime;
    private String vendorkey;
    private String slotname;
    private boolean isdefault;
    private double deliverycharge;
    private String status;
    private String deliverytime;
    private String slotdate;
    private boolean isselected;
    private String jsonobject;
    private double maximumdeliverydistance;
    private double defaultdeliverydistance;
    private double defaultdeliverycharge;
    private double deliverychargeperkm;

    public Deliveryslotdetails(String name, double deliverycharge) {
        this.slotname = name;
        this.deliverycharge = deliverycharge;
    }

    public Deliveryslotdetails(JSONObject jsonObject) {
        try {
            this.key = jsonObject.getString("key");
            this.slotstarttime = jsonObject.getString("slotstarttime");
            this.slotname = jsonObject.getString("slotname");
            this.slotendtime = jsonObject.getString("slotendtime");
            this.vendorkey = jsonObject.getString("vendorkey");
            this.isdefault = jsonObject.getBoolean("isdefault");
            this.deliverycharge = jsonObject.getDouble("deliverycharge");
            this.status = jsonObject.getString("status");
            this.deliverytime = jsonObject.getString("deliverytime");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Deliveryslotdetails(Parcel in) {
        key = in.readString();
        slotstarttime = in.readString();
        slotendtime = in.readString();
        vendorkey = in.readString();
        slotname = in.readString();
        isdefault = in.readByte() != 0;
        deliverycharge = in.readDouble();
        status = in.readString();
        deliverytime = in.readString();
        slotdate = in.readString();
        isselected = in.readByte() != 0;
        maximumdeliverydistance = in.readDouble();
        defaultdeliverydistance = in.readDouble();
        defaultdeliverycharge = in.readDouble();
        deliverychargeperkm = in.readDouble();
    }

    public static final Creator<Deliveryslotdetails> CREATOR = new Creator<Deliveryslotdetails>() {
        @Override
        public Deliveryslotdetails createFromParcel(Parcel in) {
            return new Deliveryslotdetails(in);
        }

        @Override
        public Deliveryslotdetails[] newArray(int size) {
            return new Deliveryslotdetails[size];
        }
    };

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setSlotstarttime(String slotstarttime) { this.slotstarttime = slotstarttime; }
    public String getSlotstarttime() { return slotstarttime; }

    public void setSlotendtime(String slotendtime) { this.slotendtime = slotendtime; }
    public String getSlotendtime() { return slotendtime; }

    public void setSlotdate(String slotdate) { this.slotdate = slotdate; }
    public String getSlotdate() { return slotdate; }

    public void setVendorkey(String vendorkey) { this.vendorkey = vendorkey; }
    public String getVendorkey() { return vendorkey; }

    public void setSlotname(String slotname) { this.slotname = slotname; }
    public String getSlotname() { return slotname; }

    public void setIsdefault(boolean isdefault) { this.isdefault = isdefault; }
    public boolean getIsdefault() { return isdefault; }

    public void setDeliverycharge(double deliverycharge) { this.deliverycharge = deliverycharge; }
    public double getDeliverycharge() { return deliverycharge; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setDeliverytime(String deliverytime) { this.deliverytime = deliverytime; }
    public String getDeliverytime() { return deliverytime; }

    public void setMaximumdeliverydistance(double maximumdeliverydistance) { this.maximumdeliverydistance = maximumdeliverydistance; }
    public double getMaximumdeliverydistance() { return maximumdeliverydistance; }

    public void setDefaultdeliverydistance(double defaultdeliverydistance) { this.defaultdeliverydistance = defaultdeliverydistance; }
    public double getDefaultdeliverydistance() { return defaultdeliverydistance; }

    public void setDefaultdeliverycharge(double defaultdeliverycharge) { this.defaultdeliverycharge = defaultdeliverycharge; }
    public double getDefaultdeliverycharge() { return defaultdeliverycharge; }

    public void setDeliverychargeperkm(double deliverychargeperkm) { this.deliverychargeperkm = deliverychargeperkm; }
    public double getDeliverychargeperkm() { return deliverychargeperkm; }

    public String getJsonString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", key);
            jsonObject.put("slotstarttime", slotstarttime);
            jsonObject.put("slotendtime", slotendtime);
            jsonObject.put("vendorkey", vendorkey);
            jsonObject.put("slotname", slotname);
            jsonObject.put("isdefault", isdefault);
            jsonObject.put("deliverycharge", deliverycharge);
            jsonObject.put("status", status);
            jsonObject.put("deliverytime", deliverytime);
            jsonObject.put("maximumdeliverydistance", maximumdeliverydistance);
            jsonObject.put("defaultdeliverydistance", defaultdeliverydistance);
            jsonObject.put("defaultdeliverycharge", defaultdeliverycharge);
            jsonObject.put("deliverychargeperkm", deliverychargeperkm);
            return jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public void setIsselected(boolean isselected) { this.isselected = isselected; }
    public boolean getIsselected() { return this.isselected; }

    public long getDateInLong() {
        try {
            if (this.slotname.contains("Delivery")) {
                return 0;
            }
            String datetimestr = this.slotdate + " " + this.slotstarttime;
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
            Date date = sdf.parse(datetimestr);
            return date.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public int compareTo(Deliveryslotdetails deliveryslotdetails) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
            String objdatetimestr = TMCUtil.getInstance().getCurrentDate() + " " + deliveryslotdetails.getSlotstarttime();
            String thisdatestr = TMCUtil.getInstance().getCurrentDate() + " " + this.getSlotstarttime();

            Date objdate = sdf.parse(objdatetimestr);
            Date thisdate = sdf.parse(thisdatestr);
            int comparetovalue = thisdate.compareTo(objdate);
         // return objdate.compareTo(thisdate);
            return thisdate.compareTo(objdate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(slotstarttime);
        parcel.writeString(slotendtime);
        parcel.writeString(vendorkey);
        parcel.writeString(slotname);
        parcel.writeByte((byte) (isdefault ? 1 : 0));
        parcel.writeDouble(deliverycharge);
        parcel.writeString(status);
        parcel.writeString(deliverytime);
        parcel.writeString(slotdate);
        parcel.writeByte((byte) (isselected ? 1 : 0));
        parcel.writeDouble(maximumdeliverydistance);
        parcel.writeDouble(defaultdeliverycharge);
        parcel.writeDouble(defaultdeliverydistance);
        parcel.writeDouble(deliverychargeperkm);
    }
}
