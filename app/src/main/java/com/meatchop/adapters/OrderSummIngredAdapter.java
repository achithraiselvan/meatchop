package com.meatchop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meatchop.R;
import com.meatchop.data.Mealkitingredient;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderSummIngredAdapter extends RecyclerView.Adapter<OrderSummIngredAdapter.ViewHolder> {

    public List<Mealkitingredient> ingredientsList = null;
    private Context context;
    private ArrayList<ViewHolder> holderList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TMCTextView ingredient_itemname;
        View checkbox_layout;
        ImageView checkboxempty_view;
        ImageView checkboxselected_view;
        TMCTextView ingredient_qty;
        TMCTextView ingredient_price;
        View mealingredientitem_layout;

        View selectall_layout;
        View checkboxall_layout;
        ImageView checkboxemptyall_view;
        ImageView checkboxselectedall_view;
        ViewHolder(View view) {
            super(view);
            ingredient_itemname = (TMCTextView) view.findViewById(R.id.ingredient_itemname);
            checkbox_layout = view.findViewById(R.id.checkbox_layout);
            checkboxempty_view = (ImageView) view.findViewById(R.id.checkboxempty_view);
            checkboxselected_view = (ImageView) view.findViewById(R.id.checkboxselected_view);
            ingredient_qty = (TMCTextView) view.findViewById(R.id.ingredient_qty);
            ingredient_price = (TMCTextView) view.findViewById(R.id.ingredient_price);
            mealingredientitem_layout = view.findViewById(R.id.mealingredientitem_layout);
            selectall_layout = view.findViewById(R.id.selectall_layout);
            checkboxall_layout = view.findViewById(R.id.checkboxall_layout);
            checkboxemptyall_view = view.findViewById(R.id.checkboxemptyall_view);
            checkboxselectedall_view = view.findViewById(R.id.checkboxselectedall_view);
        }

    }

    public OrderSummIngredAdapter(Context context, List<Mealkitingredient> mealkitingredientsList) {
        this.context = context;
        this.ingredientsList = new ArrayList<Mealkitingredient>();
        this.ingredientsList.addAll(mealkitingredientsList);
    }

    @NonNull
    @Override
    public OrderSummIngredAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_ordersummingred_layout, parent, false);
        return new OrderSummIngredAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Mealkitingredient mealkitingredients =  ingredientsList.get(position);
        holder.ingredient_itemname.setText(mealkitingredients.getItemname());
        //  Log.d("TMCIngredientsAdapter", "ingredient ctgy "+mealkitingredients.getIngredientctgy() +
        //                                          "ingredient itemname "+mealkitingredients.getItemname());

        String rs = context.getResources().getString((R.string.Rs));
        holder.ingredient_qty.setText("(" + mealkitingredients.getAvailableqty() + ")");
        holder.ingredient_price.setText(rs + mealkitingredients.getTotalamount());
        if (mealkitingredients.isSelected()) {
            holder.checkboxempty_view.setVisibility(View.GONE);
            holder.checkboxselected_view.setVisibility(View.VISIBLE);
        } else {
            holder.checkboxempty_view.setVisibility(View.VISIBLE);
            holder.checkboxselected_view.setVisibility(View.GONE);
        }

        holder.mealingredientitem_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkboxempty_view.getVisibility() == View.VISIBLE) {
                    holder.checkboxempty_view.setVisibility(View.GONE);
                    holder.checkboxselected_view.setVisibility(View.VISIBLE);
                    mealkitingredients.setIsselected(true);
                } else {
                    holder.checkboxempty_view.setVisibility(View.VISIBLE);
                    holder.checkboxselected_view.setVisibility(View.GONE);
                    mealkitingredients.setIsselected(false);
                }
                // Log.d("TMCIngredAdapter", "holder itemname "+holder.ingredient_itemname.getText().toString());
                return;
            }
        });

        if (holderList == null) {
            holderList = new ArrayList<ViewHolder>();
        }
        holderList.add(position, holder);

        if (position == 0) {
            holder.selectall_layout.setVisibility(View.VISIBLE);
            holder.checkboxall_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkboxemptyall_view.getVisibility() == View.VISIBLE) {
                        selectAllItems();
                        holder.checkboxselectedall_view.setVisibility(View.VISIBLE);
                        holder.checkboxemptyall_view.setVisibility(View.GONE);
                    } else {
                        unselectAllItems();
                        holder.checkboxselectedall_view.setVisibility(View.GONE);
                        holder.checkboxemptyall_view.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            holder.selectall_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public void selectAllItems() {
        if ((holderList == null) || (holderList.size() == 0)) {
            return;
        }
        for (int i=0; i<holderList.size(); i++) {
            ViewHolder holder = holderList.get(i);
            Mealkitingredient mealkitingredients =  ingredientsList.get(i);
            holder.checkboxempty_view.setVisibility(View.GONE);
            holder.checkboxselected_view.setVisibility(View.VISIBLE);
            mealkitingredients.setIsselected(false);
        }
    }

    public void unselectAllItems() {
        if ((holderList == null) || (holderList.size() == 0)) {
            return;
        }
        for (int i=0; i<holderList.size(); i++) {
            ViewHolder holder = holderList.get(i);
            Mealkitingredient mealkitingredients =  ingredientsList.get(i);
            holder.checkboxempty_view.setVisibility(View.VISIBLE);
            holder.checkboxselected_view.setVisibility(View.GONE);
            mealkitingredients.setIsselected(true);
        }
    }
}
