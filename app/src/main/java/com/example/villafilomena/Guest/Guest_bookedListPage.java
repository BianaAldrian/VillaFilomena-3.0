package com.example.villafilomena.Guest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Guest.Guest_bookingDetails_Adapter;
import com.example.villafilomena.Models.BookingInfo_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Guest_bookedListPage extends AppCompatActivity {
    public static boolean fromBooking = false;
    String ipAddress;
    String email;
    RecyclerView bookedListContainer;
    ArrayList<BookingInfo_Model> bookingHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_booked_list_page);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        SharedPreferences email_pref = getSharedPreferences("guestEmail_Pref", MODE_PRIVATE);
        email = email_pref.getString("guestEmail", "");

        bookedListContainer = findViewById(R.id.guest_bookedList_container);

        if (fromBooking) {
            Guest_fragmentsContainer guest = new Guest_fragmentsContainer();
            guest.closeActivity();
        }

        getBookingInfo();
    }

    private void getBookingInfo() {
        bookingHolder = new ArrayList<>();

        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getBookings.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    BookingInfo_Model model = new BookingInfo_Model(
                            object.getString("id"),
                            object.getString("guest_email"),
                            object.getString("checkIn_date"),
                            object.getString("checkIn_time"),
                            object.getString("checkOut_date"),
                            object.getString("checkOut_time"),
                            object.getString("adult_qty"),
                            object.getString("kid_qty"),
                            object.getString("room_id"),
                            object.getString("cottage_id"),
                            object.getString("total_payment"),
                            object.getString("payment_status"),
                            object.getString("GCash_number"),
                            object.getString("reference_num"),
                            object.getString("proofPay_url"),
                            object.getString("receipt_url"),
                            object.getString("bookings_status"),
                            object.optString("reason", "")
                    );

                    bookingHolder.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Guest_bookingDetails_Adapter adapter = new Guest_bookingDetails_Adapter(this, bookingHolder);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            bookedListContainer.setLayoutManager(layoutManager);
            bookedListContainer.setAdapter(adapter);

        }, error -> {
            Log.d("getBookingInfo", error.getMessage());
            Toast.makeText(this, "Error retrieving booking information", Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("guest_email", email);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromBooking) {
            Guest_fragmentsContainer.fromBooking = "booking";
            startActivity(new Intent(this, Guest_fragmentsContainer.class));
        }
    }
}