package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.SphericalUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.TMCApplication;
import com.meatchop.adapters.PlacesAutoCompleteAdapter;
import com.meatchop.adapters.TMCUserAddressListAdapter;
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
import com.meatchop.widget.TMCMenuItemCatalog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocationServicesActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener, View.OnClickListener{

    private String TAG = "LocationServicesActivity";

    public static final int CALLTYPE_HOMESCREEN = 0;
    public static final int CALLTYPE_TMCMENUITEM = 1;
    public static final int CALLTYPE_TMCSEARCHSCREEN = 2;
    public static final int CALLTYPE_MORESCREEN = 3;
    public static final int CALLTYPE_MANAGEADDRESS = 4;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 0;
    private static final int LOCATIONMAP_ACT_REQCODE = 1;

    private View back_icon;
    private TMCEditText searchforarea_edittext;
    private View currentlocation_layout;

    private double latitude;
    private double longitude;

    private Marker marker;
    private GoogleMap map;
    private ResultReceiver resultReceiver;

    ArrayList<String> placesList;
    ArrayAdapter<String> placesListAdatper;
    String placesURL;
    String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;
    private RecyclerView savedaddresses_recyclerview;

    private View loadinganimmask_layout;
    private View loadinganim_layout;
    private View searchclose_btn;
    private SettingsUtil settingsUtil;
    private HashMap<String, TMCUserAddress> tmcUserAddressHashMap;  // {Key -- TMCUserAddress}

    private boolean defaultaddressalreadyset = false;
    private boolean isLocationUpdated;
    private String selectedVendorKey;
    private boolean callfromtmcmenitemactivity;
    private boolean callfromprofileactivity;
    private boolean isCurrentLocationButtonClicked = false;
    private Intent defaultIntentForParsing = null;

    public int calltype;
    private String lastMappedVendorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
     // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_location_services);
        settingsUtil = new SettingsUtil(this);

        if (getPackageName().equals("com.meatchop")) {
            calltype = getIntent().getIntExtra("calltype", CALLTYPE_HOMESCREEN);
            callfromtmcmenitemactivity = (calltype == LocationServicesActivity.CALLTYPE_TMCMENUITEM) ? true : false;
            callfromprofileactivity = (calltype == LocationServicesActivity.CALLTYPE_MORESCREEN) ? true : false;
         // callfromtmcmenitemactivity = getIntent().getBooleanExtra("callfromtmcmenitemactivity", false);
         // callfromprofileactivity = getIntent().getBooleanExtra("callfromprofileactivity", false);
        }
     // Log.d(TAG, "callfromtmcmenitemactivity "+callfromtmcmenitemactivity);

        String apiKey = getResources().getString(R.string.google_map_api_key);
        Places.initialize(getApplicationContext(), apiKey);

        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout.setOnClickListener(this);
        searchclose_btn = findViewById(R.id.searchclose_btn);

        back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchforarea_edittext = (TMCEditText) findViewById(R.id.searchforarea_edittext);
        currentlocation_layout = findViewById(R.id.currentlocation_layout);
        currentlocation_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingAnim();
                isCurrentLocationButtonClicked = true;
                checkLocationPermissionforOurApp();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.places_recycler_view);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        savedaddresses_recyclerview = (RecyclerView) findViewById(R.id.savedaddresses_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        savedaddresses_recyclerview.setLayoutManager(layoutManager);

        searchforarea_edittext.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (s.toString().length() >= 4) {
                        mAutoCompleteAdapter.getFilter().filter(s.toString());
                        if (recyclerView.getVisibility() == View.GONE) {recyclerView.setVisibility(View.VISIBLE);}
                        if (searchclose_btn.getVisibility() == View.GONE) {searchclose_btn.setVisibility(View.VISIBLE);}
                        savedaddresses_recyclerview.setVisibility(View.GONE);
                    }
                } else {
                    if (searchclose_btn.getVisibility() == View.VISIBLE) {searchclose_btn.setVisibility(View.GONE);}
                    if (recyclerView.getVisibility() == View.VISIBLE) {recyclerView.setVisibility(View.GONE);}
                    savedaddresses_recyclerview.setVisibility(View.VISIBLE);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        searchclose_btn.setOnClickListener(this);

        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();

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

        if ((settingsUtil.getUserkey() != null) && !(TextUtils.isEmpty(settingsUtil.getUserkey()))) {
            getAddressDetails(settingsUtil.getUserkey());
        } else {
            showForceLoginAlert();
        }
    }

    private void closeActivityAndSendForceLoginHandler() {
        try {
            AWSMobileClient.getInstance().signOut();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra("forcelogin", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.loadinganimmask_layout) {
            hideLoadingAnim();
        } else if (id == R.id.searchclose_btn) {
            searchforarea_edittext.setText("");
            searchclose_btn.setVisibility(View.GONE);
        }
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    private Handler createUserAddressHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String menutype = bundle.getString("menutype");
                if (menutype.equalsIgnoreCase("addressclicked")) {
                    showLoadingAnim();
                    String useraddresskey = bundle.getString("useraddresskey");
                    TMCUserAddress tmcUserAddress = tmcUserAddressHashMap.get(useraddresskey);
                    if (settingsUtil.isRefreshVendorMapping()) {
                        if (tmcUserAddress.getDeliverydistance() > 10) {
                            if (!tmcUserAddress.isVendorremapping()) {
                                settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
                                getVendorDetailsAndMapCorrectVendor(new LatLng(tmcUserAddress.getLocationlat(),
                                        tmcUserAddress.getLocationlong()));
                                return false;
                            }
                        }
                    }
                    selectedVendorKey = tmcUserAddress.getVendorkey();
                    if ((lastMappedVendorName == null) || (TextUtils.isEmpty(lastMappedVendorName))) {
                        subscribeToFCMTopic(tmcUserAddress.getVendorname());
                    } else {
                        if (tmcUserAddress.getVendorname().equalsIgnoreCase(lastMappedVendorName)) {
                            // Do Nothing
                            subscribeToFCMTopic(tmcUserAddress.getVendorname());
                        } else {
                            subscribeToFCMTopic(tmcUserAddress.getVendorname());
                            unsubscribeFromFCMTopic(lastMappedVendorName);
                        }
                    }
                    settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
                    getVendorDetails(tmcUserAddress);
                    hideKeyboard();
                }

                return false;
            }
        };
        return new Handler(callback);
    }

    private void parseUserAddressAndCloseActivity(TMCUserAddress tmcUserAddress) {
        Intent intent = new Intent();
        intent.putExtra("addressclicked", true);
        intent.putExtra("vendorkey", tmcUserAddress.getVendorkey());
        intent.putExtra("vendorname", tmcUserAddress.getVendorname());
        intent.putExtra("addressline1", tmcUserAddress.getAddressline1());
        intent.putExtra("addressline2", tmcUserAddress.getAddressline2());
        intent.putExtra("isLocationUpdated", true);
        setResult(RESULT_OK, intent);
        TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public void click(Place place) {
        hideKeyboard();
        showLoadingAnim();
        String CustomerLatitude = String.valueOf(Objects.requireNonNull(place.getLatLng()).latitude);
        String CustomerLongitude = String.valueOf(place.getLatLng().longitude);

        defaultIntentForParsing =new Intent(LocationServicesActivity.this, LocationMapActivity.class);
        Bundle Lat_Lon_Bundle = new Bundle();
        Lat_Lon_Bundle.putDouble("latitude", Double.parseDouble(CustomerLatitude));
        Lat_Lon_Bundle.putDouble("longitude",Double.parseDouble(CustomerLongitude));
        defaultIntentForParsing.putExtras(Lat_Lon_Bundle);
        defaultIntentForParsing.putExtra("defaultaddressalreadyset", defaultaddressalreadyset);
        startActivityForResult(defaultIntentForParsing, LOCATIONMAP_ACT_REQCODE);
        hideKeyboard();
     // checkLocationPermissionforOurApp();
     // startActivityForResult(i, LOCATIONMAP_ACT_REQCODE);
     // Toast.makeText(this, place.getAddress()+", "+place.getLatLng().latitude+place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
    }

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermissionforOurApp() {
        Log.d(TAG, "checkLocationPermissionforOurApp");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "checkLocationPermissionforOurApp PERMISSION Not GRANTED");
            // Asking user if explanation is needed
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))&&(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION))) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                Log.d(TAG, "checkLocationPermissionforOurApp Request permission 1");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                Log.d(TAG, "checkLocationPermissionforOurApp Request permission 2");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
         // hideLoadingAnim();
            return false;
        } else {
            Log.d(TAG, "checkLocationPermissionforOurApp PERMISSION GRANTED");
         // Toast.makeText(this, "permission Granted", Toast.LENGTH_LONG).show();

            SwitchOnGps(LocationServicesActivity.this);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                 // Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_LONG).show();

                    Log.d(TAG, "onRequestPermissionsResult Location Permission Granted");
                    SwitchOnGps(LocationServicesActivity.this);
                    // permission was granted. Do the
                    // contacts-related task you need to do.
                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    private void SwitchOnGps(final Context context) {
        Log.d(TAG, "SwitchOnGps");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                    Log.d(TAG, "SwitchOnGps before registering receiver");
                    showLoadingAnim();
                    registerReceiver(locationreceiver, new IntentFilter(CurrentLocationService.str_receiverr));
                    startService(new Intent(LocationServicesActivity.this, CurrentLocationService.class));

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult((Activity) context,LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                    hideLoadingAnim();
                }
            }
        });
    }

    private String CustomerLatitude;
    private String CustomerLongitude;

    private BroadcastReceiver locationreceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.v(TAG, "BroadCastSucess");

                CustomerLatitude = bundle.getString(CurrentLocationService.Latitude);
                CustomerLongitude = bundle.getString(CurrentLocationService.Longitude);
                if (CustomerLatitude != null&&CustomerLongitude!=null) {
                    if (isCurrentLocationButtonClicked) {
                        defaultIntentForParsing =new Intent(LocationServicesActivity.this, LocationMapActivity.class);
                        Bundle Lat_Lon_Bundle = new Bundle();
                        Lat_Lon_Bundle.putDouble("latitude",Double.parseDouble(CustomerLatitude));
                        Lat_Lon_Bundle.putDouble("longitude",Double.parseDouble(CustomerLongitude));
                        defaultIntentForParsing.putExtras(Lat_Lon_Bundle);
                        defaultIntentForParsing.putExtra("defaultaddressalreadyset", defaultaddressalreadyset);
                        startActivityForResult(defaultIntentForParsing, LOCATIONMAP_ACT_REQCODE);
                    } else {

                    }
                } else {
                    Toast.makeText(LocationServicesActivity.this, "Download failed",
                            Toast.LENGTH_LONG).show();
                    Log.v(TAG, "BroadCast Failed");
                    showUnableToFetchLocationAlert();
                }
                stopService(new Intent(LocationServicesActivity.this, CurrentLocationService.class));
                Log.d(TAG, "BroadCastSucess"+CustomerLongitude +"   "+CustomerLatitude);
                unregisterReceiver(locationreceiver);
                hideLoadingAnim();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    Log.d(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                    Toast.makeText(LocationServicesActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                    LatLng latlon = place.getLatLng();
                    longitude = latlon.longitude;
                    latitude = latlon.latitude;
                    defaultIntentForParsing = new Intent(LocationServicesActivity.this, LocationMapActivity.class);
                    defaultIntentForParsing.putExtra("longitude", longitude);
                    defaultIntentForParsing.putExtra("latitude", latitude);
                    defaultIntentForParsing.putExtra("defaultaddressalreadyset", defaultaddressalreadyset);
                    startActivityForResult(defaultIntentForParsing, LOCATIONMAP_ACT_REQCODE);
                    hideKeyboard();
                 // checkLocationPermissionforOurApp();
                 // startActivityForResult(intent, LOCATIONMAP_ACT_REQCODE);
                    break;
                case AutocompleteActivity.RESULT_ERROR:
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Toast.makeText(LocationServicesActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, Objects.requireNonNull(status.getStatusMessage()));
                    break;
                case RESULT_CANCELED:
                    Log.i(TAG, "Request cancelled " );
                    break;
            }
        } else if (requestCode == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made

                    showLoadingAnim();
                    registerReceiver(locationreceiver, new IntentFilter(CurrentLocationService.str_receiverr));
                    startService(new Intent(LocationServicesActivity.this, CurrentLocationService.class));

                    Log.i(TAG, "onActivityResult: GPS Enabled by user");
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Log.i(TAG, "onActivityResult: User rejected GPS request");
                    finish();
                    break;
                default:
                    break;
            }
        } else if (requestCode == LOCATIONMAP_ACT_REQCODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    boolean forcelogin = data.getBooleanExtra("forcelogin", false);
                    if (forcelogin) {
                        closeActivityAndSendForceLoginHandler();
                        return;
                    }
                    isLocationUpdated = data.getBooleanExtra("newlocationsaved", false);
                    String vendorkey = data.getStringExtra("deliverableVendorKey");
                    if (isLocationUpdated) {
                        Intent intent = new Intent();
                        intent.putExtra("isLocationUpdated", isLocationUpdated);
                        intent.putExtra("vendorkey", vendorkey);
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(0, 0);
                        return;
                    }

                    if (searchforarea_edittext != null) {
                        searchforarea_edittext.setText("");
                    }
                    if (searchclose_btn != null) {
                        searchclose_btn.setVisibility(View.GONE);
                    }
                 /* if (isLocationUpdated) {
                        getAddressDetails(settingsUtil.getUserkey());
                    } */
                }

            }
            hideLoadingAnim();
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

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchforarea_edittext.getWindowToken(), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<TMCUserAddress> tmcUserAddressArrayList;
 /* private void getAddressDetails(String userkey) {
        showLoadingAnim();
        TMCRestClient.getAddressDetails(userkey, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "getAddressDetails jsonObject " + jsonObject); //No I18N
                    String message = jsonObject.getString("message");
                    tmcUserAddressArrayList = new ArrayList<TMCUserAddress>();
                    tmcUserAddressHashMap = new HashMap<String, TMCUserAddress>();
                    if (message.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("content");
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            TMCUserAddress tmcUserAddress = new TMCUserAddress(jsonObject1);
                            String addresstype = jsonObject1.getString("addresstype");
                            if (addresstype.equalsIgnoreCase("Home")) {
                                defaultaddressalreadyset = true;
                            }
                            tmcUserAddressArrayList.add(tmcUserAddress);
                            tmcUserAddressHashMap.put(tmcUserAddress.getKey(), tmcUserAddress);
                        }
                        Log.d(TAG, "defaultaddressalreadyset "+defaultaddressalreadyset);
                        TMCUserAddressListAdapter tmcUserAddressListAdapter =
                                   new TMCUserAddressListAdapter(getApplicationContext(), tmcUserAddressArrayList);
                        tmcUserAddressListAdapter.setHandler(createUserAddressHandler());
                        savedaddresses_recyclerview.setAdapter(tmcUserAddressListAdapter);
                    }
                    hideLoadingAnim();
                    Log.d(TAG, "tmcuseraddressarrlist size "+tmcUserAddressArrayList.size());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                hideLoadingAnim();
                Log.d(TAG, "getAddressDetails fetch failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                hideLoadingAnim();
                Log.d(TAG, "getAddressDetails fetch failed"); //No i18n
            }
        });
    }
*/
    private void getAddressDetails(String userkey) {
        if ((userkey == null) || (TextUtils.isEmpty(userkey))) {
            return;
        }
        showLoadingAnim();
        // String url = TMCRestClient.AWS_GETTMCMENUITEMS + "?storeid=" + vendorkey + "&subctgyid=" + selectedtmcsubctgykey;
        String url = TMCRestClient.AWS_GETADDRESS + "?userid=" + userkey;
        Log.d(TAG, "getAddressDetails url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getAddressDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            tmcUserAddressArrayList = new ArrayList<TMCUserAddress>();
                            tmcUserAddressHashMap = new HashMap<String, TMCUserAddress>();
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TMCUserAddress tmcUserAddress = new TMCUserAddress(jsonObject1);
                                    String addresstype = jsonObject1.getString("addresstype");
                                    if (addresstype.equalsIgnoreCase("Home")) {
                                        defaultaddressalreadyset = true;
                                    }
                                    tmcUserAddressArrayList.add(tmcUserAddress);
                                    tmcUserAddressHashMap.put(tmcUserAddress.getKey(), tmcUserAddress);
                                }
                                if (calltype != LocationServicesActivity.CALLTYPE_MANAGEADDRESS) {
                                    TMCUserAddressListAdapter tmcUserAddressListAdapter =
                                            new TMCUserAddressListAdapter(getApplicationContext(), tmcUserAddressArrayList);
                                    tmcUserAddressListAdapter.setHandler(createUserAddressHandler());
                                    savedaddresses_recyclerview.setAdapter(tmcUserAddressListAdapter);
                                }
                            }
                            hideLoadingAnim();
                            Log.d(TAG, "tmcuseraddressarrlist size "+tmcUserAddressArrayList.size());
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

    private void showForceLoginAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(LocationServicesActivity.this, R.string.app_name, R.string.forcelogin_alert,
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


    private void showUnableToFetchLocationAlert() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(LocationServicesActivity.this, R.string.app_name, R.string.locationnotfetched_alert,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                                finish();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    private void getVendorDetails(TMCUserAddress tmcUserAddress) {
        String url = TMCRestClient.AWS_GETVENDORRECORDS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String vendorkey = jsonObject1.getString("key");
                                    if (vendorkey.equalsIgnoreCase(selectedVendorKey)) {
                                        TMCVendor tmcVendor = new TMCVendor(jsonObject1);
                                        settingsUtil.setDefaultVendor(tmcVendor.getJsonObjString());
                                    }
                                }
                                parseUserAddressAndCloseActivity(tmcUserAddress);
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

    private ArrayList<TMCVendor> tmcVendorArrayList;
    private HashMap<Integer, TMCVendor> latlngvendorkeyList; // {Hashcode of latlng, Vendorkey}
    private HashMap<String, TMCVendor> tmcVendorHashMap;
    private void getVendorDetailsAndMapCorrectVendor(LatLng CustomerLatlng) {
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

    private HashMap<String, TMCVendor> deliverableVendorMap;
    private HashMap<Double, String> delDistanceVendorKeyMap;
    private List<MarkerOptions> markers = new ArrayList<>();
    private int locationapicalls = 0;
    private int locationapiresponses = 0;
    private void drawSphericalRound(LatLng latLngofuser, List<TMCVendor> vendorList, boolean isAddUpdateAddress) {
        deliverableVendorMap = new HashMap<String, TMCVendor>();
        delDistanceVendorKeyMap = new HashMap<Double, String>();
        for (int i=0; i<vendorList.size(); i++) {
            TMCVendor tmcVendor = vendorList.get(i);
            LatLng vendorlatLng = new LatLng(tmcVendor.getLocationlat(), tmcVendor.getLocationlong());
            MarkerOptions marker1 = new MarkerOptions().position(vendorlatLng);
            markers.add(marker1);
        }

        locationapicalls = 0; locationapiresponses = 0;
        // Log.d(TAG, "markers size "+markers.size());
        for (int j=0; j<markers.size(); j++) {
            MarkerOptions marker = markers.get(j);
         // int dellocinmeters = settingsUtil.getDeliveryLocationRadius() * 1000;
            int dellocinmeters = 30 * 1000;  // Hardcoded value
            double computedistinbetween = SphericalUtil.computeDistanceBetween(latLngofuser, marker.getPosition());
            if (computedistinbetween < dellocinmeters) {
                locationapicalls = locationapicalls + 1;
                LatLng latLngofshopfromSphericalMarker = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
                try {
                    calculateDistanceviaApi(latLngofuser,latLngofshopfromSphericalMarker,marker,isAddUpdateAddress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (j == (markers.size()-1)) {
                // Log.d(TAG, "locationapicalls "+locationapicalls + " locationapiresponses" +locationapiresponses);
                startSchedulerThreadForGettingMappedVendors();
            }
        }
    }

    private void calculateDistanceviaApi(LatLng LatLangofUser, LatLng LatLangofshop,MarkerOptions marker,
                                         boolean isAddUpdateAddress) throws JSONException {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + LatLangofUser.latitude + "," + LatLangofUser.longitude + "&destinations=" + LatLangofshop.latitude + "," + LatLangofshop.longitude + "&mode=driving&language=en-EN&sensor=false&key=AIzaSyDYkZGOckF609Cjt6mnyNX9QhTY9-kAqGY";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                    locationapiresponses = locationapiresponses + 1;
                    JSONArray rowsArray = (JSONArray) response.get("rows");
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
                    int dellocradius = 30;  // Hardcoded value
                    if(dist < dellocradius) {
                        if (latlngvendorkeyList != null) {
                            TMCVendor tmcVendor = latlngvendorkeyList.get(LatLangofshop.hashCode());
                            deliverableVendorMap.put(tmcVendor.getKey(), tmcVendor);
                            delDistanceVendorKeyMap.put(new Double(dist), tmcVendor.getKey());
                        }
                    } else {
                        // Log.d(TAG, "calculateDistanceviaApi completedlocationapicalls "+completedlocationapicalls+ " tmcVendorArrayList.size() "+tmcVendorArrayList.size());
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
    private void startSchedulerThreadForGettingMappedVendors() {
        if (locationapicalls == locationapiresponses) {
            mapDeliverableVendor();
        } else {
            if (schthreadcount > schthreadmaxcount) {
                return;
            }
            schthreadcount = schthreadcount + 1;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startSchedulerThreadForGettingMappedVendors();
                }
            }, 1500);
        }
    }

    private void mapDeliverableVendor() {
        Log.d(TAG, "mapDeliverableVendor "+deliverableVendorMap);
        if ((deliverableVendorMap == null) || (deliverableVendorMap.size() <= 0)) {
            return;
        }
        if ((delDistanceVendorKeyMap == null) || (delDistanceVendorKeyMap.size() <= 0)) {
            return;
        }
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
        TMCVendor deliverableVendor = deliverableVendorMap.get(deliverablevendorkey);
        selectedVendorKey = deliverableVendor.getKey();
        settingsUtil.setDefaultVendor(deliverableVendor.getJsonObjString());
        Log.d(TAG, "deliverableVendor "+deliverableVendor.getKey() + " name "+deliverableVendor.getName());
        updateVendorDetailsInUserAddress(deliverableVendor, shorterdistance);
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
     // initAddressAndVendorDetails();
    }

    private void updateVendorDetailsInUserAddress(TMCVendor tmcVendor, double deliverydistance) {
        String defaultaddressjson = settingsUtil.getDefaultAddress();
        if ((defaultaddressjson == null) || (TextUtils.isEmpty(defaultaddressjson))) {
            return;
        }
        try {
            TMCUserAddress tmcUserAddress = new TMCUserAddress(new JSONObject(defaultaddressjson));
         /* if (tmcUserAddress.getVendorkey().equalsIgnoreCase(tmcVendor.getKey())) {
                parseUserAddressAndCloseActivity(tmcUserAddress);
                hideKeyboard();
                return;
            } */

            tmcUserAddress.setDeliverydistance(deliverydistance);
            tmcUserAddress.setVendorkey(tmcVendor.getKey());
            tmcUserAddress.setVendorname(tmcVendor.getName());
            tmcUserAddress.setVendorRemapping(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", tmcUserAddress.getKey());
            jsonObject.put("deliverydistance", deliverydistance);
            jsonObject.put("vendorkey", tmcVendor.getKey());
            jsonObject.put("vendorname", tmcVendor.getName());
            jsonObject.put("isvendorremapping", true);
            updateUserAddressDetailsInAWS(jsonObject);

            settingsUtil.setDefaultAddress(tmcUserAddress.getJsonString());
         // Log.d(TAG, "settingsUtil.getDefaultAddress() " + settingsUtil.getDefaultAddress());

         // TMCMenuItemCatalog.getInstance().clearMenuItems();
         // TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
            TMCMenuItemCatalog.getInstance().clear();
            TMCDataCatalog.getInstance().clear();
            settingsUtil.setDefaultVendor(tmcVendor.getJsonObjString());
            parseUserAddressAndCloseActivity(tmcUserAddress);

            // initAddressAndVendorDetails();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateUserAddressDetailsInAWS(JSONObject jsonObject) {
        String url = TMCRestClient.AWS_UPDATEADDRESS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                try {
                    // Log.d(TAG, "updateAddressDetails jsonobject " + response);

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
    }

    private void subscribeToFCMTopic(String vendorname) {
        try {
            String vname = vendorname.replaceAll(" ", "");
            vname = vname.toUpperCase();
            Log.d(TAG, "subscribeToFCMTopic vname "+vname);
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
            FirebaseMessaging.getInstance().unsubscribeFromTopic(vname)
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







}
