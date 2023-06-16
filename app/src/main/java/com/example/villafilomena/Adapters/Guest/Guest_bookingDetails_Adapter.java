package com.example.villafilomena.Adapters.Guest;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Models.BookingInfo_Model;
import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

public class Guest_bookingDetails_Adapter extends RecyclerView.Adapter<Guest_bookingDetails_Adapter.ViewHolder> {
    Activity activity;
    ArrayList<BookingInfo_Model> bookingHolder;
    String ipAddress;

    public Guest_bookingDetails_Adapter(Activity activity, ArrayList<BookingInfo_Model> bookingHolder) {
        this.activity = activity;
        this.bookingHolder = bookingHolder;

        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");
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

        String roomInfo = getRoomInfo(model.getRoom_id());
        String cottageInfo = getCottageInfo(model.getCottage_id());

        holder.seeMore.setOnClickListener(v -> {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.popup_guest_more_booking_info_dialog);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            //TextView roomName = dialog.findViewById(R.id.guest_bookedInfo_Name);
            TextView Details = dialog.findViewById(R.id.guest_bookedInfo_Details);
            TextView RejectionTxt = dialog.findViewById(R.id.RejectionTxt);
            TextView reasons = dialog.findViewById(R.id.guest_bookedInfo_Reasons);

            Details.setText(
                    "Check-in: "+model.getCheckIn_date() +" - "+ model.getCheckIn_time()+"\n"+
                    "Check-out: "+model.getCheckOut_date() +" - "+ model.getCheckOut_time()+"\n"+
                    "Adult: "+model.getAdult_qty()+"\n"+
                    "Kid: "+model.getKid_qty()+"\n"+
                    "Room: "+roomInfo+"\n"+
                    "Cottage: "+cottageInfo+"\n"+
                    "Total Payment: "+model.getTotal_payment());


            RejectionTxt.setVisibility(View.GONE);
            reasons.setVisibility(View.GONE);
            if (model.getBookings_status().equals("Rejected")) {
                RejectionTxt.setVisibility(View.VISIBLE);
                reasons.setVisibility(View.VISIBLE);
            }

            reasons.setText(model.getReason());

            dialog.show();
        });
    }

    private String getRoomInfo(String roomID) {
        StringJoiner str = new StringJoiner(", ");

        String[] res = roomID.split(",");
        for (String number : res) {
            String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getGuestSelectRoom.php";
            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        str.add(object.getString("roomName"));
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }, error -> Log.e("getRoomInfo", error.toString())) {
                @Override
                protected HashMap<String, String> getParams() {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", number.trim());
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }

        return str.toString();
    }

    private String getCottageInfo(String cottageID) {
        StringJoiner str = new StringJoiner(", ");

        String[] res = cottageID.split(",");
        for (String number : res) {
            String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getGuestSelectedCottage.php";
            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        str.add(object.getString("cottageName"));
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }, error -> Log.e("getRoomInfo", error.toString())) {
                @Override
                protected HashMap<String, String> getParams() {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", number.trim());
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }

        return str.toString();
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
