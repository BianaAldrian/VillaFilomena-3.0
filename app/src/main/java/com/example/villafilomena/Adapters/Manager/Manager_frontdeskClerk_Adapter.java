package com.example.villafilomena.Adapters.Manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Models.Manager.Manager_frondeskClerk_Model;
import com.example.villafilomena.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager_frontdeskClerk_Adapter extends RecyclerView.Adapter<Manager_frontdeskClerk_Adapter.ViewHolder> {
    String ipAddress;
    Activity activity;
    ArrayList<Manager_frondeskClerk_Model> clerkHolder;

    public Manager_frontdeskClerk_Adapter(Activity activity, ArrayList<Manager_frondeskClerk_Model> clerkHolder) {
        this.activity = activity;
        this.clerkHolder = clerkHolder;

        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_frondesk_user_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Manager_frondeskClerk_Model model = clerkHolder.get(position);

        Picasso.get().load(model.getImageUrl()).into(holder.clerkImage);
        holder.clerkName.setText("" + model.getClerkName());
        holder.clerkUsername.setText("" + model.getClerkUsername());
        holder.clerkContact.setText("" + model.getClerkContact());

        holder.delete.setOnClickListener(v -> {
            deleteClerk(model.getClerkUsername());
        });
    }

    private void deleteClerk(String clerkUsername) {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/delete/manager_deleteClerks.php";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Log.d("Like", "clerk deleted");
                Toast.makeText(activity, "Clerk User Deleted", Toast.LENGTH_SHORT).show();
                activity.runOnUiThread(() -> notifyDataSetChanged());
            } else if (response.equals("failed")) {
                Log.d("Like", "clerk failed delete");
            }
        }, Throwable::printStackTrace) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("clerkUsername", clerkUsername);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }


    @Override
    public int getItemCount() {
        return clerkHolder.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView clerkImage, delete;
        TextView clerkStatus, clerkName, clerkUsername, clerkContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            clerkImage = itemView.findViewById(R.id.manager_clerkList_image);
            delete = itemView.findViewById(R.id.manager_clerkList_delete);
            delete.setVisibility(View.GONE);
            clerkName = itemView.findViewById(R.id.manager_clerkList_name);
            clerkUsername = itemView.findViewById(R.id.manager_clerkList_username);
            clerkContact = itemView.findViewById(R.id.manager_clerkList_contact);
        }
    }
}
