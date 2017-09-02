package com.goryn.wikiguide.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WikiPage {
    @SerializedName("pageid")
    @Expose
    public Integer pageid;
    @SerializedName("ns")
    @Expose
    public Integer ns;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("extract")
    @Expose
    public String extract;
}
