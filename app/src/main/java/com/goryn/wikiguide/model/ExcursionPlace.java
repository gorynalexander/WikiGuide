package com.goryn.wikiguide.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Odinn on 17.01.2018.
 */

public class ExcursionPlace implements Parcelable {
    private String placeTitle = "";
    private String thumbURL = "";
    private double lat;
    private double lon;

    public ExcursionPlace(){}

    public ExcursionPlace(String placeTitle, String thumbURL, double lat, double lon) {
        this.placeTitle = placeTitle;
        this.thumbURL = thumbURL;
        this.lat = lat;
        this.lon = lon;
    }

    public String getPlaceTitle() {
        return placeTitle != null ? placeTitle : null;
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



    protected ExcursionPlace(Parcel in) {
        placeTitle = in.readString();
        thumbURL = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeTitle);
        dest.writeString(thumbURL);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ExcursionPlace> CREATOR = new Parcelable.Creator<ExcursionPlace>() {
        @Override
        public ExcursionPlace createFromParcel(Parcel in) {
            return new ExcursionPlace(in);
        }

        @Override
        public ExcursionPlace[] newArray(int size) {
            return new ExcursionPlace[size];
        }
    };
}