package com.example.villafilomena.FrontDesk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Guest.RoomCottageDetails2_Adapter;
import com.example.villafilomena.Models.RoomCottageDetails_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FrontDesk_Booking2 extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    String ipAddress;
    RecyclerView roomListContainer;
    CheckBox cash, gCash;
    Button backBtn, continueBtn;
    ArrayList<RoomCottageDetails_Model> detailsHolder;
    TextView total, qty, day_night, tally;
    String mode_ofPayment, GCash_number, reference_num;
    Dialog loading_dialog;
    ImageView receipt;
    String imageUrl;
    String GCashNum, RefNum, email;
    private Uri selectedImageUri;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_desk_booking2);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        roomListContainer = findViewById(R.id.frontDesk_booking2_selectedRoomList);
        total = findViewById(R.id.frontdesk_booking2_total);
        qty = findViewById(R.id.frontdesk_booking2_qty);
        day_night = findViewById(R.id.frontdesk_booking2_days_nights);
        tally = findViewById(R.id.frontdesk_booking2_tally);
        total = findViewById(R.id.frontdesk_booking2_total);
        backBtn = findViewById(R.id.frontDesk_booking2_back);
        cash = findViewById(R.id.frontDesk_payment_cash);
        gCash = findViewById(R.id.frontDesk_payment_gcash);
        continueBtn = findViewById(R.id.frontDesk_booking2_continue);

        detailsHolder = new ArrayList<>();

        qty.setText("" + FrontDesk_Booking1.finalAdultQty + "\n" + FrontDesk_Booking1.finalKidQty + "\n" + FrontDesk_Booking1.selectedRoom_id.size() + "\n" + FrontDesk_Booking1.selectedCottage_id.size());
        day_night.setText("" + FrontDesk_Booking1.dayDiff + " day/s\n" + FrontDesk_Booking1.nightDiff + " night/s\n");
        tally.setText("₱" + FrontDesk_Booking1.adultFee + "\n₱" + FrontDesk_Booking1.kidFee + "\n₱" + FrontDesk_Booking1.roomRate + "\n₱" + FrontDesk_Booking1.cottageRate);
        total.setText("Total Payment: ₱" + FrontDesk_Booking1.total);

        if (!FrontDesk_Booking1.selectedRoom_id.isEmpty()) {
            for (String roomId : FrontDesk_Booking1.selectedRoom_id) {
                displaySelectedRoom(roomId);
            }
        }
