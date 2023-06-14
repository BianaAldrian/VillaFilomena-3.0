package com.example.villafilomena.FrontDesk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.FrontDesk.FrontDesk_Arrival_Departure_Adapter;
import com.example.villafilomena.Models.FrontDesk.FrontDesk_Arrival_Departure_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class FrontDesk_ExpectedData extends AppCompatActivity {
    RecyclerView container;
    String ipAddress;
    ArrayList<FrontDesk_Arrival_Departure_Model> Holder;
    ImageView back, next;
    TextView Txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_desk_expected_data);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        container = findViewById(R.id.frontdesk_container);
        back = findViewById(R.id.frontdesk_back);
        next = findViewById(R.id.frontdesk_next);
        Txt = findViewById(R.id.frontdesk_Txt);

        getArrivalGuest();
        back.setVisibility(View.INVISIBLE);
        back.setOnClickListener(v -> {
            Txt.setText("Expected Arrival");
            next.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);
            getArrivalGuest();
        });
        next.setOnClickListener(v -> {
            Txt.setText("Expected Departure");
            back.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
            getDepartureGuest();
        });

    }

    private void getArrivalGuest() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);

        Holder = new ArrayList<>();

        String url = "http://" + ipAddress + "/VillaFilomena/frontdesk_dir/retrieve/frontdesk_getArrivalGuest.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                boolean isEnabled = true; // Move isEnabled outside the loop

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    FrontDesk_Arrival_Departure_Model model = new FrontDesk_Arrival_Departure_Model(
                            i,
                            object.getString("id"),
                            object.getString("guest_email"),
                            object.getString("adult_qty"),
                            object.getString("kid_qty"),
                            object.getString("room_id"),
                            object.getString("cottage_id"),
                            object.getString("total_payment"),
                            object.getString("payment_status"),
                            object.getString("checkIn_date")
                    );

                    if (!object.getString("bookings_status").equals("Checked-out")) {
                        Holder.add(model);
                        isEnabled = true; // Update isEnabled for each valid model
                    }
                    if (object.getString("bookings_status").equals("Checked-in")) {
                        isEnabled = false; // Set isEnabled to false if any Checked-in status found
                    }
                }

                FrontDesk_Arrival_Departure_Adapter adapter = new FrontDesk_Arrival_Departure_Adapter(this, Holder, "Checked-in", isEnabled);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                container.setLayoutManager(layoutManager);
                container.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("date", formattedDate);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getDepartureGuest(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);

        Holder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/frontdesk_dir/retrieve/frontdesk_getDeparture.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                boolean isEnabled = true; // Move isEnabled outside the loop

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    FrontDesk_Arrival_Departure_Model model = new FrontDesk_Arrival_Departure_Model(
                            i,
                            object.getString("id"),
                            object.getString("guest_email"),
                            object.getString("adult_qty"),
                            object.getString("kid_qty"),
                            object.getString("room_id"),
                            object.getString("cottage_id"),
                            object.getString("total_payment"),
                            object.getString("payment_status"),
                            object.getString("checkIn_date")
                    );

                    if (!object.getString("bookings_status").equals("Checked-in")) {
                        Holder.add(model);
                        isEnabled = true; // Update isEnabled for each valid model
                    }
                    if (object.getString("bookings_status").equals("Checked-out")) {
                        isEnabled = false; // Set isEnabled to false if any Checked-in status found
                    }
                }
                FrontDesk_Arrival_Departure_Adapter adapter = new FrontDesk_Arrival_Departure_Adapter(this, Holder, "Checked-out", isEnabled);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                container.setLayoutManager(layoutManager);
                container.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show())

        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("date", formattedDate);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
}