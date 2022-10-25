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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meatchop.R;
import com.meatchop.data.ItemWeightDetails;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.List;

public class ItemWeightSelectionViewAdapter extends RecyclerView.Adapter<ItemWeightSelectionViewAdapter.ViewHolder> {

    public String TAG = "ItemWtSelViewAdapter";
    public ArrayList<ItemWeightDetails> itemWeightDetailsList = null;
    private Context context;
    private ArrayList<ItemWeightSelectionViewAdapter.ViewHolder> holderList;
    private String selectedmarinadelinkedcode;
    private Handler handler;
    private Handler selectionPaneHandler;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View listitem_layout;
        ImageView itemnotselected_view;
        ImageView itemselected_view;
        TMCTextView itemgrossweight_textview;
        TMCTextView itemnetweight_textview;
        TMCTextView itemprice_textview;
        View portionsize_layout;
        TMCTextView portionsize_textview;

        ViewHolder(View view) {
            super(view);
            listitem_layout =  view.findViewById(R.id.listitem_layout);
            itemnotselected_view = (ImageView) view.findViewById(R.id.itemnotselected_view);
            itemselected_view = (ImageView) view.findViewById(R.id.itemselected_view);
            itemgrossweight_textview = (TMCTextView) view.findViewById(R.id.itemgrossweight_textview);
            itemnetweight_textview = (TMCTextView) view.findViewById(R.id.itemnetweight_textview);
            itemprice_textview = (TMCTextView) view.findViewById(R.id.itemprice_textview);
            portionsize_layout = view.findViewById(R.id.portionsize_layout);
            portionsize_textview = (TMCTextView) view.findViewById(R.id.portionsize_textview);
        }
    }

    public ItemWeightSelectionViewAdapter(Context context, ArrayList<ItemWeightDetails> itemWeightDetailsArrayList,
                                                                                                  Handler handler) {
        this.context = context;
        this.handler = handler;
        this.itemWeightDetailsList = new ArrayList<ItemWeightDetails>();
        this.itemWeightDetailsList.addAll(itemWeightDetailsArrayList);
    }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    @NonNull
    @Override
    public ItemWeightSelectionViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemweightchildlist_item, parent, false);
        return new ItemWeightSelectionViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemWeightSelectionViewAdapter.ViewHolder holder, int position) {
        final ItemWeightDetails itemWeightDetails =  itemWeightDetailsList.get(position);

        if (holderList == null) {
            holderList = new ArrayList<ItemWeightSelectionViewAdapter.ViewHolder>();
        }
        holderList.add(position, holder);

        if (itemWeightDetails.isIsdefault()) {
            holder.itemselected_view.setVisibility(View.VISIBLE);
            holder.itemnotselected_view.setVisibility(View.GONE);
        } else {
            holder.itemselected_view.setVisibility(View.GONE);
            holder.itemnotselected_view.setVisibility(View.VISIBLE);
        }

        double grosswtingrams = itemWeightDetails.getGrossweightingrams();
        double netwtingrams = itemWeightDetails.getNetweightingrams();

        if (grosswtingrams > 0) {
         // String grosswt = String.format("%.0f", grosswtingrams);
            holder.itemgrossweight_textview.setText("Gross wt. " + itemWeightDetails.getGrossweight());
        }

        if (netwtingrams > 0) {
         // String netwt = String.format("%.0f", netwtingrams);
            holder.itemnetweight_textview.setText("Net wt. " + itemWeightDetails.getNetweight());
        }

        String portionsize = itemWeightDetails.getPortionsize();
        if ((portionsize != null) && !(TextUtils.isEmpty(portionsize))) {
            holder.portionsize_layout.setVisibility(View.VISIBLE);
            holder.portionsize_textview.setText(portionsize);
        } else {
            holder.portionsize_layout.setVisibility(View.GONE);
        }

        String rs = context.getResources().getString(R.string.Rs);

     // double roundedprice = Math.round(itemWeightDetails.getItemprice());
        holder.itemprice_textview.setText(rs + String.format("%.2f", itemWeightDetails.getItemprice()));

        holder.listitem_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Unselect all other items
                unselectAllItems();

                holder.itemnotselected_view.setVisibility(View.GONE);
                holder.itemselected_view.setVisibility(View.VISIBLE);
                try {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("menutype", "itemweightclicked");
                    bundle.putString("weightkey", itemWeightDetails.getWeightkey());
                    bundle.putDouble("grosswtingrams", itemWeightDetails.getGrossweightingrams());
                    msg.setData(bundle);
                    if (getHandler() != null) {
                        getHandler().sendMessage(msg);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemWeightDetailsList.size();
    }

    public void unselectAllItems() {
        if ((holderList == null) || (holderList.size() == 0)) {
            return;
        }
        for (int i=0; i<holderList.size(); i++) {
            ItemWeightSelectionViewAdapter.ViewHolder holder = holderList.get(i);
            holder.itemselected_view.setVisibility(View.GONE);
            holder.itemnotselected_view.setVisibility(View.VISIBLE);
        }
    }
}
