package com.imaginers.onirban.home.stores.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Store {

    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("store_name")
    @Expose
    private String storeName;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("status")
    private String status;
    @SerializedName("banner")
    private String banner;

    public Store( String pStoreName, String pLon, String pLat, String pUserId, String pBanner) {
        storeName = pStoreName;
        lon = pLon;
        lat = pLat;
        userId = pUserId;
        banner = pBanner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String pBanner) {
        banner = pBanner;
    }
}