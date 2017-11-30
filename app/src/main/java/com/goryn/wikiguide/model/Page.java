package com.goryn.wikiguide.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.xml.transform.Source;

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

    @Nullable
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;

    @SerializedName("original")
    @Expose
    public Thumbnail original;

    @SerializedName("terms")
    @Expose
    private Terms terms;


    @SerializedName("extract")
    @Expose
    private String extract;

    @Nullable
    public String getFullImage(){
        return original != null ? getOriginal().getSource() : null;
    }
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

    @Nullable
    public Thumbnail getThumbnail(){
        return thumbnail;
    }

    @Nullable
    public Thumbnail getOriginal(){
        return original;
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

        return thumbnail != null ? getThumbnail().getSource() : null;
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

        @SerializedName("source")
        @Expose
        public String source;
        @SerializedName("width")
        @Expose
        public int width;
        @SerializedName("height")
        @Expose
        public int height;

        @Nullable
        public String getSource() {
            return  source == null ? null : source;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

}
