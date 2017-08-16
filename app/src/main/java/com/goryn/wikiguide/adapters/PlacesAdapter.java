package com.goryn.wikiguide.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Page;
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
    }

    @Override
    public int getItemCount() {
        return pagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPlaceTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            tvPlaceTitle = (TextView) itemView.findViewById(R.id.tv_place_title);
        }
    }
}
