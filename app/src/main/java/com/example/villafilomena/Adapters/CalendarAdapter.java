package com.example.villafilomena.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Manager.Manager_GuestInfo_Adapter;
import com.example.villafilomena.Models.Manager.Manager_GuestInfo_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    String ipAddress;
    Context context;
    private List<Date> datesList;
    private int currentMonth;

    public CalendarAdapter(Context context, List<Date> datesList, int currentMonth) {
        this.context = context;
        this.datesList = datesList;
        this.currentMonth = currentMonth;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentMonth(List<Date> datesList, int currentMonth) {
        this.datesList = datesList;
        this.currentMonth = currentMonth;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_calendar_day_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date = datesList.get(position);

        if (date == null) {
            //Handle empty cells or other scenarios
            holder.day.setText("");
            holder.schedule.setText("");
            //holder.dayView.setVisibility(View.GONE);
        } else {
            // Format the date as desired
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
            String formattedDay = dayFormat.format(date);

            // Set the formatted date to the TextView
            holder.day.setText(formattedDay);

            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            // Fetch the schedule data
            fetchScheduleData(formattedDate, holder.schedule);

            // Adjust the starting day of the week
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            // Set the appropriate text color or styling based on the day of the week
            if (dayOfWeek == Calendar.SUNDAY) {
                // Sunday styling
                holder.day.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            } else if (dayOfWeek == Calendar.SATURDAY) {
                holder.day.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue));
            } else {
                // Other days of the week styling
                holder.day.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
            }

            holder.itemView.setOnClickListener(v -> {
                showScheduledUserDialog(formattedDate);
            });

        }
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    @SuppressLint("SetTextI18n")
    private void fetchScheduleData(String formattedDate, TextView schedule) {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getCalendarSchedules.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                StringBuilder scheduleText = new StringBuilder();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    if (Objects.equals(object.getString("checkIn_count"), "0") && Objects.equals(object.getString("checkOut_count"), "0")) {
                        // No check-in or check-out
                        continue;
                    }

                    String checkInCount = object.getString("checkIn_count");
                    String checkOutCount = object.getString("checkOut_count");

                    if (!Objects.equals(checkInCount, "0")) {
                        scheduleText.append(checkInCount).append(" Check-In");
                    }

                    if (!Objects.equals(checkOutCount, "0")) {
                        if (scheduleText.length() > 0) {
                            scheduleText.append("\n");
                        }
                        scheduleText.append(checkOutCount).append(" Check-Out");
                    }

                    scheduleText.append("\n");
                }

                if (scheduleText.length() > 0) {
                    scheduleText.deleteCharAt(scheduleText.length() - 1); // Remove the last newline character
                } else {
                    scheduleText.append(""); // Set empty text if no schedules found
                }

                schedule.setText(scheduleText.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("date", formattedDate);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void showScheduledUserDialog(String formattedDate) {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getDateSchedule.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray checkInArray = jsonObject.getJSONArray("checkIn");
                JSONArray checkOutArray = jsonObject.getJSONArray("checkOut");

                String[] checkIn_guestEmail_array = new String[checkInArray.length()];
                String[] checkOut_guestEmail_array = new String[checkOutArray.length()];

                //StringJoiner str = new StringJoiner("\n");

                //str.add(formattedDate);

                //str.add("Checking In:");
                // Process check-in data
                for (int i = 0; i < checkInArray.length(); i++) {
                    checkIn_guestEmail_array[i] = checkInArray.getString(i);
                }

                //str.add("Checking Out:");
                // Process check-out data
                for (int i = 0; i < checkOutArray.length(); i++) {
                    checkOut_guestEmail_array[i] = checkOutArray.getString(i);
                    //str.add(guestEmail);
                    //Log.e("CheckOut", guestEmail + " is checking out");
                }

                listScheduledGuest(checkIn_guestEmail_array, checkOut_guestEmail_array);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON", "Error parsing JSON: " + e.getMessage());
            }
        }, error -> {
            error.printStackTrace();
            Log.e("Volley", "Volley error: " + error.getMessage());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("date", formattedDate.trim());
                return map;
            }
        };
        requestQueue.add(stringRequest);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void listScheduledGuest(String[] checkIn_array, String[] checkOut_array) {
        Dialog scheduledUser = new Dialog(context);
        scheduledUser.setContentView(R.layout.popup_manager_calendar_date_scheduled_dialog);
        Window window = scheduledUser.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        RecyclerView checkIn_container = scheduledUser.findViewById(R.id.manager_dialog_scheduledGuest_checkIn_container);
        RecyclerView checkOut_container = scheduledUser.findViewById(R.id.manager_dialog_scheduledGuest_checkOut_container);

        List<Manager_GuestInfo_Model> checkIn_guestInfoList = new ArrayList<>();
        List<Manager_GuestInfo_Model> checkOut_guestInfoList = new ArrayList<>();


        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getCalendarGuestInfo.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            // Handle the response from the server
            try {
                JSONObject jsonResponse = new JSONObject(response);
                System.out.println("jsonResponse " + jsonResponse);

                // Retrieve the check-in and check-out guest arrays
                JSONArray checkInGuests;
                JSONArray checkOutGuests;

                // Check if the checkInGuests value is a string or a JSON array
                Object checkInGuestsObj = jsonResponse.get("checkInGuests");
                if (checkInGuestsObj instanceof String) {
                    checkInGuests = new JSONArray((String) checkInGuestsObj);
                } else {
                    checkInGuests = (JSONArray) checkInGuestsObj;
                }

                // Check if the checkOutGuests value is a string or a JSON array
                Object checkOutGuestsObj = jsonResponse.get("checkOutGuests");
                if (checkOutGuestsObj instanceof String) {
                    checkOutGuests = new JSONArray((String) checkOutGuestsObj);
                } else {
                    checkOutGuests = (JSONArray) checkOutGuestsObj;
                }

                // Process the check-in guest array
                for (int i = 0; i < checkInGuests.length(); i++) {
                    JSONObject guestObject = checkInGuests.getJSONObject(i);
                    String email = guestObject.getString("email");
                    String fullname = guestObject.getString("fullname");
                    String contact = guestObject.getString("contact");

                    // Do something with the guest information
                    // ...
                    Manager_GuestInfo_Model model = new Manager_GuestInfo_Model(email, fullname, contact);
                    checkIn_guestInfoList.add(model);
                }

                Manager_GuestInfo_Adapter checkIn_adapter = new Manager_GuestInfo_Adapter(checkIn_guestInfoList);
                checkIn_container.setLayoutManager(new LinearLayoutManager(context));
                checkIn_container.setAdapter(checkIn_adapter);
                checkIn_adapter.notifyDataSetChanged();


                // Process the check-out guest array
                for (int i = 0; i < checkOutGuests.length(); i++) {
                    JSONObject guestObject = checkOutGuests.getJSONObject(i);
                    String email = guestObject.getString("email");
                    String fullname = guestObject.getString("fullname");
                    String contact = guestObject.getString("contact");

                    // Do something with the guest information
                    // ...
                    Manager_GuestInfo_Model model = new Manager_GuestInfo_Model(email, fullname, contact);
                    checkOut_guestInfoList.add(model);
                }

                Manager_GuestInfo_Adapter checkOut_adapter = new Manager_GuestInfo_Adapter(checkOut_guestInfoList);
                checkOut_container.setLayoutManager(new LinearLayoutManager(context));
                checkOut_container.setAdapter(checkOut_adapter);
                checkOut_adapter.notifyDataSetChanged();

                scheduledUser.show();

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON", "Error parsing JSON: " + e.getMessage());
            }
        }, error -> {
            error.printStackTrace();
            Log.e("Volley", "Volley error: " + error.getMessage());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                JSONArray checkIn_jsonArray = new JSONArray(Arrays.asList(checkIn_array));
                JSONArray checkOut_jsonArray = new JSONArray(Arrays.asList(checkOut_array));

                System.out.println("checkIn_jsonArray " + checkIn_jsonArray);
                System.out.println("checkOut_jsonArray " + checkOut_jsonArray);

                params.put("checkIn_array", checkIn_jsonArray.toString()); // Add the JSONArray directly as a parameter
                params.put("checkOut_array", checkOut_jsonArray.toString()); // Add the JSONArray directly as a parameter

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView dayView;
        TextView day, schedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int textViewWidth = screenWidth / 7;

            dayView = itemView.findViewById(R.id.manager_dayList_dayView);
            dayView.getLayoutParams().width = textViewWidth;

            day = itemView.findViewById(R.id.manager_dayList_day);
            schedule = itemView.findViewById(R.id.manager_dayList_schedule);
        }
    }
}
