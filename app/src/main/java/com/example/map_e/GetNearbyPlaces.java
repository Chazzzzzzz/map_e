package com.example.map_e;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private String googleplaceData, url;
    private GoogleMap mMap;
    private List<HashMap<String, TextView>> mapOfTextview;
    private int bottomSheetUpdateCount = 0;
    private LatLng userLatLng;

    // used to calculate distance between two latlng
    private static double EARTH_RADIUS = 6378.137;
    // suppose that the car run with the speed of 8.333 m/s,
    // we use the speed to estimate how long will it to take to reach the charging station
    private static double speed = 8.333;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        mapOfTextview = (List<HashMap<String, TextView>>) objects[2];
        userLatLng = (LatLng) objects[3];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplaceData = downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleplaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);
        DisplayNEarbyPlaces(nearbyPlacesList);
    }

    private void DisplayNEarbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            String availability = googleNearbyPlace.get("availability");
            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            // update bottom sheet
            if (bottomSheetUpdateCount < 3) {
                mapOfTextview.get(bottomSheetUpdateCount).get("name_station").setText(nameOfPlace);
                mapOfTextview.get(bottomSheetUpdateCount).get("address").setText(vicinity);
                int minutes = (int) Math.round(getDistanceBetweenLatlng(latLng, userLatLng) / (speed * 60));
                mapOfTextview.get(bottomSheetUpdateCount).get("time").setText(Integer.toString(minutes) + " minutes");
                mapOfTextview.get(bottomSheetUpdateCount).get("availability").setText(availability);
                bottomSheetUpdateCount++;
            }
        }
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private double getDistanceBetweenLatlng(LatLng latLng1, LatLng latLng2) {
        double radLat1 = rad(latLng1.latitude);
        double radLat2 = rad(latLng2.latitude);
        double a = radLat1 - radLat2;
        double b = rad(latLng1.longitude) - rad(latLng2.longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s*1000;
        return s;
    }
}
