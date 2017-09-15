package com.goryn.wikiguide.utils;

import android.support.annotation.NonNull;

import com.goryn.wikiguide.model.QueryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WikiQueryService {
    @GET("w/api.php?action=query&format=json&prop=coordinates|pageimages|pageterms&generator=geosearch&formatversion=2&colimit=50&piprop=thumbnail&pilimit=50&wbptterms=description&ggslimit=50")
    Call<QueryResult> request(@NonNull @Query("ggscoord") String coord,
                              @Query("ggsradius") double radius,
                              @Query("pithumbsize") int thumbsize);

}
