package com.meatchop.slider;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.meatchop.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventBannerSliderAdapter extends
        SliderViewAdapter<EventBannerSliderAdapter.SliderAdapterVH> {

    private Context context;
    private Handler handler;
    private List<SliderItem> mSliderItems = new ArrayList<>();
    private int noofitems = 0;

    public EventBannerSliderAdapter(Context context) {
        this.context = context;
    }

    public void setHandler(Handler handler) { this.handler = handler; }
    public Handler getHandler() { return handler; }

    public void setNoOfItems(int noOfItems) { noofitems = noofitems; }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = mSliderItems.get(position);

     // viewHolder.textViewDescription.setText(sliderItem.getDescription());
        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.textViewDescription.setTextColor(Color.WHITE);
        Glide.with(context)
                .load(sliderItem.getImageUrl())
                .fitCenter()
                .into(viewHolder.imageViewBackground);

        if (noofitems == (position+1)) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("menutype", "hideloadingpanel");
            msg.setData(bundle);
            if (getHandler() != null) {
                getHandler().sendMessage(msg);
            }
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             // Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
             // Log.d("EventBanSliderAdapter", "onBindViewHolder position "+position);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "itemclick");
                bundle.putInt("position", position);
                msg.setData(bundle);
                if (getHandler() != null) {
                    getHandler().sendMessage(msg);
                }
            }
        });
    }

 // @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }

}
