package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class ItemWeightDetails implements Comparable<ItemWeightDetails>, Parcelable {

    private int displayno = 0;
    private String grossweight;
    private String netweight;
    private String portionsize;
    private double grossweightingrams;
    private double netweightingrams;
    private boolean isdefault;
    private String weightkey;
    private double itempriceperkg;
    private double itemprice;

    public ItemWeightDetails(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return;
            }
            if (!jsonObject.isNull("weightkey")) {
                this.weightkey = jsonObject.getString("weightkey");
            }
            if (!jsonObject.isNull("displayno")) {
                String displaynostr = jsonObject.getString("displayno");
                displaynostr = displaynostr.replaceAll(" ", "");
                if ((displaynostr != null) && !(TextUtils.isEmpty(displaynostr))) {
                    this.displayno = jsonObject.getInt("displayno");
                }
            }
            if (!jsonObject.isNull("isdefault")) {
                this.isdefault = jsonObject.getBoolean("isdefault");
            }
            if (!jsonObject.isNull("netweightingrams")) {
                this.netweightingrams = jsonObject.getDouble("netweightingrams");
            }
            if (!jsonObject.isNull("grossweightingrams")) {
                this.grossweightingrams = jsonObject.getDouble("grossweightingrams");
            }
            if (!jsonObject.isNull("grossweight")) {
                this.grossweight = jsonObject.getString("grossweight");
            }
            if (!jsonObject.isNull("netweight")) {
                this.netweight = jsonObject.getString("netweight");
            }
            if (!jsonObject.isNull("portionsize")) {
                this.portionsize = jsonObject.getString("portionsize");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected ItemWeightDetails(Parcel in) {
        displayno = in.readInt();
        grossweightingrams = in.readDouble();
        netweightingrams = in.readDouble();
        isdefault = in.readByte() != 0;
        weightkey = in.readString();
        itempriceperkg = in.readDouble();
        itemprice = in.readDouble();
        grossweight = in.readString();
        netweight = in.readString();
        portionsize = in.readString();
    }

    public String getJSONString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("weightkey", weightkey);
            jsonObject.put("grossweightingrams", grossweightingrams);
            jsonObject.put("netweightingrams", netweightingrams);
            jsonObject.put("itemprice", itemprice);
            jsonObject.put("grossweight", grossweight);
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

    public static final Creator<ItemWeightDetails> CREATOR = new Creator<ItemWeightDetails>() {
        @Override
        public ItemWeightDetails createFromParcel(Parcel in) {
            return new ItemWeightDetails(in);
        }

        @Override
        public ItemWeightDetails[] newArray(int size) {
            return new ItemWeightDetails[size];
        }
    };

    public void setWeightkey(String weightkey) { this.weightkey = weightkey; }
    public String getWeightkey() { return weightkey; }

    public void setWeightdisplayno(int displayno) { this.displayno = displayno; }
    public int getWeightdisplayno() { return displayno; }

    public void setGrossweightingrams(double grossweightingrams) { this.grossweightingrams = grossweightingrams; }
    public double getGrossweightingrams() { return grossweightingrams; }

    public void setNetweightingrams(double netweightingrams) { this.netweightingrams = netweightingrams; }
    public double getNetweightingrams() { return netweightingrams; }

    public void setItempriceperkg(double itempriceperkg) { this.itempriceperkg = itempriceperkg; }
    public double getItempriceperkg() { return itempriceperkg; }

    public void setGrossweight(String grossweight) { this.grossweight = grossweight; }
    public String getGrossweight() { return this.grossweight; }

    public void setNetweight(String netweight) { this.netweight = netweight; }
    public String getNetweight() { return this.netweight; }

    public void setPortionsize() { this.portionsize = portionsize; }
    public String getPortionsize() { return this.portionsize; }

    public double getItemprice() {
        Log.d("ItemWeightDetails", "itempriceperkg "+itempriceperkg);
        if (itempriceperkg <= 0) {
            return 0;
        }
        double itemprice = (itempriceperkg / 1000) * grossweightingrams;
        return itemprice;
    }

    public void setIsdefault(boolean isdefault) { this.isdefault = isdefault; }
    public boolean isIsdefault() { return isdefault; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(displayno);
        dest.writeDouble(grossweightingrams);
        dest.writeDouble(netweightingrams);
        dest.writeByte((byte) (isdefault ? 1 : 0));
        dest.writeString(weightkey);
        dest.writeString(grossweight);
        dest.writeString(netweight);
        dest.writeString(portionsize);
    }

    @Override
    public int compareTo(ItemWeightDetails o) {
        try {
            /* For Ascending order do like this */
            return (this.displayno - o.getWeightdisplayno());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }


}
