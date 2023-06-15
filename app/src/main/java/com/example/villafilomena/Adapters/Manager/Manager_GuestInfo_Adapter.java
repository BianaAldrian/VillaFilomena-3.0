package com.example.villafilomena.Adapters.Manager;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.Manager.Manager_GuestInfo_Model;
import com.example.villafilomena.R;

import java.util.List;

public class Manager_GuestInfo_Adapter extends RecyclerView.Adapter<Manager_GuestInfo_Adapter.ViewHolder> {
    private final List<Manager_GuestInfo_Model> guestInfoList;

    public Manager_GuestInfo_Adapter(List<Manager_GuestInfo_Model> guestInfoList) {
        this.guestInfoList = guestInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_calendar_guest_scheduled_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the Manager_GuestInfo_Model object at the specified position
        Manager_GuestInfo_Model guestInfo = guestInfoList.get(position);

        holder.guestDetails.setText(guestInfo.getFullName() + "\n" + guestInfo.getContact() + "\n" + guestInfo.getEmail());
    }

    @Override
    public int getItemCount() {
        return guestInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView guestDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            guestDetails = itemView.findViewById(R.id.calendar_guestDetails);
        }
    }
}

