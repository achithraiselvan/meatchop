package com.meatchop.adapters;

import android.content.Context;
import android.graphics.Paint;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.meatchop.R;
import com.meatchop.data.Orderdetails;
import com.meatchop.widget.TMCTextView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TMCDetailOrderAdapter extends RecyclerView.Adapter<TMCDetailOrderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<JSONObject> itemdetailsList;
    private String TAG = "TMCDetailOrderAdapter";
    private Handler handler;
    private double itemtotal;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TMCTextView itemname_textview;
        TMCTextView itemprice_textview;
        ImageView checkoutimage_view;
        TMCTextView itemweight_textview;
        View tmctile_progressbar;
        TMCTextView pricebeforediscount_textview;
        TMCTextView itemdiscountdetails_textview;
        TMCTextView itemcutname_text;
        View itemcutnamefiller_layout;
        View itempricefiller_layout;
        View itemnamefiller_layout;

        ViewHolder(View view) {
            super(view);
            itemname_textview = (TMCTextView) view.findViewById(R.id.itemname_textview);
            itemprice_textview = (TMCTextView) view.findViewById(R.id.itemprice_textview);
            itemweight_textview = (TMCTextView) view.findViewById(R.id.itemweight_textview);
            checkoutimage_view = (ImageView) view.findViewById(R.id.checkoutimage_view);
            tmctile_progressbar = view.findViewById(R.id.tmctile_progressbar);
            pricebeforediscount_textview = (TMCTextView) view.findViewById(R.id.pricebeforediscount_textview);
            itemdiscountdetails_textview = (TMCTextView) view.findViewById(R.id.itemdiscountdetails_textview);
            itemcutname_text = (TMCTextView) view.findViewById(R.id.itemcutname_text);
            itemcutnamefiller_layout = view.findViewById(R.id.itemcutnamefiller_layout);
            itempricefiller_layout = view.findViewById(R.id.itempricefiller_layout);
            itemnamefiller_layout = view.findViewById(R.id.itemnamefiller_layout);
        }
    }

    public void setHandler(Handler handler) { this.handler = handler; }

    public TMCDetailOrderAdapter(Context context, JSONArray itemdetailslist) {
        this.context = context;
        this.itemdetailsList = new ArrayList<JSONObject>();
        try {
            for(int i = 0; i < itemdetailslist.length(); i++) {
                JSONObject jsonobject = itemdetailslist.getJSONObject(i);
                this.itemdetailsList.add(jsonobject);
            }
        } catch (Exception ex) {

        }
     // Log.d(TAG, "itemdetailsList "+itemdetailsList.toString());
    }

    @NonNull
    @Override
    public TMCDetailOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tmcdetailorders, parent, false);
        return new TMCDetailOrderAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        JSONObject itemdetails = itemdetailsList.get(position);
     // Log.d(TAG, "itemdetails "+itemdetails);

        String imageUrl = "";
        try {
            String itemname = ""; int itemqty = 0;
            double itemprice = itemdetails.getDouble("tmcprice");
            if (itemdetails.has("marinadeitemdesp")) {
                JSONObject marinadejsonobj = itemdetails.getJSONObject("marinadeitemdesp");
                String marinadename = itemdetails.getString("itemname");
                String meatname = marinadejsonobj.getString("itemname");
                String meatqty = marinadejsonobj.getString("grossweight");
                double meatprice = marinadejsonobj.getDouble("tmcprice");
                itemprice = meatprice + itemprice;
                itemname = meatname + " " + meatqty + " With " + marinadename;
            } else {
                itemname = itemdetails.getString("itemname");
                String netweight = itemdetails.getString("netweight");
                String portionsize = itemdetails.getString("portionsize");
                if (TextUtils.isEmpty(netweight)) {
                    if (TextUtils.isEmpty(portionsize)) {
                        holder.itemweight_textview.setVisibility(View.GONE);
                    } else {
                        holder.itemweight_textview.setText("" + portionsize);
                        holder.itemweight_textview.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.itemweight_textview.setText("Net: " + netweight);
                    holder.itemweight_textview.setVisibility(View.VISIBLE);
                }
            }

            itemqty = itemdetails.getInt("quantity");
            String itemnameqty = itemname + " x " + itemqty;
            holder.itemname_textview.setText(itemnameqty);
            String rs = context.getResources().getString(R.string.Rs);
            holder.itemprice_textview.setText(rs + itemprice);
            imageUrl = itemdetails.getString("checkouturl");

            double applieddiscpercentage = 0; double discountamount = 0;
            if (itemdetails.has("applieddiscpercentage")) {
                try {
                    String applieddiscpercinstr = itemdetails.getString("applieddiscpercentage");
                    applieddiscpercentage = Double.parseDouble(applieddiscpercinstr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (itemdetails.has("discountamount")) {
                try {
                    String discamountinstr = itemdetails.getString("discountamount");
                    discountamount = Double.parseDouble(discamountinstr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (applieddiscpercentage > 0) {
                int discpercentage = (int) applieddiscpercentage;
             /* double oldamount = itemprice / ((100 - applieddiscpercentage) / 100);
                double savedamount = oldamount - itemprice;
                double strikedoutprice = oldamount; */
                double strikedoutprice = itemprice + discountamount;
                holder.itemdiscountdetails_textview.setText(discpercentage + "% OFF");
                holder.itemdiscountdetails_textview.setVisibility(View.VISIBLE);
                holder.pricebeforediscount_textview.setText(rs + String.format("%.2f", strikedoutprice));
                holder.pricebeforediscount_textview.setPaintFlags(holder.pricebeforediscount_textview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.pricebeforediscount_textview.setVisibility(View.VISIBLE);
            } else {
                holder.pricebeforediscount_textview.setVisibility(View.GONE);
                holder.itemdiscountdetails_textview.setVisibility(View.GONE);
            }

            if (itemdetails.has("cutname")) {
                String itemcutname = itemdetails.getString("cutname");
                if ((itemcutname != null) && (!TextUtils.isEmpty(itemcutname))) {
                    holder.itemcutname_text.setText(itemcutname);
                    holder.itemcutname_text.setVisibility(View.VISIBLE);
                    holder.itemcutnamefiller_layout.setVisibility(View.VISIBLE);
                    holder.itempricefiller_layout.setVisibility(View.GONE);
                    holder.itemnamefiller_layout.setVisibility(View.GONE);
                } else {
                    holder.itemnamefiller_layout.setVisibility(View.VISIBLE);
                    holder.itempricefiller_layout.setVisibility(View.VISIBLE);
                    holder.itemcutname_text.setVisibility(View.GONE);
                    holder.itemcutnamefiller_layout.setVisibility(View.GONE);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

     // Log.d(TAG, "imageUrl "+imageUrl);
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

    }

    @Override
    public int getItemCount() {
        return itemdetailsList.size();
    }

}
