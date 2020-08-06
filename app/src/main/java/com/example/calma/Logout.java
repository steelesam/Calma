package com.example.calma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class Logout extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button logoutButton;
    ImageView logoutImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        logoutImage = findViewById(R.id.imageView3);

        mAuth = FirebaseAuth.getInstance();

        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                backToLogin();
            }
        });

    }

    private void backToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
