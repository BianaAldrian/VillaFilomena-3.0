package com.example.villafilomena.Adapters.Manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.Manager.Manager_frondeskClerk_Model;
import com.example.villafilomena.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Manager_frontdeskClerk_Adapter extends RecyclerView.Adapter<Manager_frontdeskClerk_Adapter.ViewHolder> {
    Activity activity;
    ArrayList<Manager_frondeskClerk_Model> clerkHolder;

    public Manager_frontdeskClerk_Adapter(Activity activity, ArrayList<Manager_frondeskClerk_Model> clerkHolder) {
        this.activity = activity;
        this.clerkHolder = clerkHolder;
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

        });

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
