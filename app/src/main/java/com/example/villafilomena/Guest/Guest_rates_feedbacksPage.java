package com.example.villafilomena.Guest;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Feedbacks_Adapter;
import com.example.villafilomena.Adapters.Image_Adapter;
import com.example.villafilomena.Models.Feedbacks_Model;
import com.example.villafilomena.Models.Image_Model;
import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Guest_rates_feedbacksPage extends AppCompatActivity {
    private static final int PICK_IMAGES_REQUEST_CODE = 1;
    public static boolean fromFeedback = false;
    String ipAddress, email;
    RecyclerView feedbackContainer, imageContainer;
    Button sendFeedback;
    Spinner starRate;
    ArrayList<Image_Model> newImageList;
    String FeedBack;
    float Rating;
    List<String> downloadUrls = new ArrayList<>();
    int uploadedCount = 0;
    //boolean exist_bookings = false, exist_rates = false;
    ArrayList<Feedbacks_Model> feedbacksHolder;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_rates_feedbacks_page);

        fromFeedback = true;

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        SharedPreferences email_pref = getSharedPreferences("guestEmail_Pref", MODE_PRIVATE);
        email = email_pref.getString("guestEmail", "");

        feedbackContainer = findViewById(R.id.guest_feedbackContainer);
        sendFeedback = findViewById(R.id.guest_sendFeedback);
        starRate = findViewById(R.id.guest_starRate);

        String[] starOptions = new String[]{"All", "1.0", "2.0", "3.0", "4.0", "5.0"};

        ArrayAdapter<String> starSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout_list, starOptions);
        starRate.setAdapter(starSpinnerAdapter);

        starRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the Spinner
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Do something with the selected item
                Log.d("Selected Item", selectedItem);

                if (selectedItem.equals("All")) {
                    displayFeedbacks();
                } else {
                    displaySortedFeedback(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (email.equals("")) {
            sendFeedback.setVisibility(View.GONE);
        }

        //displayFeedbacks();

        sendFeedback.setOnClickListener(v -> showFeedbackDialog());
    }

    private void displayFeedbacks() {
        feedbacksHolder = new ArrayList<>();
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getFeedbacks.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Feedbacks_Model model = new Feedbacks_Model(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("guest_email"),
                                    jsonObject.getString("ratings"),
                                    jsonObject.getString("feedback"),
                                    jsonObject.getString("image_urls"),
                                    jsonObject.getString("date"));
                            feedbacksHolder.add(model);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    feedbackContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    Feedbacks_Adapter adapter = new Feedbacks_Adapter(this, feedbacksHolder, true);
                    feedbackContainer.setAdapter(adapter);
                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                // Set the POST parameters
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }

    private void displaySortedFeedback(String selectedItem) {
        feedbacksHolder = new ArrayList<>();
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_sortFeedback.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Feedbacks_Model model = new Feedbacks_Model(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("guest_email"),
                                    jsonObject.getString("ratings"),
                                    jsonObject.getString("feedback"),
                                    jsonObject.getString("image_urls"),
                                    jsonObject.getString("date"));
                            feedbacksHolder.add(model);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    feedbackContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    Feedbacks_Adapter adapter = new Feedbacks_Adapter(this, feedbacksHolder, true);
                    feedbackContainer.setAdapter(adapter);
                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                // Set the POST parameters
                Map<String, String> params = new HashMap<>();
                params.put("ratings", selectedItem);
                return params;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }

    // Create a method to retrieve the response from the server
   /* private void checkBookingsAndRates() {
        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_checkEmailforFeedback.php";

        // Create a StringRequest for the HTTP GET request
        // Handle error
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String bookings = jsonObject.getString("bookings");
                        String rates = jsonObject.getString("rates");

                        // Handle the bookings and rates variables according to your logic
                        if (bookings.equals("exist_bookings")) {
                            // Bookings exist
                            //exist_bookings = true;
                            sendFeedback.setVisibility(View.VISIBLE);
                            if (rates.equals("exist_rates")) {
                                // Rates exist
                                //exist_rates = true;
                                sendFeedback.setVisibility(View.VISIBLE);
                            } else if (rates.equals("not_exist_rates")) {
                                // Rates do not exist
                                //exist_bookings = false;
                                sendFeedback.setVisibility(View.GONE);
                            }
                        } else if (bookings.equals("not_exist_bookings")) {
                            // Bookings do not exist
                            //exist_rates = false;
                            sendFeedback.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                // Set the POST parameters
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showFeedbackDialog() {
        Dialog feedback = new Dialog(this);
        feedback.setContentView(R.layout.dialog_guest_feedback);

        AppCompatRatingBar ratingBar = feedback.findViewById(R.id.guest_rating_dialog);
        TextView rateValue = feedback.findViewById(R.id.guest_rateValue_dialog);
        EditText review = feedback.findViewById(R.id.guest_feedback_dialog);
        imageContainer = feedback.findViewById(R.id.guest_imageContainer_dialog);
        Button addImage = feedback.findViewById(R.id.guest_addImage_dialog);
        Button submit = feedback.findViewById(R.id.guest_submit_dialog);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            String wordValue;

            switch (Math.round(rating)) {
                case 1:
                    wordValue = "Very dissatisfied";
                    break;
                case 2:
                    wordValue = "Dissatisfied";
                    break;
                case 3:
                    wordValue = "Neutral";
                    break;
                case 4:
                    wordValue = "Satisfied";
                    break;
                case 5:
                    wordValue = "Very satisfied";
                    break;
                default:
                    wordValue = "";
                    break;
            }

            rateValue.setText(wordValue);
        });

        newImageList = new ArrayList<>();
        imageContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        addImage.setOnClickListener(v -> selectImage());

        submit.setOnClickListener(v -> {
            Rating = ratingBar.getRating();
            FeedBack = review.getText().toString();

            // Check if there are any images to upload
            if (newImageList.size() > 0) {
                uploadImages();
            } else {
                insertFeedbacks(null);
            }

            feedback.hide();
        });

        feedback.show();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple images were selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        newImageList.add(new Image_Model("", imageUri.toString()));
                    }
                } else if (data.getData() != null) {
                    // Single image was selected
                    Uri imageUri = data.getData();
                    newImageList.add(new Image_Model("", imageUri.toString()));
                }

                // Update the image container adapter
                Image_Adapter adapter = new Image_Adapter(this, newImageList, true);
                imageContainer.setAdapter(adapter);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadImages() {
        for (Image_Model image : newImageList) {
            uploadImageToFirebaseStorage(image);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadImageToFirebaseStorage(Image_Model image) {
        String filename = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("feedbacksImages/" + filename);
        Uri imageUri = Uri.parse(image.getImage_url());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            downloadUrls.add(uri.toString());
                            uploadedCount++;

                            if (uploadedCount == newImageList.size()) {
                                insertFeedbacks(downloadUrls);
                            }
                        }).addOnFailureListener(Throwable::printStackTrace))
                .addOnFailureListener(Throwable::printStackTrace);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void insertFeedbacks(List<String> downloadUrls) {
        // Get the current date
        //LocalDate currentDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now();

        // Create a DateTimeFormatter for the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Format the current date using the formatter
        String formattedDate = currentDate.format(formatter);

        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/insert/guest_insertFeedbacks.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(this, "Feedback successfully", Toast.LENGTH_LONG).show();
                        displayFeedbacks();
                    } else if (response.equals("failed")) {
                        Toast.makeText(this, "Feedback failed", Toast.LENGTH_SHORT).show();
                    }
                },
                volleyError -> volleyError.printStackTrace()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("ratings", String.valueOf(Rating));
                params.put("feedback", FeedBack);
                params.put("date", formattedDate);

                if (downloadUrls == null) {
                    params.put("image_urls", "");
                } else {
                    params.put("image_urls", String.join(",", downloadUrls));
                }

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}