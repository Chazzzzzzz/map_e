package com.example.map_e;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String availability = "N/A";
        String photoReference = "";


        try {
            if (!googlePlaceJSON.isNull("name")) {
                NameOfPlace = googlePlaceJSON.getString("name");
            }

            if(!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }

            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");

            if (!googlePlaceJSON.isNull("opening_hours")) {
                availability = googlePlaceJSON.getJSONObject("opening_hours").getString("open_now");
            }

            if (!googlePlaceJSON.isNull("photos")) {
                photoReference = googlePlaceJSON.getJSONArray("photos").getJSONObject(0).getString("photo_reference");

            }

            googlePlaceMap.put("place_name", NameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            googlePlaceMap.put("availability", availability);
            googlePlaceMap.put("photo_reference", photoReference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }


    private List<HashMap<String, String>> getAllNearByPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();
        List<HashMap<String, String>>  nearbyPlacesList = new ArrayList<>();
        HashMap<String, String> nearbyPlaceMap = null;

        for (int i = 0; i < counter; i++) {
            try {
                nearbyPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                nearbyPlacesList.add(nearbyPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearbyPlacesList;
    }

    public List<HashMap<String, String>> parse(String JSONData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(JSONData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearByPlaces(jsonArray);
    }
}
