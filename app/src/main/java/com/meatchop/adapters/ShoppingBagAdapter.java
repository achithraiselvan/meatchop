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
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

public class ShoppingBagAdapter extends RecyclerView.Adapter<ShoppingBagAdapter.ViewHolder> {

    public List<TMCMenuItem> tmcMenuItemList = null;
    private Context context;
    private Handler handler;
    private ArrayList<ViewHolder> viewHolderList;
    private double totalsavedamount = 0;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView checkoutimage_view;
        TMCTextView itemname_text;
        TMCTextView itemqty_text;
        TMCTextView itemprice_text;
        TMCTextView netweight_textview;
        TMCTextView pricebeforediscount_textview;
        TMCTextView portionsize_textview;
        View fieldseperator_layout;
        View qty_layout;
        View customize_layout;
        TMCTextView itemdiscountdetails_textview;
        TMCTextView itemcutname_text;
        View itemcutnamefiller_layout;
        View customizeoptionfiller_layout;
        TMCTextView grosswt_textview;
        View grosswtseperator_layout;
        View netwtseperator_layout;

        ViewHolder(View view) {
            super(view);
            checkoutimage_view = (ImageView) view.findViewById(R.id.checkoutimage_view);
            itemname_text = (TMCTextView) view.findViewById(R.id.itemname_text);
            qty_layout = view.findViewById(R.id.qty_layout);
            itemqty_text = (TMCTextView) view.findViewById(R.id.itemqty_text);
            itemprice_text = (TMCTextView) view.findViewById(R.id.itemprice_text);
            fieldseperator_layout = view.findViewById(R.id.fieldseperator_layout);
            pricebeforediscount_textview = view.findViewById(R.id.pricebeforediscount_textview);
            netweight_textview = (TMCTextView) view.findViewById(R.id.netweight_textview);
            portionsize_textview = (TMCTextView) view.findViewById(R.id.portionsize_textview);
            customize_layout = view.findViewById(R.id.customize_layout);
            customize_layout = view.findViewById(R.id.customize_layout);
            itemdiscountdetails_textview = (TMCTextView) view.findViewById(R.id.itemdiscountdetails_textview);
            itemcutname_text = (TMCTextView) view.findViewById(R.id.itemcutname_text);
            itemcutnamefiller_layout = view.findViewById(R.id.itemcutnamefiller_layout);
            grosswt_textview = (TMCTextView) view.findViewById(R.id.grosswt_textview);
            grosswtseperator_layout = view.findViewById(R.id.grosswtseperator_layout);
            netwtseperator_layout = view.findViewById(R.id.netwtseperator_layout);
        }
    }

    public ShoppingBagAdapter(Context context, List<TMCMenuItem> menuItemList) {
        this.context = context;
        this.tmcMenuItemList = new ArrayList<TMCMenuItem>();
        this.tmcMenuItemList.addAll(menuItemList);
    }

    public void setHandler(Handler handler) { this.handler = handler; }

    public double getTotalsavedamount() { return totalsavedamount; }

    @NonNull
    @Override
    public ShoppingBagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_shoppingbag_layout, parent, false);
        return new ShoppingBagAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TMCMenuItem tmcMenuItem =  tmcMenuItemList.get(position);
        int selectedqty = tmcMenuItem.getSelectedqty();

        if (selectedqty <= 0) { return; }
        holder.itemname_text.setText(tmcMenuItem.getItemname());
        if (viewHolderList == null) {
            viewHolderList = new ArrayList<ViewHolder>();
        }
        viewHolderList.add(position, holder);

        if (position == 0) {
            holder.fieldseperator_layout.setVisibility(View.GONE);
        }

        String imageUrl = tmcMenuItem.getImageurl();
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(context)
                    .load(imageUrl)
                    .fitCenter()
                    .error(R.mipmap.tmclogo_2)
                    .into(holder.checkoutimage_view);
        }

        String rs = context.getResources().getString((R.string.Rs));
        holder.itemqty_text.setText("Qty: " + selectedqty);
        double tmcprice = 0;
     // Log.d("Shoppingbagadapter", "isMarinadeMeatItemAvl "+isMarinadeMeatItemAvl+" meatmenuitem "+meatmenuitem);

        tmcprice = tmcMenuItem.getTmcprice();

     // holder.netwtportionsize_layout.setVisibility(View.VISIBLE);
        holder.customize_layout.setVisibility(View.GONE);
        if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
            if (tmcMenuItem.getMarinadeMeatTMCMenuItem() != null) {
             // holder.netwtportionsize_layout.setVisibility(View.GONE);
                holder.customize_layout.setVisibility(View.VISIBLE);
                String itemname = tmcMenuItem.getMarinadeMeatTMCMenuItem().getItemname() +
                                         " " + tmcMenuItem.getMarinadeMeatTMCMenuItem().getNetweight() +
                                        " With " + tmcMenuItem.getItemname() + " Marinade Box";
                holder.itemname_text.setText(itemname);
             // tmcprice = tmcprice + tmcMenuItem.getMarinadeMeatTMCMenuItem().getTmcprice();
            } else {
                String itemname = tmcMenuItem.getItemname() + " Marinade Box";
                holder.itemname_text.setText(itemname);
            }
        }

        boolean isShowCustomizeLayout = false;
        if (tmcMenuItem.getSelecteditemweight() != null) {
            isShowCustomizeLayout = true;
        }
        if (tmcMenuItem.getSelecteditemcut() != null) {
            try {
                JSONObject jsonObject = new JSONObject(tmcMenuItem.getSelecteditemcut());
                String cutname = jsonObject.getString("cutname");
                holder.itemcutname_text.setText(cutname);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            holder.itemcutname_text.setVisibility(View.VISIBLE);
            holder.itemcutnamefiller_layout.setVisibility(View.GONE);
            isShowCustomizeLayout = true;
        } else {
            holder.itemcutname_text.setVisibility(View.GONE);
            holder.itemcutnamefiller_layout.setVisibility(View.VISIBLE);
        }
        if (isShowCustomizeLayout) {
            holder.customize_layout.setVisibility(View.VISIBLE);
        } else {
            holder.customize_layout.setVisibility(View.GONE);
        }


        if (selectedqty > 1) {
            tmcprice = selectedqty * tmcprice;
        }

        double applieddiscountpercentage = tmcMenuItem.getApplieddiscpercentage();
        holder.itemprice_text.setText(rs + String.format("%.2f", tmcprice));
     // double strikedoutprice = tmcMenuItem.getStrikedoutprice();

        double strikedoutprice = 0;
        if (applieddiscountpercentage > 0) {
            int discpercentage = (int) applieddiscountpercentage;
            holder.itemdiscountdetails_textview.setText(discpercentage + "% OFF");
            double oldamount = tmcprice / ((100 - applieddiscountpercentage) / 100);
            double savedamount = oldamount - tmcprice;
            strikedoutprice = oldamount;
            if (savedamount > 0) {
                totalsavedamount = totalsavedamount + savedamount;
            }
            holder.itemdiscountdetails_textview.setVisibility(View.VISIBLE);
            holder.pricebeforediscount_textview.setText(rs + String.format("%.2f", strikedoutprice));
            holder.pricebeforediscount_textview.setPaintFlags(holder.pricebeforediscount_textview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.pricebeforediscount_textview.setVisibility(View.VISIBLE);

        } else {
            holder.itemdiscountdetails_textview.setVisibility(View.GONE);
            holder.pricebeforediscount_textview.setVisibility(View.GONE);
        }

     // Log.d("ShopBagAdapter", "totalsavedamount "+totalsavedamount);

        String netweight = tmcMenuItem.getNetweight();
        String portionsize = tmcMenuItem.getPortionsize();
        String grosswt = tmcMenuItem.getGrossweight();
        boolean isgrosswtavl = false; boolean isnetwtavl = false; boolean isportionsizeavl = false;
        if ((grosswt != null) && !(TextUtils.isEmpty(grosswt))) {
            isgrosswtavl = true;
            holder.grosswt_textview.setText("Gross wt. " + grosswt);
        }

        if ((netweight != null && !(TextUtils.isEmpty(netweight)))) {
            isnetwtavl = true;
            holder.netweight_textview.setText("Net wt. " + netweight);
        }
        if ((portionsize != null && !(TextUtils.isEmpty(portionsize)))) {
            isportionsizeavl = true;
            holder.portionsize_textview.setText(portionsize);
        }

        // We can show only two fields(out of Gross wt, Net wt, Portion size) in the UI. If Grosswt is avl, we will show Gross wt
        // and net wt or portionsize. If Gross wt is not avl, we will net wt and portion size. If portion size is not avl,
        // we will show only net wt.
        if ((isgrosswtavl && isnetwtavl && isportionsizeavl) ||
                             (isgrosswtavl && isnetwtavl)) {
            holder.grosswt_textview.setVisibility(View.VISIBLE);
            holder.grosswtseperator_layout.setVisibility(View.VISIBLE);
            holder.netweight_textview.setVisibility(View.VISIBLE);
            holder.netwtseperator_layout.setVisibility(View.GONE);
            holder.portionsize_textview.setVisibility(View.GONE);
        } else if (isgrosswtavl && isportionsizeavl) {
            holder.grosswt_textview.setVisibility(View.VISIBLE);
            holder.grosswtseperator_layout.setVisibility(View.VISIBLE);
            holder.netweight_textview.setVisibility(View.GONE);
            holder.netwtseperator_layout.setVisibility(View.GONE);
            holder.portionsize_textview.setVisibility(View.VISIBLE);
        } else if (isnetwtavl && isportionsizeavl) {
            holder.grosswt_textview.setVisibility(View.GONE);
            holder.grosswtseperator_layout.setVisibility(View.GONE);
            holder.netweight_textview.setVisibility(View.VISIBLE);
            holder.netwtseperator_layout.setVisibility(View.VISIBLE);
            holder.portionsize_textview.setVisibility(View.VISIBLE);
        } else if (isgrosswtavl) {
            holder.grosswt_textview.setVisibility(View.VISIBLE);
            holder.grosswtseperator_layout.setVisibility(View.GONE);
            holder.netweight_textview.setVisibility(View.GONE);
            holder.netwtseperator_layout.setVisibility(View.GONE);
            holder.portionsize_textview.setVisibility(View.GONE);
        } else if (isnetwtavl) {
            holder.grosswt_textview.setVisibility(View.GONE);
            holder.grosswtseperator_layout.setVisibility(View.GONE);
            holder.netweight_textview.setVisibility(View.VISIBLE);
            holder.netwtseperator_layout.setVisibility(View.GONE);
            holder.portionsize_textview.setVisibility(View.GONE);
        } else if (isportionsizeavl) {
            holder.grosswt_textview.setVisibility(View.GONE);
            holder.grosswtseperator_layout.setVisibility(View.GONE);
            holder.netweight_textview.setVisibility(View.GONE);
            holder.netwtseperator_layout.setVisibility(View.GONE);
            holder.portionsize_textview.setVisibility(View.VISIBLE);
        }

        holder.qty_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = tmcMenuItem.getSelectedqty();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "qtyclicked");
                bundle.putInt("qty", qty);
                bundle.putInt("position", position);
             // Log.d("ShoppingBagAdapter", "qty "+qty);
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });

        holder.customize_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = tmcMenuItem.getSelectedqty();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                if (tmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                    bundle.putString("menutype", "customizebtnclicked");
                    bundle.putInt("position", position);
                    bundle.putString("menuitemkey", tmcMenuItem.getKey());
                    msg.setData(bundle);
                    if (handler != null) {
                        handler.sendMessage(msg);
                    }
                } else {
                    bundle.putString("menutype", "customizeitemcutsweightclicked");
                    bundle.putInt("position", position);
                    bundle.putString("menuitemkey", tmcMenuItem.getKey());
                    msg.setData(bundle);
                    if (handler != null) {
                        handler.sendMessage(msg);
                    }
                }

            }
        });

     /* holder.customize_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = tmcMenuItem.getKey();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "customizebtnclicked");
                bundle.putString("key", key);
                bundle.putInt("position", position);
                bundle.putParcelable("tmcmenuitem", tmcMenuItem);
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        }); */
    }

    @Override
    public int getItemCount() {
        return tmcMenuItemList.size();
    }

    public void updateItemCount(int position, int count) {
        try {
            TMCMenuItem tmcMenuItem =  tmcMenuItemList.get(position);
            tmcMenuItem.setSelectedqty(count);
            ViewHolder holder = viewHolderList.get(position);
            if (tmcMenuItem != null) {
                holder.itemqty_text.setText("Qty: " + count);
                double tmcprice = tmcMenuItem.getTmcprice();
                int selectedqty = tmcMenuItem.getSelectedqty();
                if (selectedqty > 1) {
                    tmcprice = selectedqty * tmcprice;
                }
                String rs = context.getResources().getString((R.string.Rs));
                holder.itemprice_text.setText(rs + String.format("%.2f", tmcprice));

                double applieddiscountpercentage = tmcMenuItem.getApplieddiscpercentage();
                double strikedoutprice = 0;
                if (applieddiscountpercentage > 0) {
                    double oldamount = tmcprice / ((100 - applieddiscountpercentage) / 100);
                    strikedoutprice = oldamount;
                    holder.itemdiscountdetails_textview.setVisibility(View.VISIBLE);
                    holder.pricebeforediscount_textview.setText(rs + String.format("%.2f", strikedoutprice));
                    holder.pricebeforediscount_textview.setPaintFlags(holder.pricebeforediscount_textview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.pricebeforediscount_textview.setVisibility(View.VISIBLE);
                } else {
                    holder.itemdiscountdetails_textview.setVisibility(View.GONE);
                    holder.pricebeforediscount_textview.setVisibility(View.GONE);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeItem(int position, int count) {
        try {
            TMCMenuItem tmcMenuItem =  tmcMenuItemList.get(position);
            tmcMenuItem.setSelectedqty(0);
            TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
            notifyDataSetChanged();

         /* ViewHolder holder = viewHolderList.get(position);
            if (tmcMenuItem != null) {
                holder.itemqty_text.setText("Qty: " + count);
            } */
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

}
