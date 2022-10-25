/*$Id$*/
package com.meatchop.widget;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.meatchop.R;
import com.meatchop.adapters.MeatchopCtgyAdapter;
import com.meatchop.adapters.TMCCtgyAdapter;

import java.util.ArrayList;


public class StaticTMCCtgyPane extends LinearLayout {

    private Context context;
    private LinearLayout fieldArea;
    private View lastView;
    private Handler handler;

    public StaticTMCCtgyPane(Context context) {
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
        mInflater.inflate(R.layout.static_tmcctgy_pane, this, true);
        fieldArea = (LinearLayout) findViewById(R.id.add_fields_area);
    }

 /* private LinearLayout getFormItem(String imageUrl, ArrayList<String> ctgys) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.tmcctgy_view_item, this, false);

        ImageView ctgyheader_image = (ImageView) v.findViewById(R.id.ctgyheader_image);

        final ExpandableHeightListView ctgysList = (ExpandableHeightListView) v.findViewById(R.id.ctgys_listview);
        ctgysList.setExpanded(true);

        Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .into(ctgyheader_image);

        MeatchopCtgyAdapter adapter = new MeatchopCtgyAdapter(context, ctgys);
     // adapter.setHelper(getHelper());
     // adapter.setHandler(getHandler());
        ctgysList.setAdapter(adapter);

        ctgyheader_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("StaticTMCCtgyPane", "ctgyheader_image onclick listener");
                Log.d("StaticTMCCtgyPane", "ctgysList.getVisibility() "+ctgysList.getVisibility());
                if (ctgysList.getVisibility() == View.VISIBLE) {
                    ctgysList.setVisibility(View.GONE);
                } else {
                    ctgysList.setVisibility(View.VISIBLE);
                }
            }
        });

        return v;
    } */

    private LinearLayout getFormItem(String imageUrl, ArrayList<String> ctgys) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.banner_list_item, this, false);

        ImageView imageSlide = (ImageView) v.findViewById(R.id.imageSlide);


        Log.d("StaticTMCCtgyPane", "imageUrl "+imageUrl);
        Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .into(imageSlide);

        return v;
    }

    public void addFormItem(String imageUrl, ArrayList<String> ctgys) {
        lastView = getFormItem(imageUrl, ctgys);
        fieldArea.addView(lastView);
    }

}
