package com.goryn.wikiguide.model;


import java.util.ArrayList;
import java.util.List;

public class WikiQuery {
    private List<WikiPage> wikiPages = null;

    public List<WikiPage> getWikiPages(){
        if (wikiPages == null){
            return new ArrayList<>();
        }
        return wikiPages;
    }
}
