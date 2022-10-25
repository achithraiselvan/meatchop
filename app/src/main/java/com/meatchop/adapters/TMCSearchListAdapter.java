package com.meatchop.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.meatchop.R;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TMCSearchListAdapter extends RecyclerView.Adapter<TMCSearchListAdapter.ViewHolder> {

    public List<TMCMenuItem> tmcMenuItemList = null;
    private Context context;
    private Handler handler;
    private ArrayList<ViewHolder> viewHolderList;
    private List<TMCMenuItem> filteredMenuItemList = null;

    private boolean isLocationUpdated = false;
    private boolean isUserLoggedIn = false;

    private SettingsUtil settingsUtil;

    private TMCTextView lastSelectedCartCountTextView;
    private ViewHolder lastSelectedHolder;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView checkoutimage_view;
        TMCTextView itemname_text;
        TMCTextView itemqty_text;
        TMCTextView itemprice_text;
        View fieldseperator_layout;
        View additem_layout;
        View addcartitems_layout;
        TMCTextView cartcount_textview;
        View cartminus_btn;
        View cartplus_btn;
        View tmctile_progressbar;
        View customizabletext_view;
        View itemnotavailable_text;

        ViewHolder(View view) {
            super(view);
            checkoutimage_view = (ImageView) view.findViewById(R.id.checkoutimage_view);
            itemname_text = (TMCTextView) view.findViewById(R.id.itemname_text);
            itemqty_text = (TMCTextView) view.findViewById(R.id.itemqty_text);
            itemprice_text = (TMCTextView) view.findViewById(R.id.itemprice_text);
            fieldseperator_layout = view.findViewById(R.id.fieldseperator_layout);
            additem_layout = view.findViewById(R.id.additem_layout);
            addcartitems_layout = view.findViewById(R.id.addcartitems_layout);
            cartcount_textview = view.findViewById(R.id.cartcount_textview);
            cartplus_btn = view.findViewById(R.id.cartplus_btn);
            cartminus_btn = view.findViewById(R.id.cartminus_btn);
            tmctile_progressbar = view.findViewById(R.id.tmctile_progressbar);
            customizabletext_view = view.findViewById(R.id.customizabletext_view);
            itemnotavailable_text = view.findViewById(R.id.itemnotavailable_text);
        }
    }

    public TMCSearchListAdapter(Context context, List<TMCMenuItem> menuItemList) {
        this.context = context;
        this.tmcMenuItemList = new ArrayList<TMCMenuItem>();
        this.tmcMenuItemList.addAll(menuItemList);
        this.filteredMenuItemList = new ArrayList<TMCMenuItem>();
        settingsUtil = new SettingsUtil(context);
        isUserLoggedIn = AWSMobileClient.getInstance().isSignedIn();
        if (isUserLoggedIn) {
            if ((settingsUtil.getMobile() == null) || (TextUtils.isEmpty(settingsUtil.getMobile()))) {
                AWSMobileClient.getInstance().signOut();
                isUserLoggedIn = false;
            }
            if ((settingsUtil.getUserkey() == null) || (TextUtils.isEmpty(settingsUtil.getUserkey()))) {
                AWSMobileClient.getInstance().signOut();
                isUserLoggedIn = false;
            }
        }

        String defaultaddress = settingsUtil.getDefaultAddress();
        isLocationUpdated = ((defaultaddress == null) || (TextUtils.isEmpty(defaultaddress))) ? false : true;
    }

    public void setHandler(Handler handler) { this.handler = handler; }

    public void updateLastSelectedCartCountTextView(int count) {
        if (lastSelectedHolder != null) {
            lastSelectedHolder.additem_layout.setVisibility(View.GONE);
            lastSelectedHolder.addcartitems_layout.setVisibility(View.VISIBLE);
            lastSelectedHolder.cartcount_textview.setText("" + count);
        }
    }


    @NonNull
    @Override
    public TMCSearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tmcsearchlist_layout, parent, false);
        return new TMCSearchListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TMCMenuItem tmcMenuItem =  filteredMenuItemList.get(position);
        holder.itemname_text.setText(tmcMenuItem.getItemname());
        if (viewHolderList == null) {
            viewHolderList = new ArrayList<ViewHolder>();
        }
        viewHolderList.add(position, holder);
     /* if (!tmcMenuItem.isShowInApp()) {
            return;
        } */

        String imageUrl = tmcMenuItem.getImageurl();

        Glide.with(context)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.tmctile_progressbar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.tmctile_progressbar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .fitCenter()
                .error(R.mipmap.tmclogo_2)
                .into(holder.checkoutimage_view);


        String rs = context.getResources().getString((R.string.Rs));
        holder.itemqty_text.setText(tmcMenuItem.getNetweight());
        holder.itemprice_text.setText(rs + String.format("%.2f", tmcMenuItem.getTmcprice()));

        TMCMenuItem menuItemFromCatalog = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(tmcMenuItem.getKey());
        int selectedqty = (menuItemFromCatalog == null) ? 0 : menuItemFromCatalog.getSelectedqty();
        if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
            holder.customizabletext_view.setVisibility(View.VISIBLE);
            selectedqty = TMCMenuItemCatalog.getInstance().getMarinadeSelectedQty(tmcMenuItem.getAwsKey());
        } else {
            holder.customizabletext_view.setVisibility(View.GONE);
        }

        if (selectedqty > 0) {
            holder.additem_layout.setVisibility(View.GONE);
            holder.addcartitems_layout.setVisibility(View.VISIBLE);
            holder.cartcount_textview.setText("" + selectedqty);
        } else {
            holder.additem_layout.setVisibility(View.VISIBLE);
            holder.addcartitems_layout.setVisibility(View.GONE);
            holder.cartcount_textview.setText("0");
        }

        boolean isitemavailable = tmcMenuItem.isItemavailability(settingsUtil.isInventoryCheck());
        if (!isitemavailable) {
            holder.addcartitems_layout.setVisibility(View.GONE);
            holder.additem_layout.setVisibility(View.GONE);
            holder.customizabletext_view.setVisibility(View.GONE);
            holder.itemnotavailable_text.setVisibility(View.VISIBLE);
        } else {
            holder.itemnotavailable_text.setVisibility(View.GONE);
        }

        holder.additem_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder1 = viewHolderList.get(position);
                if (!isUserLoggedIn) {
                    sendHandlerMessage("openloginscreen", null);
                    return;
                }
                if (!isLocationUpdated) {
                    sendHandlerMessage("openlocationscreen", null);
                    return;
                }

                TMCMenuItem tmcMenuItem =  filteredMenuItemList.get(position);
                if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                    lastSelectedCartCountTextView = holder.cartcount_textview;
                    lastSelectedHolder = holder;
                    sendHandlerMessage("openmarinadescreen", tmcMenuItem);
                    return;
                } else if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MEALKIT)) {
                    lastSelectedHolder = holder;
                    sendHandlerMessage("openmealkitscreen", tmcMenuItem);
                    return;
                } else if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_REGULAR)) {
                    if (tmcMenuItem.isWeightAndCutsCustomizable()) {
                        lastSelectedHolder = holder;
                        lastSelectedCartCountTextView = holder.cartcount_textview;
                        sendHandlerMessageForCutsAndWeight("openmenuitemcustomizationactivity",
                                tmcMenuItem.getKey(), tmcMenuItem.getItemcutdetails(), tmcMenuItem.getItemweightdetails());
                    } else {
                        holder.additem_layout.setVisibility(View.GONE);
                        holder.addcartitems_layout.setVisibility(View.VISIBLE);
                        holder.cartcount_textview.setText("1");
                        tmcMenuItem.setSelectedqty(1);
                        TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
                        sendHandlerMessage("changecartcount", null);
                    }

                }


            }
        });

        holder.cartplus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogInAndLocationUpdateStatus();
                if (!isUserLoggedIn) {
                    sendHandlerMessage("openloginscreen", null);
                    return;
                }
                if (!isLocationUpdated) {
                    sendHandlerMessage("openlocationscreen", null);
                    return;
                }
                ViewHolder holder1 = viewHolderList.get(position);
                TMCMenuItem tmcMenuItem =  filteredMenuItemList.get(position);
                if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                    lastSelectedCartCountTextView = holder.cartcount_textview;
                    lastSelectedHolder = holder;
                    sendHandlerMessage("openmarinadescreen", tmcMenuItem);
                    return;
                } else if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MEALKIT)) {
                    lastSelectedHolder = holder;
                    sendHandlerMessage("openmealkitscreen", tmcMenuItem);
                    return;
                } else if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_REGULAR)) {
                    if (tmcMenuItem.isWeightAndCutsCustomizable()) {
                        lastSelectedCartCountTextView = holder.cartcount_textview;
                        lastSelectedHolder = holder;
                        sendHandlerMessageForCutsAndWeight("openmenuitemcustomizationactivity",
                                tmcMenuItem.getKey(), tmcMenuItem.getItemcutdetails(), tmcMenuItem.getItemweightdetails());
                    } else {
                        String oldcountstr = holder1.cartcount_textview.getText().toString();
                        int newcount = incrementValue(oldcountstr);
                        holder.cartcount_textview.setText("" + newcount);
                        tmcMenuItem.setSelectedqty(newcount);
                        TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
                        sendHandlerMessage("changecartcount", null);
                    }
                }

            }
        });

        holder.cartminus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder1 = viewHolderList.get(position);
                TMCMenuItem tmcMenuItem =  filteredMenuItemList.get(position);
                String oldcountstr = holder1.cartcount_textview.getText().toString();
                int oldcountvalue = Integer.parseInt(oldcountstr);
                if (oldcountvalue <= 0) {
                    return;
                }
                if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                    if (oldcountvalue > 1) {
                        sendHandlerMessage("multiplecustomizationsalert", tmcMenuItem);
                        return;
                    }
                }
                if (tmcMenuItem.isWeightAndCutsCustomizable()) {
                    sendHandlerMessage("multiplecustomizationsalert", tmcMenuItem);
                    return;
                }
                int newcount = decrementValue(oldcountstr);
                if (newcount <= 0) {
                    holder.additem_layout.setVisibility(View.VISIBLE);
                    holder.addcartitems_layout.setVisibility(View.GONE);
                }
                holder.cartcount_textview.setText("" + newcount);
                tmcMenuItem.setSelectedqty(newcount);
                TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
                sendHandlerMessage("changecartcount", null);
            }
        });

     // holder.itemprice_text.setText(rs + tmcMenuItem.getBaseamount());
    }

    private void checkLogInAndLocationUpdateStatus() {
        if (settingsUtil == null) {
            settingsUtil = new SettingsUtil(context);
        }
        isUserLoggedIn = AWSMobileClient.getInstance().isSignedIn();
        String defaultaddress = settingsUtil.getDefaultAddress();
        isLocationUpdated = ((defaultaddress == null) || (TextUtils.isEmpty(defaultaddress))) ? false : true;
    }

    @Override
    public int getItemCount() {
        return filteredMenuItemList.size();
    }

    public int filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (filteredMenuItemList != null) {
            filteredMenuItemList.clear();
        }
        if (charText.length() == 0) {

        } else {
            for (TMCMenuItem menuItem : tmcMenuItemList) {
                if (menuItem.getItemname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    filteredMenuItemList.add(menuItem);

                }
            }
        }
        notifyDataSetChanged();
        return filteredMenuItemList.size();
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

    private void sendHandlerMessage(String bundlestr, TMCMenuItem tmcMenuItem) {
        Message msg =  new Message();
        Bundle bundle = new Bundle();
        bundle.putString("menutype", bundlestr);
        if (tmcMenuItem != null) {
            bundle.putString("menuitemkey", tmcMenuItem.getKey());
            if (tmcMenuItem.getMarinadelinkedcodes() != null) {
                bundle.putString("marinadelinkedcodes", tmcMenuItem.getMarinadelinkedcodes());
            }
        }
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



}
