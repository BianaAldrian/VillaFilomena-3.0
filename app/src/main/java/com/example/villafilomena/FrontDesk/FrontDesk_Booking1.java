package com.example.villafilomena.FrontDesk;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Cottage_Adapter;
import com.example.villafilomena.Adapters.Guest.Guest_MonthYearAdapter;
import com.example.villafilomena.Adapters.Room_Adapter;
import com.example.villafilomena.Models.RoomCottageDetails_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FrontDesk_Booking1 extends AppCompatActivity {
    public static String finalCheckIn_date;
    public static String finalCheckOut_date;
    public static String finalCheckIn_time;
    public static String finalCheckOut_time;
    public static ArrayList<String> selectedRoom_id;
    public static ArrayList<String> selectedCottage_id;
    public static boolean showBox = false;
    public static int finalAdultQty, finalKidQty;
    public static double total;
    public static int dayDiff, nightDiff;
    public static double dayTour_kidFee, dayTour_adultFee, nightTour_kidFee, nightTour_adultFee, kidFee, adultFee, roomRate, cottageRate;
    String ipAddress;
    CardView sched, qty;
    TextView dayTourInfo, nightTourInfo, displaySched, displayQty;
    RecyclerView roomList, cottageList;
    Button continueBtn;
    ArrayList<RoomCottageDetails_Model> roomHolder;
    ArrayList<RoomCottageDetails_Model> cottageHolder;
    private Guest_MonthYearAdapter calendarAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_desk_booking1);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        dayTourInfo = findViewById(R.id.frontdesk_dayTourInfo);
        nightTourInfo = findViewById(R.id.frontdesk_nightTourInfo);
        sched = findViewById(R.id.frontdesk_pickSched);
        qty = findViewById(R.id.frontdesk_pickQty);
        displaySched = findViewById(R.id.frontdesk_sched);
        displayQty = findViewById(R.id.frontdesk_qty);
        roomList = findViewById(R.id.frontdesk_roomList);
        cottageList = findViewById(R.id.frontdesk_cottageList);
        continueBtn = findViewById(R.id.frontdesk_booking_continue);

        getEntranceFee_Details();

        sched.setOnClickListener(v -> showBottomDialog());
        qty.setOnClickListener(v -> pickQty());

        selectedRoom_id = new ArrayList<>();
        selectedCottage_id = new ArrayList<>();

        continueBtn.setOnClickListener(v -> {
            if (finalCheckIn_date == null){
                Toast.makeText(this, "Check-In and Check-Out not set", Toast.LENGTH_SHORT).show();
            } else if (finalAdultQty == 0) {
                Toast.makeText(this, "Adult Quantity not set", Toast.LENGTH_SHORT).show();
            } else {

                //for getting the checked rooms
                double roomTotalPrice = 0;

                int roomChildCount = roomList.getChildCount();
                for (int i=0; i<roomChildCount; i++){
                    View childView = roomList.getLayoutManager().findViewByPosition(i);
                    ImageView check = childView.findViewById(R.id.RoomCottageDetail_check);
                    if (check.getVisibility() == View.VISIBLE){
                        final RoomCottageDetails_Model model = roomHolder.get(i);
                        selectedRoom_id.add(model.getId());

                        roomTotalPrice += Double.parseDouble(model.getRate());
                    }
                }

                //for getting the checked cottages
                double cottageTotalPrice = 0;
                int cottageChildCount = cottageList.getChildCount();
                for (int i=0; i<cottageChildCount; i++){
                    View childView = cottageList.getLayoutManager().findViewByPosition(i);
                    ImageView check = childView.findViewById(R.id.RoomCottageDetail_check);
                    if (check.getVisibility() == View.VISIBLE){
                        final RoomCottageDetails_Model model = cottageHolder.get(i);
                        selectedCottage_id.add(model.getId());

                        cottageTotalPrice += Double.parseDouble(model.getRate());
                    }
                }

                double dayTour_roomRate, nightTour_roomRate;
                double dayTour_cottageRate, nightTour_cottageRate;

                dayTour_roomRate = roomTotalPrice * dayDiff;
                nightTour_roomRate = roomTotalPrice * nightDiff;
                dayTour_cottageRate = cottageTotalPrice * dayDiff;
                nightTour_cottageRate = cottageTotalPrice * nightDiff;

                roomRate = dayTour_roomRate + nightTour_roomRate;
                cottageRate = dayTour_cottageRate + nightTour_cottageRate;

                dayTour_kidFee = (finalKidQty * dayTour_kidFee) * dayDiff;
                dayTour_adultFee = (finalAdultQty * dayTour_adultFee) * dayDiff;
                nightTour_kidFee = (finalKidQty * nightTour_kidFee) * nightDiff;
                nightTour_adultFee = (finalAdultQty * nightTour_adultFee) * nightDiff;

                adultFee = dayTour_adultFee + nightTour_adultFee;
                kidFee = dayTour_kidFee + nightTour_kidFee;

                total = dayTour_kidFee + dayTour_adultFee + nightTour_kidFee + nightTour_adultFee + dayTour_roomRate + nightTour_roomRate + dayTour_cottageRate + nightTour_cottageRate;

                Log.d("Day Diff", dayDiff + "\n" + nightDiff);
                startActivity(new Intent(this, FrontDesk_Booking2.class));
            }
        });

       displayRooms();
       displayCottages();
    }

    @SuppressLint("SetTextI18n")
    private void getEntranceFee_Details(){
        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_entrFee_details.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    dayTour_kidFee = Double.parseDouble(object.getString("dayTour_kidFee"));
                    dayTour_adultFee = Double.parseDouble(object.getString("dayTour_adultFee"));
                    nightTour_kidFee = Double.parseDouble(object.getString("nightTour_kidFee"));
                    nightTour_adultFee = Double.parseDouble(object.getString("nightTour_adultFee"));

                    dayTourInfo.setText(""+object.getString("dayTour_time") +"\n\n" +
                            "KID "+ object.getString("dayTour_kidAge") + " - ₱" + object.getString("dayTour_kidFee") +"\n"+
                            "ADULT "+  object.getString("dayTour_adultAge") + " - ₱" + object.getString("dayTour_adultFee"));

                    nightTourInfo.setText(""+object.getString("nightTour_time") +"\n\n" +
                            "KID "+ object.getString("nightTour_kidAge") + " - ₱" + object.getString("nightTour_kidFee") +"\n"+
                            "ADULT "+  object.getString("nightTour_adultAge") + " - ₱" + object.getString("nightTour_adultFee"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> Log.d("getEntranceFee_Details", error.getMessage()));

        requestQueue.add(stringRequest);
    }

   /* @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void pickSched() {
        Dialog calendar = new Dialog(this);
        calendar.setContentView(R.layout.popup_booking_calendar_dialog);
        calendar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = calendar.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        CalendarView checkIn = calendar.findViewById(R.id.popup_pickDate_checkIn);
        CalendarView checkOut = calendar.findViewById(R.id.popup_pickDate_checkOut);
        CheckBox checkIn_dayTour = calendar.findViewById(R.id.popup_checkIn_dayTour);
        CheckBox checkIn_nightTour = calendar.findViewById(R.id.popup_checkIn_nightTour);
        CheckBox checkOut_dayTour = calendar.findViewById(R.id.popup_checkOut_dayTour);
        CheckBox checkOut_nightTour = calendar.findViewById(R.id.popup_checkOut_nightTour);
        Button cancel = calendar.findViewById(R.id.popup_pickDate_cancel);
        Button done = calendar.findViewById(R.id.popup_pickDate_done);

        checkIn.setDate(System.currentTimeMillis());
        checkOut.setDate(System.currentTimeMillis());

        String[] checkIn_date = {""};
        String[] checkOut_date = {""};
        String[] checkIn_time = {""};
        String[] checkOut_time = {""};

        checkIn.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            checkIn_date[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        checkOut.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            checkOut_date[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (isChecked) {
                if (buttonView.getId() == R.id.popup_checkIn_dayTour) {
                    checkIn_nightTour.setChecked(false);
                    checkIn_time[0] = "dayTour";
                } else if (buttonView.getId() == R.id.popup_checkIn_nightTour) {
                    checkIn_dayTour.setChecked(false);
                    checkIn_time[0] = "nightTour";
                } else if (buttonView.getId() == R.id.popup_checkOut_dayTour) {
                    checkOut_nightTour.setChecked(false);
                    checkOut_time [0] = "dayTour";
                } else if (buttonView.getId() == R.id.popup_checkOut_nightTour) {
                    checkOut_dayTour.setChecked(false);
                    checkOut_time [0] = "nightTour";
                }
            }
        };

        checkIn_dayTour.setOnCheckedChangeListener(listener);
        checkIn_nightTour.setOnCheckedChangeListener(listener);
        checkOut_dayTour.setOnCheckedChangeListener(listener);
        checkOut_nightTour.setOnCheckedChangeListener(listener);

        cancel.setOnClickListener(v -> {
            calendar.hide();
        });

        done.setOnClickListener(v -> {
            finalCheckIn_date = checkIn_date[0];
            finalCheckIn_time = checkIn_time[0];
            finalCheckOut_date = checkOut_date[0];
            finalCheckOut_time = checkOut_time[0];

            Log.d("Tag","finalCheckIn_date: " + finalCheckIn_date);
            Log.d("Tag","finalCheckIn_time: " + finalCheckIn_time);
            Log.d("Tag","finalCheckOut_date: " + finalCheckOut_date);
            Log.d("Tag","finalCheckOut_time: " + finalCheckOut_time);

            //Toast.makeText(getContext(), Arrays.toString(checkIn_date) + "\n"+ Arrays.toString(checkOut_date) + "\n"+ Arrays.toString(checkIn_time) + "\n"+ Arrays.toString(checkOut_time), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), finalCheckIn_date + "\n"+ finalCheckIn_time + "\n"+ finalCheckOut_date + "\n"+  finalCheckOut_time, Toast.LENGTH_SHORT).show();

            if (finalCheckIn_date.equals("")){
                Toast.makeText(this, "Select Check-In Date", Toast.LENGTH_SHORT).show();
            } else if (finalCheckOut_date.equals("")) {
                Toast.makeText(this, "Select Check-Out Date", Toast.LENGTH_SHORT).show();
            } else if (finalCheckIn_time.equals("")) {
                Toast.makeText(this, "Select Check-In Time", Toast.LENGTH_SHORT).show();
            } else if (finalCheckOut_time.equals("")) {
                Toast.makeText(this, "Select Check-Out Time", Toast.LENGTH_SHORT).show();
            } else {
                getDateDifference();
                displayAvailableRooms(finalCheckIn_date, finalCheckIn_time, finalCheckOut_date, finalCheckOut_time);
                displayAvailableCottage();

                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy");

                    //check-in date
                    String inputCheckIn_Date = finalCheckIn_date;
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
                    String inputCheckOut_Date = finalCheckOut_date;
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

                    //Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();
                    displaySched.setText("Check-In\n"+checkIn_formattedDate+" - "+finalCheckIn_time+"\nCheck-Out\n"+checkOut_formattedDate+" - "+finalCheckOut_time);

                } catch (ParseException e) {
                    e.printStackTrace();
                    // Handle parsing exception if required
                }

                calendar.hide();
            }

        });

        calendar.show();
    }*/

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.guest_btm_dialog_calendar);

        ImageView close = dialog.findViewById(R.id.btmDialog_close);
        RecyclerView dateContainer = dialog.findViewById(R.id.btmDialog_dateContainer);
        TextView checkInTxt = dialog.findViewById(R.id.btmDialog_checkIn);
        TextView checkOutTxt = dialog.findViewById(R.id.btmDialog_checkOut);
        Button applyDatesBtn = dialog.findViewById(R.id.btmDialog_applyDates);
        close.setOnClickListener(v -> dialog.dismiss());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dateContainer.setLayoutManager(layoutManager);

        // Generate calendar data
        List<String> calendarData = generateCalendarData();

        // Create the adapter and set it for the RecyclerView
        calendarAdapter = new Guest_MonthYearAdapter(this, calendarData, checkInTxt, checkOutTxt, applyDatesBtn);
        dateContainer.setAdapter(calendarAdapter);

        applyDatesBtn.setOnClickListener(v -> {
            finalCheckIn_date = calendarAdapter.getFirstSelectedDate();
            finalCheckOut_date = calendarAdapter.getSecondSelectedDate();
            finalCheckIn_time = calendarAdapter.getFirstSelectedTime();
            finalCheckOut_time = calendarAdapter.getSecondSelectedTime();

            if ((finalCheckIn_date != null || finalCheckOut_date != null) && (finalCheckIn_time != null || finalCheckOut_time != null)) {
                Log.d("Date", "Check-in: " + finalCheckIn_date + "\nCheck-out: " + finalCheckOut_date);
                Log.d("Time", "Check-in: " + finalCheckIn_time + "\nCheck-out: " + finalCheckOut_time);

                getDateDifference(finalCheckIn_date, finalCheckOut_date, finalCheckIn_time, finalCheckOut_time);
                displayAvailableRooms(finalCheckIn_date, finalCheckIn_time, finalCheckOut_date, finalCheckOut_time);
                displayAvailableCottage(finalCheckIn_date, finalCheckIn_time, finalCheckOut_date, finalCheckOut_time);

                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy");

                    //check-in date
                    String inputCheckIn_Date = finalCheckIn_date;
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
                    String inputCheckOut_Date = finalCheckOut_date;
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

                    //Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();
                    displaySched.setText("Check-In\n"+checkIn_formattedDate+" - "+finalCheckIn_time+"\nCheck-Out\n"+checkOut_formattedDate+" - "+finalCheckOut_time);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private List<String> generateCalendarData() {
        List<String> data = new ArrayList<>();

        // Get the current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Generate data for 12 months from now
        for (int i = 0; i < 12; i++) {
            int month = (currentMonth + i) % 12;
            int year = currentYear + (currentMonth + i) / 12;

            if (month == 0) {
                // Increment year if the month is January
                currentYear++;
                year++;
            }

            // Add month and year to the data list
            String monthYear = getMonthName(month) + " " + year;
            data.add(monthYear);
        }

        return data;
    }

    private String getMonthName(int month) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        return months[month];
    }

    private void getDateDifference(String firstSelectedDate, String secondSelectedDate, String firstSelectedTime, String secondSelectedTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date dateCheckIn = sdf.parse(firstSelectedDate);
            Date dateCheckOut = sdf.parse(secondSelectedDate);

            long differenceInMillis = Math.abs(dateCheckOut.getTime() - dateCheckIn.getTime());
            dayDiff = (int) (differenceInMillis / (24 * 60 * 60 * 1000));
            nightDiff = (int) (differenceInMillis / (24 * 60 * 60 * 1000));

            if (firstSelectedTime != null && secondSelectedTime != null) {
                if (firstSelectedTime.equals("dayTour") && secondSelectedTime.equals("dayTour")) {
                    dayDiff += 1;
                } else if (firstSelectedTime.equals("nightTour") && secondSelectedTime.equals("nightTour")) {
                    nightDiff += 1;
                } else if (firstSelectedTime.equals("dayTour") && secondSelectedTime.equals("nightTour")) {
                    dayDiff += 1;
                    nightDiff += 1;
                }
            }

            //Toast.makeText(getContext(), String.valueOf(dayDiff), Toast.LENGTH_LONG).show();
        } catch (ParseException e) {
            Log.e("getDateDifference", e.toString());
        }
    }

    @SuppressLint("SetTextI18n")
    private void pickQty() {
        Dialog qty = new Dialog(this);
        qty.setContentView(R.layout.popup_pick_adult_childrent_qty);
        qty.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = qty.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView incAdultCount = qty.findViewById(R.id.popup_guestQty_incAdult);
        ImageView decAdultCount = qty.findViewById(R.id.popup_guestQty_decAdult);
        TextView adultCount = qty.findViewById(R.id.popup_guestQty_adultCount);
        ImageView incKidCount = qty.findViewById(R.id.popup_guestQty_incKid);
        ImageView decKidCount = qty.findViewById(R.id.popup_guestQty_decKid);
        TextView kidCount = qty.findViewById(R.id.popup_guestQty_kidCount);
        Button done = qty.findViewById(R.id.popup_guestQty_doneBtn);

        final int[] adultQty = {0};
        final int[] kidQty = {0};

        incAdultCount.setOnClickListener(v -> {
            adultQty[0]++;
            adultCount.setText(""+ Arrays.toString(adultQty).replace("[", "").replace("]", "").trim());
        });
        decAdultCount.setOnClickListener(v -> {
            if (adultQty[0] != 0){
                adultQty[0]--;
            }
            adultCount.setText(""+ Arrays.toString(adultQty).replace("[", "").replace("]", "").trim());
        });

        incKidCount.setOnClickListener(v -> {
            kidQty[0]++;
            kidCount.setText(""+ Arrays.toString(kidQty).replace("[", "").replace("]", "").trim());
        });
        decKidCount.setOnClickListener(v -> {
            if (kidQty[0] != 0){
                kidQty[0]--;
            }
            kidCount.setText(""+ Arrays.toString(kidQty).replace("[", "").replace("]", "").trim());
        });

        done.setOnClickListener(v -> {
            finalAdultQty = adultQty[0];
            finalKidQty = kidQty[0];

            displayQty.setText(finalAdultQty+" Adult/s\n"+finalKidQty+" Kid/s");

            qty.hide();
        });

        qty.show();
    }

    private void displayRooms() {
        showBox = false;
        roomHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_getRoomDetails.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    RoomCottageDetails_Model model = new RoomCottageDetails_Model(
                            object.getString("id"),
                            object.getString("imageUrl"),
                            object.getString("roomName"),
                            object.getString("roomCapacity"),
                            object.getString("roomRate"),
                            object.getString("roomDescription"));

                    roomHolder.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Room_Adapter adapter = new Room_Adapter(this,roomHolder, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            roomList.setLayoutManager(layoutManager);
            roomList.setAdapter(adapter);

        }, error -> {
            Log.e("displayRooms",  error.getMessage());
        });
        requestQueue.add(stringRequest);

    }

    private void displayCottages() {
        showBox = false;
        cottageHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_getCottageDetails.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    RoomCottageDetails_Model model = new RoomCottageDetails_Model(
                            object.getString("id"),
                            object.getString("imageUrl"),
                            object.getString("cottageName"),
                            object.getString("cottageCapacity"),
                            object.getString("cottageRate"),
                            object.getString("cottageDescription"));
                    cottageHolder.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Cottage_Adapter adapter = new Cottage_Adapter(this,cottageHolder, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            cottageList.setLayoutManager(layoutManager);
            cottageList.setAdapter(adapter);

        }, error -> {
            Log.e("displayCottages", error.getMessage());
        });
        requestQueue.add(stringRequest);
    }

    private void displayAvailableRooms(String finalCheckIn_date, String finalCheckIn_time, String finalCheckOut_date, String finalCheckOut_time) {
        showBox = true;

        roomHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_getAvailableRoom.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    RoomCottageDetails_Model model = new RoomCottageDetails_Model(
                            object.getString("id"),
                            object.getString("imageUrl"),
                            object.getString("roomName"),
                            object.getString("roomCapacity"),
                            object.getString("roomRate"),
                            object.getString("roomDescription"));

                    roomHolder.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            roomList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            Room_Adapter adapter = new Room_Adapter(this, roomHolder, true);
            roomList.setAdapter(adapter);

        },
                Throwable::printStackTrace)
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("checkIn_date", finalCheckIn_date);
                map.put("checkIn_time", finalCheckIn_time);
                map.put("checkOut_date", finalCheckOut_date);
                map.put("checkOut_time", finalCheckOut_time);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void displayAvailableCottage(String finalCheckIn_date, String finalCheckIn_time, String finalCheckOut_date, String finalCheckOut_time){
        showBox = true;

        cottageHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_getAvailableCottage.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    RoomCottageDetails_Model model = new RoomCottageDetails_Model(
                            object.getString("id"),
                            object.getString("imageUrl"),
                            object.getString("cottageName"),
                            object.getString("cottageCapacity"),
                            object.getString("cottageRate"),
                            object.getString("cottageDescription"));

                    cottageHolder.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Room_Adapter adapter = new Room_Adapter(this,cottageHolder, true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            cottageList.setLayoutManager(layoutManager);
            cottageList.setAdapter(adapter);
        },
                error -> Log.e("displayAvailableCottage", error.getMessage()))
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("checkIn_date", finalCheckIn_date);
                map.put("checkIn_time", finalCheckIn_time);
                map.put("checkOut_date", finalCheckOut_date);
                map.put("checkOut_time", finalCheckOut_time);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

}