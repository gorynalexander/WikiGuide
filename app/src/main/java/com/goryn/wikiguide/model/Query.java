package com.goryn.wikiguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Odinn on 10.08.2017.
 */

public class Query {

    @SerializedName("pages")
    @Expose
    private List<Page> pages = null;

    public List<Page> getPages() {
        return pages;
    }

}
