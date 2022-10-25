package com.meatchop.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.Rating;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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
import com.meatchop.data.Ordertrackingdetails;
import com.meatchop.sqlitedata.Orderdetailslocal;
import com.meatchop.sqlitedata.Ordertrackingdetailslocal;
import com.meatchop.sqlitedata.Ratingorderdetailslocal;
import com.meatchop.widget.TMCTextView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TMCOrdersAdapter extends RecyclerView.Adapter<TMCOrdersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Orderdetailslocal> orderdetailsList;
    private HashMap<String, Ordertrackingdetailslocal> ordertrackingdetailsHashMap;
    private HashMap<String, Ratingorderdetailslocal> ratingorderdetailslocalHashMap;
    private String TAG = "TMCOrdersAdapter";
    private Handler handler;
    private HashMap<String, ViewHolder> orderIdHolderMap;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TMCTextView itemname_textview;
        TMCTextView itemprice_textview;
        ImageView checkoutimage_view;
        View itemdetails_layout;
        View itemdetails_arrow;
        View rflayout1;
        View rfstar1;
        View rfstar1_sel;
        View rflayout2;
        View rfstar2;
        View rfstar2_sel;
        View rflayout3;
        View rfstar3;
        View rfstar3_sel;
        View rflayout4;
        View rfstar4;
        View rfstar4_sel;
        View rflayout5;
        View rfstar5;
        View rfstar5_sel;
        View tmctile_progressbar;
        TMCTextView orderstatus_text;
        TMCTextView orderplacedtime_text;
        View rateorder_layout;
        View feedbackprovided_layout;
        View trackorder_layout;
        View orderstatusdetails_layout;
        View orderstatusloadinganim_layout;
        View tellusmore_layout;


        ViewHolder(View view) {
            super(view);
            itemname_textview = (TMCTextView) view.findViewById(R.id.itemname_textview);
            itemprice_textview = (TMCTextView) view.findViewById(R.id.itemprice_textview);
            checkoutimage_view = (ImageView) view.findViewById(R.id.checkoutimage_view);
            tmctile_progressbar = view.findViewById(R.id.tmctile_progressbar);
            itemdetails_arrow = view.findViewById(R.id.itemdetails_arrow);
            itemdetails_layout = view.findViewById(R.id.itemdetails_layout);
            orderstatus_text = (TMCTextView) view.findViewById(R.id.orderstatus_text);
            orderplacedtime_text = (TMCTextView) view.findViewById(R.id.orderplacedtime_text);
            rflayout1 = view.findViewById(R.id.rflayout1);
            rfstar1 = view.findViewById(R.id.rfstar1);
            rfstar1_sel = view.findViewById(R.id.rfstar1_sel);

            rflayout2 = view.findViewById(R.id.rflayout2);
            rfstar2 = view.findViewById(R.id.rfstar2);
            rfstar2_sel = view.findViewById(R.id.rfstar2_sel);

            rflayout3 = view.findViewById(R.id.rflayout3);
            rfstar3 = view.findViewById(R.id.rfstar3);
            rfstar3_sel = view.findViewById(R.id.rfstar3_sel);

            rflayout4 = view.findViewById(R.id.rflayout4);
            rfstar4 = view.findViewById(R.id.rfstar4);
            rfstar4_sel = view.findViewById(R.id.rfstar4_sel);

            rflayout5 = view.findViewById(R.id.rflayout5);
            rfstar5 = view.findViewById(R.id.rfstar5);
            rfstar5_sel = view.findViewById(R.id.rfstar5_sel);

            rateorder_layout = view.findViewById(R.id.rateorder_layout);
            trackorder_layout = view.findViewById(R.id.trackorder_layout);
            feedbackprovided_layout = view.findViewById(R.id.feedbackprovided_layout);

            orderstatusdetails_layout = view.findViewById(R.id.orderstatusdetails_layout);
            orderstatusloadinganim_layout = view.findViewById(R.id.orderstatusloadinganim_layout);
            tellusmore_layout = view.findViewById(R.id.tellusmore_layout);
        }
    }

    public void setHandler(Handler handler) { this.handler = handler; }

 /* public TMCOrdersAdapter(Context context, ArrayList<Orderdetails> orderdetailslist) {
        this.context = context;
        this.orderdetailsList = new ArrayList<Orderdetails>();
        this.orderdetailsList.addAll(orderdetailslist);

        Collections.sort(this.orderdetailsList);
        orderIdHolderMap = new HashMap<String, ViewHolder>();
        // Log.d("TMCOrdersAdapter", "recipeList "+recipeList.toString());
    } */

    public TMCOrdersAdapter(Context context, ArrayList<Orderdetailslocal> orderdetailslist,
                            HashMap<String, Ordertrackingdetailslocal> ordertrackingdetailshashmap,
                            HashMap<String, Ratingorderdetailslocal> ratingorderdetailsHashMap) {
        this.context = context;
        this.orderdetailsList = new ArrayList<Orderdetailslocal>();
        if ((orderdetailslist != null) && (orderdetailslist.size() > 0)) {
            this.orderdetailsList.addAll(orderdetailslist);
        }

        this.ordertrackingdetailsHashMap = new HashMap<String, Ordertrackingdetailslocal>();
        if ((ordertrackingdetailshashmap != null) && (ordertrackingdetailshashmap.size() > 0)) {
            this.ordertrackingdetailsHashMap.putAll(ordertrackingdetailshashmap);
        }
     // Collections.sort(this.orderdetailsList);

        this.ratingorderdetailslocalHashMap = new HashMap<String, Ratingorderdetailslocal>();
        if ((ratingorderdetailsHashMap != null) && (ratingorderdetailsHashMap.size() > 0)) {
            this.ratingorderdetailslocalHashMap.putAll(ratingorderdetailsHashMap);
        }

        orderIdHolderMap = new HashMap<String, ViewHolder>();
     // Log.d("TMCOrdersAdapter", "recipeList "+recipeList.toString());
    }

    @NonNull
    @Override
    public TMCOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tmcorders, parent, false);
        return new TMCOrdersAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Orderdetailslocal orderdetails = orderdetailsList.get(position);
        orderIdHolderMap.put(orderdetails.getOrderid(), holder);

        String itemdesp = orderdetails.getConsolidatedItemDespWithQty();
        double payableamount = orderdetails.getPayableamount();

        holder.itemname_textview.setText(itemdesp);
        String rs = context.getResources().getString(R.string.Rs);
        holder.itemprice_textview.setText(rs + payableamount);

        Ordertrackingdetailslocal ordertrackingdetails = ordertrackingdetailsHashMap.get(orderdetails.getOrderid());
        String orderstatus = (ordertrackingdetails != null) ? ordertrackingdetails.getOrderstatus() : "";
        updateOrderStatusInHolder(orderstatus, holder);

        if (orderstatus.equalsIgnoreCase("Delivered")) {
            Ratingorderdetailslocal ratingorderdetailslocal = ratingorderdetailslocalHashMap.get(orderdetails.getOrderid());
            if (ratingorderdetailslocal != null) {
                holder.feedbackprovided_layout.setVisibility(View.VISIBLE);
                holder.rateorder_layout.setVisibility(View.GONE);
            } else {
                holder.rateorder_layout.setVisibility(View.VISIBLE);
                holder.feedbackprovided_layout.setVisibility(View.GONE);
            }
        } else {
            holder.feedbackprovided_layout.setVisibility(View.GONE);
            holder.rateorder_layout.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(orderstatus)) {

        }

        String orderplaceddate = orderdetails.getOrderplaceddate();
        holder.orderplacedtime_text.setText(orderplaceddate);

        String imageUrl = orderdetails.getImageurl();
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

        setRatingOnClickListeners(holder);

        holder.itemdetails_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "itemclicked");
             // bundle.putString("orderkey", orderdetails.getServerkey());
                bundle.putString("orderid", orderdetails.getOrderid());
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });

        holder.itemdetails_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "itemclicked");
             // bundle.putString("orderkey", orderdetails.getServerkey());
                bundle.putString("orderid", orderdetails.getOrderid());
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });

        holder.trackorder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "trackorder");
             // bundle.putString("orderkey", orderdetails.getServerkey());
                bundle.putString("orderid", orderdetails.getOrderid());
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });

        holder.rateorder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "rateorder");
                bundle.putString("orderid", orderdetails.getOrderid());
             // bundle.putString("orderkey", orderdetails.getServerkey());
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });

        holder.tellusmore_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "tellusmore");
                bundle.putString("orderid", orderdetails.getOrderid());
             // bundle.putString("orderkey", orderdetails.getServerkey());
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderdetailsList.size();
    }

    private void setRatingOnClickListeners(ViewHolder holder) {
        holder.rflayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.rfstar1.getVisibility() == View.VISIBLE) {
                    // Unselect STAR1,STAR2,STAR3,STAR4
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);
                    holder.rfstar4_sel.setVisibility(View.GONE);
                    holder.rfstar4.setVisibility(View.VISIBLE);
                    holder.rfstar3_sel.setVisibility(View.GONE);
                    holder.rfstar3.setVisibility(View.VISIBLE);
                    holder.rfstar2_sel.setVisibility(View.GONE);
                    holder.rfstar2.setVisibility(View.VISIBLE);

                    // SELECT STAR1
                    holder.rfstar1_sel.setVisibility(View.VISIBLE);
                    holder.rfstar1.setVisibility(View.GONE);
                } else {

                    if (holder.rfstar2_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR1
                        holder.rfstar1_sel.setVisibility(View.VISIBLE);
                        holder.rfstar1.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR1
                        holder.rfstar1_sel.setVisibility(View.GONE);
                        holder.rfstar1.setVisibility(View.VISIBLE);
                    }

                    // Unselect STAR2,STAR3,STAR4,STAR5
                    holder.rfstar2_sel.setVisibility(View.GONE);
                    holder.rfstar2.setVisibility(View.VISIBLE);
                    holder.rfstar3_sel.setVisibility(View.GONE);
                    holder.rfstar3.setVisibility(View.VISIBLE);
                    holder.rfstar4_sel.setVisibility(View.GONE);
                    holder.rfstar4.setVisibility(View.VISIBLE);
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.rflayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.rfstar2.getVisibility() == View.VISIBLE) {

                    // Unselect STAR3,STAR4,STAR5
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);
                    holder.rfstar4_sel.setVisibility(View.GONE);
                    holder.rfstar4.setVisibility(View.VISIBLE);
                    holder.rfstar3_sel.setVisibility(View.GONE);
                    holder.rfstar3.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2
                    holder.rfstar2_sel.setVisibility(View.VISIBLE);
                    holder.rfstar2.setVisibility(View.GONE);
                    holder.rfstar1_sel.setVisibility(View.VISIBLE);
                    holder.rfstar1.setVisibility(View.GONE);
                } else {

                    if (holder.rfstar3_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR2
                        holder.rfstar2_sel.setVisibility(View.VISIBLE);
                        holder.rfstar2.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR2
                        holder.rfstar2_sel.setVisibility(View.GONE);
                        holder.rfstar2.setVisibility(View.VISIBLE);
                    }

                    // Unselect STAR3,STAR4,STAR5
                    holder.rfstar3_sel.setVisibility(View.GONE);
                    holder.rfstar3.setVisibility(View.VISIBLE);
                    holder.rfstar4_sel.setVisibility(View.GONE);
                    holder.rfstar4.setVisibility(View.VISIBLE);
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.rflayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.rfstar3.getVisibility() == View.VISIBLE) {

                    // Unselect STAR4,STAR5
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);
                    holder.rfstar4_sel.setVisibility(View.GONE);
                    holder.rfstar4.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2,STAR3
                    holder.rfstar3_sel.setVisibility(View.VISIBLE);
                    holder.rfstar3.setVisibility(View.GONE);
                    holder.rfstar2_sel.setVisibility(View.VISIBLE);
                    holder.rfstar2.setVisibility(View.GONE);
                    holder.rfstar1_sel.setVisibility(View.VISIBLE);
                    holder.rfstar1.setVisibility(View.GONE);
                } else {
                    if (holder.rfstar4_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR3
                        holder.rfstar3_sel.setVisibility(View.VISIBLE);
                        holder.rfstar3.setVisibility(View.GONE);
                    } else {
                        // UnSelect STAR3
                        holder.rfstar3_sel.setVisibility(View.GONE);
                        holder.rfstar3.setVisibility(View.VISIBLE);
                    }
                    // Unselect STAR4,STAR5
                    holder.rfstar4_sel.setVisibility(View.GONE);
                    holder.rfstar4.setVisibility(View.VISIBLE);
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.rflayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.rfstar4.getVisibility() == View.VISIBLE) {
                    // Unselect STAR5
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);

                    // Select STAR1,STAR2,STAR3,STAR4
                    holder.rfstar4_sel.setVisibility(View.VISIBLE);
                    holder.rfstar4.setVisibility(View.GONE);
                    holder.rfstar3_sel.setVisibility(View.VISIBLE);
                    holder.rfstar3.setVisibility(View.GONE);
                    holder.rfstar2_sel.setVisibility(View.VISIBLE);
                    holder.rfstar2.setVisibility(View.GONE);
                    holder.rfstar1_sel.setVisibility(View.VISIBLE);
                    holder.rfstar1.setVisibility(View.GONE);
                } else {
                    if (holder.rfstar5_sel.getVisibility() == View.VISIBLE) {
                        // SELECT STAR4
                        holder.rfstar4_sel.setVisibility(View.VISIBLE);
                        holder.rfstar4.setVisibility(View.GONE);
                    } else {
                        // Unselect STAR4
                        holder.rfstar4_sel.setVisibility(View.GONE);
                        holder.rfstar4.setVisibility(View.VISIBLE);
                    }
                    // Unselect STAR5
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.rflayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.rfstar5.getVisibility() == View.VISIBLE) {
                    holder.rfstar5_sel.setVisibility(View.VISIBLE);
                    holder.rfstar5.setVisibility(View.GONE);
                    holder.rfstar4_sel.setVisibility(View.VISIBLE);
                    holder.rfstar4.setVisibility(View.GONE);
                    holder.rfstar3_sel.setVisibility(View.VISIBLE);
                    holder.rfstar3.setVisibility(View.GONE);
                    holder.rfstar2_sel.setVisibility(View.VISIBLE);
                    holder.rfstar2.setVisibility(View.GONE);
                    holder.rfstar1_sel.setVisibility(View.VISIBLE);
                    holder.rfstar1.setVisibility(View.GONE);
                } else {
                    // Unselect STAR5
                    holder.rfstar5_sel.setVisibility(View.GONE);
                    holder.rfstar5.setVisibility(View.VISIBLE);
                }
            }
        });
    }

 /* public void updateOrderStatusDetails(HashMap<String, Ordertrackingdetails> orderTrackingDetailsHashMap) {
        try {
            if ((this.ordertrackingdetailsHashMap == null) || (this.ordertrackingdetailsHashMap.size() <= 0)) {
                this.ordertrackingdetailsHashMap = new HashMap<String, Ordertrackingdetails>();
                this.ordertrackingdetailsHashMap.putAll(orderTrackingDetailsHashMap);
            }

            if ((orderIdHolderMap == null) || (orderIdHolderMap.size() > 0)) {
                Object[] keys = orderIdHolderMap.keySet().toArray();
                for (int i=0; i<keys.length; i++) {
                    String orderid = (String) keys[i];
                    ViewHolder holder = orderIdHolderMap.get(orderid);
                    Ordertrackingdetails ordertrackingdetails = ordertrackingdetailsHashMap.get(orderid);
                    String orderstatus = ordertrackingdetails.getOrderstatus();
                    updateOrderStatusInHolder(orderstatus, holder);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/

    public void updateOrderStatusInAdapter(String orderid, String orderstatus) {
        if (orderIdHolderMap == null) {
            return;
        }
        ViewHolder holder = orderIdHolderMap.get(orderid);
        updateOrderStatusInHolder(orderstatus, holder);
    }

    public void updateRatingStatusInHolder(boolean ratingprovided, String orderid) {
        if (orderIdHolderMap == null) {
            return;
        }
        ViewHolder holder = orderIdHolderMap.get(orderid);
        if (holder == null) {
            return;
        }
        if (ratingprovided) {
            holder.feedbackprovided_layout.setVisibility(View.VISIBLE);
            holder.rateorder_layout.setVisibility(View.GONE);
        }
    }

    private void updateOrderStatusInHolder(String orderstatus, ViewHolder holder) {
        if ((holder == null) || (orderstatus == null)) {
            return;
        }
        holder.orderstatus_text.setVisibility(View.VISIBLE);
        if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_NEW)) {
            holder.orderstatus_text.setText("Received");
            holder.rateorder_layout.setVisibility(View.GONE);
        } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CONFIRMED)) {
            holder.orderstatus_text.setText("Confirmed");
            holder.rateorder_layout.setVisibility(View.GONE);
        } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_READYFORPICKUP)) {
            holder.orderstatus_text.setText("Confirmed");
            holder.rateorder_layout.setVisibility(View.GONE);
        } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_PICKEDUP)) {
            holder.orderstatus_text.setText("Out for Delivery");
            holder.rateorder_layout.setVisibility(View.GONE);
        } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_DELIVERED)) {
            holder.orderstatus_text.setText("Delivered");
            holder.rateorder_layout.setVisibility(View.VISIBLE);
        } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CANCELLED)) {
            holder.orderstatus_text.setText("CANCELLED");
            holder.rateorder_layout.setVisibility(View.GONE);
        } else {
            holder.orderstatusloadinganim_layout.setVisibility(View.GONE);
            holder.orderstatus_text.setVisibility(View.GONE);
            Typeface boldface = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
            holder.orderplacedtime_text.setTypeface(boldface);
        }
        holder.orderstatusloadinganim_layout.setVisibility(View.GONE);
        holder.orderstatusdetails_layout.setVisibility(View.VISIBLE);
    }


}
