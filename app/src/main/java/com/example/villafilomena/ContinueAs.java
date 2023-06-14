package com.example.villafilomena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.villafilomena.FrontDesk.FrontDesk_Login;
import com.example.villafilomena.Guest.Guest_fragmentsContainer;
import com.example.villafilomena.Manager.LoginRegister.Manager_Login;

public class ContinueAs extends AppCompatActivity {
    Button guest, frontdesk, manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_as);

        guest = findViewById(R.id.btnGuest);
        frontdesk = findViewById(R.id.btnFrontdesk);
        manager = findViewById(R.id.btnManager);

        guest.setOnClickListener(view -> {
            startActivity(new Intent(ContinueAs.this, Guest_fragmentsContainer.class));
        });

        frontdesk.setOnClickListener(view -> {
            //startActivity(new Intent(ContinueAs.this, FrontDesk_Dashboard.class));
            startActivity(new Intent(ContinueAs.this, FrontDesk_Login.class));
        });

        manager.setOnClickListener(view -> {
            //startActivity(new Intent(ContinueAs.this, Manager_Dashboard.class));
            startActivity(new Intent(ContinueAs.this, Manager_Login.class));
        });

    }
}