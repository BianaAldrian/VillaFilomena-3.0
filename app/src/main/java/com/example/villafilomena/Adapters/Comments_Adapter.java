package com.example.villafilomena.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.Comments_Model;
import com.example.villafilomena.R;

import java.util.ArrayList;

public class Comments_Adapter extends RecyclerView.Adapter<Comments_Adapter.ViewHolder> {
    Context context;
    ArrayList<Comments_Model> commentsHolder;

    public Comments_Adapter(Context context, ArrayList<Comments_Model> commentsHolder) {
        this.context = context;
        this.commentsHolder = commentsHolder;
    }

    @NonNull
    @Override
    public Comments_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list, parent, false);
        return new Comments_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Comments_Adapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return commentsHolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
