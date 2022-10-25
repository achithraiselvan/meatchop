package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Deliveryslabdetails implements Parcelable{

    private String key;
    private String vendorkey;
    private double minkms;
    private double maxkms;
    private double minordervalue;
    private double deliverycharge;
    private JSONObject jsonObject;

    public Deliveryslabdetails(String key, String vendorkey, double minkms, double maxkms,
                               double minordervalue, double deliverycharges) {
        this.key = key;
        this.vendorkey = vendorkey;
        this.minkms = minkms;
        this.maxkms = maxkms;
        this.minordervalue = minordervalue;
        this.deliverycharge = deliverycharges;
    }

    public Deliveryslabdetails(JSONObject jsonObject) {
        try {
            this.key = jsonObject.getString("key");
            this.vendorkey = jsonObject.getString("vendorkey");
            this.minkms = jsonObject.getDouble("minkms");
            this.maxkms = jsonObject.getDouble("maxkms");
            this.minordervalue = jsonObject.getDouble("minordervalue");
            this.deliverycharge = jsonObject.getDouble("deliverycharge");
            this.jsonObject = jsonObject;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public boolean isRange(double userdeliverydistance) {
        // userdeliverydistance = 3.5, minkms = 3, maxkms = 5    // True
        // userdeliverydistance = 1.5, minkms = 0, maxkms = 3   // True
        // userdeliverydistance = 3, minkms = 3, maxkms = 5    // False
        // userdeliverydistance = 3, minkms = 0, maxkms = 3   // True
        if ((userdeliverydistance > minkms) && (userdeliverydistance <= maxkms)) {
            return true;
        } else {
            return false;
        }
    }

    protected Deliveryslabdetails(Parcel in) {
        key = in.readString();
        vendorkey = in.readString();
        minkms = in.readDouble();
        maxkms = in.readDouble();
        minordervalue = in.readDouble();
        deliverycharge = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(vendorkey);
        dest.writeDouble(minkms);
        dest.writeDouble(maxkms);
        dest.writeDouble(minordervalue);
        dest.writeDouble(deliverycharge);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Deliveryslabdetails> CREATOR = new Creator<Deliveryslabdetails>() {
        @Override
        public Deliveryslabdetails createFromParcel(Parcel in) {
            return new Deliveryslabdetails(in);
        }

        @Override
        public Deliveryslabdetails[] newArray(int size) {
            return new Deliveryslabdetails[size];
        }
    };

    public String getKey() { return this.key; }
    public String getVendorkey() { return this.vendorkey; }
    public double getMinkms() { return this.minkms; }
    public double getMaxkms() { return this.maxkms; }
    public double getMinordervalue() { return this.minordervalue; }
    public double getDeliverycharges() { return this.deliverycharge; }

}
