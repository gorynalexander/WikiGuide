package com.goryn.wikiguide.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goryn.wikiguide.R;

/**
 * Created by Odinn on 28.07.2017.
 */

public class PlacesFragment extends Fragment {
    TextView tvResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_list, container, false);
        tvResponse = (TextView) view.findViewById(R.id.tv_response);
        tvResponse.setText("MAGA PIDORAS");

        return view;
    }
}
