package com.goryn.wikiguide.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Page;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder>{

    List<Page> pagesList;

    public PlacesAdapter(List<Page> pagesList) {
        this.pagesList = pagesList;
    }

    public void setPagesList(List<Page> pagesList){
        this.pagesList = pagesList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Page page = pagesList.get(position);
        holder.tvPlaceTitle.setText(page.getTitle());


//        if (page.getTerms().getDescription() != null) holder.tvPlaceDescription.setText(page.getTerms().getDescription().get(0));
//
//        Picasso.with(holder.itemView.getContext())
//                .load(page.getThumbnail().getSource())
//                .into(holder.ivPlaceImage);
    }

    @Override
    public int getItemCount() {
        return pagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPlaceTitle;
        TextView tvPlaceDescription;
        ImageView ivPlaceImage;
        public ViewHolder(View itemView) {
            super(itemView);
            tvPlaceTitle = (TextView) itemView.findViewById(R.id.tv_place_title);
            tvPlaceDescription = (TextView) itemView.findViewById(R.id.tv_place_description);
            ivPlaceImage = (ImageView) itemView.findViewById(R.id.iv_place_image);
        }
    }
}
