package com.goryn.wikiguide;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.goryn.wikiguide.managers.LocationManager;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.Query;
import com.goryn.wikiguide.utils.GoogleApiHelper;

import java.util.List;


public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private LocationManager locationManager;

    private Query query;
    private Query wQuery;

    private static App mInstance;



    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Context context = getApplicationContext();
        googleApiHelper = new GoogleApiHelper();
        locationManager = new LocationManager(context);

        query = new Query();
        wQuery = new Query();

    }

    public static App getInstance() {
        return mInstance;
    }

    public static GoogleApiClient getGoogleApiClient() {
        return getGoogleApiHelper().getGoogleApiClient();
    }

    public static GoogleApiHelper getGoogleApiHelper(){
        return getInstance().googleApiHelper;
    }
    public static LocationManager getLocationManager() {
        return getInstance().locationManager;
    }

    public static void setQuery(Query query){
        getInstance().query = query;
    }
    public static Query getQuery(){
        return getInstance().query;
    }

}
