package com.goryn.wikiguide.ui;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.QueryResult;
import com.goryn.wikiguide.model.WikiQuery;
import com.goryn.wikiguide.model.WikiQueryResult;
import com.goryn.wikiguide.ui.fragments.GameMapFragment;
import com.goryn.wikiguide.ui.fragments.PlacesFragment;
import com.goryn.wikiguide.utils.NetworkBroadcastReceiver;
import com.goryn.wikiguide.utils.WikiQueryService;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    Toolbar toolbar;

    FragmentManager fragmentManager;
    Fragment fragment;

    /*  Location   */
    private Location myLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient googleApiClient;


    /* Fragments */
    private PlacesFragment placesFragment;

    private NetworkBroadcastReceiver broadcastReceiver;

    /*Retrofit */
//    private WikiQueryService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavDrawer();

        buildGoogleAPiClient();

        broadcastReceiver = new NetworkBroadcastReceiver();
        broadcastReceiver.setActivityHandler(this);
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // TODO : UNREGISTER BROADCAST RECIEVING

    }

    private void buildGoogleAPiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, 0, this)
                .build();

        App.getGoogleApiHelper().setGoogleApiClient(googleApiClient);
        App.getGoogleApiHelper().connect();

    }

    private void initNavDrawer() {
        placesFragment = new PlacesFragment();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle); //TODO check why deprecated
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, placesFragment);
        transaction.commit();
        // TODO поставить маркер того, что айтем выбран

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_main:
//                        Toast.makeText(MainActivity.this, "Main", Toast.LENGTH_SHORT).show();
                        fragment = placesFragment;
                        break;
                    case R.id.nav_map:
//                        Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
                        fragment = new GameMapFragment();
                        break;
                }
//
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private Retrofit retrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void makeRequestToWiki(LatLng latLng) {
        Retrofit retrofit = retrofitBuilder();
        WikiQueryService service = retrofit.create(WikiQueryService.class);

        String geo = String.format(Locale.ROOT, "%f|%f", latLng.latitude, latLng.longitude);

        Call<QueryResult> call = service.request(geo, 10000, 1000);


        call.enqueue(new retrofit2.Callback<QueryResult>() {
            @Override
            public void onResponse(Call<QueryResult> call, retrofit2.Response<QueryResult> response) {
//                Toast.makeText(MainActivity.this, "" + response.body().getQuery().getPages().get(0).getThumbUrl(), Toast.LENGTH_SHORT).show();
                App.setQuery(response.body().getQuery());

                loadWikiPages(response.body().getQuery().getPages());
            }

            @Override
            public void onFailure(Call<QueryResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWikiPages(List<Page> pages) {
        String titles = "";
        for (int i = 0; i < pages.size(); i++) {
            if (i == pages.size() - 1) {
                titles += pages.get(i).getTitle();
            } else titles += pages.get(i).getTitle() + "|";
        }
        String requestTitles = titles.replaceAll(" ", "%20");
        Log.i("WIKIGUIDE", requestTitles);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=1&formatversion=2&titles=Port of Odessa|North Odessa Cape|Kuyalnik Estuary|Dofinivka Estuary|Luzanivka Hydropark";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("WIKIGUIDE_RESPONSE", response);
                WikiQueryResult result;
                Gson gson = new Gson();
                result = gson.fromJson(response, WikiQueryResult.class);
//                Log.i("WIKIGUIDE_RESPONSE", result.getQuery().getWikiPages().get(0).getTitle());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);

//        Retrofit retrofit = retrofitBuilder();
//        WikiQueryService service = retrofit.create(WikiQueryService.class);
//        Call<WikiQueryResult> call = service.request(requestTitles);
//        call.enqueue(new Callback<WikiQueryResult>() {
//            @Override
//            public void onResponse(Call<WikiQueryResult> call, retrofit2.Response<WikiQueryResult> response) {
//                // Toast.makeText(MainActivity.this, response.body().query.getWikiPages().get(0).getTitle(), Toast.LENGTH_SHORT).show();
//                Gson gson = new Gson();
//
//                Log.i("WIKIGUIDE", gson.toJson(response));
//                // TODO If batchcomplete response.body().getQuery().getWikiPages().get(0).getTitle()
////                App.setWikiQuery(response.body().query);
////                placesFragment.notifyDataFromActivity();
//            }
//
//            @Override
//            public void onFailure(Call<WikiQueryResult> call, Throwable t) {
//                Log.e("WIKIGUIDE_ERROR", "ERROR WHEN CALLING WIKI PAGES");
//            }
//        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        App.getLocationManager().startLocationUpdates();
        LatLng latLng = new LatLng(App.getLocationManager().getCurrentLocation().getLatitude(), App.getLocationManager().getCurrentLocation().getLongitude());
        String str = Double.toString(latLng.latitude) + Double.toString(latLng.longitude);
        toolbar.setTitle(str);
//        App.getLocationManager().setUserMarker();
        makeRequestToWiki(latLng);

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i("WikiGuide", "Connection lost.  Reason: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i("WikiGuide", "Connection lost.  Reason: Service Disconnected");
        } else {
            Log.d("WikiGuide", "onConnectionSuspended: reason " + i);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("WikiGuide", "onConnectionFailed: connectionResult.toString() = " + connectionResult.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
}

