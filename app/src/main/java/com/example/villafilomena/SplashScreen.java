package com.example.villafilomena;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String IP = sharedPreferences.getString("IP", "");

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //startActivity(new Intent(SplashScreen.this, ContinueAs.class));
            String url = "http://" + IP + "/VillaFilomena/check_connection.php";

            RequestQueue myRequest = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                if (response.equals("success")) {
                    //Toast.makeText(getApplicationContext(), "IP is correct", Toast.LENGTH_SHORT).show();
                    /*startActivity(new Intent(SplashScreen.this, ContinueAs.class));*/
                    startActivity(new Intent(SplashScreen.this, TermsPrivacy.class));
                    finish();
                } else if (response.equals("failed")) {
                    sharedPreferences.edit().clear().apply();
                    startActivity(new Intent(SplashScreen.this, IP_Connect.class));
                    Toast.makeText(getApplicationContext(), "Can't Connect to Server", Toast.LENGTH_LONG).show();
                }
            },
                    error -> {
                        sharedPreferences.edit().clear().apply();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SplashScreen.this, IP_Connect.class));
                    });
            myRequest.add(stringRequest);
            finish();
        }, 3000);
    }
}