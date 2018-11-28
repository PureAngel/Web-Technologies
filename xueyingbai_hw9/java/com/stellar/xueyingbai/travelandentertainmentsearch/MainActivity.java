package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyInterface, Search.OnFragmentInteractionListener, Favorites.OnFragmentInteractionListener, GoogleApiClient.OnConnectionFailedListener {

    private LocationManager locationManager;
    private String locationProvider;       //位置提供器
    String lat_lng;
    TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.search,
            R.drawable.heart_fill_white,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );
        }

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Places.PLACE_DETECTION_API)

                .enableAutoManage(this, 0, this)
                .build();

        // set tabs
        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("SEARCH"));
        tabLayout.addTab(tabLayout.newTab().setText("FAVORITES"));
        setupTabIcons();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager main_viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        main_viewPager.setOffscreenPageLimit(2);
        final Search_Fav_pagerAdapter main_pageradapter = new Search_Fav_pagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        main_viewPager.setAdapter(main_pageradapter);
        main_viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                main_viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //lat_lng = getLngAndLat(this);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(0).setCustomView(getTabView(0));
//        tabLayout.getTabAt(1).setCustomView(getTabView(1));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Could not connect to Google API Client:Error" + connectionResult.getErrorCode(),Toast.LENGTH_SHORT).show();
        MainActivity.this.finish();

    }

    @Override
    public void show_new_page() {

    }

//    private String getLngAndLat(Context context) {
//        double latitude = 0.0;
//        double longitude = 0.0;
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  //从gps获取经纬度
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (location != null) {
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//            } else {//当GPS信号弱没获取到位置的时候又从网络获取
//                return getLngAndLatWithNetwork();
//            }
//        } else {    //从网络获取经纬度
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
//            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            if (location != null) {
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//            }
//        }
//        return longitude + "," + latitude;
//    }
//
//    //从网络获取经纬度
//    public String getLngAndLatWithNetwork() {
//        double latitude = 0.0;
//        double longitude = 0.0;
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
//        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//        }
//        return longitude + "," + latitude;
//    }
//
//
//    LocationListener locationListener = new LocationListener() {
//
//        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        // Provider被enable时触发此函数，比如GPS被打开
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        // Provider被disable时触发此函数，比如GPS被关闭
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//
//        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
//        @Override
//        public void onLocationChanged(Location location) {
//        }
//    };

}

