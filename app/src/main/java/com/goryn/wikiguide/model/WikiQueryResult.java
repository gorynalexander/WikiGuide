package com.goryn.wikiguide.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WikiQueryResult {


    @SerializedName("batchcomplete")
    @Expose
    public Boolean batchcomplete;
    @SerializedName("query")
    @Expose
    public WikiQuery query;

    public Boolean getBatchcomplete() {
        return batchcomplete;
    }

    public WikiQuery getQuery() {
        return query;
    }

}
