package com.meatchop.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meatchop.R;
import com.meatchop.data.TMCCtgy;
import com.meatchop.models.Bannerdetails;
import com.meatchop.widget.AnimatedExpandableListView;
import android.os.Handler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimatedTMCCtgyAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    private ArrayList<String> tmcCtgyNameList;
    private HashMap<String, TMCCtgy> tmcCtgyHashMap;
    private HashMap<String, String> tmcCtgySubctgyMap;
    private Handler handler;

    private String TAG = "TMCCtgyAdapter";

    public AnimatedTMCCtgyAdapter(Context context, ArrayList<String> tmcCtgyNameList, HashMap<String, TMCCtgy> tmcCtgyHashMap) {
        this.context = context;
        this.tmcCtgyNameList = tmcCtgyNameList;
        this.tmcCtgyHashMap = tmcCtgyHashMap;
        loadSubCtgyCtgyMap();
    }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    private void loadSubCtgyCtgyMap() {
        if ((tmcCtgyHashMap == null) || (tmcCtgyHashMap.size() <= 0)) {
            return;
        }
        tmcCtgySubctgyMap = new HashMap<String, String>();
        Object[] keys = tmcCtgyHashMap.keySet().toArray();
        for (int i=0; i<keys.length; i++) {
            String ctgyname = (String) keys[i];
            TMCCtgy tmcCtgy = tmcCtgyHashMap.get(ctgyname);
            ArrayList<String> subctgylist = tmcCtgy.getSubCtgyList();
            for (int j=0; j<subctgylist.size(); j++) {
                String subctgyname = subctgylist.get(j);
                tmcCtgySubctgyMap.put(subctgyname, ctgyname);
            }
        }
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String ctgyname = tmcCtgyNameList.get(listPosition);
        TMCCtgy tmcCtgy = (TMCCtgy) tmcCtgyHashMap.get(ctgyname);
        return tmcCtgy.getSubCtgyList().get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    private String getCtgy(String subctgy) {
        if ((tmcCtgySubctgyMap == null) || (tmcCtgySubctgyMap.size() <= 0)) {
            return null;
        }
        return tmcCtgySubctgyMap.get(subctgy);
    }

    @Override
    public View getRealChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);
        expandedListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subctgyname = expandedListTextView.getText().toString();
                String tmcctgyname = tmcCtgySubctgyMap.get(subctgyname);
                try {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("menutype", "itemclick");
                    bundle.putString("subctgyname", subctgyname);
                    bundle.putString("tmcctgyname", tmcctgyname);
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
    public int getRealChildrenCount(int listPosition) {
        String ctgyname = tmcCtgyNameList.get(listPosition);
        ArrayList<String> subctgys = tmcCtgyHashMap.get(ctgyname).getSubCtgyList();
        return subctgys.size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.tmcCtgyNameList.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.tmcCtgyNameList.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        ImageView ctgyheader_image = (ImageView) convertView.findViewById(R.id.ctgyheader_image);

        String imageUrl = tmcCtgyHashMap.get(listTitle).getImageurl();
        Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .into(ctgyheader_image);
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
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

}
