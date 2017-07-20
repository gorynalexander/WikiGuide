package com.goryn.wikiguide.fragments;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Odinn on 19.07.2017.
 */

public class MapFragment extends Fragment {
    public static MapFragment getInstance(Bundle b){
        MapFragment fragment = new MapFragment();
        fragment.setArguments(b);
        return fragment;
    }
}
