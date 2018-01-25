package com.goryn.wikiguide.adapters;

import android.app.Activity;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Excursion;
import com.goryn.wikiguide.model.ExcursionPlace;
import com.goryn.wikiguide.ui.fragments.ExcursionPlacesFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class ExcursionsAdapter extends RecyclerView.Adapter<ExcursionsAdapter.ViewHolder> {
    private List<Excursion> excursions = new ArrayList<>();
    private Context context;

    public ExcursionsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ExcursionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_excursion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExcursionsAdapter.ViewHolder holder, int position) {
        final Excursion excursion = excursions.get(position);
        double distKm = excursion.getDistance();
        String dist = new DecimalFormat("#.#").format(distKm);
        holder.title.setText(excursion.getTitle() +" ("+ dist+ "km. from you)");
        StringBuilder places = new StringBuilder();
        Iterator<ExcursionPlace> iterator = excursion.getExcursionPlaces().iterator();
        while (iterator.hasNext()) {
            places.append(iterator.next().getPlaceTitle()).append(" ");
        }
        holder.desc.setText(places);

        holder.exc_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // init fragment
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, ExcursionPlacesFragment.newInstance((ArrayList<ExcursionPlace>) excursion.getExcursionPlaces(), excursion.getTitle()));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return excursions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        LinearLayout exc_item;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_excrursion_title);
            desc = (TextView) itemView.findViewById(R.id.tv_excrursion_description);
            exc_item = (LinearLayout) itemView.findViewById(R.id.ll_exc);
        }
    }

    public void addExcursion(Excursion excursion) {
        excursions.add(excursion);
        sortCollection();
    }

    public void sortCollection(){
        Collections.sort(excursions);
        notifyDataSetChanged();
    }
}
