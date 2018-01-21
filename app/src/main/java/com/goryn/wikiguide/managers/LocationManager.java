package com.goryn.wikiguide.managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.ExcursionPlace;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.Query;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


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

        //setMarkers(App.getQuery());

    }

    public void loadExcursion(List<ExcursionPlace> excursionPlaces){

        final ImageLoader imageLoader = ImageLoader.getInstance();
        if (markers != null) {
            for (Marker marker : markers) {
                marker.remove();
            }
        }

        for (final ExcursionPlace place : excursionPlaces) {
            imageLoader.loadImage(place.getThumbURL(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    Marker marker = googleMap.addMarker(createMarkerOptions(place, createBitmapFromView(loadedImage)));
                    markers.add(marker);
                }
            });
        }
    }

    public void loadImages() {


        List<Page> pages = App.getQuery().getPages();
        final ImageLoader imageLoader = ImageLoader.getInstance();

        // Updating list of markers to avoid copies
        if (markers != null) {
            for (Marker marker : markers) {
                marker.remove();
            }
        }

        for (final Page page : pages) {
            imageLoader.loadImage(page.getThumbUrl(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    Marker marker = googleMap.addMarker(createMarkerOptions(page, createBitmapFromView(loadedImage)));
                    markers.add(marker);
                }
            });
        }
    }


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public LatLng getCurrentLatLng() {
        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    public com.google.maps.model.LatLng getCurrentUserLatLng() {
        return new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
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

    public void removeUserCirce(){
        userCircle.remove();
    }

    public void removeUserMarker() {
        userMarker.remove();
        userCircle.remove();
    }

    public void setUserCircleRadius(int radius) {
        circleRadius = radius;
        if (userCircle != null) {
            userCircle.setRadius(radius);
        }
    }



    public MarkerOptions createMarkerOptions(Page page, Bitmap bitmap) {
        return new MarkerOptions()
                .position(new LatLng(page.getCoordinates().get(0).getLat(), page.getCoordinates().get(0).getLon()))
                .title(page.getTitle())
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    public MarkerOptions createMarkerOptions(ExcursionPlace place, Bitmap bitmap) {
        return new MarkerOptions()
                .position(new LatLng(place.getLat(), place.getLon()))
                .title(place.getPlaceTitle())
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }


    private Bitmap createBitmapFromView(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.image_marker, null);
        CircleImageView ivMarker = (CircleImageView) customMarkerView.findViewById(R.id.ivPhotoMarker);

        ivMarker.setImageBitmap(bitmap);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null) {
            drawable.draw(canvas);
        }
        customMarkerView.draw(canvas);

        return returnedBitmap;
    }

}
