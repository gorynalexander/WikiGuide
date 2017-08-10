package com.goryn.wikiguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Odinn on 10.08.2017.
 */

public class Terms {
    @SerializedName("description")
    @Expose
    private List<String> description = null;

    public List<String> getDescription() {
        return description;
    }

}
