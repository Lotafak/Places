package com.patrykk.places.foursquare;

/**
 * Model for Foursquare data
 */
public class FoursquareVenueModel {
    /**
     * Indicates type of image from Foursquare API (grey, 64x64)
     */
    private final String IMAGE_SIZE = "bg_64";

    private String name,city,address,country, categoryId, categoryUrl, id;
    private Double latitude, longitude;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getFullAddress() {
        return address + ", " + city;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String prefix, String suffix) {
        this.categoryUrl = prefix + IMAGE_SIZE + suffix;
    }
}