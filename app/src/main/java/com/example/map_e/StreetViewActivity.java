package com.example.map_e;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.gms.maps.model.StreetViewSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama streetViewPanorama;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("lat", 37.871666);
        double longitude = intent.getDoubleExtra("lng", -122.272781);
        latLng = new LatLng(latitude, longitude);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.street_view);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapsActivity();
            }
        });
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama = streetViewPanorama;
        streetViewPanorama.setPosition(latLng, StreetViewSource.OUTDOOR);
        streetViewPanorama.setStreetNamesEnabled(true);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(true);
        streetViewPanorama.setUserNavigationEnabled(true);
        streetViewPanorama.animateTo(new StreetViewPanoramaCamera.Builder()
                .orientation(new StreetViewPanoramaOrientation(20, 20)).zoom(streetViewPanorama.getPanoramaCamera().zoom)
        .build(), 2000);
        streetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(panoramaChangeListener);
    }

    private StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener panoramaChangeListener = new StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener() {
        @Override
        public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera) {
            Toast.makeText(StreetViewActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();

        }
    };

    public void openMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
