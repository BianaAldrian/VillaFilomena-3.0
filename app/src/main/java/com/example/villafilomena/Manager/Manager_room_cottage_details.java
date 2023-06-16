package com.example.villafilomena.Manager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Cottage_Adapter;
import com.example.villafilomena.Adapters.Room_Adapter;
import com.example.villafilomena.Models.RoomCottageDetails_Model;
import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager_room_cottage_details extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    String ipAddress;
    StorageReference roomImages, cottageImages;
    ImageView Edit, Save;
    RecyclerView roomDetails_Container, cottageDetails_container;
    TextView addRoom, addCottage;
    Dialog addRoom_details, addCottage_details;
    //Image for Room details
    ImageView chosenImage;
    Room_Adapter roomAdapter;
    Cottage_Adapter cottageAdapter;
    ArrayList<RoomCottageDetails_Model> room_detailsHolder;
    ArrayList<RoomCottageDetails_Model> cottage_detailsHolder;
    Uri imageUri;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_room_cottage_details);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        roomImages = FirebaseStorage.getInstance().getReference("RoomImages");
        cottageImages = FirebaseStorage.getInstance().getReference("cottageImages");

        Edit = findViewById(R.id.manager_RoomCottageDetail_Edit);
        Save = findViewById(R.id.manager_RoomCottageDetail_Save);
        addRoom = findViewById(R.id.manager_addRoom);
        addCottage = findViewById(R.id.manager_addCottage);
        roomDetails_Container = findViewById(R.id.manager_roomDetails_Container);
        cottageDetails_container = findViewById(R.id.manager_cottageDetails_Container);

        addRoom.setVisibility(View.GONE);
        addCottage.setVisibility(View.GONE);
        displayRoomDetails();
        displayCottageDetails();

        Edit.setOnClickListener(v -> {
            Save.setVisibility(View.VISIBLE);
            Edit.setVisibility(View.GONE);
            addRoom.setVisibility(View.VISIBLE);
            addCottage.setVisibility(View.VISIBLE);

            roomAdapter.setNewData(true, room_detailsHolder);
            cottageAdapter.setNewData(true, cottage_detailsHolder);
        });

        Save.setOnClickListener(v -> {
            Save.setVisibility(View.GONE);
            Edit.setVisibility(View.VISIBLE);
            addRoom.setVisibility(View.GONE);
            addCottage.setVisibility(View.GONE);

            roomAdapter.setNewData(false, room_detailsHolder);
            cottageAdapter.setNewData(false, cottage_detailsHolder);
        });

        addRoom.setOnClickListener(v -> {
            addRoom_details = new Dialog(this);
            addRoom_details.setContentView(R.layout.popup_add_room_cottage_details_dialog);
            Window window = addRoom_details.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView close = addRoom_details.findViewById(R.id.popup_roomCottageDtl_Close);
            chosenImage = addRoom_details.findViewById(R.id.popup_roomCottageDtl_ChosenImage);
            Button chooseRoomImage = addRoom_details.findViewById(R.id.popup_roomCottageChsImage_btn);
            Button done = addRoom_details.findViewById(R.id.popup_roomCottageDtlDone_Btn);
            EditText roomName = addRoom_details.findViewById(R.id.popup_roomCottageDtl_Name);
            EditText roomCapacity = addRoom_details.findViewById(R.id.popup_roomCottageDtl_Capacity);
            EditText roomRate = addRoom_details.findViewById(R.id.popup_roomCottageDtl_Rate);
            EditText roomDescription = addRoom_details.findViewById(R.id.popup_roomCottageDtl_Description);
            TextView title = addRoom_details.findViewById(R.id.addRoomCottage_title);

            title.setText("Add Cottage Details");
            roomName.setHint("Room Type");
            roomCapacity.setHint("Room Capacity");
            roomRate.setHint("Room Rate");
            roomDescription.setHint("Room Description");

            close.setOnClickListener(v1 -> addRoom_details.hide());

            chooseRoomImage.setOnClickListener(v1 -> chooseImage());

            done.setOnClickListener(v1 -> {
                insertRoomDetails(roomName, roomCapacity, roomRate, roomDescription);
            });

            addRoom_details.show();
        });

        addCottage.setOnClickListener(v -> {
            addCottage_details = new Dialog(this);
            addCottage_details.setContentView(R.layout.popup_add_room_cottage_details_dialog);
            addCottage_details.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Window window = addCottage_details.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView close = addCottage_details.findViewById(R.id.popup_roomCottageDtl_Close);
            chosenImage = addCottage_details.findViewById(R.id.popup_roomCottageDtl_ChosenImage);
            Button chooseCottageImage = addCottage_details.findViewById(R.id.popup_roomCottageChsImage_btn);
            Button done = addCottage_details.findViewById(R.id.popup_roomCottageDtlDone_Btn);
            EditText cottageName = addCottage_details.findViewById(R.id.popup_roomCottageDtl_Name);
            EditText cottageCapacity = addCottage_details.findViewById(R.id.popup_roomCottageDtl_Capacity);
            EditText cottageRate = addCottage_details.findViewById(R.id.popup_roomCottageDtl_Rate);
            EditText cottageDescription = addCottage_details.findViewById(R.id.popup_roomCottageDtl_Description);
            TextView title = addCottage_details.findViewById(R.id.addRoomCottage_title);

            title.setText("Add Cottage Details");
            cottageName.setHint("Cottage Type");
            cottageCapacity.setHint("Cottage Capacity");
            cottageRate.setHint("Cottage Rate");
            cottageDescription.setHint("Cottage Description");

            close.setOnClickListener(v1 -> addCottage_details.hide());

            chooseCottageImage.setOnClickListener(v1 -> chooseImage());

            done.setOnClickListener(v1 -> {
                insertCottageDetails(cottageName, cottageCapacity, cottageRate, cottageDescription);
            });

            addCottage_details.show();
        });
    }

    public void displayRoomDetails() {
        room_detailsHolder = new ArrayList<>();
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getRoomDetails.php";
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

                    room_detailsHolder.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            roomAdapter = new Room_Adapter(this, room_detailsHolder, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            roomDetails_Container.setLayoutManager(layoutManager);
            roomDetails_Container.setAdapter(roomAdapter);
        }, error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show());
        requestQueue.add(stringRequest);
    }

    public void displayCottageDetails() {
        cottage_detailsHolder = new ArrayList<>();

        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getCottageDetails.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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

                    cottage_detailsHolder.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cottageAdapter = new Cottage_Adapter(this, cottage_detailsHolder, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            cottageDetails_container.setLayoutManager(layoutManager);
            cottageDetails_container.setAdapter(cottageAdapter);
        }, error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show());
        requestQueue.add(stringRequest);

    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            chosenImage.setImageURI(imageUri);
        }

    }

    private String getfileExt(Uri MyUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(MyUri));
    }

    private void insertRoomDetails(EditText roomName, EditText roomCapacity, EditText roomRate, EditText roomDescription) {
        if (imageUri != null) {
            String fileName = roomName.getText().toString() + "." + getfileExt(imageUri);
            StorageReference reference = roomImages.child(fileName);
            reference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String roomUrl = uri.toString();
                            String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/insert/manager_uploadRoomDtls.php";
                            RequestQueue requestQueue = Volley.newRequestQueue(this);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                                if (response.equals("success")) {
                                    Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                    addRoom_details.hide();
                                    displayRoomDetails();
                                } else if (response.equals("failed")) {
                                    Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            },
                                    error -> Log.e("insertRoomDetails", error.getMessage())) {
                                @Override
                                protected HashMap<String, String> getParams() {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("imageUrl", roomUrl);
                                    map.put("roomName", roomName.getText().toString());
                                    map.put("roomCapacity", roomCapacity.getText().toString());
                                    map.put("roomRate", roomRate.getText().toString());
                                    map.put("roomDescription", roomDescription.getText().toString());
                                    return map;
                                }
                            };
                            requestQueue.add(stringRequest);
                        });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertCottageDetails(EditText cottageName, EditText cottageCapacity, EditText cottageRate, EditText cottageDescription) {
        if (imageUri != null) {
            String fileName = cottageName.getText().toString() + "." + getfileExt(imageUri);
            StorageReference reference = cottageImages.child(fileName);
            reference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String cottageUrl = uri.toString();
                            String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/insert/manager_uploadCottageDtls.php";
                            RequestQueue requestQueue = Volley.newRequestQueue(this);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                                if (response.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                                    addCottage_details.hide();
                                    displayCottageDetails();
                                } else if (response.equals("failed")) {
                                    Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            },
                                    error -> Log.e("insertCottageDetails", error.getMessage()))
                            {
                                @Override
                                protected HashMap<String, String> getParams() {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("imageUrl", cottageUrl);
                                    map.put("cottageName", cottageName.getText().toString());
                                    map.put("cottageCapacity", cottageCapacity.getText().toString());
                                    map.put("cottageRate", cottageRate.getText().toString());
                                    map.put("cottageDescription", cottageDescription.getText().toString());
                                    return map;
                                }
                            };
                            requestQueue.add(stringRequest);
                        });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}