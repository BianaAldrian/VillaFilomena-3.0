package com.example.villafilomena.Guest;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Guest_accountPage extends AppCompatActivity {
    String ipAddress;
    EditText name, contact, email, password, editFirstname, editLastname, editContact, editEmail, editPassword, editConfirmPass;
    LinearLayout accountContainer, editAccountContainer;
    ImageView eyeOn, eyeOff;

    ImageView edit, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_account_page);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        name = findViewById(R.id.Guest_fullname);
        contact = findViewById(R.id.Guest_contact);
        email = findViewById(R.id.Guest_email);
        password = findViewById(R.id.Guest_password);
        accountContainer = findViewById(R.id.Guest_viewAccount_container);
        editAccountContainer = findViewById(R.id.Guest_editAccount_container);
        edit = findViewById(R.id.Guest_edit);
        save = findViewById(R.id.Guest_save);

        editFirstname = findViewById(R.id.guest_edit_firstname);
        editLastname = findViewById(R.id.guest_edit_lastname);
        editContact = findViewById(R.id.guest_edit_contact);
        editEmail = findViewById(R.id.guest_edit_email);
        editPassword = findViewById(R.id.guest_edit_password);
        editConfirmPass = findViewById(R.id.guest_edit_confirmPass);

        get_Infos();

        save.setVisibility(View.GONE);
        edit.setOnClickListener(v -> {
            accountContainer.setVisibility(View.GONE);
            editAccountContainer.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
        });
        save.setOnClickListener(v -> {
            accountContainer.setVisibility(View.VISIBLE);
            editAccountContainer.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            
            editAccount();
        });

       /* eyeOn.setOnClickListener(v -> {
            eyeOff.setVisibility(View.VISIBLE);
            eyeOn.setVisibility(View.GONE);
            password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        });
        eyeOff.setOnClickListener(v -> {
            eyeOff.setVisibility(View.GONE);
            eyeOn.setVisibility(View.VISIBLE);
            password.setInputType(InputType.TYPE_CLASS_TEXT);
        });*/
    }

    private void editAccount() {
        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/update/guest_updateAccount.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")){
                Toast.makeText(this, "Account Edit Successful", Toast.LENGTH_SHORT).show();
            }
            else if(response.equals("failed")){
                Toast.makeText(this, "Account Edit Failed", Toast.LENGTH_SHORT).show();
            }
        },
                error -> Toast.makeText(this, error.getMessage().toString(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",editEmail.getText().toString().trim());
                map.put("password",editPassword.getText().toString().trim());
                map.put("fullname",editFirstname.getText().toString().trim() +" "+ editLastname.getText().toString());
                map.put("contact",editContact.getText().toString().trim());

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void get_Infos() {
        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_getGuestInfo.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            name.setText(jsonObject.getString("fullname"));
                            contact.setText(jsonObject.getString("contact"));
                            email.setText(jsonObject.getString("email"));
                            password.setText(jsonObject.getString("password"));

                            String[] splName = jsonObject.getString("fullname").split(" ");
                            editFirstname.setText(splName[0]);
                            editLastname.setText(splName[1]);
                            editContact.setText(jsonObject.getString("contact"));
                            editEmail.setText(jsonObject.getString("email"));
                            editPassword.setText(jsonObject.getString("password"));
                            editConfirmPass.setText(jsonObject.getString("password"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", Guest_fragmentsContainer.email);
                return params;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }
}