package com.goryn.wikiguide.model;

import android.support.annotation.NonNull;
import android.text.Editable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Odinn on 18.01.2018.
 */

public class Excursion implements Comparable{
    private String title;
    private List<ExcursionPlace> excursionPlaces = new ArrayList<>();
    private double distance;

    public Excursion(){}


    public Excursion(String title, List<ExcursionPlace> excursionPlaces) {
        this.title = title;
        this.excursionPlaces = excursionPlaces;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ExcursionPlace> getExcursionPlaces() {
        return excursionPlaces;
    }

    public void setExcursionPlaces(List<ExcursionPlace> excursionPlaces) {
        this.excursionPlaces = excursionPlaces;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("excursionPlaces", excursionPlaces);
        result.put("addedByUser", new User());
        return result;
    }


    @Override
    public int compareTo(@NonNull Object o) {
        double distance = ((Excursion) o).getDistance();
        if (this.distance < distance){
            return -1;
        } else if (this.distance > distance){
            return 1;
        } else {
            return 0;
        }
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
