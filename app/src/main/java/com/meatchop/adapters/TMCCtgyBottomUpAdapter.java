package com.meatchop.adapters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meatchop.R;
import com.meatchop.data.TMCCtgy;
import com.meatchop.data.TMCSubCtgy;
import com.meatchop.widget.AnimatedExpandableListView;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class TMCCtgyBottomUpAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    private String selectedSubCtgy;
    private String selectedTMCCtgy;
    private ArrayList<String> tmcCtgyNameList;
    private TreeMap<String, ArrayList<TMCSubCtgy>> tmcCtgyHashMap;
    private Handler handler;
    private HashMap<String, String> tmcCtgySubctgyMap;

    private String TAG = "TMCCtgyBottomUpAdapter";

    private ArrayList<View> headerfieldSeperatorList;
    private ArrayList<View> itemfieldSeperatorList;

    private ArrayList<GroupViewHolder> groupViewHolders;
    private int selectedgroupposition;

    public TMCCtgyBottomUpAdapter(Context context, String selectedTMCCtgy, String selectedSubCtgy, ArrayList<String> tmcCtgyNameList,
                                                                         TreeMap tmcCtgyHashMap) {
        this.context = context;
        this.selectedTMCCtgy = selectedTMCCtgy;
        this.selectedSubCtgy = selectedSubCtgy;
        this.tmcCtgyNameList = tmcCtgyNameList;
        this.tmcCtgyHashMap = tmcCtgyHashMap;
        Log.d(TAG, "tmcCtgyNameList "+tmcCtgyNameList);
        loadSubCtgyCtgyMap();
    }

    public void changeCtgyNames(String tmcctgy, String subctgy) {
        this.selectedTMCCtgy = tmcctgy;
        this.selectedSubCtgy = subctgy;
    }

    private void loadSubCtgyCtgyMap() {
        if ((tmcCtgyHashMap == null) || (tmcCtgyHashMap.size() <= 0)) {
            return;
        }
        tmcCtgySubctgyMap = new HashMap<String, String>();
        Object[] keys = tmcCtgyHashMap.keySet().toArray();
        for (int i=0; i<keys.length; i++) {
            String ctgyname = (String) keys[i];
            ArrayList<TMCSubCtgy> tmcSubCtgyList = (ArrayList<TMCSubCtgy>) tmcCtgyHashMap.get(ctgyname);

            if (tmcSubCtgyList != null) {
                for (int j=0; j<tmcSubCtgyList.size(); j++) {
                    String subctgyname = tmcSubCtgyList.get(j).getSubctgyname();
                    tmcCtgySubctgyMap.put(subctgyname, ctgyname);
                }
            }
        }

        Log.d(TAG, "tmcCtgySubctgyMap "+tmcCtgySubctgyMap);
    }

    public void setSelectedGroupPosition(int position) {
        this.selectedgroupposition = position;
    }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String ctgyname = tmcCtgyNameList.get(listPosition);
        ArrayList<TMCSubCtgy> tmcSubCtgyArrayList = tmcCtgyHashMap.get(ctgyname);
        return tmcSubCtgyArrayList.get(expandedListPosition).getSubctgyname();
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
            convertView = layoutInflater.inflate(R.layout.tmcctgybottomuplist_item, parent,
                    false);

            if (convertView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((ViewGroup) convertView.findViewById(R.id.list_item_container)).getLayoutTransition()
                            .enableTransitionType(LayoutTransition.CHANGING);
                }
                holder.listposition = listPosition;
                holder.expandedlistposition = expandedListPosition;

                holder.container = convertView.findViewById(R.id.list_item_container);
                holder.expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
                holder.checkmark_view = convertView.findViewById(R.id.checkmark_view);
                convertView.setTag(holder);

            }
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }


        final View holderView = convertView;

        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        holder.expandedListTextView.setText(expandedListText);

        if (expandedListText.equalsIgnoreCase(selectedSubCtgy)) {
            holder.checkmark_view.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(50,0,0,0);
            holder.expandedListTextView.setLayoutParams(params);
        } else {
            holder.checkmark_view.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(110,0,0,0);
            holder.expandedListTextView.setLayoutParams(params);
        }

        holder.expandedListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChildViewHolder holder = (ChildViewHolder) holderView.getTag();
                String subctgyname = holder.expandedListTextView.getText().toString();
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
                    changeCtgyNames(tmcctgyname, subctgyname);

                    notifyDataSetChanged();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int listPosition) {
        if ((tmcCtgyNameList == null) || (tmcCtgyNameList.size() <= 0)) {
            return 0;
        }
        String ctgyname = tmcCtgyNameList.get(listPosition);
        ArrayList<TMCSubCtgy> subctgys = tmcCtgyHashMap.get(ctgyname);
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
            convertView = layoutInflater.inflate(R.layout.tmcctgybottomuplist_group, parent,
                    false);
            if (convertView != null) {
                holder.position = listPosition;
                holder.container = convertView.findViewById(R.id.list_item_container);
                holder.listTitleTextView = (TextView) convertView
                        .findViewById(R.id.listTitle);
                holder.listtitle_fieldseperator = convertView.findViewById(R.id.listtitle_fieldseperator);
                holder.expandarrow_indicator = (ImageView) convertView.findViewById(R.id.expandarrow_indicator);
                holder.collapsearrow_indicator = (ImageView) convertView.findViewById(R.id.collapsearrow_indicator);
                convertView.setTag(holder);

                if (groupViewHolders == null) {
                    groupViewHolders = new ArrayList<>();
                }
                Object prevHolder = null;
                if (groupViewHolders.size() > listPosition) {
                    prevHolder = groupViewHolders.get(listPosition);
                }
                if (prevHolder == null) {
                    groupViewHolders.add(listPosition, holder);
                }

            }
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        final View holderView = convertView;
        String listTitle = (String) getGroup(listPosition);
        holder.listTitleTextView.setText(listTitle);

        Log.d(TAG, "listTitle "+listTitle);
     /* if (listTitle.equalsIgnoreCase("TMC Meal Kits")) {
            holder.expandarrow_indicator.setVisibility(View.GONE);
            holder.collapsearrow_indicator.setVisibility(View.GONE);
            holder.listTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GroupViewHolder holder = (GroupViewHolder) holderView.getTag();
                    String ctgyname = holder.listTitleTextView.getText().toString();
                    try {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("menutype", "itemclick");
                        bundle.putString("tmcctgyname", ctgyname);
                        msg.setData(bundle);
                        if (getHandler() != null) {
                            getHandler().sendMessage(msg);
                        }
                        changeCtgyNames(ctgyname, "");

                        notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return convertView;
        } */


        if (isExpanded) {
            holder.expandarrow_indicator.setVisibility(View.GONE);
            holder.collapsearrow_indicator.setVisibility(View.VISIBLE);
            holder.listtitle_fieldseperator.setVisibility(View.VISIBLE);
        } else {
            holder.expandarrow_indicator.setVisibility(View.VISIBLE);
            holder.collapsearrow_indicator.setVisibility(View.GONE);
            if (selectedgroupposition == -1) {  // Hide all the field seperators
                holder.listtitle_fieldseperator.setVisibility(View.GONE);
            } else if (selectedgroupposition+1 == listPosition) {  // Show Field seperator next to Expanded list
                holder.listtitle_fieldseperator.setVisibility(View.VISIBLE);
            } else {
                holder.listtitle_fieldseperator.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    public void showFieldSeperator(int position) {
        if ((headerfieldSeperatorList == null) || (headerfieldSeperatorList.size() == 0)) {
            return;
        }
        if (headerfieldSeperatorList.size() > position) {
            View fieldseperator1 = headerfieldSeperatorList.get(position);
            if (fieldseperator1 != null) {
             // Log.d(TAG, "fieldseperator1 "+fieldseperator1.getId());
                fieldseperator1.setVisibility(View.VISIBLE);
            }
            View fieldseperator2 = headerfieldSeperatorList.get(position+1);
            if (fieldseperator2 != null) {
             // Log.d(TAG, "fieldseperator2 "+fieldseperator2.getId());
                fieldseperator2.setVisibility(View.VISIBLE);
            }
        }
     /* if (itemfieldSeperatorList.size() > position) {
            View fieldseperator2 = itemfieldSeperatorList.get(position);
            if (fieldseperator2 != null) {
                fieldseperator2.setVisibility(View.VISIBLE);
            }
        } */
    }

    public void hideFieldSeperator(int position) {
        if ((headerfieldSeperatorList == null) || (headerfieldSeperatorList.size() == 0)
                || (itemfieldSeperatorList == null) || (itemfieldSeperatorList.size() == 0)) {
            return;
        }
        if (headerfieldSeperatorList.size() > position) {
            View fieldseperator1 = headerfieldSeperatorList.get(position);
            if (fieldseperator1 != null) {
                fieldseperator1.setVisibility(View.GONE);
            }
        }
     /* if (itemfieldSeperatorList.size() > position) {
            View fieldseperator2 = itemfieldSeperatorList.get(position);
            if (fieldseperator2 != null) {
                fieldseperator2.setVisibility(View.GONE);
            }
        } */
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    class ChildViewHolder {
        int listposition;
        int expandedlistposition;
        View container;
        TextView expandedListTextView;
        View checkmark_view;
    }

    class GroupViewHolder {
        int position;
        View container;
        TextView listTitleTextView;
        View listtitle_fieldseperator;
        ImageView expandarrow_indicator;
        ImageView collapsearrow_indicator;
    }

}
