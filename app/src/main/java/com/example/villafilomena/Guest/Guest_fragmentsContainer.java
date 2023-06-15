package com.example.villafilomena.Guest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.villafilomena.R;
import com.example.villafilomena.TermsPrivacy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Guest_fragmentsContainer extends AppCompatActivity {
    public static String fromBooking = "";
    public static String email;
    SharedPreferences sharedPreferencesEmail;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView toolbar;
    ImageView banner;
    Button home, book, logIn, logOut;
    AppBarLayout appbar;
    NestedScrollView nestedScrllView;

    //Navigation View Layout
    CardView navView_account, navView_booking, navView_ratings, navTerm_privacy;
    TextView fullname;
    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_fragments_container);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ipAddress = sharedPreferences.getString("IP", "");

        sharedPreferencesEmail = getSharedPreferences("guestEmail_Pref", MODE_PRIVATE);
        email = sharedPreferencesEmail.getString("guestEmail", "");

        //Navigation View Layout
        navView_account = findViewById(R.id.guest_navView_account);
        navView_booking = findViewById(R.id.guest_navView_bookings);
        navView_ratings = findViewById(R.id.guest_navView_ratings);
        navTerm_privacy = findViewById(R.id.guest_navView_termsPrivacy);

        drawerLayout = findViewById(R.id.guest_drawerLayout);
        navigationView = findViewById(R.id.guest_navView);
        toolbar = findViewById(R.id.guest_menuToolbar);
        logIn = findViewById(R.id.guest_navView_logInBtn);
        logOut = findViewById(R.id.guest_navView_logOutBtn);
        fullname = findViewById(R.id.menu_userName);
        get_Infos();

        banner = findViewById(R.id.imgviewBanner);
        home = findViewById(R.id.btnHome);
        book = findViewById(R.id.btnBook);
        appbar = findViewById(R.id.appbar);
        nestedScrllView = findViewById(R.id.nestedScrllView);

        home.setPaintFlags(home.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        home.setPaintFlags(home.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        replace_home(new Guest_homePage());

        home.setOnClickListener(view -> {
            nestedScrllView.fullScroll(View.FOCUS_UP);
            toggle(true);
            nestedScrllView.setNestedScrollingEnabled(true);

            home.setPaintFlags(home.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            home.setPaintFlags(home.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

            if ((book.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) == Paint.UNDERLINE_TEXT_FLAG &&
                    (book.getPaintFlags() & Paint.FAKE_BOLD_TEXT_FLAG) == Paint.FAKE_BOLD_TEXT_FLAG) {
                book.setPaintFlags(book.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
                book.setPaintFlags(book.getPaintFlags() ^ Paint.FAKE_BOLD_TEXT_FLAG);

                replace_home(new Guest_homePage());

            }
        });

        book.setOnClickListener(view -> {
            nestedScrllView.fullScroll(View.FOCUS_UP);
            toggle(false);
            nestedScrllView.setNestedScrollingEnabled(false);

            book.setPaintFlags(book.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            book.setPaintFlags(book.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

            if ((home.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) == Paint.UNDERLINE_TEXT_FLAG &&
                    (home.getPaintFlags() & Paint.FAKE_BOLD_TEXT_FLAG) == Paint.FAKE_BOLD_TEXT_FLAG) {
                home.setPaintFlags(home.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
                home.setPaintFlags(home.getPaintFlags() ^ Paint.FAKE_BOLD_TEXT_FLAG);

                replace_book(new Guest_bookingPage1());
            }

            //replace(new Guest_bookingPage1());
        });

        //fromBookingFunctions();
        toolbarFunctions();
    }

   /* private void fromBookingFunctions() {
        if (fromBooking.equals("booking")){
            *//*nestedScrllView.fullScroll(View.FOCUS_UP);
            toggle(true);
            nestedScrllView.setNestedScrollingEnabled(true);
*//*
            home.setPaintFlags(home.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            home.setPaintFlags(home.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
            replace_home(new Guest_homePage());
        }
    }*/

    @SuppressLint("SetTextI18n")
    private void get_Infos() {
        String url = "http://" + ipAddress + "/VillaFilomena/guest_dir/retrieve/guest_getGuestInfo.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            fullname.setText(jsonObject.getString("fullname"));
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

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }

    private void toolbarFunctions() {
        toolbar.setOnClickListener(v -> {
            if (TextUtils.isEmpty(email)) {
                logIn.setVisibility(View.VISIBLE);
                logOut.setVisibility(View.GONE);
            } else {
                logIn.setVisibility(View.GONE);
                logOut.setVisibility(View.VISIBLE);
            }
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navView_account.setOnClickListener(v -> {
            startActivity(new Intent(this, Guest_accountPage.class));
        });
        navView_booking.setOnClickListener(v -> {
            startActivity(new Intent(this, Guest_bookedListPage.class));
        });
        navView_ratings.setOnClickListener(v -> {
            startActivity(new Intent(this, Guest_rates_feedbacksPage.class));
        });
        navTerm_privacy.setOnClickListener(v -> {
            startActivity(new Intent(this, TermsPrivacy.class));
        });

        logIn.setOnClickListener(v -> {
            Guest_Login.originateFrom = "fragmentContainer";
            startActivity(new Intent(this, Guest_Login.class));
            finish();
        });
        logOut.setOnClickListener(v -> {
            Guest_Login.originateFrom = "fragmentContainer";
            sharedPreferencesEmail.edit().clear().apply();
            startActivity(new Intent(this, Guest_Login.class));
            finish();
        });
    }

    private void replace(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.guestFragmentContainer, fragment).commit();
    }

    private void replace_home(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.guestFragmentContainer, fragment).commit();
    }

    private void replace_book(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.guestFragmentContainer, fragment).commit();
    }

    private void toggle(boolean show) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        params.height = banner.getHeight();
        appbar.setLayoutParams(params);
        appbar.setExpanded(show);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (TextUtils.isEmpty(email)) {
            logIn.setVisibility(View.VISIBLE);
            logOut.setVisibility(View.GONE);
        } else {
            logIn.setVisibility(View.GONE);
            logOut.setVisibility(View.VISIBLE);
        }
    }

    public void closeActivity() {
        finish();
    }
}