package com.example.villafilomena.Guest;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class Guest_Register extends AppCompatActivity {
    String token;
    String ipAddress;
    TextInputEditText email, password1, password2, FName, LName, contact;
    Button register;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_register);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    token = task.getResult();
                });

        email = findViewById(R.id.Guest_register_email);
        password1 = findViewById(R.id.Guest_register_password1);
        password2 = findViewById(R.id.Guest_register_password2);
        FName = findViewById(R.id.Guest_register_FName);
        LName = findViewById(R.id.Guest_register_LName);
        contact = findViewById(R.id.Guest_register_contact);
        register = findViewById(R.id.Guest_register_registerBtn);
        login = findViewById(R.id.Guest_register_login);

        register.setOnClickListener(v -> {
            check_email();
        });

        login.setOnClickListener(v -> {
            Guest_Login.originateFrom = "registration";
            startActivity(new Intent(this, Guest_Login.class));
            finish();
        });

    }

    private void check_email() {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_checkemail.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("true")) {
                Toast.makeText(this, "Email already exist", Toast.LENGTH_LONG).show();
            } else if (response.equals("false")) {
                register();
            }
        },
                error -> Toast.makeText(this, error.getMessage().toString(), Toast.LENGTH_LONG).show()) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", email.getText().toString().trim());

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void register() {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/insert/guest_registration.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Toast.makeText(this, "Registration Complete", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, Guest_fragmentsContainer.class));
                Guest_fragmentsContainer.email = email.getText().toString();
                finish();
            } else if (response.equals("failed")) {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show();
            }
        },
                error -> Toast.makeText(this, error.getMessage().toString(), Toast.LENGTH_LONG).show()) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", email.getText().toString().trim());
                map.put("password", password1.getText().toString().trim());
                map.put("fullname", FName.getText().toString().trim() + " " + LName.getText().toString().trim());
                map.put("contact", contact.getText().toString().trim());
                map.put("token", token);

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
}