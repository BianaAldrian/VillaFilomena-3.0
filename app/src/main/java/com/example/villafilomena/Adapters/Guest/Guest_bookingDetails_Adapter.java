package com.example.villafilomena.Adapters.Guest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.Models.BookingInfo_Model;
import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Guest_bookingDetails_Adapter extends RecyclerView.Adapter<Guest_bookingDetails_Adapter.ViewHolder> {
    Activity activity;
    ArrayList<BookingInfo_Model> bookingHolder;

    public Guest_bookingDetails_Adapter(Activity activity, ArrayList<BookingInfo_Model> bookingHolder) {
        this.activity = activity;
        this.bookingHolder = bookingHolder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guest_bookings_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingInfo_Model model = bookingHolder.get(position);

        holder.checkIn_info.setText(model.getCheckIn_date() + "\n" + model.getCheckIn_time());
        holder.checkOut_info.setText(model.getCheckOut_date() + "\n" + model.getCheckOut_time());
        holder.booking_status.setText(model.getBookings_status());

        if (model.getBookings_status().equals("Pending")) {
            holder.print_receipt.setVisibility(View.GONE);
        } else if (model.getBookings_status().equals("Rejected")) {
            holder.print_receipt.setVisibility(View.GONE);
        } else {
            holder.print_receipt.setVisibility(View.VISIBLE);
        }

        holder.print_receipt.setOnClickListener(v -> {
            StorageReference invoiceReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getReceipt_url());
            String InvoiceName = invoiceReference.getName();

            String url = model.getReceipt_url();

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Receipt");
            request.setDescription("Downloading file...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, InvoiceName);

            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        });

        holder.seeMore.setOnClickListener(v -> {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.popup_guest_more_booking_info_dialog);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return bookingHolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView checkIn_info, checkOut_info, booking_status;
        Button seeMore, print_receipt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkIn_info = itemView.findViewById(R.id.guest_bookingList_checkIn_dateTime);
            checkOut_info = itemView.findViewById(R.id.guest_bookingList_checkOut_dateTime);
            booking_status = itemView.findViewById(R.id.guest_bookingList_status);
            print_receipt = itemView.findViewById(R.id.guest_bookingList_receipt);
            seeMore = itemView.findViewById(R.id.guest_bookingList_seeMore);
        }
    }
}
