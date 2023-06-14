package com.example.villafilomena.FrontDesk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.R;

import java.util.HashMap;

public class FrontDesk_Login extends AppCompatActivity {
    String ipAddress;
    EditText username, password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontdesk_login);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        username = findViewById(R.id.frontdesk_login_email);
        password = findViewById(R.id.frontdesk_login_password);
        login = findViewById(R.id.frontdesk_login);

        login.setOnClickListener(v -> {
            Log.d("Username", username.getText().toString().trim());
            Log.d("Password", password.getText().toString().trim());
            Login();
        });

    }

    private void Login() {
        String url = "http://" + ipAddress + "/VillaFilomena/frontdesk_dir/retrieve/frontdesk_login.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("Login Response", response); // Log the response received from the server

            if (response.equals("not_exist")) {
                Toast.makeText(this, "Email doesn't exist", Toast.LENGTH_LONG).show();

            } else if (response.equals("true")) {
                Log.d("Login", "Successful login"); // Log a success message
                startActivity(new Intent(this, FrontDesk_Dashboard.class));
                //Guest_fragmentsContainer.email = email.getText().toString();

            } else if (response.equals("false")) {
                Log.d("Login", "Incorrect password"); // Log an incorrect password message
                Toast.makeText(this, "Password Incorrect", Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Log.e("Login Error", error.getMessage()); // Log the error message
            Toast.makeText(this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", username.getText().toString().trim());
                map.put("password", password.getText().toString().trim());

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

}