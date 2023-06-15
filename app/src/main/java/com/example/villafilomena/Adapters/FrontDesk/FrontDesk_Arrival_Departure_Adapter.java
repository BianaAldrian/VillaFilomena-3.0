package com.example.villafilomena.Adapters.FrontDesk;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Models.FrontDesk.FrontDesk_Arrival_Departure_Model;
import com.example.villafilomena.R;

import java.util.ArrayList;
import java.util.HashMap;

public class FrontDesk_Arrival_Departure_Adapter extends RecyclerView.Adapter<FrontDesk_Arrival_Departure_Adapter.ViewHolder> {
    Context context;
    ArrayList<FrontDesk_Arrival_Departure_Model> Holder;
    String status;
    String ipAddress;
    boolean isEnabled;

    public FrontDesk_Arrival_Departure_Adapter(Context context, ArrayList<FrontDesk_Arrival_Departure_Model> holder, String status, boolean isEnabled) {
        this.context = context;
        this.Holder = holder;
        this.status = status;
        this.isEnabled = isEnabled;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frontdesk_arrival_departure_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FrontDesk_Arrival_Departure_Model model = Holder.get(position);

        holder.no.setText("" + model.getCount());
        holder.guestName.setText("" + model.getGuestEmail());
        holder.qty.setText("Adult: " + model.getAdultQty() + "\nKid: " + model.getKidQty());
        holder.roomCottage.setText("" + model.getRoomId() + model.getCottageId());
        holder.total.setText("" + model.getTotal());
        holder.balance.setText("" + model.getBalance());
        holder.date.setText("" + model.getDate());

        if (status.equals("Checked-in")) {
            holder.status.setText("Check-in");

            if (!isEnabled) {
                holder.status.setBackgroundResource(R.color.grey);
                holder.status.setText("Checked-in");
                holder.status.setClickable(false);
            } else {
                holder.status.setOnClickListener(v -> {
                    holder.status.setText("Checked-in");
                    holder.status.setBackgroundResource(R.color.grey);

                    updateStatus(model.getGuestEmail(), "Checked-in");
                    Log.d("Checked-in", model.getGuestEmail());
                });
            }
        } else {
            holder.status.setText("Check-out");
            holder.status.setOnClickListener(v -> {
                holder.status.setText("Checked-out");
                holder.status.setBackgroundResource(R.color.grey);
                Log.d("Check-out", model.getGuestEmail());

                updateStatus(model.getGuestEmail(), "Checked-out");
            });
        }
    }

    private void updateStatus(String guestEmail, String s) {
        Log.d("UpdateStatus", "GuestEmail: " + guestEmail + ", Status: " + s);

        String url = "http://" + ipAddress + "/VillaFilomena/frontdesk_dir/update/frontdesk_updateStatus.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("UpdateStatus", "Response: " + response);
            if (response.equals("success")) {
                Log.d("Token Update", "Token Updated");
            } else if (response.equals("failed")) {
                Log.d("Token Update", "Token Update Failed");
            }
        },
                Throwable::printStackTrace) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("guest_email", guestEmail);
                map.put("status", s);

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }


    @Override
    public int getItemCount() {
        return Holder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView no, guestName, qty, roomCottage, total, balance, date;
        Button status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            no = itemView.findViewById(R.id.arrival_departure_NO);
            guestName = itemView.findViewById(R.id.arrival_departure_guestName);
            qty = itemView.findViewById(R.id.arrival_departure_Qty);
            roomCottage = itemView.findViewById(R.id.arrival_departure_roomCottage);
            total = itemView.findViewById(R.id.arrival_departure_total);
            balance = itemView.findViewById(R.id.arrival_departure_balance);
            date = itemView.findViewById(R.id.arrival_departure_date);
            status = itemView.findViewById(R.id.arrival_departure_status);
        }
    }
}
