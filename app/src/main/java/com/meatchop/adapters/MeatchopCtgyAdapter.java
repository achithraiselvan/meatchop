package com.meatchop.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;


import com.meatchop.R;
import com.meatchop.widget.TMCTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MeatchopCtgyAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Typeface titleFace;
    public List<String> ctgyList = null;
    private Context context;
    private Handler handler;


    ViewHolder holder;

    LinearLayout detailFieldsArea;
    String[] orderList;

    public MeatchopCtgyAdapter(Context context, List<String> ctgyItems) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.ctgyList = new ArrayList<String>();
        ctgyList.addAll(ctgyItems);
    }

    @Override
    public int getCount() {
        return ctgyList.size();
    }

    @Override
    public String getItem(int position) {
        return ctgyList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setHandler(Handler handler) { this.handler = handler; }

    public List<String> getItemList() { return ctgyList; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder holder;

     // Log.d("ItemStatusAdapter" , "getView position and convertview "+position+" " +convertView);
        if (convertView != null) {
            ViewHolder holder1 = (ViewHolder) convertView.getTag();
            if (position != holder1.position) {
                convertView = null;
            }
        }

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.meatchopctgy_adapter_layout, parent,
                    false);
            if (convertView != null) {
                holder.position = position;
                holder.container = convertView.findViewById(R.id.list_item_container);
                holder.itemname_text = (TMCTextView) convertView.findViewById(R.id.itemname_text);
                holder.itemname_layout = convertView.findViewById(R.id.itemname_layout);
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final View holderView = convertView;

        String ctgy = ctgyList.get(position);
        holder.itemname_text.setText(ctgy);

        holder.itemname_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MeatchopCtgyAdapter.ViewHolder holder = (MeatchopCtgyAdapter.ViewHolder) holderView.getTag();
                String ctgy = ctgyList.get(holder.position);
                sendHandlerMessage("itemclicked", ctgy);
            }
        });
        return convertView;
    }

    private void sendHandlerMessage(String message, String ctgyname) {
        Message msg =  new Message();
        Bundle bundle = new Bundle();
        bundle.putString("menutype", message);
        bundle.putString("ctgyname" , ctgyname);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }


    class ViewHolder {
        int position;
        View container;
        View itemname_layout;
        TMCTextView itemname_text;
    }


    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String currentTime = sdf.format(new Date());
        return currentTime;
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }


}
