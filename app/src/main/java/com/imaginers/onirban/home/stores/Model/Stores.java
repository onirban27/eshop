package com.imaginers.onirban.home.stores.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Stores {

    @SerializedName("all_stores")
    @Expose
    private ArrayList<Store> allStores = null;
    @SerializedName("status")
    @Expose
    private String status;

    public ArrayList<Store> getAllStores() {
        return allStores;
    }

    public void setAllStores(ArrayList<Store> allStores) {
        this.allStores = allStores;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String pStatus) {
        status = pStatus;
    }
}
