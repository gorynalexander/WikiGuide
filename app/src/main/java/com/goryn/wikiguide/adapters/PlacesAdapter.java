package com.goryn.wikiguide.adapters;

import android.support.annotation.Dimension;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.WikiPage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    List<Page> pagesList;
    List<WikiPage> wikiPagesList;

    public PlacesAdapter(List<Page> pagesList) {
        checkData(pagesList, wikiPagesList);

    }

    public void setPagesList(List<Page> pagesList, List<WikiPage> wikiPagesList) {
        checkData(pagesList, wikiPagesList);

        notifyDataSetChanged();
    }

    private void checkData(List<Page> pagesList, List<WikiPage> wikiPagesList) {
        Iterator<Page> iterator = pagesList.iterator();
        while (iterator.hasNext()) {
            Page page = iterator.next();
            if (page.getThumbUrl() == null) {
                iterator.remove();
            }
        }
        this.pagesList = pagesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Page page = pagesList.get(position);

        holder.tvPlaceDescription.setVisibility(View.GONE);
        String text = page.getExtract().replaceAll("\\<.*?>","");
        text = text.trim();
        holder.tvPlaceDescription.setText(text);

        holder.tvPlaceTitle.setText(page.getTitle());


//        if (page.getTerms().getDescription() != null) holder.tvPlaceDescription.setText(page.getTerms().getDescription().get(0));

        Picasso.with(holder.itemView.getContext())
                .load(page.getThumbUrl())
                .into(holder.ivPlaceImage);
        holder.btnPlaceReadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tvPlaceDescription.getVisibility() == View.GONE){
                    holder.tvPlaceDescription.setVisibility(View.VISIBLE);
                } else {
                    holder.tvPlaceDescription.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaceTitle;
        TextView tvPlaceDescription;
        ImageView ivPlaceImage;

        Button btnPlaceReadmore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPlaceTitle = (TextView) itemView.findViewById(R.id.tv_place_title);
            tvPlaceDescription = (TextView) itemView.findViewById(R.id.tv_place_description);
            ivPlaceImage = (ImageView) itemView.findViewById(R.id.iv_place_image);
            btnPlaceReadmore = (Button) itemView.findViewById(R.id.btn_place_readmore);
        }
    }
}
