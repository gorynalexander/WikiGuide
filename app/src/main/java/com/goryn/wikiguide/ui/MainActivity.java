package com.goryn.wikiguide.ui;

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
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.ui.fragments.GameMapFragment;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    FragmentManager fragmentManager;
    Fragment fragment;

    /*  Location    */
    private Location myLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient apiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavDrawer();

    }

    private void initNavDrawer() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle); //TODO check why deprecated
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, new GameMapFragment());
        transaction.commit();
        // TODO поставить маркер того, что айтем выбран

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_main:
                        Toast.makeText(MainActivity.this, "Main", Toast.LENGTH_SHORT).show();
//                        fragment = new PlacesFragment();
                        break;
                    case R.id.nav_map:
                        Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
//                        fragment = new GameMapFragment();
                        break;
                }
//
//                final FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.replace(R.id.container, fragment)
//                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }


    private void makeRequestToWiki() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String gscoord = "46.5844002|30.7768015";
        String gsradius = "10000";
//        String url = "https://en.wikipedia.org/w/api.php?action=query&list=geosearch&gscoord=37.786952|-122.399523&gsradius=10000&gslimit=10&format=json";
        String url = String.format("https://en.wikipedia.org/w/api.php?action=query&list=geosearch&gscoord=%1$s&gsradius=%2$s&gslimit=10&format=json", gscoord, gsradius);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });


        queue.add(stringRequest);
    }
}
