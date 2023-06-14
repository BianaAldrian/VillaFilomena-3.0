package com.example.villafilomena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class TermsPrivacy extends AppCompatActivity {

    Button cont;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_privacy);

        checkBox = findViewById(R.id.terms_privacy_check);
        cont = findViewById(R.id.terms_privacy_continue_btn);

        cont.setOnClickListener(v -> {
            startActivity(new Intent(this,ContinueAs.class));
            finish();
        });
    }
}