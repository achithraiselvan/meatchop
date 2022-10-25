package com.meatchop.data;


import android.os.Parcel;
import android.os.Parcelable;

public class Paymenttransaction implements Parcelable {

    private String key;
    private String mobileno;
    private String orderid;
    private String merchantorderid;
    private String paymentmode;
    private String paymenttype;
    private String transactiontime;
    private double transactionamount;
    private String status;
    private String desp;
    private String merchantpaymentid;

    public Paymenttransaction() {

    }

    public Paymenttransaction(String mobileno, String orderid, String merchantorderid, String transactiontime, String paymentmode,
                              double transactionamount, String status) {
        this.mobileno = mobileno;
        this.orderid = orderid;
        this.merchantorderid = merchantorderid;
        this.transactiontime = transactiontime;
        this.paymentmode = paymentmode;
        this.transactionamount = transactionamount;
        this.status = status;
    }

    protected Paymenttransaction(Parcel in) {
        key = in.readString();
        mobileno = in.readString();
        orderid = in.readString();
        merchantorderid = in.readString();
        paymentmode = in.readString();
        paymenttype = in.readString();
        transactiontime = in.readString();
        transactionamount = in.readDouble();
        status = in.readString();
        desp = in.readString();
        merchantpaymentid = in.readString();
    }

    public static final Creator<Paymenttransaction> CREATOR = new Creator<Paymenttransaction>() {
        @Override
        public Paymenttransaction createFromParcel(Parcel in) {
            return new Paymenttransaction(in);
        }

        @Override
        public Paymenttransaction[] newArray(int size) {
            return new Paymenttransaction[size];
        }
    };

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setMobileno(String mobileno) { this.mobileno = mobileno; }
    public String getMobileno() { return mobileno; }

    public void setOrderid(String orderid) { this.orderid = orderid; }
    public String getOrderid() { return orderid; }

    public void setMerchantorderid(String merchantorderid) { this.merchantorderid = merchantorderid; }
    public String getMerchantorderid() { return merchantorderid; }

    public void setPaymentmode(String paymentmode) { this.paymentmode = paymentmode; }
    public String getPaymentmode() { return paymentmode; }

    public void setPaymenttype(String paymenttype) { this.paymenttype = paymenttype; }
    public String getPaymenttype() { return paymenttype; }

    public void setTransactiontime(String transactiontime) { this.transactiontime = transactiontime; }
    public String getTransactiontime() { return transactiontime; }

    public void setTransactionamount(double transactionamount) { this.transactionamount = transactionamount; }
    public double getTransactionamount() { return transactionamount; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setDesp(String desp) { this.desp = desp; }
    public String getDesp() { return desp; }

    public void setMerchantpaymentid(String merchantpaymentid) { this.merchantpaymentid = merchantpaymentid; }
    public String getMerchantpaymentid() { return merchantpaymentid; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(mobileno);
        parcel.writeString(orderid);
        parcel.writeString(merchantorderid);
        parcel.writeString(paymentmode);
        parcel.writeString(paymenttype);
        parcel.writeString(transactiontime);
        parcel.writeDouble(transactionamount);
        parcel.writeString(status);
        parcel.writeString(desp);
        parcel.writeString(merchantpaymentid);
    }
}
