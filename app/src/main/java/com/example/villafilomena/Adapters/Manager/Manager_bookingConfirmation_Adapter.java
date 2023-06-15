package com.example.villafilomena.Adapters.Manager;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.villafilomena.Models.BookingInfo_Model;
import com.example.villafilomena.R;
import com.example.villafilomena.subclass.Generate_PDFReceipt;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

@SuppressLint("SetTextI18n")
public class Manager_bookingConfirmation_Adapter extends RecyclerView.Adapter<Manager_bookingConfirmation_Adapter.ViewHolder> {
    Activity activity;
    String ipAddress;
    ArrayList<BookingInfo_Model> bookingHolder;
    String postUrl = "https://fcm.googleapis.com/fcm/send";
    String fcmServerKey = "AAAAI4TgXbw:APA91bE2zEO0mZ5SAiJMRN1l7IzpMsTnmGuVaaayK4CjNhNZl8_13wDpR0ciw4uNPrIQhHD0NaMWj-U0K3Lc97_CStmBq1bn7LXct-jwTEW2GfwqyLXmxlIOytd76qskBgu0VW9HxVY7";
    StorageReference InvoiceReference;
    Dialog loading_dialog;

    private ItemClickListener mItemClickListener;

    public Manager_bookingConfirmation_Adapter(Activity activity, ArrayList<BookingInfo_Model> bookingHolder) {
        this.activity = activity;
        this.bookingHolder = bookingHolder;

        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        InvoiceReference = FirebaseStorage.getInstance().getReference("Receipts");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_pending_booking_list, parent, false);
        return new ViewHolder(view);
    }

    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookingInfo_Model model = bookingHolder.get(position);

        getGuestInfo(model.getGuest_email(), holder.name, holder.email, holder.contact);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy");

            //check-in date
            String inputCheckIn_Date = model.getCheckIn_date();
            Date setCheckIn = inputFormat.parse(inputCheckIn_Date);
            Calendar setCheckIn_Date = Calendar.getInstance();
            setCheckIn_Date.setTime(setCheckIn);
            int checkIn_year = setCheckIn_Date.get(Calendar.YEAR);
            int checkIn_month = setCheckIn_Date.get(Calendar.MONTH);
            int checkIn_dayOfMonth = setCheckIn_Date.get(Calendar.DAY_OF_MONTH);
            // Convert the numeric month to its corresponding word representation
            String[] checkIn_months = new DateFormatSymbols().getMonths();
            String checkIn_monthName = checkIn_months[checkIn_month];
            String checkIn_formattedDate = checkIn_monthName + " " + checkIn_dayOfMonth + ", " + checkIn_year;

            //check-out date
            String inputCheckOut_Date = model.getCheckOut_date();
            Date setCheckOut = inputFormat.parse(inputCheckOut_Date);
            Calendar setCheckOut_Date = Calendar.getInstance();
            setCheckOut_Date.setTime(setCheckOut);
            int checkOut_year = setCheckOut_Date.get(Calendar.YEAR);
            int checkOut_month = setCheckOut_Date.get(Calendar.MONTH);
            int checkOut_dayOfMonth = setCheckOut_Date.get(Calendar.DAY_OF_MONTH);
            // Convert the numeric month to its corresponding word representation
            String[] checkOut_months = new DateFormatSymbols().getMonths();
            String checkOut_monthName = checkOut_months[checkOut_month];
            String checkOut_formattedDate = checkOut_monthName + " " + checkOut_dayOfMonth + ", " + checkOut_year;

            holder.checkIn_checkOut.setText(checkIn_formattedDate + " - " + model.getCheckIn_time() + "\n" + checkOut_formattedDate + " - " + model.getCheckOut_time());

        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception if required
        }

        getRoomInfo(holder.room, model.getRoom_id());

        holder.total.setText("₱" + model.getTotal_payment());
        holder.GCashNumber.setText(model.getGCash_number());
        holder.refNum.setText(model.getReference_num());
        Glide.with(activity)
                .load(model.getProofPay_url())
                .into(holder.proofImage);

        holder.proofImage.setOnClickListener(v -> {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_feedback_images);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView imageHolder = dialog.findViewById(R.id.dialog_feedbackImages_imageHolder);

            Glide.with(holder.itemView.getContext())
                    .load(model.getProofPay_url())
                    .into(imageHolder);

            dialog.show();
        });

        holder.reject.setOnClickListener(v -> {
            Dialog reject = new Dialog(activity);
            reject.setContentView(R.layout.dialog_manager_reject_reason);

            EditText reason = reject.findViewById(R.id.manager_reject_reason);
            Button confirm = reject.findViewById(R.id.manager_reject_confirm);

            confirm.setOnClickListener(v1 -> {
                String reasonText = reason.getText().toString().trim();
                if (TextUtils.isEmpty(reasonText)) {
                    // EditText is empty
                    Toast.makeText(activity, "Reason is empty", Toast.LENGTH_SHORT).show();
                } else {
                    loading_dialog = new Dialog(activity);
                    loading_dialog.setContentView(R.layout.loading_dialog);
                    loading_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    Window loadingWidow = loading_dialog.getWindow();
                    loadingWidow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    //ProgressBar progressBar = loading_dialog.findViewById(R.id.loading_dialog);

                    loading_dialog.show();

                    deleteRoomSched(position);
                    rejectGuestBooking(position, model.getId(), reasonText);
                    reject.dismiss();
                }

            });

            reject.show();
        });

        holder.accept.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Accept");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("YES", (dialog, which) -> {
                loading_dialog = new Dialog(activity);
                loading_dialog.setContentView(R.layout.loading_dialog);
                loading_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Window loadingWidow = loading_dialog.getWindow();
                loadingWidow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                //ProgressBar progressBar = loading_dialog.findViewById(R.id.loading_dialog);

                loading_dialog.show();

                // Handle the OK button click
                confirmGuestBooking(position, model.getId());
            });
            builder.setNegativeButton("NO", (dialog, which) -> {
                // Handle the Cancel button click
                dialog.dismiss(); // Close the dialog
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            //getGuestToken(model.getGuest_email());
            //Toast.makeText(activity, model.getGuest_email(), Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return bookingHolder.size();
    }

    private void getGuestInfo(String guest_email, TextView name, TextView email, TextView contact) {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getGuestInfo.php";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    //SendNotifications(object.getString("token"), message);

                    name.setText("" + object.getString("fullname"));
                    email.setText("" + object.getString("email"));
                    contact.setText("" + object.getString("contact"));

                }
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
            }
        },
                error -> Log.e("getGuestInfo", error.toString())) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", guest_email);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getRoomInfo(TextView room, String roomID) {

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
                        room.setText("" + str);

                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            },
                    error -> Log.e("getRoomInfo", error.toString())) {
                @Override
                protected HashMap<String, String> getParams() {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", number.trim());
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }

    }

    private void confirmGuestBooking(int position, String id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        final BookingInfo_Model model = bookingHolder.get(position);

        Generate_PDFReceipt pdfReceipt = new Generate_PDFReceipt(activity,
                model.getGuest_email(),
                model.getKid_qty(),
                model.getAdult_qty(),
                model.getCheckIn_date(),
                model.getCheckIn_time(),
                model.getCheckOut_date(),
                model.getCheckOut_time(),
                model.getRoom_id(),
                model.getCottage_id(),
                model.getTotal_payment(),
                model.getPayment_status(),
                model.getGCash_number(),
                model.getReference_num());

        pdfReceipt.setUploadCompleteListener(invoiceUrl -> {

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }

            String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/update/manager_confirmGuestBooking.php";
            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                if (response.equals("success")) {
                    Toast.makeText(activity, "Confirmation Successful", Toast.LENGTH_SHORT).show();
                    getGuestToken(model.getGuest_email(), "Your Booking is accepted");
                    loading_dialog.hide();
                } else if (response.equals("failed")) {
                    Toast.makeText(activity, "Confirmation Failed", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                }
            },
                    error -> Log.e("confirmGuestBooking", error.toString())) {
                @Override
                protected HashMap<String, String> getParams() {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", id);
                    map.put("invoiceUrl", invoiceUrl);
                    map.put("date", currentDate);
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        });

        pdfReceipt.generatePDF();
    }

    private void rejectGuestBooking(int position, String id, String reasonText) {
        final BookingInfo_Model model = bookingHolder.get(position);

        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/update/manager_rejectGuestBooking.php";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Toast.makeText(activity, "Booking Rejected", Toast.LENGTH_SHORT).show();
                getGuestToken(model.getGuest_email(), "Your Booking is Rejected");
                loading_dialog.hide();
            } else if (response.equals("failed")) {
                Toast.makeText(activity, "Rejection Failed", Toast.LENGTH_SHORT).show();
                loading_dialog.hide();
            }
        },
                Throwable::printStackTrace) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("reason", reasonText);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void deleteRoomSched(int position) {
        final BookingInfo_Model model = bookingHolder.get(position);

        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/delete/manager_deleteRoomSched.php";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Toast.makeText(activity, "Room Delete Success", Toast.LENGTH_SHORT).show();
            } else if (response.equals("failed")) {
                Toast.makeText(activity, "Room Delete Failed", Toast.LENGTH_SHORT).show();
            }
        },
                error -> Toast.makeText(activity, error.getMessage().toString(), Toast.LENGTH_LONG).show()) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("guest_email", model.getGuest_email());
                map.put("checkIn_date", model.getCheckIn_date());
                map.put("checkIn_time", model.getCheckIn_time());
                map.put("checkOut_date", model.getCheckOut_date());
                map.put("checkOut_time", model.getCheckOut_time());
                map.put("room_id", model.getRoom_id());
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getGuestToken(String guest_email, String message) {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getGuestToken.php";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    SendNotifications(object.getString("token"), message);

                    //Toast.makeText(getContext(), object.getString("token"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        },
                error -> Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", guest_email);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void SendNotifications(String token, String message) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JSONObject mainObj = new JSONObject();

        try {
            mainObj.put("to", token);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", "Villa Filomena");
            notiObject.put("body", message);
            notiObject.put("icon", R.drawable.logo); // enter icon that exists in drawable only

            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
                // code run is got response
            }, error -> {
                // code run is got error
            }) {
                @Override
                public Map<String, String> getHeaders() {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;

                }
            };
            requestQueue.add(request);

        } catch (JSONException e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button accept, reject;
        TextView name, email, contact, checkIn_checkOut, room, cottage, total, balance, GCashNumber, refNum;
        ImageView proofImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.manager_pendingBooking_guestName);
            email = itemView.findViewById(R.id.manager_pendingBooking_guestEmail);
            contact = itemView.findViewById(R.id.manager_pendingBooking_guestContact);
            checkIn_checkOut = itemView.findViewById(R.id.manager_pendingBooking_checkIn_checkOut);
            room = itemView.findViewById(R.id.manager_pendingBooking_room);
            cottage = itemView.findViewById(R.id.manager_pendingBooking_cottage);
            total = itemView.findViewById(R.id.manager_pendingBooking_guestTotal);
            balance = itemView.findViewById(R.id.manager_pendingBooking_guestBalance);
            GCashNumber = itemView.findViewById(R.id.manager_pendingBooking_guestGcashNumber);
            refNum = itemView.findViewById(R.id.manager_pendingBooking_guestRefNum);
            proofImage = itemView.findViewById(R.id.manager_pendingBooking_proofImage);

            accept = itemView.findViewById(R.id.manager_pendingBooking_acceptBtn);
            reject = itemView.findViewById(R.id.manager_pendingBooking_rejectBtn);

        }
    }

}
