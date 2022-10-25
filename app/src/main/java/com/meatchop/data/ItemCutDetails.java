package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONObject;

public class ItemCutDetails implements Comparable<ItemCutDetails>, Parcelable {

    private String cutkey;
    private String cutname;
    private String cutdesp;
    private String cutimagename;
    private int cutdisplayno;
    private boolean isdefault = false;
    private double netweightingrams;
    private String netweight;
    private double cutprice;
    private String portionsize;

    public ItemCutDetails(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return;
            }
            if (!jsonObject.isNull("cutkey")) {
                this.cutkey = jsonObject.getString("cutkey");
            }
            if (!jsonObject.isNull("cutname")) {
                this.cutname = jsonObject.getString("cutname");
            }
            if (!jsonObject.isNull("cutdesp")) {
                this.cutdesp = jsonObject.getString("cutdesp");
            }
            if (!jsonObject.isNull("cutimagename")) {
                this.cutimagename = jsonObject.getString("cutimagename");
            }
            if (!jsonObject.isNull("cutdisplayno")) {
                this.cutdisplayno = jsonObject.getInt("cutdisplayno");
            }
            if (!jsonObject.isNull("isdefault")) {
                this.isdefault = jsonObject.getBoolean("isdefault");
            }
            if (!jsonObject.isNull("netweightingrams")) {
                this.netweightingrams = jsonObject.getDouble("netweightingrams");
            }
            if (!jsonObject.isNull("netweight")) {
                this.netweight = jsonObject.getString("netweight");
            }
            if (!jsonObject.isNull("cutprice")) {
                this.cutprice = jsonObject.getDouble("cutprice");
            }
            if (!jsonObject.isNull("portionsize")) {
                this.portionsize = jsonObject.getString("portionsize");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected ItemCutDetails(Parcel in) {
        cutkey = in.readString();
        cutname = in.readString();
        cutdesp = in.readString();
        cutimagename = in.readString();
        cutdisplayno = in.readInt();
        isdefault = in.readByte() != 0;
        netweightingrams = in.readDouble();
        netweight = in.readString();
        cutprice = in.readDouble();
        portionsize = in.readString();
    }

    public static final Creator<ItemCutDetails> CREATOR = new Creator<ItemCutDetails>() {
        @Override
        public ItemCutDetails createFromParcel(Parcel in) {
            return new ItemCutDetails(in);
        }

        @Override
        public ItemCutDetails[] newArray(int size) {
            return new ItemCutDetails[size];
        }
    };

    public void setCutkey(String cutkey) { this.cutkey = cutkey; }
    public String getCutkey() { return cutkey; }

    public void setCutname(String cutname) { this.cutname = cutname; }
    public String getCutname() { return cutname; }

    public void setCutdesp(String cutdesp) { this.cutdesp = cutdesp; }
    public String getCutdesp() { return cutdesp; }

    public void setCutimagename(String cutimagename) { this.cutimagename = cutimagename; }
    public String getCutimagename() { return cutimagename; }

    public void setCutdisplayno(int cutdisplayno) { this.cutdisplayno = cutdisplayno; }
    public int getCutdisplayno() { return cutdisplayno; }

    public boolean isIsdefault() { return isdefault; }
    public void setIsdefault(boolean isdefault) { this.isdefault = isdefault; }

    public void setNetweightingrams(double netweightingrams) { this.netweightingrams = netweightingrams; }
    public double getNetweightingrams() { return netweightingrams; }

    public void setCutprice(double cutprice) { this.cutprice = cutprice; }
    public double getCutprice() { return cutprice; }

    public void setNetweight(String netweight) { this.netweight = netweight; }
    public String getNetweight() { return netweight; }

    public void setPortionsize(String portionsize) { this.portionsize = portionsize; }
    public String getPortionsize() { return portionsize; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cutkey);
        dest.writeString(cutname);
        dest.writeString(cutdesp);
        dest.writeString(cutimagename);
        dest.writeInt(cutdisplayno);
        dest.writeByte((byte) (isdefault ? 1 : 0));
        dest.writeDouble(netweightingrams);
        dest.writeDouble(cutprice);
        dest.writeString(netweight);
        dest.writeString(portionsize);
    }

    @Override
    public int compareTo(ItemCutDetails o) {
        try {
            /* For Ascending order do like this */
            return (this.cutdisplayno - o.getCutdisplayno());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public String getJSONString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cutkey", cutkey);
            jsonObject.put("cutname", cutname);
            jsonObject.put("netweightingrams", netweightingrams);
            jsonObject.put("netweight", netweight);
            if ((portionsize != null) && (!TextUtils.isEmpty(portionsize))) {
                jsonObject.put("portionsize", portionsize);
            }
            return jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
