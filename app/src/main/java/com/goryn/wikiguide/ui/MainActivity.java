package com.goryn.wikiguide.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import android.view.Menu;
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
import com.google.android.gms.location.SettingsApi;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.QueryResult;
import com.goryn.wikiguide.ui.fragments.GameMapFragment;
import com.goryn.wikiguide.ui.fragments.PlacesFragment;
import com.goryn.wikiguide.utils.NetworkBroadcastReceiver;
import com.goryn.wikiguide.utils.WikiQueryService;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    Toolbar toolbar;

    FragmentManager fragmentManager;
    Fragment fragment;

    /*  Location   */
    private GoogleApiClient googleApiClient;
    private LatLng currentLatLng;


    /* Fragments */
    private PlacesFragment placesFragment;

    private NetworkBroadcastReceiver broadcastReceiver;


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
                        fragment = placesFragment;
                        break;
                    case R.id.nav_map:
                        fragment = new GameMapFragment();
                        break;
                    case R.id.nav_settings:
                  //      Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                     //   startActivity(intent);
                        break;
                }

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

        Call<QueryResult> call = service.request(geo, 5000, 144);


        call.enqueue(new retrofit2.Callback<QueryResult>() {
            @Override
            public void onResponse(Call<QueryResult> call, retrofit2.Response<QueryResult> response) {
                App.setQuery(response.body().getQuery());
                placesFragment.notifyDataFromActivity();
            }

            @Override
            public void onFailure(Call<QueryResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        App.getLocationManager().startLocationUpdates();
        currentLatLng = new LatLng(App.getLocationManager().getCurrentLocation().getLatitude(), App.getLocationManager().getCurrentLocation().getLongitude());

        makeRequestToWiki(currentLatLng);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_update:
                makeRequestToWiki(currentLatLng);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}

