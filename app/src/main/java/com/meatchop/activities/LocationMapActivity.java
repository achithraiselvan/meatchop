package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import cz.msebera.android.httpclient.Header;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.SphericalUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.services.CurrentLocationService;
import com.meatchop.services.FetchAddressIntentService;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.utils.TMCconstants;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCDataCatalog;
import com.meatchop.widget.TMCEditText;
import com.meatchop.widget.TMCMapFragment;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import android.os.ResultReceiver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class LocationMapActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private final int ADDRESSTYPE_HOME = 1;
    private final int ADDRESSTYPE_OTHERS = 2;
    String CustomerLongitude = "0", CustomerLatitude = "0";
    double Latitude, Longitude;
    Marker marker;
    private static final String TAG = "LocationMapActivity";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    RelativeLayout imgLocationPinUp;

    GoogleMap map;
    ResultReceiver resultReceiver;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private TMCTextView addressdetails_text;
    private View changelocation_layout;
    private View back_button;
    private TMCEditText flatblockno_text;
    private TMCEditText landmark_text;
    private TMCEditText pincode_text;
    private View hometag_layout;
    private FrameLayout hometag_seperatorline;
    private View others_layout;
    private TMCEditText othertag_text;
    private FrameLayout othertag_seperatorline;
    private View confirmlocation_layout;
    private RelativeLayout map_relativelayout;
    private LatLng CustomerLatlng;
    private TMCEditText addressline_edittext;

    private BottomSheetBehavior bottomSheetBehavior;
    private int normal_size_of_layout;
    private int peek_height_of_bottom_sheet;
    private int exact_size_of_layout;
    private boolean isOpen = false;
    private SettingsUtil settingsUtil;

    private int completedlocationapicalls = 0;
 // private String deliverableVendorKey;
 // private String deliverableVendorName;
    private TMCVendor deliverableVendor;
    private boolean isHomeTagSelected = false;
    private LatLng deliverableLatLng;
    private int defaultaddresstype = 0;
    private View loadinganimmask_layout;
    private View loadinganim_layout;

    private boolean defaultaddressalreadyset;

    private TMCUserAddress parsedTMCUserAddress;
    private boolean callfromprofileactivity;
    private boolean isLocationSaved = false;
    private double deliverydistance = 0;


    private String deliverydistinstring;
    private String lastMappedVendorName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location_map);

        if (getPackageName().equals("com.meatchop")) {
            parsedTMCUserAddress = getIntent().getParcelableExtra("tmcuseraddress");
            callfromprofileactivity = getIntent().getBooleanExtra("callfromprofileactivity", false);
            CustomerLongitude = getIntent().getStringExtra("CustomerLongitude");
            CustomerLatitude = getIntent().getStringExtra("CustomerLatitude");
            defaultaddressalreadyset = getIntent().getBooleanExtra("defaultaddressalreadyset", false);
         // Log.d(TAG, "defaultaddressalreadyset "+defaultaddressalreadyset);
            Latitude = getIntent().getDoubleExtra("latitude", 0);
            Longitude = getIntent().getDoubleExtra("longitude", 0);
        }

        resultReceiver = new AddressResultReceiver(new Handler());
        imgLocationPinUp = (RelativeLayout) findViewById(R.id.imgLocationPinUp);
        map_relativelayout = findViewById(R.id.map_relativelayout);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(this);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout.setOnClickListener(this);

        settingsUtil = new SettingsUtil(this);

        try {
            String defaultaddress = settingsUtil.getDefaultAddress();
            if ((defaultaddress != null) && !(TextUtils.isEmpty(defaultaddress))) {
                TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
                lastMappedVendorName = tmcUserAddress.getVendorname();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

     // Latitude = Double.parseDouble(Objects.requireNonNull(CustomerLatitude));
     // Longitude = Double.parseDouble(CustomerLongitude);


        if (callfromprofileactivity && (parsedTMCUserAddress != null)) {
            Latitude = parsedTMCUserAddress.getLocationlat();
            Longitude = parsedTMCUserAddress.getLocationlong();
        }
     // Log.d(TAG, "latitude "+Latitude+ " longitude "+Longitude);

        String apiKey = getResources().getString(R.string.google_map_api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        initComponent();
        CustomerLatlng = new LatLng(Latitude, Longitude);
        getVendorDetails();

     /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(LocationMapActivity.this); */
     // Objects.requireNonNull(mapFragment).getMapAsync(LocationMapActivity.this);

        TMCMapFragment mapFragment = (TMCMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);

     // ScrollView mscrollview = (ScrollView) findViewById(R.id.mscrolliew);
        mapFragment.setListener(new TMCMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {

            }
        });
        Objects.requireNonNull(mapFragment).getMapAsync(LocationMapActivity.this);

        KeyboardVisibilityEvent.setEventListener(
                (LocationMapActivity.this),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpenn) {

                     // Log.d("ur", String.valueOf(isOpen));
                        if(isOpenn){
                            isOpen = true;
                            if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
                                Log.e("KeyboardLog", "STATE_EXPANDED");
                                setMapPaddingBotttom(1.0f, (float) (normal_size_of_layout + peek_height_of_bottom_sheet));
                            }
                            if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED)
                            {
                                Log.e("KeyboardLog", "STATE_Colapsed");
                                setMapPaddingBotttom(1.0f, (float) (normal_size_of_layout - peek_height_of_bottom_sheet));
                            }

                        }
                        else {
                            isOpen=false;
                            Log.e("KeyboardLog", "Elseclosed");

                            if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
                                Log.e("Keyboard", "Else_STATE_EXPANDED");
                                setMapPaddingBotttom(1.0f,
                                        (float) (normal_size_of_layout - peek_height_of_bottom_sheet));
                            }
                            if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED){
                                Log.e("Keyboard", "Else_STATE_COLLAPSED");

                                setMapPaddingBotttom(0.0f, (float) (exact_size_of_layout + peek_height_of_bottom_sheet));
                            }

                        }
                    }
                });

        if ((settingsUtil.getUserkey() == null) || (TextUtils.isEmpty(settingsUtil.getUserkey()))) {
            closeActivityAndSendForceLoginHandler();
        }
    }

    private void closeActivityAndSendForceLoginHandler() {
        Intent intent = new Intent();
        intent.putExtra("forcelogin", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if ((id == R.id.changelocation_layout) || (id == R.id.back_button)) {
            setResult(RESULT_OK);
            finish();
        } else if (id == R.id.hometag_layout) {
            hometag_seperatorline.setBackgroundColor(getResources().getColor(R.color.meatchopbg_red));
            othertag_seperatorline.setBackgroundColor(getResources().getColor(R.color.fieldseperator_color));
            defaultaddresstype = ADDRESSTYPE_HOME;
        } else if (id == R.id.others_layout) {
            defaultaddresstype = ADDRESSTYPE_OTHERS;
        }
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

    private void initComponent() {
        // get the bottom sheet view
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        addressdetails_text = llBottomSheet.findViewById(R.id.addressdetails_text);
        changelocation_layout = llBottomSheet.findViewById(R.id.changelocation_layout);
        changelocation_layout.setOnClickListener(this);

        flatblockno_text = (TMCEditText) llBottomSheet.findViewById(R.id.flatblockno_text);
        landmark_text = (TMCEditText) llBottomSheet.findViewById(R.id.landmark_text);
        pincode_text = (TMCEditText) llBottomSheet.findViewById(R.id.pincode_text);
        addressline_edittext = (TMCEditText) llBottomSheet.findViewById(R.id.addressline_edittext);
        hometag_layout = llBottomSheet.findViewById(R.id.hometag_layout);
        hometag_layout.setOnClickListener(this);
        hometag_seperatorline = (FrameLayout) llBottomSheet.findViewById(R.id.hometag_seperatorline);
        others_layout = llBottomSheet.findViewById(R.id.others_layout);
        others_layout.setOnClickListener(this);
        othertag_text = (TMCEditText) llBottomSheet.findViewById(R.id.othertag_text);
        othertag_seperatorline = (FrameLayout) llBottomSheet.findViewById(R.id.othertag_seperatorline);
        confirmlocation_layout = llBottomSheet.findViewById(R.id.confirmlocation_layout);
        if (defaultaddressalreadyset) {
            hometag_layout.setVisibility(View.GONE);
        } else {
            hometag_layout.setVisibility(View.VISIBLE);
        }

        if (callfromprofileactivity && (parsedTMCUserAddress != null)) {
            changelocation_layout.setVisibility(View.GONE);
            String addressline1 = parsedTMCUserAddress.getAddressline1();
            String addressline2 = parsedTMCUserAddress.getAddressline2();
            String landmark = parsedTMCUserAddress.getLandmark();
            String pincode = parsedTMCUserAddress.getPincode();
            String addresstype = parsedTMCUserAddress.getAddresstype();
            if (addresstype.equalsIgnoreCase("Home")) {
                isHomeTagSelected = true;
                defaultaddresstype = ADDRESSTYPE_HOME;
                othertag_seperatorline.setBackgroundColor(getResources().getColor(R.color.fieldseperator_color));
                hometag_seperatorline.setBackgroundColor(getResources().getColor(R.color.meatchopbg_red));
            } else {
                hometag_seperatorline.setBackgroundColor(getResources().getColor(R.color.fieldseperator_color));
                othertag_seperatorline.setBackgroundColor(getResources().getColor(R.color.meatchopbg_red));
                defaultaddresstype = ADDRESSTYPE_OTHERS;
                othertag_text.setText(addresstype);
            }
            if (addressline1 != null) {
                flatblockno_text.setText(addressline1);
            }
            if (addressline2 != null) {
                addressline_edittext.setText(addressline2);
            }
            if (landmark != null) {
                landmark_text.setText(landmark);
            }
            if (pincode != null) {
                pincode_text.setText(pincode);
            }
        }

        othertag_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    hometag_seperatorline.setBackgroundColor(getResources().getColor(R.color.fieldseperator_color));
                    othertag_seperatorline.setBackgroundColor(getResources().getColor(R.color.meatchopbg_red));
                    defaultaddresstype = ADDRESSTYPE_OTHERS;
                } else {
                    if (!defaultaddressalreadyset) {
                        hometag_seperatorline.setBackgroundColor(getResources().getColor(R.color.meatchopbg_red));
                        defaultaddresstype = ADDRESSTYPE_HOME;
                    }
                    othertag_seperatorline.setBackgroundColor(getResources().getColor(R.color.fieldseperator_color));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        normal_size_of_layout = getResources().getConfiguration().screenHeightDp;
        peek_height_of_bottom_sheet = ((normal_size_of_layout/2)+100);
        bottomSheetBehavior.setPeekHeight(peek_height_of_bottom_sheet);

        peek_height_of_bottom_sheet = normal_size_of_layout - peek_height_of_bottom_sheet;
        exact_size_of_layout = (normal_size_of_layout * 2);
        map_relativelayout.getLayoutParams().height = exact_size_of_layout +peek_height_of_bottom_sheet;
        imgLocationPinUp.getLayoutParams().height = exact_size_of_layout +peek_height_of_bottom_sheet;


     // Log.e("TAG","Normal_Size_of_Layout "+String.valueOf(Normal_Size_of_Layout));
     // Log.e("TAG","Exact_Size_of_Layout "+String.valueOf(Exact_Size_of_Layout));
     // Log.e("TAG","Peek_Heigt_of_Bottom_Sheet  "+String.valueOf(Peek_Heigt_of_Bottom_Sheet));

        // change the state of the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("LongLogTag")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(llBottomSheet.getWindowToken(), 0);
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                     // Log.d("Bottom Sheet Behaviour Log", "STATE_COLLAPSED");
                        setMapPaddingBotttom(0.0f, (float) (exact_size_of_layout +peek_height_of_bottom_sheet));
                     /* ValueAnimator v = ValueAnimator.ofInt(exact_size_of_layout -peek_height_of_bottom_sheet,
                                                              exact_size_of_layout +peek_height_of_bottom_sheet);
                        v.setDuration(200);
                        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                Integer value = (Integer) animation.getAnimatedValue();
                                map_relativelayout.getLayoutParams().height = value.intValue();
                                map_relativelayout.requestLayout();
                            }
                        });
                        v.start(); */
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                     // Log.d("Bottom Sheet Behaviour Log", "STATE_EXPANDED");
                     // setMapPaddingBotttom(1.0f, (float) (exact_size_of_layout +peek_height_of_bottom_sheet));
                        setMapPaddingBotttom(1.0f, (float) (normal_size_of_layout - peek_height_of_bottom_sheet));

                     /* ValueAnimator va = ValueAnimator.ofInt(exact_size_of_layout + peek_height_of_bottom_sheet,
                                                               exact_size_of_layout - peek_height_of_bottom_sheet);
                        va.setDuration(200);
                        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                Integer value = (Integer) animation.getAnimatedValue();
                                map_relativelayout.getLayoutParams().height = value.intValue();
                                map_relativelayout.requestLayout();
                            }
                        });
                        va.start(); */
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.e("TAG","bottomSheet   2   "+String.valueOf(slideOffset));

                switch (bottomSheetBehavior.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        // Log.e("TAG","dragging   2   "+String.valueOf(slideOffset));
                        // maplayout.startAnimation(slideUpAnimation);
                        setMapPaddingBotttom(slideOffset,
                                (float) normal_size_of_layout-peek_height_of_bottom_sheet);
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        //  Log.e("TAG","settling   "+String.valueOf(slideOffset));
                        //   setMapPaddingBotttom(slideOffset, (float) (Normal_Size_of_Layout - Peek_Heigt_of_Bottom_Sheet));
                        break;
                }
            }
        });

        flatblockno_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
             // Log.d(TAG, "flatblockno_text"+ "addTextChangedListener");
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        confirmlocation_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             // Log.d(TAG, "customerlatlng "+CustomerLatlng.toString()+" tmcVendorArrayList "+tmcVendorArrayList);
                String addressline1 = flatblockno_text.getText().toString();
                String addressline2 = addressline_edittext.getText().toString();
                String landmark = landmark_text.getText().toString();
                String otherstagaddresstype = othertag_text.getText().toString();
                String pincode = pincode_text.getText().toString();
                if ((TextUtils.isEmpty(addressline1)) && (TextUtils.isEmpty(addressline2))) {
                    showTMCAlert(R.string.enteraddress_alert);
                    return;
                }
             /* if (TextUtils.isEmpty(addressline2)) {
                    showTMCAlert(R.string.enteraddress_alert);
                    return;
                } */
                if (TextUtils.isEmpty(landmark)) {
                    showTMCAlert(R.string.enterlandmark_alert);
                    return;
                }
                if (TextUtils.isEmpty(pincode)) {
                    showTMCAlert(R.string.enterpincode_alert);
                    return;
                }

                if ((defaultaddresstype == ADDRESSTYPE_HOME) || (defaultaddresstype == ADDRESSTYPE_OTHERS)) {
                    if ((defaultaddresstype == ADDRESSTYPE_OTHERS) && (TextUtils.isEmpty(otherstagaddresstype))) {
                        showTMCAlert(R.string.otherstag_text);
                        return;
                    }
                } else {
                    showTMCAlert(R.string.otherstag_text);
                    return;
                }
                hideKeyboardsofAllEdittexts();
                showLoadingAnim();
             // Log.d(TAG, "CustomerLatlng "+CustomerLatlng+ " tmcVendorArrayList "+tmcVendorArrayList);
                if ((CustomerLatlng != null) && (tmcVendorArrayList != null)) {
                    drawSphericalRound(CustomerLatlng, tmcVendorArrayList, true);
                }

            }
        });


    }

    private void setMapPaddingBotttom(Float offset,Float maxMapPaddingBottom ) {
        //From 0.0 (min) - 1.0 (max)
        // Float  = 170f;
        //left,top,right,bottom
        if (map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLng(CustomerLatlng));
        }
        //  imgLocationPinUp.setPadding(0, 0, 0, Math.round((offset * (maxMapPaddingBottom/2))));

        map_relativelayout.setPadding(0, 0, 0, Math.round((offset * maxMapPaddingBottom)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
     // Log.d(TAG, "onMapReadyMethod");
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        PlaceMarkers(18);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
     // map.moveCamera(CameraUpdateFactory.zoomTo(18));


        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        Latitude = map.getCameraPosition().target.latitude;
                        Longitude = map.getCameraPosition().target.longitude;

                     // Log.d(TAG, "onCameraIdle method");
                        PlaceMarkers(0);

                    }
                });

            }
        });
    }

    private void PlaceMarkers(int zoomToValue) {

        if(marker != null) {
            marker.remove();
        }

        CustomerLatlng = new LatLng(Latitude, Longitude);
        MarkerOptions CustomersMarker = new MarkerOptions();
     // CustomersMarker.position(CustomerLatlng);
        Log.d("placemarker", Latitude + "  " + Longitude);
     // Log.d("placemarker", String.valueOf(CustomersMarker));
     // CustomersMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
     // marker = map.addMarker(CustomersMarker);

        //  CustomersMarker.title("You ");
        ////  CustomersMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pin));

        //  marker = map.addMarker(CustomersMarker);

        if (zoomToValue <= 0) {
            map.moveCamera(CameraUpdateFactory.newLatLng(CustomerLatlng));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(CustomerLatlng, zoomToValue));
        }

        Location location = new Location("providerNA");
        location.setLatitude(Latitude);
        location.setLongitude(Longitude);
        map.setOnCameraIdleListener(null);
        fetchMyLocationsAddress(location);
    }

    private void fetchMyLocationsAddress(Location location) {
        Log.i("tagCustomer", "Location  " + location);

        Intent intent = new Intent(this, FetchAddressIntentService.class);

        intent.putExtra(TMCconstants.RECEIVER, resultReceiver);
        intent.putExtra(TMCconstants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == TMCconstants.SUCCESS_RESULT) {

                String address = resultData.getString(TMCconstants.TotalLocationResult);
             // Log.d("tagCustomer", "Address  " + address);
             // Log.d("tagCustomer", "AreaName  " + resultData.getString(TMCconstants.AreaNameOfLocation));
             // addressdetails_text.setText(address);

                try {
                    if (address.contains("Tamil Nadu")) {
                        int position = address.indexOf("Tamil Nadu");
                        String addressline1 = address.substring(0, position-1);
                        addressline_edittext.setText(addressline1);
                    } else {
                        addressline_edittext.setText(address);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //  Toast.makeText(MapActivity.this, "My Location Result :  " + resultData.getString(Constant.TotalLocationResult), Toast.LENGTH_LONG).show();
             // AreaName.setText(resultData.getString(TMCconstants.AreaNameOfLocation));
             // FullAddress.setText(resultData.getString(TMCconstants.TotalLocationResult));
            } else {
             // Toast.makeText(LocationMapActivity.this, resultData.getString(TMCconstants.TotalLocationResult),
             //                                                           Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                 // Log.d(TAG, "onActivityResult Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                    Toast.makeText(LocationMapActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                    LatLng latlon = place.getLatLng();
                    Longitude = latlon.longitude;
                    Latitude = latlon.latitude;
                    PlaceMarkers(18);
                    break;
                case AutocompleteActivity.RESULT_ERROR:
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Toast.makeText(LocationMapActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, Objects.requireNonNull(status.getStatusMessage()));
                    break;
                case RESULT_CANCELED:
                    Log.i(TAG, "Request cancelled " );
                    break;
            }
        }
    }

    private boolean isAddAddressDetailscalled = false;
    private void addAddressDetails(LatLng latlngofuser) {
        if (isAddAddressDetailscalled) {
            return;
        }
        isAddAddressDetailscalled = true;
        String addressline1 = flatblockno_text.getText().toString();
        String addressline2 = addressline_edittext.getText().toString();
        String landmark = landmark_text.getText().toString();
        String pincode = pincode_text.getText().toString();
        String addresstype = "";
        boolean isdefault = false;
        if (defaultaddresstype == ADDRESSTYPE_HOME) {
            addresstype = "Home";
            isdefault = true;
        } else if (defaultaddresstype == ADDRESSTYPE_OTHERS) {
            addresstype = othertag_text.getText().toString();
            isdefault = false;
        }

        TMCUserAddress tmcUserAddress = new TMCUserAddress();
        tmcUserAddress.setUserkey(settingsUtil.getUserkey());
        tmcUserAddress.setAddressline1(addressline1);
        tmcUserAddress.setAddressline2(addressline2);
        tmcUserAddress.setLandmark(landmark);
        tmcUserAddress.setLocationlat(latlngofuser.latitude);
        tmcUserAddress.setLocationlong(latlngofuser.longitude);
        tmcUserAddress.setAddresstype(addresstype);
        tmcUserAddress.setVendorkey(deliverableVendor.getKey());
        tmcUserAddress.setVendorname(deliverableVendor.getName());
        tmcUserAddress.setPincode(pincode);
        tmcUserAddress.setIsdefault(isdefault);
        tmcUserAddress.setDeliverydistance(deliverydistance);
        tmcUserAddress.setVendorRemapping(false);

        settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
     // TMCVendor tmcVendor = tmcVendorHashMap.get(deliverableVendorKey);
        settingsUtil.setDefaultVendor(deliverableVendor.getJsonObjString());
        addAddressDetailsInAWS(tmcUserAddress);
        if ((lastMappedVendorName == null) || (TextUtils.isEmpty(lastMappedVendorName))) {
            subscribeToFCMTopic(deliverableVendor.getName());
        } else {
            if (deliverableVendor.getName().equalsIgnoreCase(lastMappedVendorName)) {
                // Do Nothing
                subscribeToFCMTopic(deliverableVendor.getName());
            } else {
                subscribeToFCMTopic(deliverableVendor.getName());
                unsubscribeFromFCMTopic(lastMappedVendorName);
            }
        }

    }

    private void updateAddressDetails() {
        if (parsedTMCUserAddress == null) {
            return;
        }
        TMCUserAddress newtmcuseraddress = new TMCUserAddress();
        boolean isChangesDone = false;

        String addressline2 = addressline_edittext.getText().toString();
        String addressline1 = flatblockno_text.getText().toString();
        String landmark = landmark_text.getText().toString();
        String pincode = pincode_text.getText().toString();
        double lat = CustomerLatlng.latitude;
        double longitude = CustomerLatlng.longitude;
        if (!addressline1.equalsIgnoreCase(parsedTMCUserAddress.getAddressline1())) {
            isChangesDone = true;
            parsedTMCUserAddress.setAddressline1(addressline1);
        }
        if (!addressline2.equalsIgnoreCase(parsedTMCUserAddress.getAddressline2())) {
            isChangesDone = true;
            parsedTMCUserAddress.setAddressline2(addressline2);
        }
        if (!landmark.equalsIgnoreCase(parsedTMCUserAddress.getLandmark())) {
            isChangesDone = true;
            parsedTMCUserAddress.setLandmark(landmark);
        }
        if (!pincode.equalsIgnoreCase(parsedTMCUserAddress.getPincode())) {
            isChangesDone = true;
            parsedTMCUserAddress.setPincode(pincode);
        }
        if (lat != parsedTMCUserAddress.getLocationlat()) {
            isChangesDone = true;
            parsedTMCUserAddress.setLocationlat(lat);
        }
        if (longitude != parsedTMCUserAddress.getLocationlong()) {
            isChangesDone = true;
            parsedTMCUserAddress.setLocationlong(longitude);
        }
        if (deliverydistance != parsedTMCUserAddress.getDeliverydistance()) {
            isChangesDone = true;
            parsedTMCUserAddress.setDeliverydistance(deliverydistance);
        }
        if (defaultaddresstype == ADDRESSTYPE_HOME) {
            if (!parsedTMCUserAddress.getAddresstype().equalsIgnoreCase("Home")) {
                isChangesDone = true;
                parsedTMCUserAddress.setAddresstype("Home");
            }
        } else {
            String othersaddresstype = othertag_text.getText().toString();
            if (!parsedTMCUserAddress.getAddresstype().equalsIgnoreCase(othersaddresstype)) {
                isChangesDone = true;
                parsedTMCUserAddress.setAddresstype(othersaddresstype);
            }
        }
        if (isChangesDone) {
            updateAddressDetailsInAWS(parsedTMCUserAddress);
        } else {
            onBackPressed();
        }

    }

    private void addAddressDetailsInAWS(TMCUserAddress userAddress) {
        String userkey = userAddress.getUserkey(); String addresstype = userAddress.getAddresstype();
        String addressline1 = userAddress.getAddressline1(); String addressline2 = userAddress.getAddressline2();
        String landmark = userAddress.getLandmark();  String pincode = userAddress.getPincode();
        double locationlat = userAddress.getLocationlat(); double locationlong = userAddress.getLocationlong();
        String vendorkey = userAddress.getVendorkey(); String vendorname = userAddress.getVendorname();
        JSONObject jsonObject = new JSONObject();
        try {
            if ((userkey != null) && (!TextUtils.isEmpty(userkey))) {
                jsonObject.put("userkey", userkey);
            }
            if ((addresstype != null) && (!TextUtils.isEmpty(addresstype))) {
                jsonObject.put("addresstype", addresstype);
            }
            if ((addressline1 != null) && (!TextUtils.isEmpty(addressline1))) {
                jsonObject.put("addressline1", addressline1);
            }
            if ((addressline2 != null) && (!TextUtils.isEmpty(addressline2))) {
                jsonObject.put("addressline2", addressline2);
            }
            if ((landmark != null) && (!TextUtils.isEmpty(landmark))) {
                jsonObject.put("landmark", landmark);
            }
            if ((pincode != null) && (!TextUtils.isEmpty(pincode))) {
                jsonObject.put("pincode", pincode);
            }
            if ((vendorkey != null) && (!TextUtils.isEmpty(vendorkey))) {
                jsonObject.put("vendorkey", vendorkey);
            }
            if ((vendorname != null) && (!TextUtils.isEmpty(vendorname))) {
                jsonObject.put("vendorname", vendorname);
            }
            if (deliverydistance > 0) {
                jsonObject.put("deliverydistance", deliverydistance);
            }
            jsonObject.put("locationlat", locationlat);
            jsonObject.put("locationlong", locationlong);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String url = TMCRestClient.AWS_ADDADDRESS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                 // Log.d(TAG, "addOrderDetails jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = response.getJSONObject("content");
                        // TMCUserAddress tmcUserAddress1 = new TMCUserAddress(jsonObject1);
                        String key = jsonObject1.getString("key");
                        userAddress.setKey(key);
                        settingsUtil.setDefaultAddress(userAddress.getJsonString());
                     // TMCMenuItemCatalog.getInstance().clearMenuItems();
                     // TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
                        TMCMenuItemCatalog.getInstance().clear();
                        TMCDataCatalog.getInstance().clear();
                        Intent intent = new Intent();
                        intent.putExtra("newlocationsaved", true);
                        intent.putExtra("deliverableVendorKey", deliverableVendor.getKey());
                        intent.putExtra("isLocationUpdated", true);
                        closeActivity(intent);
                     // Log.d(TAG, "settingsutil default address "+settingsUtil.getDefaultAddress());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

     /* TMCRestClient.addAddressDetails(getApplicationContext(), tmcUserAddress, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "addAddressDetails jsonObject " + jsonObject); //No I18N
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("content");
                     // TMCUserAddress tmcUserAddress1 = new TMCUserAddress(jsonObject1);
                        String key = jsonObject1.getString("key");
                        tmcUserAddress.setKey(key);
                        settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
                        Intent intent = new Intent();
                        intent.putExtra("newlocationsaved", true);
                        intent.putExtra("deliverableVendorKey", deliverableVendorKey);
                        closeActivity(intent);
                        Log.d(TAG, "settingsutil default address "+settingsUtil.getDefaultAddress());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "addAddressDetails fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "addAddressDetails fetch failed"); //No i18n
            }
        }); */
    }

    private void updateAddressDetailsInAWS(TMCUserAddress userAddress) {
        String key = userAddress.getKey();
        String userkey = userAddress.getUserkey(); String addresstype = userAddress.getAddresstype();
        String addressline1 = userAddress.getAddressline1(); String addressline2 = userAddress.getAddressline2();
        String landmark = userAddress.getLandmark();  String pincode = userAddress.getPincode();
        double locationlat = userAddress.getLocationlat(); double locationlong = userAddress.getLocationlong();
        String vendorkey = userAddress.getVendorkey(); String vendorname = userAddress.getVendorname();
     // Log.d(TAG, "deliverydistance "+userAddress.getDeliverydistance());
        JSONObject jsonObject = new JSONObject();
        try {
            if ((key != null) && (!TextUtils.isEmpty(key))) {
                jsonObject.put("key", key);
            }
            if ((userkey != null) && (!TextUtils.isEmpty(userkey))) {
                jsonObject.put("userkey", userkey);
            }
            if ((addresstype != null) && (!TextUtils.isEmpty(addresstype))) {
                jsonObject.put("addresstype", addresstype);
            }
            if ((addressline1 != null) && (!TextUtils.isEmpty(addressline1))) {
                jsonObject.put("addressline1", addressline1);
            }
            if ((addressline2 != null) && (!TextUtils.isEmpty(addressline2))) {
                jsonObject.put("addressline2", addressline2);
            }
            if ((landmark != null) && (!TextUtils.isEmpty(landmark))) {
                jsonObject.put("landmark", landmark);
            }
            if ((pincode != null) && (!TextUtils.isEmpty(pincode))) {
                jsonObject.put("pincode", pincode);
            }
            if ((vendorkey != null) && (!TextUtils.isEmpty(vendorkey))) {
                jsonObject.put("vendorkey", vendorkey);
            }
            if ((vendorname != null) && (!TextUtils.isEmpty(vendorname))) {
                jsonObject.put("vendorname", vendorname);
            }
            if (locationlat > 0) {
                jsonObject.put("locationlat", locationlat);
            }
            if (locationlong > 0) {
                jsonObject.put("locationlong", locationlong);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String url = TMCRestClient.AWS_UPDATEADDRESS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                 // Log.d(TAG, "updateAddressDetails jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        // TMCUserAddress tmcUserAddress1 = new TMCUserAddress(jsonObject1);
                        settingsUtil.setDefaultAddress(userAddress.getJsonString());
                        Intent intent = new Intent();
                        intent.putExtra("newlocationsaved", true);
                        intent.putExtra("deliverableVendorKey", deliverableVendor.getKey());
                        intent.putExtra("isLocationUpdated", true);
                        closeActivity(intent);
                     // Log.d(TAG, "settingsutil default address "+settingsUtil.getDefaultAddress());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

     /* TMCRestClient.addAddressDetails(getApplicationContext(), tmcUserAddress, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "addAddressDetails jsonObject " + jsonObject); //No I18N
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("content");
                     // TMCUserAddress tmcUserAddress1 = new TMCUserAddress(jsonObject1);
                        String key = jsonObject1.getString("key");
                        tmcUserAddress.setKey(key);
                        settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
                        Intent intent = new Intent();
                        intent.putExtra("newlocationsaved", true);
                        intent.putExtra("deliverableVendorKey", deliverableVendorKey);
                        closeActivity(intent);
                        Log.d(TAG, "settingsutil default address "+settingsUtil.getDefaultAddress());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "addAddressDetails fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "addAddressDetails fetch failed"); //No i18n
            }
        }); */
    }


 /* private void updateAddressDetailsInAWS(TMCUserAddress tmcUserAddress) {
        TMCRestClient.updateAddressDetails(getApplicationContext(), tmcUserAddress, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "updateAddressDetails jsonObject " + jsonObject); //No I18N
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("success")) {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "updateAddressDetails fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "updateAddressDetails fetch failed"); //No i18n
            }
        });
    } */

    private ArrayList<TMCVendor> tmcVendorArrayList;
    private HashMap<Integer, TMCVendor> latlngvendorkeyList; // {Hashcode of latlng, Vendorkey}
    private HashMap<String, TMCVendor> tmcVendorHashMap;
    private void getVendorDetails() {
        showLoadingAnim();
     // Log.d(TAG, "getVendordetails method");
        String url = TMCRestClient.AWS_GETVENDORRECORDS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            // Log.d(TAG, "getVendorDetails jsonObject " + jsonObject); //No I18N
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                tmcVendorArrayList = new ArrayList<TMCVendor>();
                                latlngvendorkeyList = new HashMap<Integer, TMCVendor>();
                                tmcVendorHashMap = new HashMap<String, TMCVendor>();
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                 /* String vendorname = jsonObject1.getString("name");
                                    String key = jsonObject1.getString("key");
                                    String fssaino = jsonObject1.getString("vendorfssaino");
                                    String mobile = jsonObject1.getString("vendormobile");
                                    String status = jsonObject1.getString("status"); */
                                    String key = jsonObject1.getString("key");
                                    boolean istestvendor = false;
                                    if (jsonObject1.has("istestvendor")) {
                                        istestvendor = jsonObject1.getBoolean("istestvendor");
                                    }
                                    if (istestvendor) {
                                        continue;
                                    }
                                    String vendortype = jsonObject1.getString("vendortype");
                                    if (vendortype.equalsIgnoreCase("WAREHOUSE")) {
                                        continue;
                                    }

                                    // Log.d(TAG, "fetched vendorkey "+key);
                                    double locationlat = jsonObject1.getDouble("locationlat");
                                    double locationlong = jsonObject1.getDouble("locationlong");
                                    TMCVendor tmcVendor = new TMCVendor(jsonObject1);
                                    tmcVendorArrayList.add(tmcVendor);
                                    LatLng latlng = new LatLng(locationlat, locationlong);
                                    latlngvendorkeyList.put(new Integer(latlng.hashCode()), tmcVendor);
                                    tmcVendorHashMap.put(tmcVendor.getKey(), tmcVendor);
                                }

                                if ((CustomerLatlng != null) && (tmcVendorArrayList != null)) {
                                    drawSphericalRound(CustomerLatlng, tmcVendorArrayList, false);
                                }
                                // Log.d(TAG, "getVendorDetails results fetched");
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

    private List<MarkerOptions> markers = new ArrayList<>();
    private int distanceapicalls = 0;
    private int distanceapiresponses = 0;
    private void drawSphericalRound(LatLng latLngofuser, List<TMCVendor> vendorList, boolean isAddUpdateAddress) {
        deliverableVendorMap = new HashMap<String, TMCVendor>();
        delDistanceVendorKeyMap = new HashMap<Double, String>();
        for (int i=0; i<vendorList.size(); i++) {
            TMCVendor tmcVendor = vendorList.get(i);
            LatLng vendorlatLng = new LatLng(tmcVendor.getLocationlat(), tmcVendor.getLocationlong());
            MarkerOptions marker1 = new MarkerOptions().position(vendorlatLng);
            markers.add(marker1);
        }

        distanceapicalls = 0; distanceapiresponses = 0;
        completedlocationapicalls = 0;
        for (int i=0; i<markers.size(); i++) {
            if (isLocationSaved) {
                return;
            }
            MarkerOptions marker = markers.get(i);
            int dellocinmeters = settingsUtil.getDeliveryLocationRadius() * 1000;
         // Log.d(TAG, "delivery location radius "+settingsUtil.getDeliveryLocationRadius());
            double computedistinbetween = SphericalUtil.computeDistanceBetween(latLngofuser, marker.getPosition());
         // Log.d(TAG, "computedistinbetween "+computedistinbetween + " dellocinmeters "+dellocinmeters);
            if (computedistinbetween < dellocinmeters) {
             // Log.d("bingo", "Location  " +marker.getPosition() +"   ");
                LatLng latLngofshopfromSphericalMarker = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
                distanceapicalls = distanceapicalls + 1;
                try {
                    calculateDistanceviaApi(latLngofuser,latLngofshopfromSphericalMarker,marker,isAddUpdateAddress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
             /* completedlocationapicalls = completedlocationapicalls + 1;
                if (completedlocationapicalls == tmcVendorArrayList.size()) {
                    String action = "dellocation: " + dellocinmeters + "_computedistinbetween:" + computedistinbetween + "_"
                                      + TMCUtil.getInstance().getCurrentTime();
                    logEventInGAnalytics("DrawSphericalUtil", action, settingsUtil.getMobile());
                    hideLoadingAnim();
                    double distinkms = computedistinbetween / 1000;
                    deliverydistinstring = distinkms + " Kms";
                    String message = getResources().getString(R.string.locationnotdeliverable_alert) +
                            ". Distance: " + deliverydistinstring ;
                    showTMCAlert(message);
                } */
            }
            if (i == (markers.size()-1)) {
                // Log.d(TAG, "locationapicalls "+locationapicalls + " locationapiresponses" +locationapiresponses);
                startSchedulerThreadForGettingMappedVendors(isAddUpdateAddress);
            }
        }
    }

    private void calculateDistanceviaApi(LatLng LatLangofUser, LatLng LatLangofshop,MarkerOptions marker,
                                                               boolean isAddUpdateAddress) throws JSONException {
        Log.i("Tag", "Latlangcal");
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + LatLangofUser.latitude + "," + LatLangofUser.longitude + "&destinations=" + LatLangofshop.latitude + "," + LatLangofshop.longitude + "&mode=driving&language=en-EN&sensor=false&key=AIzaSyDYkZGOckF609Cjt6mnyNX9QhTY9-kAqGY";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {

                    completedlocationapicalls = completedlocationapicalls + 1;
                    distanceapiresponses = distanceapiresponses + 1;
                  //  Log.d(TAG, "response " + response.toString());

                    JSONArray rowsArray = (JSONArray) response.get("rows");
                    Log.d(TAG, "rows" + rowsArray.toString());
                    JSONObject elements = rowsArray.getJSONObject(0);

                    JSONArray elementsArray = (JSONArray) elements.get("elements");

                    JSONObject distance = elementsArray.getJSONObject(0);
                    JSONObject jsondistance = distance.getJSONObject("distance");

                    String distanceinString = jsondistance.getString("text");
                    double dist = 0;
                    if (distanceinString.contains("km")) {
                        dist = Double.parseDouble(distanceinString.replace(" km",""));
                    } else if (distanceinString.contains("m")) {
                        dist = Double.parseDouble(distanceinString.replace(" m",""));
                        dist = dist / 1000;
                    }
                 // Log.d(TAG, "dist :" + dist+" deliverylocradius "+settingsUtil.getDeliveryLocationRadius());
                    String action = "userdist: " + dist + " dellocradius: " + settingsUtil.getDeliveryLocationRadius()
                                               +"_" + TMCUtil.getInstance().getCurrentTime();
                    logEventInGAnalytics("calculateDistanceviaApi", action, settingsUtil.getMobile());
                    if(dist < settingsUtil.getDeliveryLocationRadius()) {
                        if (latlngvendorkeyList != null) {
                         // deliverydistance = dist;
                            TMCVendor tmcVendor = latlngvendorkeyList.get(LatLangofshop.hashCode());
                         // deliverableVendorKey = tmcVendor.getKey();
                         // deliverableVendorName = tmcVendor.getName();

                            Log.d(TAG, "calculateDistanceviaApi dist "+dist);
                            delDistanceVendorKeyMap.put(new Double(dist), tmcVendor.getKey());
                            deliverableVendorMap.put(tmcVendor.getKey(), tmcVendor);

                         /* if (isAddUpdateAddress) {
                                isLocationSaved = true;
                                if (callfromprofileactivity) {
                                    updateAddressDetails();
                                } else {
                                    addAddressDetails(CustomerLatlng);
                                }
                            } else {
                                hideLoadingAnim();
                            } */
                        }
                    } else {
                     /* if (completedlocationapicalls == tmcVendorArrayList.size()) {
                            hideLoadingAnim();
                            deliverydistinstring = distanceinString;
                            String message = getResources().getString(R.string.locationnotdeliverable_alert) +
                                    ". Distance: " + distanceinString ;
                            showTMCAlert(message);

                        } */
                    }
                } catch (JSONException e) {
                    hideLoadingAnim();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error1: " + error.getLocalizedMessage());
                Log.d(TAG, "Error2: " + error.getMessage());
                Log.d(TAG, "Error3: " + error.toString());

                error.printStackTrace();
            }
        });

        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private int schthreadcount = 0;
    private int schthreadmaxcount = 15;
    private void startSchedulerThreadForGettingMappedVendors(boolean isAddUpdateAddress) {
        Log.d(TAG, "startSchedulerThreadForGettingMappedVendors isAddUpdateAddress "+isAddUpdateAddress);
        if (distanceapicalls == distanceapiresponses) {
            mapDeliverableVendor(isAddUpdateAddress);
        } else {
            if (schthreadcount > schthreadmaxcount) {
                return;
            }
            schthreadcount = schthreadcount + 1;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startSchedulerThreadForGettingMappedVendors(isAddUpdateAddress);
                }
            }, 1500);
        }
    }

    private HashMap<String, TMCVendor> deliverableVendorMap;
    private HashMap<Double, String> delDistanceVendorKeyMap;
    private void mapDeliverableVendor(boolean isAddUpdateAddress) {
        Log.d(TAG, "mapDeliverableVendor deliverableVendorMap "+deliverableVendorMap);
        if ((deliverableVendorMap == null) || (deliverableVendorMap.size() <= 0)) {
            hideLoadingAnim();
            String action = "Lat: " + CustomerLatlng.latitude + "-Long: " + CustomerLatlng.longitude
                                                                 + "_" + TMCUtil.getInstance().getCurrentTime();
            logEventInGAnalytics("Location not deliverable", action, settingsUtil.getMobile());
            String message = getResources().getString(R.string.locationnotdeliverable_alert);
            showTMCAlert(message);
            return;
        }

        Log.d(TAG, "delDistanceVendorKeyMap "+delDistanceVendorKeyMap);
        double shorterdistance = 0; String deliverablevendorkey = null;
        Object[] delDistanceKeys = delDistanceVendorKeyMap.keySet().toArray();
        for (int i=0; i<delDistanceKeys.length; i++) {
            if (i == 0) {
                Double deldistance = (Double) delDistanceKeys[i];
                shorterdistance = deldistance.doubleValue();
                deliverablevendorkey = delDistanceVendorKeyMap.get(deldistance);
            } else {
                Double deldistance = (Double) delDistanceKeys[i];
                double deldistindouble = deldistance.doubleValue();
                if (shorterdistance < deldistindouble) {
                    // shorterdistance = shorterdistance
                } else {
                    shorterdistance = deldistindouble;
                    deliverablevendorkey = delDistanceVendorKeyMap.get(deldistance);
                }
            }
        }

        deliverydistance = shorterdistance;
        deliverableVendor = deliverableVendorMap.get(deliverablevendorkey);
        Log.d(TAG, "mapDeliverableVendor deliverableVendor "+deliverableVendor.getKey() + " name " + deliverableVendor.getName());
        if (isAddUpdateAddress) {
            isLocationSaved = true;
            if (callfromprofileactivity) {
                updateAddressDetails();
            } else {
                addAddressDetails(CustomerLatlng);
            }
        } else {
            hideLoadingAnim();
        }

        String action = "Lat: " + CustomerLatlng.latitude + "-Long: " + CustomerLatlng.longitude
                + "_" + TMCUtil.getInstance().getCurrentTime();
        logEventInGAnalytics("Location deliverable", action, settingsUtil.getMobile());
        Log.d(TAG, "deliverableVendor "+deliverableVendor.getKey() + " name "+deliverableVendor.getName());
    }

    private void showTMCAlert(int strresid) {
        new TMCAlert(LocationMapActivity.this, 0, strresid,
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

    private void showTMCAlert(String messagetext) {
        new TMCAlert(LocationMapActivity.this, "", messagetext,
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

    public void showKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }

    public void hideKeyboardsofAllEdittexts() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(flatblockno_text.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addressline_edittext.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(landmark_text.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(othertag_text.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pincode_text.getWindowToken(), 0);
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

    private void subscribeToFCMTopic(String vendorname) {
        try {
            String vname = vendorname.replaceAll(" ", "");
            vname = vname.toUpperCase();
            FirebaseMessaging.getInstance().subscribeToTopic(vname)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String action = vendorname + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("Subscribe to topic successful", action, settingsUtil.getMobile());
                                Log.d(TAG, "Subscribe to topic successful");
                                settingsUtil.setFCMTokenTopicSubscribed();
                            } else {
                                String action = vendorname + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("Subscribe to topic failed", action, settingsUtil.getMobile());
                                Log.d(TAG, "Subscribe to topic failed");
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void unsubscribeFromFCMTopic(String vendorname) {
        try {
            String vname = vendorname.replaceAll(" ", "");
            vname = vname.toUpperCase();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(vendorname)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String action = vendorname + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("UnSubscribed from topic successful", action, settingsUtil.getMobile());
                                Log.d(TAG, "UnSubscribed from topic successful");
                            } else {
                                String action = vendorname + "_" + TMCUtil.getInstance().getCurrentTime();
                                logEventInGAnalytics("UnSubscribed from topic failed", action, settingsUtil.getMobile());
                                Log.d(TAG, "UnSubscribed from topic failed");
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void closeActivity(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }


}
