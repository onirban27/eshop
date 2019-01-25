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
import android.widget.Toast;

import com.imaginers.onirban.home.rest.RetrofitInstance;
import com.imaginers.onirban.home.stores.Model.Store;
import com.imaginers.onirban.home.stores.Model.Stores;
import com.imaginers.onirban.home.stores.adapter.AllStoreAdapter;
import com.imaginers.onirban.home.stores.adapter.StoreAdapter;
import com.imaginers.onirban.home.stores.helper.StoreServices;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// all the shop will be listed here

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopContentFragment extends Fragment {

    public static final String EXTRA_POSITION = "position";

    private RecyclerView recyclerView;
    private AllStoreAdapter adapter;

    public ShopContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareStores();
    }

    public void prepareStores() {

        /*Create handle for the RetrofitInstance interface*/
        StoreServices service = RetrofitInstance.getRetrofitInstance().
                create(StoreServices.class);

        /*Call the method with parameter in the interface to get the employee data*/
        Call<Stores> call = service.getAllStoreData();

        /*Log the URL called*/
        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<Stores>() {
            @Override
            public void onResponse(Call<Stores> call, Response<Stores> response) {
                if (response.body() != null) {
                    generateStoreList(getContext(),response.body().getAllStores());
                    Toast.makeText(getContext(), "store found! "+response.body().getStatus(), Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getContext(), "No store found! "+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Stores> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong...Please try later!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void generateStoreList(Context mContext, ArrayList<Store> storeDataList) {

        recyclerView = getView().findViewById(R.id.allstore_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        if(storeDataList != null) {
            if(storeDataList.size() > 0) {
                adapter = new AllStoreAdapter(mContext, storeDataList);
                System.out.println(storeDataList);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
        }else {
            Toast.makeText(mContext, "store is null", Toast.LENGTH_SHORT).show();
        }
    }
}
