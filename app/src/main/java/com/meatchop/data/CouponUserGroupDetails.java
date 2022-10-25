package com.meatchop.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class CouponUserGroupDetails implements Parcelable {

    private String key;
    private String couponkey;
    private String groupname;
    private String status;
    private String userkey;

    public CouponUserGroupDetails(JSONObject jsonObject) {
        try {
            if (jsonObject.has("key")) {
                this.key = jsonObject.getString("key");
            }
            if (jsonObject.has("couponkey")) {
                this.couponkey = jsonObject.getString("couponkey");
            }
            if (jsonObject.has("groupname")) {
                this.groupname = jsonObject.getString("groupname");
            }
            if (jsonObject.has("status")) {
                this.status = jsonObject.getString("status");
            }
            if (jsonObject.has("userkey")) {
                this.userkey = jsonObject.getString("userkey");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected CouponUserGroupDetails(Parcel in) {
        key = in.readString();
        couponkey = in.readString();
        groupname = in.readString();
        status = in.readString();
        userkey = in.readString();
    }

    public static final Creator<CouponUserGroupDetails> CREATOR = new Creator<CouponUserGroupDetails>() {
        @Override
        public CouponUserGroupDetails createFromParcel(Parcel in) {
            return new CouponUserGroupDetails(in);
        }

        @Override
        public CouponUserGroupDetails[] newArray(int size) {
            return new CouponUserGroupDetails[size];
        }
    };

    public String getKey() { return this.key; }
    public String getCouponkey() { return this.couponkey; }
    public String getGroupname() { return this.groupname; }
    public String getStatus() { return this.status; }
    public String getUserkey() { return this.userkey; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(couponkey);
        dest.writeString(groupname);
        dest.writeString(status);
        dest.writeString(userkey);
    }
}
