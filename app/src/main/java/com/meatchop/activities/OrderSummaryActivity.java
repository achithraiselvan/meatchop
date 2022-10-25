package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import io.sentry.util.StringUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonObject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.IngredRecyclerAdapter;
import com.meatchop.adapters.OrderSummIngredAdapter;
import com.meatchop.adapters.ShoppingBagAdapter;
import com.meatchop.data.Coupondetails;
import com.meatchop.data.Coupontransactions;
import com.meatchop.data.Deliveryslabdetails;
import com.meatchop.data.Deliveryslotdetails;
import com.meatchop.data.Deliveryslots;
import com.meatchop.data.Mealkitingredient;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCTile;
import com.meatchop.data.TMCUser;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.widget.StaticSlotSelectionPane;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCEditText;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrderSummaryActivity extends BaseActivity {

    public static final int DELIVERYTYPE_HOMEDELIVERY = 0;
    public static final int DELIVERYTYPE_STOREPICKUP = 1;

    private final String TAG = "OrderSummaryActivity";

    private View back_icon;
    private RecyclerView shoppingbag_recyclerview;
    private ShoppingBagAdapter shoppingBagAdapter;
    private TMCTextView itemtotalvalue_textview;
    private TMCTextView applycoupon_textview;
    private TMCTextView couponvalue_textview;
    private TMCTextView deliveryfee_textview;
    private TMCTextView deliverypartnerfee_desp;
    private TMCTextView taxandcharges_textview;
    private TMCTextView topay_textview;
    private TMCTextView payableamount_textview;
    private View itemdiscount_layout;
    private TMCTextView itemdiscountvalue_textview;
    private View viewdetails_layout;
    private ScrollView scrollview_layout;
    private View itemqty_layout;
    private View itemqtymask_layout;
    private View selectqtydone_btn;

    private int clickedpositionforqtypopup;
    private int selectedquantity = -1;

    private RecyclerView ingred_recyclerview;
    private ImageView checkboxemptyall_view;
    private ImageView checkboxselectedall_view;
    private View ingredients_layout;
    private TMCTextView ingredients_title;
    private View ingredientmask_layout;
    private OrderSummIngredAdapter orderSummIngredAdapter;
    private View checkboxall_layout;
    private View customizelayoutdone_btn;

    private View deliverymodedetails_layout;
    private TMCTextView deliverymode_text;
    private TMCTextView slotdesp_textview;
    private View deliveryaddressdetails_layout;
    private TMCTextView deliveryaddressdetails_textview;
    private View storeaddressdetails_layout;
    private TMCTextView storeaddressdetails_textview;
    private int deliverytype = DELIVERYTYPE_HOMEDELIVERY;

    private View deliverytypemask_layout;
    private View deliverytypedropdown_layout;
    private View homedeliverydropdown_layout;
    private View storepickupdropdown_layout;
    private View homedeliverycheckmark_view;
    private View storepickupcheckmark_view;
    private View slotdetails_layout;

    private static final int SLOTSELECTION_ACT_REQ_CODE = 0;
    private static final int PAYMENTMODE_ACT_REQ_CODE = 1;
    private static final int MARINADE_ACT_REQ_CODE = 2;
    private static final int COUPONS_ACT_REQ_CODE = 3;
    private static final int ORDERCONFIRMATION_ACT_REQ_CODE = 4;
    private static final int MENUITEMCUSTOM_ACT_REQ_CODE = 5;
    private static final int NEWORDER_ACT_REQ_CODE = 6  ;
    private static final int DETAILORDER_ACT_REQ_CODE = 7;

    private SettingsUtil settingsUtil;
    private String vendorkey;

    private View loadinganimmask_layout;
    private View loadinganim_layout;

    private Deliveryslots defaultDeliverySlotDetails;
    private Deliveryslots selectedDeliverySlotDetails;
    private String selectedsessiontype;
    private ArrayList<Deliveryslots> deliveryslotdetailsArrayList;
    private Orderdetails orderdetails;

    private double deliveryamount = 0;
    private double totaltaxamount;
    private double totalpayableamount;
    private boolean quantitychanged = false;
    private TMCUserAddress tmcUserAddress;
    private TMCVendor defaultVendor;

    private HashMap<String, Boolean> itemAvailabilityMap;  // {Menuitemkey -- Itamavailability}
    private boolean isSlotActive = false;
    private TMCTextView slotavailability_textview;
    private TMCTextView savedamount_textview;
    private double totalsavedamount = 0;

    private View applycoupon_layout;
    private View couponapplied_layout;
    private TMCTextView coupondetails_textview;
    private TMCTextView couponamounttext_textview;
    private View couponamountdetails_layout;
    private View removecoupon_button;
    private double couponamount = 0;
    private String couponkey;
    private String coupontype;
    private String couponamounttext;
    private boolean couponalreadyapplied = false;
    private double minimumorderfordelivery = 0;
    private boolean isMinimumOrderValueCrossed = false;
    private boolean isMeatProductsAvailable = false;
    private boolean isSlotSelected = false;
    private String slotdesp;

    private TMCEditText notes_edittext;
    private View deliveryslotdetails_layout;
    private View deliveryslotsloadinganim_layout;
    private View pricedetails_layout;
    private View pricedetailsloadinganim_layout;
    private double userdeliverydistance = 0;
    private Deliveryslabdetails mappeddeliveryslabdetails;
    private View deliverychargedetaileddesp_textview;

    private View orderconfirm_btn;
    private View orderconfirmmask_layout;
    private View orderconfirm_layout;
    private View orderconfirmclose_btn;

    private Coupondetails appliedCouponDetails;

    private boolean isOrderConfirmationActivityCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_summary);

        settingsUtil = new SettingsUtil(this);
        try {
            tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
            userdeliverydistance = tmcUserAddress.getDeliverydistance();
            defaultVendor = new TMCVendor(new JSONObject(settingsUtil.getDefaultVendor()));
            vendorkey = tmcUserAddress.getVendorkey();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        orderconfirm_btn = findViewById(R.id.orderconfirm_btn);
        orderconfirmmask_layout = findViewById(R.id.orderconfirmmask_layout);
        orderconfirm_layout = findViewById(R.id.orderconfirm_layout);
        orderconfirmclose_btn = findViewById(R.id.orderconfirmclose_btn);

        isSlotSelected = false;
        if ((vendorkey == null) || (TextUtils.isEmpty(vendorkey))) {
            vendorkey = settingsUtil.getDefaultVendorkey();
        }

        back_icon = findViewById(R.id.back_icon);
        shoppingbag_recyclerview = (RecyclerView) findViewById(R.id.shoppingbag_recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        shoppingbag_recyclerview.setLayoutManager(mLayoutManager);
        shoppingbag_recyclerview.setItemAnimator(new DefaultItemAnimator());

        notes_edittext = (TMCEditText) findViewById(R.id.notes_edittext);

        viewdetails_layout = findViewById(R.id.viewdetails_layout);
        scrollview_layout = (ScrollView) findViewById(R.id.scrollview_layout);

        ArrayList<TMCMenuItem> tmcMenuItemList = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
        checkForDuplicateOrders(tmcMenuItemList);
        itemAvailabilityMap = new HashMap<String, Boolean>();
        int tmcMenuItemListSize = (tmcMenuItemList != null) ? tmcMenuItemList.size() : 0;
        if (settingsUtil == null) {
            settingsUtil = new SettingsUtil(this);
        }
        for (int i = 0; i < tmcMenuItemListSize; i++) {
            TMCMenuItem tmcMenuItem = tmcMenuItemList.get(i);
            itemAvailabilityMap.put(tmcMenuItem.getAwsKey(),
                    new Boolean(tmcMenuItem.isItemavailability(settingsUtil.isInventoryCheck())));
        }

        if ((tmcMenuItemList != null) && (tmcMenuItemList.size() > 0)) {
            shoppingBagAdapter = new ShoppingBagAdapter(OrderSummaryActivity.this, tmcMenuItemList);
            shoppingbag_recyclerview.setAdapter(shoppingBagAdapter);
            shoppingBagAdapter.setHandler(createHandlerForShoppingBagAdapter());
        } else {
            showCartEmptyAlert();
        }

        itemtotalvalue_textview = (TMCTextView) findViewById(R.id.itemtotalvalue_textview);
        itemdiscount_layout = findViewById(R.id.itemdiscount_layout);
        itemdiscountvalue_textview = (TMCTextView) findViewById(R.id.itemdiscountvalue_textview);
        couponvalue_textview = (TMCTextView) findViewById(R.id.couponvalue_textview);
        deliveryfee_textview = (TMCTextView) findViewById(R.id.deliveryfee_textview);
        deliverypartnerfee_desp = (TMCTextView) findViewById(R.id.deliverypartnerfee_desp);
        taxandcharges_textview = (TMCTextView) findViewById(R.id.taxandcharges_textview);
        topay_textview = (TMCTextView) findViewById(R.id.topay_textview);
        payableamount_textview = (TMCTextView) findViewById(R.id.payableamount_textview);
        itemqty_layout = findViewById(R.id.itemqty_layout);
        itemqtymask_layout = findViewById(R.id.itemqtymask_layout);
        selectqtydone_btn = findViewById(R.id.selectqtydone_btn);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        deliverychargedetaileddesp_textview = findViewById(R.id.deliverychargedetaileddesp_textview);

        pricedetails_layout = findViewById(R.id.pricedetails_layout);
        pricedetailsloadinganim_layout = findViewById(R.id.pricedetailsloadinganim_layout);

        slotavailability_textview = (TMCTextView) findViewById(R.id.slotavailability_textview);
        savedamount_textview = (TMCTextView) findViewById(R.id.savedamount_textview);

        couponamountdetails_layout = findViewById(R.id.couponamountdetails_layout);
        applycoupon_layout = findViewById(R.id.applycoupon_layout);
        couponapplied_layout = findViewById(R.id.couponapplied_layout);
        coupondetails_textview = (TMCTextView) findViewById(R.id.coupondetails_textview);
        removecoupon_button = findViewById(R.id.removecoupon_button);
        couponamounttext_textview = (TMCTextView) findViewById(R.id.couponamounttext_textview);

        deliveryslotdetails_layout = findViewById(R.id.deliveryslotdetails_layout);
        deliveryslotsloadinganim_layout = findViewById(R.id.deliveryslotsloadinganim_layout);

        initQuantityLayout();
        initIngredientLayout();

        if ((vendorkey != null) && (vendorkey.equalsIgnoreCase("vendor_2"))) { // Test Code
            // Do nothing
        } else {
            minimumorderfordelivery = new Double(settingsUtil.getMinimumOrderForDelivery());
        }

        removecoupon_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponalreadyapplied = false;
                showApplyCouponLayout();
                couponamount = 0;
                fillItemTotalValues();
            }
        });

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        viewdetails_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollview_layout.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview_layout.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });

        itemqtymask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideQuantityLayout();
            }
        });

        selectqtydone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedquantity > 0) {
                    if (shoppingBagAdapter != null) {
                        shoppingBagAdapter.updateItemCount(clickedpositionforqtypopup, selectedquantity);
                    }
                    fillItemTotalValues();
                    if ((couponamount > 0) && (appliedCouponDetails != null)) {
                        computeCouponDiscountAmount(appliedCouponDetails, false);
                    }
                    hideQuantityLayout();
                    quantitychanged = true;
                } else if (selectedquantity == 0) {
                    showItemRemovalTMCAlert();
                } else {
                    hideQuantityLayout();
                }

            }
        });

        View proceedtopay_btn = findViewById(R.id.proceedtopay_btn);
        View placeorderandpay_btn = findViewById(R.id.placeorderandpay_btn);


     /* if (settingsUtil.isPlaceOrderAndPayFeatureEnabled()) {
            placeorderandpay_btn.setVisibility(View.VISIBLE);
            proceedtopay_btn.setVisibility(View.GONE);
        } else {
            placeorderandpay_btn.setVisibility(View.GONE);
            proceedtopay_btn.setVisibility(View.VISIBLE);
        }
        proceedtopay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deliverytype == DELIVERYTYPE_HOMEDELIVERY) {
                    if (!isSlotSelected) {
                        showSlotNotSelectedAlert();
                        return;
                    }

                    if (!isMinimumOrderValueCrossed) {
                        showMinOrderValueAlert();
                        return;
                    }

                    if (!isMeatProductsAvailable) {
                        showMeatNotAvailableAlert();
                        return;
                    }
                }

                startPaymentModeActivity();
            }
        }); */

        isOrderConfirmationActivityCalled = false;
        placeorderandpay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSlotSelected) {
                    showSlotNotSelectedAlert();
                    return;
                }
                if (!isMinimumOrderValueCrossed) {
                    showMinOrderValueAlert();
                    return;
                }

                if (!isMeatProductsAvailable) {
                    showMeatNotAvailableAlert();
                    return;
                }
                // showConfirmPlaceOrderAlert();
                showOrderConfirmLayout();
            }
        });

        orderconfirm_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        orderconfirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userstatus = settingsUtil.getUserStatus();
                if ((userstatus != null) && (!TextUtils.isEmpty(userstatus))) {
                    if (userstatus.equalsIgnoreCase("BLOCKED")) {
                        String message = "Server encountered an issue. Please contact support!";
                        String title = "Alert";
                        showTMCAlert(title, message);
                        return;
                    }
                }

                if (settingsUtil.isOrderdetailsNewSchema()) {
                    startNewOrderActivity();
                } else {
                    startOrderConfirmationActivity();
                }

            }
        });

        orderconfirmmask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrderConfirmLayout();
            }
        });

        orderconfirmclose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrderConfirmLayout();
            }
        });

        storeaddressdetails_textview = findViewById(R.id.storeaddressdetails_textview);
        deliveryaddressdetails_textview = findViewById(R.id.deliveryaddressdetails_textview);

        try {
            if (defaultVendor != null) {
                storeaddressdetails_textview.setText(defaultVendor.getAddressline1() + ", " + defaultVendor.getAddressline2());
            }
            if (tmcUserAddress != null) {
                String address = tmcUserAddress.getAddressline1() + ", " + tmcUserAddress.getAddressline2();
                deliveryaddressdetails_textview.setText(address);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        deliverymodedetails_layout = findViewById(R.id.deliverymodedetails_layout);
        deliverymode_text = (TMCTextView) findViewById(R.id.deliverymode_text);
        slotdesp_textview = (TMCTextView) findViewById(R.id.slotdesp_textview);
        deliveryaddressdetails_layout = findViewById(R.id.deliveryaddressdetails_layout);
        storeaddressdetails_layout = findViewById(R.id.storeaddressdetails_layout);
        deliverytypemask_layout = findViewById(R.id.deliverytypemask_layout);
        deliverytypedropdown_layout = findViewById(R.id.deliverytypedropdown_layout);
        homedeliverydropdown_layout = findViewById(R.id.homedeliverydropdown_layout);
        storepickupdropdown_layout = findViewById(R.id.storepickupdropdown_layout);
        homedeliverycheckmark_view = findViewById(R.id.homedeliverycheckmark_view);
        storepickupcheckmark_view = findViewById(R.id.storepickupcheckmark_view);
        slotdetails_layout = findViewById(R.id.slotdetails_layout);
        deliverymodedetails_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeliveryTypeLayout();
                if (deliverytype == DELIVERYTYPE_HOMEDELIVERY) {
                    homedeliverycheckmark_view.setVisibility(View.VISIBLE);
                    storepickupcheckmark_view.setVisibility(View.GONE);
                } else if (deliverytype == DELIVERYTYPE_STOREPICKUP) {
                    homedeliverycheckmark_view.setVisibility(View.GONE);
                    storepickupcheckmark_view.setVisibility(View.VISIBLE);
                }
            }
        });
        deliverytypemask_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDeliveryTypeLayout();
            }
        });
        homedeliverydropdown_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homedeliverycheckmark_view.setVisibility(View.VISIBLE);
                deliverytype = DELIVERYTYPE_HOMEDELIVERY;
                deliverymode_text.setText(getResources().getString(R.string.homedelivery_text));
                slotdetails_layout.setVisibility(View.VISIBLE);
                deliveryaddressdetails_layout.setVisibility(View.VISIBLE);
                storeaddressdetails_layout.setVisibility(View.GONE);

                selectedDeliverySlotDetails = null;
                isSlotSelected = false;
                slotdesp_textview.setText("Choose your delivery slot");
                TMCMenuItemCatalog.getInstance().setSelectedDeliverySlot("");

                fillItemTotalValues();
                hideDeliveryTypeLayout();
            }
        });
        storepickupdropdown_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storepickupcheckmark_view.setVisibility(View.GONE);
                deliverytype = DELIVERYTYPE_STOREPICKUP;
                deliverymode_text.setText(getResources().getString(R.string.storepickup_text));
                slotdetails_layout.setVisibility(View.VISIBLE);
                deliveryaddressdetails_layout.setVisibility(View.GONE);
                storeaddressdetails_layout.setVisibility(View.VISIBLE);
                selectedDeliverySlotDetails = null;
                isSlotSelected = false;
                slotdesp_textview.setText("Choose your pickup slot");
                TMCMenuItemCatalog.getInstance().setSelectedDeliverySlot("");
                fillItemTotalValues();
                hideDeliveryTypeLayout();
            }
        });
        slotdetails_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSlotSelectionActivity();
            }
        });
        applycoupon_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (couponalreadyapplied) {
                    return;
                }
                startCouponsActivity();
            }
        });
        String mobile = settingsUtil.getMobile();
        String userkey = settingsUtil.getUserkey();
        if ((mobile == null) || (TextUtils.isEmpty(mobile))) {
            AWSMobileClient.getInstance().signOut();
            showForceLoginAlert();
            return;
        }
        if ((userkey == null) || (TextUtils.isEmpty(userkey))) {
            AWSMobileClient.getInstance().signOut();
            showForceLoginAlert();
            return;
        }
        if (settingsUtil.isInventoryCheck()) {
            getNewTMCMenuItemAvailability();
        } else {
            getTMCMenuItemAvailability();
        }

        showPriceDetailsLoadingAnim();

        if (userdeliverydistance > 0) {
            getDeliverySlotDetails(vendorkey);
            getDeliveryslabdetails(vendorkey);
        } else {
            fetchDistanceFromAWS(tmcUserAddress);
        }
    }

    private void showCartEmptyAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(OrderSummaryActivity.this, R.string.app_name, R.string.cartempty_alert,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                Intent intent = new Intent();
                                intent.putExtra("cartempty", true);
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void showForceLoginAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(OrderSummaryActivity.this, R.string.app_name, R.string.forcelogin_alertforadditem,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                closeActivityAndSendForceLoginHandler();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void closeActivityAndSendForceLoginHandler() {
        Intent intent = new Intent();
        intent.putExtra("forcelogin", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void startCouponsActivity() {
        Intent intent = new Intent(OrderSummaryActivity.this, CouponsActivity.class);
        intent.putExtra("mappedvendorkey", vendorkey);
        startActivityForResult(intent, COUPONS_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

 /* private void startPaymentModeActivity() {
        Intent intent = new Intent(OrderSummaryActivity.this, PaymentModeActivity.class);
        Orderdetails orderdetails1 = createOrderdetailsObject();
        intent.putExtra("orderdetails", orderdetails1);
        intent.putExtra("slotdesp", slotdesp);
        startActivityForResult(intent, PAYMENTMODE_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    } */

    boolean isNewOrderActivityCalled = false;
    private void startNewOrderActivity() {
        if (isNewOrderActivityCalled) {
            return;
        }
        isNewOrderActivityCalled = true;
        Intent intent = new Intent(OrderSummaryActivity.this, Neworderactivity.class);
        Orderdetails orderdetails1 = createOrderdetailsObject();
        // Temporary code ------------------------------
        boolean isitemdesppresentinjson = isItemDespPresentInJSON(orderdetails1.getItemdesp());
        logEventInGAnalytics("startOrderConfActivity itemdespavl", "" + isitemdesppresentinjson, settingsUtil.getMobile());
        if (!isitemdesppresentinjson) {
            showItemsNotAddedAlertAndClearCart();
            return;
        }
        //----------------------------------------------
        intent.putExtra("orderdetails", orderdetails1);
        intent.putExtra("orderid", orderdetails1.getOrderid());
        intent.putExtra("slotdesp", slotdesp);
        intent.putExtra("isaddneworder", true);
        startActivityForResult(intent, NEWORDER_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

    private void startOrderConfirmationActivity() {
        if (isOrderConfirmationActivityCalled) {
            return;
        }
        isOrderConfirmationActivityCalled = true;
        Intent intent = new Intent(OrderSummaryActivity.this, OrderConfirmationActivity.class);
        Orderdetails orderdetails1 = createOrderdetailsObject();
        // Temporary code ------------------------------
        boolean isitemdesppresentinjson = isItemDespPresentInJSON(orderdetails1.getItemdesp());
        logEventInGAnalytics("startOrderConfActivity itemdespavl", "" + isitemdesppresentinjson, settingsUtil.getMobile());
        if (!isitemdesppresentinjson) {
            showItemsNotAddedAlertAndClearCart();
            return;
        }
        //----------------------------------------------
        intent.putExtra("orderdetails", orderdetails1);
        intent.putExtra("orderid", orderdetails1.getOrderid());
        intent.putExtra("slotdesp", slotdesp);
        intent.putExtra("isaddneworder", true);
        startActivityForResult(intent, ORDERCONFIRMATION_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }


    private void startSlotSelectionActivity() {
        Intent intent = new Intent(OrderSummaryActivity.this, SlotSelectionActivity.class);
        intent.putExtra("vendorkey", vendorkey);
        if (isSlotSelected) {
            intent.putExtra("selectedsessiontype", selectedsessiontype);
            intent.putExtra("selecteddeliveryslotdetails", selectedDeliverySlotDetails);
        }
        intent.putExtra("deliverytype", deliverytype);
        intent.putExtra("deliveryslotdetailsarraylist", deliveryslotdetailsArrayList);
        startActivityForResult(intent, SLOTSELECTION_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void fillSlotDetailsInTextView() {
        // String selectedsessiontype = TMCMenuItemCatalog.getInstance().getSelectedSlotSessionType();
        // String selecteddeliveryslot = TMCMenuItemCatalog.getInstance().getSelectedDeliverySlot();

     /* if ((TextUtils.isEmpty(selectedsessiontype)) || (TextUtils.isEmpty(selecteddeliveryslot))) {
            isSlotSelected = false;
         /* selectedDeliverySlotDetails = defaultDeliverySlotDetails;
            if (defaultDeliverySlotDetails != null) {
                if (defaultDeliverySlotDetails.getSlotname().equalsIgnoreCase("Express Delivery")) {
                    slotdesp_textview.setText(defaultDeliverySlotDetails.getSlotname() + " - " +
                            defaultDeliverySlotDetails.getDeliverytime());
                } else {
                    String slotdate = "";
                    if (defaultDeliverySlotDetails.getSlotdatetype().equalsIgnoreCase("Today")) {
                        slotdate = TMCUtil.getInstance().getCurrentDate();

                    } else if (defaultDeliverySlotDetails.getSlotdatetype().equalsIgnoreCase("Tomorrow")) {
                        slotdate = TMCUtil.getInstance().getTomorrowDate();
                    }
                    String slotdesp = slotdate + " " + defaultDeliverySlotDetails.getSlotstarttime() +
                            " - " + defaultDeliverySlotDetails.getSlotendtime();
                    slotdesp_textview.setText(slotdesp);
                }
            }
            deliveryamount = selectedDeliverySlotDetails.getDeliverycharge(); */
        if (selectedDeliverySlotDetails != null) {
            isSlotSelected = true;
            String selectedslotstarttime = "";
            String selectedslotendtime = "";
            String selectedslotdeliverytime = "";
            try {
                // Deliveryslots deliveryslotdetails = new Deliveryslots(new JSONObject(selecteddeliveryslot));
                // selectedDeliverySlotDetails = deliveryslotdetails;

                selectedslotstarttime = selectedDeliverySlotDetails.getSlotstarttime();
                selectedslotendtime = selectedDeliverySlotDetails.getSlotendtime();
                selectedslotdeliverytime = selectedDeliverySlotDetails.getDeliverytime();
                // deliveryamount = selectedDeliverySlotDetails.getDeliverycharge();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            String slotdate = "";
            String sessiontype = selectedDeliverySlotDetails.getSlotdatetype();
            sessiontype = StringUtils.capitalize(sessiontype);
            String slotname = selectedDeliverySlotDetails.getSlotname();
            if (slotname.equalsIgnoreCase(Deliveryslots.SLOTNAME_PREORDER)) {
                if (sessiontype.equalsIgnoreCase("Today")) {
                    slotdate = TMCUtil.getInstance().getCurrentDate();
                    slotdesp = slotdate + " " + selectedslotstarttime + " - " + selectedslotendtime;
                } else if (sessiontype.equalsIgnoreCase("Tomorrow")) {
                    slotdate = TMCUtil.getInstance().getTomorrowDate();
                    slotdesp = slotdate + " " + selectedslotstarttime + " - " + selectedslotendtime;
                } else {
                    slotdesp = sessiontype + " - " + selectedslotdeliverytime;
                }
            } else if (slotname.equalsIgnoreCase(Deliveryslots.SLOTNAME_EXPRESSDELIVERY)) {
                slotdesp = slotname + " - " + selectedDeliverySlotDetails.getDeliverytime();
            } else if (slotname.equalsIgnoreCase(Deliveryslots.SLOTNAME_SPLDAYPREORDER)) {
                slotdate = selectedDeliverySlotDetails.getSlotdate();
                slotdesp = slotdate + " " + selectedslotstarttime + " - " + selectedslotendtime;
            }

            slotdesp_textview.setText(slotdesp);
        } else {
            isSlotSelected = false;
            slotdesp_textview.setText("Choose your delivery slot");
        }
     /* if (selectedDeliverySlotDetails != null) {
            Log.d(TAG, "selectedDeliverySlotDetails status "+selectedDeliverySlotDetails.getStatus());
            if (selectedDeliverySlotDetails.getStatus().equalsIgnoreCase("Active")) {
                isSlotActive = true;
                slotavailability_textview.setVisibility(View.GONE);
            } else {
                isSlotActive = false;
                slotavailability_textview.setVisibility(View.VISIBLE);
            }
        } */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SLOTSELECTION_ACT_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        isSlotSelected = data.getBooleanExtra("isslotselected", false);
                        selectedDeliverySlotDetails = data.getParcelableExtra("selecteddeliveryslotdetails");
                        double newdeliverycharges = data.getDoubleExtra("newdeliverycharges", 0);
                     /* if (newdeliverycharges > 0) {
                            deliveryamount = newdeliverycharges;
                        } else {
                            deliveryamount = 0;
                        } */
                        fillSlotDetailsInTextView();
                        fillItemTotalValues();
                    }

                }
                break;

            case MARINADE_ACT_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean itemupdated = data.getBooleanExtra("itemupdated", false);
                        if (itemupdated) {
                            refreshShoppingBagAdapter();
                        }
                    }
                }
                break;

            case MENUITEMCUSTOM_ACT_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean isvalueschanged = data.getBooleanExtra("isvalueschanged", false);
                        if (isvalueschanged) {
                            refreshShoppingBagAdapter();
                        }
                    }
                }
                break;

            case COUPONS_ACT_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean applycouponclicked = data.getBooleanExtra("applycouponclicked", false);
                        if (applycouponclicked) {
                            couponalreadyapplied = true;
                            appliedCouponDetails = data.getParcelableExtra("coupondetails");
                            computeCouponDiscountAmount(appliedCouponDetails, true);
                        }
                    }
                }
                break;

            case DETAILORDER_ACT_REQ_CODE:
                if (resultCode == RESULT_OK) {

                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void fillItemTotalValues() {
        hidePriceDetailsLoadingAnim();
        ArrayList<TMCMenuItem> menuItems = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
        if (menuItems == null) {
            return;
        }
        double itemtotalvalue = 0;
        double itemtotalgstvalue = 0;

        totalsavedamount = 0;
        for (int i = 0; i < menuItems.size(); i++) {
            TMCMenuItem tmcMenuItem = menuItems.get(i);
            // Log.d(TAG, "tmcmenuitemkey "+tmcMenuItem.getAwsKey());
            // Log.d(TAG, "menustockdetails "+tmcMenuItem.getMenuStockDetailsArray());
            double baseamount = 0;
            double itemtotal = 0;
            baseamount = tmcMenuItem.getTmcprice();
            itemtotal = tmcMenuItem.getSelectedqty() * baseamount;
            itemtotalvalue = itemtotalvalue + itemtotal;
            double gstpercentage = tmcMenuItem.getGstpercentage();
            // Compute savedamount
            double applieddiscountpercentage = tmcMenuItem.getApplieddiscpercentage();
            if (applieddiscountpercentage > 0) {
                double oldamount = itemtotal / ((100 - applieddiscountpercentage) / 100);
                double savedamount = oldamount - itemtotal;
                totalsavedamount = totalsavedamount + savedamount;
            }

            // Log.d(TAG, "gstpercentage "+gstpercentage);
            if (gstpercentage > 0) {
                double itemgstvalue = itemtotal * (gstpercentage / 100);
                itemtotalgstvalue = itemtotalgstvalue + itemgstvalue;
            }
        }

        if (deliverytype == OrderSummaryActivity.DELIVERYTYPE_HOMEDELIVERY) {
            if (mappeddeliveryslabdetails != null) {
                deliveryamount = mappeddeliveryslabdetails.getDeliverycharges();
                minimumorderfordelivery = mappeddeliveryslabdetails.getMinordervalue();
            }
        } else if (deliverytype == OrderSummaryActivity.DELIVERYTYPE_STOREPICKUP) {
            deliveryamount = 0;
            minimumorderfordelivery = settingsUtil.getMinimumOrderForDelivery();
        }
        if (deliveryamount > 0) {
            deliverypartnerfee_desp.setText("Delivery charge for " + userdeliverydistance + " kms");
        } else {
            deliverypartnerfee_desp.setText("Delivery charge");
            deliverychargedetaileddesp_textview.setVisibility(View.GONE);
        }

        String rs = getResources().getString((R.string.Rs));
        double couponvalue = 0;
        totaltaxamount = itemtotalgstvalue;

        totalpayableamount = itemtotalvalue + deliveryamount + totaltaxamount;
        if (couponamount > 0) {
            totalpayableamount = totalpayableamount - couponamount;
            couponvalue_textview.setText(rs + String.format("%.2f", couponamount));
            couponamounttext_textview.setText(couponamounttext);
            couponamountdetails_layout.setVisibility(View.VISIBLE);
        } else {
            couponamountdetails_layout.setVisibility(View.GONE);
            couponamount = 0;
            couponvalue_textview.setText(rs + String.format("%.2f", couponamount));
        }

        if (totalsavedamount > 0) {
            double itemtotalplussavedamount = itemtotalvalue + totalsavedamount;
            itemtotalvalue_textview.setText(rs + String.format("%.2f", itemtotalplussavedamount));
            itemdiscountvalue_textview.setText(rs + String.format("%.2f", totalsavedamount));
            itemdiscount_layout.setVisibility(View.VISIBLE);
            String savedamountstr = String.format("%.2f", totalsavedamount);
            String text = "You just saved " + rs + savedamountstr + " on these items!";
            savedamount_textview.setText(text);
            savedamount_textview.setVisibility(View.VISIBLE);
        } else {
            savedamount_textview.setVisibility(View.GONE);
            itemtotalvalue_textview.setText(rs + String.format("%.2f", itemtotalvalue));
            itemdiscount_layout.setVisibility(View.GONE);
        }

        deliveryfee_textview.setText(rs + String.format("%.2f", deliveryamount));
        taxandcharges_textview.setText(rs + String.format("%.2f", totaltaxamount));
        topay_textview.setText(rs + String.format("%.2f", totalpayableamount));
        payableamount_textview.setText(rs + String.format("%.2f", totalpayableamount));

        isMeatProductsAvailable = TMCMenuItemCatalog.getInstance().
                isMeatProductsAvailableInCart(settingsUtil.getFreshproducectgykey());
        if (itemtotalvalue >= minimumorderfordelivery) {
            isMinimumOrderValueCrossed = true;
        } else {
            isMinimumOrderValueCrossed = false;
        }

     /* if (shoppingBagAdapter != null) {
            double savedamount = shoppingBagAdapter.getTotalsavedamount();
            if (savedamount > 0) {
                String savedamountstr = String.format("%.2f", savedamount);
                String text = "You just saved " +  rs + savedamountstr + " on these items!";
                savedamount_textview.setText(text);
                savedamount_textview.setVisibility(View.VISIBLE);
            } else {
                savedamount_textview.setVisibility(View.GONE);
            }
        } */
    }

    private Orderdetails createOrderdetailsObject() {
        Orderdetails orderdetails = new Orderdetails();
        String addressjson = settingsUtil.getDefaultAddress();
        TMCUserAddress tmcUserAddress = null;
        ArrayList<TMCMenuItem> menuItems = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
        JSONArray jsonArray = new JSONArray(); JSONObject itemdespjson = new JSONObject();
        JSONArray menustockdetailsArray = new JSONArray(); JSONObject menustockdetailsjson = new JSONObject();
        try {
            tmcUserAddress = new TMCUserAddress(new JSONObject(addressjson));
            for (int i = 0; i < menuItems.size(); i++) {
                TMCMenuItem tmcMenuItem = menuItems.get(i);

                // Code for Google Analytics logs
                tmcMenuItem.setTMCTracker(getTmcTracker());
                tmcMenuItem.setMobileno(settingsUtil.getMobile());

                JSONObject menuitemjson = tmcMenuItem.getJSONObjectStringForOrderDetails();
                jsonArray.put(menuitemjson);
                if (settingsUtil.isInventoryCheck()) {
                    JSONArray menuStockDetailsArray = tmcMenuItem.getMenuStockDetailsArray();
                    orderdetails.addMenuStockdetails(menuStockDetailsArray);
                }
                // Log.d(TAG, "menuitemjson "+menuitemjson);
            }
            itemdespjson.put("itemdesp", jsonArray);
            JSONArray menustockdetailsarray = orderdetails.getMenustockdetailsArray();
            if ((menustockdetailsarray != null) && (menustockdetailsarray.length() > 0)) {
                menustockdetailsjson.put("menustockdetails", orderdetails.getMenustockdetailsArray());
                orderdetails.setMenustockdetails(menustockdetailsjson.toString());
            }
            // Log.d(TAG, "menustockdetailsjson "+menustockdetailsjson);
        } catch (Exception ex) {
            logEventInGAnalytics("createOrderdetailsObject exception", ex.getMessage(), settingsUtil.getMobile());
            try {
                logEventInGAnalytics("createOrderdetailsObject ex lineno", "" + ex.getStackTrace()[0].getLineNumber(),
                        settingsUtil.getMobile());
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }

            logEventInGAnalytics("createOrderdetailsObject exception", itemdespjson.toString(), settingsUtil.getMobile());
            ex.printStackTrace();
        }
        orderdetails.setOrderid(generateOrderid());
        orderdetails.setVendorkey(tmcUserAddress.getVendorkey());
        orderdetails.setVendorname(tmcUserAddress.getVendorname());

        orderdetails.setItemdesp(itemdespjson.toString());
        orderdetails.setUseraddress(tmcUserAddress.getAddressline1() + ", " + tmcUserAddress.getAddressline2());
        if (selectedDeliverySlotDetails != null) {
            String slotname = selectedDeliverySlotDetails.getSlotname();
            String newslotname = slotname.replaceAll(" ", "");
            orderdetails.setSlotname(newslotname.toUpperCase());

            if (slotname.equalsIgnoreCase(Deliveryslots.SLOTNAME_PREORDER)) {
                orderdetails.setSlottimerange(selectedDeliverySlotDetails.getSlotstarttime() + " - " +
                        selectedDeliverySlotDetails.getSlotendtime());
                String sessiontype = TMCMenuItemCatalog.getInstance().getSelectedSlotSessionType();
                if (sessiontype.equalsIgnoreCase("Today")) {
                    orderdetails.setSlotdate(TMCUtil.getInstance().getCurrentDate());
                    orderdetails.setSlotdatenew(TMCUtil.getInstance().getCurrentDateNew());
                } else if (sessiontype.equalsIgnoreCase("Tomorrow")) {
                    orderdetails.setSlotdate(TMCUtil.getInstance().getTomorrowDate());
                    orderdetails.setSlotdatenew(TMCUtil.getInstance().getTomorrowDateNew());
                }
            } else if (slotname.equalsIgnoreCase(Deliveryslots.SLOTNAME_EXPRESSDELIVERY)) {
                orderdetails.setSlottimerange(selectedDeliverySlotDetails.getDeliverytime());
                orderdetails.setSlotdate(TMCUtil.getInstance().getCurrentDate());
                orderdetails.setSlotdatenew(TMCUtil.getInstance().getCurrentDateNew());
            } else if (slotname.equalsIgnoreCase(Deliveryslots.SLOTNAME_SPLDAYPREORDER)) {
                orderdetails.setSlotdate(selectedDeliverySlotDetails.getSlotdate());
                orderdetails.setSlottimerange(selectedDeliverySlotDetails.getSlotstarttime() + " - " +
                        selectedDeliverySlotDetails.getSlotendtime());
            }
        }

        String notes = notes_edittext.getText().toString();
        if (notes != null) {
            orderdetails.setNotes(notes);
        }

        deliveryamount = Math.round(deliveryamount * 100.0) / 100.0;
        totaltaxamount = Math.round(totaltaxamount * 100.0) / 100.0;
        totalpayableamount = Math.round(totalpayableamount * 100.0) / 100.0;

        if (totalsavedamount > 0) {
            totalsavedamount = Math.round(totalsavedamount * 100.0) / 100.0;
            orderdetails.setDiscountamount(totalsavedamount);
        }
        orderdetails.setDeliveryamount(deliveryamount);
        orderdetails.setGstamount(totaltaxamount);
        orderdetails.setPayableamount(totalpayableamount);
        orderdetails.setOrdertype("APPORDER");
        if (couponamount > 0) {
            couponamount = Math.round(couponamount * 100.0) / 100.0;
            orderdetails.setCoupondiscount(couponamount);
            orderdetails.setCouponkey(couponkey);
            orderdetails.setCoupontype(coupontype);
        }
        if (deliverytype == DELIVERYTYPE_HOMEDELIVERY) {
            orderdetails.setDeliverytype("HOMEDELIVERY");
        } else if (deliverytype == DELIVERYTYPE_STOREPICKUP) {
            orderdetails.setDeliverytype("STOREPICKUP");
            // orderdetails.setSlotdate(TMCUtil.getInstance().getCurrentDate());
            orderdetails.setSlotname("Preorder");
        }
        orderdetails.setUserkey(settingsUtil.getUserkey());
        orderdetails.setUsermobile(settingsUtil.getMobile());
        return orderdetails;
    }

    @Override
    public void onBackPressed() {
        if (itemqty_layout.getVisibility() == View.VISIBLE) {
            hideQuantityLayout();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("quantitychanged", quantitychanged);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_right);
    }

    private Handler createHandlerForShoppingBagAdapter() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("qtyclicked")) {
                    int qty = bundle.getInt("qty");
                    clickedpositionforqtypopup = bundle.getInt("position");
                    showQuantityLayout(qty);
                } else if (menutype.equalsIgnoreCase("customizebtnclicked")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    startMarinadeActivity(menuitemkey);
                } else if (menutype.equalsIgnoreCase("customizeitemcutsweightclicked")) {
                    String menuitemkey = bundle.getString("menuitemkey");
                    startMenuItemCustomizationActivity(menuitemkey);
                }
                return false;
            }
        };
        return new Handler(callback);
    }

    private void initIngredientLayout() {
        ingred_recyclerview = (RecyclerView) findViewById(R.id.ingred_recyclerview);
        ingredients_layout = findViewById(R.id.ingredients_layout);
        ingredients_title = (TMCTextView) findViewById(R.id.ingredients_title);
        ingredientmask_layout = findViewById(R.id.ingredientmask_layout);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ingred_recyclerview.setLayoutManager(mLayoutManager);
        ingred_recyclerview.setItemAnimator(new DefaultItemAnimator());
        customizelayoutdone_btn = findViewById(R.id.customizelayoutdone_btn);
        customizelayoutdone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIngredientsLayout();
            }
        });
    }

    private void startMenuItemCustomizationActivity(String menuitemkey) {
        TMCMenuItem menuitemfromcart = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(menuitemkey);
        Intent intent = new Intent(OrderSummaryActivity.this, MenuItemCustomizationActivity.class);
        intent.putExtra("menuitemkey", menuitemkey);
        intent.putExtra("callfromordersummaryactivity", true);
        startActivityForResult(intent, MENUITEMCUSTOM_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void startMarinadeActivity(String menuitemkey) {
        TMCMenuItem menuitemfromcart = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(menuitemkey);
        Intent intent = new Intent(OrderSummaryActivity.this, MarinadeActivity.class);
        String marinadelinkedcodes = menuitemfromcart.getMarinadelinkedcodes();
        String selectedmarinadelinkedcode = menuitemfromcart.getMarinadeMeatTMCMenuItem().getItemuniquecode();
        intent.putExtra("menuitemkey", menuitemkey);
        intent.putExtra("marinadelinkedcodes", marinadelinkedcodes);
        intent.putExtra("selectedmarinadelinkedcode", selectedmarinadelinkedcode);
        intent.putExtra("callfromordersummaryactivity", true);
        startActivityForResult(intent, MARINADE_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
    }

    private void showIngredientsLayout(ArrayList<Mealkitingredient> mealkitingredients) {
        if (orderSummIngredAdapter != null) {
            orderSummIngredAdapter = null;
        }
        // preLoadMealKitIngredients();
        orderSummIngredAdapter = new OrderSummIngredAdapter(OrderSummaryActivity.this, mealkitingredients);
        ingred_recyclerview.setAdapter(orderSummIngredAdapter);
        ingredientmask_layout.setVisibility(View.VISIBLE);
        ingredients_layout.setVisibility(View.VISIBLE);

     /* Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(300);
        ingredients_layout.startAnimation(bottomUp); */

    }

    private void hideIngredientsLayout() {
        ingredients_layout.setVisibility(View.GONE);
        ingredientmask_layout.setVisibility(View.GONE);
     /* Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                ingredients_layout.setVisibility(View.GONE);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(200);
        ingredientmask_layout.startAnimation(bottomDown);
        ingredientmask_layout.setVisibility(View.GONE); */
    }

    private void preLoadMealKitIngredients() {
        ArrayList<Mealkitingredient> ingredientList = new ArrayList<Mealkitingredient>();

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

        orderSummIngredAdapter = new OrderSummIngredAdapter(OrderSummaryActivity.this, ingredientList);
        ingred_recyclerview.setAdapter(orderSummIngredAdapter);
    }


    private ArrayList<View> itemqtylayoutList;
    private ArrayList<TMCTextView> itemqtyTextViewList;

    private void initQuantityLayout() {
        View itemqtyzero_layout = findViewById(R.id.itemqtyzero_layout);
        View itemqtyone_layout = findViewById(R.id.itemqtyone_layout);
        View itemqtytwo_layout = findViewById(R.id.itemqtytwo_layout);
        View itemqtythree_layout = findViewById(R.id.itemqtythree_layout);
        View itemqtyfour_layout = findViewById(R.id.itemqtyfour_layout);
        View itemqtyfive_layout = findViewById(R.id.itemqtyfive_layout);
        View itemqtysix_layout = findViewById(R.id.itemqtysix_layout);
        View itemqtyseven_layout = findViewById(R.id.itemqtyseven_layout);
        View itemqtyeight_layout = findViewById(R.id.itemqtyeight_layout);
        View itemqtynine_layout = findViewById(R.id.itemqtynine_layout);
        View itemqtyten_layout = findViewById(R.id.itemqtyten_layout);
        TMCTextView itemqtyzero_text = (TMCTextView) findViewById(R.id.itemqtyzero_text);
        TMCTextView itemqtyone_text = (TMCTextView) findViewById(R.id.itemqtyone_text);
        TMCTextView itemqtytwo_text = (TMCTextView) findViewById(R.id.itemqtytwo_text);
        TMCTextView itemqtythree_text = (TMCTextView) findViewById(R.id.itemqtythree_text);
        TMCTextView itemqtyfour_text = (TMCTextView) findViewById(R.id.itemqtyfour_text);
        TMCTextView itemqtyfive_text = (TMCTextView) findViewById(R.id.itemqtyfive_text);
        TMCTextView itemqtysix_text = (TMCTextView) findViewById(R.id.itemqtysix_text);
        TMCTextView itemqtyseven_text = (TMCTextView) findViewById(R.id.itemqtyseven_text);
        TMCTextView itemqtyeight_text = (TMCTextView) findViewById(R.id.itemqtyeight_text);
        TMCTextView itemqtynine_text = (TMCTextView) findViewById(R.id.itemqtynine_text);
        TMCTextView itemqtyten_text = (TMCTextView) findViewById(R.id.itemqtyten_text);
        itemqtylayoutList = new ArrayList<View>();
        itemqtyTextViewList = new ArrayList<TMCTextView>();
        itemqtylayoutList.add(0, itemqtyzero_layout);
        itemqtylayoutList.add(1, itemqtyone_layout);
        itemqtylayoutList.add(2, itemqtytwo_layout);
        itemqtylayoutList.add(3, itemqtythree_layout);
        itemqtylayoutList.add(4, itemqtyfour_layout);
        itemqtylayoutList.add(5, itemqtyfive_layout);
        itemqtylayoutList.add(6, itemqtysix_layout);
        itemqtylayoutList.add(7, itemqtyseven_layout);
        itemqtylayoutList.add(8, itemqtyeight_layout);
        itemqtylayoutList.add(9, itemqtynine_layout);
        itemqtylayoutList.add(10, itemqtyten_layout);

        itemqtyTextViewList.add(0, itemqtyzero_text);
        itemqtyTextViewList.add(1, itemqtyone_text);
        itemqtyTextViewList.add(2, itemqtytwo_text);
        itemqtyTextViewList.add(3, itemqtythree_text);
        itemqtyTextViewList.add(4, itemqtyfour_text);
        itemqtyTextViewList.add(5, itemqtyfive_text);
        itemqtyTextViewList.add(6, itemqtysix_text);
        itemqtyTextViewList.add(7, itemqtyseven_text);
        itemqtyTextViewList.add(8, itemqtyeight_text);
        itemqtyTextViewList.add(9, itemqtynine_text);
        itemqtyTextViewList.add(10, itemqtyten_text);

        itemqtyzero_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtyzero_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtyzero_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 1; i < itemqtylayoutList.size(); i++) {
                        // if (i == 1) { continue; }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 1; j < itemqtyTextViewList.size(); j++) {
                        // if (j == 1) { continue; }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 0;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        itemqtyone_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtyone_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtyone_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 1) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 1) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 1;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemqtytwo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtytwo_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtytwo_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 2) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 2) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 2;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemqtythree_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtythree_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtythree_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 3) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 3) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 3;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        itemqtyfour_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtyfour_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtyfour_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        View itemview = itemqtylayoutList.get(i);
                        if (i == 4) {
                            continue;
                        }
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 4) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 4;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemqtyfive_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtyfive_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtyfive_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 5) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 5) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 5;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemqtysix_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtysix_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtysix_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 6) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 6) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 6;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemqtyseven_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtyseven_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtyseven_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 7) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 7) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 7;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemqtyeight_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtyeight_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtyeight_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 8) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 8) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 8;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemqtynine_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtynine_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtynine_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 9) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 9) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 9;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemqtyten_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    itemqtyten_layout.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
                    itemqtyten_text.setTextColor(getResources().getColor(R.color.meatchopbg_red));
                    for (int i = 0; i < itemqtylayoutList.size(); i++) {
                        if (i == 10) {
                            continue;
                        }
                        View itemview = itemqtylayoutList.get(i);
                        itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
                    }
                    for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                        if (j == 10) {
                            continue;
                        }
                        TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                        itemtextview.setTextColor(getResources().getColor(R.color.black));
                    }
                    selectedquantity = 10;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void showQuantityLayout(int quantity) {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                itemqtymask_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(300);
        itemqty_layout.startAnimation(bottomUp);
        itemqty_layout.setVisibility(View.VISIBLE);
        try {
            View itemview = itemqtylayoutList.get(quantity);
            itemview.setBackground(getResources().getDrawable(R.drawable.selectedqty_circle));
            TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(quantity);
            itemtextview.setTextColor(getResources().getColor(R.color.meatchopbg_red));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hideQuantityLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                itemqty_layout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(200);
        itemqty_layout.startAnimation(bottomDown);
        itemqtymask_layout.setVisibility(View.GONE);

        try {
            for (int i = 0; i < itemqtylayoutList.size(); i++) {
                View itemview = itemqtylayoutList.get(i);
                itemview.setBackground(getResources().getDrawable(R.drawable.qty_circle));
            }
            for (int j = 0; j < itemqtyTextViewList.size(); j++) {
                TMCTextView itemtextview = (TMCTextView) itemqtyTextViewList.get(j);
                itemtextview.setTextColor(getResources().getColor(R.color.black));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showDeliveryTypeLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                deliverytypemask_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(300);
        deliverytypedropdown_layout.startAnimation(bottomUp);
        deliverytypedropdown_layout.setVisibility(View.VISIBLE);
    }

    private void hideDeliveryTypeLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                deliverytypedropdown_layout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(200);
        deliverytypedropdown_layout.startAnimation(bottomDown);
        deliverytypemask_layout.setVisibility(View.GONE);
    }

    private void showItemRemovalTMCAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                new TMCAlert(OrderSummaryActivity.this, R.string.app_name, R.string.itemdeletionalermessage_text,
                        R.string.ok_capt, R.string.cancel_capt,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                quantitychanged = true;
                                refreshShoppingBagAdapterAndClearItem();
                                fillItemTotalValues();
                                if ((couponamount > 0) && (appliedCouponDetails != null)) {
                                    computeCouponDiscountAmount(appliedCouponDetails, false);
                                }
                                hideQuantityLayout();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void showMeatNotAvailableAlert() {
        String message = getResources().getString(R.string.meatprodnotavl_alertmessage);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(OrderSummaryActivity.this, getResources().getString(R.string.app_name),
                        message, getResources().getString(R.string.ok_capt), "",
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {

                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void showMinOrderValueAlert() {
        String message = getResources().getString(R.string.minorderval_alertmessage) + " Rs." + minimumorderfordelivery;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(OrderSummaryActivity.this, getResources().getString(R.string.app_name),
                        message, getResources().getString(R.string.ok_capt), "",
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {

                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }


    private void showTMCAlert(String alerttitle, String alertmessage) {
        if ((alertmessage == null) || (TextUtils.isEmpty(alertmessage))) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                new TMCAlert(OrderSummaryActivity.this, alerttitle, alertmessage,
                        getResources().getString(R.string.ok_capt), "",
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {

                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void showSlotNotSelectedAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                new TMCAlert(OrderSummaryActivity.this, R.string.slotnotselected_title, R.string.slotnotselectedalert_text,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {

                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void showSlotNotAvailableAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                new TMCAlert(OrderSummaryActivity.this, R.string.slotnotavailablealert_title, R.string.slotnotavailablealert_text,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {

                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void refreshShoppingBagAdapterAndClearItem() {
        quantitychanged = true;
        ArrayList<TMCMenuItem> menuItemList = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
        TMCMenuItem tmcMenuItem = menuItemList.get(clickedpositionforqtypopup);
        tmcMenuItem.setSelectedqty(0);
        TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
        int cartcount = TMCMenuItemCatalog.getInstance().getCartCount();
        if (cartcount <= 0) {
            Intent intent = new Intent();
            intent.putExtra("quantitychanged", quantitychanged);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        shoppingBagAdapter = new ShoppingBagAdapter(OrderSummaryActivity.this, TMCMenuItemCatalog.getInstance().getCartMenuItemList());
        shoppingbag_recyclerview.setAdapter(shoppingBagAdapter);
        shoppingBagAdapter.setHandler(createHandlerForShoppingBagAdapter());
    }

    private void refreshShoppingBagAdapterAndRemoveItem(String menuitemkey) {
        quantitychanged = true;
        TMCMenuItem tmcMenuItem = TMCMenuItemCatalog.getInstance().getCartTMCMenuItem(menuitemkey);
        tmcMenuItem.setSelectedqty(0);
        TMCMenuItemCatalog.getInstance().updateMenuItemInCart(tmcMenuItem);
        int cartcount = TMCMenuItemCatalog.getInstance().getCartCount();
        if (cartcount <= 0) {
            Intent intent = new Intent();
            intent.putExtra("quantitychanged", quantitychanged);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        shoppingBagAdapter = new ShoppingBagAdapter(OrderSummaryActivity.this, TMCMenuItemCatalog.getInstance().getCartMenuItemList());
        shoppingbag_recyclerview.setAdapter(shoppingBagAdapter);
        shoppingBagAdapter.setHandler(createHandlerForShoppingBagAdapter());
    }

    private void refreshShoppingBagAdapter() {
        ArrayList<TMCMenuItem> menuItemList = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
        shoppingBagAdapter = new ShoppingBagAdapter(OrderSummaryActivity.this, TMCMenuItemCatalog.getInstance().getCartMenuItemList());
        shoppingbag_recyclerview.setAdapter(shoppingBagAdapter);
        shoppingBagAdapter.setHandler(createHandlerForShoppingBagAdapter());
        fillItemTotalValues();
        if ((couponamount > 0) && (appliedCouponDetails != null)) {
            computeCouponDiscountAmount(appliedCouponDetails, false);
        }
    }

    private void showDeliverySlotsLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                deliveryslotsloadinganim_layout.setVisibility(View.VISIBLE);
                deliveryslotdetails_layout.setVisibility(View.GONE);
            }
        });
    }

    private void hideDeliverySlotsLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                deliveryslotsloadinganim_layout.setVisibility(View.GONE);
                deliveryslotdetails_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showPriceDetailsLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                pricedetailsloadinganim_layout.setVisibility(View.VISIBLE);
                pricedetails_layout.setVisibility(View.GONE);
            }
        });
    }

    private void hidePriceDetailsLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                pricedetailsloadinganim_layout.setVisibility(View.GONE);
                pricedetails_layout.setVisibility(View.VISIBLE);
            }
        });
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

 /* private void getDeliverySlotDetails1(String vendorkey) {
        showLoadingAnim();
        TMCRestClient.getDeliverySlotDetails(vendorkey, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    String message = jsonObject.getString("message");

                    if (message.equalsIgnoreCase("success")) {
                        deliveryslotdetailsArrayList = new ArrayList<Deliveryslotdetails>();
                        defaultDeliverySlotDetails = null;
                        JSONArray jsonArray = jsonObject.getJSONArray("content");
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject deliveryjsonobj = jsonArray.getJSONObject(i);
                            boolean isdefault = deliveryjsonobj.getBoolean("isdefault");
                            Deliveryslotdetails deliveryslotdetails = new Deliveryslotdetails(deliveryjsonobj);
                            if (isdefault) {
                                defaultDeliverySlotDetails = deliveryslotdetails;
                            } else {
                                deliveryslotdetailsArrayList.add(deliveryslotdetails);
                            }
                        }
                        fillSlotDetailsInTextView();
                        fillItemTotalValues();

                        hideLoadingAnim();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "getDeliverySlotDetails fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "getDeliverySlotDetails fetch failed"); //No i18n
            }
        });
    }
*/

    private void showConfirmPlaceOrderAlert() {
        String title = getResources().getString(R.string.confirmplaceorder_title);
        String message = getResources().getString(R.string.confirmplacerder_desp);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(OrderSummaryActivity.this, R.string.confirmorder_title, R.string.confirmplacerder_desp,
                        R.string.ok_capt, R.string.cancel_capt,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                startOrderConfirmationActivity();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void getDeliverySlotDetails(String vendorkey) {
        String url = TMCRestClient.AWS_GETDELSLOTDETAILS + "?storeid=" + vendorkey;
        showDeliverySlotsLoadingAnim();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response " + response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                deliveryslotdetailsArrayList = new ArrayList<Deliveryslots>();
                                defaultDeliverySlotDetails = null;

                                JSONArray jsonArray = response.getJSONArray("content");
                             /* double userdeliverydistance = 0;
                                if (tmcUserAddress != null) {
                                    userdeliverydistance = tmcUserAddress.getDeliverydistance();
                                } */
                                String action = "deldistance:" + userdeliverydistance + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("GetDeliverySlots", action, settingsUtil.getMobile());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject deliveryjsonobj = jsonArray.getJSONObject(i);
                                    boolean isdefault = deliveryjsonobj.getBoolean("isdefault");
                                    Deliveryslots deliveryslotdetails = new Deliveryslots(deliveryjsonobj);

                                    // Log.d(TAG, "userdeliverydistance "+userdeliverydistance+
                                    //         " deliveryslotdetails.getMaximumdeliverydistance() "+deliveryslotdetails.getMaximumdeliverydistance()+
                                    //         " defdeldistance " +deliveryslotdetails.getDefaultdeliverydistance());
                                    String deliveryslotname = deliveryslotdetails.getSlotname();
                                    deliveryslotdetails.setIsdeliverable(true);
                                    if (userdeliverydistance > 0) {
                                        if (deliveryslotdetails.getMaximumdeliverydistance() >= userdeliverydistance) {
                                            String selecteddeliveryslotkey = TMCMenuItemCatalog.getInstance().getSelecteddeliveryslotkey();
                                            if (selecteddeliveryslotkey.equalsIgnoreCase(deliveryslotdetails.getKey())) {
                                                if (deliveryslotdetails.getStatus().equalsIgnoreCase("Active")) {
                                                    selectedDeliverySlotDetails = deliveryslotdetails;
                                                }
                                            }
                                        } else {
                                            deliveryslotdetails.setIsdeliverable(false);
                                        }
                                    } else {
                                        if (deliveryslotname.equalsIgnoreCase(Deliveryslots.SLOTNAME_EXPRESSDELIVERY)) {
                                            deliveryslotdetails.setIsdeliverable(false);
                                        }
                                    }
                                    deliveryslotdetailsArrayList.add(deliveryslotdetails);

                                 /* if (defaultDeliverySlotDetails == null) {
                                        if (deliveryslotdetails.getIsdeliverable() &&
                                                      deliveryslotdetails.getStatus().equalsIgnoreCase("Active")) {
                                            Log.d(TAG, "deliveryslotdetails "+deliveryslotdetails.getSlotstarttime());
                                            defaultDeliverySlotDetails = deliveryslotdetails;
                                            isSlotActive = true;
                                            Log.d(TAG, "defaultDeliverySlotDetails "+defaultDeliverySlotDetails.getSlotname());
                                        }
                                    } */



                                 /* if (isdefault) {
                                        defaultDeliverySlotDetails = deliveryslotdetails;
                                        if (defaultDeliverySlotDetails.getStatus().equalsIgnoreCase("Active")) {
                                            isSlotActive = true;
                                        } else {
                                            isSlotActive = false;
                                        }
                                    } else {
                                        deliveryslotdetailsArrayList.add(deliveryslotdetails);
                                    } */
                                }
                                Log.d(TAG, "isslotactive " + isSlotActive);
                                hideDeliverySlotsLoadingAnim();
                                fillSlotDetailsInTextView();
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
                        getDeliverySlotDetails(vendorkey);
                        error.printStackTrace();
                    }
                }) {
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

    private void getNewTMCMenuItemAvailability() {
        if ((itemAvailabilityMap == null) || (itemAvailabilityMap.size() <= 0)) {
            return;
        }
        showLoadingAnim();
        // String url = TMCRestClient.AWS_GETTMCMENUITEMS + "?storeid=" + vendorkey + "&subctgyid=" + selectedtmcsubctgykey;
        String url = TMCRestClient.AWS_GETTMCMENUITEMAVLDETAILSFORSTORE + "?vendorkey=" + vendorkey;
        Log.d(TAG, "getNewTMCMenuItemAvailability url " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley getTMCMenuItemAvailability response "+response.toString());
                        try {
                            String message = response.getString("message");

                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String menuitemkey = jsonObject1.getString("menuitemkey");
                                    boolean newmenuitemavl = jsonObject1.getBoolean("itemavailability");

                                 /* TMCMenuItem newtmcMenuItem = new TMCMenuItem(jsonObject1);
                                    if (newtmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                                        continue;
                                    } */
                                    Boolean oldavailability = itemAvailabilityMap.get(menuitemkey);

                                    if (oldavailability == null) {
                                        // Do nothing
                                    } else {
                                        // Log.d(TAG, "oldavailability "+oldavailability.booleanValue()+ " newavailability "+newtmcMenuItem.isItemavailability());
                                        if (newmenuitemavl == oldavailability.booleanValue()) {
                                            // Do nothing
                                        } else {
                                            refreshShoppingBagAdapterAndRemoveItem(menuitemkey);
                                            fillItemTotalValues();
                                            TMCMenuItem newtmcMenuItem = TMCMenuItemCatalog.getInstance().getTMCMenuItem(menuitemkey);
                                            showItemNotAvailableAlert(newtmcMenuItem.getItemname());
                                        }
                                    }
                                }
                            }
                            hideLoadingAnim();
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
                        hideLoadingAnim();
                    }
                }) {
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

    private void getTMCMenuItemAvailability() {
        if ((itemAvailabilityMap == null) || (itemAvailabilityMap.size() <= 0)) {
            return;
        }
        showLoadingAnim();
        // String url = TMCRestClient.AWS_GETTMCMENUITEMS + "?storeid=" + vendorkey + "&subctgyid=" + selectedtmcsubctgykey;
        String url = TMCRestClient.AWS_GETTMCMENUITEMSFORSTORE + "?storeid=" + vendorkey;
        Log.d(TAG, "getTMCMenuItems url " + url);
        if (settingsUtil == null) {
            settingsUtil = new SettingsUtil(this);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        // Log.d(TAG, "Volley getTMCMenuItemAvailability response "+response.toString());
                        try {
                            String message = response.getString("message");

                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TMCMenuItem newtmcMenuItem = new TMCMenuItem(jsonObject1);
                                    if (newtmcMenuItem.getMenutype().equalsIgnoreCase(TMCMenuItem.MENUTYPE_MARINADE)) {
                                        continue;
                                    }
                                    Boolean oldavailability = itemAvailabilityMap.get(newtmcMenuItem.getKey());

                                    if (oldavailability == null) {
                                        // Do nothing
                                    } else {
                                        // Log.d(TAG, "oldavailability "+oldavailability.booleanValue()+ " newavailability "+newtmcMenuItem.isItemavailability());
                                        if (newtmcMenuItem.isItemavailability(settingsUtil.isInventoryCheck())
                                                == oldavailability.booleanValue()) {
                                            // Do nothing
                                        } else {
                                            refreshShoppingBagAdapterAndRemoveItem(newtmcMenuItem.getKey());
                                            fillItemTotalValues();
                                            showItemNotAvailableAlert(newtmcMenuItem.getItemname());
                                        }
                                    }
                                }
                            }

                            Log.d(TAG, "hideloadinganim");
                            hideLoadingAnim();
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
                        hideLoadingAnim();
                    }
                }) {
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

    private void showItemNotAvailableAlert(String menuitemname) {
        String alerttitle = getResources().getString(R.string.itemnotavl_title);
        String alertmessage = getResources().getString(R.string.itemnotavl_alertmessage1) + "  '" + menuitemname + "'  " +
                getResources().getString(R.string.itemnotavl_alertmessage2);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(OrderSummaryActivity.this, alerttitle, alertmessage,
                        getResources().getString(R.string.ok_capt), "",
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {

                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void fetchDistanceFromAWS(TMCUserAddress tmcUserAddress) {
        String url = TMCRestClient.AWS_GETADDRESS_FROMKEY + tmcUserAddress.getKey();
        // Log.d(TAG, "getAddressDetailsFromAWS url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response " + response.toString());
                        try {
                            // Log.d(TAG, "getAddressDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Object deldistanceobj = jsonObject1.get("deliverydistance");
                                    if (deldistanceobj instanceof Double) {
                                        double deliverydistance = jsonObject1.getDouble("deliverydistance");
                                        tmcUserAddress.setDeliverydistance(deliverydistance);
                                        settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
                                        userdeliverydistance = deliverydistance;
                                    }
                                }
                                String action = "deldistance:" + userdeliverydistance + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("DelDistancefetchedOrderSummaryAct", action, settingsUtil.getMobile());
                                getDeliverySlotDetails(vendorkey);
                                getDeliveryslabdetails(vendorkey);
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
                    }
                }) {
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

    private void getDeliveryslabdetails(String vendorkey) {
        String url = TMCRestClient.AWS_GETDELIVERYSLABDETAILS + "?storeid=" + vendorkey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Deliveryslabdetails deliveryslabdetails = new Deliveryslabdetails(jsonObject1);
                                    boolean isrange = deliveryslabdetails.isRange(userdeliverydistance);
                                    if (isrange) {
                                        mappeddeliveryslabdetails = deliveryslabdetails;
                                        continue;
                                    }
                                }
                                fillItemTotalValues();
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
                    }
                }) {
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

    private Tracker getTmcTracker() {
        if (tmcTracker == null) {
            tmcTracker = ((TMCApplication) getApplication()).getDefaultTracker();
        }
        return tmcTracker;
    }

    private Tracker tmcTracker;

    private void logEventInGAnalytics(String category, String action, String label) {
        try {
            String label1 = label.substring(2);
            if (tmcTracker == null) {
                tmcTracker = ((TMCApplication) getApplication()).getDefaultTracker();
            }
            tmcTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action).setLabel(label1)
                    .build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String generateOrderid() {
        return "" + System.nanoTime();
    }

    private void showOrderConfirmLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                orderconfirmmask_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(animationListener);
        bottomUp.setDuration(300);
        orderconfirm_layout.startAnimation(bottomUp);
        orderconfirm_layout.setVisibility(View.VISIBLE);
    }

    private void hideOrderConfirmLayout() {
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                orderconfirm_layout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setAnimationListener(animationListener);
        bottomDown.setDuration(200);
        orderconfirm_layout.startAnimation(bottomDown);
        orderconfirmmask_layout.setVisibility(View.GONE);
    }

    private void showApplyCouponLayout() {
        couponapplied_layout.setVisibility(View.GONE);
        applycoupon_layout.setVisibility(View.VISIBLE);
    }

    private void showCouponAppliedLayout(double couponamount, String couponcode) {
        String rs = getResources().getString(R.string.Rs);
        String couponamtstr = String.format("%.2f", couponamount);
        String text = rs + couponamtstr + " savings with " + couponcode + " coupon";
        coupondetails_textview.setText(text);
        couponapplied_layout.setVisibility(View.VISIBLE);
        applycoupon_layout.setVisibility(View.GONE);
    }

    private boolean isItemDespPresentInJSON(String itemdesp) {
        try {
            JSONObject jsonObject = new JSONObject(itemdesp);
            boolean isitemdespavl = jsonObject.isNull("itemdesp");
            return !isitemdespavl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void showItemsNotAddedAlertAndClearCart() {
        String title = getResources().getString(R.string.servererror_alert);
        String message = getResources().getString(R.string.orderfailure_alert) + " " + settingsUtil.getSupportMobileNo();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(OrderSummaryActivity.this, title, message,
                        getResources().getString(R.string.ok_capt), "",
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
                                Intent intent = new Intent();
                                intent.putExtra("quantitychanged", true);
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void computeCouponDiscountAmount(Coupondetails coupondetails, boolean showalertmessage) {
        if (coupondetails == null) {
            return;
        }
        if (coupondetails.getType().equalsIgnoreCase(Coupondetails.TYPE_FLATPERCENT)) {
            double minordervalue = coupondetails.getMinimumordervalue();
            if (totalpayableamount < minordervalue) {
                // show min order value alert
                couponalreadyapplied = false;
                if (couponamount > 0) {   // Remove existing coupon
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    String alertmessage = getResources().getString(R.string.couponcartvalueless_alertmessage) + " Rs." + minordervalue;
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), alertmessage);
                }
                return;
            }
            boolean isMeatProductsAvlInCart = TMCMenuItemCatalog.getInstance().
                    isMeatProductsAvailableInCart(settingsUtil.getFreshproducectgykey());
            if (!isMeatProductsAvlInCart) {
                couponalreadyapplied = false;
                if (couponamount > 0) {  // Remove existing coupon
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    String alertmessage = getResources().getString(R.string.couponmeatnotavl_alertmessage);
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), alertmessage);
                }
                return;
            }
            double discountpercentage = coupondetails.getValue();
            double discountordervalue = totalpayableamount * (discountpercentage / 100);
            double maxdiscountvalue = coupondetails.getMaxdiscountvalue();
            if (discountordervalue > maxdiscountvalue) {
                couponamount = maxdiscountvalue;
            } else {
                couponamount = discountordervalue;
            }
            couponkey = coupondetails.getKey();
            coupontype = coupondetails.getType();
            int couponvalue = (int) coupondetails.getValue();
            couponamounttext = "Coupon discount (" + couponvalue + "%)";
            showCouponAppliedLayout(couponamount, coupondetails.getCouponcode());
            fillItemTotalValues();
            String alertmessage = "Congrats! " + "Your coupon '" + coupondetails.getCouponcode() + "' applied successfully. ";
            showTMCAlert(getResources().getString(R.string.couponapplied_alerttitle), alertmessage);
        } else if (coupondetails.getType().equalsIgnoreCase(Coupondetails.TYPE_FLATVALUE)) {
            double minordervalue = coupondetails.getMinimumordervalue();
            if (totalpayableamount < minordervalue) {
                // show min order value alert
                couponalreadyapplied = false;
                if (couponamount > 0) {   // Remove existing coupon
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    String alertmessage = getResources().getString(R.string.couponcartvalueless_alertmessage) + " Rs." + minordervalue;
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), alertmessage);
                }
                return;
            }
            boolean isMeatProductsAvlInCart = TMCMenuItemCatalog.getInstance().
                    isMeatProductsAvailableInCart(settingsUtil.getFreshproducectgykey());
            if (!isMeatProductsAvlInCart) {
                couponalreadyapplied = false;
                if (couponamount > 0) {   // Remove existing coupon
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    String alertmessage = getResources().getString(R.string.couponmeatnotavl_alertmessage);
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), alertmessage);
                }
                return;
            }
            double discountvalue = coupondetails.getValue();
            couponamount = discountvalue;
            coupontype = coupondetails.getType();
            couponkey = coupondetails.getKey();
            couponamounttext = "Coupon discount";
            showCouponAppliedLayout(couponamount, coupondetails.getCouponcode());
            fillItemTotalValues();
            String alertmessage = "Congrats! " + "Your coupon '" + coupondetails.getCouponcode() + "' applied successfully. ";
            showTMCAlert(getResources().getString(R.string.couponapplied_alerttitle), alertmessage);
        } else if (coupondetails.getType().equalsIgnoreCase(Coupondetails.TYPE_SKUDISCOUNT)) {
            try {
                String skudetailsstr = coupondetails.getSkudetails();
                JSONObject skudetailsjson = new JSONObject(skudetailsstr);
                String discounttype = skudetailsjson.getString("discounttype");
                if (discounttype.equalsIgnoreCase("SKUMAPPINGDISCOUNT")) {
                    applySkuMappingCouponDetails(coupondetails, showalertmessage);
                } else if (discounttype.equalsIgnoreCase("SKUFLATVALUE")) {
                    applySkuFlatDiscountCouponDetails(coupondetails, showalertmessage);
                } else if (discounttype.equalsIgnoreCase("SKUFLATPERCENTAGE")) {
                    applySKUFlatPercentageCouponDetails(coupondetails, showalertmessage);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void applySkuMappingCouponDetails(Coupondetails coupondetails, boolean showalertmessage) {
        try {
            ArrayList<TMCMenuItem> menuItems = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
            if ((menuItems == null) || (menuItems.size() <= 0)) {
                return;
            }
            String skusubctgy = ""; JSONArray skumenuitems = null;
            double actualdiscountedamount = 0;
            String skudetailsstr = coupondetails.getSkudetails();
            JSONObject skudetailsjson = new JSONObject(skudetailsstr);
            if (skudetailsjson.has("tmcsubctgykey")) {
                skusubctgy = skudetailsjson.getString("tmcsubctgykey");
            }
            if (skudetailsjson.has("menuitemkeys")) {
                skumenuitems = skudetailsjson.getJSONArray("menuitemkeys");
            }
            String discountedmenuitem = skudetailsjson.getString("discountedmenuitemkey");
            String discountedpricestr = skudetailsjson.getString("discountedprice");

         /* double mingrosswtingrams = 0;
            if (skudetailsjson.has("mingrosswtingrams")) {
                mingrosswtingrams = skudetailsjson.getDouble("mingrosswtingrams");
            } */

            double minimumitemsvalue = 0;
            if (skudetailsjson.has("minimumitemsvalue")) {
                minimumitemsvalue = skudetailsjson.getDouble("minimumitemsvalue");
            }
            double discountedprice = Double.parseDouble(discountedpricestr);
            boolean isskumenuitemavl = false;
            double cartitemsvalue = 0;
            for (int i = 0; i < menuItems.size(); i++) {
                TMCMenuItem tmcMenuItem = menuItems.get(i);
                if ((skusubctgy != null) && !(TextUtils.isEmpty(skusubctgy))) {
                    if (skusubctgy.equalsIgnoreCase(tmcMenuItem.getTmcsubctgykey())) {
                        cartitemsvalue = cartitemsvalue + (tmcMenuItem.getSelectedqty() * tmcMenuItem.getTmcprice());
                     // isskumenuitemavl = true;
                    }
                } else {
                    if (skumenuitems != null) {
                        for (int j=0; j<skumenuitems.length(); j++) {
                            String skumenuitemkey = (String)skumenuitems.get(j);
                            if (skumenuitemkey.equalsIgnoreCase(tmcMenuItem.getAwsKey())) {
                                cartitemsvalue = cartitemsvalue + (tmcMenuItem.getSelectedqty() * tmcMenuItem.getTmcprice());

                             /* if (mingrosswtingrams > 0) {
                                    if (tmcMenuItem.getGrossweightingrams() >= mingrosswtingrams) {
                                        isskumenuitemavl = true;
                                    }
                                } else {
                                    isskumenuitemavl = true;
                                } */
                            }
                        }
                    }
                }

                if (discountedmenuitem.equalsIgnoreCase(tmcMenuItem.getAwsKey())) {
                    actualdiscountedamount = tmcMenuItem.getTmcprice() - discountedprice;
                }
            }

            if (cartitemsvalue >= minimumitemsvalue) {
                isskumenuitemavl = true;
            } else {
                couponalreadyapplied = false;
                if (couponamount > 0) {
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                }
                return;
            }

            if (isskumenuitemavl && (actualdiscountedamount > 0)) {
                couponkey = coupondetails.getKey();
                coupontype = coupondetails.getType();
                couponamount = actualdiscountedamount;
                couponamounttext = "Coupon discount";
                showCouponAppliedLayout(couponamount, coupondetails.getCouponcode());
                fillItemTotalValues();
                if (showalertmessage) {
                    String alertmessage = "Congrats! " + "Your coupon '" + coupondetails.getCouponcode() + "' applied successfully. ";
                    showTMCAlert(getResources().getString(R.string.couponapplied_alerttitle), alertmessage);
                }
            } else {
                couponalreadyapplied = false;
                if (couponamount > 0) {
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                }
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void applySKUFlatPercentageCouponDetails(Coupondetails coupondetails, boolean showalertmessage) {
        try {
            ArrayList<TMCMenuItem> menuItems = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
            if ((menuItems == null) || (menuItems.size() <= 0)) {
                return;
            }
            String skusubctgy = null;
            JSONObject skudetailsjson = new JSONObject(coupondetails.getSkudetails());
            JSONArray skumenuitemsjsonarray = null;
            if (skudetailsjson.has("menuitemkeys")) {
                skumenuitemsjsonarray = skudetailsjson.getJSONArray("menuitemkeys");
            }
            if (skudetailsjson.has("tmcsubctgykey")) {
                skusubctgy = skudetailsjson.getString("tmcsubctgykey");
            }

            ArrayList<String> couponmenuitemkeys = new ArrayList<>();
            if (skumenuitemsjsonarray != null) {
                for (int i=0; i<skumenuitemsjsonarray.length(); i++) {
                    couponmenuitemkeys.add((String)skumenuitemsjsonarray.get(i));
                }
            }
            double minimumitemsvalue = 0; double discountpercentage = 0;
            double maxdiscountvalue = 0;
            if (skudetailsjson.has("minimumitemsvalue")) {
                minimumitemsvalue = skudetailsjson.getDouble("minimumitemsvalue");
            }
            if (skudetailsjson.has("discountvalue")) {
                discountpercentage = skudetailsjson.getDouble("discountvalue");
            }
            if (skudetailsjson.has("maxdiscountvalue")) {
                maxdiscountvalue = skudetailsjson.getDouble("maxdiscountvalue");
            }

            boolean menuitemsnotavlincoupon = ((couponmenuitemkeys == null) || (couponmenuitemkeys.size() <= 0)) &&
                                               ((skusubctgy == null) || (TextUtils.isEmpty(skusubctgy)));
            if (menuitemsnotavlincoupon) {
                couponalreadyapplied = false;
                if (couponamount > 0) {  // Remove existing coupon
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                }
                return;
            } else {
                double ordervalueforcouponcomputation = 0;
                boolean isskusubctgyavl = false;
                isskusubctgyavl = ((skusubctgy != null) && (!TextUtils.isEmpty(skusubctgy)));
                for (int i = 0; i < menuItems.size(); i++) {
                    TMCMenuItem tmcMenuItem = menuItems.get(i);
                    if (isskusubctgyavl) {
                        if (skusubctgy.equalsIgnoreCase(tmcMenuItem.getTmcsubctgykey())) {
                            double itemprice = tmcMenuItem.getTmcprice() * tmcMenuItem.getSelectedqty();
                            ordervalueforcouponcomputation = ordervalueforcouponcomputation + itemprice;
                        }
                    } else {
                        if (couponmenuitemkeys.contains(tmcMenuItem.getAwsKey())) {
                            double itemprice = tmcMenuItem.getTmcprice() * tmcMenuItem.getSelectedqty();
                            ordervalueforcouponcomputation = ordervalueforcouponcomputation + itemprice;
                        }
                    }
                }
                if (ordervalueforcouponcomputation <= 0) {
                    couponalreadyapplied = false;
                    if (couponamount > 0) {
                        couponamount = 0;
                        appliedCouponDetails = null;
                        showApplyCouponLayout();
                        fillItemTotalValues();
                    }
                    if (showalertmessage) {
                        showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                    }
                    return;
                }

                if (ordervalueforcouponcomputation < minimumitemsvalue) {
                    // show min order value alert
                    couponalreadyapplied = false;
                    if (couponamount > 0) {
                        couponamount = 0;
                        appliedCouponDetails = null;
                        showApplyCouponLayout();
                        fillItemTotalValues();
                    }
                    if (showalertmessage) {
                        showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                    }
                    return;
                }

                // Log.d(TAG, "ordervalueforcouponcomputation "+ordervalueforcouponcomputation);
                double discountordervalue = ordervalueforcouponcomputation * (discountpercentage / 100);
                if (discountordervalue > maxdiscountvalue) {
                    couponamount = maxdiscountvalue;
                } else {
                    couponamount = discountordervalue;
                }
                couponkey = coupondetails.getKey();
                coupontype = coupondetails.getType();
                int couponvalue = (int) coupondetails.getValue();
                String discpercentage = String.format("%.0f", discountpercentage);
                couponamounttext = "Coupon discount (" + discpercentage + "%)";
                showCouponAppliedLayout(couponamount, coupondetails.getCouponcode());
                fillItemTotalValues();
                if (showalertmessage) {
                    String alertmessage = "Congrats! " + "Your coupon '" + coupondetails.getCouponcode() + "' applied successfully. ";
                    showTMCAlert(getResources().getString(R.string.couponapplied_alerttitle), alertmessage);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void applySkuFlatDiscountCouponDetails(Coupondetails coupondetails, boolean showalertmessage) {
        try {
            ArrayList<TMCMenuItem> menuItems = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
            if ((menuItems == null) || (menuItems.size() <= 0)) {
                return;
            }
            String skusubctgy = null;
            JSONObject skudetailsjson = new JSONObject(coupondetails.getSkudetails());
            JSONArray skumenuitemsjsonarray = null;
            if (skudetailsjson.has("menuitemkeys")) {
                skumenuitemsjsonarray = skudetailsjson.getJSONArray("menuitemkeys");
            }

            if (skudetailsjson.has("tmcsubctgykey")) {
                skusubctgy = skudetailsjson.getString("tmcsubctgykey");
            }

            ArrayList<String> couponmenuitemkeys = new ArrayList<>();
            if (skumenuitemsjsonarray != null) {
                for (int i=0; i<skumenuitemsjsonarray.length(); i++) {
                    couponmenuitemkeys.add((String)skumenuitemsjsonarray.get(i));
                }
            }
            double minimumitemsvalue = 0; double discountvalue = 0;
            double mingrosswtingrams = 0;
            if (skudetailsjson.has("minimumitemsvalue")) {
                minimumitemsvalue = skudetailsjson.getDouble("minimumitemsvalue");
            }
            if (skudetailsjson.has("discountvalue")) {
                discountvalue = skudetailsjson.getDouble("discountvalue");
            }
         /* if (skudetailsjson.has("mingrosswtingrams")) {
                mingrosswtingrams = skudetailsjson.getDouble("mingrosswtingrams");
            } */

            if (couponmenuitemkeys == null) {
                couponalreadyapplied = false;
                if (couponamount > 0) {
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                }
                return;
            } else {
                double ordervalueforcouponcomputation = 0;
                boolean isskusubctgyavl = false;
                isskusubctgyavl = ((skusubctgy != null) && (!TextUtils.isEmpty(skusubctgy)));
                for (int i = 0; i < menuItems.size(); i++) {
                    TMCMenuItem tmcMenuItem = menuItems.get(i);
                    if (isskusubctgyavl) {
                        if (skusubctgy.equalsIgnoreCase(tmcMenuItem.getTmcsubctgykey())) {
                            double itemprice = tmcMenuItem.getTmcprice() * tmcMenuItem.getSelectedqty();
                            ordervalueforcouponcomputation = ordervalueforcouponcomputation + itemprice;
                        }
                    } else {
                        if (couponmenuitemkeys.contains(tmcMenuItem.getAwsKey())) {
                            double itemprice = tmcMenuItem.getTmcprice() * tmcMenuItem.getSelectedqty();
                            ordervalueforcouponcomputation = ordervalueforcouponcomputation + itemprice;
                        }
                    }
                }
                if (ordervalueforcouponcomputation <= 0) {
                    couponalreadyapplied = false;
                    if (couponamount > 0) {
                        couponamount = 0;
                        appliedCouponDetails = null;
                        showApplyCouponLayout();
                        fillItemTotalValues();
                    }
                    if (showalertmessage) {
                        showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                    }
                    return;
                }

                if (ordervalueforcouponcomputation < minimumitemsvalue) {
                    // show min order value alert
                    couponalreadyapplied = false;
                    if (couponamount > 0) {
                        couponamount = 0;
                        appliedCouponDetails = null;
                        showApplyCouponLayout();
                        fillItemTotalValues();
                    }
                    if (showalertmessage) {
                        showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                    }
                    return;
                }

             /* if (ordervalueforcouponcomputation >= discountvalue) {
                    couponamount = discountvalue;
                } else {
                    couponamount = ordervalueforcouponcomputation;
                } */

                couponamount = discountvalue;

                coupontype = coupondetails.getType();
                couponkey = coupondetails.getKey();
                couponamounttext = "Coupon discount";
                showCouponAppliedLayout(couponamount, coupondetails.getCouponcode());
                fillItemTotalValues();
                if (showalertmessage) {
                    String alertmessage = "Congrats! " + "Your coupon '" + coupondetails.getCouponcode() + "' applied successfully. ";
                    showTMCAlert(getResources().getString(R.string.couponapplied_alerttitle), alertmessage);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkForDuplicateOrders(ArrayList<TMCMenuItem> tmcMenuItems) {
        String lastordermenuitems = settingsUtil.getLastPlacedOrderMenuItems();
        boolean isduplicateorder = false;
        if ((lastordermenuitems == null) || (TextUtils.isEmpty(lastordermenuitems))) {
            return;
        }
        View duplicateorder_layout = findViewById(R.id.duplicateorder_layout);
        View duplicateordermask_layout = findViewById(R.id.duplicateordermask_layout);
        View duplicateordernobtn_layout = findViewById(R.id.duplicateordernobtn_layout);
        View duplicateorderyesbtn_layout = findViewById(R.id.duplicateorderyesbtn_layout);

        try {
            JSONObject jsonObject = new JSONObject(lastordermenuitems);
            String lastorderplacedtime = jsonObject.getString("orderplacedtime");
            String lastordermenuitemids = jsonObject.getString("menuitemids");
            String orderid = jsonObject.getString("orderid");
            long difftime = TMCUtil.getInstance().getDifferenceTime(lastorderplacedtime, TMCUtil.getInstance().getCurrentTime());
            if (difftime <  7200000) {  // 2 hours
                for (int i=0; i<tmcMenuItems.size(); i++) {
                    String newmenuitemid = tmcMenuItems.get(i).getAwsKey();
                    if (lastordermenuitemids.contains(newmenuitemid)) {
                        isduplicateorder = true;
                    }
                }
            } else {
                return;
            }
            if (isduplicateorder) {
                duplicateorderyesbtn_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDetailOrderActivity(orderid);
                        duplicateorder_layout.setVisibility(View.GONE);
                        duplicateordermask_layout.setVisibility(View.GONE);
                    }
                });
                duplicateordernobtn_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        duplicateorder_layout.setVisibility(View.GONE);
                        duplicateordermask_layout.setVisibility(View.GONE);
                    }
                });
                duplicateordermask_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    public void run() {
                        duplicateorder_layout.setVisibility(View.VISIBLE);
                        duplicateordermask_layout.setVisibility(View.VISIBLE);
                    }
                }, 500);


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startDetailOrderActivity(String orderid) {
        if ((orderid == null) || (TextUtils.isEmpty(orderid))) {
            return;
        }
        Intent intent = new Intent(OrderSummaryActivity.this, DetailOrderActivity.class);
        intent.putExtra("orderid", orderid);
        startActivityForResult(intent, DETAILORDER_ACT_REQ_CODE);
    }

/*
else if (coupondetails.getType().equalsIgnoreCase(Coupondetails.TYPE_SKUFLATPERCENT)) {
            ArrayList<TMCMenuItem> menuItems = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
            if ((menuItems == null) || (menuItems.size() <= 0)) {
                return;
            }
            ArrayList<String> couponmenuitemkeys = coupondetails.getMenuItemKeysList();
            // Log.d(TAG, "couponmenuitemkeys "+couponmenuitemkeys);
            if (couponmenuitemkeys == null) {
                couponalreadyapplied = false;
                if (couponamount > 0) {  // Remove existing coupon
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                }
                return;
            } else {
                double ordervalueforcouponcomputation = 0;
                for (int i = 0; i < menuItems.size(); i++) {
                    TMCMenuItem tmcMenuItem = menuItems.get(i);
                    if (couponmenuitemkeys.contains(tmcMenuItem.getAwsKey())) {
                        double itemprice = tmcMenuItem.getTmcprice() * tmcMenuItem.getSelectedqty();
                        ordervalueforcouponcomputation = ordervalueforcouponcomputation + itemprice;
                    }
                }
                if (ordervalueforcouponcomputation <= 0) {
                    couponalreadyapplied = false;
                    if (couponamount > 0) {
                        couponamount = 0;
                        appliedCouponDetails = null;
                        showApplyCouponLayout();
                        fillItemTotalValues();
                    }
                    if (showalertmessage) {
                        showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                    }
                    return;
                }

                double minordervalue = coupondetails.getMinimumordervalue();
                if (ordervalueforcouponcomputation < minordervalue) {
                    // show min order value alert
                    couponalreadyapplied = false;
                    if (couponamount > 0) {
                        couponamount = 0;
                        appliedCouponDetails = null;
                        showApplyCouponLayout();
                        fillItemTotalValues();
                    }
                    if (showalertmessage) {
                        String alertmessage = getResources().getString(R.string.couponitemvalueless_alertmessage) + " Rs." + minordervalue;
                        showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), alertmessage);
                    }
                    return;
                }

                // Log.d(TAG, "ordervalueforcouponcomputation "+ordervalueforcouponcomputation);
                double discountpercentage = coupondetails.getValue();
                double discountordervalue = ordervalueforcouponcomputation * (discountpercentage / 100);
                double maxdiscountvalue = coupondetails.getMaxdiscountvalue();
                if (discountordervalue > maxdiscountvalue) {
                    couponamount = maxdiscountvalue;
                } else {
                    couponamount = discountordervalue;
                }
                couponkey = coupondetails.getKey();
                coupontype = coupondetails.getType();
                int couponvalue = (int) coupondetails.getValue();
                couponamounttext = "Coupon discount (" + couponvalue + "%)";
                showCouponAppliedLayout(couponamount, coupondetails.getCouponcode());
                fillItemTotalValues();
                String alertmessage = "Congrats! " + "Your coupon '" + coupondetails.getCouponcode() + "' applied successfully. ";
                showTMCAlert(getResources().getString(R.string.couponapplied_alerttitle), alertmessage);
            }
        } else if (coupondetails.getType().equalsIgnoreCase(Coupondetails.TYPE_SKUFLATVALUE)) {
            ArrayList<TMCMenuItem> menuItems = TMCMenuItemCatalog.getInstance().getCartMenuItemList();
            if ((menuItems == null) || (menuItems.size() <= 0)) {
                return;
            }
            ArrayList<String> couponmenuitemkeys = coupondetails.getMenuItemKeysList();
            if (couponmenuitemkeys == null) {
                couponalreadyapplied = false;
                if (couponamount > 0) {
                    couponamount = 0;
                    appliedCouponDetails = null;
                    showApplyCouponLayout();
                    fillItemTotalValues();
                }
                if (showalertmessage) {
                    showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                }
                return;
            } else {
                double ordervalueforcouponcomputation = 0;
                for (int i = 0; i < menuItems.size(); i++) {
                    TMCMenuItem tmcMenuItem = menuItems.get(i);
                    if (couponmenuitemkeys.contains(tmcMenuItem.getAwsKey())) {
                        double itemprice = tmcMenuItem.getTmcprice() * tmcMenuItem.getSelectedqty();
                        ordervalueforcouponcomputation = ordervalueforcouponcomputation + itemprice;
                    }
                }
                if (ordervalueforcouponcomputation <= 0) {
                    couponalreadyapplied = false;
                    if (couponamount > 0) {
                        couponamount = 0;
                        appliedCouponDetails = null;
                        showApplyCouponLayout();
                        fillItemTotalValues();
                    }
                    if (showalertmessage) {
                        showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), coupondetails.getAlertmessage());
                    }
                    return;
                }

                double minordervalue = coupondetails.getMinimumordervalue();
                if (ordervalueforcouponcomputation < minordervalue) {
                    // show min order value alert
                    couponalreadyapplied = false;
                    if (couponamount > 0) {
                        couponamount = 0;
                        appliedCouponDetails = null;
                        showApplyCouponLayout();
                        fillItemTotalValues();
                    }
                    if (showalertmessage) {
                        String alertmessage = getResources().getString(R.string.couponitemvalueless_alertmessage) + " Rs." + minordervalue;
                        showTMCAlert(getResources().getString(R.string.couponnotapplied_alerttitle), alertmessage);
                    }
                    return;
                }

                double discountvalue = coupondetails.getValue();
                if (ordervalueforcouponcomputation >= discountvalue) {
                    couponamount = discountvalue;
                } else {
                    couponamount = ordervalueforcouponcomputation;
                }

                coupontype = coupondetails.getType();
                couponkey = coupondetails.getKey();
                couponamounttext = "Coupon discount";
                showCouponAppliedLayout(couponamount, coupondetails.getCouponcode());
                fillItemTotalValues();
                String alertmessage = "Congrats! " + "Your coupon '" + coupondetails.getCouponcode() + "' applied successfully. ";
                showTMCAlert(getResources().getString(R.string.couponapplied_alerttitle), alertmessage);
            }
        }


 */


}