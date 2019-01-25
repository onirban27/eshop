package com.imaginers.onirban.home.stores.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.imaginers.onirban.home.R;
import com.imaginers.onirban.home.product.adapter.ProductAdapter;
import com.imaginers.onirban.home.product.model.Product;
import com.imaginers.onirban.home.stores.Model.Store;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder>  {

    private Context mContext;
    private ArrayList<Store> mStoreList;
    Store mStore;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;
        public ImageView thumbnail;


        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }


    public StoreAdapter(Context mContext, ArrayList<Store> mStoreList) {
        this.mContext = mContext;
        this.mStoreList = mStoreList;
    }

    @Override
    public StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_store_repo, parent, false);

        return new StoreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.MyViewHolder holder, int pI) {
        //final Store store = mStoreList.get(pI);
        holder.title.setText(mStoreList.get(pI).getStoreName());
        holder.description.setText(mStoreList.get(pI).getStoreId());
        // loading product cover using Glide library
        Glide.with(mContext).load(mStoreList.get(pI).getBanner()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return mStoreList.size();
    }
}

