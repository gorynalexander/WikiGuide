package com.goryn.wikiguide.managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.Query;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by Odinn on 27.07.2017.
 */

public class LocationManager implements LocationListener {
    private Location previuousLocation, currentLocation;
    private Context context;

    private GoogleMap googleMap;
    private LocationRequest locationRequest;

    public LocationManager(Context context) {
        this.context = context;
        createLocationRequest();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    public void updateMap(GoogleMap map) {
        if (map == null) {
            return;
        }

        googleMap = map;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(false);
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.addMarker(new MarkerOptions()
        .position(latLng)
        .title("Your pos"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 12.0f));
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }


    public void startLocationUpdates() {
        if (locationRequest == null) {
            createLocationRequest();
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(App.getGoogleApiClient(), locationRequest, this);
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(App.getGoogleApiClient());
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(App.getGoogleApiClient(), this);
    }

    public void setUserMarker() {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your pos"));
    }

    public void setMarkers(Query result) {
        for (Page page : result.getPages()) {
            if (page.getCoordinates() != null) {
                googleMap.addMarker(createMarkerOptions(page));
            }
        }
    }

    public MarkerOptions createMarkerOptions(Page page) {

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(page.getThumbUrl()).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap logo = Bitmap.createScaledBitmap(bitmap, page.getThumbWidth()/6, page.getThumbHeight()/6, true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(logo);
        return new MarkerOptions()
                .position(new LatLng(page.getCoordinates().get(0).getLat(), page.getCoordinates().get(0).getLon()))
                .title(page.getTitle())
                .icon(bitmapDescriptor);

    }

}
