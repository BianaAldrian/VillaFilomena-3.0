package com.example.villafilomena.FrontDesk;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.villafilomena.R;
import com.google.android.material.card.MaterialCardView;

public class FrontDesk_Dashboard extends AppCompatActivity {
    MaterialCardView booking, arrival_departure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_desk_dashboard);

        booking = findViewById(R.id.frontdesk_booking);
        arrival_departure = findViewById(R.id.frontdesk_arrival_departure);

        booking.setOnClickListener(v -> {
            startActivity(new Intent(this, FrontDesk_Booking1.class));

        });
        arrival_departure.setOnClickListener(v -> {
            startActivity(new Intent(this, FrontDesk_ExpectedData.class));
        });
    }
}