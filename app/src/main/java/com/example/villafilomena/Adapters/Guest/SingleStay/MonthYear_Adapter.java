package com.example.villafilomena.Adapters.Guest.SingleStay;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class MonthYear_Adapter extends RecyclerView.Adapter<MonthYear_Adapter.ViewHolder> {
    Context context;
    TextView checkInTxt, checkOutTxt;
    Button applyDatesBtn;
    Date_Adapter.ClickListener clickListener;
    private List<String> calendarData;
    private int clickCounter = 0;
    private List<View> firstSelectedHolder;
    private List<View> secondSelectedHolder;
    private String SelectedDate;
    private String secondSelectedDate;
    private String Str_duration;
    private String secondSelectedTime = "";


    public MonthYear_Adapter(Context context, List<String> calendarData, TextView checkInTxt, TextView checkOutTxt, String Str_duration, Button applyDatesBtn) {
        this.context = context;
        this.calendarData = calendarData;
        this.checkInTxt = checkInTxt;
        this.checkOutTxt = checkOutTxt;
        this.Str_duration = Str_duration;
        this.applyDatesBtn = applyDatesBtn;

        firstSelectedHolder = new ArrayList<>();
        secondSelectedHolder = new ArrayList<>();
    }

    @NonNull
    @Override
    public MonthYear_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guest_booking_calendar_date_list, parent, false);

        OnItemClick();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthYear_Adapter.ViewHolder holder, int position) {
        String monthYear = calendarData.get(position);
        holder.dateTxt.setText(monthYear);

        int month = parseMonth(monthYear);
        int year = parseYear(monthYear);

        holder.daysContainer.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 7));
        List<String> datesList = generateDatesForMonth(month, year);
        Date_Adapter dayAdapter = new Date_Adapter(holder.itemView.getContext(), month, year, datesList, clickListener);
        holder.daysContainer.setAdapter(dayAdapter);

    }

    private void OnItemClick() {
        clickListener = (v, position, dayTxt, month, year) -> {
            Log.d("Date", dayTxt.getText().toString() + "/" + month+1 + "/" + year);

            int displayedDate = Integer.parseInt(dayTxt.getText().toString());
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", displayedDate, month + 1, year);

            /*if (clickCounter == 0) {
                clickCounter++;
                dayTxt.setBackgroundResource(R.color.teal_700);
                firstSelectedDate = selectedDate;
                firstSelectedHolder.add(dayTxt);

            } else {
                for (View selectedItem : firstSelectedHolder) {
                    selectedItem.setBackgroundColor(Color.TRANSPARENT);
                }
            }*/

            if (firstSelectedHolder != null) {
                for (View selectedItem : firstSelectedHolder) {
                    selectedItem.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            dayTxt.setBackgroundResource(R.color.teal_700);
            SelectedDate = selectedDate;
            firstSelectedHolder.add(dayTxt);

            if (Str_duration == "singleDay"){
                checkInTxt.setText(selectedDate + " - 7:00 AM");
                checkOutTxt.setText(selectedDate + " - 5:00 PM");

            }else {
                checkInTxt.setText(selectedDate + " - 7:00 PM");
                checkOutTxt.setText(selectedDate + " - 5:00 AM");
            }
            applyDatesBtn.setBackgroundResource(R.drawable.btn_bg);
            applyDatesBtn.setClickable(true);
        };
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

    public String getSelectedDate() {
        return SelectedDate;
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
