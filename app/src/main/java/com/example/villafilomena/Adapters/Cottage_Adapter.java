package com.example.villafilomena.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.villafilomena.Models.RoomCottageDetails_Model;
import com.example.villafilomena.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cottage_Adapter extends RecyclerView.Adapter<Cottage_Adapter.ViewHolder> {
    String ipAddress;
    Context context;
    ArrayList<RoomCottageDetails_Model> detailsHolder;
    boolean showBox = false;
    private boolean showDelete;

    public Cottage_Adapter(Context context, ArrayList<RoomCottageDetails_Model> detailsHolder, boolean showBox) {
        this.context = context;
        this.detailsHolder = detailsHolder;
        this.showBox = showBox;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");
    }

    public void setNewData(boolean showDelete, ArrayList<RoomCottageDetails_Model> detailsHolder) {
        this.showDelete = showDelete;
        this.detailsHolder = detailsHolder;
        notifyDataSetChanged();
    }

    public void setAvailability(ArrayList<RoomCottageDetails_Model> detailsHolder){
        this.detailsHolder = detailsHolder;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_cottage_details_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (showBox) {
            holder.box.setVisibility(View.VISIBLE);
        }

        RoomCottageDetails_Model model = detailsHolder.get(position);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.image_placeholder)  // Placeholder image while loading
                .error(R.drawable.error_image)  // Error image if loading fails
                .transform(new RoundedCorners(10));  // Apply rounded corners to the image

        Glide.with(context)
                .load(model.getImageUrl())
                .apply(requestOptions)
                .into(holder.image);

        /*Picasso.get().load(model.getImageUrl()).into(holder.image);*/
        holder.infos.setText(
                ""+model.getName()+"\n"+
                        model.getCapacity()+" People\n"+
                        "₱"+model.getRate());

        holder.seeMore.setOnClickListener(v -> {
            Dialog DetailedInfo = new Dialog(context);
            DetailedInfo.setContentView(R.layout.popup_room_cottage_detailed_information_dialog);
            Window window = DetailedInfo.getWindow();
            DetailedInfo.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView infoImage = DetailedInfo.findViewById(R.id.RoomCottageDetailInfo_image);
            TextView title = DetailedInfo.findViewById(R.id.RoomCottageDetailInfo_title);
            TextView details = DetailedInfo.findViewById(R.id.RoomCottageDetailInfo_details);

            Glide.with(context)
                    .load(model.getImageUrl())
                    .apply(requestOptions)
                    .into(infoImage);

            title.setText(""+model.getName() +"\n"+ model.getCapacity() +"\n"+ model.getRate());
            details.setText(""+model.getDescription());

            DetailedInfo.show();
        });

        holder.box.setOnClickListener(v -> {
            if (holder.check.getVisibility() == View.VISIBLE){
                holder.check.setVisibility(View.GONE);
            } else if (holder.check.getVisibility() == View.GONE) {
                holder.check.setVisibility(View.VISIBLE);
            }
        });

        if (!showDelete) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
        }

        holder.delete.setOnClickListener(v -> {
            deleteImage(model.getId());
            detailsHolder.remove(position);
            notifyItemRemoved(position);
        });
    }

    private void deleteImage(String id) {
        String url = "http://" + ipAddress + "/VillaFilomena/manager_dir/delete/manager_delete_cottage.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(context, "Cottage removed", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else if (response.equals("failed")) {
                        Toast.makeText(context, "Cottage can't be removed", Toast.LENGTH_SHORT).show();
                    }
                },
                Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return detailsHolder.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView box;
        ImageView check, image, delete;
        TextView infos;
        Button seeMore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            box = itemView.findViewById(R.id.RoomCottageDetail_box);
            check = itemView.findViewById(R.id.RoomCottageDetail_check);
            image = itemView.findViewById(R.id.RoomCottageDetail_image);
            infos = itemView.findViewById(R.id.RoomCottageDetail_infos);
            seeMore = itemView.findViewById(R.id.RoomCottageDetail_seeMore_Btn);
            delete = itemView.findViewById(R.id.roomList_delete);
        }
    }
}
