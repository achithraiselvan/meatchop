package com.meatchop.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.meatchop.R;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.utils.SettingsUtil;

import org.jetbrains.annotations.Nullable;


public class StaticMenuItemPane extends LinearLayout {

    private Context context;
    private LinearLayout fieldArea;
    private View lastView;
    private Handler handler;
    private boolean isAllowUserToAddCart = false;
    private SettingsUtil settingsUtil;
    private boolean isUserLoggedIn;
    private boolean isLocationUpdated;

    private TMCTextView lastSelectedCartCountTextView;


    public StaticMenuItemPane(Context context) {
        super(context);
        this.context = context;
        settingsUtil = new SettingsUtil(context);
        try {
            if ((settingsUtil.getMobile() == null) || (TextUtils.isEmpty(settingsUtil.getMobile()))) {
                isUserLoggedIn = false;
            } else {
                isUserLoggedIn = AWSMobileClient.getInstance().isSignedIn();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String defaultaddress = settingsUtil.getDefaultAddress();
        isLocationUpdated = ((defaultaddress != null) && !(TextUtils.isEmpty(defaultaddress))) ? true : false;
        initLayout();
    }

    public void removeAllViews() {
        lastView = null;
        fieldArea.removeAllViews();
    }

    public void setisAllowUserToAddCart(boolean isallowusertoaddcart) { this.isAllowUserToAddCart = isallowusertoaddcart; }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    public void updateLastSelectedCartCountTextView(int count) {
        if (lastSelectedCartCountTextView != null) {
            lastSelectedCartCountTextView.setText("" + count);
        }
    }

    private void initLayout() {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.static_menuitem_pane, this, true);
        fieldArea = (LinearLayout) findViewById(R.id.add_fields_area);
    }

    private LinearLayout getFormItem(TMCMenuItem tmcMenuItem) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.tmcmenuitem_details, this, false);
        if (v != null) {
         // final RoundishImageView vendor_image = (RoundishImageView) v.findViewById(R.id.menuitem_image);
            CardView menuitem_cardview = (CardView) v.findViewById(R.id.menuitem_cardview);
            final ImageView vendor_image = (ImageView) v.findViewById(R.id.menuitem_image);
            TMCTextView itemname_textview = (TMCTextView) v.findViewById(R.id.itemname_textview);
            TMCTextView netweight_textview = (TMCTextView) v.findViewById(R.id.netweight_textview);
            TMCTextView grossweight_textview = (TMCTextView) v.findViewById(R.id.grossweight_textview);
            TMCTextView portionsize_textview = (TMCTextView) v.findViewById(R.id.portionsize_textview);
            TMCTextView tmcprice_textview = (TMCTextView) v.findViewById(R.id.tmcprice_textview);
            TMCTextView strikeoutprice_textview = (TMCTextView) v.findViewById(R.id.strikeoutprice_textview);
            TMCTextView itemdiscountdetails_textview = (TMCTextView) v.findViewById(R.id.itemdiscountdetails_textview);
            View cartplus_btn = v.findViewById(R.id.cartplus_btn);
            View cartminus_btn = v.findViewById(R.id.cartminus_btn);
            TMCTextView cartcount_textview = (TMCTextView) v.findViewById(R.id.cartcount_textview);
            View addcartitems_layout = v.findViewById(R.id.addcartitems_layout);
            View tmcmenuitem_progressbar = v.findViewById(R.id.tmcmenuitem_progressbar);
            View customizabletext_view = v.findViewById(R.id.customizabletext_view);
            View itemnotavailable_text = v.findViewById(R.id.itemnotavailable_text);

            v.setTag(tmcMenuItem.getKey());

            itemname_textview.setText(tmcMenuItem.getItemname());
            boolean itemavailability = tmcMenuItem.isItemavailability(settingsUtil.isInventoryCheck());
            String netweight = tmcMenuItem.getNetweight();
            String grossweight = tmcMenuItem.getGrossweight();
            String portionsize = tmcMenuItem.getPortionsize();
            if ((netweight != null) && !(TextUtils.isEmpty(netweight))) {
                netweight_textview.setText("Net: " + netweight);
                netweight_textview.setVisibility(View.VISIBLE);
            } else {
                netweight_textview.setVisibility(View.GONE);
            }

            if ((grossweight != null) && !(TextUtils.isEmpty(grossweight))) {
                grossweight_textview.setText("Gross: " + grossweight);
                grossweight_textview.setVisibility(View.VISIBLE);
            } else {
                grossweight_textview.setVisibility(View.GONE);
            }
            if ((portionsize != null) && !(TextUtils.isEmpty(portionsize))) {
                portionsize_textview.setText(portionsize);
                portionsize_textview.setVisibility(View.VISIBLE);
            } else {
                portionsize_textview.setVisibility(View.GONE);
            }

            String rs = context.getResources().getString(R.string.Rs);
            double applieddiscountpercentage = tmcMenuItem.getApplieddiscpercentage();
            double tmcprice = tmcMenuItem.getTmcprice();
            tmcprice_textview.setText(rs + String.format("%.2f", tmcprice));
            if (applieddiscountpercentage > 0) {
                int discpercentage = (int) applieddiscountpercentage;
                double oldamount = tmcprice / ((100 - applieddiscountpercentage) / 100);
                itemdiscountdetails_textview.setText(discpercentage + "%" + " OFF");
                strikeoutprice_textview.setText(rs + String.format("%.2f", oldamount));
                strikeoutprice_textview.setPaintFlags(strikeoutprice_textview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                if (itemavailability) {
                    strikeoutprice_textview.setVisibility(View.VISIBLE);
                    itemdiscountdetails_textview.setVisibility(View.VISIBLE);
                } else {
                    strikeoutprice_textview.setVisibility(View.GONE);
                    itemdiscountdetails_textview.setVisibility(View.GONE);
                }
            } else {
                itemdiscountdetails_textview.setText("");
                itemdiscountdetails_textview.setVisibility(View.GONE);
                strikeoutprice_textview.setVisibility(View.GONE);
            }

         /* if (isAllowUserToAddCart) {
                addcartitems_layout.setVisibility(View.VISIBLE);
            } else {
                addcartitems_layout.setVisibility(View.GONE);
            } */

            TMCMenuItem menuItemFromCatalog = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(tmcMenuItem.getKey());
            int selectedqty = (menuItemFromCatalog == null) ? 0 : menuItemFromCatalog.getSelectedqty();
            if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                selectedqty = TMCMenuItemCatalog.getInstance().getMarinadeSelectedQty(tmcMenuItem.getAwsKey());
            } else if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MEALKIT)) {
                grossweight_textview.setVisibility(View.GONE);
                netweight_textview.setVisibility(View.GONE);
            } else {
                if (tmcMenuItem.isWeightAndCutsCustomizable()) {
                    selectedqty = TMCMenuItemCatalog.getInstance().getCutsAndWeightSelectedQty(tmcMenuItem.getAwsKey());
                }
            }

            cartcount_textview.setText("" + selectedqty);

            String imageUrl = tmcMenuItem.getImageurl();

            if (!TextUtils.isEmpty(imageUrl)) {
                try {
                    Glide.with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    tmcmenuitem_progressbar.setVisibility(View.GONE);
                                    // menuitem_cardview.setVisibility(View.VISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    tmcmenuitem_progressbar.setVisibility(View.GONE);
                                    // menuitem_cardview.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .error(R.mipmap.tmclogo_2)
                            .into(vendor_image);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            String key = tmcMenuItem.getKey();
            String menutype = tmcMenuItem.getMenutype();
            if (menutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                customizabletext_view.setVisibility(View.VISIBLE);
            } else {
                if (tmcMenuItem.isWeightAndCutsCustomizable()) {
                    customizabletext_view.setVisibility(View.VISIBLE);
                } else {
                    customizabletext_view.setVisibility(View.GONE);
                }
            }

            if (!itemavailability) {
                addcartitems_layout.setVisibility(View.GONE);
                itemnotavailable_text.setVisibility(View.VISIBLE);
            } else {
                addcartitems_layout.setVisibility(View.VISIBLE);
                itemnotavailable_text.setVisibility(View.GONE);
            }

         /* vendor_image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                 // isVendorClicked = true;
                    Log.d("StaticMenuItemPane", "getFormItem method v.getTag() "+view.getTag());
                    String menuitemkey = (String) view.getTag();
                    sendHandlerMessage("menuitemclicked", menuitemkey);
                }
            }); */

            cartplus_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isUserLoggedIn) {
                        sendHandlerMessage("openloginscreen", "", "");
                        return;
                    }
                    if (!isLocationUpdated) {
                        sendHandlerMessage("openlocationscreen", "", "");
                        return;
                    }
                    String oldcountstr = cartcount_textview.getText().toString();
                    int newcount = incrementValue(oldcountstr);
                    String tmcmenutype = tmcMenuItem.getMenutype();
                    if (tmcmenutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_REGULAR)) {
                        if (tmcMenuItem.isWeightAndCutsCustomizable()) {
                            lastSelectedCartCountTextView = cartcount_textview;
                            sendHandlerMessageForCutsAndWeight("openmenuitemcustomizationactivity",
                                             tmcMenuItem.getKey(), tmcMenuItem.getItemcutdetails(), tmcMenuItem.getItemweightdetails());
                        } else {
                            cartcount_textview.setText("" + newcount);
                            tmcMenuItem.setSelectedqty(newcount);
                            TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
                            sendHandlerMessage("cartupdated", "", "");
                        }
                    } else if (tmcmenutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                        lastSelectedCartCountTextView = cartcount_textview;
                        String marinadelinkedcodes = tmcMenuItem.getMarinadelinkedcodes();
                        if ((marinadelinkedcodes == null) || (TextUtils.isEmpty(marinadelinkedcodes))) {
                            cartcount_textview.setText("" + newcount);
                            tmcMenuItem.setSelectedqty(newcount);
                            TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
                            sendHandlerMessage("cartupdated", "", "");
                        } else {
                            sendHandlerMessage("openmarinadescreen", tmcMenuItem.getKey(), marinadelinkedcodes);
                        }
                    } else if (tmcmenutype.equalsIgnoreCase(TMCMenuItem.MENUTYPE_MEALKIT)) {
                        lastSelectedCartCountTextView = cartcount_textview;
                        sendHandlerMessage("openmealkitscreen", tmcMenuItem.getKey(), "");
                    }
                }
            });

            cartminus_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String oldcountstr = cartcount_textview.getText().toString();
                    int oldcountvalue = Integer.parseInt(oldcountstr);
                    if (oldcountvalue <= 0) {
                        return;
                    }
                    if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                        sendHandlerMessage("multiplecustomizationsalert", tmcMenuItem.getKey(), "");
                        return;
                    }
                    if (tmcMenuItem.isWeightAndCutsCustomizable()) {
                        sendHandlerMessage("multiplecustomizationsalert", tmcMenuItem.getKey(), "");
                        return;
                    }
                    int newcount = decrementValue(oldcountstr);
                    cartcount_textview.setText("" + newcount);
                    tmcMenuItem.setSelectedqty(newcount);
                    String tmcmenutype = tmcMenuItem.getMenutype();
                    TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
                    sendHandlerMessage("cartupdated", "", tmcmenutype);
                }
            });
        }
        return v;
    }

    public void incrementMenuItem() {

    }

    public void addFormItem(TMCMenuItem tmcMenuItem) {
        lastView = getFormItem(tmcMenuItem);
        fieldArea.addView(lastView);
    }

    private void sendHandlerMessage(String bundlestr, String menuitemkey, String marinadelinkedcodes) {
        Message msg =  new Message();
        Bundle bundle = new Bundle();
        bundle.putString("menutype", bundlestr);
        bundle.putString("menuitemkey", menuitemkey);
        bundle.putString("marinadelinkedcodes", marinadelinkedcodes);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void sendHandlerMessageForCutsAndWeight(String bundlestr, String menuitemkey, String itemcutdetails,
                                                         String itemweightdetails) {
        Message msg =  new Message();
        Bundle bundle = new Bundle();
        bundle.putString("menutype", bundlestr);
        bundle.putString("menuitemkey", menuitemkey);
        if (itemcutdetails != null) {
            bundle.putString("itemcutdetails", itemcutdetails);
        }
        if (itemweightdetails != null) {
            bundle.putString("itemweightdetails", itemweightdetails);
        }
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private int incrementValue(String text) {
        try {
            int countvalue = Integer.parseInt(text);
            countvalue = countvalue + 1;
            return countvalue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
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
