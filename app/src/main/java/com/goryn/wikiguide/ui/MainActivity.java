package com.goryn.wikiguide.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import android.support.annotation.NonNull;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.QueryResult;
import com.goryn.wikiguide.ui.fragments.MapFragment;
import com.goryn.wikiguide.ui.fragments.PlacesFragment;
import com.goryn.wikiguide.utils.NetworkBroadcastReceiver;
import com.goryn.wikiguide.utils.WikiQueryService;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                SharedPreferences.OnSharedPreferenceChangeListener {

    FragmentManager fragmentManager;
    Fragment fragment;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    /*  Location   */
    private GoogleApiClient googleApiClient;
    private LatLng currentLatLng;


    /* Fragments */
    private PlacesFragment placesFragment;

    private NetworkBroadcastReceiver broadcastReceiver;

    private int placesCount;
    private int radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavDrawer();

        initPreferences();
        buildGoogleAPiClient();

        //broadcastReceiver = new NetworkBroadcastReceiver();
        //broadcastReceiver.setActivityHandler(this);
        //registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // TODO : UNREGISTER BROADCAST RECIEVING

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(0);
        }

    }

    private void initPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        radius = Integer.parseInt(sharedPreferences.getString("pref_radius", "5000"));
        App.getLocationManager().setUserCircleRadius(radius);
        placesCount = Integer.parseInt(sharedPreferences.getString("pref_count", "25"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        placesFragment = new PlacesFragment();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawer,         /* DrawerLayout object */
                toolbar,
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawer.addDrawerListener(mDrawerToggle);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, placesFragment);
        transaction.commit();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                item.setChecked(true);
                switch (id) {
                    case R.id.action_nav_main:
                        fragment = placesFragment;
                        break;
                    case R.id.action_nav_map:
                        fragment = new MapFragment();
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


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        Log.i("NEW REQUEST", ""+ placesCount);
        Call<QueryResult> call = service.request(geo, radius, 144, placesCount);


        call.enqueue(new retrofit2.Callback<QueryResult>() {
            @Override
            public void onResponse(Call<QueryResult> call, retrofit2.Response<QueryResult> response) {
                App.setQuery(response.body().getQuery());
                placesFragment.notifyDataFromActivity();
                // TODO: UPDATE MAP IF IT'S ALIVE

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
        //unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_update:
                makeRequestToWiki(currentLatLng);
                App.getLocationManager().setMarkers(App.getQuery());
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_count_key))){
            placesCount = Integer.parseInt(sharedPreferences.getString("pref_count", "25"));
            makeRequestToWiki(App.getLocationManager().getCurrentLatLng());
            App.getLocationManager().setMarkers(App.getQuery());

        } else if (key.equals(getString(R.string.pref_radius_key))){
            radius = Integer.parseInt(sharedPreferences.getString("pref_radius", "5000"));
            App.getLocationManager().setUserCircleRadius(radius);
            makeRequestToWiki(App.getLocationManager().getCurrentLatLng());
        }
    }
}

