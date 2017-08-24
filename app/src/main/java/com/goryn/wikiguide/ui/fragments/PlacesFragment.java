package com.goryn.wikiguide.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.adapters.PlacesAdapter;
import com.goryn.wikiguide.model.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Odinn on 28.07.2017.
 */

public class PlacesFragment extends Fragment {

    RecyclerView rvPlaces;
    PlacesAdapter rvPlacesAdapter;
    List<Page> pagesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_list, container, false);
        rvPlaces = (RecyclerView) view.findViewById(R.id.rv_places_list);
        if (App.getQuery().getPages() != null) {
            pagesList = App.getQuery().getPages();
            Toast.makeText(getContext(),""+ App.getQuery().getPages().size(), Toast.LENGTH_SHORT).show();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            rvPlaces.setLayoutManager(layoutManager);
            rvPlacesAdapter = new PlacesAdapter(pagesList);
            rvPlaces.setAdapter(rvPlacesAdapter);
        } else {
            Toast.makeText(getContext(), "NULL", Toast.LENGTH_SHORT).show();
            // TODO SHOW MESSAGE THAT NO PLACES DETECTED OR BAD CONNECTION
        }
        return view;
    }



    public void notifyDataFromActivity(){
        rvPlacesAdapter.setPagesList(App.getQuery().getPages());
    }
}
