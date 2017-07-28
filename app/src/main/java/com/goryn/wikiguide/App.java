package com.goryn.wikiguide;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.goryn.wikiguide.managers.LocationManager;
import com.goryn.wikiguide.utils.GoogleApiHelper;


public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private LocationManager locationManager;

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        Context context = getApplicationContext();
        googleApiHelper = new GoogleApiHelper();
        locationManager = new LocationManager(context);
    }

    public static App getInstance() {
        return mInstance;
    }

    public static GoogleApiClient getGoogleApiClient() {
        return getInstance().googleApiHelper.getGoogleApiClient();
    }

    public static GoogleApiHelper getGoogleApiHelper(){
        return getInstance().googleApiHelper;
    }
    public static LocationManager getLocationManager() {
        return getInstance().locationManager;
    }

}
