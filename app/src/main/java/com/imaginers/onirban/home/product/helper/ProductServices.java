package com.imaginers.onirban.home.product.helper;

import com.imaginers.onirban.home.product.model.Product;
import com.imaginers.onirban.home.product.model.Products;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ProductServices {

         //read
        @GET("/api/showproduct.php?")
        Call<Products> getProductData(@Query("store_id") String store);
        //read
        @GET("/api/showAllProducts.php?")
        Call<Products> getAllProductData();

        //read
        @GET("/api/search.php?")
        Call<Products> searchProductData(@Query("product") String productName);

        //insert
        @POST("/api/insertproduct.php")
        Call<Product> insertData(@Body Product product);

        //update
//        @PATCH("api/upProduct.php?")
//        Call<Product> updateData(@Query("product_id") String productid);
//
//        @PUT("api/updProduct.php?")
//        Call<Product> replaceData(@Query("product_id") String productid);

        //update
        @POST("/api/upproduct.php?")
        Call<Product> upData(@Body Product product);

        // delete
        @POST("/api/delproduct.php?")
        Call<Product> delData(@Query("product_id") String productid);

}
