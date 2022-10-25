package com.meatchop.adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meatchop.R;
import com.meatchop.data.Mealkitingredient;
import com.meatchop.data.Mealkitingredients;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredRecyclerAdapter extends RecyclerView.Adapter<IngredRecyclerAdapter.ViewHolder> {

    public List<Mealkitingredients> ingredientsList = null;
    private Context context;
    private ArrayList<ViewHolder> holderList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TMCTextView ingredient_itemname;
        TMCTextView ingredient_qty;
        TMCTextView displayno_textview;
        ViewHolder(View view) {
            super(view);
            ingredient_itemname = (TMCTextView) view.findViewById(R.id.ingredient_itemname);
            ingredient_qty = (TMCTextView) view.findViewById(R.id.ingredient_qty);
            displayno_textview = (TMCTextView) view.findViewById(R.id.displayno_textview);
        }
    }

    public IngredRecyclerAdapter(Context context, List<Mealkitingredients> mealkitingredientsList) {
        this.context = context;
        this.ingredientsList = new ArrayList<Mealkitingredients>();
        this.ingredientsList.addAll(mealkitingredientsList);
        Collections.sort(this.ingredientsList);
        Log.d("IngredRecyclerAdapter", "ingredientslist "+ingredientsList);
    }

    @NonNull
    @Override
    public IngredRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredientsadapter_layout, parent, false);
        return new IngredRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Mealkitingredients mealkitingredients =  ingredientsList.get(position);
        Log.d("IngredRecyclerAdapter", "mealkitingredients name "+mealkitingredients.getItemname() +" displayno " +
                             mealkitingredients.getDisplayno());
        holder.ingredient_itemname.setText(mealkitingredients.getItemname());
        holder.displayno_textview.setText("" + mealkitingredients.getDisplayno() + ".");

        String rs = context.getResources().getString((R.string.Rs));
        holder.ingredient_qty.setText("(" + mealkitingredients.getNetweight() + ")");

        if (holderList == null) {
            holderList = new ArrayList<ViewHolder>();
        }
        holderList.add(position, holder);
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }


}
