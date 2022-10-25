package com.meatchop.data;

import org.json.JSONObject;

public class Mealkitingredient {

    private String key;
    private String tmcmenuitemkey;
    private String itemname;
    private int displayno;
    private double baseamount;
    private double gstamount;
    private int gstpercentage;
    private double totalamount;
    private String ingredientctgy;
    private String actualqty;
    private String availableqty;
    private boolean isselected = true;

    public Mealkitingredient() {

    }

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setTmcmenuitemkey(String tmcmenuitemkey) { this.tmcmenuitemkey = tmcmenuitemkey; }
    public String getTmcmenuitemkey() { return tmcmenuitemkey; }

    public void setItemname(String itemname) { this.itemname = itemname; }
    public String getItemname() { return itemname; }

    public void setDisplayno(int displayno) { this.displayno = displayno; }
    public int getDisplayno() { return displayno; }

    public void setBaseamount(double baseamount) { this.baseamount = baseamount; }
    public double getBaseamount() { return baseamount; }

    public void setGstamount(double gstamount) { this.gstamount = gstamount; }
    public double getGstamount() { return gstamount; }

    public void setGstpercentage(int gstpercentage) { this.gstpercentage = gstpercentage; }
    public int getGstpercentage() { return gstpercentage; }

    public void setTotalamount(double totalamount) { this.totalamount = totalamount; }
    public double getTotalamount() { return totalamount; }

    public void setIngredientctgy(String ingredientctgy) { this.ingredientctgy = ingredientctgy; }
    public String getIngredientctgy() { return ingredientctgy; }

    public void setActualqty(String actualqty) { this.actualqty = actualqty; }
    public String getActualqty() { return actualqty; }

    public void setAvailableqty(String availableqty) { this.availableqty = availableqty; }
    public String getAvailableqty() { return availableqty; }

    public void setIsselected(boolean isselected) { this.isselected = isselected; }
    public boolean isSelected() { return isselected; }



}
