package com.goryn.wikiguide.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goryn.wikiguide.R;
import com.goryn.wikiguide.adapters.ExcursionPlacesAdapter;
import com.goryn.wikiguide.model.ExcursionPlace;
import com.goryn.wikiguide.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class ExcursionPlacesFragment extends Fragment {

    //private List<ExcursionPlace> excursionPlaces = new ArrayList<>();
    RecyclerView recyclerView;

    public static ExcursionPlacesFragment newInstance(ArrayList<ExcursionPlace> excursionPlaces, String title) {
        ExcursionPlacesFragment excursionPlacesFragment = new ExcursionPlacesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("toolbar_title", title);
        bundle.putParcelableArrayList("excursion_places", excursionPlaces);
        excursionPlacesFragment.setArguments(bundle);

        return excursionPlacesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.fragment_places_list, container, false);

        String toolbarTitle = getArguments().getString("toolbar_title");
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity.getSupportActionBar() != null) {
            mainActivity.setDrawerEnabled(false);
            ActionBar actionBar = mainActivity.getSupportActionBar();
            actionBar.setTitle(toolbarTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        List<ExcursionPlace> excursionPlaces = getArguments().getParcelableArrayList("excursion_places");

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_places_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        ExcursionPlacesAdapter excursionPlacesAdapter = new ExcursionPlacesAdapter(getContext(), excursionPlaces);
        recyclerView.setAdapter(excursionPlacesAdapter);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(getContext(), item.getItemId(), Toast.LENGTH_SHORT).show();
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
