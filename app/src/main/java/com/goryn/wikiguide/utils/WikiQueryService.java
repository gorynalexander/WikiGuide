package com.goryn.wikiguide.utils;

import android.support.annotation.NonNull;

import com.goryn.wikiguide.model.QueryResult;
import com.goryn.wikiguide.model.WikiQueryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Odinn on 10.08.2017.
 */

public interface WikiQueryService {
    @GET("w/api.php?action=query&format=json&prop=coordinates|pageimages|pageterms&generator=geosearch&formatversion=2&colimit=50&piprop=thumbnail&pilimit=50&wbptterms=description&ggslimit=50")
    Call<QueryResult> request(@NonNull @Query("ggscoord") String coord,
                              @Query("ggsradius") double radius,
                              @Query("pithumbsize") int thumbsize);

    @GET("w/api.php?action=query&format=json&prop=extracts&exintro=1&formatversion=2")
    Call<WikiQueryResult> request(@NonNull @Query("titles") String titles);

    //titles=Luzanivka+Hydropark%7CNorth+Odessa+Cape&formatversion=2&
}
