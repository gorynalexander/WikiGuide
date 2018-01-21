package com.goryn.wikiguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.ExcursionPlace;

import org.w3c.dom.Text;

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

        if (excursionPlace.getPlaceTitle() != null) {
            holder.tvTitle.setText(excursionPlace.getPlaceTitle());
        }

        holder.tvDesc.setText("Random text cuz i've forgot to send it to the server");

    }

    @Override
    public int getItemCount() {
        return excursionPlaces.size();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        CircleImageView ivExcursionPlace;
        TextView tvTitle, tvDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            ivExcursionPlace = (CircleImageView) itemView.findViewById(R.id.iv_excursion_place);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_excursion_place_title);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_excursion_place_desc);
        }
    }


}
