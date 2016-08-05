package com.patrykk.places.foursquare;

import android.graphics.Bitmap;

import java.util.HashMap;

public class FoursquareCategoryModel {
    private final String IMAGE_SIZE = "bg_64";
    private String id, name, iconPrefix, iconSuffix;
    private Bitmap icon;
    private HashMap<String, Bitmap> map;

    FoursquareCategoryModel(){
        map = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return iconPrefix + IMAGE_SIZE + iconSuffix;
    }

    public String getIconSuffix() {
        return iconSuffix;
    }

    public void setIconSuffix(String iconSuffix) {
        this.iconSuffix = iconSuffix;
    }

    public String getIconPrefix() {
        return iconPrefix;
    }

    public void setIconPrefix(String iconPrefix) {
        this.iconPrefix = iconPrefix;
    }

    public HashMap<String, Bitmap> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Bitmap> map) {
        this.map = map;
    }
}
