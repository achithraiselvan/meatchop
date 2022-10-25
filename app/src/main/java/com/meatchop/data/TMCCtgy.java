package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.TreeMap;

public class TMCCtgy implements Parcelable {

    private String name;
    private String imageurl;
    private TreeMap<Integer, String> subCtgyMap;  // {Display no -- Subctgyname}
    private ArrayList<String> subctgylist;

    public TMCCtgy(String name) {
        this.name = name;
    }

    public TMCCtgy(String name, String imageurl, ArrayList<String> subctgylist) {
        this.name = name;
        this.imageurl = imageurl;
        this.subctgylist = subctgylist;
    }

    protected TMCCtgy(Parcel in) {
        name = in.readString();
        imageurl = in.readString();
        subctgylist = in.createStringArrayList();
    }

    public static final Creator<TMCCtgy> CREATOR = new Creator<TMCCtgy>() {
        @Override
        public TMCCtgy createFromParcel(Parcel in) {
            return new TMCCtgy(in);
        }

        @Override
        public TMCCtgy[] newArray(int size) {
            return new TMCCtgy[size];
        }
    };

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }
    public String getImageurl() { return imageurl; }

    public void setSubCtgyList(ArrayList<String> subctgylist) { this.subctgylist = subctgylist; }
    public ArrayList<String> getSubCtgyList() { return subctgylist; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(imageurl);
        parcel.writeStringList(subctgylist);
    }
}
