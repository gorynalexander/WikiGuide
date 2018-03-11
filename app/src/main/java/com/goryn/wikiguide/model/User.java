package com.goryn.wikiguide.model;

/**
 * Created by Odinn on 20.02.2018.
 */

public class User {
    private String name;
    private String iconURL;
    User (){
        name = "Anonymous";
        iconURL = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}
