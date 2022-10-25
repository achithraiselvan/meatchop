package com.meatchop.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meatchop.R;
import com.meatchop.data.TMCCtgy;
import com.meatchop.models.Bannerdetails;
import com.meatchop.slider.EventBannerSliderAdapter;
import com.meatchop.slider.SliderItem;
import com.meatchop.widget.AnimatedExpandableListView;
import com.meatchop.widget.TMCTextView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TMCCtgyAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    private ArrayList<String> tmcCtgyNameList;
    private HashMap<String, String> tmcCtgyUrlMap;
    private HashMap<String, ArrayList<String>> tmcSubCtgyMap;

    private String TAG = "TMCCtgyAdapter";

    public TMCCtgyAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;

        Log.d("CustomExpListAdapter", "expandableListTitle "+expandableListTitle.toString());
        Log.d("CustomExpListAdapter", "expandableListDetail "+expandableListDetail.toString());
    }

    public TMCCtgyAdapter(Context context, ArrayList<String> tmcCtgyNameList, HashMap<String, String> tmcCtgyUrlMap,
                                           HashMap<String, ArrayList<String>> tmcSubCtgyMap, ArrayList<Bannerdetails> bannerdetailsArrayList) {
        this.context = context;
        this.tmcCtgyNameList = tmcCtgyNameList;
        this.tmcCtgyUrlMap = tmcCtgyUrlMap;
        this.tmcSubCtgyMap = tmcSubCtgyMap;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.tmcSubCtgyMap.get(this.tmcCtgyNameList.get(listPosition))
                .get(expandedListPosition);
     // return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
     //         .get(expandedListPosition);
    }

    // expandableListTitle should be replaced by tmcCtgyNameList
    // expandableListDetail should be replaced by tmcSubCtgyMap

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        Log.d("CustomExpListAdapter", "getChildView expandedListText "+expandedListText+" listPosition "+listPosition+" expandedListPosition "+expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        Log.d("CustomExpListAdapter", "expandedListText "+expandedListText);
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.tmcSubCtgyMap.get(this.tmcCtgyNameList.get(listPosition)).size();
     // return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
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
        Log.d("CustomExpListAdapter", "getGroupView listTitle "+listTitle+" listPosition "+listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        ImageView ctgyheader_image = (ImageView) convertView.findViewById(R.id.ctgyheader_image);

        String imageUrl = tmcCtgyUrlMap.get(listTitle);
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
