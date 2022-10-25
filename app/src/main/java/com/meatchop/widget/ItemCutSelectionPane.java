package com.meatchop.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meatchop.R;
import com.meatchop.adapters.ItemCutSelectionViewAdapter;
import com.meatchop.adapters.ItemWeightSelectionViewAdapter;
import com.meatchop.adapters.MarinadeSelectionViewAdapter;
import com.meatchop.data.ItemCutDetails;
import com.meatchop.data.ItemWeightDetails;
import com.meatchop.data.TMCMenuItem;

import java.util.ArrayList;

public class ItemCutSelectionPane extends LinearLayout {
    private String TAG = "ItemCutSelectionPane";

    private Context context;
    private LinearLayout fieldArea;
    private View lastView;
    private Handler handler;
    private ArrayList<ItemCutSelectionViewAdapter> adapterList;

    public ItemCutSelectionPane(Context context) {
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

    private void initLayout() {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.itemcut_selection_pane, this, true);
        fieldArea = (LinearLayout) findViewById(R.id.add_fields_area);
        adapterList = new ArrayList<ItemCutSelectionViewAdapter>();
    }

    private LinearLayout getFormItem(String title, ArrayList<ItemCutDetails> itemCutDetailsArrayList) {
        if ((itemCutDetailsArrayList == null) || (itemCutDetailsArrayList.size() <= 0)) {
            return null;
        }

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.itemcutselection_view_item, this, false);
        if (v != null) {
            TMCTextView listTitle = (TMCTextView) v.findViewById(R.id.listTitle);
            listTitle.setText(title);

            RecyclerView itemcuts_recyclerview = (RecyclerView) v.findViewById(R.id.itemcuts_recyclerview);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            itemcuts_recyclerview.setLayoutManager(mLayoutManager);
            itemcuts_recyclerview.setItemAnimator(new DefaultItemAnimator());

            ItemCutSelectionViewAdapter itemcutsSelectionViewAdapter = new ItemCutSelectionViewAdapter(
                    context, itemCutDetailsArrayList, handler);
            itemcuts_recyclerview.setAdapter(itemcutsSelectionViewAdapter);
            adapterList.add(itemcutsSelectionViewAdapter);
        }
        return v;
    }

    public void addFormItem(String title, ArrayList<ItemCutDetails> itemCutDetailsArrayList) {
        lastView = getFormItem(title, itemCutDetailsArrayList);
        fieldArea.addView(lastView);
    }

    private Handler createMarinadeSelectionPaneHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("unselectitems")) {
                    String selectedmenuitemkey = bundle.getString("selectedmenuitemkey");
                    // unselectItems(selectedmenuitemkey);
                }
                return false;
            }
        };
        return new Handler(callback);
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

}
