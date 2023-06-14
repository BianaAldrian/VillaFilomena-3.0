package com.example.villafilomena.Manager;

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

public class Manager_Account_Page extends AppCompatActivity {
    String ipAddress;
    EditText name, contact, email, password, editFirstname, editLastname, editContact, editEmail, editPassword, editConfirmPass;
    LinearLayout accountContainer, editAccountContainer;
    ImageView edit, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_account_page);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        edit = findViewById(R.id.manager_edit);
        save = findViewById(R.id.manager_save);

        name = findViewById(R.id.manager_fullname);
        contact = findViewById(R.id.manager_contact);
        email = findViewById(R.id.manager_email);
        password = findViewById(R.id.manager_password);
        accountContainer = findViewById(R.id.manager_viewAccount_container);
        editAccountContainer = findViewById(R.id.manager_editAccount_container);

        editFirstname = findViewById(R.id.manager_edit_firstname);
        editLastname = findViewById(R.id.manager_edit_lastname);
        editContact = findViewById(R.id.manager_edit_contact);
        editEmail = findViewById(R.id.manager_edit_email);
        editPassword = findViewById(R.id.manager_edit_password);
        editConfirmPass = findViewById(R.id.manager_edit_confirmPass);

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
        get_Infos();
    }

    private void editAccount() {
        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/update/manager_updateAccount.php";
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
                map.put("firstname",editFirstname.getText().toString().trim());
                map.put("lastname",editLastname.getText().toString());
                map.put("contact",editContact.getText().toString().trim());

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void get_Infos() {
        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getAccount.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            name.setText(jsonObject.getString("firstname") +" "+ jsonObject.getString("lastname"));
                            contact.setText(jsonObject.getString("contactNum"));
                            email.setText(jsonObject.getString("email"));
                            password.setText(jsonObject.getString("password"));

                            editFirstname.setText(jsonObject.getString("firstname"));
                            editLastname.setText(jsonObject.getString("lastname"));
                            editContact.setText(jsonObject.getString("contactNum"));
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
                params.put("email", Manager_Dashboard.email);
                return params;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }
}