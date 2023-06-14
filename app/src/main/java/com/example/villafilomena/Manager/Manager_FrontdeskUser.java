package com.example.villafilomena.Manager;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.Adapters.Manager.Manager_frontdeskClerk_Adapter;
import com.example.villafilomena.Models.Manager.Manager_frondeskClerk_Model;
import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager_FrontdeskUser extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    public static Button addFrontdesk, deleteFrontdesk, deletingDone;
    String ipAddress;
    StorageReference clerkImage;
    RecyclerView clerkContainer;
    Dialog addFrontdeskDialog;
    Uri imageUri;
    ImageView clerksImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_frontdesk_user);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        clerkImage = FirebaseStorage.getInstance().getReference("clerkImage");

        addFrontdesk = findViewById(R.id.manager_addFrontdeskUser_btn);
        deleteFrontdesk = findViewById(R.id.manager_deleteFrontdeskUser_btn);
        deletingDone = findViewById(R.id.manager_FrontdeskUser_doneBtn);
        clerkContainer = findViewById(R.id.manager_frontdeskClerk_container);

        deletingDone.setVisibility(View.GONE);

        listClerks();

        addFrontdesk.setOnClickListener(v -> {
            addFrontdeskDialog = new Dialog(this);
            addFrontdeskDialog.setContentView(R.layout.popup_add_frontdesk_user_dialog);

            clerksImage = addFrontdeskDialog.findViewById(R.id.popup_clerksImage);
            Button chooseImage = addFrontdeskDialog.findViewById(R.id.popup_chooseClerkImage);
            EditText clerksName = addFrontdeskDialog.findViewById(R.id.popup_clerksName);
            EditText clerksContact = addFrontdeskDialog.findViewById(R.id.popup_clerksContact);
            EditText clerksUsername = addFrontdeskDialog.findViewById(R.id.popup_clerksUsername);
            EditText clerksPassword = addFrontdeskDialog.findViewById(R.id.popup_clerksPassword);
            Button done = addFrontdeskDialog.findViewById(R.id.popup_addClerks_doneBtn);

            chooseImage.setOnClickListener(v1 -> {
                chooseImage();
            });

            done.setOnClickListener(v1 -> {
                upload_frontdeskClerk(clerksName, clerksContact, clerksUsername, clerksPassword);
            });

            addFrontdeskDialog.show();
        });

        deleteFrontdesk.setOnClickListener(v -> {
            //holder.delete.setVisibility(View.VISIBLE);
            Manager_FrontdeskUser.deletingDone.setVisibility(View.VISIBLE);
            Manager_FrontdeskUser.addFrontdesk.setVisibility(View.GONE);
            Manager_FrontdeskUser.deleteFrontdesk.setVisibility(View.GONE);

            int childCount = clerkContainer.getChildCount();
            for (int i=0; i<childCount; i++){
                View childView =clerkContainer.getLayoutManager().findViewByPosition(i);
                ImageView delete = (ImageView)childView.findViewById(R.id.manager_clerkList_delete);
                delete.setVisibility(View.VISIBLE);
            }

        });

        deletingDone.setOnClickListener(v -> {
            addFrontdesk.setVisibility(View.VISIBLE);
            deletingDone.setVisibility(View.GONE);
            deleteFrontdesk.setVisibility(View.VISIBLE);

            int childCount = clerkContainer.getChildCount();
            for (int i=0; i<childCount; i++){
                View childView =clerkContainer.getLayoutManager().findViewByPosition(i);
                ImageView delete = (ImageView)childView.findViewById(R.id.manager_clerkList_delete);
                delete.setVisibility(View.GONE);
            }
        });

    }

    private void listClerks() {
        ArrayList<Manager_frondeskClerk_Model> clerkHolder = new ArrayList<>();

        String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/retrieve/manager_getFrontdeskClerks.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Manager_frondeskClerk_Model model = new Manager_frondeskClerk_Model(
                            object.getString("id"),
                            object.getString("imageUrl"),
                            object.getString("clerkName"),
                            object.getString("clerkContact"),
                            object.getString("clerkUsername"),
                            object.getString("clerkPassword"));

                    clerkHolder.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Manager_frontdeskClerk_Adapter adapter = new Manager_frontdeskClerk_Adapter(this,clerkHolder);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            clerkContainer.setLayoutManager(layoutManager);
            clerkContainer.setAdapter(adapter);

        }, error -> Toast.makeText(this,error.getMessage().toString(), Toast.LENGTH_LONG).show());
        requestQueue.add(stringRequest);
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            clerksImage.setImageURI(imageUri);
        }

    }

    private String getfileExt(Uri MyUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(MyUri));
    }
    private void upload_frontdeskClerk(EditText clerksName, EditText clerksContact, EditText clerksUsername, EditText clerksPassword) {
        if(imageUri != null){
            String fileName = clerksName.getText().toString()+"."+getfileExt(imageUri);
            StorageReference reference = clerkImage.child(fileName);
            reference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String clerkUrl = uri.toString();
                            String url = "http://"+ipAddress+"/VillaFilomena/manager_dir/insert/manager_uploadFrontdeskClerk.php";
                            RequestQueue requestQueue = Volley.newRequestQueue(this);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                                if (response.equals("success")){
                                    Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                    addFrontdeskDialog.hide();
                                }
                                else if(response.equals("failed")){
                                    Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            },
                                    error -> Toast.makeText(this, error.getMessage().toString(), Toast.LENGTH_LONG).show())
                            {
                                @Override
                                protected HashMap<String,String> getParams() {
                                    HashMap<String,String> map = new HashMap<>();
                                    map.put("imageUrl",clerkUrl);
                                    map.put("clerkName",clerksName.getText().toString());
                                    map.put("clerkContact",clerksContact.getText().toString());
                                    map.put("clerkUsername",clerksUsername.getText().toString());
                                    map.put("clerkPassword",clerksPassword.getText().toString());
                                    return map;
                                }
                            };
                            requestQueue.add(stringRequest);
                        });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    );
        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}