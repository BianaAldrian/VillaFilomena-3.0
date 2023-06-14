package com.example.villafilomena.Adapters.Guest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.RoomCottageDetails_Model;
import com.example.villafilomena.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RoomCottageDetails2_Adapter extends RecyclerView.Adapter<RoomCottageDetails2_Adapter.ViewHolder> {
    ArrayList<RoomCottageDetails_Model> detailsHolder;

    public RoomCottageDetails2_Adapter(ArrayList<RoomCottageDetails_Model> detailsHolder) {
        this.detailsHolder = detailsHolder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_room_cottage_details_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomCottageDetails_Model model = detailsHolder.get(position);

        Picasso.get().load(model.getImageUrl()).into(holder.roomImage);
        holder.roomName.setText(model.getName());
        holder.roomDescription.setText(model.getDescription());
        holder.roomCapacity.setText(model.getCapacity());
        holder.roomRate.setText(model.getRate());
    }

    @Override
    public int getItemCount() {
        return detailsHolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImage;
        TextView roomName, roomDescription, roomCapacity, roomRate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            roomImage = itemView.findViewById(R.id.RoomCottageDetail2_roomImage);
            roomName = itemView.findViewById(R.id.RoomCottageDetail2_roomName);
            roomDescription = itemView.findViewById(R.id.RoomCottageDetail2_roomDescription);
            roomCapacity = itemView.findViewById(R.id.RoomCottageDetail2_roomCapacity);
            roomRate = itemView.findViewById(R.id.RoomCottageDetail2_roomRate);
        }
    }
}
