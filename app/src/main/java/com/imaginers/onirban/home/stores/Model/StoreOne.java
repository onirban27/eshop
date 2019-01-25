package com.imaginers.onirban.home.stores.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreOne {
    @SerializedName("store")
    @Expose
    private Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store pStore) {
        store = pStore;
    }
}
