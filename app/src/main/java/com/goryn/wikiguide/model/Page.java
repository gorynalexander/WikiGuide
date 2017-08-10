package com.goryn.wikiguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Odinn on 10.08.2017.
 */

public class Page {
    @SerializedName("pageid")
    @Expose
    private int pageid;

    @SerializedName("ns")
    @Expose
    private int ns;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("index")
    @Expose
    private int index;

    @SerializedName("coordinates")
    @Expose
    private List<Coordinate> coordinates = null;

    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;

    @SerializedName("terms")
    @Expose
    private Terms terms;

    public int getPageid() {
        return pageid;
    }

    public int getNs() {
        return ns;
    }


    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public Terms getTerms() {
        return terms;
    }

}
