package com.goryn.wikiguide.managers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.Query;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.security.AccessController.getContext;


public class LocationManager implements LocationListener {
    private Location previuousLocation, currentLocation;
    private Context context;

    private GoogleMap googleMap;
    private LocationRequest locationRequest;

    private Marker userMarker;
    private Circle userCircle;
    private int circleRadius = 5000;

    private List<Marker> markers = new ArrayList<>();

    public LocationManager(Context context) {
        this.context = context;
        createLocationRequest();
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
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

        userMarker = map.addMarker(new MarkerOptions()
        .position(latLng)
        .title("Your position"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 12.0f));

        userCircle = map.addCircle(new CircleOptions()
        .center(latLng)
        .radius(circleRadius)
        .strokeColor(Color.BLUE));

        setMarkers(App.getQuery());

    }


    public Location getCurrentLocation() {
        return currentLocation;
    }
    public LatLng getCurrentLatLng(){
        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker != null) setUserMarker(currentLatLng);

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

    public void setUserMarker(LatLng latLng) {
        userMarker.setPosition(latLng);
        userCircle.setCenter(latLng);
    }
    public void setUserCircleRadius(int radius){
        circleRadius = radius;
        if (userCircle != null){
            userCircle.setRadius(radius);
        }
    }


    public void setMarkers(Query result) {
        if (markers != null){
            for (Marker marker : markers){
                marker.remove();
                Log.i("TAAAAG", marker.getTitle());
            }
        }
        for (Page page : result.getPages()) {
            Log.i("MAP_DEBUGING", "123");
            if (page.getCoordinates() != null) {
                Log.i("MAP_DEBUGING", page.getTitle());
                Marker marker = googleMap.addMarker(createMarkerOptions(page));
                markers.add(marker);
            }
        }

    }

    public MarkerOptions createMarkerOptions(Page page) {
        return new MarkerOptions()
                .position(new LatLng(page.getCoordinates().get(0).getLat(), page.getCoordinates().get(0).getLon()))
                .title(page.getTitle())
                .icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromImageUrl(page.getThumbUrl())));
    }

    private Bitmap createBitmapFromImageUrl(String url){
        View customMarkerView = ((LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.image_marker, null);
        ImageView ivMarker = (ImageView) customMarkerView.findViewById(R.id.ivPhotoMarker);
        Picasso.with(context).load(url).into(ivMarker);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null){
            drawable.draw(canvas);
        }
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

}
