package com.imaginers.onirban.home;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.imaginers.onirban.home.product.adapter.ProductAdapter;
import com.imaginers.onirban.home.product.helper.ProductServices;
import com.imaginers.onirban.home.product.model.Product;
import com.imaginers.onirban.home.product.model.Products;
import com.imaginers.onirban.home.rest.RetrofitInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductGridFragment extends Fragment {


    public static final String EXTRA_POSITION = "position";

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    Context mContext;


    public ProductGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
//        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.fragment_product_grid, container, false);
        // Set up the RecyclerView

        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
//        menuInflater.inflate(R.menu.toolbar_menu, menu);
//        super.onCreateOptionsMenu(menu, menuInflater);
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareProducts();

    }

    public void prepareProducts() {


        /*Create handle for the RetrofitInstance interface*/
        ProductServices service = RetrofitInstance.getRetrofitInstance().
                create(ProductServices.class);

        /*Call the method with parameter in the interface to get the employee data*/
        Call<Products> call = service.getAllProductData();

        /*Log the URL called*/
        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                if (response.body() != null) {
                    generateProductList(mContext,response.body().getProducts());
                    Log.wtf("productResponse", response.body().getStatus() + "");
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(mContext, "No Products found! "+response.body(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Log.d("BALSAL", t.toString());
                Toast.makeText(getContext(), "Something went wrong...Please try later!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void generateProductList(Context mContext, ArrayList<Product> proDataList) {

        recyclerView = getView().findViewById(R.id.allproduct_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
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
