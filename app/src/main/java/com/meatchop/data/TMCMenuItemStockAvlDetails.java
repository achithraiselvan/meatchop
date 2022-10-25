package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

public class TMCMenuItemStockAvlDetails implements Parcelable {

    private String barcode;
    private boolean itemavailability;
    private String key;
    private String lastupdatedtime;
    private String menuitemkey;
    private double receivedstock;
    private double stockbalance;
    private String stockincomingkey;
    private String vendorkey;
    private boolean allownegativestock;

    public TMCMenuItemStockAvlDetails() {

    }

    public TMCMenuItemStockAvlDetails(JSONObject jsonObject) {
        try {
            if (!jsonObject.isNull("barcode")) {
                this.barcode = jsonObject.getString("barcode");
            }
            if (!jsonObject.isNull("key")) {
                this.key = jsonObject.getString("key");
            }
            if (!jsonObject.isNull("lastupdatedtime")) {
                this.lastupdatedtime = jsonObject.getString("lastupdatedtime");
            }
            if (!jsonObject.isNull("menuitemkey")) {
                this.menuitemkey = jsonObject.getString("menuitemkey");
            }
            if (!jsonObject.isNull("stockincomingkey")) {
                this.stockincomingkey = jsonObject.getString("stockincomingkey");
            }
            if (!jsonObject.isNull("vendorkey")) {
                this.vendorkey = jsonObject.getString("vendorkey");
            }
            if (!jsonObject.isNull("itemavailability")) {
                this.itemavailability = jsonObject.getBoolean("itemavailability");
            }
            if (!jsonObject.isNull("receivedstock")) {
                this.receivedstock = jsonObject.getDouble("receivedstock");
            }
            if (!jsonObject.isNull("stockbalance")) {
                this.stockbalance = jsonObject.getDouble("stockbalance");
            }
            if (!jsonObject.isNull("allownegativestock")) {
                try {
                    String allownegstockstr = jsonObject.getString("allownegativestock");
                    if ((allownegstockstr != null) && !(TextUtils.isEmpty(allownegstockstr))) {
                        this.allownegativestock = Boolean.parseBoolean(allownegstockstr);
                    }
                 // this.allownegativestock = jsonObject.getBoolean("allownegativestock");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected TMCMenuItemStockAvlDetails(Parcel in) {
        barcode = in.readString();
        itemavailability = in.readByte() != 0;
        key = in.readString();
        lastupdatedtime = in.readString();
        menuitemkey = in.readString();
        receivedstock = in.readDouble();
        stockbalance = in.readDouble();
        stockincomingkey = in.readString();
        vendorkey = in.readString();
        allownegativestock = in.readByte() != 0;
    }

    public static final Creator<TMCMenuItemStockAvlDetails> CREATOR = new Creator<TMCMenuItemStockAvlDetails>() {
        @Override
        public TMCMenuItemStockAvlDetails createFromParcel(Parcel in) {
            return new TMCMenuItemStockAvlDetails(in);
        }

        @Override
        public TMCMenuItemStockAvlDetails[] newArray(int size) {
            return new TMCMenuItemStockAvlDetails[size];
        }
    };

    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getBarcode() { return this.barcode; }

    public void setKey(String key) { this.key = key; }
    public String getKey() { return this.key; }

    public void setLastupdatedtime(String lastupdatedtime) { this.lastupdatedtime = lastupdatedtime; }
    public String getLastupdatedtime() { return this.lastupdatedtime; }

    public void setMenuitemkey(String menuitemkey) { this.menuitemkey = menuitemkey; }
    public String getMenuitemkey() { return this.menuitemkey; }

    public void setStockincomingkey(String key) { this.stockincomingkey = stockincomingkey; }
    public String getStockincomingkey() { return this.stockincomingkey; }

    public void setVendorkey(String vendorkey) { this.vendorkey = vendorkey; }
    public String getVendorkey() { return this.vendorkey; }

    public void setItemavailability(boolean itemavailability) { this.itemavailability = itemavailability; }
    public boolean isItemavailability() {
        if (allownegativestock) {
            return this.itemavailability;
        } else {
            if (stockbalance > 0) {
                return this.itemavailability;
            } else {
                return false;
            }
        }

    }

    public void setReceivedstock(double receivedstock) { this.receivedstock = receivedstock; }
    public double getReceivedstock() { return this.receivedstock; }

    public void setStockbalance(double stockbalance) { this.stockbalance = stockbalance; }
    public double getStockbalance() { return this.stockbalance; }

    public boolean isAllownegativestock() { return allownegativestock; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barcode);
        dest.writeByte((byte) (itemavailability ? 1 : 0));
        dest.writeString(key);
        dest.writeString(lastupdatedtime);
        dest.writeString(menuitemkey);
        dest.writeDouble(receivedstock);
        dest.writeDouble(stockbalance);
        dest.writeString(stockincomingkey);
        dest.writeString(vendorkey);
        dest.writeBoolean(allownegativestock);
    }
}
