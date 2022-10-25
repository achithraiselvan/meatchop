package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonObject;
import com.meatchop.TMCApplication;
import com.meatchop.widget.TMCMenuItemCatalog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TMCMenuItem implements Comparable<TMCMenuItem>, Parcelable {

    public static final String MENUTYPE_REGULAR = "Regular";
    public static final String MENUTYPE_MARINADE = "Marinade";
    public static final String MENUTYPE_MEALKIT = "Mealkit";

    private String itemname;
    private String grossweight;
    private double tmcprice;
    private String vendorname;
    private double applieddiscpercentage;
    private int displayno;
    private boolean itemavailability;
    private String tmcsubctgykey;
    private double gstpercentage;
    private String tmcctgykey;
    private int itemcalories;
    private String portionsize;
    private String itemuniquecode;
    private String vendorkey;
    private String key;
    private String imageurl;
    private String netweight;
    private double strikedoutprice = 0;
    private String menutype;
    private JSONObject awsJSONObject;
    private String barcode;

    // Below fields are not there in AWS Table
    private int selectedqty;
    private TMCMenuItem marinadeMeatTMCMenuItem;
    private String keyforcartmenuitemmap;
    private String awsKey;
    private double grossweightingrams;
    private String itemweightdetails = null;
    private String itemcutdetails = null;
    private String selecteditemweight = null;
    private String selecteditemcut = null;
    private double tmcpriceperkg;
    private String itemdesp;
    private double stockbalance;
    private String stockincomingkey;
    private String tmcmenuavlkey;
    private double receivedstock;
    private String pricetypeforpos;
    private String inventorydetails;
    private boolean isshowinapp = true;
    private double appmarkuppercentage = 0;

    // Unused columns. Will be depreciated after inventory release
    private String preparationtime;
    private String checkoutimageurl;
    private String marinadelinkedcodes;
    private boolean isMarinadeLinked;
    private JSONArray marinadeitemdesp = null;


    public TMCMenuItem(String vendorname, String itemname, String tmcctgy, int displayno, String itemdesp,
                       String imageurl, String checkoutimageurl, String itemweight, double baseamount, double gstamount,
                       int gstpercentage, double oldprice, int discountpercentage, double totalamount) {

    }

    public TMCMenuItem(JSONObject jsonObject) {
        try {
            this.awsJSONObject = jsonObject;
            this.itemname = jsonObject.getString("itemname");
            this.vendorname = jsonObject.getString("vendorname");
            this.tmcprice = jsonObject.getDouble("tmcprice");
            this.tmcsubctgykey = jsonObject.getString("tmcsubctgykey");
            this.gstpercentage = jsonObject.getDouble("gstpercentage");
            this.tmcctgykey = jsonObject.getString("tmcctgykey");
            this.itemuniquecode = jsonObject.getString("itemuniquecode");
            this.vendorkey = jsonObject.getString("vendorkey");
         // this.key = jsonObject.getString("key");
            this.awsKey = jsonObject.getString("key");

            if (!jsonObject.isNull("barcode")) {
                this.barcode = jsonObject.getString("barcode");
            }
            if (!jsonObject.isNull("itemdesp")) {
                this.itemdesp = jsonObject.getString("itemdesp");
            }
            if (!jsonObject.isNull("grossweight")) {
                this.grossweight = jsonObject.getString("grossweight");
            }
            if (!jsonObject.isNull("applieddiscountpercentage")) {
                this.applieddiscpercentage = jsonObject.getDouble("applieddiscountpercentage");
            }
            if (!jsonObject.isNull("displayno")) {
                this.displayno = jsonObject.getInt("displayno");
            }
            if (!jsonObject.isNull("itemavailability")) {
                this.itemavailability = jsonObject.getBoolean("itemavailability");
            }
            if (!jsonObject.isNull("preparationtime")) {
                this.preparationtime = jsonObject.getString("preparationtime");
            }
            if (!jsonObject.isNull("itemcalories")) {
                this.itemcalories = jsonObject.getInt("itemcalories");
            }
            if (!jsonObject.isNull("portionsize")) {
                this.portionsize = jsonObject.getString("portionsize");
            }
            if (!jsonObject.isNull("checkoutimageurl")) {
                this.checkoutimageurl = jsonObject.getString("checkoutimageurl");
            }
            if (!jsonObject.isNull("imageurl")) {
                this.imageurl = jsonObject.getString("imageurl");
            }
            if (!jsonObject.isNull("netweight")) {
                this.netweight = jsonObject.getString("netweight");
            }
            if (!jsonObject.isNull("menutype")) {
                this.menutype = jsonObject.getString("menutype");
            }
            if (!jsonObject.isNull("marinadelinkedcodes")) {
                this.marinadelinkedcodes = jsonObject.getString("marinadelinkedcodes");
            }
            if (jsonObject.has("grossweightingrams")) {
                try {
                    String grosswtingramsstr = jsonObject.getString("grossweightingrams");
                    if ((grosswtingramsstr != null) && (!TextUtils.isEmpty(grosswtingramsstr))) {
                        this.grossweightingrams = Double.parseDouble(grosswtingramsstr);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (jsonObject.has("stockbalance")) {
                try {
                    this.stockbalance = jsonObject.getDouble("stockbalance");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (!jsonObject.isNull("stockincomingkey")) {
                this.stockincomingkey = jsonObject.getString("stockincomingkey");
            }
            if (!jsonObject.isNull("tmcmenuavlkey")) {
                this.tmcmenuavlkey = jsonObject.getString("tmcmenuavlkey");
            }
            if (!jsonObject.isNull("receivedstock")) {
                this.receivedstock = jsonObject.getDouble("receivedstock");
            }


            if (this.applieddiscpercentage > 0) {
                this.strikedoutprice = this.tmcprice + (this.tmcprice * (this.applieddiscpercentage/100));
            }
            if (!jsonObject.isNull("itemweightdetails")) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("itemweightdetails");
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("itemweightdetails", jsonArray);
                    this.itemweightdetails = jsonObject1.toString();
                } catch (Exception ex) {
                    Log.d("TMCMenuItem", "this.itemname "+itemname);
                    ex.printStackTrace();
                }
            }

            if (!jsonObject.isNull("itemcutdetails")) {
              //  this.itemcutdetails = jsonObject.getString("itemcutdetails");
                try {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("itemcutdetails");
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("itemcutdetails", jsonArray1);
                    this.itemcutdetails = jsonObject1.toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (!jsonObject.isNull("appmarkuppercentage")) {
                try {
                    this.appmarkuppercentage = jsonObject.getDouble("appmarkuppercentage");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (!jsonObject.isNull("tmcpriceperkg")) {
                this.tmcpriceperkg = jsonObject.getDouble("tmcpriceperkg");
            }

            if (this.appmarkuppercentage > 0) {
                double newtmcprice = tmcprice + (tmcprice * (appmarkuppercentage/100));
                this.tmcprice = newtmcprice;

                if (tmcpriceperkg > 0) {
                    double newtmcpriceperkg = tmcpriceperkg + (tmcpriceperkg * (appmarkuppercentage/100));
                    this.tmcpriceperkg = newtmcpriceperkg;
                }
            }

            if (!jsonObject.isNull("pricetypeforpos")) {
                this.pricetypeforpos = jsonObject.getString("pricetypeforpos");
            }
            if (!jsonObject.isNull("itemdesp")) {
                this.itemdesp = jsonObject.getString("itemdesp");
            }
            if (!jsonObject.isNull("stockbalance")) {
                try {
                    String stockbalancestr = jsonObject.getString("stockbalance");
                    this.stockbalance = Double.parseDouble(stockbalancestr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (!jsonObject.isNull("stockincomingkey")) {
                this.stockincomingkey = jsonObject.getString("stockincomingkey");
            }
            if (!jsonObject.isNull("tmcmenuavlkey")) {
                this.tmcmenuavlkey = jsonObject.getString("tmcmenuavlkey");
            }
            if (!jsonObject.isNull("receivedstock")) {
                try {
                    String receivedstockstr = jsonObject.getString("receivedstock");
                    this.receivedstock = Double.parseDouble(receivedstockstr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (!jsonObject.isNull(("inventorydetails"))) {
                try {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("inventorydetails");
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("inventorydetails", jsonArray1);
                    this.inventorydetails = jsonObject1.toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (!jsonObject.isNull("showinapp")) {
                String isshowinappstr = jsonObject.getString("showinapp");
                try {
                    this.isshowinapp = Boolean.parseBoolean(isshowinappstr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TMCMenuItem(String key, String itemuniquecode, boolean itemavailability) {
        this.key = key;
        this.itemuniquecode = itemuniquecode;
        this.itemavailability = itemavailability;
    }

    protected TMCMenuItem(Parcel in) {
        itemname = in.readString();
        grossweight = in.readString();
        tmcprice = in.readDouble();
        vendorname = in.readString();
        applieddiscpercentage = in.readDouble();
        displayno = in.readInt();
        itemavailability = in.readByte() != 0;
        tmcsubctgykey = in.readString();
        preparationtime = in.readString();
        gstpercentage = in.readDouble();
        tmcctgykey = in.readString();
        itemcalories = in.readInt();
        portionsize = in.readString();
        itemuniquecode = in.readString();
        checkoutimageurl = in.readString();
        vendorkey = in.readString();
        key = in.readString();
        imageurl = in.readString();
        netweight = in.readString();
        strikedoutprice = in.readDouble();
        menutype = in.readString();
        marinadelinkedcodes = in.readString();
        selectedqty = in.readInt();
        isMarinadeLinked = in.readByte() != 0;
        awsKey = in.readString();
        grossweightingrams = in.readDouble();
        itemweightdetails = in.readString();
        itemcutdetails = in.readString();
        selecteditemcut = in.readString();
        selecteditemweight = in.readString();
        tmcpriceperkg = in.readDouble();
        itemdesp = in.readString();
        stockbalance = in.readDouble();
        stockincomingkey = in.readString();
        tmcmenuavlkey = in.readString();
        receivedstock = in.readDouble();
        barcode = in.readString();
        pricetypeforpos = in.readString();
        inventorydetails = in.readString();
        isshowinapp = in.readByte() != 0;
        appmarkuppercentage = in.readDouble();
    }

    public static final Creator<TMCMenuItem> CREATOR = new Creator<TMCMenuItem>() {
        @Override
        public TMCMenuItem createFromParcel(Parcel in) {
            return new TMCMenuItem(in);
        }

        @Override
        public TMCMenuItem[] newArray(int size) {
            return new TMCMenuItem[size];
        }
    };

    public JSONObject getAwsJSONObject() { return this.awsJSONObject; }

    public JSONObject getJSONObjectForClone() {
        try {
            JSONObject clonejson = new JSONObject(awsJSONObject.toString());
            if (stockincomingkey != null) {
                clonejson.put("stockincomingkey", stockincomingkey);
            }
            if (stockbalance > 0) {
                clonejson.put("stockbalance", stockbalance);
            }
            if (tmcmenuavlkey != null) {
                clonejson.put("tmcmenuavlkey", tmcmenuavlkey);
            }
            if (receivedstock > 0) {
                clonejson.put("receivedstock", receivedstock);
            }
            return clonejson;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setItemname(String itemname) { this.itemname = itemname; }
    public String getItemname() { return this.itemname; }

    public void setGrossweight(String grossweight) { this.grossweight = grossweight; }
    public String getGrossweight() { return this.grossweight; }

    public void setGrossweightingrams(double grossweightingrams) { this.grossweightingrams = grossweightingrams; }
    public double getGrossweightingrams() { return this.grossweightingrams; }

    public void setTmcprice(double tmcprice) { this.tmcprice = tmcprice; }
 /* public double getTmcprice() {
        if ((menutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE) && marinadeMeatTMCMenuItem != null)) {
            return marinadeMeatTMCMenuItem.getTmcprice() + this.tmcprice;
        } else {
            return this.tmcprice;
        }
    } */
    public double getTmcprice() {
     /* if ((menutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE) && marinadeMeatTMCMenuItem != null)) {
            return marinadeMeatTMCMenuItem.getTmcprice() + this.tmcprice;
        } else {
            return this.tmcprice;
        } */
        return this.tmcprice;
    }

 // public void setTmcpriceperkg(double tmcpriceperkg) { this.tmcpriceperkg = tmcpriceperkg; }
    public double getTmcpriceperkg() {
        return this.tmcpriceperkg;
    }


    public double getMarinadePrice() {
        if (menutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
            return this.tmcprice;
        }
        return 0;
    }

    public void setVendorname(String vendorname) { this.vendorname = vendorname; }
    public String getVendorname() { return vendorname; }

    public void setApplieddiscpercentage(double applieddiscpercentage) { this.applieddiscpercentage = applieddiscpercentage; }
    public double getApplieddiscpercentage() { return this.applieddiscpercentage; }

    public void setDisplayno(int displayno) { this.displayno = displayno; }
    public int getDisplayno() { return this.displayno; }

    public void setItemavailability(boolean itemavailability) { this.itemavailability = itemavailability; }
    public boolean isItemavailability(boolean isinventorycheck) {
        if (!isinventorycheck) {
            return this.itemavailability;
        } else {
            return isInventoryItemavailability();
        }
     // return this.itemavailability;
    }

    public boolean isInventoryItemavailability() {
        if (this.inventorydetails == null) {
            TMCMenuItemStockAvlDetails tmcMenuItemStockAvlDetails = TMCMenuItemCatalog.getInstance().getMenuItemStockAvlDetails(this.awsKey);
            if (tmcMenuItemStockAvlDetails != null) {
                return tmcMenuItemStockAvlDetails.isItemavailability();
            } else {
                return this.itemavailability;
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject(inventorydetails);
                JSONArray jsonArray = jsonObject.getJSONArray("inventorydetails");
                boolean tmcmenuitemavailability = false;
                TMCMenuItemStockAvlDetails tmcMenuItemStockAvlDetails =
                                            TMCMenuItemCatalog.getInstance().getMenuItemStockAvlDetails(this.awsKey);
                if (tmcMenuItemStockAvlDetails != null) {
                    tmcmenuitemavailability = tmcMenuItemStockAvlDetails.isItemavailability();
                }
                boolean invdetailsitemavailability = true;
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject inventoryJSON = jsonArray.getJSONObject(i);
                    String inventorymenuitemkey = inventoryJSON.getString("menuitemkey");
                    TMCMenuItemStockAvlDetails inventorymenuitemStockAvlDetails =
                                       TMCMenuItemCatalog.getInstance().getMenuItemStockAvlDetails(inventorymenuitemkey);
                    if ((inventorymenuitemStockAvlDetails != null) && (!inventorymenuitemStockAvlDetails.isItemavailability())) {
                        invdetailsitemavailability = false;
                    }
                }
                return (invdetailsitemavailability && tmcmenuitemavailability);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public void setTmcsubctgykey(String tmcsubctgykey) { this.tmcsubctgykey = tmcsubctgykey; }
    public String getTmcsubctgykey() { return this.tmcsubctgykey; }

    public void setPreparationtime(String preperationtime) { this.preparationtime = preparationtime; }
    public String getPreparationtime() { return this.preparationtime; }

    public void setGstpercentage(double gstpercentage) { this.gstpercentage = gstpercentage; }
    public double getGstpercentage() {
        if ((menutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE) && marinadeMeatTMCMenuItem != null)) {
            return marinadeMeatTMCMenuItem.getGstpercentage() + this.gstpercentage;
        } else {
            return this.gstpercentage;
        }
    }

    public void setTmcctgykey(String tmcctgykey) { this.tmcctgykey = tmcctgykey; }
    public String getTmcctgykey() { return this.tmcctgykey; }

    public void setItemcalories(int itemcalories) { this.itemcalories = itemcalories; }
    public int getItemcalories() { return this.itemcalories; }

    public void setPortionsize(String portionsize) { this.portionsize = portionsize; }
    public String getPortionsize() { return this.portionsize; }

    public void setItemuniquecode(String itemuniquecode) { this.itemuniquecode = itemuniquecode; }
    public String getItemuniquecode() { return this.itemuniquecode; }

    public void setCheckoutimageurl(String checkoutimageurl) { this.checkoutimageurl = checkoutimageurl; }
    public String getCheckoutimageurl() { return this.checkoutimageurl; }

    public void setVendorkey(String vendorkey) { this.vendorkey = vendorkey; }
    public String getVendorkey() { return vendorkey; }

    public void setItemweightdetails(String itemweightdetails) { this.itemweightdetails = itemweightdetails; }
    public String getItemweightdetails() { return this.itemweightdetails; }

    public void setItemcutdetails(String itemcutdetails) { this.itemcutdetails = itemcutdetails; }
    public String getItemcutdetails() { return this.itemcutdetails; }

    public void setSelecteditemweight(String selecteditemweight) { this.selecteditemweight = selecteditemweight; }
    public String getSelecteditemweight() { return this.selecteditemweight; }

    public void setSelecteditemcut(String selecteditemcut) { this.selecteditemcut = selecteditemcut; }
    public String getSelecteditemcut() { return this.selecteditemcut; }


    public void setItemdesp(String itemdesp) { this.itemdesp = itemdesp; }
    public String getItemdesp() { return this.itemdesp; }

    public void setStockbalance(double stockbalance) { this.stockbalance = stockbalance; }
    public double getStockbalance() { return this.stockbalance; }

    public void setReceivedstock(double receivedstock) { this.receivedstock = receivedstock; }
    public double getReceivedstock() { return this.receivedstock; }

    public void setStockincomingkey(String stockincomingkey) { this.stockincomingkey = stockincomingkey; }
    public String getStockincomingkey() { return this.stockincomingkey; }

    public void setTmcmenuavlkey(String tmcmenuavlkey) { this.tmcmenuavlkey = tmcmenuavlkey; }
    public String getTmcmenuavlkey() { return this.tmcmenuavlkey; }

    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getBarcode() { return this.barcode; }

    public void setInventorydetails(String inventorydetails) { this.inventorydetails = inventorydetails; }
    public String getInventorydetails() { return this.inventorydetails; }

    public boolean isShowInApp() { return this.isshowinapp; }

    // public void setKey(String key) { this.key = key; }
 /* public String getKey() {
        if (menutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
            String marinadecode = this.getItemuniquecode();
            if (this.marinadeMeatTMCMenuItem != null) {
                String meatcode = this.marinadeMeatTMCMenuItem.getItemuniquecode();
                String key = this.awsKey + "_" + marinadecode + "_" + meatcode;
                return key;
            } else {
                String key = this.awsKey + "_" + marinadecode;
                return key;
            }
        } else {
            return this.awsKey;
        }
    } */

    // public void setKey(String key) { this.key = key; }
    public String getKey() {
        if ((menutype != null) && (menutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE))) {
            String marinadecode = this.getItemuniquecode();
            if (this.marinadeMeatTMCMenuItem != null) {
                String meatcode = this.marinadeMeatTMCMenuItem.getItemuniquecode();
                String key = this.awsKey + "_" + marinadecode + "_" + meatcode;
                return key;
            } else {
                String key = this.awsKey + "_" + marinadecode;
                return key;
            }
        } else if ((this.selecteditemweight == null) && (this.selecteditemcut == null)) {
            return this.awsKey;
        } else {
            String weightkey = null; String cutkey = null; String suffixkey = null;
            if ((this.selecteditemweight != null) && (!TextUtils.isEmpty(selecteditemweight))) {
                try {
                    JSONObject itemweightjson = new JSONObject(selecteditemweight);
                    weightkey = itemweightjson.getString("weightkey");
                    suffixkey = weightkey;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if ((this.selecteditemcut != null) && (!TextUtils.isEmpty(selecteditemcut))) {
                try {
                    JSONObject itemcutjson = new JSONObject(selecteditemcut);
                    cutkey = itemcutjson.getString("cutkey");
                    if (cutkey != null) {
                        suffixkey = suffixkey + "_" + cutkey;
                    } else {
                        suffixkey = cutkey;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (suffixkey != null) {
                return this.awsKey + "_" + suffixkey;
            } else {
                return this.awsKey;
            }
        }
    }

    public void setAwsKey(String awsKey) { this.awsKey = awsKey; }
    public String getAwsKey() { return this.awsKey; }

    public boolean isWeightAndCutsCustomizable() {
        if ((itemcutdetails != null) && (!TextUtils.isEmpty(itemcutdetails))) {
            return true;
        }
        if ((itemweightdetails != null) && (!TextUtils.isEmpty(itemweightdetails))) {
            return true;
        }
        return false;
    }

 /* public String getKeyForMarinadeMeatItem() {
        String keyformenuitemmap = "";
        keyformenuitemmap = this.key + "_" + this.itemuniquecode;
        if (marinadeMeatTMCMenuItem != null) {
            keyformenuitemmap = keyformenuitemmap + marinadeMeatTMCMenuItem.getItemuniquecode();
        }
        return keyformenuitemmap;
    } */

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }
    public String getImageurl() { return this.imageurl; }

    public void setNetweight(String netweight) { this.netweight = netweight; }
    public String getNetweight() { return this.netweight; }

    public double getStrikedoutprice() { return this.strikedoutprice; }

    public void setSelectedqty(int selectedqty) { this.selectedqty = selectedqty; }
    public int getSelectedqty() {
        return this.selectedqty;
    }

    public void setMenutype(String menutype) { this.menutype = menutype; }
    public String getMenutype() { return this.menutype; }

    public void setMarinadelinkedcodes(String marinadelinkedcodes) { this.marinadelinkedcodes = marinadelinkedcodes; }
    public String getMarinadelinkedcodes() { return this.marinadelinkedcodes; }

    public void setMarinadeLinked(boolean isMarinadeLinked) { this.isMarinadeLinked = isMarinadeLinked; }
    public boolean isMarinadeLinked() { return isMarinadeLinked; }

    public void setMarinadeitemdesp(JSONArray marinadeitemdesp) { this.marinadeitemdesp = marinadeitemdesp; }
    public JSONArray getMarinadeitemdesp() { return marinadeitemdesp; }

    public void setMarinadeMeatTMCMenuItem(TMCMenuItem marinadeMeatTMCMenuItem) { this.marinadeMeatTMCMenuItem = marinadeMeatTMCMenuItem; }
    public TMCMenuItem getMarinadeMeatTMCMenuItem() { return this.marinadeMeatTMCMenuItem; }

    public void setPricetypeforpos(String pricetypeforpos) { this.pricetypeforpos = pricetypeforpos; }
    public String getPricetypeforpos() { return this.pricetypeforpos; }

    public JSONObject getJSONObjectStringForOrderDetails() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            double gstamount = tmcprice * (gstpercentage/100);
            if (gstamount > 0) {
                gstamount = Math.round(gstamount * 100.0) / 100.0;
            }
            double tmcpriceroundedval = Math.round(tmcprice * 100.0) / 100.0;
            jsonObject.put("checkouturl", imageurl);
            jsonObject.put("gstamount", gstamount);
            jsonObject.put("itemname", itemname);
            jsonObject.put("menuitemid", awsKey);
            jsonObject.put("grossweight", grossweight);
            jsonObject.put("netweight", netweight);
            jsonObject.put("portionsize", portionsize);
            jsonObject.put("quantity", selectedqty);
            if (appmarkuppercentage > 0) {
                jsonObject.put("appmarkuppercentage", appmarkuppercentage);
            }
            jsonObject.put("tmcprice", tmcpriceroundedval);
            jsonObject.put("tmcsubctgykey", tmcsubctgykey);
            if (grossweightingrams > 0) {
                jsonObject.put("grossweightingrams", grossweightingrams);
            }
            if (applieddiscpercentage > 0) {
                jsonObject.put("applieddiscpercentage", applieddiscpercentage);
                double strikedamount = tmcprice / ((100 - applieddiscpercentage) / 100);
                double discountamount = strikedamount - tmcprice;
                discountamount = Math.round(discountamount * 100.0) / 100.0;
                jsonObject.put("discountamount", discountamount);
            }

            if (marinadeMeatTMCMenuItem != null) {
                JSONObject marinadeMeatJson = new JSONObject();
                marinadeMeatJson.put("checkouturl", marinadeMeatTMCMenuItem.getImageurl());
                double gstamount1 = marinadeMeatTMCMenuItem.getTmcprice() * (marinadeMeatTMCMenuItem.getGstpercentage()/100);
                marinadeMeatJson.put("gstamount", gstamount1);
                marinadeMeatJson.put("itemname", marinadeMeatTMCMenuItem.getItemname());
                marinadeMeatJson.put("menuitemid", marinadeMeatTMCMenuItem.getKey());
                marinadeMeatJson.put("grossweight", marinadeMeatTMCMenuItem.getGrossweight());
                marinadeMeatJson.put("netweight", marinadeMeatTMCMenuItem.getNetweight());
                marinadeMeatJson.put("portionsize", marinadeMeatTMCMenuItem.getPortionsize());
                marinadeMeatJson.put("quantity", marinadeMeatTMCMenuItem.getSelectedqty());
                marinadeMeatJson.put("tmcprice", marinadeMeatTMCMenuItem.getTmcprice());
                marinadeMeatJson.put("tmcsubctgykey", marinadeMeatTMCMenuItem.getTmcsubctgykey());
                if (marinadeMeatTMCMenuItem.getGrossweightingrams() > 0) {
                    jsonObject.put("grossweightingrams", marinadeMeatTMCMenuItem.getGrossweightingrams());
                }
                jsonObject.put("marinadeitemdesp", marinadeMeatJson);
            }

            if ((selecteditemcut != null) && !(TextUtils.isEmpty(selecteditemcut))) {
                JSONObject jsonObject1 = new JSONObject(selecteditemcut);
                String cutname = jsonObject1.getString("cutname");
                jsonObject.put("cutname", cutname);
            }
            return jsonObject;
        } catch (Exception ex) {
            logEventInGAnalytics("getJSONObjectStringForOrderDetails exception", ex.getMessage(), this.mobileno);
            try {
                logEventInGAnalytics("getJSONObjectStringForOrderDetails ex lineno", "" + ex.getStackTrace()[0].getLineNumber(),
                                                                            this.mobileno);
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
            ex.printStackTrace();
        }
        return null;
    }

 /* public JSONObject getJSONObjectForMenuStockDetails() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("menuitemkey", awsKey);
            jsonObject.put("stockincomingkey", stockincomingkey);
            jsonObject.put("tmcmenuavlkey", tmcmenuavlkey);
            jsonObject.put("receivedstock", receivedstock);
            double orderedqty = selectedqty * grossweightingrams;
            jsonObject.put("orderedqty", orderedqty);
            jsonObject.put("itemname", itemname);
            jsonObject.put("tmcctgykey", tmcctgykey);
            jsonObject.put("tmcsubctgykey", tmcsubctgykey);
            jsonObject.put("barcode", barcode);
            if (pricetypeforpos.equalsIgnoreCase("tmcprice")) {
                jsonObject.put("stocktype", "unit");
            } else if (pricetypeforpos.equalsIgnoreCase("tmcpriceperkg")) {
                jsonObject.put("stocktype", "grams");
            }
            return jsonObject;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    } */

    public JSONArray getMenuStockDetailsArray() {
        try {
            if (inventorydetails == null) {
                JSONObject menudetailsjson = new JSONObject();
                menudetailsjson.put("menuitemkey", awsKey);
                double orderedqty = 0;
                menudetailsjson.put("itemname", itemname);
                menudetailsjson.put("tmcctgykey", tmcctgykey);
                menudetailsjson.put("tmcsubctgykey", tmcsubctgykey);
                menudetailsjson.put("barcode", barcode);
                if (pricetypeforpos.equalsIgnoreCase("tmcprice")) {
                    menudetailsjson.put("stocktype", "unit");
                    orderedqty = selectedqty;
                } else if (pricetypeforpos.equalsIgnoreCase("tmcpriceperkg")) {
                    orderedqty = selectedqty * grossweightingrams;
                    menudetailsjson.put("stocktype", "grams");
                }
                menudetailsjson.put("orderedqty", orderedqty);

                TMCMenuItemStockAvlDetails tmcMenuItemStockAvlDetails =
                        TMCMenuItemCatalog.getInstance().getMenuItemStockAvlDetails(awsKey);
                if (tmcMenuItemStockAvlDetails != null) {
                    menudetailsjson.put("stockincomingkey", tmcMenuItemStockAvlDetails.getStockincomingkey());
                    menudetailsjson.put("tmcmenuavlkey", tmcMenuItemStockAvlDetails.getKey());
                    menudetailsjson.put("receivedstock", tmcMenuItemStockAvlDetails.getReceivedstock());
                    menudetailsjson.put("allownegativestock", tmcMenuItemStockAvlDetails.isAllownegativestock());
                }
                JSONArray menudetailsjsonArray = new JSONArray();
                menudetailsjsonArray.put(menudetailsjson);
                return menudetailsjsonArray;
            } else {
                JSONObject jsonObject = new JSONObject(inventorydetails);
                JSONArray jsonArray = jsonObject.getJSONArray("inventorydetails");
                if (jsonArray == null) {
                    return null;
                }
                JSONArray menudetailsjsonarray = new JSONArray();
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject inventoryJSON = jsonArray.getJSONObject(i);
                    String inventorymenuitemkey = inventoryJSON.getString("menuitemkey");
                    TMCMenuItemStockAvlDetails tmcMenuItemStockAvlDetails =
                                        TMCMenuItemCatalog.getInstance().getMenuItemStockAvlDetails(inventorymenuitemkey);
                    TMCMenuItem tmcMenuItem = TMCMenuItemCatalog.getInstance().getTMCMenuItemForInventory(inventorymenuitemkey);

                    JSONObject menudetailsjson = new JSONObject();
                    menudetailsjson.put("menuitemkey", tmcMenuItem.getAwsKey());
                    menudetailsjson.put("itemname", tmcMenuItem.getItemname());
                    menudetailsjson.put("tmcctgykey", tmcMenuItem.getTmcctgykey());
                    menudetailsjson.put("tmcsubctgykey", tmcMenuItem.getTmcsubctgykey());
                    menudetailsjson.put("barcode", tmcMenuItem.getBarcode());
                    if (tmcMenuItem.getPricetypeforpos().equalsIgnoreCase("tmcprice")) {
                        menudetailsjson.put("stocktype", "unit");
                    } else if (tmcMenuItem.getPricetypeforpos().equalsIgnoreCase("tmcpriceperkg")) {
                        menudetailsjson.put("stocktype", "grams");
                    }

                    if (tmcMenuItemStockAvlDetails != null) {
                        double orderedqty = 0;
                        if (!inventoryJSON.isNull("grossweightingrams")) {
                            try {
                                String grosswtstr = inventoryJSON.getString("grossweightingrams");
                                double grosswt = Double.parseDouble(grosswtstr);
                                orderedqty = selectedqty * grosswt;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            orderedqty = selectedqty * grossweightingrams;
                        }
                        menudetailsjson.put("orderedqty", orderedqty);
                        menudetailsjson.put("stockincomingkey", tmcMenuItemStockAvlDetails.getStockincomingkey());
                        menudetailsjson.put("tmcmenuavlkey", tmcMenuItemStockAvlDetails.getKey());
                        menudetailsjson.put("receivedstock", tmcMenuItemStockAvlDetails.getReceivedstock());
                        menudetailsjson.put("allownegativestock", tmcMenuItemStockAvlDetails.isAllownegativestock());
                    }
                    menudetailsjsonarray.put(menudetailsjson);
                }
                return menudetailsjsonarray;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int compareTo(TMCMenuItem o) {
        try {
            /* For Ascending order do like this */
            return (this.displayno - o.getDisplayno());
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
        parcel.writeString(itemname);
        parcel.writeString(grossweight);
        parcel.writeDouble(tmcprice);
        parcel.writeString(vendorname);
        parcel.writeDouble(applieddiscpercentage);
        parcel.writeInt(displayno);
        parcel.writeByte((byte) (itemavailability ? 1 : 0));
        parcel.writeString(tmcsubctgykey);
        parcel.writeString(preparationtime);
        parcel.writeDouble(gstpercentage);
        parcel.writeString(tmcctgykey);
        parcel.writeInt(itemcalories);
        parcel.writeString(portionsize);
        parcel.writeString(itemuniquecode);
        parcel.writeString(checkoutimageurl);
        parcel.writeString(vendorkey);
        parcel.writeString(key);
        parcel.writeString(imageurl);
        parcel.writeString(netweight);
        parcel.writeDouble(strikedoutprice);
        parcel.writeInt(selectedqty);
        parcel.writeByte((byte) (isMarinadeLinked ? 1 : 0));
        parcel.writeString(awsKey);
        parcel.writeString(marinadelinkedcodes);
        parcel.writeDouble(grossweightingrams);
        parcel.writeString(itemdesp);
        parcel.writeDouble(stockbalance);
        parcel.writeString(stockincomingkey);
        parcel.writeString(tmcmenuavlkey);
        parcel.writeDouble(receivedstock);
        parcel.writeString(pricetypeforpos);
        parcel.writeString(inventorydetails);
        parcel.writeByte((byte) (isshowinapp ? 1 : 0));
        parcel.writeDouble(appmarkuppercentage);
    }


    public String toString() {
        String key = "key " + getKey();
        String itemname = " itemname " + this.itemname;
        String itemprice = " itemprice " + this.tmcprice;
        String gstpercentage = " gstpercentage " + this.gstpercentage;
        String menutype = " menutype " + this.menutype;
        String selqty = " selqty " + this.selectedqty;
        String str = key + itemname + itemprice + gstpercentage + menutype + selqty;
        if (marinadeMeatTMCMenuItem != null) {
            str = str + " MarinadeMenuItem: " + marinadeMeatTMCMenuItem.toString();
        }
        return str;
    }

    // Code for Google Analytics logs
    public Tracker tmcTracker;
    public String mobileno;
    public void setTMCTracker(Tracker tmcTracker) { this.tmcTracker = tmcTracker; }
    public void setMobileno(String mobileno) { this.mobileno = mobileno; }
    private void logEventInGAnalytics(String category, String action, String label) {
        try {
            String label1 = label.substring(2);
            if (tmcTracker != null) {
                tmcTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(category)
                        .setAction(action).setLabel(label1)
                        .build());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
