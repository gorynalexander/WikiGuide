package com.goryn.wikiguide.model;

import android.support.annotation.Nullable;

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
    private List<Coordinate> coordinates;

    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;

    @SerializedName("terms")
    @Expose
    private Terms terms;


    private String extract;


    public String getExtract() {
        return extract;
    }

    public void setExtract(String extract) {
        this.extract = extract;
    }

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

    @Nullable
    public String getThumbUrl() {
        return thumbnail != null ? thumbnail.source() : null;
    }

    public int getThumbWidth() {
        return thumbnail.getWidth();
    }

    public int getThumbHeight() {
        return thumbnail.getHeight();
    }


    @Nullable
    public String getDescription() {
        return terms != null && terms.description() != null ? terms.description().get(0) : null;
    }

    static class Terms {
        @SuppressWarnings("unused")
        private List<String> description;

        List<String> description() {
            return description;
        }
    }

    static class Thumbnail {
        @SuppressWarnings("unused")
        private String source;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @SuppressWarnings("unused")
        private int width;
        @SuppressWarnings("unused")
        private int height;

        String source() {
            return source;
        }
    }

}
