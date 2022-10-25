package com.meatchop.adapters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Build;
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
import android.widget.ListView;
import android.widget.TextView;

import com.meatchop.R;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.widget.AnimatedExpandableListView;
import com.meatchop.widget.MarinadeExpandableListView;
import com.meatchop.widget.TMCDataCatalog;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MarindesListViewAdapter extends MarinadeExpandableListView.MarinatedExpandableListAdapter  {

    private String TAG = "MarinadesListViewAdapter";
    private Context context;
    private HashMap<String, ArrayList<TMCMenuItem>> tmcMenuItemHashMap;  // {TMCSubctgyKey -- ArrayList<TMCMenuItem>}
    private Handler handler;
    private ArrayList<String> tmcSubCtgyNameList;
    private String selectedmarinadelinkedcode;

    private ArrayList<ChildViewHolder> childViewHolderArrayList;

    public MarindesListViewAdapter(Context context, HashMap<String, ArrayList<TMCMenuItem>> tmcmenuitemmap) {
        this.context = context;
        this.tmcMenuItemHashMap = new HashMap<>();
        this.tmcMenuItemHashMap.putAll(tmcmenuitemmap);
        this.tmcSubCtgyNameList = new ArrayList<>();
        Object[] keys = tmcmenuitemmap.keySet().toArray();
        for (int i=0; i<keys.length; i++) {
            String tmcsubctgyname = (String) keys[i];
            Log.d(TAG, "tmcsubctgyname "+tmcsubctgyname);
            this.tmcSubCtgyNameList.add(tmcsubctgyname);
        }
        this.childViewHolderArrayList = new ArrayList<ChildViewHolder>();
    }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    public void setSelectedMarinadeLinkedCode(String marinadeLinkedCode) { this.selectedmarinadelinkedcode = marinadeLinkedCode; }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String tmcsubctgyname = tmcSubCtgyNameList.get(listPosition);
        ArrayList<TMCMenuItem> tmcMenuItems = tmcMenuItemHashMap.get(tmcsubctgyname);
        return tmcMenuItems.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }


    @Override
    public View getRealChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder holder;
        if (convertView != null) {
            ChildViewHolder holder1 = (ChildViewHolder) convertView.getTag();
            if ((listPosition != holder1.listposition) && (expandedListPosition == holder1.expandedlistposition)) {
                convertView = null;
            }
        }

        if (convertView == null) {
            holder = new ChildViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.tmcmarinadechildlist_item, parent,
                    false);

            if (convertView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((ViewGroup) convertView.findViewById(R.id.list_item_container)).getLayoutTransition()
                            .enableTransitionType(LayoutTransition.CHANGING);
                }
                holder.listposition = listPosition;
                holder.expandedlistposition = expandedListPosition;

                holder.container = convertView.findViewById(R.id.list_item_container);
                holder.expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListTextView);
                holder.listitem_layout = convertView.findViewById(R.id.listitem_layout);
                holder.itemselected_view = convertView.findViewById(R.id.itemselected_view);
                holder.itemnotselected_view = convertView.findViewById(R.id.itemnotselected_view);
                holder.netweight_textview = convertView.findViewById(R.id.netweight_textview);
                holder.itemprice_textview = convertView.findViewById(R.id.itemprice_textview);
                convertView.setTag(holder);

            }
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolderArrayList.add(holder);

        final View holderView = convertView;

        final TMCMenuItem expandedTMCMenuItem = (TMCMenuItem) getChild(listPosition, expandedListPosition);
        holder.expandedListTextView.setText(expandedTMCMenuItem.getItemname());
        String netweight = expandedTMCMenuItem.getNetweight();
        double itemprice = expandedTMCMenuItem.getTmcprice();
        String portionsize = expandedTMCMenuItem.getPortionsize();
        if ((netweight == null) || (TextUtils.isEmpty(netweight))) {
            holder.netweight_textview.setText(portionsize);
        } else {
            holder.netweight_textview.setText(netweight);
        }
        String rs = context.getResources().getString(R.string.Rs);
        holder.itemprice_textview.setText(rs + itemprice);

        if (selectedmarinadelinkedcode != null) {
            if (expandedTMCMenuItem.getItemuniquecode().equalsIgnoreCase(selectedmarinadelinkedcode)) {
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
                ChildViewHolder holder = (ChildViewHolder) holderView.getTag();
                unSelectItems();
                holder.itemnotselected_view.setVisibility(View.GONE);
                holder.itemselected_view.setVisibility(View.VISIBLE);
                String subctgyname = holder.expandedListTextView.getText().toString();
                Log.d(TAG, "expandedTMCMenuItem itemname "+expandedTMCMenuItem.getItemname());
                try {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("menutype", "itemclick");
                    bundle.putString("tmcmenuitemkey", expandedTMCMenuItem.getKey());
                    bundle.putDouble("meatmenuitemprice", expandedTMCMenuItem.getTmcprice());
                    msg.setData(bundle);
                    if (getHandler() != null) {
                        getHandler().sendMessage(msg);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        String tmcsubctgyname = tmcSubCtgyNameList.get(groupPosition);
        ArrayList<TMCMenuItem> tmcMenuItems = tmcMenuItemHashMap.get(tmcsubctgyname);
        return tmcMenuItems.size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.tmcSubCtgyNameList.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.tmcSubCtgyNameList.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        GroupViewHolder holder;
        if (convertView != null) {
            GroupViewHolder holder1 = (GroupViewHolder) convertView.getTag();
            if (listPosition != holder1.position) {
                convertView = null;
            }
        }

        if (convertView == null) {
            holder = new GroupViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.tmcmarinadeheaderlist_item, parent,
                    false);
            if (convertView != null) {
                holder.position = listPosition;
                holder.container = convertView.findViewById(R.id.list_item_container);
                holder.listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
                holder.listtitle_fieldseperator = convertView.findViewById(R.id.listtitle_fieldseperator);
                convertView.setTag(holder);


            }
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }



        final View holderView = convertView;
        if (listPosition > 0) {
            holder.listtitle_fieldseperator.setVisibility(View.VISIBLE);
        }
        String listTitle = (String) getGroup(listPosition);
        String tmcsubctgyname = TMCDataCatalog.getInstance().getTMCSubCtgyName(listTitle);
        holder.listTitleTextView.setText(tmcsubctgyname);

        Log.d(TAG, "listTitle "+listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public void unSelectItems() {
        if ((childViewHolderArrayList == null) || (childViewHolderArrayList.size() <= 0)) {
            return;
        }
        for (int i=0; i<childViewHolderArrayList.size(); i++) {
            ChildViewHolder holder = childViewHolderArrayList.get(i);
            if (holder == null) { continue; }
            if (holder.itemselected_view != null) {
                holder.itemselected_view.setVisibility(View.GONE);
            }
            if (holder.itemnotselected_view != null) {
                holder.itemnotselected_view.setVisibility(View.VISIBLE);
            }
        }
    }


    class ChildViewHolder {
        int listposition;
        int expandedlistposition;
        View container;
        View listitem_layout;
        TextView expandedListTextView;
        View itemnotselected_view;
        View itemselected_view;
        TMCTextView netweight_textview;
        TMCTextView itemprice_textview;
    }

    class GroupViewHolder {
        int position;
        View container;
        TextView listTitleTextView;
        View listtitle_fieldseperator;
    }



}
