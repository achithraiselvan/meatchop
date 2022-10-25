package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Orderdetails implements Parcelable, Comparable<Orderdetails> {

    public static final String ORDERSTATUS_NEW = "NEW";
    public static final String ORDERSTATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDERSTATUS_READYFORPICKUP = "READY FOR PICKUP";
    public static final String ORDERSTATUS_PICKEDUP = "PICKED UP";
    public static final String ORDERSTATUS_DELIVERED = "DELIVERED";
    public static final String ORDERSTATUS_CANCELLED = "CANCELLED";

    public static final String SLOTNAME_PREORDER = "PREORDER";
    public static final String SLOTNAME_EXPRESSDELIVERY = "EXPRESSDELIVERY";
    public static final String SLOTNAME_SPECIALDAYPREORDER = "SPECIALDAYPREORDER";


    private String key = "";
    private String orderid = "";
    private String tokenno = "";
    private String vendorkey = "";
    private String vendorname = "";
    private String itemdesp = "";
    private double coupondiscount = 0;
    private String couponkey = "";
    private String coupontype;
    private double deliveryamount = 0;
    private double gstamount = 0;
    private double payableamount = 0;
    private String merchantorderid = "";
    private String slotname = "";      // expressdelivery or preorder
    private String slotdate = "";
    private String slottimerange = "";
    private String ordertype; // apporder OR posorder
    private String deliverytype;  // storepickup or homedelivery
    private String paymentmode;  // razorpay | paytm | cashondelivery | pos
    private String userkey = "";
    private String usermobile = "";
    private String orderplaceddate = "";
    private String orderplacedtime = "";
    private String useraddress = "";
    private String notes = "";
    private double discountamount = 0;
    private String menustockdetails;
    private String slotdatenew;
    private String orderplaceddatenew;
    private String userstatus;

    public Orderdetails() {

    }

    public Orderdetails(JSONObject jsonObject) {
        try {
            if (jsonObject.has("key")) {
                this.key = jsonObject.getString("key");
            }
            if (jsonObject.has("orderid")) {
                this.orderid = jsonObject.getString("orderid");
            }
            if (jsonObject.has("tokenno")) {
                this.tokenno = jsonObject.getString("tokenno");
            }
            if (jsonObject.has("vendorkey")) {
                this.vendorkey = jsonObject.getString("vendorkey");
            }
            if (jsonObject.has("vendorname")) {
                this.vendorname = jsonObject.getString("vendorname");
            }
            if (jsonObject.has("itemdesp")) {
                JSONArray jsonArray = jsonObject.getJSONArray("itemdesp");
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("itemdesp", jsonArray);
                this.itemdesp = jsonObject1.toString();
            }
            if (jsonObject.has("coupondiscount")) {
                try {
                    Object coupondiscobj = jsonObject.get("coupondiscount");
                    if (coupondiscobj instanceof Double) {
                        this.coupondiscount = jsonObject.getDouble("coupondiscount");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (jsonObject.has("couponkey")) {
                this.couponkey = jsonObject.getString("couponkey");
            }
            if (jsonObject.has("deliveryamount")) {
                try {
                    this.deliveryamount = jsonObject.getDouble("deliveryamount");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (jsonObject.has("gstamount")) {
                try {
                    this.gstamount = jsonObject.getDouble("gstamount");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (jsonObject.has("payableamount")) {
                try {
                    this.payableamount = jsonObject.getDouble("payableamount");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (jsonObject.has("merchantorderid")) {
                this.merchantorderid = jsonObject.getString("merchantorderid");
            }
            if (jsonObject.has("slotname")) {
                this.slotname = jsonObject.getString("slotname");
            }
            if (jsonObject.has("slotdate")) {
                this.slotdate = jsonObject.getString("slotdate");
            }
            if (jsonObject.has("slottimerange")) {
                this.slottimerange = jsonObject.getString("slottimerange");
            }
            if (jsonObject.has("ordertype")) {
                this.ordertype = jsonObject.getString("ordertype");
            }
            if (jsonObject.has("deliverytype")) {
                this.deliverytype = jsonObject.getString("deliverytype");
            }
            if (jsonObject.has("paymentmode")) {
                this.paymentmode = jsonObject.getString("paymentmode");
            }
            if (jsonObject.has("userkey")) {
                this.userkey = jsonObject.getString("userkey");
            }
            if (jsonObject.has("usermobile")) {
                this.usermobile = jsonObject.getString("usermobile");
            }
            if (jsonObject.has("orderplacedtime")) {
                this.orderplacedtime = jsonObject.getString("orderplacedtime");
            }
            if (jsonObject.has("useraddress")) {
                this.useraddress = jsonObject.getString("useraddress");
            }
            if (jsonObject.has("orderplaceddate")) {
                this.orderplaceddate = jsonObject.getString("orderplaceddate");
            }
            if (jsonObject.has("notes")) {
                this.notes = jsonObject.getString("notes");
            }

            if (jsonObject.has("discountamount")) {
                try {
                    String discamtstr = jsonObject.getString("discountamount");
                    this.discountamount = Double.parseDouble(discamtstr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (jsonObject.has("userstatus")) {
                this.userstatus = jsonObject.getString("userstatus");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    protected Orderdetails(Parcel in) {
        key = in.readString();
        orderid = in.readString();
        tokenno = in.readString();
        vendorkey = in.readString();
        vendorname = in.readString();
        itemdesp = in.readString();
        coupondiscount = in.readDouble();
        couponkey = in.readString();
        deliveryamount = in.readDouble();
        gstamount = in.readDouble();
        payableamount = in.readDouble();
        merchantorderid = in.readString();
        slotname = in.readString();
        slotdate = in.readString();
        slottimerange = in.readString();
        ordertype = in.readString();
        deliverytype = in.readString();
        paymentmode = in.readString();
        userkey = in.readString();
        usermobile = in.readString();
        orderplaceddate = in.readString();
        orderplacedtime = in.readString();
        useraddress = in.readString();
        notes = in.readString();
        discountamount = in.readDouble();
        menustockdetails = in.readString();
        slotdatenew = in.readString();
        orderplaceddatenew = in.readString();
        userstatus = in.readString();
    }

    public static final Creator<Orderdetails> CREATOR = new Creator<Orderdetails>() {
        @Override
        public Orderdetails createFromParcel(Parcel in) {
            return new Orderdetails(in);
        }

        @Override
        public Orderdetails[] newArray(int size) {
            return new Orderdetails[size];
        }
    };

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setOrderid(String orderid) { this.orderid = orderid; }
    public String getOrderid() { return orderid; }

    public void setTokenno(String tokenno) { this.tokenno = tokenno; }
    public String getTokenno() { return tokenno; }

    public void setVendorkey(String vendorkey) { this.vendorkey = vendorkey; }
    public String getVendorkey() { return vendorkey; }

    public void setVendorname(String vendorname) { this.vendorname = vendorname; }
    public String getVendorname() { return vendorname; }

    public void setItemdesp(String itemdesp) { this.itemdesp = itemdesp; }
    public String getItemdesp() { return itemdesp; }

    public void setCoupondiscount(double coupondiscount) { this.coupondiscount = coupondiscount; }
    public double getCoupondiscount() { return coupondiscount; }

    public void setCouponkey(String couponkey) { this.couponkey = couponkey; }
    public String getCouponkey() { return couponkey; }

    public void setCoupontype(String coupontype) { this.coupontype = coupontype; }
    public String getCoupontype() { return coupontype; }

    public void setDeliveryamount(double deliveryamount) { this.deliveryamount = deliveryamount; }
    public double getDeliveryamount() { return deliveryamount; }

    public void setGstamount(double gstamount) { this.gstamount = gstamount; }
    public double getGstamount() { return gstamount; }

    public void setPayableamount(double payableamount) { this.payableamount = payableamount; }
    public double getPayableamount() { return payableamount; }

    public void setMerchantorderid(String merchantorderid) { this.merchantorderid = merchantorderid; }
    public String getMerchantorderid() { return merchantorderid; }

    public void setSlotname(String slotname) { this.slotname = slotname; }
    public String getSlotname() { return slotname; }

    public void setSlotdate(String slotdate) { this.slotdate = slotdate; }
    public String getSlotdate() { return slotdate; }

    public void setSlotdatenew(String slotdatenew) { this.slotdatenew = slotdatenew; }
    public String getSlotdatenew() { return slotdatenew; }

    public void setSlottimerange(String slottimerange) { this.slottimerange = slottimerange; }
    public String getSlottimerange() { return slottimerange; }

    public void setOrdertype(String ordertype) { this.ordertype = ordertype; }
    public String getOrdertype() { return ordertype; }

    public void setDeliverytype(String deliverytype) { this.deliverytype = deliverytype; }
    public String getDeliverytype() { return deliverytype; }

    public void setPaymentmode(String paymentmode) { this.paymentmode = paymentmode; }
    public String getPaymentmode() { return paymentmode; }

    public void setUserkey(String userkey) { this.userkey = userkey; }
    public String getUserkey() { return userkey; }

    public void setUsermobile(String usermobile) { this.usermobile = usermobile; }
    public String getUsermobile() { return usermobile; }

    public void setOrderplaceddate(String orderplaceddate) { this.orderplaceddate = orderplaceddate; }
    public String getOrderplaceddate() { return orderplaceddate; }

    public void setOrderplaceddatenew(String orderplaceddatenew) { this.orderplaceddatenew = orderplaceddatenew; }
    public String getOrderplaceddatenew() { return orderplaceddatenew; }

    public void setOrderplacedtime(String orderplacedtime) { this.orderplacedtime = orderplacedtime; }
    public String getOrderplacedtime() { return orderplacedtime; }

    public void setUseraddress(String useraddress) { this.useraddress = useraddress; }
    public String getUseraddress() { return this.useraddress; }

    public void setNotes(String notes) { this.notes = notes; }
    public String getNotes() { return this.notes; }

    public void setDiscountamount(double discountamount) { this.discountamount = discountamount; }
    public double getDiscountamount() { return this.discountamount; }

    public void setUserstatus(String userstatus) { this.userstatus = userstatus; }
    public String getUserstatus() { return this.userstatus; }


    public String getImageurl() {
        try {
            JSONObject jsonObject = new JSONObject(itemdesp);
            JSONArray jsonArray = jsonObject.getJSONArray("itemdesp");
            String imageurl = "";
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                imageurl = jsonObject1.getString("checkouturl");
                if (!TextUtils.isEmpty(imageurl)) {
                    continue;
                }
            }
            return imageurl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getConsolidatedItemDespWithQty() {
        if (itemdesp == null) {
            return null;
        }
     // Log.d("Orderdetails", "itemdesp "+itemdesp);

        String consolidateditemdesp = "";
        try {
            JSONObject itemdespjson = new JSONObject(itemdesp);
            JSONArray jsonArray = itemdespjson.getJSONArray("itemdesp");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                int quantity = jsonobject.getInt("quantity");
                String itemname = "";
                if (jsonobject.has("marinadeitemdesp")) {
                    JSONObject marinadejsonobj = jsonobject.getJSONObject("marinadeitemdesp");
                    String marinadename = jsonobject.getString("itemname");
                    String meatname = marinadejsonobj.getString("itemname");
                    String meatqty = marinadejsonobj.getString("grossweight");
                    itemname = meatname + " " + meatqty + " With " + marinadename;
                } else {
                    itemname = jsonobject.getString("itemname");
                }

                String itemdetails = itemname + " x " + quantity;
                if (i == 0) {
                    consolidateditemdesp = itemdetails;
                } else {
                    consolidateditemdesp = consolidateditemdesp + ", " + itemdetails;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return consolidateditemdesp;

    }

    public double getItemtotal() {
        if (itemdesp == null) {
            return 0;
        }

        double itemtotal = 0;
        try {
            JSONObject itemdespjson = new JSONObject(itemdesp);
            JSONArray jsonArray = itemdespjson.getJSONArray("itemdesp");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                double itemprice = jsonobject.getDouble("tmcprice");
                int quantity = jsonobject.getInt("quantity");
                if (jsonobject.has("marinadeitemdesp")) {
                    JSONObject marinadejsonobj = jsonobject.getJSONObject("marinadeitemdesp");
                    double marinademeatprice = marinadejsonobj.getDouble("tmcprice");
                    itemprice = itemprice + marinademeatprice;
                }
                itemtotal = itemtotal + (itemprice * quantity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return itemtotal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(orderid);
        parcel.writeString(tokenno);
        parcel.writeString(vendorkey);
        parcel.writeString(vendorname);
        parcel.writeString(itemdesp);
        parcel.writeDouble(coupondiscount);
        parcel.writeString(couponkey);
        parcel.writeDouble(deliveryamount);
        parcel.writeDouble(gstamount);
        parcel.writeDouble(payableamount);
        parcel.writeString(merchantorderid);
        parcel.writeString(slotname);
        parcel.writeString(slotdate);
        parcel.writeString(slottimerange);
        parcel.writeString(ordertype);
        parcel.writeString(deliverytype);
        parcel.writeString(paymentmode);
        parcel.writeString(userkey);
        parcel.writeString(usermobile);
        parcel.writeString(orderplaceddate);
        parcel.writeString(orderplacedtime);
        parcel.writeString(useraddress);
        parcel.writeString(notes);
        parcel.writeDouble(discountamount);
        parcel.writeString(menustockdetails);
        parcel.writeString(slotdatenew);
        parcel.writeString(orderplaceddatenew);
        parcel.writeString(userstatus);
    }

    public String getJSONString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", key);
            jsonObject.put("orderid", orderid);
            jsonObject.put("vendorkey", vendorkey);
            jsonObject.put("vendorname", vendorname);
            jsonObject.put("tokenno", tokenno);

            JSONObject itemdespjson = new JSONObject(itemdesp);
            JSONArray itemdespjsonarray = itemdespjson.getJSONArray("itemdesp");
            jsonObject.put("itemdesp", itemdespjsonarray);

            jsonObject.put("coupondiscount", coupondiscount);
            jsonObject.put("couponkey", couponkey);
            jsonObject.put("coupontype", coupontype);
            jsonObject.put("deliveryamount", deliveryamount);
            jsonObject.put("gstamount", gstamount);
            jsonObject.put("payableamount", payableamount);
            jsonObject.put("merchantorderid", merchantorderid);
            jsonObject.put("slotname", slotname);
            jsonObject.put("slotdate", slotdate);
            jsonObject.put("slottimerange", slottimerange);
            jsonObject.put("ordertype", ordertype);
            jsonObject.put("deliverytype", deliverytype);
            jsonObject.put("paymentmode", paymentmode);
            jsonObject.put("userkey", userkey);
            jsonObject.put("usermobile", "+" + usermobile);
            jsonObject.put("orderplaceddate", orderplaceddate);
            jsonObject.put("orderplacedtime", orderplacedtime);
            jsonObject.put("useraddress", useraddress);
            jsonObject.put("notes", notes);
            jsonObject.put("discountamount", discountamount);
            return jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getJSONStringNew() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", key);
            jsonObject.put("orderid", orderid);
            jsonObject.put("vendorkey", vendorkey);
            jsonObject.put("vendorname", vendorname);
            jsonObject.put("tokenno", tokenno);

            JSONObject itemdespjson = new JSONObject(itemdesp);
            JSONArray itemdespjsonarray = itemdespjson.getJSONArray("itemdesp");
            jsonObject.put("itemdesp", itemdespjsonarray);

            jsonObject.put("coupondiscount", coupondiscount);
            jsonObject.put("couponkey", couponkey);
            jsonObject.put("coupontype", coupontype);
            jsonObject.put("deliveryamount", deliveryamount);
            jsonObject.put("gstamount", gstamount);
            jsonObject.put("payableamount", payableamount);
            jsonObject.put("merchantorderid", merchantorderid);
            jsonObject.put("slotname", slotname);

            jsonObject.put("slotdate", slotdatenew);

            jsonObject.put("slottimerange", slottimerange);
            jsonObject.put("ordertype", ordertype);
            jsonObject.put("deliverytype", deliverytype);
            jsonObject.put("paymentmode", paymentmode);
            jsonObject.put("userkey", userkey);
            jsonObject.put("usermobileno", "+" + usermobile);
            jsonObject.put("orderplaceddate", orderplaceddatenew);
            jsonObject.put("orderplacedtime", orderplacedtime);
            jsonObject.put("useraddress", useraddress);
            jsonObject.put("notes", notes);
            jsonObject.put("discountamount", discountamount);
            if ((userstatus != null) && (!TextUtils.isEmpty(userstatus))) {
                jsonObject.put("userstatus", userstatus);
            }
            return jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int compareTo(Orderdetails o) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date objdate = sdf.parse(o.getOrderplacedtime());
            Date thisdate = sdf.parse(this.getOrderplacedtime());

            return objdate.compareTo(thisdate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    private long getTimeInLong(String time) {
        try {
            if (time == null) { return 0; }
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private HashMap<String, JSONObject> menuStockDetailsMap;
    public void addMenuStockdetails(JSONArray menustockdetailsarray) {
        if (menustockdetailsarray == null) {
            return;
        }
        if (menuStockDetailsMap == null) {
            menuStockDetailsMap = new HashMap<String, JSONObject>();
        }
        try {
            for (int i=0; i<menustockdetailsarray.length(); i++) {
                JSONObject menustockdetails = menustockdetailsarray.getJSONObject(i);
                String menuitemkey = menustockdetails.getString("menuitemkey");
                JSONObject prevmenustockdetails = menuStockDetailsMap.get(menuitemkey);
                if (prevmenustockdetails == null) {
                    menuStockDetailsMap.put(menuitemkey, menustockdetails);
                } else {
                    String prevorderedqtystr = prevmenustockdetails.getString("orderedqty");
                    double prevorderedqty = Double.parseDouble(prevorderedqtystr);

                    String neworderedqtystr = menustockdetails.getString("orderedqty");
                    double neworderedqty = Double.parseDouble(neworderedqtystr);

                    double totorderedqty = prevorderedqty + neworderedqty;
                    menustockdetails.put("orderedqty", totorderedqty);
                    menuStockDetailsMap.put(menuitemkey, menustockdetails);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


 /* public void addMenuStockdetails(JSONObject menustockdetails) {
        if (menuStockDetailsMap == null) {
            menuStockDetailsMap = new HashMap<String, JSONObject>();
        }
        try {
            String menuitemkey = menustockdetails.getString("menuitemkey");
            JSONObject prevmenustockdetails = menuStockDetailsMap.get(menuitemkey);
            if (prevmenustockdetails == null) {
                menuStockDetailsMap.put(menuitemkey, menustockdetails);
            } else {
                String prevorderedqtystr = prevmenustockdetails.getString("orderedqty");
                double prevorderedqty = Double.parseDouble(prevorderedqtystr);

                String neworderedqtystr = menustockdetails.getString("orderedqty");
                double neworderedqty = Double.parseDouble(neworderedqtystr);

                double totorderedqty = prevorderedqty + neworderedqty;
                menustockdetails.put("orderedqty", totorderedqty);
                menuStockDetailsMap.put(menuitemkey, menustockdetails);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } */

    public void setMenustockdetails(String menustockdetails) { this.menustockdetails = menustockdetails; }
    public String getMenustockdetails() { return this.menustockdetails; }

    public JSONArray getMenustockdetailsArray() {
        try {
            if (menuStockDetailsMap == null) {
                return null;
            }
            Object[] values = menuStockDetailsMap.values().toArray();
            JSONArray jsonArray = new JSONArray();
            for (int i=0; i<values.length; i++) {
                JSONObject menustockdetails = (JSONObject) values[i];
                jsonArray.put(menustockdetails);
            }
            return jsonArray;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }




}
