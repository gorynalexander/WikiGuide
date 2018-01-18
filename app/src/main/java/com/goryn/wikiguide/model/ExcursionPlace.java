package com.goryn.wikiguide.model;

/**
 * Created by Odinn on 17.01.2018.
 */

public class ExcursionPlace {
    private String placeTitle;
    private String thumbURL;
    private double lat;
    private double lon;

    public ExcursionPlace(String placeTitle, String thumbURL, double lat, double lon) {
        this.placeTitle = placeTitle;
        this.thumbURL = thumbURL;
        this.lat = lat;
        this.lon = lon;
    }

    public String getPlaceTitle() {
        return placeTitle;
    }

    public void setPlaceTitle(String placeTitle) {
        this.placeTitle = placeTitle;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
