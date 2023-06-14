package com.example.villafilomena.Adapters.Manager;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.Manager.Transaction_Model;
import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Manager_Transaction_Adapter extends RecyclerView.Adapter<Manager_Transaction_Adapter.ViewHolder> {
    ArrayList<Transaction_Model> transactionHolder;

    public Manager_Transaction_Adapter(ArrayList<Transaction_Model> transactionHolder) {
        this.transactionHolder = transactionHolder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction_Model model = transactionHolder.get(position);

        holder.email.setText(model.getEmail());
        holder.date.setText(model.getDate());
        holder.total.setText(model.getTotal());
        holder.refNum.setText(model.getRefNum());

        holder.print.setOnClickListener(v -> {
            StorageReference invoiceReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getReceiptUrl());
            String InvoiceName = invoiceReference.getName();

            String url = model.getReceiptUrl();

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Receipt");
            request.setDescription("Downloading file...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, InvoiceName);

            DownloadManager manager = (DownloadManager) holder.itemView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        });

    }

    @Override
    public int getItemCount() {
        return transactionHolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView email, date, total, refNum;
        Button print;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.transactionList_email);
            date = itemView.findViewById(R.id.transactionList_date);
            total = itemView.findViewById(R.id.transactionList_total);
            refNum = itemView.findViewById(R.id.transactionList_refNum);
            print = itemView.findViewById(R.id.transactionList_receipt);
        }
    }
}
