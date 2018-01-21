package com.goryn.wikiguide.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.adapters.ExcursionsAdapter;
import com.goryn.wikiguide.model.Excursion;
import com.goryn.wikiguide.model.ExcursionPlace;


public class ExcursionsFragment extends Fragment {

    private DatabaseReference mDatabase;
    private RecyclerView rvExcursions;
    private ExcursionsAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        View view = inflater.inflate(R.layout.fragment_places_list, container, false);
        rvExcursions = (RecyclerView) view.findViewById(R.id.rv_places_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvExcursions.setLayoutManager(layoutManager);
        adapter = new ExcursionsAdapter(getContext());
        rvExcursions.setAdapter(adapter);

        if (getActivity().getActionBar() != null){
            getActivity().getActionBar().setTitle("Excursions");
        }
        readDatabase();

        return view;
    }

    private void readDatabase() {
        mDatabase.child("excursions").addChildEventListener(childEventListener);

    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Excursion excursion = dataSnapshot.getValue(Excursion.class);
            adapter.addExcursion(excursion);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };
}
