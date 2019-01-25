package com.imaginers.onirban.home.usershop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.imaginers.onirban.home.MainActivity;
import com.imaginers.onirban.home.R;
import com.imaginers.onirban.home.Storage.MyUploadService;
import com.imaginers.onirban.home.product.helper.ProductServices;
import com.imaginers.onirban.home.product.model.Product;
import com.imaginers.onirban.home.product.model.Products;
import com.imaginers.onirban.home.rest.RetrofitInstance;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageStore extends AppCompatActivity implements AdminProductsAdapter.MyDialogItemClickListener {

    public static final String EXTRA_POSITION = "position";

    private Context mContext;
    private RecyclerView recyclerView;
    private AdminProductsAdapter adapter;
    Double lat;
    Double lon;
    String storeId,storeName,storeImage;
    ImageView signBoard1,signBoard2;
    //upload
    private static final String TAG = "Storage#MainActivity";
    private static final int RC_TAKE_PICTURE = 101;
    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";
    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    public Uri mDownloadUrl = null;
    public Uri mFileUri = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_store);

        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        storeName = intent.getStringExtra(MainActivity.EXTRA_STORENAME);
        storeId = intent.getStringExtra(MainActivity.EXTRA_STOREID);
        storeImage = intent.getStringExtra(MainActivity.EXTRA_STOREIMAGE);
        lat = intent.getDoubleExtra(MainActivity.EXTRA_LAT,0.0);
        lon = intent.getDoubleExtra(MainActivity.EXTRA_LON,0.0);
        signBoard1 = findViewById(R.id.productAddImageView);
        signBoard2 = findViewById(R.id.productNaImageView);

        initCollapsingToolbar();

        recyclerView = findViewById(R.id.product_recycler_view);
        final FloatingActionButton fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductDialog(savedInstanceState);
            }
        });

        prepareProducts();

        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }
        onNewIntent(getIntent());

        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:" + intent);
                hideProgressDialog();
                switch (intent.getAction()) {
                    case MyUploadService.UPLOAD_COMPLETED:
                    case MyUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };

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
                    generateProductList(getApplicationContext(),response.body().getProducts());
                    Log.wtf("productResponse", response.body().getMessage() + "");
                }else {
                    Toast.makeText(ManageStore.this, "No Products found! "+response.body(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Toast.makeText(ManageStore.this, "Something went wrong...Please try later!",
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

        ImageView placePicture =  findViewById(R.id.storeCoverImage);
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

    private void showProductDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_product);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final ImageView imageView = dialog.findViewById(R.id.productImage);
        final TextView url = dialog.findViewById(R.id.pictureDownloadUri);
        final EditText name = dialog.findViewById(R.id.et_product_name);
        final EditText price = dialog.findViewById(R.id.et_product_price);
        final EditText quantity = dialog.findViewById(R.id.et_product_quantity);
        final EditText description = dialog.findViewById(R.id.et_product_description);

        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }
        onNewIntent(getIntent());

        (dialog.findViewById(R.id.image_upload_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        // Download URL and Download button
        if (mDownloadUrl != null) {
            String myUrl = mDownloadUrl.toString();
            Glide.with(getBaseContext())
                    .load(myUrl)
                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        (dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ( dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = new Product(
                        name.getText().toString(),
                        quantity.getText().toString(),
                        price.getText().toString(),
                        description.getText().toString(),
                        mDownloadUrl.toString(),
                        "dress",
                        lon.toString(),
                        lat.toString(),
                        storeId);

                OkHttpClient client = new OkHttpClient();

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("http://huthat.net/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client);

                Retrofit retrofit = builder.build();

                ProductServices service = retrofit.create(ProductServices.class);

                Call<Product> call = service.insertData(product);

                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {

                        Log.d("received", "onResponse: " + response.body().getStatus());
                        prepareProducts();
                        Toast.makeText(ManageStore.this, response.body().getStatus() , Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {

                        Log.i("Hello", "" + t);
                        Toast.makeText(ManageStore.this, "Throwable" + t, Toast.LENGTH_LONG).show();

                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void generateProductList(Context mContext, ArrayList<Product> proDataList) {

        recyclerView = findViewById(R.id.product_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ManageStore.this);
        recyclerView.setLayoutManager(layoutManager);
        if(proDataList != null) {
            if(proDataList.size() > 0) {
                adapter = new AdminProductsAdapter(mContext, proDataList, this);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
                signBoard1.setVisibility(View.GONE);
                signBoard2.setVisibility(View.GONE);
            }
        }else {
            Toast.makeText(mContext, "Product is null", Toast.LENGTH_SHORT).show();
            signBoard1.setVisibility(View.VISIBLE);
            signBoard2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if this Activity was launched by clicking on an upload notification
        if (intent.hasExtra(MyUploadService.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();

                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // Save the File URI
        mFileUri = fileUri;

        // Clear the last download, if any
        mDownloadUrl = null;
        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .setAction(MyUploadService.ACTION_UPLOAD));

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));
    }

    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        mDownloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    //Dialog listeners

    public void onEditClickListener(final Product pProduct){
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_edit_product);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText name = dialog.findViewById(R.id.et_product_namee);
        final EditText price = dialog.findViewById(R.id.et_product_pricee);
        final EditText quantity = dialog.findViewById(R.id.et_product_quantityy);
        final EditText description = dialog.findViewById(R.id.et_product_descriptionn);
        name.setText(pProduct.getProductName() );
        price.setText(pProduct.getPrice() );
        quantity.setText(pProduct.getQuantity() );
        description.setText(pProduct.getDescription() );

        (dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ( dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = new Product(
                        pProduct.getProductId(),
                        name.getText().toString(),
                        quantity.getText().toString(),
                        price.getText().toString(),
                        description.getText().toString(),
                        pProduct.getImage(),
                        "dress",
                        pProduct.getLon(),
                        pProduct.getLat(),
                        pProduct.getStoreId());
                System.out.println(product.getProductId()+" "+pProduct.getProductId());

                OkHttpClient client = new OkHttpClient();

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("http://huthat.net/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client);

                Retrofit retrofit = builder.build();

                ProductServices productServices = retrofit.create(ProductServices.class);

                Call<Product> call = productServices.upData(product);


                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        prepareProducts();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(mContext, response.body().getStatus() , Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Log.i("Hello", "" + t);
                        Toast.makeText(mContext, "Throwable" + t, Toast.LENGTH_LONG).show();

                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    public void onDeleteClickListener(final Product pProduct) {

        /*Create handle for the RetrofitInstance interface*/
        ProductServices productServices = RetrofitInstance.getRetrofitInstance().
                create(ProductServices.class);
        /*Call the method with parameter in the interface to get the employee data*/
        Call<Product> call = productServices.delData(pProduct.getProductId());
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful()){
                    Toast.makeText(mContext, "response"+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    prepareProducts();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(mContext, "delete failed "+pProduct.getStatus(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
