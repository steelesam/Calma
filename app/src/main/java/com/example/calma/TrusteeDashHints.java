package com.example.calma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TrusteeDashHints extends AppCompatActivity {

    Button backToDash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trustee_dash_hints);
        backToDash = findViewById(R.id.backToDashBtn);
        backToDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrusteeDash.class);
                startActivity(intent);
                finish();
            }

        });
    }
}



