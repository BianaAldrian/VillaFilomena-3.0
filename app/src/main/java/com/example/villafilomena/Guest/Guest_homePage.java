package com.example.villafilomena.Guest;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Image_Adapter;
import com.example.villafilomena.Adapters.Video_Adapter;
import com.example.villafilomena.Models.Image_Model;
import com.example.villafilomena.Models.Video_Model;
import com.example.villafilomena.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Guest_homePage extends Fragment {
    String ipAddress;
    RecyclerView imageContainer, videoContainer;
    ArrayList<Image_Model> imageHolder;
    ArrayList<Video_Model> videoHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest_home_page, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        imageContainer = view.findViewById(R.id.guest_homepage_imageContainer);
        videoContainer = view.findViewById(R.id.guest_homepage_videoContainer);

        displayImages();
        displayVideos();
        return view;
    }

    private void displayImages() {
        imageHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_getImages.php";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Image_Model model = new Image_Model(
                            object.getString("id"),
                            object.getString("image_url"));
                    imageHolder.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            imageContainer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            Image_Adapter adapter = new Image_Adapter(getContext(), imageHolder, false);
            imageContainer.setAdapter(adapter);
            imageContainer.setHasFixedSize(true);

        }, Throwable::printStackTrace);
        requestQueue.add(stringRequest);
    }

    private void displayVideos(){
        videoHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/guest_dir/retrieve/guest_getVideos.php";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Video_Model model = new Video_Model(
                            object.getString("id"),
                            object.getString("video_url"));

                    videoHolder.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            videoContainer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            Video_Adapter adapter = new Video_Adapter(getContext(), videoHolder);
            videoContainer.setAdapter(adapter);
            videoContainer.setHasFixedSize(true);

        }, Throwable::printStackTrace);
        requestQueue.add(stringRequest);
    }
}