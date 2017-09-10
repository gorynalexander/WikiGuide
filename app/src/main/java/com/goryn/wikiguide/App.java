package com.goryn.wikiguide;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.goryn.wikiguide.managers.LocationManager;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.Query;
import com.goryn.wikiguide.model.WikiQuery;
import com.goryn.wikiguide.utils.GoogleApiHelper;

import java.util.List;


public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private LocationManager locationManager;

    private Query query;
    private WikiQuery wikiQuery;

    private static App mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Context context = getApplicationContext();
        googleApiHelper = new GoogleApiHelper();
        locationManager = new LocationManager(context);

        query = new Query();
        wikiQuery = new WikiQuery();

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

    public static WikiQuery getWikiQuery() {
        return getInstance().wikiQuery;
    }

    public static void setWikiQuery(WikiQuery wikiQuery) {

        List<Page> query = getInstance().query.getPages();
        for (int i = 0; i < query.size(); i++){
            query.get(i).setExtract(wikiQuery.getWikiPages().get(i).getExtract());
        }

        getInstance().wikiQuery = wikiQuery;
    }
}
