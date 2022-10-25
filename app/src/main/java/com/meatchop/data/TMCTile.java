package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class TMCTile implements Comparable<TMCTile>, Parcelable {

    private String tmcctgykey;
    private String desp;
    private String tmcctgyname;
    private String tmcsubctgyname;
    private String displayno;
    private String key;
    private String imageurl;
    private String name;
    private String tmcsubctgykey;

    public TMCTile() {

    }

    public TMCTile(JSONObject jsonObject) {
        try {
            this.key = jsonObject.getString("key");
            this.name = jsonObject.getString("name");
            this.tmcctgyname = jsonObject.getString("tmcctgyname");
            this.tmcsubctgyname = jsonObject.getString("tmcsubctgyname");
            this.tmcctgykey = jsonObject.getString("tmcctgykey");
            this.tmcsubctgykey = jsonObject.getString("tmcsubctgykey");
            this.displayno = jsonObject.getString("displayno");
            this.desp = jsonObject.getString("desp");
            this.imageurl = jsonObject.getString("imageurl");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected TMCTile(Parcel in) {
        tmcctgykey = in.readString();
        desp = in.readString();
        tmcctgyname = in.readString();
        tmcsubctgyname = in.readString();
        displayno = in.readString();
        key = in.readString();
        imageurl = in.readString();
        name = in.readString();
        tmcsubctgykey = in.readString();
    }

    public static final Creator<TMCTile> CREATOR = new Creator<TMCTile>() {
        @Override
        public TMCTile createFromParcel(Parcel in) {
            return new TMCTile(in);
        }

        @Override
        public TMCTile[] newArray(int size) {
            return new TMCTile[size];
        }
    };

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setTmcctgyname(String tmcctgyname) { this.tmcctgyname = tmcctgyname; }
    public String getTmcctgyname() { return tmcctgyname; }

    public void setTmcsubctgyname(String tmcsubctgyname) { this.tmcsubctgyname = tmcsubctgyname; }
    public String getTmcsubctgyname() { return tmcsubctgyname; }

    public void setTmcctgykey(String tmcctgykey) { this.tmcctgykey = tmcctgykey; }
    public String getTmcctgykey() { return tmcctgykey; }

    public void setTmcsubctgykey(String tmcsubctgykey) { this.tmcsubctgykey = tmcsubctgykey; }
    public String getTmcsubctgykey() { return tmcsubctgykey; }

    public void setDisplayno(String displayno) { this.displayno = displayno; }
    public String getDisplayno() { return displayno; }

    public void setDesp(String desp) { this.desp = desp; }
    public String getDesp() { return desp; }

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }
    public String getImageurl() { return imageurl; }

    @Override
    public int compareTo(TMCTile o) {
        try {
            int displayno = Integer.parseInt(o.getDisplayno());
            /* For Ascending order do like this */
            int thisdisplayno = Integer.parseInt(this.displayno);
            return (thisdisplayno-displayno);
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
        parcel.writeString(tmcctgykey);
        parcel.writeString(desp);
        parcel.writeString(tmcctgyname);
        parcel.writeString(tmcsubctgyname);
        parcel.writeString(displayno);
        parcel.writeString(key);
        parcel.writeString(imageurl);
        parcel.writeString(name);
        parcel.writeString(tmcsubctgykey);
    }
}
