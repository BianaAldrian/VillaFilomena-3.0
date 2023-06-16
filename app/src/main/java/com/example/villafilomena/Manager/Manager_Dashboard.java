package com.example.villafilomena.Manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Manager.LoginRegister.Manager_Login;
import com.example.villafilomena.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Manager_Dashboard extends AppCompatActivity {
    public static String email;
    LinearLayout signOut, onlineBooking, calendar, guestHomepage, roomCottage_details, employee, rateFeedback, orgChart, transaction;

    ImageView toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CardView navViewAccount;
    TextView fullname;
    String ipAddress;
    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        onlineBooking = findViewById(R.id.manager_onlineBooking);
        calendar = findViewById(R.id.manager_calendar);
        guestHomepage = findViewById(R.id.manager_guestHomepage);
        roomCottage_details = findViewById(R.id.manager_RoomCottage_details);
        employee = findViewById(R.id.manager_Employee);
        rateFeedback = findViewById(R.id.manager_rateFeedback);
        //orgChart = findViewById(R.id.manager_orgChart);
        transaction = findViewById(R.id.manager_Transaction);

        toolbar = findViewById(R.id.manager_menuToolbar);
        drawerLayout = findViewById(R.id.manager_drawerLayout);
        navigationView = findViewById(R.id.manager_navView);
        navViewAccount = findViewById(R.id.manager_navView_account);
        fullname = findViewById(R.id.menu_userName);
        logOut = findViewById(R.id.guest_navView_logOutBtn);
        get_Infos();

        toolbarFunctions();

        /*signOut.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_Login.class));
            finish();
        });*/
        onlineBooking.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_OnlineBooking.class));
        });
        calendar.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_Calendar.class));
        });
        guestHomepage.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_GuestHomepage.class));
        });
        roomCottage_details.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_room_cottage_details.class));
        });
        employee.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_FrontdeskUser.class));
        });
        rateFeedback.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_RatesFeedbacks.class));
        });
      /*  orgChart.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_Organizational_Chart.class));
        });*/
        transaction.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_Transaction_History.class));
        });

    }

    @SuppressLint("SetTextI18n")
    private void get_Infos() {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getAccount.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            fullname.setText(jsonObject.getString("firstname") + " " + jsonObject.getString("lastname"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }

    private void toolbarFunctions() {
        toolbar.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        navViewAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_Account_Page.class));
        });
        logOut.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_Login.class));
            finish();
        });
    }
}