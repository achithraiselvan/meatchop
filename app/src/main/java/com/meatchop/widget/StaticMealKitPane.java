package com.meatchop.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.meatchop.R;
import com.meatchop.data.TMCMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaticMealKitPane extends LinearLayout {

    private Context context;
    private LinearLayout fieldArea;
    private View lastView;
    private Handler handler;
    private HashMap<String, LinearLayout> menuItemLayout;

    public StaticMealKitPane(Context context) {
        super(context);
        this.context = context;
        initLayout();
    }

    public void removeAllViews() {
        lastView = null;
        fieldArea.removeAllViews();
    }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    private void initLayout() {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.static_mealkit_pane, this, true);
        fieldArea = (LinearLayout) findViewById(R.id.add_fields_area);
    }

    public void updateCartLayout(TMCMenuItem tmcMenuItem) {
        LinearLayout v = menuItemLayout.get(tmcMenuItem.getKey());
        View additem_layout = v.findViewById(R.id.additem_layout);
        View addcartitems_layout = v.findViewById(R.id.addcartitems_layout);
        View cartminus_btn = v.findViewById(R.id.cartminus_btn);
        TMCTextView cartcount_textview = (TMCTextView) v.findViewById(R.id.cartcount_textview);
        View cartplus_btn = v.findViewById(R.id.cartplus_btn);
        int selectedqty = tmcMenuItem.getSelectedqty();
        if (tmcMenuItem != null) {
            cartcount_textview.setText("" + selectedqty);
        } else {
            cartcount_textview.setText("0");
        }

        if (selectedqty == 0) {
            additem_layout.setVisibility(View.VISIBLE);
            addcartitems_layout.setVisibility(View.GONE);
        } else {
            additem_layout.setVisibility(View.GONE);
            addcartitems_layout.setVisibility(View.VISIBLE);
        }
    }

    private LinearLayout getFormItem(TMCMenuItem tmcMenuItem) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.mealkititem_details, this, false);
        if (v != null) {
            // final RoundishImageView vendor_image = (RoundishImageView) v.findViewById(R.id.menuitem_image);
            final ImageView vendor_image = (ImageView) v.findViewById(R.id.menuitem_image);
            TMCTextView itemname_textview = (TMCTextView) v.findViewById(R.id.itemname_textview);
            TMCTextView itemcalories_textview = (TMCTextView) v.findViewById(R.id.itemcalories_textview);
            TMCTextView itemprice_textview = (TMCTextView) v.findViewById(R.id.itemprice_textview);
            View additem_layout = v.findViewById(R.id.additem_layout);
            View addcartitems_layout = v.findViewById(R.id.addcartitems_layout);
            View cartminus_btn = v.findViewById(R.id.cartminus_btn);
            TMCTextView cartcount_textview = (TMCTextView) v.findViewById(R.id.cartcount_textview);
            View cartplus_btn = v.findViewById(R.id.cartplus_btn);

            v.setTag(tmcMenuItem.getKey());

            itemname_textview.setText(tmcMenuItem.getItemname());
            itemcalories_textview.setText(tmcMenuItem.getItemcalories());
            itemprice_textview.setText("Rs." + String.format("%.2f", tmcMenuItem.getTmcprice()));

            String imageUrl = tmcMenuItem.getImageurl();
            Glide.with(context)
                    .load(imageUrl)
                    .fitCenter()
                    .into(vendor_image);

         // TMCMenuItem menuItemFromCatalog = TMCMenuItemCatalog.getInstance().getTMCMenuItem(tmcMenuItem.getKey());
            int selectedqty = tmcMenuItem.getSelectedqty();
            if (tmcMenuItem != null) {
                cartcount_textview.setText("" + selectedqty);
            } else {
                cartcount_textview.setText("0");
            }

            if (selectedqty == 0) {
                additem_layout.setVisibility(View.VISIBLE);
                addcartitems_layout.setVisibility(View.GONE);
            } else {
                additem_layout.setVisibility(View.GONE);
                addcartitems_layout.setVisibility(View.VISIBLE);
            }

            vendor_image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                 /* if (isVendorClicked) {
                        isVendorClicked = false;
                        return;
                    } */
                    // isVendorClicked = true;
                    Log.d("StaticMenuItemPane", "getFormItem method v.getTag() "+view.getTag());
                    String menuitemkey = (String) view.getTag();
                    sendHandlerMessage("menuitemclicked", tmcMenuItem.getKey());
                }
            });

            additem_layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String menuitemkey = (String) view.getTag();
                    sendHandlerMessage("additemclicked", tmcMenuItem.getKey());
                }
            });

            cartplus_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String menuitemkey = (String) view.getTag();
                    String oldcountstr = cartcount_textview.getText().toString();
                    sendHandlerMessage("additemclicked", tmcMenuItem.getKey());
                }
            });

            cartminus_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String oldcountstr = cartcount_textview.getText().toString();
                    int newcount = decrementValue(oldcountstr);
                    cartcount_textview.setText("" + newcount);
                    tmcMenuItem.setSelectedqty(newcount);
                    if (newcount == 0) {
                        additem_layout.setVisibility(View.VISIBLE);
                        addcartitems_layout.setVisibility(View.GONE);
                    }
                    TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
                    sendHandlerMessage("cartupdated", "");
                }
            });

        }
        if (menuItemLayout == null) {
            menuItemLayout = new HashMap<String, LinearLayout>();
        }
        menuItemLayout.put(tmcMenuItem.getKey(), v);
        return v;
    }

    public void addFormItem(TMCMenuItem tmcMenuItem) {
        lastView = getFormItem(tmcMenuItem);
        fieldArea.addView(lastView);
    }

    private void sendHandlerMessage(String bundlestr, String menuitemkey) {
        Message msg =  new Message();
        Bundle bundle = new Bundle();
        bundle.putString("menutype", bundlestr);
        bundle.putString("menuitemkey", menuitemkey);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private int decrementValue(String text) {
        try {
            int countvalue = Integer.parseInt(text);
            if (countvalue > 0) {
                countvalue = countvalue - 1;
            }
            return countvalue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

}
