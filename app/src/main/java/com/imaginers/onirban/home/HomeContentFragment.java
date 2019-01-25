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
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
public class HomeContentFragment extends Fragment {

    private Context mContext;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    SearchView mSearchView;

    public HomeContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_content, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchView = view.findViewById(R.id.searchViewProduct);
        recyclerView = view.findViewById(R.id.recyclerViewSearch);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                searchProducts(query);
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchProducts(newText);
                return false;
            }
        });
}



    public void searchProducts(String searchtext) {

        /*Create handle for the RetrofitInstance interface*/
        ProductServices service = RetrofitInstance.getRetrofitInstance().
                create(ProductServices.class);

        /*Call the method with parameter in the interface to get the employee data*/
        Call<Products> call = service.searchProductData(searchtext);

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
                    Toast.makeText(mContext, "No Products available with this name! "+response.body(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Toast.makeText(mContext, "Something went wrong...Please try later!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void generateProductList(Context mContext, ArrayList<Product> proDataList) {


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        if(proDataList != null) {
            if(proDataList.size() > 0) {
                adapter = new ProductAdapter(mContext, proDataList);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }else {
            Toast.makeText(mContext, "Product is null", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }
}
