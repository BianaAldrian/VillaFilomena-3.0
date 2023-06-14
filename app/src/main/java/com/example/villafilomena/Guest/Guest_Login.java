package com.example.villafilomena.Guest;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class Guest_Login extends AppCompatActivity {
    public static String originateFrom = "";
    String token;
    String ipAddress;
    EditText email, password;
    Button login;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_login);

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

        email = findViewById(R.id.Guest_login_email);
        password = findViewById(R.id.Guest_login_password);
        login = findViewById(R.id.Guest_login_loginBtn);
        register = findViewById(R.id.Guest_login_register);

        login.setOnClickListener(v -> {
            login();
        });

        register.setOnClickListener(v -> {
            startActivity(new Intent(this, Guest_Register.class));
            finish();
        });
    }

    private void login() {
        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_login.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("not_exist")){
                Toast toast = Toast.makeText(this, "Email doesn't exist", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0); // Set the gravity and offset
                toast.show();

            } else if(response.equals("true")){
                updateToken();
                startActivity(new Intent(this, Guest_fragmentsContainer.class));
                //Guest_fragmentsContainer.email = email.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("guestEmail_Pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("guestEmail",email.getText().toString());
                editor.apply();

                finish();

            } else if (response.equals("false")) {
                Toast toast = Toast.makeText(this, "Password Incorrect", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0); // Set the gravity and offset
                toast.show();
            }
        },
                error -> Toast.makeText(this, error.getMessage().toString(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",email.getText().toString().trim());
                map.put("password",password.getText().toString().trim());

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updateToken() {
        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/update/guest_updatetoken.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")){
                Log.d("Token Update", "Token Updated");
            } else if(response.equals("failed")){
                Log.d("Token Update", "Token Update Failed");
            }
        },
                Throwable::printStackTrace)
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",email.getText().toString().trim());
                map.put("token",token);

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (originateFrom.equals("fragmentContainer")){
            startActivity(new Intent(this, Guest_fragmentsContainer.class));
            finish();
        } else if (originateFrom.equals("registration")){
            startActivity(new Intent(this, Guest_Register.class));
            finish();
        } else if (originateFrom.equals("booking")){
            startActivity(new Intent(this, Guest_fragmentsContainer.class));
            finish();
        }
        // Add your desired behavior here
        // For example, you can go back to the previous activity or perform some other action

        // Call super.onBackPressed() to allow the default back button behavior (finishing the current activity)
        super.onBackPressed();
    }
}