package com.imaginers.onirban.home;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.imaginers.onirban.home.Storage.MyUploadService;
import com.imaginers.onirban.home.product.adapter.ProductAdapter;
import com.imaginers.onirban.home.product.helper.ProductServices;
import com.imaginers.onirban.home.product.model.Product;
import com.imaginers.onirban.home.product.model.Products;
import com.imaginers.onirban.home.rest.RetrofitInstance;
import com.imaginers.onirban.home.usershop.AdminProductsAdapter;
import com.imaginers.onirban.home.usershop.ManageStore;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllStoreActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    private Context mContext;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    Double lat;
    Double lon;
    String storeId,storeName,storeImage,storeDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_store);
        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.all_store_product_recycler_view);
        Bundle bundle = getIntent().getExtras();
        storeName = Objects.requireNonNull(bundle).getString("storename");
        storeId = Objects.requireNonNull(bundle).getString("storeid");
        storeDescription = Objects.requireNonNull(bundle).getString("storedescription");
        storeImage = Objects.requireNonNull(bundle).getString("storeimage");

        initCollapsingToolbar();

        prepareProducts();
    }

    public void prepareProducts() {

        /*Create handle for the RetrofitInstance interface*/
        ProductServices service = RetrofitInstance.getRetrofitInstance().
                create(ProductServices.class);

        /*Call the method with parameter in the interface to get the employee data*/
        Call<Products> call = service.getProductData(storeId);

        /*Log the URL called*/
        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                if (response.body() != null) {
                    generateProductList(mContext,response.body().getProducts());
                    adapter.notifyDataSetChanged();
                    Log.wtf("productResponse", response.body().getMessage() + "");
                }else {
                    Toast.makeText(AllStoreActivity.this, "No Products available! "+response.body(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Toast.makeText(AllStoreActivity.this, "Something went wrong...Please try later!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.collapsing_toolbar);
        //         Set title of Detail page
        collapsingToolbar.setTitle(storeName);

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        ImageView placePicture =  findViewById(R.id.backimg_allstore);
        Glide.with(this).load(storeImage).into(placePicture);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(storeName);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(storeName);
                    isShow = true;
                }
            }
        });
    }

    private void generateProductList(Context mContext, ArrayList<Product> proDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(proDataList != null) {
            if(proDataList.size() > 0) {
                adapter = new ProductAdapter(mContext, proDataList);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
        }else {
            Toast.makeText(mContext, "Product is null", Toast.LENGTH_SHORT).show();
        }
    }


}
