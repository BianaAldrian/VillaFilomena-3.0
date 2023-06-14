package com.example.villafilomena.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.villafilomena.Models.Image_Model;
import com.example.villafilomena.R;

import java.util.ArrayList;

public class Feedback_Image_Adapter extends RecyclerView.Adapter<Feedback_Image_Adapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Image_Model> urlList;

    public Feedback_Image_Adapter(Context context, ArrayList<Image_Model> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedbacks_image_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image_Model model = urlList.get(position);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.image_placeholder)  // Placeholder image while loading
                .error(R.drawable.error_image)  // Error image if loading fails
                .transform(new RoundedCorners(10));  // Apply rounded corners to the image

        Glide.with(context)
                .load(model.getImage_url())
                .apply(requestOptions)
                .into(holder.imageHolder);

        holder.imageHolder.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_feedback_images);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView imageHolder = dialog.findViewById(R.id.dialog_feedbackImages_imageHolder);

            Glide.with(context)
                    .load(model.getImage_url())
                    .apply(requestOptions)
                    .into(imageHolder);

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageHolder = itemView.findViewById(R.id.feedbackImageList_imageHolder);
        }
    }
}

