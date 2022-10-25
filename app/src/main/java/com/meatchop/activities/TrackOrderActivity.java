package com.meatchop.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.data.Orderdetails;
import com.meatchop.data.Ordertrackingdetails;
import com.meatchop.data.TMCUserAddress;
import com.meatchop.data.TMCVendor;
import com.meatchop.services.FetchAddressIntentService;
import com.meatchop.utils.DataParser;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.utils.TMCconstants;
import com.meatchop.widget.TMCMapFragment;
import com.meatchop.widget.TMCMenuItemCatalog;
import com.meatchop.widget.TMCTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import cz.msebera.android.httpclient.Header;

// Unused Activity
public class TrackOrderActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private final int TMCDETAILORDERACT_REQ_CODE = 0;
 // String CustomerLongitude = "0", CustomerLatitude = "0";
    double UserLatitude, UserLongitude;
    double VendorLatitude; double VendorLongitude;
    String vendorname;
    GoogleMap map;
    ResultReceiver resultReceiver;
    Marker marker;
    private LatLng CustomerLatlng;
    private String TAG = "TrackOrderActivity";

 // private Orderdetails orderdetails;
    private SettingsUtil settingsUtil;

    private TMCTextView orderid_text;
    private TMCTextView deliverytimeinmins_textview;
    private TMCTextView estimateddeliverytime_textview;

    private View vieworderdetails_btn;
    private View vieworderdetails_layout;

    private View pendingstatus_value;
    private View pendingstatusselected_value;
    private View readystatus_value;
    private View readystatusselected_value;
    private View deliveredstatus_value;
    private View deliveredstatusselected_value;
    private String selectedVendorKey;

    private boolean callfromtmcordersactivity = false;
    private Ordertrackingdetails ordertrackingdetails;

    private View loadinganim_layout;
    private View loadinganimmask_layout;
    private TMCTextView orderstatus_desp;
    private TMCTextView calldesp_text;
    private View callbutton_layout;

    private String storemobileno;
    private String deliverypartnermobileno;

    private TMCMapFragment mapFragment;
    private boolean isActivityActive = false;

    private String orderid;
    private Orderdetails orderdetails;
    private String slotname;
    private boolean isPreorderslot = false;
    private TMCTextView orderreceived_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trackorder);
        isActivityActive = true;

        settingsUtil = new SettingsUtil(this);
        if (getPackageName().equals("com.meatchop")) {
            orderdetails = getIntent().getParcelableExtra("orderdetails");
            orderid = getIntent().getStringExtra("orderid");
            if (orderdetails == null) {
                slotname = getIntent().getStringExtra("slotname");
            } else {
                slotname = orderdetails.getSlotname();
            }
            callfromtmcordersactivity = getIntent().getBooleanExtra("callfromtmcordersactivity", false);
        }

        if ((slotname != null) && (slotname.equalsIgnoreCase("Preorder"))) {
            isPreorderslot = true;
            setContentView(R.layout.activity_trackorder_preorder);
        } else {
            isPreorderslot = false;
        }

        orderreceived_textview = (TMCTextView) findViewById(R.id.orderreceived_textview);
        orderid_text = (TMCTextView) findViewById(R.id.orderid_text);
        orderid_text.setText("#" + orderid);

        deliverytimeinmins_textview = (TMCTextView) findViewById(R.id.deliverytimeinmins_textview);
        estimateddeliverytime_textview = (TMCTextView) findViewById(R.id.estimateddeliverytime_textview);
        orderstatus_desp = (TMCTextView) findViewById(R.id.orderstatus_desp);
     // fillEstimatedDeliveryTime();
        calldesp_text = (TMCTextView) findViewById(R.id.calldesp_text);
        callbutton_layout = findViewById(R.id.callbutton_layout);

        pendingstatus_value = findViewById(R.id.pendingstatus_value);
        pendingstatusselected_value = findViewById(R.id.pendingstatusselected_value);
        readystatus_value = findViewById(R.id.readystatus_value);
        readystatusselected_value = findViewById(R.id.readystatusselected_value);
        deliveredstatus_value = findViewById(R.id.deliveredstatus_value);
        deliveredstatusselected_value = findViewById(R.id.deliveredstatusselected_value);

        loadinganim_layout = findViewById(R.id.loadinganim_layout);
        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);

        try {
            TMCUserAddress userAddress = new TMCUserAddress(new JSONObject(settingsUtil.getDefaultAddress()));
            CustomerLatlng = new LatLng(userAddress.getLocationlat(), userAddress.getLocationlong());
            UserLatitude = userAddress.getLocationlat();
            UserLongitude = userAddress.getLocationlong();
            selectedVendorKey = userAddress.getVendorkey();
            vendorname = userAddress.getVendorname();

            TMCVendor tmcVendor = new TMCVendor(new JSONObject(settingsUtil.getDefaultVendor()));
            VendorLatitude = tmcVendor.getLocationlat(); VendorLongitude = tmcVendor.getLocationlong();
            storemobileno = tmcVendor.getVendormobile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        vieworderdetails_layout = findViewById(R.id.vieworderdetails_layout);

        vieworderdetails_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTMCDetailOrderActivity();
            }
        });

        callbutton_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "";
                if (ordertrackingdetails != null) {
                    status = ordertrackingdetails.getOrderstatus();
                }
                String mobileno = "";
                if (status.equalsIgnoreCase(Orderdetails.ORDERSTATUS_PICKEDUP)) {
                    mobileno = deliverypartnermobileno;
                } else {
                    mobileno = storemobileno;
                }
                if ((mobileno != null) && !(TextUtils.isEmpty(mobileno))) {
                    Intent callIntent = new Intent(
                            Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + mobileno)); // NO I18N
                    startActivity(callIntent);
                }
            }
        });
        resultReceiver = new AddressResultReceiver(new Handler());

        if (!isPreorderslot) {
            mapFragment = (TMCMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
            mapFragment.getMapAsync(this);
            mapFragment.setListener(new TMCMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {

                }
            });
            Objects.requireNonNull(mapFragment).getMapAsync(TrackOrderActivity.this);
        }

        getOrderTrackingDetails();
        schedulePeriodicHandlerForMapRefresh();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume method");
        isActivityActive = true;
        schedulePeriodicHandlerForMapRefresh();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause method");
        isActivityActive = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop method");
        isActivityActive = false;
        super.onStop();
    }

    private void showLoadingAnimation() {
        loadinganimmask_layout.setVisibility(View.VISIBLE);
        loadinganim_layout.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        loadinganimmask_layout.setVisibility(View.GONE);
        loadinganim_layout.setVisibility(View.GONE);
    }

    private void startTMCDetailOrderActivity() {
        Intent intent = new Intent(TrackOrderActivity.this, TMCDetailOrderActivity.class);
        intent.putExtra("orderid", orderid);
        intent.putExtra("orderdetails", orderdetails);
        startActivityForResult(intent, TMCDETAILORDERACT_REQ_CODE);
    }

 /* private void fillEstimatedDeliveryTime() {
        try {
            String orderplacedtime = orderdetails.getOrderplacedtime();
            String slotname = orderdetails.getSlotname();
            Log.d(TAG, "slotname "+slotname + " slotdate "+orderdetails.getSlotdate() + " slottimerange "+orderdetails.getSlottimerange());
            if (slotname.equalsIgnoreCase("Preorder")) {
                String slotdate = orderdetails.getSlotdate();
                String slottimerange = orderdetails.getSlottimerange();
                estimateddeliverytime_textview.setText(slotdate);
                deliverytimeinmins_textview.setText("(" + slottimerange + ")");
            } else if (slotname.contains("Delivery")) {
                String deliverytime = orderdetails.getSlottimerange();
                deliverytimeinmins_textview.setText("(" + deliverytime + ")");
                deliverytime = deliverytime.replaceAll("mins", "").replaceAll(" ", "");
                String expecteddeliverytime = TMCUtil.getInstance().getTimeAfterMinuteAddition(orderplacedtime, deliverytime);
                estimateddeliverytime_textview.setText(expecteddeliverytime);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } */

    private void fillOrderStatusDetails() {
        if (ordertrackingdetails == null) { return; }
        String orderstatus = ordertrackingdetails.getOrderstatus();
        if ( (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_NEW)) ||
            (orderstatus.equals(Orderdetails.ORDERSTATUS_CONFIRMED)) || orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_READYFORPICKUP)) {
         // Log.d(TAG, "fillOrderStatusDetails orderstatus "+orderstatus);
            if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_NEW)) {
                orderstatus_desp.setText("Your order has been received");
             // orderreceived_textview.setText("received");
            } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CONFIRMED)) {
                orderstatus_desp.setText("Your order is confirmed");
             // orderreceived_textview.setText("confirmed");
            } else {
             // orderreceived_textview.setText("confirmed");
            }

            calldesp_text.setText("Call Store");
            pendingstatus_value.setVisibility(View.GONE); pendingstatusselected_value.setVisibility(View.VISIBLE);
            readystatus_value.setVisibility(View.VISIBLE); readystatusselected_value.setVisibility(View.GONE);
            deliveredstatus_value.setVisibility(View.VISIBLE); deliveredstatusselected_value.setVisibility(View.GONE);
        } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_PICKEDUP)) {
            deliverypartnermobileno = ordertrackingdetails.getDeliveryusermobileno();
            orderstatus_desp.setText("Your order is picked up");
            calldesp_text.setText("Call Delivery Partner");
            pendingstatus_value.setVisibility(View.GONE); pendingstatusselected_value.setVisibility(View.VISIBLE);
            readystatus_value.setVisibility(View.GONE); readystatusselected_value.setVisibility(View.VISIBLE);
            deliveredstatus_value.setVisibility(View.VISIBLE); deliveredstatusselected_value.setVisibility(View.GONE);
        } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_DELIVERED)) {
            callbutton_layout.setVisibility(View.GONE);
            orderstatus_desp.setText("Your order has been delivered");
            pendingstatus_value.setVisibility(View.GONE); pendingstatusselected_value.setVisibility(View.VISIBLE);
            readystatus_value.setVisibility(View.GONE); readystatusselected_value.setVisibility(View.VISIBLE);
            deliveredstatus_value.setVisibility(View.GONE); deliveredstatusselected_value.setVisibility(View.VISIBLE);
        } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CANCELLED)) {
            callbutton_layout.setVisibility(View.GONE);
            orderstatus_desp.setText("Your order has been cancelled");
            pendingstatus_value.setVisibility(View.VISIBLE); pendingstatusselected_value.setVisibility(View.GONE);
            readystatus_value.setVisibility(View.VISIBLE); readystatusselected_value.setVisibility(View.GONE);
            deliveredstatus_value.setVisibility(View.VISIBLE); deliveredstatusselected_value.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (callfromtmcordersactivity) {
            setResult(RESULT_OK);
            finish();
        } else {
            startHomeScreenActivity();
        }
    }

    private void startHomeScreenActivity() {
        TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReadyMethod");
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        PlaceMarkers(14);
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

    }

    private void removeMarkers() {
        Log.d(TAG, "removemarkers method");
        isStopScheduler = true;
        if (marker != null) {
            marker.remove();
        }
        map.clear();
    }

    private void PlaceMarkers(int zoomToValue) {
        Log.d(TAG, "placemarkers method");
        if(marker != null) {
            marker.remove();
        }

        Log.d("placemarker", UserLatitude + "  " + UserLongitude);
        //setting Marker for Customer Location
        CustomerLatlng = new LatLng(UserLatitude, UserLongitude);
        MarkerOptions CustomersMarker = new MarkerOptions();
        CustomersMarker.position(CustomerLatlng);
     // Log.i("tagCustomer", DoubleCustomerLatitude + "  " + DoubleCustomerLongitutde);
        CustomersMarker.title("Customer");
     // CustomersMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        CustomersMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_trackorder_deliveryhome));
        marker = map.addMarker(CustomersMarker);

        //move the Map to the Customer Location
        if (zoomToValue <= 0) {
            map.moveCamera(CameraUpdateFactory.newLatLng(CustomerLatlng));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(CustomerLatlng, zoomToValue));
        }

     // double storelat = 12.9404755;
     // double storelong = 80.1494831;
        //setting Marker for VendorShop Location
        LatLng VendorlatLng = new LatLng(VendorLatitude, VendorLongitude);
        MarkerOptions VendorsMarker = new MarkerOptions();
        VendorsMarker.position(VendorlatLng);
     // Log.i("tagVendor", DoubleVendorLatitude + "  " + DoubleVendorLongitude);
        VendorsMarker.title(vendorname);
     // VendorsMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
     // VendorsMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_storesample_pin));
        VendorsMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_deliverybike));
        marker = map.addMarker(VendorsMarker);

        //Drawing the Route between the Customer Location and Vendor Location.
        String url = getUrl(CustomerLatlng, VendorlatLng);
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);


     /* double DeliveryPartnerLatitude = 12.9404755;
        double DeliveryPartnerLongitude = 80.1494831;
        //Place the Marker in DeliveryPartner Location
        LatLng DeliveryPartnerlatLng = new LatLng(DeliveryPartnerLatitude, DeliveryPartnerLongitude);
        MarkerOptions DeliverypartnerMarker = new MarkerOptions();
        DeliverypartnerMarker.position(DeliveryPartnerlatLng);
        DeliverypartnerMarker.title("Delivery Partner");
        DeliverypartnerMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_trackorder_scooter));
        Marker marker1 = map.addMarker(DeliverypartnerMarker);
        map.moveCamera(CameraUpdateFactory.newLatLng(DeliveryPartnerlatLng)); */

        //Getting the Address of the Customer Location
     // Location location = new Location("providerNA");
     // location.setLatitude(UserLatitude);
     // location.setLongitude(UserLongitude);
     // map.setOnCameraIdleListener(null);
     // fetchMyLocationsAddress(location);
    }

    private void fetchMyLocationsAddress(Location location) {
        Log.i("tagCustomer", "Location  " + location);

        Intent intent = new Intent(this, FetchAddressIntentService.class);

        intent.putExtra(TMCconstants.RECEIVER, resultReceiver);
        intent.putExtra(TMCconstants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    @Override
    public void onClick(View view) {

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
                Log.d("tagCustomer", "Address  " + address);
                Log.d("tagCustomer", "AreaName  " + resultData.getString(TMCconstants.AreaNameOfLocation));
             // addressdetails_text.setText(address);

                //  Toast.makeText(MapActivity.this, "My Location Result :  " + resultData.getString(Constant.TotalLocationResult), Toast.LENGTH_LONG).show();
                // AreaName.setText(resultData.getString(TMCconstants.AreaNameOfLocation));
                // FullAddress.setText(resultData.getString(TMCconstants.TotalLocationResult));
            } else {
                Toast.makeText(TrackOrderActivity.this, resultData.getString(TMCconstants.TotalLocationResult),
                        Toast.LENGTH_LONG).show();

            }
        }
    }

    //Making the Url which helps us to get the route between Customer and Vendor Location
    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyDYkZGOckF609Cjt6mnyNX9QhTY9-kAqGY";
        ;


        return url;
    }


    private Polyline lastaddedPolyLine;
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);


        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                // Starts parsing data
                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

             // Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }

            if (lastaddedPolyLine != null) {
                lastaddedPolyLine.remove();
            }
            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                lastaddedPolyLine = map.addPolyline(lineOptions);
            }
            else {
             // Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }




    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
         // Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
         // Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void createMarkers() {
        Log.d(TAG, "createmarkers method");
        int zoomToValue = 14;
        if(marker != null) {
            marker.remove();
        }

        // Log.d("placemarker", UserLatitude + "  " + UserLongitude);
        //setting Marker for Customer Location
        MarkerOptions CustomersMarker = new MarkerOptions();
        CustomersMarker.position(CustomerLatlng);
        // Log.i("tagCustomer", DoubleCustomerLatitude + "  " + DoubleCustomerLongitutde);
        CustomersMarker.title("Customer");
        // CustomersMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        CustomersMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_trackorder_deliveryhome));
        marker = map.addMarker(CustomersMarker);

        //move the Map to the Customer Location
        if (zoomToValue <= 0) {
            map.moveCamera(CameraUpdateFactory.newLatLng(CustomerLatlng));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(CustomerLatlng, zoomToValue));
        }
        LatLng vendorlatLng = new LatLng(VendorLatitude,VendorLongitude);
        MarkerOptions VendorMarker = new MarkerOptions();
        VendorMarker.position(vendorlatLng);
        // Log.i("tagVendor", DoubleVendorLatitude + "  " + DoubleVendorLongitude);
        VendorMarker.title(vendorname);
        // VendorsMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        VendorMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_deliverybike));
        marker = map.addMarker(VendorMarker);
    }

    private void refreshMarkers() {
        Log.d(TAG, "refreshMarkers method");
        int zoomToValue = 14;
        if(marker != null) {
            marker.remove();
        }

     // Log.d("placemarker", UserLatitude + "  " + UserLongitude);
        //setting Marker for Customer Location
        MarkerOptions CustomersMarker = new MarkerOptions();
        CustomersMarker.position(CustomerLatlng);
        // Log.i("tagCustomer", DoubleCustomerLatitude + "  " + DoubleCustomerLongitutde);
        CustomersMarker.title("Customer");
        // CustomersMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        CustomersMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_trackorder_deliveryhome));
        marker = map.addMarker(CustomersMarker);

        //move the Map to the Customer Location
        if (zoomToValue <= 0) {
            map.moveCamera(CameraUpdateFactory.newLatLng(CustomerLatlng));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(CustomerLatlng, zoomToValue));
        }

        if (ordertrackingdetails == null) {
            return;
        }
        double deliveryPartnerLat = ordertrackingdetails.getDeliveryuserlat();
        double deliveryPartnerLong = ordertrackingdetails.getDeliveryuserlong();
        LatLng DeliveryPartnerlatLng;
        if ((deliveryPartnerLat == 0) || (deliveryPartnerLong == 0)) {
            DeliveryPartnerlatLng = new LatLng(VendorLatitude, VendorLongitude);
        } else {
            DeliveryPartnerlatLng = new LatLng(deliveryPartnerLat,deliveryPartnerLong);
            Log.d(TAG, "deliveryPartnerLat "+deliveryPartnerLat+" deliveryPartnerLong "+deliveryPartnerLong);
        }

        MarkerOptions DeliveryMarker = new MarkerOptions();
        DeliveryMarker.position(DeliveryPartnerlatLng);
        // Log.i("tagVendor", DoubleVendorLatitude + "  " + DoubleVendorLongitude);
        DeliveryMarker.title(vendorname);
        // VendorsMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        DeliveryMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_deliverybike));
        marker = map.addMarker(DeliveryMarker);

        //Drawing the Route between the Customer Location and Vendor Location.
        String url = getUrl(CustomerLatlng, DeliveryPartnerlatLng);
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
    }

    private boolean isStopScheduler = false;
    private void schedulePeriodicHandlerForMapRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isStopScheduler || !isActivityActive) {
                    return;
                }
             // Log.d(TAG, "before calling ordertracking details method from schedulePeriodicHandlerForMapRefresh");
                getOrderTrackingDetails();
                schedulePeriodicHandlerForMapRefresh();
            }
        }, 10000);  // Will be called every 10 secs
    }

    private void getOrderTrackingDetails() {
        String url = TMCRestClient.AWS_GETORDERTRACKINGDETAILS + "?orderid=" + orderid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                     // Log.d(TAG, "getOrderTrackingDetails Volley response "+response.toString());
                        try {
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = response.getJSONArray("content");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject ordertrackingjsonobj = jsonArray.getJSONObject(i);
                                    ordertrackingdetails = new Ordertrackingdetails(ordertrackingjsonobj);
                                }
                                fillOrderStatusDetails();

                                if (!isPreorderslot) {
                                    String orderstatus = ordertrackingdetails.getOrderstatus();
                                    if ( (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_NEW))
                                            || (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CONFIRMED))
                                            || (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_READYFORPICKUP)) ) {

                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_PICKEDUP)) {
                                        refreshMarkers();
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_DELIVERED)) {
                                        removeMarkers();
                                    } else if (orderstatus.equalsIgnoreCase(Orderdetails.ORDERSTATUS_CANCELLED)) {
                                        removeMarkers();
                                    }
                                }

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




}
