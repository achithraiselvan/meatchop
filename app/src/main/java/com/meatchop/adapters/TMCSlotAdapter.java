package com.meatchop.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meatchop.R;
import com.meatchop.activities.OrderSummaryActivity;
import com.meatchop.data.Deliveryslotdetails;
import com.meatchop.data.Deliveryslots;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCUtil;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class TMCSlotAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private Handler handler;
    private ArrayList<Deliveryslots> deliveryslotdetailsList;
    private ArrayList<String> timeSlotList;
    private HashMap<String, String> slotDetailsMap;
    private Deliveryslots selecteddeliveryslotdetails;

    private ViewHolder clickedHolder;
    private String sessionType;

    private SettingsUtil settingsUtil;
    private double userdeliverydistance = 0;
    private int deliverytype;

    ViewHolder holder;

    public TMCSlotAdapter(Context context, ArrayList<Deliveryslots> slotdetailsList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        settingsUtil = new SettingsUtil(context);
        // this.slotDetailsMap = slotdetailsmap;
        this.deliveryslotdetailsList = new ArrayList<Deliveryslots>();
        this.deliveryslotdetailsList.addAll(slotdetailsList);
        Collections.sort(deliveryslotdetailsList);
    }

    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public String getSessionType() { return sessionType; }

    public void setSelectedDeliverySlotDetails(Deliveryslots selecteddeliveryslotdetails) {
        this.selecteddeliveryslotdetails = selecteddeliveryslotdetails;
    }

    public void setUserdeliverydistance(double userdeliverydistance) {
        this.userdeliverydistance = userdeliverydistance;
    }

    public void setDeliverytype(int deliverytype) {
        this.deliverytype = deliverytype;
    }

    @Override
    public int getCount() {
        return deliveryslotdetailsList.size();
    }

    @Override
    public String getItem(int position) {
        // return timeSlotList.get(position);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
     /* if (itemList.size() > 0) {
            return getDateTimeInLong(itemList.get(i).getCreateddate());
        } else {
            return i;
        } */
    }

    private long getDateTimeInLong(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            return sdf.parse(date).getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public void setHandler(Handler handler) { this.handler = handler; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder holder;

        if (convertView != null) {
            ViewHolder holder1 = (ViewHolder) convertView.getTag();
            if (position != holder1.position) {
                convertView = null;
            }
        }

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.tmcslot_list_item, parent,
                    false);
            if (convertView != null) {
                holder.position = position;
                holder.container = convertView.findViewById(R.id.list_item_container);
                holder.sessiontime_textview = (TMCTextView) convertView.findViewById(R.id.sessiontime_textview);
                holder.deliverycharge_textview = (TMCTextView) convertView.findViewById(R.id.deliverycharge_textview);
                holder.timeslot_btn = convertView.findViewById(R.id.timeslot_btn);
                holder.deliverynotavl_textview = convertView.findViewById(R.id.deliverynotavl_textview);
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final View holderView = convertView;

        Deliveryslots deliveryslotdetails = deliveryslotdetailsList.get(position);

        if (deliverytype == OrderSummaryActivity.DELIVERYTYPE_STOREPICKUP) {
            if (deliveryslotdetails.getSlotname().equalsIgnoreCase("Express Delivery")) {
                deliveryslotdetails.setIsdeliverable(false);
            }
        }

        if (deliveryslotdetails.getIsdefault()) {
            holder.sessiontime_textview.setText(deliveryslotdetails.getSlotname() + " - " + deliveryslotdetails.getDeliverytime());
        } else {
            String timeslotdetails = deliveryslotdetails.getSlotstarttime() + " - " + deliveryslotdetails.getSlotendtime();
            holder.sessiontime_textview.setText(timeslotdetails);
        }

        double defaultdeldistance = deliveryslotdetails.getDefaultdeliverydistance();
        double newdeliverycharges = 0;
        double defaultdeliverycharge = deliveryslotdetails.getDefaultdeliverycharge();
     // Log.d("TMCSlotAdapter", "defdeldistance "+defaultdeldistance + " slot key "+deliveryslotdetails.getKey()
     //                   +" deliveryslotjson "+deliveryslotdetails.getJsonString());
        if (defaultdeldistance >= userdeliverydistance) {
            newdeliverycharges = defaultdeliverycharge;
        } else {
            double deliverychargeperkm = deliveryslotdetails.getDeliverychargeperkm();
            double diffindistance = userdeliverydistance - defaultdeldistance;
            newdeliverycharges = defaultdeliverycharge + (diffindistance * deliverychargeperkm);
        }
     // Log.d("TMCSlotAdapter", "newdeliverycharges "+newdeliverycharges);
        String rs = context.getResources().getString(R.string.Rs);
        holder.deliverycharge_textview.setText(rs + String.format("%.2f", newdeliverycharges));

        boolean isselected = false;

        String selecteddeliveryslot = TMCMenuItemCatalog.getInstance().getSelectedDeliverySlot();
        if ((selecteddeliveryslot != null && !(TextUtils.isEmpty(selecteddeliveryslot)))) {
            Deliveryslots selecteddeliveryslotdetails = null;
            try {
                selecteddeliveryslotdetails = new Deliveryslots(new JSONObject(selecteddeliveryslot));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (selecteddeliveryslotdetails.getKey().equalsIgnoreCase(deliveryslotdetails.getKey())) {
                isselected = true;
            }
        }

        if (isselected) {
            holder.timeslot_btn.setBackground(context.getResources().getDrawable(R.drawable.timeslotselected_bg));
            holder.sessiontime_textview.setTextColor(context.getResources().getColor(R.color.meatchopbg_red));
            holder.deliverycharge_textview.setTextColor(context.getResources().getColor(R.color.meatchopbg_red));
            holder.deliverynotavl_textview.setVisibility(View.GONE);
            clickedHolder = holder;
        } else {
            if (deliveryslotdetails.getIsdeliverable() && (deliveryslotdetails.getStatus().equalsIgnoreCase("Active"))) {
                holder.timeslot_btn.setBackground(context.getResources().getDrawable(R.drawable.timeslot_bg));
                holder.sessiontime_textview.setTextColor(context.getResources().getColor(R.color.black));
                holder.deliverycharge_textview.setTextColor(context.getResources().getColor(R.color.secondary_text_color));
                holder.deliverynotavl_textview.setVisibility(View.GONE);
            } else {
                String text = "Not Available";
                holder.deliverynotavl_textview.setText(text);
                holder.deliverynotavl_textview.setVisibility(View.VISIBLE);
                holder.timeslot_btn.setBackground(context.getResources().getDrawable(R.drawable.timeslotdisabled_bg));
                holder.sessiontime_textview.setTextColor(context.getResources().getColor(R.color.secondary_text_color));
                holder.deliverycharge_textview.setTextColor(context.getResources().getColor(R.color.secondary_text_color));
            }
        }

     // holder.deliverycharge_textview.setText(rs + deliveryslotdetails.getDeliverycharge());
        holder.timeslot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder = (ViewHolder) holderView.getTag();
                Deliveryslots deliveryslotdetails = deliveryslotdetailsList.get(holder.position);
                if (deliveryslotdetails.getStatus().equalsIgnoreCase("Inactive")) {
                    return;
                }
                if (!deliveryslotdetails.getIsdeliverable()) {
                    return;
                }
                double defaultdeldistance = deliveryslotdetails.getDefaultdeliverydistance();
                double defaultdeliverycharge = deliveryslotdetails.getDefaultdeliverycharge();
                double newdeliverycharges = 0;
                if (defaultdeldistance >= userdeliverydistance) {
                    newdeliverycharges = defaultdeliverycharge;
                } else {
                    double deliverychargeperkm = deliveryslotdetails.getDeliverychargeperkm();
                    double diffindistance = userdeliverydistance - defaultdeldistance;
                    newdeliverycharges = defaultdeliverycharge + (diffindistance * deliverychargeperkm);
                }
                if (clickedHolder == null) {
                    holder.timeslot_btn.setBackground(context.getResources().getDrawable(R.drawable.timeslotselected_bg));
                    holder.sessiontime_textview.setTextColor(context.getResources().getColor(R.color.meatchopbg_red));
                    holder.deliverycharge_textview.setTextColor(context.getResources().getColor(R.color.meatchopbg_red));
                    clickedHolder = holder;
                    sendHandlerMessage(deliveryslotdetails, true, newdeliverycharges);
                } else {
                    if (holder.position == clickedHolder.position) {
                        holder.timeslot_btn.setBackground(context.getResources().getDrawable(R.drawable.timeslot_bg));
                        holder.sessiontime_textview.setTextColor(context.getResources().getColor(R.color.black));
                        holder.deliverycharge_textview.setTextColor(context.getResources().getColor(R.color.secondary_text_color));
                        clickedHolder = null;
                        sendHandlerMessage(null, false, newdeliverycharges);
                    } else {
                        holder.timeslot_btn.setBackground(context.getResources().getDrawable(R.drawable.timeslotselected_bg));
                        holder.sessiontime_textview.setTextColor(context.getResources().getColor(R.color.meatchopbg_red));
                        holder.deliverycharge_textview.setTextColor(context.getResources().getColor(R.color.meatchopbg_red));

                        clickedHolder.timeslot_btn.setBackground(context.getResources().getDrawable(R.drawable.timeslot_bg));
                        clickedHolder.sessiontime_textview.setTextColor(context.getResources().getColor(R.color.black));
                        clickedHolder.deliverycharge_textview.setTextColor(context.getResources().getColor(R.color.secondary_text_color));
                        clickedHolder = holder;
                        sendHandlerMessage(deliveryslotdetails, true, newdeliverycharges);
                    }
                }
            }
        });
        return convertView;
    }

    private void sendHandlerMessage(Deliveryslots deliveryslotdetails, boolean isselected, double newdeliverycharge) {
        Message msg =  new Message();
        Bundle bundle = new Bundle();
        if (isselected) {
         // settingsUtil.setSelectedSlotSessionType(sessionType);
         // settingsUtil.setSelectedDeliverySlot(deliveryslotdetails.getJsonString());
            TMCMenuItemCatalog.getInstance().setSelectedSlotSessionType(sessionType);
            TMCMenuItemCatalog.getInstance().setSelectedDeliverySlot(deliveryslotdetails.getJsonString());
            TMCMenuItemCatalog.getInstance().setSelecteddeliveryslotkey(deliveryslotdetails.getKey());

            bundle.putString("menutype", "deliveryslotselected");
            bundle.putParcelable("deliveryslotdetails", deliveryslotdetails);
            bundle.putString("selectedsessiontype", sessionType);
            bundle.putDouble("newdeliverycharges", newdeliverycharge);
        } else {
            bundle.putString("menutype", "deliveryslotunselected");
            bundle.putString("sessiontype", sessionType);
        }
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    class ViewHolder {
        int position;
        View container;
        View timeslot_btn;
        TMCTextView sessiontime_textview;
        TMCTextView deliverycharge_textview;
        TMCTextView deliverynotavl_textview;
    }

    public void unselectSlots() {
        if (clickedHolder != null) {
            clickedHolder.timeslot_btn.setBackground(context.getResources().getDrawable(R.drawable.timeslot_bg));
            clickedHolder.sessiontime_textview.setTextColor(context.getResources().getColor(R.color.primary_text_color));
            clickedHolder.deliverycharge_textview.setTextColor(context.getResources().getColor(R.color.primary_text_color));
            clickedHolder = null;
        }
    }


}
