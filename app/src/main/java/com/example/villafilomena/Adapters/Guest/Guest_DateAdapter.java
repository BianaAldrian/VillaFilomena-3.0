package com.example.villafilomena.Adapters.Guest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.villafilomena.R;

import java.util.Calendar;
import java.util.List;

public class Guest_DateAdapter extends RecyclerView.Adapter<Guest_DateAdapter.ViewHolder> {
    ClickListener clickListener;
    private Context context;
    private List<String> dateList;
    private int month;
    private int year;

    public Guest_DateAdapter(Context context, int month, int year, List<String> dateList, ClickListener clickListener) {
        this.context = context;
        this.month = month;
        this.year = year;
        this.dateList = dateList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guest_calendar_day_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = dateList.get(position);
        if (!date.isEmpty()) {
            holder.day.setText(date);

            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Get the current month and year
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);

            // Check if the displayed date is in the past, the current date, or the next day
            int displayedDate = Integer.parseInt(date);

            if (currentYear > year || (currentYear == year && currentMonth > month) || (currentYear == year && currentMonth == month && displayedDate < currentDay)) {
                // Date is in the past, set the text color to grey
                holder.day.setTextColor(context.getResources().getColor(R.color.grey));
                holder.itemView.setClickable(false); // Disable click for past dates
            } else if (currentYear == year && currentMonth == month && (displayedDate == currentDay || displayedDate == currentDay + 1)) {
                // Date is the current date or the next day, set the text color to grey
                holder.day.setTextColor(context.getResources().getColor(R.color.grey));
                holder.itemView.setClickable(false); // Disable click for past dates
            }

            // Check if the current date and month match the displayed date and month
            if (currentDay == displayedDate && currentMonth == month) {
                // Highlight the current date
                holder.dayIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.dayIndicator.setVisibility(View.GONE);
            }

            String strCurrentMonth = String.valueOf(month+1);
            String strCurrentYear = String.valueOf(year);

            String disableDate = date +"/"+strCurrentMonth+"/"+strCurrentYear;

            if (disableDate.equals("7/7/2023")){
                holder.day.setTextColor(context.getResources().getColor(R.color.grey));
            }

           /* holder.day.setOnClickListener(v -> {
                Toast.makeText(context, date +"/"+strCurrentMonth+"/"+strCurrentYear, Toast.LENGTH_SHORT).show();
            });*/

        } else {
            holder.day.setText("");
            holder.dayIndicator.setVisibility(View.GONE);
            holder.itemView.setClickable(false); // Disable click for empty dates
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public interface ClickListener {
        void onClick(View v, int position, TextView day, int month, int year);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView day, dayIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            int screenWidth = itemView.getResources().getDisplayMetrics().widthPixels;
            int textViewWidth = screenWidth / 7;

            day = itemView.findViewById(R.id.guest_calendar_dayTxt);
            day.getLayoutParams().width = textViewWidth;

            dayIndicator = itemView.findViewById(R.id.guest_calendar_dayIncator);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onClick(view, position, day, month, year);
                }
            }
        }
    }
}
