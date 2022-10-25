package com.meatchop.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.meatchop.R;
import com.meatchop.adapters.MarinadeSelectionViewAdapter;
import com.meatchop.data.TMCMenuItem;

import java.util.ArrayList;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StaticMarinadeSelectionPane extends LinearLayout {

    private String TAG = "StaticMarinadeSelectionPane";

    private Context context;
    private LinearLayout fieldArea;
    private View lastView;
    private Handler handler;
    private String selectedmarinadelinkedcode;
    private ArrayList<MarinadeSelectionViewAdapter> adapterList;

    public StaticMarinadeSelectionPane(Context context) {
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

    public void setSelectedMarinadeLinkedCode(String marinadeLinkedCode) { this.selectedmarinadelinkedcode = marinadeLinkedCode; }

    private void initLayout() {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.static_marinadeselection_pane, this, true);
        fieldArea = (LinearLayout) findViewById(R.id.add_fields_area);
        adapterList = new ArrayList<MarinadeSelectionViewAdapter>();
    }

    private LinearLayout getFormItem(String subctgykey, ArrayList<TMCMenuItem> menuItems) {
        if ((menuItems == null) || (menuItems.size() <= 0)) {
            return null;
        }

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.marinadeselection_view_item, this, false);
        if (v != null) {
            TMCTextView listTitle = (TMCTextView) v.findViewById(R.id.listTitle);
            String tmcsubctgyname = TMCDataCatalog.getInstance().getTMCSubCtgyName(subctgykey);
            listTitle.setText(tmcsubctgyname);

            RecyclerView marinades_recyclerview = (RecyclerView) v.findViewById(R.id.marinades_recyclerview);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            marinades_recyclerview.setLayoutManager(mLayoutManager);
            marinades_recyclerview.setItemAnimator(new DefaultItemAnimator());

            MarinadeSelectionViewAdapter marinadeSelectionViewAdapter = new MarinadeSelectionViewAdapter(
                                                                context, menuItems, selectedmarinadelinkedcode, handler, createMarinadeSelectionPaneHandler());
            marinades_recyclerview.setAdapter(marinadeSelectionViewAdapter);
            adapterList.add(marinadeSelectionViewAdapter);
        }
        return v;
    }

    public void addFormItem(String subctgykey, ArrayList<TMCMenuItem> menuitems) {
        lastView = getFormItem(subctgykey, menuitems);
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
                    unselectItems(selectedmenuitemkey);
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

    public void unselectItems(String selectedmenuitemkey) {
        if (adapterList == null) {
            return;
        }
        for (int i=0; i<adapterList.size(); i++) {
            MarinadeSelectionViewAdapter adapter = adapterList.get(i);
            adapter.unselectAllItems(selectedmenuitemkey);
        }
    }

}
