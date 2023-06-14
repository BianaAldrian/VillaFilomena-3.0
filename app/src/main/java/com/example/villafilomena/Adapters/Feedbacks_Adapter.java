package com.example.villafilomena.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Models.Feedbacks_Model;
import com.example.villafilomena.Models.Image_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feedbacks_Adapter extends RecyclerView.Adapter<Feedbacks_Adapter.ViewHolder> {
    boolean fromGuest = false;
    private Context context;
    private List<Feedbacks_Model> feedbacksList;
    private String ipAddress;

    public Feedbacks_Adapter(Context context, List<Feedbacks_Model> feedbacksList, boolean fromGuest) {
        this.context = context;
        this.feedbacksList = feedbacksList;
        this.fromGuest =fromGuest;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedbacks_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feedbacks_Model model = feedbacksList.get(position);

        getGuestInfo(model.getGuest_email(), holder.name);
        holder.ratingBar.setRating(Float.parseFloat(model.getRatings()));
        holder.date.setText(model.getDate());
        holder.feedbacks.setText(model.getFeedback());

        if (fromGuest){
            holder.comment.setVisibility(View.GONE);
            holder.commentSection.setVisibility(View.GONE);
        } else {
            holder.comment.setVisibility(View.VISIBLE);
            holder.commentSection.setVisibility(View.VISIBLE);
        }

        String imageUrl = model.getImage_urls();

        String[] separatedUrl = imageUrl.split(",");

        ArrayList<Image_Model> separatedUrlList = new ArrayList<>();

        for (String element : separatedUrl) {
            Image_Model model1 = new Image_Model("", element);
            separatedUrlList.add(model1);
        }

        if (!imageUrl.equals("")){
            holder.imageContainer.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            Feedback_Image_Adapter adapter = new Feedback_Image_Adapter(context, separatedUrlList);
            holder.imageContainer.setAdapter(adapter);
            holder.imageContainer.setHasFixedSize(true);
        }
    }

    @Override
    public int getItemCount() {
        return feedbacksList.size();
    }

    private void getGuestInfo(String email, TextView name) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getGuestInfo.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            name.setText(jsonObject.getString("fullname"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstLetter, name, date, feedbacks;
        AppCompatRatingBar ratingBar;
        RecyclerView imageContainer;
        LinearLayout comment, commentSection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstLetter = itemView.findViewById(R.id.feedbackList_firstLetter);
            name = itemView.findViewById(R.id.feedbackList_name);
            date = itemView.findViewById(R.id.feedbackList_date);
            feedbacks = itemView.findViewById(R.id.feedbackList_feedback);
            ratingBar = itemView.findViewById(R.id.feedbackList_rateBar);
            imageContainer = itemView.findViewById(R.id.feedbackList_imageContainer);
            comment = itemView.findViewById(R.id.feedbackList_comment);
            commentSection = itemView.findViewById(R.id.feedbackList_commentSection);
        }
    }
}
