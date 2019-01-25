package com.imaginers.onirban.home.product.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("store_name")
    @Expose
    private String storeName;
    @SerializedName("response")
    @Expose
    private String status;


    public Product(String pProductId, String pProductName, String pQuantity, String pPrice, String pDescription, String pImage, String pCategory, String pLon, String pLat, String pStoreId) {
        productId = pProductId;
        productName = pProductName;
        quantity = pQuantity;
        price = pPrice;
        description = pDescription;
        image = pImage;
        category = pCategory;
        lon = pLon;
        lat = pLat;
        storeId = pStoreId;
    }

    public Product(String pProductName, String pPrice, String pImage) {
        productName = pProductName;
        price = pPrice;
        image = pImage;
    }

    public Product(String pS, String pS1, String pS2, String pS3, String pS4, String pDress, String pS5, String pS6, String pStoreId) {
        productName = pS;
        quantity = pS1;
        price = pS2;
        description = pS3;
        image = pS4;
        category = pDress;
        lon = pS5;
        lat = pS6;
        storeId = pStoreId;

    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity='" + quantity + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", category='" + category + '\'' +
                ", lon='" + lon + '\'' +
                ", lat='" + lat + '\'' +
                ", storeId='" + storeId + '\'' +
                '}';
    }
}
