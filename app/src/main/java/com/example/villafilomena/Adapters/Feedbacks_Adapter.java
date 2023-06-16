package com.example.villafilomena.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Guest.Guest_fragmentsContainer;
import com.example.villafilomena.Models.Comments_Model;
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
    boolean isLiked;
    String myEmail;
    ArrayList<Comments_Model> commentsHolder;
    private Context context;
    private List<Feedbacks_Model> feedbacksList;
    private String ipAddress;

    public Feedbacks_Adapter(Context context, List<Feedbacks_Model> feedbacksList, boolean fromGuest) {
        this.context = context;
        this.feedbacksList = feedbacksList;
        this.fromGuest = fromGuest;

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

        getGuestInfo(holder.name, model.getGuest_email());
        holder.ratingBar.setRating(Float.parseFloat(model.getRatings()));
        holder.date.setText(model.getDate());
        holder.feedbacks.setText(model.getFeedback());

        String imageUrl = model.getImage_urls();

        String[] separatedUrl = imageUrl.split(",");

        ArrayList<Image_Model> separatedUrlList = new ArrayList<>();

        for (String element : separatedUrl) {
            Image_Model model1 = new Image_Model("", element);
            separatedUrlList.add(model1);
        }

        //guestEmail = model.getGuest_email();
        myEmail = Guest_fragmentsContainer.email;
        //feedbackId = model.getId();
       /* likeIcon = holder.likeIcon;
        likeCount = holder.likeCount;*/

        getFeedbackLikes(model.getId(), model.getGuest_email(), holder.likeIcon, holder.likeCount);

        holder.likeIcon.setOnClickListener(v -> {
            if (isLiked) {
                Log.d("Like", "Unliked");
                holder.likeIcon.setColorFilter(Color.BLACK); // Set to black or any other color
                deleteLike(model.getId(), model.getGuest_email(), holder.likeIcon, holder.likeCount);
                isLiked = false;

            } else {
                Log.d("Like", "Liked");
                holder.likeIcon.setColorFilter(Color.BLUE);
                insertLike(model.getId(), model.getGuest_email(), holder.likeIcon, holder.likeCount);
                isLiked = true;
            }
        });

        holder.comment.setOnClickListener(v -> {
            showCommentDialog(model.getId(), model.getGuest_email(), model.getRatings(), model.getDate(), model.getFeedback(), model.getImage_urls());
        });
    }

    private void showCommentDialog(String feedbackId, String guestEmail, String ratings, String date, String feedback, String image_urls) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_feedbacks_comment);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView guestName = dialog.findViewById(R.id.feedbackList_name);
        TextView dateTxt = dialog.findViewById(R.id.feedbackList_date);
        RatingBar ratingBar = dialog.findViewById(R.id.feedbackList_rateBar);
        TextView feedbacks = dialog.findViewById(R.id.feedbackList_feedback);
        RecyclerView imageContainer = dialog.findViewById(R.id.feedbackList_imageContainer);
        ImageView likeIcon = dialog.findViewById(R.id.feedbackList_likeIcon);
        TextView likeCount = dialog.findViewById(R.id.feedbackList_likeCount);
        TextView commentCount = dialog.findViewById(R.id.feedbackList_commentCount);
        RecyclerView commentContainer = dialog.findViewById(R.id.feedbackList_commentContainer);
        EditText giveComment = dialog.findViewById(R.id.feedbackList_giveComment);
        ImageView sendComment = dialog.findViewById(R.id.feedbackList_sendComment);

        getGuestInfo(guestName, guestEmail);
        dateTxt.setText(date);
        ratingBar.setRating(Float.parseFloat(ratings));
        feedbacks.setText(feedback);

        getFeedbackLikes(feedbackId, guestEmail, likeIcon, likeCount);
        getFeedbackComments(feedbackId, commentCount);

        commentContainer.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        Comments_Adapter adapter = new Comments_Adapter(context, commentsHolder);
        commentContainer.setAdapter(adapter);

        sendComment.setOnClickListener(v -> {
            if (giveComment.getText().length() != 0){
                insertComment(feedbackId, guestEmail, giveComment, commentCount);
            }
        });

        dialog.show();
    }

    private void getGuestInfo(TextView name, String guestEmail){
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
                Throwable::printStackTrace
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", guestEmail);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }
    private void getFeedbackLikes(String feedbackId, String guestEmail, ImageView likeIcon, TextView likeCount)   {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getFeedbackLikes.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        int count = 0;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            count++;

                            if (jsonObject.getString("like_by").equals(myEmail)) {
                                isLiked = true;
                                likeIcon.setColorFilter(Color.BLUE);
                            } else {
                                isLiked = false;
                                likeIcon.setColorFilter(Color.BLACK);
                            }
                        }

                        if (count == 0 || count == 1) {
                            likeCount.setText("" + count + " Like");
                        } else {
                            likeCount.setText("" + count + " Likes");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                // Set the POST parameters
                Map<String, String> params = new HashMap<>();
                params.put("feedbackId", feedbackId);
                return params;
            }
        };
        // Add the request to the Volley request queue
        Volley.newRequestQueue(context).add(request);
    }
    private void insertLike(String feedbackId, String guestEmail, ImageView likeIcon, TextView likeCount) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/insert/guest_insertLikes.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Log.d("Like", "Like inserted");
                getFeedbackLikes(feedbackId, guestEmail, likeIcon, likeCount);
            } else if (response.equals("failed")) {
                Log.d("Like", "Like failed");
            }
        },
                Throwable::printStackTrace) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("feedbackId", feedbackId);
                map.put("like_to", guestEmail);
                map.put("like_by", myEmail);

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void deleteLike(String feedbackId, String guestEmail, ImageView likeIcon, TextView likeCount) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/delete/guest_deleteLikes.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Log.d("Like", "Like delete");
                getFeedbackLikes(feedbackId, guestEmail, likeIcon, likeCount);
            } else if (response.equals("failed")) {
                Log.d("Like", "Like delete");
            }
        },
                Throwable::printStackTrace
        ) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("feedbackId", feedbackId);

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void getFeedbackComments(String feedbackId, TextView commentCount) {
        commentsHolder = new ArrayList<>();
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getFeedbackComments.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        int count=0;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            count++;

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Comments_Model model = new Comments_Model(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("comment_to"),
                                    jsonObject.getString("comment_by"),
                                    jsonObject.getString("comment")
                            );
                            commentsHolder.add(model);
                        }

                        if (count == 0 || count == 1) {
                            commentCount.setText("" + count + " Comment");
                        } else {
                            commentCount.setText("" + count + " Comments");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                // Set the POST parameters
                Map<String, String> params = new HashMap<>();
                params.put("feedbackId", feedbackId);
                return params;
            }
        };
        // Add the request to the Volley request queue
        Volley.newRequestQueue(context).add(request);
    }
    private void insertComment(String feedbackId, String guestEmail, EditText giveComment, TextView commentCount) {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/insert/guest_insertLikes.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")) {
                Log.d("Like", "Comment inserted");
                getFeedbackComments(feedbackId, commentCount);
            } else if (response.equals("failed")) {
                Log.d("Like", "Comment failed");
            }
        },
                Throwable::printStackTrace) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("feedbackId", feedbackId);
                map.put("comment_to", guestEmail);
                map.put("comment_by", myEmail);
                map.put("comment", giveComment.getText().toString());

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return feedbacksList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstLetter, name, date, feedbacks;
        AppCompatRatingBar ratingBar;
        RecyclerView imageContainer;
        LinearLayout comment;
        ImageView likeIcon;
        TextView likeCount, commentCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstLetter = itemView.findViewById(R.id.feedbackList_firstLetter);
            name = itemView.findViewById(R.id.feedbackList_name);
            date = itemView.findViewById(R.id.feedbackList_date);
            feedbacks = itemView.findViewById(R.id.feedbackList_feedback);
            ratingBar = itemView.findViewById(R.id.feedbackList_rateBar);
            imageContainer = itemView.findViewById(R.id.feedbackList_imageContainer);
            comment = itemView.findViewById(R.id.feedbackList_comment);
            likeIcon = itemView.findViewById(R.id.feedbackList_likeIcon);
            likeCount = itemView.findViewById(R.id.feedbackList_likeCount);
            commentCount = itemView.findViewById(R.id.feedbackList_commentCount);

        }
    }
}
