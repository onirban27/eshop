package com.imaginers.onirban.home.stores.helper;

import com.imaginers.onirban.home.stores.Model.Store;
import com.imaginers.onirban.home.stores.Model.StoreOne;
import com.imaginers.onirban.home.stores.Model.Stores;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StoreServices {

    @GET("/api/showstore.php?")
    Call<StoreOne> getStoreData(@Query("user_id") String user);

    @GET("/api/showAllStores.php?")
    Call<Stores> getAllStoreData();

    @POST("/api/insertstore.php")
    Call<Store> insertStore(@Body Store store);
}
