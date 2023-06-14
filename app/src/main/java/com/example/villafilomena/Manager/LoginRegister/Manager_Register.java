package com.example.villafilomena.Manager.LoginRegister;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Manager.Manager_Dashboard;
import com.example.villafilomena.R;
import com.example.villafilomena.subclass.JavaMailAPI;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Random;

public class Manager_Register extends AppCompatActivity {
    String token;
    String ipAddress;
    TextView login;
    EditText email, password, reEnterPass, firstname, lastname, contact;
    Button signUp;

    public static String generateCode(){
       /* int min = 1; // Minimum value
        int max = 100; // Maximum value
        int count = 6; // Number of random numbers to generate

        code = new StringJoiner("");

        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int randomNumber = random.nextInt(max - min + 1) + min;
            code.add(Integer.toString(randomNumber));
        }*/

        Random random = new Random();
        int pin = random.nextInt(900000) + 100000; // Generate a random 6-digit number
        return String.valueOf(pin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_register);

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

        login = findViewById(R.id.manager_register_Login);
        email = findViewById(R.id.manager_register_Email);
        password = findViewById(R.id.manager_register_Password);
        reEnterPass = findViewById(R.id.manager_register_Password1);
        firstname = findViewById(R.id.manager_register_firstname);
        lastname = findViewById(R.id.manager_register_lastname);
        contact = findViewById(R.id.manager_register_Contact);
        signUp = findViewById(R.id.manager_register_signUp);

        login.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_Login.class));
            finish();
        });

        signUp.setOnClickListener(v -> {
            verify();
        });

    }

    private void register(){
        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/insert/manager_register.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")){
                startActivity(new Intent(this, Manager_Dashboard.class));
                Toast.makeText(this, "Registration Complete", Toast.LENGTH_SHORT).show();
            }
            else if(response.equals("failed")){
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        },
                error -> Toast.makeText(this, error.getMessage().toString(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",email.getText().toString());
                map.put("password",password.getText().toString());
                map.put("firstname",firstname.getText().toString());
                map.put("lastname",lastname.getText().toString());
                map.put("contactNum",contact.getText().toString());
                map.put("token",token);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void verify(){
        String code = generateCode();

        String subject = "Verification Code";

        JavaMailAPI javaMailAPI = new JavaMailAPI(this,email.getText().toString().trim(),subject,code);

        javaMailAPI.execute();

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_manager_register_verification);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        EditText verificationCode = dialog.findViewById(R.id.popup_verifyRegister_code);
        Button verify = dialog.findViewById(R.id.popup_verifyRegister_verify);

        verify.setOnClickListener(v -> {
            if (verificationCode.getText().toString().equals(code)){
                //Toast.makeText(this, "same code", Toast.LENGTH_SHORT).show();
                register();
            } else {
                Toast.makeText(this, "Invalid Code, try again", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}