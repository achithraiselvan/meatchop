package com.meatchop.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.meatchop.R;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCVendor;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

public class StoreAddressDetailsAdapter extends RecyclerView.Adapter<StoreAddressDetailsAdapter.ViewHolder> {

    public List<TMCVendor> tmcVendorList = null;
    private Context context;
    private Handler handler;
    private String mappedvendorkey;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TMCTextView storetitle_text;
        TMCTextView storeaddress_text;
        View callstore_layout;
        View getdirections_layout;
        View storedetails_layout;
        TMCTextView serviceabledesp_text;

        ViewHolder(View view) {
            super(view);
            storetitle_text = (TMCTextView) view.findViewById(R.id.storetitle_text);
            storeaddress_text = (TMCTextView) view.findViewById(R.id.storeaddress_text);
            callstore_layout = view.findViewById(R.id.callstore_layout);
            getdirections_layout = view.findViewById(R.id.getdirections_layout);
            storedetails_layout = view.findViewById(R.id.storedetails_layout);
            serviceabledesp_text = (TMCTextView) view.findViewById(R.id.serviceabledesp_text);
        }
    }

    public StoreAddressDetailsAdapter(Context context, List<TMCVendor> vendorList, String mappedvendorkey) {
        this.context = context;
        this.tmcVendorList = new ArrayList<TMCVendor>();
        this.tmcVendorList.addAll(vendorList);
        this.mappedvendorkey = mappedvendorkey;
    }

    public void setHandler(Handler handler) { this.handler = handler; }

    @NonNull
    @Override
    public StoreAddressDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_storeaddressdetails_layout, parent, false);
        return new StoreAddressDetailsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TMCVendor tmcVendor =  tmcVendorList.get(position);
        String storeaddressforapp = tmcVendor.getStoreaddressforapp();
        String storeaddresstitleforapp = tmcVendor.getStoreaddresstitleforapp();

        if ((mappedvendorkey != null) && (tmcVendor.getKey().equalsIgnoreCase(mappedvendorkey))) {
            holder.storedetails_layout.setBackground(context.getResources().getDrawable(R.drawable.mappedstoredetailscontactus_border));
            holder.serviceabledesp_text.setVisibility(View.VISIBLE);
        } else {
            holder.serviceabledesp_text.setVisibility(View.GONE);
        }

        holder.storetitle_text.setText(storeaddresstitleforapp);
        holder.storeaddress_text.setText(storeaddressforapp);

        holder.callstore_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storeno = tmcVendor.getVendormobile();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "callstoreclicked");
                bundle.putString("storeno", storeno);
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });

        holder.getdirections_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String googlemaplocationurl = tmcVendor.getGooglemaplocationurl();
                double loclat = tmcVendor.getLocationlat();
                double loclong = tmcVendor.getLocationlong();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "getdirectionsclicked");
                bundle.putDouble("loclat", loclat);
                bundle.putDouble("loclong", loclong);
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return tmcVendorList.size();
    }

}
