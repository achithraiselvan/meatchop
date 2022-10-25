package com.meatchop.adapters;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meatchop.R;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TMCProfileAddressListAdapter extends RecyclerView.Adapter<TMCProfileAddressListAdapter.ViewHolder> {

    public List<TMCUserAddress> tmcUserAddressList = null;
    private Context context;
    private Handler handler;
    private ArrayList<ViewHolder> viewHolderList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TMCTextView savedaddresses_text;
        ImageView imageview_home;
        ImageView imageview_others;
        TMCTextView addresstype_text;
        TMCTextView addressline_text;
        View fieldseperator_layout;
        View addresslayout_btn;
        View editaddress_btn;
        View deleteaddress_btn;

        ViewHolder(View view) {
            super(view);
            savedaddresses_text = (TMCTextView) view.findViewById(R.id.savedaddresses_text);
            imageview_home = (ImageView) view.findViewById(R.id.imageview_home);
            imageview_others = (ImageView) view.findViewById(R.id.imageview_others);
            addresstype_text = (TMCTextView) view.findViewById(R.id.addresstype_text);
            addressline_text = (TMCTextView) view.findViewById(R.id.addressline_text);
            fieldseperator_layout = view.findViewById(R.id.fieldseperator_layout);
            addresslayout_btn = view.findViewById(R.id.addresslayout_btn);
            editaddress_btn = view.findViewById(R.id.editaddress_btn);
            deleteaddress_btn = view.findViewById(R.id.deleteaddress_btn);
        }
    }

    public TMCProfileAddressListAdapter(Context context, List<TMCUserAddress> userAddressList) {
        this.context = context;
        this.tmcUserAddressList = new ArrayList<TMCUserAddress>();
        this.tmcUserAddressList.addAll(userAddressList);
    }

    public void setHandler(Handler handler) { this.handler = handler; }

    @NonNull
    @Override
    public TMCProfileAddressListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tmcprofileaddresslist_layout, parent, false);
        return new TMCProfileAddressListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TMCUserAddress tmcUserAddress =  tmcUserAddressList.get(position);
        if (tmcUserAddress.getAddresstype().equalsIgnoreCase("Home")) {
            holder.imageview_home.setVisibility(View.VISIBLE);
            holder.imageview_others.setVisibility(View.GONE);
        } else {
            holder.imageview_home.setVisibility(View.GONE);
            holder.imageview_others.setVisibility(View.VISIBLE);
        }
        holder.addresstype_text.setText(tmcUserAddress.getAddresstype());
        holder.addressline_text.setText(tmcUserAddress.getAddressline1() + " ," + tmcUserAddress.getAddressline2());
        if (position == 0) {
            holder.savedaddresses_text.setVisibility(View.VISIBLE);
        } else {
            holder.savedaddresses_text.setVisibility(View.GONE);
        }
        holder.editaddress_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = tmcUserAddress.getKey();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "editbtnclicked");
                bundle.putString("useraddresskey", key);
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });
        holder.deleteaddress_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = tmcUserAddress.getKey();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "deletebtnclicked");
                bundle.putString("useraddresskey", key);
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tmcUserAddressList.size();
    }


}
