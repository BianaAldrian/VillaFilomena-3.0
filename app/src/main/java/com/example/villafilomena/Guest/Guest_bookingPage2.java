package com.example.villafilomena.Guest;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.villafilomena.Adapters.Guest.RoomDetails2_Adapter;
import com.example.villafilomena.Models.RoomCottageDetails_Model;
import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class Guest_bookingPage2 extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    String ipAddress;
    RecyclerView roomList, cottageList;
    TextView total, qty, day_night, tally;
    Button backBtn, continueBtn;
    ArrayList<RoomCottageDetails_Model> detailsHolder;
    ArrayList<RoomCottageDetails_Model> cottageHolder;
    String postUrl = "https://fcm.googleapis.com/fcm/send";
    String fcmServerKey = "AAAAI4TgXbw:APA91bE2zEO0mZ5SAiJMRN1l7IzpMsTnmGuVaaayK4CjNhNZl8_13wDpR0ciw4uNPrIQhHD0NaMWj-U0K3Lc97_CStmBq1bn7LXct-jwTEW2GfwqyLXmxlIOytd76qskBgu0VW9HxVY7";
    Dialog loading_dialog;
    String selectedPaymentOption;
    ImageView receipt;
    String imageUrl;
    String GCashNum, RefNum;
    private Uri selectedImageUri;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest_booking_page2, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        roomList = view.findViewById(R.id.Guest_booking2_selectedRoomList);
        cottageList = view.findViewById(R.id.Guest_booking2_selectedCottageList);
        qty = view.findViewById(R.id.Guest_booking2_qty);
        day_night = view.findViewById(R.id.Guest_booking2_days_nights);
        tally = view.findViewById(R.id.Guest_booking2_tally);
        total = view.findViewById(R.id.Guest_booking2_total);
        backBtn = view.findViewById(R.id.Guest_booking2_back);
        continueBtn = view.findViewById(R.id.Guest_booking2_continue);

        detailsHolder = new ArrayList<>();
        cottageHolder = new ArrayList<>();

        qty.setText("" + Guest_bookingPage1.finalAdultQty + "\n" + Guest_bookingPage1.finalKidQty + "\n" + Guest_bookingPage1.selectedRoom_id.size() + "\n" + Guest_bookingPage1.selectedCottage_id.size());
        day_night.setText("" + Guest_bookingPage1.dayDiff + " day/s\n" + Guest_bookingPage1.nightDiff + " night/s\n");
        tally.setText("₱" + Guest_bookingPage1.adultFee + "\n₱" + Guest_bookingPage1.kidFee + "\n₱" + Guest_bookingPage1.roomRate + "\n₱" + Guest_bookingPage1.cottageRate);
        total.setText("Total Payment: ₱" + Guest_bookingPage1.total);

        if (!Guest_bookingPage1.selectedRoom_id.isEmpty()){
            for (String roomId : Guest_bookingPage1.selectedRoom_id) {
                displaySelectedRoom(roomId);
            }
        }

        if (!Guest_bookingPage1.selectedCottage_id.isEmpty()){
            for (String roomId : Guest_bookingPage1.selectedCottage_id) {
                displaySelectedCottage(roomId);
            }
        }

        StringJoiner str = new StringJoiner(",");

        for (String roomId : Guest_bookingPage1.selectedRoom_id) {
            str.add(roomId);
        }

        backBtn.setOnClickListener(v -> back(new Guest_bookingPage1()));

        continueBtn.setOnClickListener(v -> termsConditionDialog());

        return view;
    }

    private void back(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.guestFragmentContainer, fragment).commit();
    }

    private void displaySelectedRoom(String roomId) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getSelectedRoomDetails.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

                RoomDetails2_Adapter adapter = new RoomDetails2_Adapter(detailsHolder);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                roomList.setLayoutManager(layoutManager);
                roomList.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        },
                error -> Log.e("displaySelectedRoom", error.getMessage()))
        {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", roomId);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void displaySelectedCottage(String cottageId){
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getSelectedCottageDetails.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    RoomCottageDetails_Model model = new RoomCottageDetails_Model(
                            object.getString("id"),
                            object.getString("imageUrl"),
                            object.getString("cottageName"),
                            object.getString("cottageCapacity"),
                            object.getString("cottageRate"),
                            object.getString("cottageDescription"));

                    cottageHolder.add(model);
                }

                RoomDetails2_Adapter adapter = new RoomDetails2_Adapter(cottageHolder);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                cottageList.setLayoutManager(layoutManager);
                cottageList.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        },
                error -> Log.e("displaySelectedRoom", error.getMessage()))
        {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", cottageId);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void termsConditionDialog() {
        Dialog termsCondition = new Dialog(getContext());
        termsCondition.setContentView(R.layout.popup_payment_termsandcondition_dialog);
        Window window = termsCondition.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Button contBtn = termsCondition.findViewById(R.id.popup_termsCondition_continue);

        contBtn.setOnClickListener(v -> {
            GCashDialog();
            termsCondition.dismiss();
        });

        termsCondition.show();
    }

    @SuppressLint("SetTextI18n")
    private void GCashDialog() {
        Dialog gcash = new Dialog(getContext());
        gcash.setContentView(R.layout.popup_gcash_payment_dialog);
        Window window = gcash.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        EditText gcashNum = gcash.findViewById(R.id.popup_GCash_guestNumber);
        Spinner paymentOptionSpn = gcash.findViewById(R.id.popup_GCash_paymentOptionSpn);
        TextView totalPayment = gcash.findViewById(R.id.popup_GCash_totalPayment);
        EditText refNum = gcash.findViewById(R.id.popup_GCash_referenceNum);
        Button confirm = gcash.findViewById(R.id.popup_GCash_confirm);
        Button upload = gcash.findViewById(R.id.popup_GCash_upload);
        receipt = gcash.findViewById(R.id.popup_GCash_image);

        String[] paymentOptions = new String[]{"Full", "Partial"};

        totalPayment.setText("" + Guest_bookingPage1.total);

        ArrayAdapter<String> paymentSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, paymentOptions);
        paymentOptionSpn.setAdapter(paymentSpinnerAdapter);

        paymentOptionSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaymentOption = parent.getItemAtPosition(position).toString();

                if (selectedPaymentOption.equals("Full")) {
                    totalPayment.setText("" + Guest_bookingPage1.total);
                } else if (selectedPaymentOption.equals("Partial")) {
                    totalPayment.setText("" + (Guest_bookingPage1.total / 2));
                }
                //Toast.makeText(getContext(), selectedPaymentOption, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        upload.setOnClickListener(v -> {
            chooseImage();
        });

        confirm.setOnClickListener(v -> {
            if (gcashNum.getText().length() < 11 || gcashNum.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Invalid!");
                builder.setMessage("Incomplete GCash number, please double check");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    // Handle the OK button click
                    dialog.dismiss();

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (refNum.getText().length() < 13 || refNum.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                loading_dialog = new Dialog(getContext());
                loading_dialog.setContentView(R.layout.loading_dialog);
                loading_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Window loadingWidow = loading_dialog.getWindow();
                loadingWidow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                //ProgressBar progressBar = loading_dialog.findViewById(R.id.loading_dialog);


                // Upload the selected image to Firebase Storage
                if (selectedImageUri == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("GCash receipt is empty");
                    builder.setMessage("Please provide a proof of GCash payment");
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    loading_dialog.show();
                    gcash.dismiss();
                    uploadImage(selectedImageUri);
                }
            }
        });
        gcash.show();
    }

    private void uploadImage(Uri imageUri) {
        // Generate a unique filename for the image
        String filename = UUID.randomUUID().toString();

        // Create a reference to the Firebase Storage location where you want to store the image
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("ProofImage/" + filename);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image upload success
                // You can retrieve the download URL to store or display the image
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    //imageUrl = uri.toString();
                    if (uri != null && !uri.toString().isEmpty()) {
                        requestBooking(uri.toString());
                    }
                    // Do something with the image URL, such as storing it in a database
                }).addOnFailureListener(e -> {
                    // Handle any errors that occurred during URL retrieval
                    Log.e("uploadImage", e.getMessage());
                });
            } else {
                // Image upload failed
                // Handle the failure case
                Log.e("uploadImage", "Upload Failed");
            }
        });
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Set the selected image to the receipt ImageView
            Glide.with(getContext())
                    .load(selectedImageUri)
                    .into(receipt);
        }
    }

    private void requestBooking(String imageUrl) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/insert/guest_requestBooking.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                selectedImageUri = null;
                getManagerToken();

                if (!Guest_bookingPage1.selectedRoom_id.isEmpty()) {
                    for (String roomId : Guest_bookingPage1.selectedRoom_id) {
                        reserveRoom(roomId);
                    }
                }
                if (!Guest_bookingPage1.selectedCottage_id.isEmpty()) {
                    for (String cottageId : Guest_bookingPage1.selectedCottage_id) {
                        reserveCottage(cottageId);
                    }
                }

                Guest_bookedListPage.fromBooking = true;
                startActivity(new Intent(getContext(), Guest_bookedListPage.class));
                Guest_fragmentsContainer guest = new Guest_fragmentsContainer();
                guest.finish();


                Toast toast = Toast.makeText(getContext(), "Booking Request Successful", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0); // Set the gravity and offset
                toast.show();
                loading_dialog.dismiss();
            } else if (response.equals("failed")) {
                Toast.makeText(getContext(), "Booking Request Failed", Toast.LENGTH_SHORT).show();
                loading_dialog.hide();
            }
        },
                volleyError -> Log.e("requestBooking", volleyError.getMessage()))
        {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("guest_email", Guest_fragmentsContainer.email);
                //map.put("guest_email","aldrian.scarlan@gmail.com");
                map.put("checkIn_date", Guest_bookingPage1.finalCheckIn_date);
                map.put("checkIn_time", Guest_bookingPage1.finalCheckIn_time);
                map.put("checkOut_date", Guest_bookingPage1.finalCheckOut_date);
                map.put("checkOut_time", Guest_bookingPage1.finalCheckOut_time);
                map.put("adult_qty", String.valueOf(Guest_bookingPage1.finalAdultQty));
                map.put("kid_qty", String.valueOf(Guest_bookingPage1.finalKidQty));
                map.put("room_id", String.valueOf(Guest_bookingPage1.selectedRoom_id).replace("[", "").replace("]", "").trim());
                map.put("cottage_id", String.valueOf(Guest_bookingPage1.selectedCottage_id).replace("[", "").replace("]", "").trim());
                map.put("total_payment", String.valueOf(Guest_bookingPage1.total));
                map.put("payment_status", selectedPaymentOption);
                map.put("GCash_number", GCashNum);
                map.put("reference_num", RefNum);
                map.put("proofPay_url", imageUrl);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void reserveRoom(String roomId) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/insert/guest_roomReservation.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Log.d("Room", "Room Reservation Successful");
            } else if (response.equals("failed")) {
                Log.d("Room", "Room Reservation Failed");
            }
        },
                volleyError -> Log.e("reserveRoom", volleyError.getMessage()))
        {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("room_id", roomId);
                map.put("bookBy_guest_email", Guest_fragmentsContainer.email);
                map.put("checkIn_date", Guest_bookingPage1.finalCheckIn_date);
                map.put("checkIn_time", Guest_bookingPage1.finalCheckIn_time);
                map.put("checkOut_date", Guest_bookingPage1.finalCheckOut_date);
                map.put("checkOut_time", Guest_bookingPage1.finalCheckOut_time);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void reserveCottage(String cottageId) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/insert/guest_cottageReservation.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Log.d("Cottage", "Cottage Reservation Successful");
            } else if (response.equals("failed")) {
                Log.d("Cottage", "Cottage Reservation Failed");
            }
        },
                volleyError -> Log.e("reserveCottage", volleyError.getMessage()))
        {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("room_id", cottageId);
                map.put("bookBy_guest_email", Guest_fragmentsContainer.email);
                map.put("checkIn_date", Guest_bookingPage1.finalCheckIn_date);
                map.put("checkIn_time", Guest_bookingPage1.finalCheckIn_time);
                map.put("checkOut_date", Guest_bookingPage1.finalCheckOut_date);
                map.put("checkOut_time", Guest_bookingPage1.finalCheckOut_time);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getManagerToken() {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getManagerToken.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    SendNotifications(object.getString("token"));

                    //Toast.makeText(getContext(), object.getString("token"), Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> Log.e("getManagerToken", volleyError.getMessage()));

        requestQueue.add(stringRequest);
    }

    public void SendNotifications(String token) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject mainObj = new JSONObject();

        try {
            mainObj.put("to", token);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", "Guest");
            notiObject.put("body", "You have a new Booking");
            notiObject.put("icon", R.drawable.logo); // enter icon that exists in drawable only

            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
                // code run is got response
            }, error -> {
                // code run is got error
            }) {
                @Override
                public Map<String, String> getHeaders() {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;

                }
            };
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}