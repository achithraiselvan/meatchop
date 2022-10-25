package com.meatchop.data;

import org.json.JSONObject;

public class TMCVendor {

    private String key;
    private String name;
    private String vendormobile;
    private double locationlat;
    private double locationlong;
    private String status;
    private String fssaino;
    private String addressline1;
    private String addressline2;
    private String landmark;
    private String pincode;
    private String bannerurl;
    private boolean showbanner = false;
    private String googlebusinessurl;
    private String accountscreenurl;
    private String jsonObjString;
    private String storeaddressforapp;
    private String storeaddresstitleforapp;
    private String googlemaplocationurl;
    private boolean inventorycheck;

    public TMCVendor(String key, String name, String vendormobile, double locationlat, double locationlong,
                     String status, String fssaino) {
        this.key = key;
        this.name = name;
        this.vendormobile = vendormobile;
        this.locationlat = locationlat;
        this.locationlong = locationlong;
        this.status = status;
        this.fssaino = fssaino;
    }

    public TMCVendor(JSONObject jsonObject) {
        try {
            this.key = jsonObject.getString("key");
            this.name = jsonObject.getString("name");
            this.vendormobile = jsonObject.getString("vendormobile");
            this.locationlat = jsonObject.getDouble("locationlat");
            this.locationlong = jsonObject.getDouble("locationlong");
            this.status = jsonObject.getString("status");
            this.fssaino = jsonObject.getString("vendorfssaino");
            this.addressline1 = jsonObject.getString("addressline1");
            this.addressline2 = jsonObject.getString("addressline2");
            this.landmark = jsonObject.getString("landmark");
            this.pincode = jsonObject.getString("pincode");

            if (jsonObject.has("accountscreenurl")) {
                this.accountscreenurl = jsonObject.getString("accountscreenurl");
            }
            if (jsonObject.has("bannerurl")) {
                this.bannerurl = jsonObject.getString("bannerurl");
            }
            if (jsonObject.has("googlebusinessurl")) {
                this.googlebusinessurl = jsonObject.getString("googlebusinessurl");
            }
            if (jsonObject.has("showbanner")) {
                this.showbanner = jsonObject.getBoolean("showbanner");
            }
            if (!jsonObject.isNull("storeaddressforapp")) {
                this.storeaddressforapp = jsonObject.getString("storeaddressforapp");
            }
            if (!jsonObject.isNull("storeaddresstitleforapp")) {
                this.storeaddresstitleforapp = jsonObject.getString("storeaddresstitleforapp");
            }
            if (!jsonObject.isNull("googlemaplocationurl")) {
                this.googlemaplocationurl = jsonObject.getString("googlemaplocationurl");
            }
            if (!jsonObject.isNull("inventorycheck")) {
                String inventorycheck = jsonObject.getString("inventorycheck");
                try {
                    this.inventorycheck = Boolean.parseBoolean(inventorycheck);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            this.jsonObjString = jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setVendormobile(String vendormobile) { this.vendormobile = vendormobile; }
    public String getVendormobile() { return vendormobile; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setLocationlat(double locationlat) { this.locationlat = locationlat; }
    public double getLocationlat() { return locationlat; }

    public void setLocationlong(double locationlong) { this.locationlong = locationlong; }
    public double getLocationlong() { return locationlong; }

    public void setFssaino(String fssaino) { this.fssaino = fssaino; }
    public String getFssaino() { return fssaino; }

    public void setAddressline1(String addressline1) { this.addressline1 = addressline1; }
    public String getAddressline1() { return addressline1; }

    public void setAddressline2(String addressline2) { this.addressline2 = addressline2; }
    public String getAddressline2() { return addressline2; }

    public void setLandmark(String landmark) { this.landmark = landmark; }
    public String getLandmark() { return landmark; }

    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getPincode() { return pincode; }

    public String getBannerurl() { return this.bannerurl; }
    public boolean isShowbanner() { return this.showbanner; }
    public String getGooglebusinessurl() { return this.googlebusinessurl; }
    public String getAccountscreenurl() { return this.accountscreenurl; }
    public String getStoreaddressforapp() { return this.storeaddressforapp; }
    public String getStoreaddresstitleforapp() { return this.storeaddresstitleforapp; }
    public String getGooglemaplocationurl() { return this.googlemaplocationurl; }
    public boolean isInventorycheck() { return this.inventorycheck; }

    public String getJsonObjString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", key);
            jsonObject.put("name", name);
            jsonObject.put("vendormobile", vendormobile);
            jsonObject.put("locationlat", locationlat);
            jsonObject.put("locationlong", locationlong);
            jsonObject.put("status", status);
            jsonObject.put("vendorfssaino", fssaino);
            jsonObject.put("addressline1", addressline1);
            jsonObject.put("addressline2", addressline2);
            jsonObject.put("landmark", landmark);
            jsonObject.put("pincode", pincode);
            jsonObject.put("bannerurl", bannerurl);
            jsonObject.put("showbanner", showbanner);
            jsonObject.put("googlebusinessurl", googlebusinessurl);
            jsonObject.put("accountscreenurl", accountscreenurl);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }

}
