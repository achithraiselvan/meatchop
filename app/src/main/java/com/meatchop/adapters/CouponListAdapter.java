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
import android.widget.TextView;

import com.meatchop.R;
import com.meatchop.data.Coupondetails;
import com.meatchop.widget.TMCTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CouponListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    public List<Coupondetails> itemList = null;
    private Context context;
    private Handler handler;
    private boolean disableapplyview;

    ViewHolder holder;


    public CouponListAdapter(Context context, List<Coupondetails> listItems, boolean disableapplyview) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.itemList = new ArrayList<Coupondetails>();
        itemList.addAll(listItems);
        this.disableapplyview = disableapplyview;
        // titleFace = Typeface.createFromAsset(context.getAssets(),
        //         "fonts/Roboto-Bold.ttf");
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Coupondetails getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int i) {
        if (itemList.size() > 0) {
            return getDateTimeInLong(itemList.get(i).getCreateddate());
        } else {
            return i;
        }
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
    //  public void setHelper(DatabaseHelper helper) { this.helper = helper; }

    public List<Coupondetails> getItemList() { return itemList; }

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
            convertView = inflater.inflate(R.layout.coupon_list_item, parent,
                    false);
            if (convertView != null) {
                holder.position = position;
                holder.container = convertView.findViewById(R.id.list_item_container);
                holder.coupon_divider = convertView.findViewById(R.id.coupon_divider);
                holder.couponheader_textview = (TextView) convertView.findViewById(R.id.couponheader_textview);
                holder.coupontitle_textview = (TextView) convertView.findViewById(R.id.coupontitle_textview);
                holder.couponsubtitle_view =  convertView.findViewById(R.id.couponsubtitle_view);
                holder.couponsubtitle_textview = (TextView) convertView.findViewById(R.id.couponsubtitle_textview);
                holder.applycoupon_button = convertView.findViewById(R.id.applycoupon_button);
                holder.couponapplied_button = convertView.findViewById(R.id.couponapplied_button);
                holder.couponapplied_textview = convertView.findViewById(R.id.couponapplied_textview);
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final View holderView = convertView;

        Coupondetails coupondetails = itemList.get(position);
        String couponcode = coupondetails.getCode();
        String coupontitle = coupondetails.getTitle();
        String couponsubtitle = coupondetails.getSubtitle();

        holder.couponheader_textview.setText(couponcode);
        holder.coupontitle_textview.setText(coupontitle);
        if ((couponsubtitle == null) || (TextUtils.isEmpty(couponsubtitle))) {
            holder.couponsubtitle_view.setVisibility(View.GONE);
        } else {
            holder.couponsubtitle_view.setVisibility(View.VISIBLE);
            holder.couponsubtitle_textview.setText(couponsubtitle);
        }

     /* if (position == 0) {
            holder.coupon_divider.setVisibility(View.GONE);
        } else {
            holder.coupon_divider.setVisibility(View.VISIBLE);
        } */

        holder.applycoupon_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder = (ViewHolder) holderView.getTag();
                Log.d("CouponListAdapter", "applycoupon clicked");
                Coupondetails coupondetails = itemList.get(holder.position);
                Log.d("CouponListAdapter", "coupondetails "+coupondetails);
                if (coupondetails != null) {
                    Log.d("CouponListAdapter", "coupondetails "+coupondetails.getSubtitle());
                }
                sendHandlerMessage("applybuttonclicked", coupondetails.getKey(), coupondetails.getUsertype());
            }
        });

        if (disableapplyview) {
            holder.applycoupon_button.setVisibility(View.GONE);
            holder.couponapplied_button.setVisibility(View.GONE);
        }

        // checkCouponAvailedCount(coupondetails.getId(), coupondetails.getRedeemcount(), holder);

        return convertView;
    }

    private void sendHandlerMessage(String bundlestr, String coupondetailsid, String usertype) {
        Message msg =  new Message();
        Bundle bundle = new Bundle();
        bundle.putString("menutype", bundlestr);
        bundle.putString("coupondetailsid", coupondetailsid);
        bundle.putString("couponusertype", usertype);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    boolean isGetAppliedCouponCount = false;
 /* private void checkCouponAvailedCount(String couponid, final int redeemcount, final ViewHolder viewholder) {
        SettingsUtil settingsUtil = new SettingsUtil(context);
        String mobileno = settingsUtil.getMobile();
        String mobilenocouponid = mobileno + "-" + couponid;

        Log.d("CouponListAdapter", "checkCouponAvailedCount method");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discounttransactionsRef = rootRef.child("discounttransactions");
        Query discountQuery = discounttransactionsRef.orderByChild("mobilenocouponid").equalTo(mobilenocouponid);
        isGetAppliedCouponCount = true;
        discountQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("CouponListAdapter", "mobilenocouponid not exists ");
                }
                Log.d("CouponListAdapter", "checkCouponAvailedCount method isGetAppliedCouponCount");
                if (!isGetAppliedCouponCount) { return; }
                isGetAppliedCouponCount = false;
                int appliedcount = 0;
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Discounttransactions discounttransactions = singleSnapshot.getValue(Discounttransactions.class);
                    appliedcount = appliedcount + 1;
                }
                Log.d("CouponListAdapter", "appliedcount "+appliedcount+" redeemcount "+redeemcount);
                if (redeemcount > 0) {
                    if (appliedcount < redeemcount) {
                        viewholder.applycoupon_button.setVisibility(View.VISIBLE);
                        viewholder.couponapplied_button.setVisibility(View.GONE);
                    } else {
                        viewholder.applycoupon_button.setVisibility(View.GONE);
                        viewholder.couponapplied_button.setVisibility(View.VISIBLE);
                        viewholder.couponapplied_textview.setText("Availed");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("OrderSummaryActivity", "onCancelled", databaseError.toException());
            }
        });
    }
*/
    class ViewHolder {
        int position;
        View container;
        View coupon_divider;
        TextView couponheader_textview;
        TextView coupontitle_textview;
        View couponsubtitle_view;
        TextView couponsubtitle_textview;
        View applycoupon_button;
        View couponapplied_button;
        TMCTextView couponapplied_textview;
    }



}
