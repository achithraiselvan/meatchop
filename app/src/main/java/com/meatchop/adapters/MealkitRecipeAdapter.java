package com.meatchop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meatchop.R;
import com.meatchop.data.Mealkitingredient;
import com.meatchop.data.Mealkitrecipes;
import com.meatchop.widget.TMCTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MealkitRecipeAdapter extends RecyclerView.Adapter<MealkitRecipeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Mealkitrecipes> recipeList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TMCTextView stepno_textview;
        TMCTextView recipe_textview;
        TMCTextView recipe_title;
        ViewHolder(View view) {
            super(view);
            stepno_textview = (TMCTextView) view.findViewById(R.id.stepno_textview);
            recipe_textview = (TMCTextView) view.findViewById(R.id.recipe_textview);
            recipe_title = (TMCTextView) view.findViewById(R.id.recipe_title);
        }
    }

    public MealkitRecipeAdapter(Context context, ArrayList<Mealkitrecipes> recipes) {
        this.context = context;
        this.recipeList = new ArrayList<Mealkitrecipes>();
        this.recipeList.addAll(recipes);
        Collections.sort(this.recipeList);
        Log.d("MealkitrecipeAdapter", "recipeList "+recipeList.toString());
    }

    @NonNull
    @Override
    public MealkitRecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mealkitrecipe_layout, parent, false);
        return new MealkitRecipeAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Mealkitrecipes recipe = recipeList.get(position);
     // holder.stepno_textview.setText("STEP " + recipe.getDisplayno());
        holder.recipe_title.setText(recipe.getDisplayno() + ". " + recipe.getTitle());
        holder.recipe_textview.setText(recipe.getDesp());

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

}
