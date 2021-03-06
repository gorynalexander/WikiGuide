package com.goryn.wikiguide.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.goryn.wikiguide.App;
import com.goryn.wikiguide.R;
import com.goryn.wikiguide.model.Page;
import com.goryn.wikiguide.model.QueryResult;
import com.goryn.wikiguide.ui.fragments.MapFragment;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<Page> pagesList = new ArrayList<>();
    private Context context;


    public PlacesAdapter(Context context, List<Page> pagesList) {
        this.context = context;
        checkData(pagesList);
    }

    public void setPagesList(List<Page> pagesList) {
        this.pagesList.clear();
        checkData(pagesList);
        notifyDataSetChanged();
    }

    private void checkData(List<Page> pagesList) {
        Iterator<Page> iterator = pagesList.iterator();
        while (iterator.hasNext()) {
            Page page = iterator.next();
            if (page.getThumbUrl() == null) {
                Log.i("tag", page.getTitle());
                iterator.remove();
            }
            LatLng userPos = App.getLocationManager().getCurrentLatLng();
            LatLng placePos = new LatLng(page.getCoordinates().get(0).getLat() , page.getCoordinates().get(0).getLon());

            Double distanceBetweetPlaces = SphericalUtil.computeDistanceBetween(userPos, placePos) / 1000;
            page.setDistanceToUser(distanceBetweetPlaces);

            Collections.sort(pagesList);

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
        final Page page = pagesList.get(position);

        holder.tvPlaceDescription.setVisibility(View.GONE);
        String dist = new DecimalFormat("#.##").format(page.getDistanceToUser());

        holder.tvPlaceTitle.setText(page.getTitle());
        holder.tvPlaceDistance.setText(dist + "km.");


        Glide.with(holder.itemView.getContext())
                .load(page.getFullImage())
                .into(holder.ivPlaceImage);


        holder.btnPlaceReadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInfoAboutPage(page, holder);

            }
        });

        holder.btnPlaceNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putSerializable("page",page);
                MapFragment fragment = MapFragment.newInstance(0, bundle);
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment)
                        .commit();
            }
        });
    }

    private void loadInfoAboutPage(Page page, final ViewHolder holder) {
        if (holder.tvPlaceDescription.getVisibility() == View.GONE) {
            String title = page.getTitle();
            String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=1&formatversion=2&titles=" + title;
            RequestQueue queue = Volley.newRequestQueue(holder.itemView.getContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("WIKIGUIDE_RESPONSE", response);
                    QueryResult result;
                    Gson gson = new Gson();
                    result = gson.fromJson(response, QueryResult.class);
                    holder.tvPlaceDescription.setVisibility(View.VISIBLE);
                    String text = result.getQuery().getPages().get(0).getExtract().replaceAll("\\<.*?>", "");
                    text = text.trim();
                    holder.tvPlaceDescription.setText(text);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(stringRequest);
        } else {
            holder.tvPlaceDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return pagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaceTitle;
        TextView tvPlaceDescription;
        ImageView ivPlaceImage;
        TextView tvPlaceDistance;

        Button btnPlaceReadmore;
        Button btnPlaceNavigate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPlaceTitle = (TextView) itemView.findViewById(R.id.tv_place_title);
            tvPlaceDescription = (TextView) itemView.findViewById(R.id.tv_place_description);
            ivPlaceImage = (ImageView) itemView.findViewById(R.id.iv_place_image);
            btnPlaceReadmore = (Button) itemView.findViewById(R.id.btn_place_readmore);
            tvPlaceDistance = (TextView) itemView.findViewById(R.id.tv_place_distance);
            btnPlaceNavigate = (Button) itemView.findViewById(R.id.btn_place_navigate);
        }
    }
}
