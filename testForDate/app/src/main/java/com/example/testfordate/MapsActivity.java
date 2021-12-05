package com.example.testfordate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManagerNonConfig;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;
import androidx.room.Room;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataBase.gmapspot.GmapDatabase;
import DataBase.gmapspot.Gmapspot;
import DataBase.gmapspot.GmapspotDao;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static boolean rLocationGranted = false;
    private FusedLocationProviderClient mFusedLocationProvider;
    private UiSettings mUiSettings;
    PlacesClient placesClient;
    SearchView searchView;
    private ImageButton locate;
    private ImageButton addpin;
    private TextView search;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    LatLng marker_latlng=null;

    Marker marker1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        //searchView = findViewById(R.id.idSearchView);
        search = findViewById(R.id.search);
        locate = findViewById(R.id.locate);
        addpin = findViewById(R.id.addpin);
        SupportMapFragment mapFragment = null;
        if (chkPlayService()) {
            initialMap();
            if (rLocationGranted) {
                //Log.i("map check","map check success!!");
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(),"AIzaSyAvovol62ewDqWllzA2xy3B-gys2X3ig5Y");
        placesClient = Places.createClient(this);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG,Place.Field.NAME,Place.Field.ID);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(MapsActivity.this);
                Log.i("t","send the text");
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
        //---資料庫宣告---
        //gmapDatabase = Room.databaseBuilder(this, GmapDatabase.class,"gmap").allowMainThreadQueries().build();
        //gmapDatabase = GmapDatabase.getInstance(this.getApplicationContext());
        GmapDatabase gmapDatabase = GmapDatabase.getInstance(this);

        //--------------------------------------------------------------
        addpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(marker_latlng == null){
                    Toast.makeText(MapsActivity.this,"尚未選擇地點",Toast.LENGTH_LONG).show();
                }
                /*else {
                    Log.i("marker_latitude: ", String.valueOf(marker_latlng.latitude));
                    Log.i("marker_longitude: ", String.valueOf(marker_latlng.longitude));
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
                    final EditText editText = new EditText(MapsActivity.this); //final一個editText
                    alertDialog.setTitle("加入此標籤，請輸入標題");
                    alertDialog.setView(editText);
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MapsActivity.this, "加入標籤", Toast.LENGTH_LONG).show();
                            String title = editText.getText().toString();
                            Log.i("title_string",title);
                            Gmapspot gmapspot = new Gmapspot(marker_latlng.latitude, marker_latlng.longitude, title, nowDate);
                            gmapDatabase.GmapspotDao().insertGmapspot(gmapspot);
                        }
                    });
                    alertDialog.setNeutralButton("取消", ((dialog, which) -> {
                    }));
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }*/
                else{
                    Log.i("marker_latitude: ", String.valueOf(marker_latlng.latitude));
                    Log.i("marker_longitude: ", String.valueOf(marker_latlng.longitude));
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", marker_latlng.latitude);
                    bundle.putDouble("longitude", marker_latlng.longitude);
                    MapDialog mapDialog = new MapDialog();
                    mapDialog.setArguments(bundle);
                    mapDialog.show(getSupportFragmentManager(),"map dialog");
                }
            }
        });
    }
    protected void setmarker() {
        //mMap.clear();
        GmapDatabase gmapDatabase = GmapDatabase.getInstance(this);
        int dbcount = gmapDatabase.GmapspotDao().countdb();
        Log.i("mapdbcount",String.valueOf(dbcount));
        for(int i = 1; i <= dbcount; i++){
            /*Log.i("mapdbid:",String.valueOf(i));
            Log.i("mapdblatitude:",String.valueOf(gmapDatabase.GmapspotDao().getlatitude(i)));
            Log.i("mapdblongitude:",String.valueOf(gmapDatabase.GmapspotDao().getlongitude(i)));*/
            LatLng lng = new LatLng(gmapDatabase.GmapspotDao().getlatitude(i),gmapDatabase.GmapspotDao().getlongitude(i));
            mMap.addMarker(new MarkerOptions().position(lng).title(gmapDatabase.GmapspotDao().gettitle(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
                place.getAttributions();
                LatLng sLocation_latlng = new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);
                marker_latlng = sLocation_latlng;
                //mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sLocation_latlng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                mMap.addMarker(new MarkerOptions().position(sLocation_latlng).title(place.getName()));
            }
            return;
        }
    }
    private void initialMap() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1])
                == PackageManager.PERMISSION_GRANTED) {
            //可以取得FINE_LOCATION
            rLocationGranted = true;
        } else if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0])
                == PackageManager.PERMISSION_GRANTED) {
            //可以取得COARSE_LOCATION
            rLocationGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (rLocationGranted) {
                Task location = mFusedLocationProvider.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //找到目前位置了
                            Location mLocation = (Location) task.getResult();
                            LatLng mLocation_latlng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                            Log.i("latlng", "latitude is :" + mLocation.getLatitude());
                            Log.i("latlng", "latitude is :" + mLocation.getLongitude());
                            //把一開始地圖的中心拉到目前所在位置
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation_latlng));
                            //地圖縮放12倍
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                            //在目前所在位置放置圖標
                            //mMap.addMarker(new MarkerOptions().position(mLocation_latlng).title("Marker on me"));
                        }
                    }
                });
            }
        } catch (SecurityException ex) {
            // Log.e("LocationError", ex.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        rLocationGranted = false;
        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            rLocationGranted = false;
                            return;
                        }
                    }
                    rLocationGranted = true;
                }
            }
        }
    }

    private boolean chkPlayService() {
        int avail = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);
        if (avail == ConnectionResult.SUCCESS) {
            Log.i("Map Test", "version is Fine");
            return true;
        } else {
            Toast.makeText(this, "版本不符合", Toast.LENGTH_LONG).show();
            return false;
        }
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
        mUiSettings = mMap.getUiSettings();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        getDeviceLocation();
        setmarker();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                //mMap.clear();
                if(marker1 != null){
                    marker1.remove();
                }
                Marker marker =  mMap.addMarker(new MarkerOptions().position(latLng).title("marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marker_latlng = latLng;
                marker1 = marker;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                /*String lat = String.valueOf(marker.getPosition().latitude);
                String lon = String.valueOf(marker.getPosition().longitude);
                Log.i("spt_",lat);
                Log.i("spt_",lon);*/
                //marker_latlng = marker.getPosition();
                Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", marker.getPosition().latitude);
                bundle.putDouble("longitude", marker.getPosition().longitude);
                bundle.putString("title", marker.getTitle());
                markerinformation markerinformation = new markerinformation();
                markerinformation.setArguments(bundle);
                markerinformation.show(getSupportFragmentManager(),"marker");
                return true;
            }
        });
    }

}
