package com.example.villafilomena.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.villafilomena.Models.Image_Model;
import com.example.villafilomena.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Image_Adapter extends RecyclerView.Adapter<Image_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<Image_Model> imageHolder;
    private String ipAddress;
    private boolean showDelete;
    private boolean isUri;
    private RequestManager glideRequestManager;

    public Image_Adapter(Context context, ArrayList<Image_Model> imageHolder, boolean isUri) {
        this.context = context;
        this.imageHolder = imageHolder;
        this.isUri = isUri;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        if (isUri) {
            glideRequestManager = Glide.with(context);
        }
    }

    public void setNewData(boolean showDelete, ArrayList<Image_Model> imageHolder) {
        this.showDelete = showDelete;
        this.imageHolder = imageHolder;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image_Model model = imageHolder.get(position);
        String imagePath = model.getImage_url();

        if (isUri) {
            glideRequestManager
                    .load(imagePath)
                    .into(holder.image);
            holder.progressBar.setVisibility(View.GONE);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(model.getImage_url())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.image);

           /* Picasso.get()
                    .load(imagePath)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loading is successful, hide the progressBar
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            // Image loading failed, handle the error if needed
                        }
                    });*/
        }

        if (!showDelete) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
        }

        holder.delete.setOnClickListener(v -> {
            deleteImage(model.getId());
            imageHolder.remove(position);
            notifyItemRemoved(position);
        });

        holder.image.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_feedback_images);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView imageHolder = dialog.findViewById(R.id.dialog_feedbackImages_imageHolder);

            Glide.with(holder.itemView.getContext())
                    .load(model.getImage_url())
                    .into(imageHolder);

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return imageHolder.size();
    }

    private void deleteImage(String id) {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/delete/manager_deleteImage.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(context, "Image removed", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else if (response.equals("failed")) {
                        Toast.makeText(context, "Image can't be removed", Toast.LENGTH_SHORT).show();
                    }
                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, delete;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageList);
            progressBar = itemView.findViewById(R.id.image_progress);
            delete = itemView.findViewById(R.id.imageList_delete);
        }
    }
}

