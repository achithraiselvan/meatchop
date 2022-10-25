package com.meatchop.models;

public class Bannerdetails {

    private String imageurl;
    private boolean isclickable;
    private boolean isactive;

    public Bannerdetails(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImageurl() { return imageurl; }
    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

    public boolean getIsclickable() { return isclickable; }
    public void setIsclickable(boolean isclickable) { this.isclickable = isclickable; }

    public boolean getIsactive() { return isactive; }
    public void setIsactive(boolean isactive) { this.isactive = isactive; }

}
