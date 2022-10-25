package com.meatchop.services;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.meatchop.utils.TMCconstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;

public class FetchAddressIntentService extends IntentService {
    private ResultReceiver resultReceiver;
    List<Address> addresses;
    String AreaName ="0";
    public FetchAddressIntentService()
    {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null)
        {
            boolean isIntentValid = false;
            ComponentName name = intent.resolveActivity(getPackageManager());
            Log.d("FetchAddressIntService", "component name "+name);
            if (name != null) {
                String packagename = name.getPackageName();
                String classname = name.getClassName();
                if ( (packagename.equals("com.meatchop")) && (classname.equals("com.meatchop.services.FetchAddressIntentService")) ) {
                    isIntentValid = true;
                }
            }
            Log.d("FetchAddressIntService", "isFetchAddressIntent "+isIntentValid);
            if (!isIntentValid) {
                return;
            }
            String errormessage="";
            resultReceiver = intent.getParcelableExtra(TMCconstants.RECEIVER);
            Log.d("tagCustomer",  "Fetch Address  "+resultReceiver);

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            Location location = intent.getParcelableExtra(TMCconstants.LOCATION_DATA_EXTRA);
            if (location == null) {
                return;
            }
            addresses = null;
            try {
                Log.i("tagCustomer",  "lat  lon  "+geocoder.getFromLocation(location.getLatitude(),
                                                                      location.getLongitude(), 1));

                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Log.i("tagCustomer",  "Fetch Addressssssss  "+addresses.get(0));
            } catch (Exception e) {
                errormessage = e.getMessage();
            }

            if(addresses == null || addresses.isEmpty())
            {
                deliverResultToReceiver(AreaName,TMCconstants.FAILURE_RESULT,errormessage);
            }
            else{
                Address address = addresses.get(0);
                AreaName =  address.getThoroughfare();

                if(AreaName == null){
                    AreaName= address.getSubLocality();

                    if(AreaName == null){
                        AreaName =  address.getFeatureName();

                    }
                }

                ArrayList<String> addressFragments = new ArrayList<>();
                for(int i= 0; i <= address.getMaxAddressLineIndex();i++)

                {
                    addressFragments.add(address.getAddressLine(i));
                    Log.i("tagCustomer",  " addressFragments "+addressFragments);

                }
                deliverResultToReceiver(AreaName,TMCconstants.SUCCESS_RESULT,
                         TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")),addressFragments));
            }
        }
    }

    private void deliverResultToReceiver(String AreaName,int resultCode,String addressmessage){
        Log.i("tagCustomer",  "addressmessage  "+addressmessage);

        Bundle bundle =new Bundle();

        bundle.putString(TMCconstants.TotalLocationResult,addressmessage);
        bundle.putString(TMCconstants.AreaNameOfLocation,AreaName);
        resultReceiver.send(resultCode,bundle);
    }

}