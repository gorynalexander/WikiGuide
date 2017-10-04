package com.goryn.wikiguide.ui.fragments;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.managers.LocationManager;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.Query;
import com.goryn.wikiguide.model.QueryResult;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MapFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    MapView map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        map = (MapView) view.findViewById(R.id.mapView);
        map.onCreate(savedInstanceState);
        Log.i("FRAGMENT", "onCreateView");

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                App.getLocationManager().updateMap(googleMap);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        com.google.maps.model.LatLng placePos = new com.google.maps.model.LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                        setMarkerInfo(marker);
                        DirectionsResult results = requestDirection(TravelMode.WALKING,
                                App.getLocationManager().getCurrentUserLatLng(),
                                placePos);
                        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
                        googleMap.addPolyline(new PolylineOptions().addAll(decodedPath));
                        return true;
                    }
                });
                App.getLocationManager().setMarkers(App.getQuery());

            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Log.i("FRAGMENT", "onResume");
    }

    private GeoApiContext geoApiContextBuilder() {
        GeoApiContext geoApiContext = new GeoApiContext();
        geoApiContext.setApiKey(getString(R.string.directions_api_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);

        return geoApiContext;
    }

    private DirectionsResult requestDirection(TravelMode travelMode, com.google.maps.model.LatLng user, com.google.maps.model.LatLng destination) {
        DateTime time = DateTime.now();
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(geoApiContextBuilder())
                    .mode(travelMode)
                    .origin(user)
                    .destination(destination)
                    .departureTime(time)
                    .await();
        } catch (ApiException e) {
            Log.e("ERROR_DIRECTION", e.getMessage());
        } catch (InterruptedException e) {
            Log.e("ERROR_DIRECTION", e.getMessage());
        } catch (IOException e) {
            Log.e("ERROR_DIRECTION", e.getMessage());
        }
        return result;
    }

    @Override
    public void onPause() {
        map.onPause();
        super.onPause();
        Log.i("FRAGMENT", "onPause");
        App.getLocationManager().removeUserMarker();
    }

    @Override
    public void onDestroy() {
        map.onDestroy();
        super.onDestroy();
        Log.i("FRAGMENT", "onDestroy");
    }

    @Override
    public void onLowMemory() {
        map.onLowMemory();
        super.onLowMemory();
    }

    private void setMarkerInfo(final Marker marker) {
        if (marker.getTitle().equals("Your pos")) {
            return;
        }

        String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts|pageimages&piprop=thumbnail&pithumbsize=700&exintro=1&formatversion=2&titles=" + marker.getTitle();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                QueryResult result;
                Gson gson = new Gson();
                result = gson.fromJson(response, QueryResult.class);
                String text = result.getQuery().getPages().get(0).getExtract().replaceAll("\\<.*?>", "");
                text = text.trim();


                DialogPlus dialog = DialogPlus.newDialog(getContext())
                        .setExpanded(true)
                        .setContentHolder(new ViewHolder(R.layout.dialog_place_info))
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT + 200)
                        .create();

                View view = dialog.getHolderView();
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_place_title);
                TextView tvExtract = (TextView) view.findViewById(R.id.tv_dialog_place_extract);
                ImageView ivImage = (ImageView) view.findViewById(R.id.iv_dialog_place_image);

                tvExtract.setText(text);
                Picasso.with(getContext()).load(result.getQuery().getPages().get(0).getThumbUrl()).into(ivImage);
                tvTitle.setText(marker.getTitle());
                dialog.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        App.getLocationManager().setMarkers(App.getQuery());
    }
}
