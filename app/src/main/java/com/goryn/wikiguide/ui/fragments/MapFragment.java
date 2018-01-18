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
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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
import com.goryn.wikiguide.model.ExcursionPlace;
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


public class MapFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    private MapView map;
    private Polyline polylineToPlace;
    private  List<ExcursionPlace> excursionPlaces = new ArrayList<>();
    private int order;

    /*
    ** Bottom sheet items
     */
    private TextView tvPlaceInfoTitle;
    private TextView tvPlaceInfoExtract;
    private ImageView ivPlaceInfoImage;
    private Button btnNavigation;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout bottomSheetHeader;

    private static int MODE_DEFAULT = 0;
    private static int MODE_EXCURSION = 1;
    private int mode = MODE_DEFAULT;

    private int actionId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        map = (MapView) view.findViewById(R.id.mapView);
        map.onCreate(savedInstanceState);

        tvPlaceInfoTitle = (TextView) view.findViewById(R.id.tv_dialog_place_title);
        tvPlaceInfoExtract = (TextView) view.findViewById(R.id.tv_dialog_place_extract);
        ivPlaceInfoImage = (ImageView) view.findViewById(R.id.iv_dialog_place_image);
        btnNavigation = (Button) view.findViewById(R.id.btn_dialog_navigate);

        bottomSheet = (LinearLayout) view.findViewById(R.id.bottom_sheet_place_details);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetHeader = (RelativeLayout) view.findViewById(R.id.bottom_sheet_header);
        bottomSheetHeader.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

                        if (mode == MODE_DEFAULT) {

                            setMarkerInfo(marker, googleMap);

                            marker.showInfoWindow();
                        } else if (mode == MODE_EXCURSION){
                            addPlaceToExcursion(marker);
                        }

                        return true;
                    }
                });

            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void addPlaceToExcursion(Marker marker) {
        Log.i("Image URL", App.getQuery().getImageURLByTitle(marker.getTitle()));
        excursionPlaces.add(new ExcursionPlace(marker.getTitle(), "",   marker.getPosition().latitude,  marker.getPosition().longitude));
        Toast.makeText(getContext(), "Place '"+ marker.getTitle() + "' was added to the excursion list as #"+excursionPlaces.size(), Toast.LENGTH_LONG).show();
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
        App.getLocationManager().removeUserMarker();
    }

    @Override
    public void onDestroy() {
        map.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        map.onLowMemory();
        super.onLowMemory();
    }

    private void setMarkerInfo(final Marker marker, final GoogleMap googleMap) {
        if (marker.getTitle().equals("You! :)")) {
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


                btnNavigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (polylineToPlace != null) polylineToPlace.remove();

                        com.google.maps.model.LatLng placePos =
                                new com.google.maps.model.LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                        DirectionsResult results = requestDirection(TravelMode.WALKING,
                                App.getLocationManager().getCurrentUserLatLng(),
                                placePos);
                        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
                        polylineToPlace = googleMap.addPolyline(new PolylineOptions().addAll(decodedPath));

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(App.getLocationManager().getCurrentLatLng().latitude, App.getLocationManager().getCurrentLatLng().longitude), 12.0f));
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
                tvPlaceInfoExtract.setText(text);
                //TODO: пофиксить картинку
                Picasso.with(getContext()).load(result.getQuery().getPages().get(0).getThumbUrl()).into(ivPlaceInfoImage);

                Glide.with(getContext())
                        .load(result.getQuery().getPages().get(0).getThumbUrl())
                        .into(ivPlaceInfoImage);

                tvPlaceInfoTitle.setText(marker.getTitle());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


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
        App.getLocationManager().loadImages();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bottom_sheet_header) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);

        MenuItem createExcursion = menu.add(0, 0, 3, "Create Excursion");
        createExcursion.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        actionId = createExcursion.getItemId();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == actionId) {
            if (mode == MODE_DEFAULT) {
                Toast.makeText(getContext(), "Choose places in order", Toast.LENGTH_LONG).show();
                mode = MODE_EXCURSION;
                item.setTitle("Save Excursion");
            } else if(mode == MODE_EXCURSION){
                // SAVING EXCURSION TO DB
                mode = MODE_DEFAULT;
                item.setTitle("Create Excursion");
                excursionPlaces = new ArrayList<>();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
}
