package com.example.villafilomena.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.Video_Model;
import com.example.villafilomena.R;

import java.util.ArrayList;

public class Video_Adapter extends RecyclerView.Adapter<Video_Adapter.ViewHolder> {
    Context context;
    private ArrayList<Video_Model> videoHolder;

    public Video_Adapter(Context context, ArrayList<Video_Model> videoHolder) {
        this.context = context;
        this.videoHolder = videoHolder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video_Model model = videoHolder.get(position);

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(holder.video);
        holder.video.setMediaController(mediaController);

        holder.video.setVideoURI(Uri.parse(model.getVideo_url()));

        /*if (TextUtils.isEmpty(model.getVideo_url())) {
            // Video URL is empty, show ProgressBar
            holder.video.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.play.setVisibility(View.GONE);
            holder.pause.setVisibility(View.GONE);
        } else {
            // Video URL is not empty, hide ProgressBar and set up play/pause buttons
            holder.progressBar.setVisibility(View.GONE);
            holder.video.setVisibility(View.VISIBLE);
            holder.video.setVideoURI(Uri.parse(model.getVideo_url()));
            holder.play.setVisibility(View.VISIBLE);
            holder.pause.setVisibility(View.GONE);

            Handler handler = new Handler();
            Runnable hidePauseButtonRunnable = () -> {
                holder.pause.setVisibility(View.GONE);
            };

            holder.play.setOnClickListener(v -> {
                holder.video.start();
                holder.play.setVisibility(View.GONE);
                holder.pause.setVisibility(View.VISIBLE);

                handler.postDelayed(hidePauseButtonRunnable, 3000); // Hide pause button after 3 seconds

                holder.video.setOnTouchListener((view, motionEvent) -> {
                    handler.removeCallbacks(hidePauseButtonRunnable); // Cancel hiding pause button
                    handler.postDelayed(hidePauseButtonRunnable, 3000); // Restart hiding pause button after 3 seconds

                    if (holder.video.isPlaying()) {
                        holder.video.pause();
                        holder.pause.setVisibility(View.GONE);
                        holder.play.setVisibility(View.VISIBLE);
                    } else {
                        holder.video.start();
                        holder.play.setVisibility(View.GONE);
                        holder.pause.setVisibility(View.VISIBLE);
                    }
                    return false;
                });
            });

            holder.pause.setOnClickListener(v -> {
                holder.video.pause();
                holder.pause.setVisibility(View.GONE);
                holder.play.setVisibility(View.VISIBLE);
            });
        }*/
    }

    @Override
    public int getItemCount() {
        return videoHolder.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        VideoView video;
        ProgressBar progressBar;
        ImageButton play, pause;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            video = itemView.findViewById(R.id.videoList);
           /* progressBar = itemView.findViewById(R.id.video_progress);
            play = itemView.findViewById(R.id.video_play);
            pause = itemView.findViewById(R.id.video_pause);*/
        }
    }
}

