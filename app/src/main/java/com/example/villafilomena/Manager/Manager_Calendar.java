package com.example.villafilomena.Manager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.CalendarAdapter;
import com.example.villafilomena.Adapters.Manager.Manager_DateAdapter;
import com.example.villafilomena.Models.Schedule_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Manager_Calendar extends AppCompatActivity {
    String ipAddress;
    RecyclerView dateContainer;
    ImageView exit, menu;
    int schedCurrentMonth;
    int schedCurrentYear;
    Manager_DateAdapter manager_adapter;
    ArrayList<Schedule_Model> schedDatesList;
    private CalendarAdapter adapter;
    private int currentMonth;
    private int currentYear;

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_calendar);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        dateContainer = findViewById(R.id.manager_calendar_dateContainer);
        exit = findViewById(R.id.manager_calendar_exit);
        menu = findViewById(R.id.manager_calendar_menu);

        exit.setOnClickListener(v -> {
            startActivity(new Intent(this, Manager_Dashboard.class));
            finish();
        });

        menu.setOnClickListener(v -> menu());

        Spinner monthSpinner = findViewById(R.id.manager_monthSpinner);
        Spinner yearSpinner = findViewById(R.id.manager_yearSpinner);

        String[] monthOptions = new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        // Replace the yearOptions array with the appropriate range of years
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = currentYear - 10;
        int endYear = currentYear + 10;
        String[] yearOptions = generateYearOptions(startYear, endYear);

        ArrayAdapter<String> monthSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout_list, monthOptions);
        ArrayAdapter<String> yearSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout_list, yearOptions);

        monthSpinner.setAdapter(monthSpinnerAdapter);
        yearSpinner.setAdapter(yearSpinnerAdapter);

        // Set up the RecyclerView layout manager and adapter
        dateContainer.setLayoutManager(new GridLayoutManager(this, 7)); // Assuming 7 columns for each week
        List<Date> datesList = generateDatesForMonth(currentMonth, currentYear); // Generate your list of dates for the initial month and year
        adapter = new CalendarAdapter(Manager_Calendar.this, datesList, currentMonth);
        dateContainer.setAdapter(adapter);
        dateContainer.setHasFixedSize(true);

        // Set the current month and year as the selected items in the Spinners
        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);
        monthSpinner.setSelection(currentMonth);
        int currentYearIndex = currentYear - startYear;
        yearSpinner.setSelection(currentYearIndex);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMonth(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no month is selected
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectYear(Integer.parseInt(yearOptions[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no year is selected
            }
        });

    }

    private String[] generateYearOptions(int startYear, int endYear) {
        int totalYears = endYear - startYear + 1;
        String[] yearOptions = new String[totalYears];
        for (int i = 0; i < totalYears; i++) {
            yearOptions[i] = String.valueOf(startYear + i);
        }
        return yearOptions;
    }

    private List<Date> generateDatesForMonth(int month, int year) {
        List<Date> datesList = new ArrayList<>();

        // Create a Calendar instance and set the month and year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        // Set the day of the month to 1 to get the starting date
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Get the starting day of the week for the month
        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Get the maximum number of days in the month
        int maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Calculate the number of cells needed in the grid
        int totalCells = startDayOfWeek - 1 + maxDaysInMonth;

        // Add empty cells for the days before the starting day
        for (int i = 0; i < startDayOfWeek - 1; i++) {
            datesList.add(null);
        }

        // Add the dates for the month
        for (int i = 1; i <= maxDaysInMonth; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            Date date = calendar.getTime();
            datesList.add(date);
        }

        // Add empty cells for the remaining days
        int remainingCells = 7 - (totalCells % 7);
        if (remainingCells < 7) {
            for (int i = 0; i < remainingCells; i++) {
                datesList.add(null);
            }
        }

        return datesList;
    }

    private void selectYear(int selectedYear) {
        currentYear = selectedYear;
        List<Date> newDatesList = generateDatesForMonth(currentMonth, currentYear);
        adapter.setCurrentMonth(newDatesList, currentMonth);
    }

    private void selectMonth(int selectedMonth) {
        currentMonth = selectedMonth;
        List<Date> newDatesList = generateDatesForMonth(currentMonth, currentYear);
        adapter.setCurrentMonth(newDatesList, currentMonth);
    }

    private void schedules() {
        // Your code to retrieve the dates from the server or any other data source
        schedDatesList = new ArrayList<>();
        // Add retrieved dates to the list
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/retrieve/manager_getSched.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Schedule_Model model = new Schedule_Model(jsonObject.getString("id"), jsonObject.getString("disable_date"), jsonObject.getString("reason"));
                            schedDatesList.add(model);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<String> highlightedDates = new ArrayList<>();
                    for (Schedule_Model model : schedDatesList) {
                        // Assuming the date is stored in the "disable_date" field of the Schedule_Model object
                        highlightedDates.add(model.getDisable_date());
                    }
                    manager_adapter.setHighlightedDates(highlightedDates);

                }, Throwable::printStackTrace);

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);

    }

    private void menu() {
        PopupMenu popupMenu = new PopupMenu(this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.manager_dropdown_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            // Handle menu item selection
            // Handle menu item 1 click

            switch (item.getItemId()) {
                case R.id.menu_item1:
                    schedules();
                    // Handle menu item 1 click
                    Dialog calendar = new Dialog(this);
                    calendar.setContentView(R.layout.manager_calendar_scheduler_dialog);
                    calendar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    Window windowCalendar = calendar.getWindow();
                    windowCalendar.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    ImageView prev = calendar.findViewById(R.id.manager_calendarSchedule_prev);
                    ImageView next = calendar.findViewById(R.id.manager_calendarSchedule_next);
                    TextView date = calendar.findViewById(R.id.manager_calendarSchedule_date);
                    RecyclerView daysContainer = calendar.findViewById(R.id.manager_calendarSchedule_daysContainer);
                    Button disable = calendar.findViewById(R.id.manager_calendarSchedule_disable);

                    Calendar schedCalendar = Calendar.getInstance();
                    schedCurrentMonth = schedCalendar.get(Calendar.MONTH);
                    schedCurrentYear = schedCalendar.get(Calendar.YEAR);

                    prev.setOnClickListener(v -> updateCalendar(-1, date));

                    next.setOnClickListener(v -> updateCalendar(1, date));

                    daysContainer.setLayoutManager(new GridLayoutManager(this, 7)); // Assuming 7 columns for each week
                    List<Date> datesList = generateDatesForMonth(schedCurrentMonth, schedCurrentYear); // Generate your list of dates for the initial month and year
                    manager_adapter = new Manager_DateAdapter(datesList, schedCurrentMonth, schedCurrentYear);
                    daysContainer.setAdapter(manager_adapter);

                    disable.setOnClickListener(v -> {
                        List<String> dateHolder = manager_adapter.getDateHolder();
                        if (dateHolder.isEmpty()) {
                            Log.d("Selected Date", "dateHolder is empty");
                        } else {
                            String dateHolderString = TextUtils.join(", ", dateHolder);
                            Log.d("Selected Date", dateHolderString);

                            Dialog reason = new Dialog(this);
                            reason.setContentView(R.layout.dialog_manager_reject_reason);

                            EditText reasonTxt = reason.findViewById(R.id.manager_reject_reason);
                            Button confirm = reason.findViewById(R.id.manager_reject_confirm);

                            confirm.setOnClickListener(v1 -> {
                                String reasonText = reasonTxt.getText().toString().trim();
                                if (TextUtils.isEmpty(reasonText)) {
                                    // EditText is empty
                                    Toast.makeText(this, "Reason is empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (String str : dateHolder){
                                        uploadDisableDates(str, reasonText);
                                    }
                                    calendar.dismiss();
                                    reason.dismiss();
                                }
                            });

                            reason.show();
                        }
                    });

                    calendar.show();

                    return true;

                case R.id.menu_item2:
                    // Handle menu item 2 click
                    Dialog capacity = new Dialog(this);
                    capacity.setContentView(R.layout.manager_calendar_capacity_dialog);
                    capacity.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    Window windowCapacity = capacity.getWindow();
                    windowCapacity.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    TextView info = capacity.findViewById(R.id.manager_calendarCapacity_info);
                    EditText setInfo = capacity.findViewById(R.id.manager_calendarCapacity_setInfo);
                    Button edit = capacity.findViewById(R.id.manager_calendarCapacity_edit);
                    Button save = capacity.findViewById(R.id.manager_calendarCapacity_save);

                    setInfo.setVisibility(View.GONE);
                    save.setVisibility(View.GONE);

                    edit.setOnClickListener(v1 -> {
                        save.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.GONE);
                        setInfo.setVisibility(View.VISIBLE);
                    });

                    save.setOnClickListener(v1 -> {
                        if (setInfo.getText().length() == 0){
                            Toast.makeText(this, "Set Capacity", Toast.LENGTH_SHORT).show();
                        } else {
                            info.setText("Maximum of " +setInfo.getText().toString()+ " Guests");
                            save.setVisibility(View.GONE);
                            edit.setVisibility(View.VISIBLE);
                            setInfo.setVisibility(View.GONE);
                        }
                    });

                    capacity.show();
                    return true;

                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    public void uploadDisableDates(String dates, String reason) {
        if (!TextUtils.isEmpty(reason)) {
            String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/insert/manager_insertCalendarSched.php";

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                if (response.equals("success")) {
                    Log.d("Upload", "Upload Successful");
                } else if (response.equals("failed")) {
                    Log.d("Upload", "Upload Failed");
                }
            },
                    error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
                @Override
                protected HashMap<String, String> getParams() {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("disable_date", dates);
                    map.put("reason", reason);
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(this, "reason is empty", Toast.LENGTH_LONG).show();
        }
    }

    private void updateCalendar(int offset, TextView date) {
        // Adjust the month and year based on the offset
        schedCurrentMonth += offset;
        if (schedCurrentMonth < 0) {
            schedCurrentMonth = 11;  // December
            schedCurrentYear--;
        } else if (schedCurrentMonth > 11) {
            schedCurrentMonth = 0;  // January
            schedCurrentYear++;
        }

        // Generate new list of dates for the updated month and year
        List<Date> updatedDatesList = generateDatesForMonth(schedCurrentMonth, schedCurrentYear);

        // Update the adapter with the new dates list and current month
        manager_adapter.updateDatesList(updatedDatesList, schedCurrentMonth);

        // Update the displayed date text
        String displayedDate = getDisplayedDate(schedCurrentMonth, schedCurrentYear);
        date.setText(displayedDate);
    }

    private String getDisplayedDate(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

}
