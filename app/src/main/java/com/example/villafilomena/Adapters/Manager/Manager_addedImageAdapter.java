package com.example.villafilomena.Adapters.Manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.Manager.Manager_addedImageModel;
import com.example.villafilomena.R;

import java.util.ArrayList;

public class Manager_addedImageAdapter extends RecyclerView.Adapter<Manager_addedImageAdapter.ViewHolder> {
    ArrayList<Manager_addedImageModel> imageHolder;

    public Manager_addedImageAdapter(ArrayList<Manager_addedImageModel> imageHolder) {
        this.imageHolder = imageHolder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_banner_image_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Manager_addedImageModel model = imageHolder.get(position);
        holder.bannerImage.setImageURI(model.getImageUri());

        holder.intro.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return imageHolder.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        TextView intro;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bannerImage = itemView.findViewById(R.id.manager_bannerImageList);
            intro = itemView.findViewById(R.id.manager_introductionList);
        }
    }
}
