package com.imaginers.onirban.home;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imaginers.onirban.home.Storage.MyUploadService;
import com.imaginers.onirban.home.base.ProfileActivity;
import com.imaginers.onirban.home.rest.RetrofitInstance;
import com.imaginers.onirban.home.stores.Model.Store;
import com.imaginers.onirban.home.stores.Model.StoreOne;
import com.imaginers.onirban.home.stores.helper.StoreServices;
import com.imaginers.onirban.home.usershop.ManageStore;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.imaginers.onirban.home.Auth.Model.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.imaginers.onirban.home.Auth.Model.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    Double lat;
    Double lon;
    String storeId,storeName,storeImage;

    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth mAuth;
    private boolean status;
    Button mStoreButton;
    public static final String EXTRA_STORENAME = "storename";
    public static final String EXTRA_STOREID = "storeid";
    public static final String EXTRA_STOREIMAGE = "storeimage";
    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LON = "lon";

    //upload
    //upload
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
        setContentView(R.layout.activity_main);
        //getStoreData();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mStoreButton = findViewById(R.id.storeManageBTN);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //works for click listening on nav drawer
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(this,
                findViewById(R.id.category_grid),
                new AccelerateDecelerateInterpolator(),
                getResources().getDrawable(R.drawable.menu), // Menu open icon
                getResources().getDrawable(R.drawable.close_menu))); // Menu close icon

        // Setting ViewPager for each Tabs
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        getLocationPermission();
         //Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            findViewById(R.id.category_grid).setBackground(getDrawable(R.drawable.category_grid_background_shape));
        }
        updateView();

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

        findViewById(R.id.storeManageBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getStoreData()){
                    getLocationPermission();
                    showStoreCreateDialog(savedInstanceState);
                }else {
                    Intent intent = new Intent(MainActivity.this,ManageStore.class);
                    intent.putExtra(EXTRA_STORENAME, storeName);
                    intent.putExtra(EXTRA_STOREID, storeId);
                    intent.putExtra(EXTRA_STOREIMAGE, storeImage);
                    intent.putExtra(EXTRA_LAT, lat);
                    intent.putExtra(EXTRA_LON,lon);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        return super.onCreateView(parent, name, context, attrs);
    }

    private void updateView() {
        if(status==true){
            mStoreButton.setText("Manage Store");
        }else{
            mStoreButton.setText("Create Store");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getStoreData();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        FirebaseUser currentUser = mAuth.getCurrentUser();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
        //updateUI(currentUser);
        //getStoreData();
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ProductGridFragment(), "Products");
        adapter.addFragment(new HomeContentFragment(), "Home");
        adapter.addFragment(new ShopContentFragment(), "Shop");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    public void myAccountClick(View view) {
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(intent);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void showStoreCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_store);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final ImageView imageView = dialog.findViewById(R.id.productImage);
        final EditText et_post = dialog.findViewById(R.id.et_post);

        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }
        onNewIntent(getIntent());

        (dialog.findViewById(R.id.store_image_upload_button)).setOnClickListener(new View.OnClickListener() {
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

                Log.d("received", "onResponse: " + lat);
                Log.d("received", "onResponse: " + lon);

                String user = mAuth.getUid();
                //button ok action>>>>>>>>>>>>>>>>>
                Store store = new Store(
                        et_post.getText().toString(),
                        lon.toString(),
                        lat.toString(),
                        user,
                        mDownloadUrl.toString());

                OkHttpClient client = new OkHttpClient();

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("https://huthat.net/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client);

                Retrofit retrofit = builder.build();

                StoreServices service = retrofit.create(StoreServices.class);

                Call<Store> call = service.insertStore(store);

                call.enqueue(new Callback<Store>() {
                    @Override
                    public void onResponse(Call<Store> call, Response<Store> response) {
                        if (response.body()!=null){
                            mStoreButton.setText("Manage Store");
                            getStoreData();
                        }
                    }

                    @Override
                    public void onFailure(Call<Store> call, Throwable t) {

                        Log.i("Hello", "" + t);
                        Toast.makeText(MainActivity.this, "Throwable" + t, Toast.LENGTH_LONG).show();

                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if(location!=null){
                        Toast.makeText(MainActivity.this, "+"+location.getLatitude(), Toast.LENGTH_SHORT).show();
                        Log.d("received", "onResponse: " + location.getLatitude());
                        Log.d("received", "onResponse: " + location.getLongitude());
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
            }
        });

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }
            }
            case RC_TAKE_PICTURE: {
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
    }

    private Boolean getStoreData(){
        /*Create handle for the RetrofitInstance interface*/
        StoreServices serviceStore = RetrofitInstance.getRetrofitInstance().
                create(StoreServices.class);

        /*Call the method with parameter in the interface to get the employee data*/
        String userid =mAuth.getUid();
        Call<StoreOne> calling = serviceStore.getStoreData(userid);


        calling.enqueue(new Callback<StoreOne>() {
            @Override
            public void onResponse(Call<StoreOne> call, Response<StoreOne> response) {
                if (response.body() != null) {
                    storeName = response.body().getStore().getStoreName();
                    storeId = response.body().getStore().getStoreId();
                    storeImage = response.body().getStore().getBanner();
                    Toast.makeText(MainActivity.this, "store data available!", Toast.LENGTH_SHORT).show();
                    if(storeId!=null){
                        status = true;
                    }

                    updateView();
                }
            }

            @Override
            public void onFailure(Call<StoreOne> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                status = false;
                updateView();
            }
        });
        Log.wtf("status", status + "");
        return status;
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

}
