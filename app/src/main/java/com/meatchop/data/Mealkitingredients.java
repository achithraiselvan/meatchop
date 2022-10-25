package com.meatchop.data;

import android.util.Log;

import org.json.JSONObject;

public class Mealkitingredients implements Comparable<Mealkitingredients> {

    private String key;
    private int displayno;
    private String itemname;
    private String netweight;
    private String tmcmenuitemkey;
    private boolean fromyourkitchen = false;
    private JSONObject jsonObject;

    public Mealkitingredients(JSONObject jsonObject) {
        try {
            this.jsonObject = jsonObject;

            if (jsonObject.has("key")) {
                this.key = jsonObject.getString("key");
            }
            if (jsonObject.has("itemname")) {
                this.itemname = jsonObject.getString("itemname");
            }
            if (jsonObject.has("netweight")) {
                this.netweight = jsonObject.getString("netweight");
            }
            if (jsonObject.has("tmcmenuitemkey")) {
                this.tmcmenuitemkey = jsonObject.getString("tmcmenuitemkey");
            }

            if (jsonObject.has("displayno")) {
                this.displayno = jsonObject.getInt("displayno");
            }
            if (jsonObject.has("fromyourkitchen")) {
                this.fromyourkitchen = jsonObject.getBoolean("fromyourkitchen");
            }
            Log.d("Mealkitingredients", "this.itemname "+this.itemname);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setItemname(String itemname) { this.itemname = itemname; }
    public String getItemname() { return itemname; }

    public void setNetweight(String netweight) { this.netweight = netweight; }
    public String getNetweight() { return netweight; }

    public void setTmcmenuitemkey(String tmcmenuitemkey) { this.tmcmenuitemkey = tmcmenuitemkey; }
    public String getTmcmenuitemkey() { return tmcmenuitemkey; }

    public void setDisplayno(int displayno) { this.displayno = displayno; }
    public int getDisplayno() { return this.displayno; }

    public void setFromyourkitchen(boolean fromyourkitchen) { this.fromyourkitchen = fromyourkitchen; }
    public boolean getFromyourkitchen() { return this.fromyourkitchen; }

    @Override
    public int compareTo(Mealkitingredients o) {
        try {
            /* For Ascending order do like this */
            return (this.displayno - o.getDisplayno());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

}
