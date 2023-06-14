package com.example.villafilomena.Manager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Feedbacks_Adapter;
import com.example.villafilomena.Models.Feedbacks_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager_RatesFeedbacks extends AppCompatActivity {
    String ipAddress;
    RecyclerView feedbackContainer;
    ArrayList<Feedbacks_Model> feedbacksHolder;
    Spinner starRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_rates_feedbacks);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        feedbackContainer = findViewById(R.id.manager_feedbackContainer);
        starRate = findViewById(R.id.manager_starRate);

        String[] starOptions = new String[] {"All", "1.0", "2.0", "3.0", "4.0", "5.0"};

        ArrayAdapter<String> starSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout_list, starOptions);
        starRate.setAdapter(starSpinnerAdapter);

        starRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the Spinner
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Do something with the selected item
                Log.d("Selected Item", selectedItem);

                if (selectedItem.equals("All")){
                    displayFeedbacks();
                } else {
                    displaySortedFeedback(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        displayFeedbacks();
    }

    private void displayFeedbacks(){
        feedbacksHolder = new ArrayList<>();
        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_getFeedbacks.php";
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
                    Feedbacks_Adapter adapter = new Feedbacks_Adapter(this, feedbacksHolder, false);
                    feedbackContainer.setAdapter(adapter);
                },
                Throwable::printStackTrace);
        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }

    private void displaySortedFeedback(String selectedItem){
        feedbacksHolder = new ArrayList<>();
        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_sortFeedback.php";
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
}