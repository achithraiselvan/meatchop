package com.meatchop.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meatchop.R;
import com.meatchop.data.ItemCutDetails;
import com.meatchop.data.ItemWeightDetails;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.utils.TMCUtil;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.List;

public class ItemCutSelectionViewAdapter extends RecyclerView.Adapter<ItemCutSelectionViewAdapter.ViewHolder> {

    public String TAG = "ItemCutSelectionViewAdapter";
    public ArrayList<ItemCutDetails> itemCutDetailsList = null;
    private Context context;
    private ArrayList<ItemCutSelectionViewAdapter.ViewHolder> holderList;
    private Handler handler;
    private Handler selectionPaneHandler;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View listitem_layout;
        ImageView itemnotselected_view;
        ImageView itemselected_view;
        TMCTextView itemcutname_textview;
        TMCTextView itemcutdesp_textview;
        ImageView itemcut_image;
        TMCTextView netweight_textview;
        View portionsize_layout;
        TMCTextView portionsize_textview;

        ViewHolder(View view) {
            super(view);
            listitem_layout =  view.findViewById(R.id.listitem_layout);
            itemnotselected_view = (ImageView) view.findViewById(R.id.itemnotselected_view);
            itemselected_view = (ImageView) view.findViewById(R.id.itemselected_view);
            itemcutname_textview = (TMCTextView) view.findViewById(R.id.itemcutname_textview);
            itemcutdesp_textview = (TMCTextView) view.findViewById(R.id.itemcutdesp_textview);
            itemcut_image = (ImageView) view.findViewById(R.id.itemcut_image);
            netweight_textview = (TMCTextView) view.findViewById(R.id.netweight_textview);
            portionsize_layout = view.findViewById(R.id.portionsize_layout);
            portionsize_textview = (TMCTextView) view.findViewById(R.id.portionsize_textview);
        }
    }

    public ItemCutSelectionViewAdapter(Context context, ArrayList<ItemCutDetails> itemCutDetailsArrayList,
                                          Handler handler) {
        this.context = context;
        this.handler = handler;
        this.itemCutDetailsList = new ArrayList<ItemCutDetails>();
        this.itemCutDetailsList.addAll(itemCutDetailsArrayList);
    }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    @NonNull
    @Override
    public ItemCutSelectionViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcutchildlist_item, parent, false);
        return new ItemCutSelectionViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemCutSelectionViewAdapter.ViewHolder holder, int position) {
        final ItemCutDetails itemCutDetails =  itemCutDetailsList.get(position);

        if (holderList == null) {
            holderList = new ArrayList<ItemCutSelectionViewAdapter.ViewHolder>();
        }
        holderList.add(position, holder);

        if (itemCutDetails.isIsdefault()) {
            holder.itemselected_view.setVisibility(View.VISIBLE);
            holder.itemnotselected_view.setVisibility(View.GONE);
        } else {
            holder.itemselected_view.setVisibility(View.GONE);
            holder.itemnotselected_view.setVisibility(View.VISIBLE);
        }

        String itemcutname = itemCutDetails.getCutname();
        String itemcutdesp = itemCutDetails.getCutdesp();
        holder.itemcutname_textview.setText(itemcutname);
        if (!TextUtils.isEmpty(itemcutdesp)) {
            holder.itemcutdesp_textview.setText(itemcutdesp);
            holder.itemcutdesp_textview.setVisibility(View.VISIBLE);
        } else {
            holder.itemcutdesp_textview.setVisibility(View.GONE);
            holder.itemcutname_textview.setTextColor(context.getResources().getColor(R.color.primary_text_color));
            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
            holder.itemcutname_textview.setTypeface(face);
        }

        double netwtingrams = itemCutDetails.getNetweightingrams();
        if (netwtingrams > 0) {
         // String netwt = String.format("%.0f", netwtingrams);
            holder.netweight_textview.setText("Net wt. " + itemCutDetails.getNetweight());
            holder.netweight_textview.setVisibility(View.VISIBLE);
        } else {
            holder.netweight_textview.setVisibility(View.GONE);
        }
        String imagename = itemCutDetails.getCutimagename();
        try {
            if ((imagename != null) && (!TextUtils.isEmpty(imagename))) {
                if (context != null) {
                    Resources resources = context.getResources();
                    int resourceId = resources.getIdentifier(imagename, "mipmap", context.getPackageName());
                    holder.itemcut_image.setImageDrawable(resources.getDrawable(resourceId));
                    holder.itemcut_image.setVisibility(View.VISIBLE);
                }
            } else {
                holder.itemcut_image.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            holder.itemcut_image.setVisibility(View.GONE);
            ex.printStackTrace();
        }

        if ((itemCutDetails.getPortionsize() != null) && !(TextUtils.isEmpty(itemCutDetails.getPortionsize()))) {
            holder.portionsize_layout.setVisibility(View.VISIBLE);
            holder.portionsize_textview.setText(itemCutDetails.getPortionsize());
        } else {
            holder.portionsize_layout.setVisibility(View.GONE);
        }

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
                    bundle.putString("menutype", "itemcutclicked");
                    bundle.putString("itemcutkey", itemCutDetails.getCutkey());
                    bundle.putString("itemcutname", itemCutDetails.getCutname());
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
        return itemCutDetailsList.size();
    }

    public void unselectAllItems() {
        if ((holderList == null) || (holderList.size() == 0)) {
            return;
        }
        for (int i=0; i<holderList.size(); i++) {
            ItemCutSelectionViewAdapter.ViewHolder holder = holderList.get(i);
            holder.itemselected_view.setVisibility(View.GONE);
            holder.itemnotselected_view.setVisibility(View.VISIBLE);
        }
    }
}
