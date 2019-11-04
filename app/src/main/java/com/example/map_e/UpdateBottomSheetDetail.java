package com.example.map_e;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;

public class UpdateBottomSheetDetail extends AsyncTask<Object, String, String> {
    private String url, googleplaceData, googlePlaceDetailData;
    private HashMap<String, TextView> mapTextview;
    private HashMap<String, ImageView> mapImageView;

    @Override
    protected String doInBackground(Object... objects) {
        url = (String) objects[0];
        mapTextview = (HashMap<String, TextView>) objects[1];
        mapImageView = (HashMap<String, ImageView>) objects[2];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplaceData = downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String placeID;
        placeID = getPlaceID(googleplaceData);
        String placeDetailURL = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeID +
                "&key=AIzaSyBVMdpbOcNpTGDYk_9FzMSTr6vOGc1Z8T8";

        Log.d("placeDetailActivity", "url= " + placeDetailURL);

        try {
            googlePlaceDetailData = downloadUrl.ReadTheURL(placeDetailURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceDetailData;
    }

    @Override
    protected void onPostExecute(String s) {
        String photoReference = updateBottomSheetDetail(googlePlaceDetailData);
        if (photoReference == null) {
            mapImageView.get("image").getLayoutParams().height = 0;
            mapImageView.get("image").requestLayout();
        } else {
            String photoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=200&photoreference=" + photoReference +
                    "&key=AIzaSyBVMdpbOcNpTGDYk_9FzMSTr6vOGc1Z8T8";
            Object transferData[] = new Object[2];
            transferData[0] = photoURL;
            transferData[1] = mapImageView.get("image");
            GetImage getImage = new GetImage();
            getImage.execute(transferData);
        }


    }

    private String updateBottomSheetDetail(String JSONData) {
        JSONObject jsonObject = null;
        JSONObject googlePlaceJSON = null;
        try {
            jsonObject = new JSONObject(JSONData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            googlePlaceJSON = jsonObject.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String Name = "";
        String Phone = "N/A";
        String Web = "N/A";
        String Availability = "N/A";
        String PhotoReference = "";

        try {
            if (!googlePlaceJSON.isNull("name")) {
                Name = googlePlaceJSON.getString("name");
            }

            if (!googlePlaceJSON.isNull("international_phone_number")) {
                Phone = googlePlaceJSON.getString("international_phone_number");
            }

            if (!googlePlaceJSON.isNull("website")) {
                Web = googlePlaceJSON.getString("website");
            }

            if (!googlePlaceJSON.isNull("opening_hours")) {
                Availability = googlePlaceJSON.getJSONObject("opening_hours").getString("open_now");
            }

            if (!googlePlaceJSON.isNull("photos")) {
                PhotoReference = googlePlaceJSON.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mapTextview.get("name_station").setText(Name);
        mapTextview.get("phone").setText(Phone);
        mapTextview.get("web").setText(Web);
        if (Availability == "true") {
            Availability = "open";
            mapTextview.get("availability").setText(Availability);
            mapTextview.get("availability").setTextColor(Color.GREEN);
        }

        return PhotoReference;
    }

    private String getPlaceID(String JSONData) {

        JSONArray jsonArray = null;
        JSONObject jsonObject = null;
        JSONObject googlePlaceJSON = null;

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

        try {
            googlePlaceJSON = (JSONObject) jsonArray.get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String placeID = "";


        try {
            if (!googlePlaceJSON.isNull("place_id")) {
                placeID = googlePlaceJSON.getString("place_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return placeID;
    }
}
