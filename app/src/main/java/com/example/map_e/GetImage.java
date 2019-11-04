package com.example.map_e;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class GetImage extends AsyncTask<Object, String, Bitmap> {

    private ImageView imageView;
    private String photoURL;


    protected Bitmap doInBackground(Object... objects) {
        photoURL = (String) objects[0];
        imageView = (ImageView) objects[1];
        Bitmap image = null;
        try{
            InputStream in = new URL(photoURL).openStream();
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e){
            e.printStackTrace();
        }

        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
