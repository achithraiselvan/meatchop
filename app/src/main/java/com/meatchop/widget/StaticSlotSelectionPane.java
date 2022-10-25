package com.meatchop.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.meatchop.R;
import com.meatchop.adapters.TMCSearchListAdapter;
import com.meatchop.adapters.TMCSlotAdapter;
import com.meatchop.data.Deliveryslotdetails;
import com.meatchop.data.Deliveryslots;
import com.meatchop.utils.TMCUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticSlotSelectionPane extends LinearLayout {

    private String TAG = "StaticSlotSelectionPane";

    private Context context;
    private LinearLayout fieldArea;
    private View lastView;
    private Handler handler;
    private Deliveryslots selecteddeliveryslotdetails;
    private double userdeliverydistance = 0;
    private int deliverytype;

    private HashMap<String, TMCSlotAdapter> tmcslotAdapterMap;


    public StaticSlotSelectionPane(Context context) {
        super(context);
        this.context = context;
        initLayout();
    }

    public void removeAllViews() {
        lastView = null;
        fieldArea.removeAllViews();
    }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    public void setSelectedDeliverySlotDetails(Deliveryslots selecteddeliveryslotdetails) {
        this.selecteddeliveryslotdetails = selecteddeliveryslotdetails;
    }

    public void setUserdeliverydistance(double userdeliverydistance) {
        this.userdeliverydistance = userdeliverydistance;
    }

    public void setDeliverytype(int deliverytype) {
        this.deliverytype = deliverytype;
    }

    private void initLayout() {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.static_slotselection_pane, this, true);
        fieldArea = (LinearLayout) findViewById(R.id.add_fields_area);
    }

    private LinearLayout getFormItem(String sessiondetails, ArrayList<Deliveryslots> slotdetailsList) {
        if ((slotdetailsList == null) || (slotdetailsList.size() <= 0)) {
            return null;
        }
        if (tmcslotAdapterMap == null) {
            tmcslotAdapterMap = new HashMap<String, TMCSlotAdapter>();
        }
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.slotselection_view_item, this, false);
        if (v != null) {
            View gridviewseperator_layout = v.findViewById(R.id.gridviewseperator_layout);
            TMCTextView sessiondetails_textview = (TMCTextView) v.findViewById(R.id.sessiondetails_textview);
            sessiondetails_textview.setText(sessiondetails);
            GridView slots_gridview = (GridView) v.findViewById(R.id.slots_gridview);
            GridView slots_gridview_expressdelivery = (GridView) v.findViewById(R.id.slots_gridview_expressdelivery);

            TMCSlotAdapter tmcSlotAdapter = new TMCSlotAdapter(context, slotdetailsList);
            tmcSlotAdapter.setUserdeliverydistance(userdeliverydistance);
            tmcSlotAdapter.setSessionType(sessiondetails);
            tmcSlotAdapter.setHandler(createTMCSlotAdapterHandler());
            tmcSlotAdapter.setSelectedDeliverySlotDetails(selecteddeliveryslotdetails);
            tmcSlotAdapter.setDeliverytype(deliverytype);
            tmcslotAdapterMap.put(sessiondetails, tmcSlotAdapter);

            if (sessiondetails.contains("Delivery")) {
                slots_gridview_expressdelivery.setVisibility(View.VISIBLE);
                slots_gridview.setVisibility(View.GONE);
                slots_gridview_expressdelivery.setAdapter(tmcSlotAdapter);
                slots_gridview_expressdelivery.setVerticalScrollBarEnabled(false);
                setGridViewHeightBasedOnChildren(slots_gridview_expressdelivery, 3);
            } else {
                slots_gridview_expressdelivery.setVisibility(View.GONE);
                slots_gridview.setVisibility(View.VISIBLE);
                slots_gridview.setAdapter(tmcSlotAdapter);
                slots_gridview.setVerticalScrollBarEnabled(false);
                setGridViewHeightBasedOnChildren(slots_gridview, 3);
                if (slotdetailsList.size() <= 3) {
                    gridviewseperator_layout.setVisibility(View.VISIBLE);
                } else {
                    gridviewseperator_layout.setVisibility(View.GONE);
                }
            }


        }

        return v;
    }

    public void addFormItem(String sessiondetails, ArrayList<Deliveryslots> slotdetailsList) {
        lastView = getFormItem(sessiondetails, slotdetailsList);
        fieldArea.addView(lastView);
    }

    private void setGridViewHeightBasedOnChildren(GridView gridView, int noOfColumns) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // adapter is not set yet
            return;
        }

        int totalHeight; //total height to set on grid view
        int items = gridViewAdapter.getCount(); //no. of items in the grid
        int rows; //no. of rows in grid

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x;
        if( items > noOfColumns ){
            x = items/noOfColumns;

            //Check if exact no. of rows of rows are available, if not adding 1 extra row
            if(items%noOfColumns != 0) {
                rows = (int) (x + 1);
            }else {
                rows = (int) (x);
            }
            totalHeight *= rows;

            //Adding any vertical space set on grid view
            totalHeight += gridView.getVerticalSpacing() * rows;
        }

        //Setting height on grid view
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    private void sendHandlerMessage(String menutype, Parcelable parcelable, String selectedsessiontype, double newdeliverycharges) {
        Message msg =  new Message();
        Bundle bundle = new Bundle();
        bundle.putString("menutype", menutype);
        bundle.putString("selectedsessiontype", selectedsessiontype);
        bundle.putParcelable("deliveryslotdetails", parcelable);
        bundle.putDouble("newdeliverycharges", newdeliverycharges);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private Handler createTMCSlotAdapterHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");

                if (menutype.equalsIgnoreCase("deliveryslotselected")) {
                    String selectedsessiontype = bundle.getString("selectedsessiontype");
                    Parcelable parcelable = bundle.getParcelable("deliveryslotdetails");
                    double newdeliverycharges = bundle.getDouble("newdeliverycharges");
                    TMCSlotAdapter[] tmcSlotAdapters = getTMCSlotAdapterList();
                    for (int i=0; i<tmcSlotAdapters.length; i++) {
                        TMCSlotAdapter adapter = tmcSlotAdapters[i];
                        if (selectedsessiontype.equalsIgnoreCase(adapter.getSessionType())) {
                            continue;
                        }
                        adapter.unselectSlots();
                    }
                    sendHandlerMessage(menutype, parcelable, selectedsessiontype, newdeliverycharges);
                } else if (menutype.equalsIgnoreCase("deliveryslotunselected")) {
                    sendHandlerMessage(menutype, null, "", 0);
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private TMCSlotAdapter[] getTMCSlotAdapterList() {
        if ((tmcslotAdapterMap == null) || (tmcslotAdapterMap.size() == 0)) {
            return null;
        }

        Object[] values = tmcslotAdapterMap.values().toArray();
        TMCSlotAdapter[] tmcSlotAdapters = new TMCSlotAdapter[values.length];
        for (int i=0; i<values.length; i++) {
            tmcSlotAdapters[i] = (TMCSlotAdapter) values[i];
        }
        return tmcSlotAdapters;
    }

    public void unselectTimeSlots() {
        if ((tmcslotAdapterMap == null) || (tmcslotAdapterMap.size() == 0)) {
            return;
        }
        TMCSlotAdapter[] adapters = getTMCSlotAdapterList();
        for (int i=0; i<adapters.length; i++) {
            TMCSlotAdapter adapter = adapters[i];
            if (adapter != null) {
                adapter.unselectSlots();
            }
        }
    }


}
