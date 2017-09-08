package com.goryn.wikiguide.utils;

import android.support.annotation.NonNull;

import com.goryn.wikiguide.model.WikiQueryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Саша on 07.09.2017.
 */

public interface WikiPagesService {

    @GET("/w/api.php?action=query&format=json&prop=extracts&exintro=1&formatversion=2&")
    Call<WikiQueryResult> request(@NonNull @Query("titles") String titles);
}
