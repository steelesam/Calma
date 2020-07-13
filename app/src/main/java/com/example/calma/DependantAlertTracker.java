package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DependantAlertTracker extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView usersName;
    Button trafficLightPage;
    LinearLayout greenLog, amberLog, redLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependant_alert_tracker);

        mAuth = FirebaseAuth.getInstance();
        usersName = findViewById(R.id.usersName);
        loadUserInformation();
        trafficLightPage = findViewById(R.id.signalPageButton);
        trafficLightPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrafficLights.class);
                startActivity(intent);
            }
        });

        greenLog = findViewById(R.id.greenLog);
        greenLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DependantGreenLog.class);
                startActivity(intent);
            }
        });
        amberLog = findViewById(R.id.amberLog);
        amberLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAmberLog();
            }
        });
        redLog = findViewById(R.id.redLog);
        redLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRedLog();
            }
        });


    }

    /**
     * Retrieves and displays info of current logged in user
     */
    private void loadUserInformation() {
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference retrieveName = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        retrieveName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("name").getValue().toString();
                usersName.setText(displayName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Opens amber log
     */
    private void openAmberLog(){
        Intent intent2 = new Intent(this, DependantAmberLog.class);
        startActivity(intent2);
    }

    /**
     * Opens red log
     */
    private void openRedLog(){
        Intent intent3 = new Intent(this, DependantRedLog.class);
        startActivity(intent3);
    }
}