package com.meatchop.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.meatchop.R;
import com.meatchop.adapters.TMCCtgyBottomUpAdapter;
import com.meatchop.data.TMCCtgy;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.widget.AnimatedExpandableListView;
import com.meatchop.widget.StaticMealKitPane;
import com.meatchop.widget.StaticMenuItemPane;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

public class MealKitActivity extends BaseActivity implements View.OnClickListener {

    private static final int ORDERSUMMARY_ACT_REQ_CODE = 0;
    private String TAG = "MealKitActivity";
    private View back_icon;
    private View changemenuctgy_layout;
    private TMCTextView selectedctgy_text;
    private View searchbtn_layout;
    private View cartbtn_layout;

    private String tmcctgyname;
    private String subctgyname;
    private TreeMap<String, TMCCtgy> tmcCtgyHashMap;
    private ArrayList<String> tmcCtgyNames;

    private AnimatedExpandableListView expandableListView;

    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private View menuctgymask_layout;
    private View menuctgy_layout;

    private TMCCtgyBottomUpAdapter tmcCtgyAdapter;
    private int expandablelistviewgroupposition = 0;

    private LinearLayout detailFields;
    private StaticMealKitPane menuItemPane;
    private ArrayList<TMCMenuItem> tmcMenuItems;

    private View cartitemcount_layout;
    private TMCTextView cartitemcount_text;

