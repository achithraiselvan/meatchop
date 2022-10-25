package com.meatchop.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.meatchop.R;
import com.meatchop.data.TMCTile;
import com.meatchop.widget.TMCTextView;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.Indicator;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class TMCTileRecyclerViewAdapter extends RecyclerView.Adapter<TMCTileRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "TMCTileRecyclerAdapter";

    private ArrayList<TMCTile> tmcTiles = new ArrayList<>();
    private Context mContext;
    private Handler handler;

    public TMCTileRecyclerViewAdapter(Context context, ArrayList<TMCTile> tmcTileArrayList) {
        tmcTiles = new ArrayList<>();
        if (tmcTileArrayList != null) {
            tmcTiles.addAll(tmcTileArrayList);
        }
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmctile_grid_item, parent, false);
        return new ViewHolder(view);
    }

    public void setHandler(Handler handler) { this.handler = handler; }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
     // Log.d(TAG, "onBindViewHolder: called.");

        if ((tmcTiles == null) || (tmcTiles.size() <= 0)) {
            return;
        }
        TMCTile tmcTile = tmcTiles.get(position);

        holder.tmctile_name.setText(tmcTile.getName());
        Glide.with(mContext)
                .load(tmcTile.getImageurl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.tmctile_progressbar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.tmctile_progressbar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .fitCenter()
                .into(holder.tmctile_imageview);

        holder.tmctile_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             // Log.d(TAG, "onClick: clicked on: " + mNames.get(position));
                TMCTile tmcTile = tmcTiles.get(position);
                String name = tmcTile.getName();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("menutype", "tileitemclick");
                bundle.putString("tmcsubctgykey", tmcTile.getTmcsubctgykey());
                bundle.putString("tmcsubctgyname", tmcTile.getTmcsubctgyname());
                bundle.putString("tmcctgyname", tmcTile.getTmcctgyname());
                bundle.putString("tilename", name);
                msg.setData(bundle);
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });

     /* if ((position+1) == tmcTiles.size()) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("menutype", "hideloadinganimation");
            msg.setData(bundle);
            if (handler != null) {
                handler.sendMessage(msg);
            }
        } */

    }

    @Override
    public int getItemCount() {
        return tmcTiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView tmctile_imageview;
        TMCTextView tmctile_name;
        View tmctile_progressbar;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tmctile_imageview = itemView.findViewById(R.id.tmctile_imageview);
            this.tmctile_name = itemView.findViewById(R.id.tmctile_name);
            this.tmctile_progressbar = itemView.findViewById(R.id.tmctile_progressbar);
        }

    }
}
