package com.imaginers.onirban.home.buy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.imaginers.onirban.home.R;

import java.util.Objects;

import static com.imaginers.onirban.home.Auth.Model.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.imaginers.onirban.home.Auth.Model.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class BuyActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "";
    Double myLat;
    Double myLon;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
                        Toast.makeText(BuyActivity.this, "+"+location.getLatitude(), Toast.LENGTH_SHORT).show();
                        Log.d("received", "onResponse: " + location.getLatitude());
                        Log.d("received", "onResponse: " + location.getLongitude());
                        myLat = location.getLatitude();
                        myLon = location.getLongitude();
                    }
                }
            }
        });
        Bundle bundle = getIntent().getExtras();
        String store = Objects.requireNonNull(bundle).getString("storename");
        String productName = Objects.requireNonNull(bundle).getString("productname");
        double lon = Double.parseDouble(Objects.requireNonNull(bundle.getString("lon")));
        double lat = Double.parseDouble(Objects.requireNonNull(bundle.getString("lat")));

//        myLat = Double.parseDouble(MainActivity.EXTRA_LAT);
//        myLon = Double.parseDouble(MainActivity.EXTRA_LON);
        // Add a marker in Sydney and move the camera
        LatLng productLocation = new LatLng(lat,lon);
        mMap.addMarker(new MarkerOptions().position(productLocation).title(store).snippet(productName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        LatLng myLocation = new LatLng( 23.7869662,90.3753452);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("You").snippet("buyer").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    private void getLastKnownLocation() {
//        Log.d(TAG, "getLastKnownLocation: called.");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if (task.isSuccessful()) {
//                    Location location = task.getResult();
//                    if(location!=null){
//                        Toast.makeText(BuyActivity.this, "+"+location.getLatitude(), Toast.LENGTH_SHORT).show();
//                        Log.d("received", "onResponse: " + location.getLatitude());
//                        Log.d("received", "onResponse: " + location.getLongitude());
//                        myLat = location.getLatitude();
//                        myLon = location.getLongitude();
//
//                    }
//                }
//            }
//        });

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
        }
    }


}
