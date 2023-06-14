package com.example.villafilomena.Manager;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Manager.Manager_Transaction_Adapter;
import com.example.villafilomena.Models.Manager.Transaction_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Manager_Transaction_History extends AppCompatActivity {
    String ipAddress;
    RecyclerView transaction_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_transaction_history);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        transaction_container = findViewById(R.id.manager_transaction_container);

        listBookingRequest();
    }

    public void listBookingRequest() {
        ArrayList<Transaction_Model> transactionHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getTransactionHistory.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Transaction_Model model = new Transaction_Model(
                            object.getString("id"),
                            object.getString("guest_email"),
                            object.getString("transaction_date"),
                            object.getString("total_payment"),
                            object.getString("reference_num"),
                            object.getString("receipt_url")
                    );
                    transactionHolder.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            transaction_container.setLayoutManager(new LinearLayoutManager(this));
            Manager_Transaction_Adapter adapter = new Manager_Transaction_Adapter(transactionHolder);
            transaction_container.setAdapter(adapter);

        }, Throwable::printStackTrace);
        requestQueue.add(stringRequest);
    }
}