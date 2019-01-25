package com.imaginers.onirban.home.usershop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imaginers.onirban.home.R;
import com.imaginers.onirban.home.product.helper.ProductServices;
import com.imaginers.onirban.home.product.model.Product;

import java.util.ArrayList;

public class AdminProductsAdapter extends RecyclerView.Adapter<AdminProductsAdapter.MyViewHolder> {

    ProductServices mProductServices;
    private Context mContext;
    private ArrayList<Product> mProductList;
    MyDialogItemClickListener mMyDialogItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price;
        public ImageView thumbnail;
        ImageButton delete,edit;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            delete = view.findViewById(R.id.deleteBTN);
            edit = view.findViewById(R.id.editBTN);
        }
    }


    public AdminProductsAdapter(Context mContext, ArrayList<Product> mProductList, MyDialogItemClickListener pMyDialogItemClickListener) {
        this.mContext = mContext;
        this.mProductList = mProductList;
        this.mMyDialogItemClickListener = pMyDialogItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Product product = mProductList.get(position);
        holder.title.setText(product.getProductName());
        holder.price.setText(product.getPrice());


        // loading product cover using Glide library
        Glide.with(mContext).load(product.getImage()).into(holder.thumbnail);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyDialogItemClickListener.onDeleteClickListener(product);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyDialogItemClickListener.onEditClickListener(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public interface MyDialogItemClickListener{

        void onEditClickListener(Product pProduct);
        void onDeleteClickListener(Product pProduct);

    }
}