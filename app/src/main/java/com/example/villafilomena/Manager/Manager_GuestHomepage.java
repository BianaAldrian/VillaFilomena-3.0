package com.example.villafilomena.Manager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Image_Adapter;
import com.example.villafilomena.Adapters.Manager.Manager_GuestHomepageViews_Adapter;
import com.example.villafilomena.Models.Image_Model;
import com.example.villafilomena.Models.Manager.Manager_GuestHomepageViews_Model;
import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Manager_GuestHomepage extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_MULTI_IMAGE_REQUEST = 2;
    public static ImageView back, edit, save, image_banner;
    TextView introView;
    TextView editBanner, editIntro, editVideo, editImage;
    Dialog edit_banner, edit_Intro, edit_Image, upload_banner;
    Uri imageUri;
    String ipAddress;
    StorageReference BannerImageReference, ImagesReference;
    String currentBanner, currentIntro;
    ArrayList<Uri> imageUriList = new ArrayList<>();
    RecyclerView imageContainer;
    ArrayList<Image_Model> imageHolder;
    Image_Adapter adapter;
    ArrayList<Image_Model> newImageList;
    RecyclerView addedImageList;
    List<String> downloadUrls = new ArrayList<>();
    int uploadedCount = 0;

    //boolean isUploaded = true;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_guest_homepage);

        //to get the IP address
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        //storing the banner image in firebase storage
        BannerImageReference = FirebaseStorage.getInstance().getReference("BannerImages");
        ImagesReference = FirebaseStorage.getInstance().getReference("Images");

        back = findViewById(R.id.manager_guestHomepage_back);
        edit = findViewById(R.id.guestHomepage_edit);
        save = findViewById(R.id.guestHomepage_save);
        editBanner = findViewById(R.id.guestHomepage_editBanner);
        image_banner = findViewById(R.id.guestHomepage_bannerImage);
        introView = findViewById(R.id.guestHomepage_introduction);
        editIntro = findViewById(R.id.guestHomepage_editIntro);
        editVideo = findViewById(R.id.guestHomepage_editVideo);
        editImage = findViewById(R.id.guestHomepage_editImage);
        imageContainer = findViewById(R.id.manager_guestHomepage_imageContainer);

        //calling the setGuestHomePageView method for setting the view in guest homepage2
        setBanner();
        setIntro();
        displayImages();


        edit.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        editBanner.setVisibility(View.GONE);
        editIntro.setVisibility(View.GONE);
        editVideo.setVisibility(View.GONE);
        editImage.setVisibility(View.GONE);

        back.setOnClickListener(v -> {
            finish();
        });

        //function when the edit icon is tap
        edit.setOnClickListener(v -> {
            edit.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
            editBanner.setVisibility(View.VISIBLE);
            editIntro.setVisibility(View.VISIBLE);
            editVideo.setVisibility(View.VISIBLE);
            editImage.setVisibility(View.VISIBLE);

            adapter.setNewData(true, imageHolder);
        });

        //function when the save icon is tap
        save.setOnClickListener(v -> {
            edit.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            editBanner.setVisibility(View.GONE);
            editIntro.setVisibility(View.GONE);
            editVideo.setVisibility(View.GONE);
            editImage.setVisibility(View.GONE);

            adapter.setNewData(false, imageHolder);
        });

        //function when the edit text in banner is tap
        editBanner.setOnClickListener(v -> {
            /*edit_banner = new Dialog(this);
            edit_banner.setContentView(R.layout.popup_edit_banner_page);
            Window window = edit_banner.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            TextView updateImageTxt = edit_banner.findViewById(R.id.manager_popup_updateImageTxt);
            TextView updateIntroTxt = edit_banner.findViewById(R.id.manager_popup_updateIntroTxt);
            EditText introduction = edit_banner.findViewById(R.id.manager_popupIntroduction);
            Button addImage = edit_banner.findViewById(R.id.manager_popupAddImage);
            Button upload = edit_banner.findViewById(R.id.manager_popupUpload);
            ImageView close = edit_banner.findViewById(R.id.manager_popupClose);
            RecyclerView bannerList = edit_banner.findViewById(R.id.manager_contentViewList);

            updateImageTxt.setVisibility(View.GONE);
            updateIntroTxt.setVisibility(View.GONE);
            introduction.setVisibility(View.GONE);
            addImage.setVisibility(View.GONE);

            bannerList(bannerList);

            upload.setOnClickListener(v1 -> chooseImage());

            close.setOnClickListener(v1 -> edit_banner.hide());

            edit_banner.show();*/

            chooseImage();

        });

        //function when the edit text in introduction is tap
        editIntro.setOnClickListener(v -> {
            edit_Intro = new Dialog(this);
            edit_Intro.setContentView(R.layout.popup_edit_banner_page);
            Window window = edit_Intro.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            Button upload = edit_Intro.findViewById(R.id.manager_popupUpload);
            ImageView close = edit_Intro.findViewById(R.id.manager_popupClose);
            EditText introduction = edit_Intro.findViewById(R.id.manager_popupIntroduction);
            RecyclerView introList = edit_Intro.findViewById(R.id.manager_contentViewList);

            introList(introList);

            upload.setOnClickListener(v1 -> uploadIntroduction(introduction));

            close.setOnClickListener(v1 -> edit_Intro.hide());

            edit_Intro.show();
        });

        //function when the edit text in videos is tap
        editVideo.setOnClickListener(v -> {
        });

        //function when the edit text in images is tap
        editImage.setOnClickListener(v -> {
            edit_Image = new Dialog(this);
            edit_Image.setContentView(R.layout.popup_edit_banner_page);
            Window window = edit_Image.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            TextView updateBannerTxt = edit_Image.findViewById(R.id.manager_popup_updateBannerTxt);
            TextView updateIntroTxt = edit_Image.findViewById(R.id.manager_popup_updateIntroTxt);
            ImageView close = edit_Image.findViewById(R.id.manager_popupClose);
            EditText introduction = edit_Image.findViewById(R.id.manager_popupIntroduction);
            Button addImage = edit_Image.findViewById(R.id.manager_popupAddImage);
            Button upload = edit_Image.findViewById(R.id.manager_popupUpload);
            addedImageList = edit_Image.findViewById(R.id.manager_popupAddedImageList);
            RecyclerView imageList = edit_Image.findViewById(R.id.manager_contentViewList);

            //hide un-needed view for updating the images
            introduction.setVisibility(View.GONE);
            updateBannerTxt.setVisibility(View.GONE);
            updateIntroTxt.setVisibility(View.GONE);

            imageList(imageList);

            newImageList = new ArrayList<>();
            addImage.setOnClickListener(v1 -> {
                addedImageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                chooseMultiImage();
            });

            upload.setOnClickListener(v1 -> uploadImages());

            close.setOnClickListener(v1 -> edit_Image.hide());

            edit_Image.show();
        });
    }

    //method for retrieving the image for banner in database and set it to image banner view
    public void setBanner() {
        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getBanner.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    currentBanner = jsonObject.getString("id");
                    String banner_url = jsonObject.getString("banner_url");

                    Picasso.get().load(banner_url).into(image_banner);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> Toast.makeText(Manager_GuestHomepage.this, error.getMessage(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("bannerStat","set");
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    //method for retrieving the introduction in database and set it to the introduction view
    @SuppressLint("SetTextI18n")
    public void setIntro(){
        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getIntro.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    currentIntro = jsonObject.getString("id");
                    String intro = jsonObject.getString("intro");

                    introView.setText(""+intro);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> Toast.makeText(Manager_GuestHomepage.this, error.getMessage(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("intro_status","set");
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void displayImages() {
        imageHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getImages.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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

            imageContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            adapter = new Image_Adapter(this, imageHolder, false);
            imageContainer.setAdapter(adapter);
            imageContainer.setHasFixedSize(true);

        }, Throwable::printStackTrace);
        requestQueue.add(stringRequest);
    }

    //method for picking the image
    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void chooseMultiImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_MULTI_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            upload_banner = new Dialog(this);
            upload_banner.setContentView(R.layout.popup_confirm_banner_upload);
            ImageView imageBanner = upload_banner.findViewById(R.id.manager_bannerImg);
            Button cancel = upload_banner.findViewById(R.id.manager_popupCancel);
            Button confirm = upload_banner.findViewById(R.id.manager_popupConfirm);

            imageBanner.setImageURI(imageUri);

            cancel.setOnClickListener(v -> upload_banner.hide());

            confirm.setOnClickListener(v -> {
                image_banner.setImageURI(imageUri);
                uploadBannerImage();
                upload_banner.hide();
                //edit_banner.hide();
            });

            upload_banner.show();
        }
        else if (requestCode == PICK_MULTI_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple images were selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        newImageList.add(new Image_Model("", imageUri.toString()));
                    }
                } else if (data.getData() != null) {
                    // Single image was selected
                    Uri imageUri = data.getData();
                    newImageList.add(new Image_Model("", imageUri.toString()));
                }

                // Update the image container adapter
                Image_Adapter adapter = new Image_Adapter(this, newImageList, true);
                addedImageList.setAdapter(adapter);
            }
        }
    }

    //method for getting the file extension type of the image or video
    private String getFileExt(Uri MyUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(MyUri));
    }

    //method for inserting banner image in database
    public void uploadBannerImage(){
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        if(imageUri != null){
            String fileName = timestamp+"."+getFileExt(imageUri);
            StorageReference reference = BannerImageReference.child(fileName);
            reference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String bannerUrl = uri.toString();
                                String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/insert/manager_uploadBanner.php";
                                RequestQueue requestQueue = Volley.newRequestQueue(this);
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                                    if (response.equals("success")){
                                        Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                                        updateBannerStat();
                                    }
                                    else if(response.equals("failed")){
                                        Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                        error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
                                {
                                    @Override
                                    protected HashMap<String,String> getParams() {
                                        HashMap<String,String> map = new HashMap<>();
                                        map.put("banner_url",bannerUrl);
                                        return map;
                                    }
                                };
                                requestQueue.add(stringRequest);
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(Manager_GuestHomepage.this, "Failed", Toast.LENGTH_SHORT).show());
        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    //method for inserting the introduction in database
    public void uploadIntroduction(EditText introduction){
        if (!TextUtils.isEmpty(introduction.getText().toString())){
            String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/insert/manager_uploadIntro.php";
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                if (response.equals("success")){
                    Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                    updateIntroStat();
                }
                else if(response.equals("failed")){
                    Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            },
                    error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
            {
                @Override
                protected HashMap<String,String> getParams() {
                    HashMap<String,String> map = new HashMap<>();
                    map.put("introText",introduction.getText().toString());
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }else {
            Toast.makeText(this, "Introduction is empty", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImages() {
        for (Image_Model image : newImageList) {
            uploadImageToFirebaseStorage(image);
        }
    }

    private void uploadImageToFirebaseStorage(Image_Model image) {
        String filename = UUID.randomUUID().toString();

        StorageReference reference = ImagesReference.child(filename);

        Uri imageUri = Uri.parse(image.getImage_url());

        reference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            downloadUrls.add(uri.toString());
                            uploadedCount++;

                            if (uploadedCount == newImageList.size()) {
                                insertImages(downloadUrls);
                            }
                        }).addOnFailureListener(Throwable::printStackTrace))
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void insertImages(List<String> downloadUrls) {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/insert/manager_uploadImage.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        for (String imageUrl : downloadUrls) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                if (response.equals("success")) {
                    count++;
                    if (count == downloadUrls.size()){
                        displayImages();
                        Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.equals("failed")) {
                    Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Log.e("insertImages", error.getMessage());
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("image_url", imageUrl);
                    return params;
                }
            };

            requestQueue.add(stringRequest);
        }
    }


    //method for inserting videos in database
    public void uploadVideos(){

    }

    //method for inserting the images in database
    /*public void uploadImages(){
        for (Uri imageUri : imageUriList) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            if(imageUri != null){
                String fileName = timestamp+"."+getFileExt(imageUri);
                StorageReference reference = ImagesReference.child(fileName);
                reference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot ->
                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/insert/manager_uploadImage.php";
                                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                                        if (response.equals("success")){
                                            Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(response.equals("failed")){
                                            Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                            error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
                                    {
                                        @Override
                                        protected HashMap<String,String> getParams() {
                                            HashMap<String,String> map = new HashMap<>();
                                            map.put("image_url",imageUrl);
                                            return map;
                                        }
                                    };
                                    requestQueue.add(stringRequest);
                                }))
                        .addOnFailureListener(e ->
                                Toast.makeText(Manager_GuestHomepage.this, "Failed", Toast.LENGTH_SHORT).show()
                        );
            }else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
        imageUriList.clear();
    }*/

    //method for updating the status of banner when a new banner is inserted
    public void updateBannerStat(){
        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/update/manager_updateBannerStat.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")){
                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            }
            else if(response.equals("failed")){
                Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
            }
        },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("id",currentBanner);
                map.put("set_banner","unset");
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    //method for updating the status of introduction when a new intro is inserted
    public void updateIntroStat(){
        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/update/manager_updateIntro.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("success")){
                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            }
            else if(response.equals("failed")){
                Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
            }
        },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("id",currentIntro);
                map.put("intro_status","unset");
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    //method for listing the uploaded images of banner in the dialog when uploading
    public void bannerList(RecyclerView bannerList){
        ArrayList<Manager_GuestHomepageViews_Model> GuestView_holder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getBanner.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Manager_GuestHomepageViews_Model model = new Manager_GuestHomepageViews_Model(object.getString("id"), object.getString("banner_url"), null, null, null, null, null, null);
                    GuestView_holder.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Manager_GuestHomepageViews_Adapter adapter = new Manager_GuestHomepageViews_Adapter(GuestView_holder);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            bannerList.setLayoutManager(layoutManager);
            bannerList.setAdapter(adapter);
        },
                error -> Toast.makeText(Manager_GuestHomepage.this, error.getMessage(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("bannerStat","unset");
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    //method for listing the uploaded introduction in the dialog when uploading
    public void introList(RecyclerView introList){
        ArrayList<Manager_GuestHomepageViews_Model> GuestView_holder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getIntro.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Manager_GuestHomepageViews_Model model = new Manager_GuestHomepageViews_Model(null, null, object.getString("id"), object.getString("intro"), null, null, null, null);
                    GuestView_holder.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Manager_GuestHomepageViews_Adapter adapter = new Manager_GuestHomepageViews_Adapter(GuestView_holder);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            introList.setLayoutManager(layoutManager);
            introList.setAdapter(adapter);
        },
                error -> Toast.makeText(Manager_GuestHomepage.this, error.getMessage(), Toast.LENGTH_LONG).show())
        {
            @Override
            protected HashMap<String,String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("intro_status","unset");
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    //method for listing the uploaded images in the dialog when uploading
    public void imageList(RecyclerView imageList){
        ArrayList<Manager_GuestHomepageViews_Model> GuestView_holder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getImages.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Manager_GuestHomepageViews_Model model = new Manager_GuestHomepageViews_Model(null, null, null, null, null, null, object.getString("id"), object.getString("image_url"));
                    GuestView_holder.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Manager_GuestHomepageViews_Adapter adapter = new Manager_GuestHomepageViews_Adapter(GuestView_holder);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            imageList.setLayoutManager(layoutManager);
            imageList.setAdapter(adapter);
        },
                error -> Toast.makeText(Manager_GuestHomepage.this, error.getMessage(), Toast.LENGTH_LONG).show())
       /* {
            @Override
            protected HashMap<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("bannerStat","unset");
                return map;
            }
        }*/;
        requestQueue.add(stringRequest);
    }

}