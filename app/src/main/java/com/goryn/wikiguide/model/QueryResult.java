package com.goryn.wikiguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Odinn on 10.08.2017.
 */

public class QueryResult {

    @SerializedName("batchcomplete")
    @Expose
    private boolean batchcomplete;

    @SerializedName("query")
    @Expose
    private Query query;

    public boolean isBatchcomplete() {
        return batchcomplete;
    }
    public Query getQuery() {
        return query;
    }
}
