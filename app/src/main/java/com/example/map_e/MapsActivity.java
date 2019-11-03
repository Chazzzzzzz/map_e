package com.example.map_e;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationListener;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.widget.Spinner;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import android.view.inputmethod.InputMethodManager;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        AdapterView.OnItemSelectedListener

{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int REQUEST_USER_LOCATION_CODE = 99;

    private double latitude, longitude;
    private int radius = 10000;

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        // add UI car select spinner
        Spinner spinner = findViewById(R.id.spinner_car);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.car_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // add bottom sheet
        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottom_sheet.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // called when the map is ready to use
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                return false;
            }
        });
    }

    public boolean checkUserLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_USER_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    // called when the location change
    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("user current location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        searchNearByChargingStation();

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }


    // called when device is connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    // for search address
    public void onClick(View v) {
        mMap.clear();
        if (v.getId() == R.id.button_search) {
            EditText addressField = (EditText) findViewById(R.id.location_search);
            String address = addressField.getText().toString();

            List<Address> addressList = null;
            MarkerOptions userMarkerOptions = new MarkerOptions();

            if (!TextUtils.isEmpty(address)) {
                Geocoder geocoder = new Geocoder(this);

                try{
                    addressList = geocoder.getFromLocationName(address, 6);

                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++){
                            Address userAddress = addressList.get(i);
                            latitude = userAddress.getLatitude();
                            longitude = userAddress.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);

                            userMarkerOptions.position(latLng);
                            userMarkerOptions.title(address);
                            userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            mMap.addMarker(userMarkerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                            searchNearByChargingStation();
                        }
                    } else {
                        Toast.makeText(this, "location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "please input address", Toast.LENGTH_SHORT).show();
            }
        }
        hideShowKeyboard();
    }

    public void hideShowKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void searchNearByChargingStation() {

        Object transferData[] = new Object[4];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();

        String searchContent = "electric vehicle charging point";
        String url = getUrl(latitude, longitude, searchContent);
        List<HashMap<String, TextView>> mapOfTextview = getMapOfTextview();
        LatLng latLng = new LatLng(latitude, longitude);

        transferData[0] = mMap;
        transferData[1] = url;
        transferData[2] = mapOfTextview;
        transferData[3] = latLng;

        getNearbyPlaces.execute(transferData);
        Toast.makeText(this, "searching nearby charging station", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "showing nearby charging station", Toast.LENGTH_SHORT).show();
    }

    private List<HashMap<String, TextView>> getMapOfTextview() {
        List<HashMap<String, TextView>> mapOfTextview = new ArrayList<>();

        HashMap<String, TextView> singleMapTextview1 = new HashMap<>();
        HashMap<String, TextView> singleMapTextview2 = new HashMap<>();
        HashMap<String, TextView> singleMapTextview3 = new HashMap<>();

        singleMapTextview1.put("name_station", (TextView) findViewById(R.id.name_station1));
        singleMapTextview1.put("address", (TextView) findViewById(R.id.address1));
        singleMapTextview1.put("time", (TextView) findViewById(R.id.time1));
        singleMapTextview1.put("availability", (TextView) findViewById(R.id.availability1));
        singleMapTextview1.put("time", (TextView) findViewById(R.id.time1));

        singleMapTextview2.put("name_station", (TextView) findViewById(R.id.name_station2));
        singleMapTextview2.put("address", (TextView) findViewById(R.id.address2));
        singleMapTextview2.put("time", (TextView) findViewById(R.id.time2));
        singleMapTextview2.put("availability", (TextView) findViewById(R.id.availability2));
        singleMapTextview2.put("time", (TextView) findViewById(R.id.time2));

        singleMapTextview3.put("name_station", (TextView) findViewById(R.id.name_station3));
        singleMapTextview3.put("address", (TextView) findViewById(R.id.address3));
        singleMapTextview3.put("time", (TextView) findViewById(R.id.time3));
        singleMapTextview3.put("availability", (TextView) findViewById(R.id.availability3));
        singleMapTextview3.put("time", (TextView) findViewById(R.id.time3));

        mapOfTextview.add(singleMapTextview1);
        mapOfTextview.add(singleMapTextview2);
        mapOfTextview.add(singleMapTextview3);

        return mapOfTextview;
    }

    private String getUrl(double latitude, double longitude, String searchContent) {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + radius);
        googleURL.append("&keyword=" + searchContent);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyBVMdpbOcNpTGDYk_9FzMSTr6vOGc1Z8T8");

        Log.d("GoogleMapsActivity", "url= " + googleURL.toString());

        return googleURL.toString();
    }

    // called when device connection suspend
    @Override
    public void onConnectionSuspended(int i) {

    }


    // called when connection with device failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