/*
        StringJoiner str = new StringJoiner(",");

        for (String roomId : FrontDesk_Booking1.selectedRoom_id) {
            str.add(roomId);
        }

        if (!FrontDesk_Booking1.selectedRoom_id.isEmpty()){
            for (String roomId : FrontDesk_Booking1.selectedRoom_id) {
                str.add(roomId);
            }
        }*/

        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (isChecked) {
                if (buttonView.getId() == R.id.frontDesk_payment_cash) {
                    gCash.setChecked(false);
                } else if (buttonView.getId() == R.id.frontDesk_payment_gcash) {
                    cash.setChecked(false);
                }
            }
        };
        cash.setOnCheckedChangeListener(listener);
        gCash.setOnCheckedChangeListener(listener);

        backBtn.setOnClickListener(v -> new FrontDesk_Booking1());

        continueBtn.setOnClickListener(v -> {
            if (cash.isChecked()) {
                mode_ofPayment = "cash";
                //Toast.makeText(this, "Cash", Toast.LENGTH_SHORT).show();

                loading_dialog = new Dialog(this);
                loading_dialog.setContentView(R.layout.loading_dialog);
                loading_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Window loadingWidow = loading_dialog.getWindow();
                loadingWidow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                loading_dialog.show();
                insertBooking();

            } else if (gCash.isChecked()) {
                mode_ofPayment = "gcash";
                GCashDialog();
            }

        });
    }

    private void displaySelectedRoom(String roomId) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getSelectedRoomDetails.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    RoomCottageDetails_Model model = new RoomCottageDetails_Model(
                            object.getString("id"),
                            object.getString("imageUrl"),
                            object.getString("roomName"),
                            object.getString("roomCapacity"),
                            object.getString("roomRate"),
                            object.getString("roomDescription"));

                    detailsHolder.add(model);
                }

                roomListContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                RoomCottageDetails2_Adapter adapter = new RoomCottageDetails2_Adapter(detailsHolder);
                roomListContainer.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> Log.e("displaySelectedRoom", error.getMessage())) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", roomId);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void GCashDialog() {
        Dialog gcash = new Dialog(this);
        gcash.setContentView(R.layout.popup_gcash_payment_dialog);
        Window window = gcash.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        EditText gcashNum = gcash.findViewById(R.id.popup_GCash_guestNumber);
        Spinner paymentOptionSpn = gcash.findViewById(R.id.popup_GCash_paymentOptionSpn);
        TextView totalPayment = gcash.findViewById(R.id.popup_GCash_totalPayment);
        EditText refNum = gcash.findViewById(R.id.popup_GCash_referenceNum);
        Button confirm = gcash.findViewById(R.id.popup_GCash_confirm);
        Button upload = gcash.findViewById(R.id.popup_GCash_upload);
        ImageView image = gcash.findViewById(R.id.popup_GCash_image);
        TextView txt = gcash.findViewById(R.id.popup_GCash_uploadTxt);
        EditText guestEmail = gcash.findViewById(R.id.popup_GCash_guestEmail);
        guestEmail.setVisibility(View.VISIBLE);
        txt.setVisibility(View.GONE);
        paymentOptionSpn.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);

        totalPayment.setText("" + FrontDesk_Booking1.total);

        confirm.setOnClickListener(v -> {
            if (gcashNum.getText().length() < 11 || gcashNum.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Invalid!");
                builder.setMessage("Incomplete GCash number, please double check");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    // Handle the OK button click
                    dialog.dismiss();

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (refNum.getText().length() < 13 || refNum.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Invalid!");
                builder.setMessage("Incomplete Reference number, please double check");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    // Handle the OK button click
                    dialog.dismiss();

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                GCashNum = gcashNum.getText().toString();
                RefNum = refNum.getText().toString();
                email = guestEmail.getText().toString();

                loading_dialog = new Dialog(this);
                loading_dialog.setContentView(R.layout.loading_dialog);
                loading_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Window loadingWidow = loading_dialog.getWindow();
                loadingWidow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                loading_dialog.show();
                insertBooking();

                gcash.hide();
            }
        });
        gcash.show();
    }

    private void insertBooking() {
        String url = "http://" + ipAddress + "/VillaFilomena/frontdesk_dir/insert/frontdesk_insertBooking.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                loading_dialog.dismiss();

                if (!FrontDesk_Booking1.selectedRoom_id.isEmpty()) {
                    for (String roomId : FrontDesk_Booking1.selectedRoom_id) {
                        bookRoom(roomId);
                    }
                }

                /*if (!Guest_bookingPage1.selectedCottage_id.isEmpty()){
                    for (String cottageId : FrontDesk_Booking1.selectedCottage_id) {
                        reserveCottage(cottageId);
                    }
                }*/


                Toast toast = Toast.makeText(this, "Booking Request Successful", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0); // Set the gravity and offset
                toast.show();

                startActivity(new Intent(this, FrontDesk_Dashboard.class));
                finish();
                FrontDesk_Booking1 booking1 = new FrontDesk_Booking1();
                booking1.finish();
            } else if (response.equals("failed")) {
                Toast.makeText(this, "Booking Request Failed", Toast.LENGTH_SHORT).show();
                loading_dialog.hide();
            }
        },
                Throwable::printStackTrace) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("guest_email", email);
                map.put("checkIn_date", FrontDesk_Booking1.finalCheckIn_date);
                map.put("checkIn_time", FrontDesk_Booking1.finalCheckIn_time);
                map.put("checkOut_date", FrontDesk_Booking1.finalCheckOut_date);
                map.put("checkOut_time", FrontDesk_Booking1.finalCheckOut_time);
                map.put("adult_qty", String.valueOf(FrontDesk_Booking1.finalAdultQty));
                map.put("kid_qty", String.valueOf(FrontDesk_Booking1.finalKidQty));
                map.put("room_id", String.valueOf(FrontDesk_Booking1.selectedRoom_id).replace("[", "").replace("]", "").trim());
                map.put("cottage_id", "cottage_id");
                map.put("total_payment", String.valueOf(FrontDesk_Booking1.total));
                map.put("mode_ofPayment", mode_ofPayment);
                map.put("payment_status", "full");
                map.put("GCash_number", GCashNum);
                map.put("reference_num", RefNum);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void bookRoom(String roomId) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/insert/guest_roomReservation.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Toast.makeText(this, "Room Booking Successful", Toast.LENGTH_SHORT).show();
            } else if (response.equals("failed")) {
                Toast.makeText(this, "Room Booking Failed", Toast.LENGTH_SHORT).show();
            }
        },
                error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("room_id", roomId);
                map.put("bookBy_guest_email", "FrontDesk_Booking1.email");
                //map.put("bookBy_guest_email", "aldrian.scarlan@gmail.com");
                map.put("checkIn_date", FrontDesk_Booking1.finalCheckIn_date);
                map.put("checkIn_time", FrontDesk_Booking1.finalCheckIn_time);
                map.put("checkOut_date", FrontDesk_Booking1.finalCheckOut_date);
                map.put("checkOut_time", FrontDesk_Booking1.finalCheckOut_time);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
}