package com.goryn.wikiguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Odinn on 10.08.2017.
 */

public class Thumbnail {
    @SerializedName("source")
    @Expose
    private String source;

    @SerializedName("width")
    @Expose
    private int width;

    @SerializedName("height")
    @Expose
    private int height;

    public String getSource() {
        return source;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
