package com.meatchop.data;

import org.json.JSONObject;

public class Mealkitrecipes implements Comparable<Mealkitrecipes> {

    private String key;
    private int displayno;
    private String title;
    private String desp;
    private String tmcmenuitemkey;
    private JSONObject jsonObject;

    public Mealkitrecipes(JSONObject jsonObject) {
        try {
            this.jsonObject = jsonObject;
            if (jsonObject.has("key")) {
                this.key = jsonObject.getString("key");
            }
            if (jsonObject.has("title")) {
                this.title = jsonObject.getString("title");
            }
            if (jsonObject.has("desp")) {
                this.desp = jsonObject.getString("desp");
            }
            if (jsonObject.has("tmcmenuitemkey")) {
                this.tmcmenuitemkey = jsonObject.getString("tmcmenuitemkey");
            }
            if (jsonObject.has("displayno")) {
                this.displayno = jsonObject.getInt("displayno");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setDesp(String desp) { this.desp = desp; }
    public String getDesp() { return desp; }

    public void setTmcmenuitemkey(String tmcmenuitemkey) { this.tmcmenuitemkey = tmcmenuitemkey; }
    public String getTmcmenuitemkey() { return tmcmenuitemkey; }

    public void setDisplayno(int displayno) { this.displayno = displayno; }
    public int getDisplayno() { return this.displayno; }

    @Override
    public int compareTo(Mealkitrecipes o) {
        try {
            /* For Ascending order do like this */
            return (this.displayno - o.getDisplayno());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }



}
