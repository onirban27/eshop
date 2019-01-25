package com.imaginers.onirban.home.stores.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imaginers.onirban.home.AllStoreActivity;
import com.imaginers.onirban.home.R;
import com.imaginers.onirban.home.buy.BuyActivity;
import com.imaginers.onirban.home.stores.Model.Store;

import java.util.ArrayList;

public class AllStoreAdapter extends RecyclerView.Adapter<AllStoreAdapter.MyViewHolder>  {

    private Context mContext;
    private ArrayList<Store> mStoreList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;
        public ImageView thumbnail;
        Button visit;


        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.store_title);
            description = view.findViewById(R.id.store_description);
            thumbnail = view.findViewById(R.id.store_image);
            visit = view.findViewById(R.id.visitBTN);
        }
    }


    public AllStoreAdapter(Context mContext, ArrayList<Store> mStoreList) {
        this.mContext = mContext;
        this.mStoreList = mStoreList;
    }

    @Override
    public AllStoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_store_repo, parent, false);

        return new AllStoreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AllStoreAdapter.MyViewHolder holder, int pI) {
        final Store store = mStoreList.get(pI);
        holder.title.setText(store.getStoreName());
        holder.description.setText(store.getStoreId());
        // loading product cover using Glide library
        Glide.with(mContext).load(store.getBanner()).into(holder.thumbnail);

        holder.visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AllStoreActivity.class);
                intent.putExtra("storename",  store.getStoreName());
                intent.putExtra("storeid", store.getStoreId());
                intent.putExtra("storeimage", store.getBanner());
                intent.putExtra("storedescription", store.getStoreId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStoreList.size();
    }
}

