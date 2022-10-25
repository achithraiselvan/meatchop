package com.meatchop.adapters;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meatchop.R;
import com.meatchop.data.Mealkitingredient;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarinadeSelectionViewAdapter extends RecyclerView.Adapter<MarinadeSelectionViewAdapter.ViewHolder>{

    public List<TMCMenuItem> menuItemList = null;
    private Context context;
    private ArrayList<MarinadeSelectionViewAdapter.ViewHolder> holderList;
    private String selectedmarinadelinkedcode;
    private Handler handler;
    private Handler selectionPaneHandler;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View listitem_layout;
        ImageView itemnotselected_view;
        ImageView itemselected_view;
        TMCTextView expandedListTextView;
        TMCTextView netweight_textview;
        TMCTextView itemprice_textview;

        ViewHolder(View view) {
            super(view);
            listitem_layout =  view.findViewById(R.id.listitem_layout);
            itemnotselected_view = (ImageView) view.findViewById(R.id.itemnotselected_view);
            itemselected_view = (ImageView) view.findViewById(R.id.itemselected_view);
            expandedListTextView = (TMCTextView) view.findViewById(R.id.expandedListTextView);
            netweight_textview = (TMCTextView) view.findViewById(R.id.netweight_textview);
            itemprice_textview = (TMCTextView) view.findViewById(R.id.itemprice_textview);
        }
    }

    public MarinadeSelectionViewAdapter(Context context, List<TMCMenuItem> menuitems,
                                         String selectedmarinadelinkedcode, Handler handler, Handler selectionPaneHandler) {
        this.context = context;
        this.selectedmarinadelinkedcode = selectedmarinadelinkedcode;
        this.handler = handler;
        this.selectionPaneHandler = selectionPaneHandler;
        this.menuItemList = new ArrayList<TMCMenuItem>();
        this.menuItemList.addAll(menuitems);
    }

    public void setSelectedMarinadeLinkedCode(String marinadeLinkedCode) { this.selectedmarinadelinkedcode = marinadeLinkedCode; }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    @NonNull
    @Override
    public MarinadeSelectionViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmcmarinadechildlist_item, parent, false);
        return new MarinadeSelectionViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MarinadeSelectionViewAdapter.ViewHolder holder, int position) {
        final TMCMenuItem menuItem =  menuItemList.get(position);
        Log.d("MarinadeViewAdapter", "menuitem name "+menuItem.getItemname());

        if (holderList == null) {
            holderList = new ArrayList<MarinadeSelectionViewAdapter.ViewHolder>();
        }
        holderList.add(position, holder);

        holder.expandedListTextView.setText(menuItem.getItemname());
        String netweight = menuItem.getNetweight();
        double itemprice = menuItem.getTmcprice();
        String portionsize = menuItem.getPortionsize();
        if ((netweight == null) || (TextUtils.isEmpty(netweight))) {
            holder.netweight_textview.setText(portionsize);
        } else {
            holder.netweight_textview.setText(netweight);
        }
        String rs = context.getResources().getString(R.string.Rs);
        holder.itemprice_textview.setText(rs + itemprice);

        if (selectedmarinadelinkedcode != null) {
            if (menuItem.getItemuniquecode().equalsIgnoreCase(selectedmarinadelinkedcode)) {
                holder.itemnotselected_view.setVisibility(View.GONE);
                holder.itemselected_view.setVisibility(View.VISIBLE);
            } else {
                holder.itemnotselected_view.setVisibility(View.VISIBLE);
                holder.itemselected_view.setVisibility(View.GONE);
            }
        }

        holder.listitem_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Unselect all other items
                unselectAllItems(menuItem.getKey());
                Message msg1 = new Message();
                Bundle bundle1 = new Bundle();
                bundle1.putString("menutype", "unselectitems");
                bundle1.putString("selectedmenuitemkey", menuItem.getKey());
                msg1.setData(bundle1);
                if (selectionPaneHandler != null) {
                    selectionPaneHandler.sendMessage(msg1);
                }

                holder.itemnotselected_view.setVisibility(View.GONE);
                holder.itemselected_view.setVisibility(View.VISIBLE);
                String subctgyname = holder.expandedListTextView.getText().toString();
                Log.d("MarinadeViewAdapter", "expandedTMCMenuItem itemname "+menuItem.getItemname());
                try {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("menutype", "itemclick");
                    bundle.putString("tmcmenuitemkey", menuItem.getKey());
                    bundle.putDouble("meatmenuitemprice", menuItem.getTmcprice());
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
        return menuItemList.size();
    }

    public void unselectAllItems(String selectedmenuitemkey) {
        if ((holderList == null) || (holderList.size() == 0)) {
            return;
        }
        for (int i=0; i<holderList.size(); i++) {
            MarinadeSelectionViewAdapter.ViewHolder holder = holderList.get(i);
            TMCMenuItem menuItem =  menuItemList.get(i);
            if (menuItem.getKey().equalsIgnoreCase(selectedmenuitemkey)) {
                continue;
            }
            holder.itemselected_view.setVisibility(View.GONE);
            holder.itemnotselected_view.setVisibility(View.VISIBLE);
        }
    }
}
