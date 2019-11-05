package com.example.map_e;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class Review extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        String stationName = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");

        TextView tvName = findViewById(R.id.review_name);
        tvName.setText(stationName);

        TextView tvAddress = findViewById(R.id.review_address);
        tvAddress.setText(address);

        RatingBar ratingBarOverall = findViewById(R.id.overall_rating);
        RatingBar ratingBarService = findViewById(R.id.service_rating);
        RatingBar ratingBarCleanness = findViewById(R.id.cleanness_rating);
        RatingBar ratingBarPrice = findViewById(R.id.price_rating);

        ratingBarOverall.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });


        Button buttonSubmit = findViewById(R.id.button_rating);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Review.this, "Thanks for the review", Toast.LENGTH_LONG).show();
                openMapsActivity();
            }
        });
    }

    public void openMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
