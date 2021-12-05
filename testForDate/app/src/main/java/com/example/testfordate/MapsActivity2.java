package com.example.testfordate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManagerNonConfig;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;
import androidx.room.Room;

import android.view.View;
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
import java.util.List;
import java.util.Map;

import DataBase.gmapspot.GmapDatabase;
import DataBase.gmapspot.Gmapspot;
import DataBase.gmapspot.GmapspotDao;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProvider;
    private UiSettings mUiSettings;
    PlacesClient placesClient;
    SearchView searchView;
    private ImageButton locate;
    private ImageButton addpin;
    private TextView search;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    LatLng marker_latlng=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        locate = findViewById(R.id.locate);
        addpin = findViewById(R.id.addpin);
        search = findViewById(R.id.search);

        SupportMapFragment mapFragment = null;
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        GmapDatabase gmapDatabase = GmapDatabase.getInstance(this);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Places.isInitialized())
                    placeinit();
                setPlacesClient();
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG,Place.Field.NAME,Place.Field.ID);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(MapsActivity2.this);
                Log.i("t","send the text");
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getDeviceLocation();
                    }
                }).start();
            }
        });

        addpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(marker_latlng == null){
                    Toast.makeText(MapsActivity2.this,"尚未選擇地點",Toast.LENGTH_LONG).show();
                }
                else{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity2.this);
                    final EditText editText = new EditText(MapsActivity2.this); //final一個editText
                    alertDialog.setTitle("新增標籤，請輸入標題");
                    alertDialog.setView(editText);
                    alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MapsActivity2.this, "加入標籤", Toast.LENGTH_LONG).show();
                            //String title = editText.getText().toString();
                            //String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                            String title = "ppp";
                            String nowDate = "1111";
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //Gmapspot gmapspot = new Gmapspot(marker_latlng.latitude, marker_latlng.longitude, title, nowDate);
                                    //gmapDatabase.GmapspotDao().insertGmapspot(gmapspot);
                                }
                            }).start();
                        }
                    });
                    alertDialog.setNeutralButton("取消", ((dialog, which) -> {
                    }));
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
        });
    }
    void placeinit(){
        Places.initialize(getApplicationContext(),"AIzaSyAvovol62ewDqWllzA2xy3B-gys2X3ig5Y");
    }
    void setPlacesClient(){
        placesClient = Places.createClient(this);
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

    private void getDeviceLocation() {
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        try {
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
        } catch (SecurityException ex) {
            // Log.e("LocationError", ex.getMessage());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDeviceLocation();
            }
        }).start();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("py").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marker_latlng = latLng;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                /*String lat = String.valueOf(marker.getPosition().latitude);
                String lon = String.valueOf(marker.getPosition().longitude);
                Log.i("spt_",lat);
                Log.i("spt_",lon);*/
                marker_latlng = marker.getPosition();
                Toast.makeText(MapsActivity2.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }
}