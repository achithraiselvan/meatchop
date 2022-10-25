package com.meatchop.sqlitedata;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.meatchop.data.Orderdetails;
import com.meatchop.helpers.DatabaseHelper;
import com.meatchop.utils.TMCUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

@DatabaseTable(tableName = "Orderdetailslocal")
public class Orderdetailslocal implements Parcelable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String serverkey;
    @DatabaseField(columnName = "orderid", unique = true)
    private String orderid;
    @DatabaseField
    private String tokenno;
    @DatabaseField
    private String vendorkey;
    @DatabaseField
    private String vendorname;
    @DatabaseField
    private String itemdesp;
    @DatabaseField
    private double coupondiscount;
    @DatabaseField
    private String couponkey;
    @DatabaseField
    private String coupontype;
    @DatabaseField
    private double deliveryamount;
    @DatabaseField
    private double gstamount;
    @DatabaseField
    private double payableamount;
    @DatabaseField
    private String merchantorderid;
    @DatabaseField
    private String slotname;      // expressdelivery or preorder
    @DatabaseField
    private String slotdate;
    @DatabaseField
    private String slottimerange;
    @DatabaseField
    private String ordertype; // apporder OR posorder
    @DatabaseField
    private String deliverytype;  // storepickup or homedelivery
    @DatabaseField
    private String paymentmode;  // razorpay | paytm | cashondelivery | pos
    @DatabaseField
    private String userkey;
    @DatabaseField
    private String usermobile;
    @DatabaseField
    private String orderplaceddate;
    @DatabaseField
    private String orderplacedtime;
    @DatabaseField
    private String useraddress;
    @DatabaseField
    private String notes;
    @DatabaseField
    private long orderplacedtimeinlong;
    @DatabaseField
    private boolean ratingprovided = false;
    @DatabaseField
    private boolean ratingskipped = false;
    @DatabaseField
    private double discountamount;
    @DatabaseField
    private String paymenttype;


    public Orderdetailslocal() {

    }

    public Orderdetailslocal(JSONObject jsonObject) {
        try {
            if (jsonObject.has("key")) {
                this.serverkey = jsonObject.getString("key");
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
                    String coupondiscstr = jsonObject.getString("coupondiscount");
                    if ((coupondiscstr != null) && (!TextUtils.isEmpty(coupondiscstr))) {
                        this.coupondiscount = Double.parseDouble(coupondiscstr);
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
            if (jsonObject.has("usermobileno")) {
                this.usermobile = jsonObject.getString("usermobileno");
            }
            if (jsonObject.has("orderplacedtime")) {
                this.orderplacedtime = jsonObject.getString("orderplacedtime");
                this.orderplacedtimeinlong = TMCUtil.getInstance().getTimeInLong(orderplacedtime);
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
            if (jsonObject.has("paymenttype")) {
                this.paymenttype = jsonObject.getString("paymenttype");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Orderdetailslocal(Parcel in) {
        id = in.readInt();
        serverkey = in.readString();
        orderid = in.readString();
        tokenno = in.readString();
        vendorkey = in.readString();
        vendorname = in.readString();
        itemdesp = in.readString();
        coupondiscount = in.readDouble();
        couponkey = in.readString();
        coupontype = in.readString();
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
        orderplacedtimeinlong = in.readLong();
        ratingprovided = in.readByte() != 0;
        ratingskipped = in.readByte() != 0;
        discountamount = in.readDouble();
        paymenttype = in.readString();
    }

    public static final Creator<Orderdetailslocal> CREATOR = new Creator<Orderdetailslocal>() {
        @Override
        public Orderdetailslocal createFromParcel(Parcel in) {
            return new Orderdetailslocal(in);
        }

        @Override
        public Orderdetailslocal[] newArray(int size) {
            return new Orderdetailslocal[size];
        }
    };

    public void setServerkey(String serverkey) { this.serverkey = serverkey; }
    public String getServerkey() { return serverkey; }

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

    public void setSlottimerange(String slottimerange) { this.slottimerange = slottimerange; }
    public String getSlottimerange() { return slottimerange; }

    public void setOrdertype(String ordertype) { this.ordertype = ordertype; }
    public String getOrdertype() { return ordertype; }

    public void setDeliverytype(String deliverytype) { this.deliverytype = deliverytype; }
    public String getDeliverytype() { return deliverytype; }

    public void setPaymentmode(String paymentmode) { this.paymentmode = paymentmode; }
    public String getPaymentmode() { return paymentmode; }

    public void setPaymenttype(String paymenttype) { this.paymenttype = paymenttype; }
    public String getPaymenttype() { return paymenttype; }

    public void setUserkey(String userkey) { this.userkey = userkey; }
    public String getUserkey() { return userkey; }

    public void setUsermobile(String usermobile) { this.usermobile = usermobile; }
    public String getUsermobile() { return usermobile; }

    public void setOrderplaceddate(String orderplaceddate) { this.orderplaceddate = orderplaceddate; }
    public String getOrderplaceddate() { return orderplaceddate; }

    public void setOrderplacedtime(String orderplacedtime) { this.orderplacedtime = orderplacedtime; }
    public String getOrderplacedtime() { return orderplacedtime; }

    public void setUseraddress(String useraddress) { this.useraddress = useraddress; }
    public String getUseraddress() { return this.useraddress; }

    public void setNotes(String notes) { this.notes = notes; }
    public String getNotes() { return this.notes; }

    public void setRatingprovided(boolean ratingprovided) { this.ratingprovided = ratingprovided; }
    public boolean isRatingprovided() { return this.ratingprovided; }

    public void setRatingskipped(boolean ratingskipped) { this.ratingskipped = ratingskipped; }
    public boolean isRatingskipped() { return this.ratingskipped; }

    public void setDiscountamount(double discountamount) { this.discountamount = discountamount; }
    public double getDiscountamount() { return this.discountamount; }

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

    public JSONArray getItemDespForRating() {
        if (itemdesp == null) {
            return null;
        }
        try {
            JSONObject itemdespjson = new JSONObject(itemdesp);
            JSONArray jsonArray = itemdespjson.getJSONArray("itemdesp");
            JSONArray itemratingjsonarray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemratingjson = new JSONObject();
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                String menuitemid = jsonobject.getString("menuitemid");
                String itemname = jsonobject.getString("itemname");
                itemratingjson.put("menuitemid", menuitemid);
                itemratingjson.put("itemname", itemname);
                itemratingjsonarray.put(itemratingjson);
            }
            return itemratingjsonarray;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(serverkey);
        dest.writeString(orderid);
        dest.writeString(tokenno);
        dest.writeString(vendorkey);
        dest.writeString(vendorname);
        dest.writeString(itemdesp);
        dest.writeDouble(coupondiscount);
        dest.writeString(couponkey);
        dest.writeString(coupontype);
        dest.writeDouble(deliveryamount);
        dest.writeDouble(gstamount);
        dest.writeDouble(payableamount);
        dest.writeString(merchantorderid);
        dest.writeString(slotname);
        dest.writeString(slotdate);
        dest.writeString(slottimerange);
        dest.writeString(ordertype);
        dest.writeString(deliverytype);
        dest.writeString(paymentmode);
        dest.writeString(userkey);
        dest.writeString(usermobile);
        dest.writeString(orderplaceddate);
        dest.writeString(orderplacedtime);
        dest.writeString(useraddress);
        dest.writeString(notes);
        dest.writeLong(orderplacedtimeinlong);
        dest.writeByte((byte) (ratingprovided ? 1 : 0));
        dest.writeByte((byte) (ratingskipped ? 1 : 0));
        dest.writeDouble(discountamount);
        dest.writeString(paymenttype);
    }

    public void update(DatabaseHelper dbHelper) {
        try {
            dbHelper.getOrderDetailsLocalDao().update(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(DatabaseHelper dbHelper) {
        try {
            dbHelper.getOrderDetailsLocalDao().createOrUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(DatabaseHelper dbHelper) {
        try {
            dbHelper.getOrderDetailsLocalDao().delete(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 /* @Override
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
    } */
}
