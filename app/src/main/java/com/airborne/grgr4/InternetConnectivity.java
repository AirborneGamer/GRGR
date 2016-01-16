package com.airborne.grgr4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InternetConnectivity {

    Context mContext;
    CallBackListener mListener;
    String mInternetConnectionStatus;

    public InternetConnectivity(Context context) {
        mContext = context;
    }

    public void setListener(CallBackListener listener) {
        mListener = listener;
    }

    public boolean isConnected() {
        try {

            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (cm.getActiveNetworkInfo() == null || !networkInfo.isConnected()) {
                Toast.makeText(mContext, R.string.internet_not_connected, Toast.LENGTH_LONG).show();
                return false;
            }

            //GOOD!
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    class getUserLocationAsync extends AsyncTask<LocalRepDataHelper, Void, Map<String, String>> {

        protected Map<String, String> doInBackground(LocalRepDataHelper... params) {

            Map<String, String> UserLocationInfo = new HashMap<>();
            mInternetConnectionStatus = "";

            try {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    UserLocationInfo.put("State", "UnknownState");
                    mInternetConnectionStatus = mContext.getString(R.string.internet_permissions_disabled);
                    return UserLocationInfo;
                }

                LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                Location gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location availableLocation = (gpsLocation != null) ? gpsLocation : networkLocation;

                if (availableLocation == null) {
                    UserLocationInfo.put("State", "UnknownState");
                    mInternetConnectionStatus = mContext.getString(R.string.internet_no_gps_location);
                    return UserLocationInfo;
                }

                double longitude = availableLocation.getLongitude();
                double latitude = availableLocation.getLatitude();

                Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());

                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                String fullStateName = addresses.get(0).getAdminArea();//Gets state name from GPS.
                String zipCode = addresses.get(0).getPostalCode();
                String StateValues = params[0].getStateAbbreviationAndFullName(fullStateName);

                UserLocationInfo.put("State", StateValues);
                UserLocationInfo.put("ZipCode", zipCode);
                return UserLocationInfo;

            } catch (Exception ex) {
                UserLocationInfo.put("State", "UnknownState");
                mInternetConnectionStatus = mContext.getString(R.string.internet_gps_failed);
                return UserLocationInfo;
            }
        }

        @Override
        protected void onPostExecute(Map<String, String> userLocationInfo) {
            mListener.userLocationCallback(userLocationInfo, mInternetConnectionStatus);
        }
    }
}
