package com.example.villafilomena.Adapters.Manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.Manager.Manager_GuestHomepageViews_Model;
import com.example.villafilomena.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Manager_GuestHomepageViews_Adapter extends RecyclerView.Adapter<Manager_GuestHomepageViews_Adapter.ViewHolder> {

    ArrayList<Manager_GuestHomepageViews_Model> GuestView_holder;

    public Manager_GuestHomepageViews_Adapter(ArrayList<Manager_GuestHomepageViews_Model> guestView_holder) {
        GuestView_holder = guestView_holder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_banner_image_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Manager_GuestHomepageViews_Model model = GuestView_holder.get(position);

        Picasso.get().load(model.getBannerUrl()).into(holder.bannerImage);
        holder.intro.setText("" + model.getIntroduction());
        Picasso.get().load(model.getImageUrl()).into(holder.bannerImage);

        if (model.getIntroduction() == null) {
            holder.intro.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return GuestView_holder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        TextView intro;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bannerImage = itemView.findViewById(R.id.manager_bannerImageList);
            intro = itemView.findViewById(R.id.manager_introductionList);
        }
    }
}
