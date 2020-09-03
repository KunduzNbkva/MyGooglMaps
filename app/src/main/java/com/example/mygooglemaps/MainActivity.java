package com.example.mygooglemaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private static final String SHARED_PREF_NAME = "map's markers";
    public static final String LONGITUDE = "long";
    public static final String LATITUDE = "lat";
    private GoogleMap map;
    private Button draw_polygon;
    private List<LatLng> list;
    PolygonOptions polygonOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        draw_polygon = findViewById(R.id.button);
        list = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    CameraPosition cameraPosition = CameraPosition
                            .builder()
                            .target(new LatLng(42.8667092, 74.5814769)).zoom(11.67f).build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker()));
                            list.add(latLng);
                            setDraw_polygon(list);
                        }
                    });
                }
            });


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!recoverGeoMarker().isEmpty()){
            setDraw_polygon(recoverGeoMarker());
        }
//        getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE).edit().clear().apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savePrefs(list);

    }

    public void setDraw_polygon(final List<LatLng> list) {
        draw_polygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                polygonOptions = new PolygonOptions();
                polygonOptions.strokeColor(Color.BLACK);
                polygonOptions.strokeWidth(10f);
                if (!list.isEmpty()){
                    for (LatLng latLng : list) {
                        polygonOptions.add(latLng);
                    }
                    map.addPolygon(polygonOptions);
                }


            }
        });
    }

    public void savePrefs(List<LatLng> list) {
        Log.d("tag", "saveGeo()");
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("listSize", list.size());
        for (int i = 0; i < list.size(); i++) {
            editor.putString("lat" + i, String.valueOf(list.get(i).latitude));
            editor.putString("lon" + i, String.valueOf(list.get(i).longitude));
        }
//        editor.putLong(LATITUDE, Double.doubleToRawLongBits(list.get(0).latitude));
//        editor.putLong(LONGITUDE, Double.doubleToRawLongBits(list.get(0).longitude));
        editor.apply();
    }

    private List<LatLng> recoverGeoMarker() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        int listSize = sharedPref.getInt("listSize", 0);
        List<LatLng> newLIst = new ArrayList<>();
        if (listSize != 0) {
            for (int i = 0; i < listSize; i++) {
                Double lat = Double.valueOf(sharedPref.getString("lat" + i, "0"));
                Double lon = Double.valueOf(sharedPref.getString("lon" + i, "0"));
                newLIst.add(new LatLng(lat, lon));
            }
        }
        return newLIst;

    }
}