package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.adapters.IngredRecyclerAdapter;
import com.meatchop.adapters.MealkitRecipeAdapter;
import com.meatchop.data.Mealkitingredient;
import com.meatchop.data.Mealkitingredients;
import com.meatchop.data.Mealkitrecipes;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCVendor;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MealingredientsActivity extends AppCompatActivity {

    private String TAG = "Mealkitingredactivity";
    private RelativeLayout mealkit_relativelayout;
    private ImageView headerimageview;
    private View back_button;

    private BottomSheetBehavior bottomSheetBehavior;
    private TMCTextView mealkit_title;
    private TMCTextView portionsize_textview;
    private TMCTextView calories_textview;
    private TMCTextView cookingtime_textview;
    private TMCTextView mealkit_desp;

    private LinearLayout ingredients_fields_area;

    private RecyclerView ingred_recyclerview;
    private IngredRecyclerAdapter ingredRecyclerAdapter;
    private RecyclerView kitcheningred_recyclerview;
    private IngredRecyclerAdapter kitchenIngredRecyclerAdapter;

    private RecyclerView recipe_recyclerview;
    private MealkitRecipeAdapter mealkitRecipeAdapter;


    private TMCMenuItem tmcMenuItem;
    private String menuitemkey;
    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private TMCTextView payableamount_textview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mealingredients);

        getSupportActionBar().hide();

        if (getPackageName().equals("com.meatchop")) {
            menuitemkey = getIntent().getStringExtra("menuitemkey");
        }
        tmcMenuItem = TMCMenuItemCatalog.getInstance().getTMCMenuItem(menuitemkey);

        headerimageview = (ImageView) findViewById(R.id.headerimageview);
        ingred_recyclerview = (RecyclerView) findViewById(R.id.ingred_recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ingred_recyclerview.setLayoutManager(mLayoutManager);
        ingred_recyclerview.setItemAnimator(new DefaultItemAnimator());

        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);

        recipe_recyclerview = (RecyclerView) findViewById(R.id.recipe_recyclerview);
        LinearLayoutManager rLayoutManager = new LinearLayoutManager(getApplicationContext());
        recipe_recyclerview.setLayoutManager(rLayoutManager);
        recipe_recyclerview.setItemAnimator(new DefaultItemAnimator());

        kitcheningred_recyclerview = (RecyclerView) findViewById(R.id.kitcheningred_recyclerview);
        LinearLayoutManager kLayoutManager = new LinearLayoutManager(getApplicationContext());
        kitcheningred_recyclerview.setLayoutManager(kLayoutManager);
        kitcheningred_recyclerview.setItemAnimator(new DefaultItemAnimator());

        payableamount_textview = (TMCTextView) findViewById(R.id.payableamount_textview);
        String rs = getResources().getString(R.string.Rs);
        payableamount_textview.setText("Item total: " + rs + tmcMenuItem.getTmcprice());

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String url = tmcMenuItem.getImageurl();
        Glide.with(getApplicationContext())
                .load(url)
                .fitCenter()
                .into(headerimageview);

        mealkit_title = (TMCTextView) findViewById(R.id.mealkit_title);
        portionsize_textview = (TMCTextView) findViewById(R.id.portionsize_textview);
        cookingtime_textview = (TMCTextView) findViewById(R.id.cookingtime_textview);
        mealkit_desp = (TMCTextView) findViewById(R.id.mealkit_desp);

        mealkit_title.setText(tmcMenuItem.getItemname());
        cookingtime_textview.setText(tmcMenuItem.getPreparationtime());
        portionsize_textview.setText(tmcMenuItem.getPortionsize());

        View additem_btn = findViewById(R.id.additem_btn);
        additem_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             // tmcMenuItem.setMealkitingredients(selectedIngredList);
                int oldquantity = tmcMenuItem.getSelectedqty();
                tmcMenuItem.setSelectedqty(oldquantity + 1);
                TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
                Intent intent = new Intent();
                intent.putExtra("additemclicked", true);
                intent.putExtra("itemcount", tmcMenuItem.getSelectedqty());
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_to_bottom);
            }
        });

        getMealkitIngredFromAWS();
    }

    private void setMapPaddingBotttom(Float offset,Float maxMapPaddingBottom ) {
        mealkit_relativelayout.setPadding(0, 0, 0, Math.round((offset * maxMapPaddingBottom)));
    }

    private void showLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.VISIBLE);
                loadinganim_layout.setVisibility(View.VISIBLE);
            }
        });

    }

    private void hideLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.GONE);
                loadinganim_layout.setVisibility(View.GONE);
            }
        });
    }

    private void getMealkitIngredFromAWS() {
        showLoadingAnim();

        String url = TMCRestClient.AWS_GETMEALKITINGRED + "?menuid=" + menuitemkey;

     // String url = "https://n2o245t1b9.execute-api.ap-south-1.amazonaws.com/prod/get" + "?tmcmenuitemkey=" + menuitemkey;
        Log.d(TAG, "getMealkitIngredFromAWS url "+url);
     // menuitemkey = menuitemkey.replace("?", "");
     // menuitemkey = menuitemkey.replace("?", "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                     // Log.d(TAG, "getMealkitIngredFromAWS response "+response.toString());
                        try {
                            String message = response.getString("message");
                         // Log.d(TAG, "getMealkitIngredFromAWS response "+response);
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                ArrayList<Mealkitingredients> ingredientsList = new ArrayList<Mealkitingredients>();
                                ArrayList<Mealkitingredients> kitcheningredientsList = new ArrayList<Mealkitingredients>();
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String tmcmenuitemkey = jsonObject1.getString("tmcmenuitemkey");

                                 /* if (menuitemkey.contains(tmcmenuitemkey)) {

                                    } */
                                    Mealkitingredients mealkitingredients = new Mealkitingredients(jsonObject1);
                                    if (mealkitingredients.getFromyourkitchen()) {
                                        kitcheningredientsList.add(mealkitingredients);
                                    } else {
                                        ingredientsList.add(mealkitingredients);
                                    }
                                }

                                ingredRecyclerAdapter = new IngredRecyclerAdapter(MealingredientsActivity.this, ingredientsList);
                                ingred_recyclerview.setAdapter(ingredRecyclerAdapter);

                                kitchenIngredRecyclerAdapter = new IngredRecyclerAdapter
                                                                          (MealingredientsActivity.this, kitcheningredientsList);
                                kitcheningred_recyclerview.setAdapter(kitchenIngredRecyclerAdapter);
                                getMealkitRecipesFromAWS();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
             // params.put("menuid", menuitemkey);
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                return header;
            }
        };
// Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getMealkitRecipesFromAWS() {
     // String url = "https://5ovrs5v1d3.execute-api.ap-south-1.amazonaws.com/prod/get";
        String url = TMCRestClient.AWS_GETMEALKITRECIPES + "?menuid=" + menuitemkey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                ArrayList<Mealkitrecipes> recipeList = new ArrayList<Mealkitrecipes>();
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String tmcmenuitemkey = jsonObject1.getString("tmcmenuitemkey");
                                 /* if (menuitemkey.contains(tmcmenuitemkey)) {
                                        Mealkitrecipes recipes = new Mealkitrecipes(jsonObject1);
                                        recipeList.add(recipes);
                                    } */
                                    Mealkitrecipes recipes = new Mealkitrecipes(jsonObject1);
                                    recipeList.add(recipes);
                                }
                                mealkitRecipeAdapter = new MealkitRecipeAdapter(MealingredientsActivity.this, recipeList);
                                recipe_recyclerview.setAdapter(mealkitRecipeAdapter);
                                hideLoadingAnim();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


 /* private ArrayList<Mealkitingredient> vegetableIngredientList;
    private ArrayList<Mealkitingredient> groceryIngredientList;
    private ArrayList<Mealkitingredient> ingredientList;
    private void preLoadMealKitIngredients() {
        vegetableIngredientList = new ArrayList<Mealkitingredient>();
        groceryIngredientList = new ArrayList<Mealkitingredient>();
        ingredientList = new ArrayList<Mealkitingredient>();

        Mealkitingredient mealkitingredient1 = new Mealkitingredient();
        mealkitingredient1.setItemname("Cut Onion");
        mealkitingredient1.setIngredientctgy("Vegetables");
        mealkitingredient1.setTotalamount(10);
        mealkitingredient1.setAvailableqty("300 Gms");
        ingredientList.add(mealkitingredient1);

        Mealkitingredient mealkitingredient2 = new Mealkitingredient();
        mealkitingredient2.setItemname("Cut Tomato");
        mealkitingredient2.setIngredientctgy("Vegetables");
        mealkitingredient2.setTotalamount(15);
        mealkitingredient2.setAvailableqty("300 Gms");
        ingredientList.add(mealkitingredient2);

        Mealkitingredient mealkitingredient3 = new Mealkitingredient();
        mealkitingredient3.setItemname("Curry Leaves");
        mealkitingredient3.setIngredientctgy("Vegetables");
        mealkitingredient3.setTotalamount(5);
        mealkitingredient3.setAvailableqty("50 Gms");
        ingredientList.add(mealkitingredient3);

        Mealkitingredient mealkitingredient4 = new Mealkitingredient();
        mealkitingredient4.setItemname("Coriander Leaves");
        mealkitingredient4.setIngredientctgy("Vegetables");
        mealkitingredient4.setTotalamount(5);
        mealkitingredient4.setAvailableqty("50 Gms");
        ingredientList.add(mealkitingredient4);

        Mealkitingredient mealkitingredient10 = new Mealkitingredient();
        mealkitingredient10.setItemname("Fresh Graded Coconut");
        mealkitingredient10.setIngredientctgy("Vegetables");
        mealkitingredient10.setTotalamount(20);
        mealkitingredient10.setAvailableqty("250 Gms");
        ingredientList.add(mealkitingredient10);

        Mealkitingredient mealkitingredient9 = new Mealkitingredient();
        mealkitingredient9.setItemname("Lemon Juice");
        mealkitingredient9.setIngredientctgy("Vegetables");
        mealkitingredient9.setTotalamount(10);
        mealkitingredient9.setAvailableqty("50 Gms");
        ingredientList.add(mealkitingredient9);

        Mealkitingredient mealkitingredient5 = new Mealkitingredient();
        mealkitingredient5.setItemname("Ginger Garlic Paste");
        mealkitingredient5.setIngredientctgy("Grocery Products");
        mealkitingredient5.setTotalamount(10);
        mealkitingredient5.setAvailableqty("50 Gms");
        ingredientList.add(mealkitingredient5);

        Mealkitingredient mealkitingredient6 = new Mealkitingredient();
        mealkitingredient6.setItemname("Oil");
        mealkitingredient6.setIngredientctgy("Grocery Products");
        mealkitingredient6.setTotalamount(23);
        mealkitingredient6.setAvailableqty("100 ml");
        ingredientList.add(mealkitingredient6);

        Mealkitingredient mealkitingredient7 = new Mealkitingredient();
        mealkitingredient7.setItemname("Turmeric Powder");
        mealkitingredient7.setIngredientctgy("Grocery Products");
        mealkitingredient7.setTotalamount(10);
        mealkitingredient7.setAvailableqty("50 Gms");
        ingredientList.add(mealkitingredient7);

        Mealkitingredient mealkitingredient8 = new Mealkitingredient();
        mealkitingredient8.setItemname("Salt");
        mealkitingredient8.setIngredientctgy("Grocery Products");
        mealkitingredient8.setTotalamount(5);
        mealkitingredient8.setAvailableqty("100 Gms");
        ingredientList.add(mealkitingredient8);

        Mealkitingredient mealkitingredient11 = new Mealkitingredient();
        mealkitingredient11.setItemname("Dry Red Chilli");
        mealkitingredient11.setIngredientctgy("Grocery Products");
        mealkitingredient11.setTotalamount(10);
        mealkitingredient11.setAvailableqty("100 Gms");
        ingredientList.add(mealkitingredient11);
     // new IngredientDetailsAsyncTask().execute();



        try {
            ArrayList recipeList = new ArrayList<String>();
            recipeList.add(0, "Add salt, turmeric powder and lemon juice to chicken and mix well. Let it marinate for 15 to 20 mins.");
            recipeList.add(1, "Dry roast all the spices except coconut in a kadai till it turns golden. once it turn golden add in coconut and roast till the coconut turns light golden. Remove them to a mixer and grind them to a smooth paste.");
            recipeList.add(2, "Heat oil in a pressure cooker, add in onions and saute till it turns soft. Add in curry leaves and ginger garlic paste. Saute for a min.");
            recipeList.add(3, "Add in marinated chicken and mix well. Add in cubed tomatoes and mix well. Cover the cooker and cook it for 2 whistle, simmer for 10 mins.Switch off the heat and let them steam go all by itself.");
            recipeList.add(4, "Open the cooker and pour the chicken in a kadai. Add more water if needed. Add the ground masala and mix well.Cover and simmer for 10 mins. Add in coriander leaves and serve.");
            mealkitRecipeAdapter = new MealkitRecipeAdapter(MealingredientsActivity.this, recipeList);
            recipe_recyclerview.setAdapter(mealkitRecipeAdapter);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/


}

