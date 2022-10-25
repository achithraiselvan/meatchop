package com.meatchop.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.meatchop.utils.SettingsUtil;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;



public class CurrentLocationService extends Service implements LocationListener {
    Intent intent;
    public static String Latitude;
    public static String Longitude;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    LocationManager locationManager;
    public static String str_receiverr = "com.example.getcurrentlocation";
    String locationStatus = null;

    private boolean isLocationReceived = false;

    private String TAG = "CurrentLocationService";
    private SettingsUtil settingsUtil;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(str_receiverr);
        isLocationReceived = false;
        settingsUtil = new SettingsUtil(this);
        getLocation();
    }

    private void getLocation() {
        Log.d(TAG, "getLocation method");
        locationStatus = null;
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.e("Gps ", isGPSEnable + "");

        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.e("Network ", isNetworkEnable + "");

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isGPSEnable) {
                GetLocationFromGPS();
            }

         /* Handler handler1 = new Handler();    // android.os.Handler
            Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                    if (locationStatus == null) {
                     // Toast.makeText(getBaseContext(), "If you stay Indoor , You May not get Accurate Location , Wait for 30 Seconds",
                     //         Toast.LENGTH_SHORT).show();

                    }
                }
            };
            handler1.postDelayed(runnable1, 1500); */
            if (locationStatus == null) {
                Handler handler = new Handler();    // android.os.Handler
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (isNetworkEnable) {
                            Log.v("TAG", "mapReady8 removed");
                            getLocationfromNetwork();
                        }

                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    private void GetLocationFromGPS() {
        Log.d(TAG, "GetLocationFromGPS method");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "GetLocationFromGPS method permission not granted");
            return;
        }
     // locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void getLocationfromNetwork() {
        locationManager.removeUpdates(CurrentLocationService.this);

        if (locationStatus == null) {
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
         // locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            Log.v("TAG", "mapReady8.2 getting LOCOTION from network");

        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onLocationChanged(Location loc) {
        if (isLocationReceived) {
            return;
        }
        isLocationReceived = true;
        Log.e("LocationchangedLattitude", loc.getLatitude() + "");
        Log.e("LocationChangedlongitude", loc.getLongitude() + "");
        Log.v("TAG", "mapReady4");
        locationStatus = "got Location from GPS";

        Longitude = String.valueOf(loc.getLongitude());
        Log.v("TAG", Longitude);
        settingsUtil.setDefaultUserLocationLong(Longitude);

        Latitude = String.valueOf(loc.getLatitude());
        Log.v("TAG", Latitude);
        settingsUtil.setDefaultUserLocationLat(Latitude);
        sendtoActivty(Latitude,Longitude);
    }

    private void sendtoActivty(String latitude, String longitude) {
        intent.putExtra(Latitude,latitude);
        intent.putExtra(Longitude,longitude);
        sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