    private String menuitemkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mealkit);

        if (getPackageName().equals("com.meatchop")) {
            menuitemkey = getIntent().getStringExtra("menuitemkey");
            tmcctgyname = getIntent().getStringExtra("tmcctgyname");
            subctgyname = getIntent().getStringExtra("subctgyname");
            tmcCtgyHashMap = (TreeMap<String, TMCCtgy>) getIntent().getSerializableExtra("tmcctgyhashmap");
            tmcCtgyNames = (ArrayList<String>) getIntent().getSerializableExtra("tmcctgynames");
        }

        back_icon = findViewById(R.id.back_icon);
        changemenuctgy_layout = findViewById(R.id.changemenuctgy_layout);
        selectedctgy_text = (TMCTextView) findViewById(R.id.selectedctgy_text);
        menuctgy_layout = findViewById(R.id.menuctgy_layout);
        menuctgymask_layout = findViewById(R.id.menuctgymask_layout);
        searchbtn_layout = findViewById(R.id.searchbtn_layout);
        cartbtn_layout = findViewById(R.id.cartbtn_layout);
        expandableListView = (AnimatedExpandableListView) findViewById(R.id.expandableListView);
        menuctgymask_layout.setOnClickListener(this);
        back_icon.setOnClickListener(this);
        detailFields = findViewById(R.id.detail_fields_area);

        // setExpListViewsGroupIndicatorToRight();
        // expandableListView.setIndicatorBounds(350, 350);

        tmcCtgyAdapter = new TMCCtgyBottomUpAdapter(this, tmcctgyname, "",
                tmcCtgyNames, tmcCtgyHashMap);

        tmcCtgyAdapter.setHandler(createTMCCtgyBottomUpHandler());
        expandableListView.setAdapter(tmcCtgyAdapter);
        expandableListView.setGroupIndicator(null);


        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.collapseGroup(groupPosition);
                    tmcCtgyAdapter.setSelectedGroupPosition(-1);
                    tmcCtgyAdapter.notifyDataSetChanged();
                } else {
                    expandableListView.expandGroup(groupPosition, false);
                    // expandableListView.expandGroupWithAnimation(groupPosition);
                    tmcCtgyAdapter.setSelectedGroupPosition(groupPosition);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            tmcCtgyAdapter.notifyDataSetChanged();
                        }
                    }, 300);
                }



                return true;
            }
        });

        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout.setOnClickListener(this);

        back_icon.setOnClickListener(this);
        changemenuctgy_layout.setOnClickListener(this);
        searchbtn_layout.setOnClickListener(this);
        cartbtn_layout.setOnClickListener(this);

        cartitemcount_layout = findViewById(R.id.cartitemcount_layout);
        cartitemcount_text = (TMCTextView) findViewById(R.id.cartitemcount_text);

        int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
        if (cartsize > 0) {
            cartitemcount_layout.setVisibility(View.VISIBLE);
            cartitemcount_text.setText("" + cartsize);
        } else {
            cartitemcount_layout.setVisibility(View.GONE);
        }

     // preLoadTMCMenuItems();
        new MenuItemDetailsAsyncTask().execute();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.changemenuctgy_layout) {
            showMenuCtgyLayout();
        } else if (id == R.id.menuctgymask_layout) {
            hideMenuCtgyLayout();
        } else if (id == R.id.back_icon) {
            onBackPressed();
        } else if (id == R.id.cartbtn_layout) {
            int cartcount = TMCMenuItemCatalog.getInstance().getCartCount();
            if (cartcount > 0) {
                startOrderSummaryActivity();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (menuctgy_layout.getVisibility() == View.VISIBLE) {
            hideMenuCtgyLayout();
            return;
        }
        finish();
        slideToRight();
    }

    private void startOrderSummaryActivity() {
        Intent intent = new Intent(MealKitActivity.this, OrderSummaryActivity.class);
        startActivityForResult(intent, ORDERSUMMARY_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

    private class MenuItemDetailsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (menuItemPane != null) {
                menuItemPane.removeAllViews();
                detailFields.removeView(menuItemPane);
                menuItemPane = null;
            }
            menuItemPane = new StaticMealKitPane(getApplicationContext());
            for (int i=0; i<tmcMenuItems.size(); i++) {
                TMCMenuItem tmcMenuItem = tmcMenuItems.get(i);
                if (tmcMenuItem != null) {
                    menuItemPane.addFormItem(tmcMenuItem);
                }
            }
            menuItemPane.setHandler(createStaticMenuItemPaneHandler());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (menuItemPane != null) {
                if (menuItemPane.getParent() == null) {
                    detailFields.addView(menuItemPane);
                }
            }

        }
    }


 /* private void preLoadTMCMenuItems() {
        String url1 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickenskinless_menuitem.jpg?alt=media&token=07947ac4-737a-4df1-8e0b-c6f25c081e0d";
        String url2 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickendrumstick_menuitem.jpg?alt=media&token=8ee49528-503f-4246-b953-94999e4645bd";
        String url3 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickenmince_menuitem.jpg?alt=media&token=8b923a8c-5f24-462b-a7de-d7ab572561cb";
        String url4 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickenbreast_menuitem.jpg?alt=media&token=d2af0421-2e48-4ef0-806c-cb414b6e7eb4";
        String url5 = "https://firebasestorage.googleapis.com/v0/b/dosavillage-acc39.appspot.com/o/meatchop%2Fchickenlollipop_menuitem.jpg?alt=media&token=ddb89820-234f-4503-9bf2-bade4d98d799";
        tmcMenuItems = new ArrayList<TMCMenuItem>();
        TMCMenuItem tmcmenuItem1 = new TMCMenuItem();
        tmcmenuItem1.setKey("1");
        tmcmenuItem1.setItemname("Chettinad Chicken Gravy");
        tmcmenuItem1.setItemcalories(350);
        tmcmenuItem1.setBaseamount(185);
        tmcmenuItem1.setImageurl(url1);

        TMCMenuItem tmcmenuItem2 = new TMCMenuItem();
        tmcmenuItem2.setKey("2");
        tmcmenuItem2.setItemname("Kerala Fish Curry");
        tmcmenuItem2.setItemcalories(300);
        tmcmenuItem2.setBaseamount(225);
        tmcmenuItem2.setImageurl(url2);

        TMCMenuItem tmcmenuItem3 = new TMCMenuItem();
        tmcmenuItem3.setKey("3");
        tmcmenuItem3.setItemname("Nellai Mutton Curry");
        tmcmenuItem3.setItemcalories(400);
        tmcmenuItem3.setBaseamount(165);
        tmcmenuItem3.setImageurl(url3);

        TMCMenuItem tmcmenuItem4 = new TMCMenuItem();
        tmcmenuItem4.setKey("4");
        tmcmenuItem4.setItemname("Mutton Pepper Fry");
        tmcmenuItem4.setItemcalories(380);
        tmcmenuItem4.setBaseamount(225);
        tmcmenuItem4.setImageurl(url4);

        TMCMenuItem tmcmenuItem5 = new TMCMenuItem();
        tmcmenuItem5.setKey("5");
        tmcmenuItem5.setItemname("Chicken Biryani For 4");
        tmcmenuItem5.setBaseamount(285);
        tmcmenuItem5.setItemcalories(350);
        tmcmenuItem5.setImageurl(url5);
        tmcMenuItems.add(tmcmenuItem1); tmcMenuItems.add(tmcmenuItem2);
        tmcMenuItems.add(tmcmenuItem3); tmcMenuItems.add(tmcmenuItem4);
        tmcMenuItems.add(tmcmenuItem5);
        Collections.sort(tmcMenuItems);
    }
*/
    private void showMenuCtgyLayout() {
        if (tmcCtgyAdapter == null) {
            tmcCtgyAdapter = new TMCCtgyBottomUpAdapter(this, tmcctgyname, subctgyname,
                    tmcCtgyNames, tmcCtgyHashMap);
            tmcCtgyAdapter.setHandler(createTMCCtgyBottomUpHandler());
            expandableListView.setAdapter(tmcCtgyAdapter);
        } else {
            try {
                expandablelistviewgroupposition = tmcCtgyNames.indexOf(tmcctgyname);
             // Log.d(TAG, "showMenuCtgyLayout position "+expandablelistviewgroupposition+" tmcctgyname "+tmcctgyname);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            expandableListView.expandGroup(expandablelistviewgroupposition, true);
            tmcCtgyAdapter.notifyDataSetChanged();
        }
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                menuctgymask_layout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(300);
        menuctgy_layout.startAnimation(bottomUp);
        menuctgy_layout.setVisibility(View.VISIBLE);
    }

    private void hideMenuCtgyLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                menuctgy_layout.setVisibility(View.GONE);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(200);
        menuctgy_layout.startAnimation(bottomDown);
        menuctgymask_layout.setVisibility(View.GONE);
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

    private Handler createTMCCtgyBottomUpHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("itemclick")) {
                    subctgyname = bundle.getString("subctgyname");
                    tmcctgyname = bundle.getString("tmcctgyname");
                    selectedctgy_text.setText(subctgyname);
                    expandableListView.collapseGroup(expandablelistviewgroupposition);
                    hideMenuCtgyLayout();
                }

                return false;
            }
        };
        return new Handler(callback);
    }

    private Handler createStaticMenuItemPaneHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("cartupdated")) {
                    int cartsize = TMCMenuItemCatalog.getInstance().getCartCount();
                    if (cartsize > 0) {
                        cartitemcount_layout.setVisibility(View.VISIBLE);
                        cartitemcount_text.setText("" + cartsize);
                    } else {
                        cartitemcount_layout.setVisibility(View.GONE);
                    }
                } else if (menutype.equalsIgnoreCase("menuitemclicked")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    startMealingredientsActivity();
                }

                return false;
            }
        };
        return new Handler(callback);
    }

    private void startMealingredientsActivity() {
        Intent intent = new Intent(MealKitActivity.this, MealingredientsActivity.class);
        startActivity(intent);
    }

    // Convert pixel to dip
    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 100.5f);
    }

    private void setExpListViewsGroupIndicatorToRight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        Log.d(TAG, "width "+width+" GetDipsFromPixel(35) "+GetDipsFromPixel(35) + " GetDipsFromPixel(5) "+GetDipsFromPixel(5));
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBounds(width - GetDipsFromPixel(35), width - GetDipsFromPixel(5));
        } else {
            expandableListView.setIndicatorBoundsRelative(width - GetDipsFromPixel(35), width - GetDipsFromPixel(5));
        }
    }


}
