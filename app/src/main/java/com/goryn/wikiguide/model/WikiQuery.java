package com.goryn.wikiguide.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class WikiQuery {
    @SerializedName("pages")
    @Expose
    private List<WikiPage> wikiPages = null;

    public List<WikiPage> getWikiPages(){
        if (wikiPages == null){
            return new ArrayList<>();
        }
        return wikiPages;
    }
}
