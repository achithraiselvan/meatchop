package com.meatchop.data;

import android.text.TextUtils;

import org.json.JSONObject;

public class TMCUser {
    private String key;
    private String name;
    private String email;
    private String mobileno;
    private String createdtime;
    private String updatedtime;
    private String authorizationcode;
    private String deviceos;
    private String appversion;
    private String fcmtoken;
    private String uniquekey;
    private String usertype;
    private String createddate;

    public TMCUser(String mobileno) {
        this.mobileno = mobileno;
    }

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setUniquekey(String uniquekey) { this.uniquekey = uniquekey; }
    public String getUniquekey() { return uniquekey; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setMobileno(String mobileno) { this.mobileno = mobileno; }
    public String getMobileno() { return mobileno; }

    public void setCreatedtime(String createdtime) { this.createdtime = createdtime; }
    public String getCreatedtime() { return createdtime; }

    public void setCreateddate(String createddate) { this.createddate = createddate; }
    public String getCreateddate() { return createddate; }

    public void setUpdatedtime(String updatedtime) { this.updatedtime = updatedtime; }
    public String getUpdatedtime() { return updatedtime; }

    public void setAuthorizationcode(String authorizationcode) { this.authorizationcode = authorizationcode; }
    public String getAuthorizationcode() { return authorizationcode; }

    public void setDeviceos(String deviceos) { this.deviceos = deviceos; }
    public String getDeviceos() { return deviceos; }

    public void setAppversion(String appversion) { this.appversion = appversion; }
    public String getAppversion() { return appversion; }

    public void setFcmtoken(String fcmtoken) { this.fcmtoken = fcmtoken; }
    public String getFcmtoken() { return fcmtoken; }

    public void setUsertype(String usertype) { this.usertype = usertype; }
    public String getUsertype() { return usertype; }



    public String getJSONObjectForInsert() {
        try {
            JSONObject jsonObject = new JSONObject();

            if ((name != null) && (!TextUtils.isEmpty(name))) {
                jsonObject.put("name", name);
            }
            if ((email != null) && (!TextUtils.isEmpty(email))) {
                jsonObject.put("email", email);
            }
            if ((mobileno != null) && (!TextUtils.isEmpty(mobileno))) {
                jsonObject.put("mobileno", mobileno);
            }
            if ((createdtime != null) && (!TextUtils.isEmpty(createdtime))) {
                jsonObject.put("createdtime", createdtime);
            }
            if ((updatedtime != null) && (!TextUtils.isEmpty(updatedtime))) {
                jsonObject.put("updatedtime", updatedtime);
            }
            if ((authorizationcode != null) && (!TextUtils.isEmpty(authorizationcode))) {
                jsonObject.put("authorizationcode", authorizationcode);
            }
            return jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }



}
