package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class TMCUserAddress implements Parcelable {

    private String key;
    private String name;
    private String addressline1;
    private String addressline2;
    private String addresstype;
    private double locationlat = 0;
    private double locationlong = 0;
    private String userkey;
    private boolean isdefault = false;
    private String landmark;
    private String vendorkey;
    private String vendorname;
    private String pincode;
    private JSONObject jsonObject;
    private double deliverydistance = 0;
    private boolean isvendorremapping = false;

    public TMCUserAddress() {

    }

    public TMCUserAddress(JSONObject jsonObject) {
        try {
            this.jsonObject = jsonObject;

            if (jsonObject.has("key")) {
                this.key = jsonObject.getString("key");
            }
            if (jsonObject.has("name")) {
                this.name = jsonObject.getString("name");
            }
            if (jsonObject.has("userkey")) {
                this.userkey = jsonObject.getString("userkey");
            }
            if (jsonObject.has("addresstype")) {
                this.addresstype = jsonObject.getString("addresstype");
            }
            if (jsonObject.has("addressline1")) {
                this.addressline1 = jsonObject.getString("addressline1");
            }
            if (jsonObject.has("addressline2")) {
                this.addressline2 = jsonObject.getString("addressline2");
            }
            if (jsonObject.has("landmark")) {
                this.landmark = jsonObject.getString("landmark");
            }
            if (jsonObject.has("locationlong")) {
                this.locationlong = jsonObject.getDouble("locationlong");
            }
            if (jsonObject.has("locationlat")) {
                this.locationlat = jsonObject.getDouble("locationlat");
            }
            if (jsonObject.has("pincode")) {
                this.pincode = jsonObject.getString("pincode");
            }
            if (jsonObject.has("vendorkey")) {
                this.vendorkey = jsonObject.getString("vendorkey");
            }
            if (jsonObject.has("vendorname")) {
                this.vendorname = jsonObject.getString("vendorname");
            }

            if (jsonObject.has("deliverydistance")) {
                Object deldistanceobj = jsonObject.get("deliverydistance");
                String deldiststr = jsonObject.getString("deliverydistance");
                this.deliverydistance = Double.parseDouble(deldiststr);
             /* if (deldistanceobj instanceof Double) {
                    this.deliverydistance = jsonObject.getDouble("deliverydistance");
                } */
            }

            if (jsonObject.has("isvendorremapping")) {
                try {
                    this.isvendorremapping = jsonObject.getBoolean("isvendorremapping");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected TMCUserAddress(Parcel in) {
        key = in.readString();
        addressline1 = in.readString();
        addressline2 = in.readString();
        addresstype = in.readString();
        locationlat = in.readDouble();
        locationlong = in.readDouble();
        userkey = in.readString();
        isdefault = in.readByte() != 0;
        landmark = in.readString();
        vendorkey = in.readString();
        vendorname = in.readString();
        pincode = in.readString();
        name = in.readString();
        deliverydistance = in.readDouble();
        isvendorremapping = in.readByte() != 0;
    }

    public static final Creator<TMCUserAddress> CREATOR = new Creator<TMCUserAddress>() {
        @Override
        public TMCUserAddress createFromParcel(Parcel in) {
            return new TMCUserAddress(in);
        }

        @Override
        public TMCUserAddress[] newArray(int size) {
            return new TMCUserAddress[size];
        }
    };

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setUserkey(String userkey) { this.userkey = userkey; }
    public String getUserkey() { return userkey; }

    public void setAddresstype(String addresstype) { this.addresstype = addresstype; }
    public String getAddresstype() { return addresstype; }

    public void setIsdefault(boolean isdefault) { this.isdefault = isdefault; }
    public boolean getIsdefault() { return isdefault; }

    public void setAddressline1(String addressline1) { this.addressline1 = addressline1; }
    public String getAddressline1() { return addressline1; }

    public void setAddressline2(String addressline2) { this.addressline2 = addressline2; }
    public String getAddressline2() { return addressline2; }

    public void setLandmark(String landmark) { this.landmark = landmark; }
    public String getLandmark() { return landmark; }

    public void setLocationlat(double locationlat) { this.locationlat = locationlat; }
    public double getLocationlat() { return locationlat; }

    public void setLocationlong(double locationlong) { this.locationlong = locationlong; }
    public double getLocationlong() { return locationlong; }

    public void setVendorkey(String vendorkey) { this.vendorkey = vendorkey; }
    public String getVendorkey() { return vendorkey; }

    public void setVendorname(String vendorname) { this.vendorname = vendorname; }
    public String getVendorname() { return vendorname; }

    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getPincode() { return pincode; }

    public void setDeliverydistance(double deliverydistance) { this.deliverydistance = deliverydistance; }
    public double getDeliverydistance() { return deliverydistance; }

    public boolean isVendorremapping() { return isvendorremapping; }
    public void setVendorRemapping(boolean isvendorremapping) { this.isvendorremapping = isvendorremapping; }

    public String getJsonString() {
        try {
         /* if (this.jsonObject != null) {
                return this.jsonObject.toString();
            } */
            JSONObject jsonObject1 = new JSONObject();
            if (key != null) {
                jsonObject1.put("key", key);
            }
            if (name != null) {
                jsonObject1.put("name", name);
            }
            if (userkey != null) {
                jsonObject1.put("userkey", userkey);
            }
            if (addresstype != null) {
                jsonObject1.put("addresstype", addresstype);
            }
            jsonObject1.put("isdefault", isdefault);
            if (addressline1 != null) {
                jsonObject1.put("addressline1", addressline1);
            }
            if (addressline2 != null) {
                jsonObject1.put("addressline2", addressline2);
            }
            if (landmark != null) {
                jsonObject1.put("landmark", landmark);
            }
            if (locationlat >= 0) {
                jsonObject1.put("locationlat", locationlat);
            }
            if (locationlong >= 0) {
                jsonObject1.put("locationlong", locationlong);
            }
            if (vendorkey != null) {
                jsonObject1.put("vendorkey", vendorkey);
            }
            if (vendorname != null) {
                jsonObject1.put("vendorname", vendorname);
            }
            if (pincode != null) {
                jsonObject1.put("pincode", pincode);
            }
            if (deliverydistance > 0) {
                jsonObject1.put("deliverydistance", deliverydistance);
            }
            jsonObject1.put("isvendorremapping", isvendorremapping);
            return jsonObject1.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(addressline1);
        parcel.writeString(addressline2);
        parcel.writeString(addresstype);
        parcel.writeDouble(locationlat);
        parcel.writeDouble(locationlong);
        parcel.writeString(userkey);
        parcel.writeByte((byte) (isdefault ? 1 : 0));
        parcel.writeString(landmark);
        parcel.writeString(vendorkey);
        parcel.writeString(vendorname);
        parcel.writeString(pincode);
        parcel.writeDouble(deliverydistance);
        parcel.writeString(name);
        parcel.writeByte((byte) (isvendorremapping ? 1 : 0));
    }
}
