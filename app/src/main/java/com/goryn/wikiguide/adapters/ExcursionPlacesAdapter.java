package com.goryn.wikiguide.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.ExcursionPlace;
import com.goryn.wikiguide.ui.fragments.ExcursionPlacesFragment;
import com.goryn.wikiguide.ui.fragments.MapFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Odinn on 20.01.2018.
 */

public class ExcursionPlacesAdapter extends RecyclerView.Adapter<ExcursionPlacesAdapter.ViewHolder> {
    private List<ExcursionPlace> excursionPlaces = new ArrayList<>();
    private Context context;

    public ExcursionPlacesAdapter (Context context, List<ExcursionPlace> excursionPlaces) {
        this.context = context;
        this.excursionPlaces = excursionPlaces;
    }

    @Override
    public ExcursionPlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_excursion_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExcursionPlacesAdapter.ViewHolder holder, int position) {
        final ExcursionPlace excursionPlace = excursionPlaces.get(position);

        Glide.with(context)
                .load(excursionPlace.getThumbURL())
                .into(holder.ivExcursionPlace);
        int count = position + 1;
        if (excursionPlace.getPlaceTitle() != null) {
            holder.tvTitle.setText(count + ") " + excursionPlace.getPlaceTitle());
        }

        holder.tvDesc.setText("Random text cuz i've forgotten to send it to the server");

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("excursionPlacesToShow", (ArrayList<? extends Parcelable>) excursionPlaces);
                transaction.replace(R.id.container, MapFragment.newInstance(2, bundle));
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return excursionPlaces.size();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        CircleImageView ivExcursionPlace;
        TextView tvTitle, tvDesc;
        RelativeLayout layoutItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ivExcursionPlace = (CircleImageView) itemView.findViewById(R.id.iv_excursion_place);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_excursion_place_title);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_excursion_place_desc);
            layoutItem = (RelativeLayout) itemView.findViewById(R.id.layout_item_excursion_place);
        }
    }


}
