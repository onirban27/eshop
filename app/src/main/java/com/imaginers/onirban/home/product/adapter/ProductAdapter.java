package com.imaginers.onirban.home.product.adapter;

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
import com.imaginers.onirban.home.R;
import com.imaginers.onirban.home.buy.BuyActivity;
import com.imaginers.onirban.home.product.model.Product;
import java.util.ArrayList;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>  {


    private Context mContext;
    private ArrayList<Product> mProductList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price,quantity;
        public ImageView thumbnail;
        Button buy;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            quantity = view.findViewById(R.id.quantity);
            buy = view.findViewById(R.id.buyBTN);
        }
    }


    public ProductAdapter(Context mContext, ArrayList<Product> mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
    }

    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product_repo, parent, false);

        return new ProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int pI) {
        final Product product = mProductList.get(pI);
        holder.title.setText(product.getProductName());
        holder.price.setText(product.getPrice());


        // loading product cover using Glide library
        Glide.with(mContext).load(product.getImage()).into(holder.thumbnail);

        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,BuyActivity.class);
                intent.putExtra("lon",product.getLon());
                intent.putExtra("lat", product.getLat());
                intent.putExtra("storename", "store: "+product.getStoreName());
                intent.putExtra("productname", "product: "+product.getProductName());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }
}

