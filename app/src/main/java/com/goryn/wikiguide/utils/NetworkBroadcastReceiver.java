package com.goryn.wikiguide.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.ui.MainActivity;


public class NetworkBroadcastReceiver extends BroadcastReceiver{

    MainActivity activity = null;

    public void setActivityHandler(MainActivity activity){
        this.activity = activity;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//
//        final NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if(info != null && info.isConnected()){
            //Toast.makeText(context, "ВКЛЮЧИЛСЯ", Toast.LENGTH_SHORT).show();
            Log.i("WIKIGUIDE BROADCAST", "INTERNET IS AVAILABLE");
//            if (!App.getGoogleApiClient().isConnected()){
//                App.getGoogleApiClient().connect();
//            }

        }
    }

}
