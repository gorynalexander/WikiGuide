package com.goryn.wikiguide.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Odinn on 17.01.2018.
 */

public class ExcursionPlace implements Parcelable, Comparable {
    private String placeTitle = "";
    private String thumbURL = "";
    private double lat;
    private double lon;
    private double distance;
    private String description;

    public ExcursionPlace(){}

    public ExcursionPlace(String placeTitle, String thumbURL, double lat, double lon, String description) {
        this.placeTitle = placeTitle;
        this.thumbURL = thumbURL;
        this.lat = lat;
        this.lon = lon;
        this.description = description;
    }
    public ExcursionPlace(String placeTitle, String thumbURL, double lat, double lon) {
        this.placeTitle = placeTitle;
        this.thumbURL = thumbURL;
        this.lat = lat;
        this.lon = lon;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public double getDistance() {
        return distance;
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExcursionPlace that = (ExcursionPlace) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lon, lon) != 0) return false;
        if (!placeTitle.equals(that.placeTitle)) return false;
        return thumbURL != null ? thumbURL.equals(that.thumbURL) : that.thumbURL == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = placeTitle.hashCode();
        result = 31 * result + (thumbURL != null ? thumbURL.hashCode() : 0);
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    @Override
    public int compareTo(@NonNull Object o) {
        double distance = ((ExcursionPlace) o).getDistance();
        return (int) (this.distance - distance);
    }
}