package com.example.villafilomena.Adapters.Guest.StayIn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Guest_MonthYearAdapter extends RecyclerView.Adapter<Guest_MonthYearAdapter.ViewHolder> {
    Context context;
    TextView checkInTxt, checkOutTxt;
    Button applyDatesBtn;
    Guest_DateAdapter.ClickListener clickListener;
    private int clickCounter = 0;
    private List<String> calendarData;
    private List<View> firstSelectedHolder;
    private List<View> secondSelectedHolder;
    private String firstSelectedDate;
    private String secondSelectedDate;
    private String firstSelectedTime = "";
    private String secondSelectedTime = "";

    public Guest_MonthYearAdapter(Context context, List<String> calendarData, TextView checkInTxt, TextView checkOutTxt, Button applyDatesBtn) {
        this.context = context;
        this.calendarData = calendarData;
        this.checkInTxt = checkInTxt;
        this.checkOutTxt = checkOutTxt;
        this.applyDatesBtn = applyDatesBtn;

        firstSelectedHolder = new ArrayList<>();
        secondSelectedHolder = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guest_booking_calendar_date_list, parent, false);
        OnItemClick();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String monthYear = calendarData.get(position);
        holder.dateTxt.setText(monthYear);

        int month = parseMonth(monthYear);
        int year = parseYear(monthYear);

        holder.daysContainer.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 7));
        List<String> datesList = generateDatesForMonth(month, year);
        Guest_DateAdapter dayAdapter = new Guest_DateAdapter(holder.itemView.getContext(), month, year, datesList, clickListener);
        holder.daysContainer.setAdapter(dayAdapter);

    }

    private void OnItemClick() {
        clickListener = (v, position, dayTxt, month, year) -> {
            Log.d("Date", dayTxt.getText().toString() + "/" + month + "/" + year);

            int displayedDate = Integer.parseInt(dayTxt.getText().toString());
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", displayedDate, month + 1, year);

            if (clickCounter == 0) {
                clickCounter++;
                dayTxt.setBackgroundResource(R.color.teal_700);
                firstSelectedDate = selectedDate;
                firstSelectedHolder.add(dayTxt);

                showDialog("Check-in");

            } else if (clickCounter == 1) {
                if (selectedDate != null && compareDates(firstSelectedDate, selectedDate) < 0) {
                    // First selected date is earlier than the second selected date
                    // Handle the logic accordingly
                    Log.d("Context", "First date is earlier than the second date");
                    clickCounter++;
                    dayTxt.setBackgroundResource(R.color.teal_700);
                    secondSelectedDate = selectedDate;
                    secondSelectedHolder.add(dayTxt);

                    showDialog("Check-out");
                } else if (selectedDate != null && compareDates(firstSelectedDate, selectedDate) > 0) {
                    // First selected date is later than the second selected date
                    // Handle the logic accordingly
                    Log.d("Context", "First date is later than the second date");

                    showDialog("Check-in");

                    // Reset the click counter back to 0
                    clickCounter = 0;
                    // Reset the background color for all selected dates
                    for (View selectedItem : firstSelectedHolder) {
                        selectedItem.setBackgroundColor(Color.TRANSPARENT);
                    }
                    for (View selectedItem : secondSelectedHolder) {
                        selectedItem.setBackgroundColor(Color.TRANSPARENT);
                    }

                    // Clear the variables
                    firstSelectedHolder = new ArrayList<>();
                    secondSelectedHolder = new ArrayList<>();
                    firstSelectedDate = null;
                    secondSelectedDate = null;

                    clickCounter++;

                    dayTxt.setBackgroundResource(R.color.teal_700);

                    firstSelectedDate = selectedDate;
                    firstSelectedHolder.add(dayTxt);
                } else {
                    // The selected date is null or the same as the first selected date
                    // Handle the logic accordingly
                    //Log.d("Context", "Selected date is null or the same as the first date");

                    showDialog("Check-out");
                    clickCounter++;
                    secondSelectedDate = selectedDate;
                    secondSelectedHolder.add(dayTxt);
                }
            } else if (clickCounter == 2) {
                if (compareDates(secondSelectedDate, selectedDate) == 0) {
                    // First selected date is earlier than the second selected date
                    // Handle the logic accordingly
                    Log.d("Context", "Selected dates are the same");
                } else {
                    showDialog("Check-in");
                    // Reset the click counter back to 0
                    clickCounter = 0;
                    // Reset the background color for all selected dates
                    for (View selectedItem : firstSelectedHolder) {
                        selectedItem.setBackgroundColor(Color.TRANSPARENT);
                    }
                    for (View selectedItem : secondSelectedHolder) {
                        selectedItem.setBackgroundColor(Color.TRANSPARENT);
                    }

                    // Clear the variables
                    firstSelectedHolder = new ArrayList<>();
                    secondSelectedHolder = new ArrayList<>();
                    firstSelectedDate = null;
                    secondSelectedDate = null;

                    clickCounter++;

                    dayTxt.setBackgroundResource(R.color.teal_700);

                    firstSelectedDate = selectedDate;
                    firstSelectedHolder.add(dayTxt);
                }

            }

            /*if (firstSelectedDate != null && !firstSelectedDate.isEmpty()) {
                checkInTxt.setText(firstSelectedDate);
            }

            if (secondSelectedDate == null || secondSelectedDate.isEmpty()) {
                checkOutTxt.setText("Select Check-out date");
            } else {
                checkOutTxt.setText(secondSelectedDate);
            }

            if (firstSelectedDate == null || secondSelectedDate == null){
                applyDatesBtn.setBackgroundResource(R.color.grey);
                applyDatesBtn.setClickable(false);
            } else {
                applyDatesBtn.setBackgroundResource(R.drawable.btn_bg);
                applyDatesBtn.setClickable(true);
            }*/
        };
    }

    @SuppressLint("SetTextI18n")
    public void showDialog(String text) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_calendar_time_sched);

        TextView check = dialog.findViewById(R.id.btmDialogDialog_Check);
        CheckBox dayTour = dialog.findViewById(R.id.btmDialogDialog_dayTour);
        CheckBox nightTour = dialog.findViewById(R.id.btmDialogDialog_nightTour);
        Button done = dialog.findViewById(R.id.btmDialogDialog_done);

        check.setText(text);

        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (isChecked) {
                if (text.equals("Check-in")) {
                    if (buttonView.getId() == R.id.btmDialogDialog_dayTour) {
                        nightTour.setChecked(false);
                        firstSelectedTime = "dayTour";
                    } else if (buttonView.getId() == R.id.btmDialogDialog_nightTour) {
                        dayTour.setChecked(false);
                        firstSelectedTime = "nightTour";
                    }
                } else {
                    if (buttonView.getId() == R.id.btmDialogDialog_dayTour) {
                        nightTour.setChecked(false);
                        secondSelectedTime = "dayTour";
                    } else if (buttonView.getId() == R.id.btmDialogDialog_nightTour) {
                        dayTour.setChecked(false);
                        secondSelectedTime = "nightTour";
                    }
                }
            }
        };

        dayTour.setOnCheckedChangeListener(listener);
        nightTour.setOnCheckedChangeListener(listener);


        done.setOnClickListener(v1 -> {
            if (!dayTour.isChecked() && !nightTour.isChecked()) {
                Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show();
            } else {
                if (text.equals("Check-in")) {
                    checkInTxt.setText(firstSelectedDate + "\n" + firstSelectedTime);
                } else {
                    checkOutTxt.setText(secondSelectedDate + "\n" + secondSelectedTime);
                }

                if (secondSelectedDate == null || secondSelectedDate.isEmpty()) {
                    checkOutTxt.setText("Select Check-out date");
                }
                if (firstSelectedDate == null || secondSelectedDate == null) {
                    applyDatesBtn.setBackgroundResource(R.color.grey);
                    applyDatesBtn.setClickable(false);
                } else {
                    applyDatesBtn.setBackgroundResource(R.drawable.btn_bg);
                    applyDatesBtn.setClickable(true);
                }

                dialog.dismiss();
                // Perform other actions if at least one checkbox is selected
            }
        });
        dialog.show();
    }

    public String getFirstSelectedDate() {
        return firstSelectedDate;
    }

    public String getSecondSelectedDate() {
        return secondSelectedDate;
    }

    public String getFirstSelectedTime() {
        return firstSelectedTime;
    }

    public String getSecondSelectedTime() {
        return secondSelectedTime;
    }

    private int compareDates(String date1, String date2) {
        // Parse the date strings into Date objects for comparison
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);

            // Compare the dates using the compareTo() method of the Date class
            return d1.compareTo(d2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Handle the parsing error according to your requirement
        }
    }

    @Override
    public int getItemCount() {
        return calendarData.size();
    }


    private int parseMonth(String monthYearString) {
        String[] parts = monthYearString.split(" ");
        String monthString = parts[0];
        SimpleDateFormat format = new SimpleDateFormat("MMMM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Objects.requireNonNull(format.parse(monthString)));
            return calendar.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Default to January if parsing fails
        }
    }

    private int parseYear(String monthYearString) {
        String[] parts = monthYearString.split(" ");
        String yearString = parts[1];
        try {
            return Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0; // Default to 0 if parsing fails
        }
    }


    private List<String> generateDatesForMonth(int month, int year) {
        List<String> datesList = new ArrayList<>();

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
            datesList.add("");
        }

        // Add the dates for the month
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
        for (int i = 1; i <= maxDaysInMonth; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            Date date = calendar.getTime();
            String dateString = dateFormat.format(date);
            datesList.add(dateString);
        }

        // Add empty cells for the remaining days
        int remainingCells = 7 - (totalCells % 7);
        if (remainingCells < 7) {
            for (int i = 0; i < remainingCells; i++) {
                datesList.add("");
            }
        }

        return datesList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTxt;
        RecyclerView daysContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTxt = itemView.findViewById(R.id.calendar_dateList_dateTxt);
            daysContainer = itemView.findViewById(R.id.calendar_dateList_daysContainer);
        }
    }
}
