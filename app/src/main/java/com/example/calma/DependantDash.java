package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DependantDash extends AppCompatActivity {

    /**
     * Declare XML elements
     */
    ImageView animationPic, logoutButton;
    TextView wantToSend, hitBelow, usersName, accType;
    Button alertPage;
    FirebaseAuth mAuth;
    LinearLayout trustedPeopleOption, myProfileOption, hintsOptions, alertTrackerOption;


    /**
     * OnCreate start
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependant_dash);
        mAuth = FirebaseAuth.getInstance();

        animationPic = findViewById(R.id.animationPic);
        wantToSend = findViewById(R.id.wantToSendSignal);
        hitBelow = findViewById(R.id.hitBelow);
        alertPage = findViewById(R.id.signalPageButton);
        usersName = findViewById(R.id.usersName);
        accType = findViewById(R.id.accountType);
        trustedPeopleOption = findViewById(R.id.trustedPeople);
        myProfileOption = findViewById(R.id.myProfile);
        hintsOptions = findViewById(R.id.dashHints);
        alertTrackerOption = findViewById(R.id.alertTrackerOption);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutPage();
            }
        });


        loadUserInformation();

        alertPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlertsPage();
            }
        });
        trustedPeopleOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrustedContactsPage();
            }
        });
        myProfileOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyProfilePage();
            }
        });

        hintsOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDashTipsAndHints();
            }
        });

        alertTrackerOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlertTrackerPage();
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
     * Opens up traffic lights alert page
     */
    public void openAlertsPage() {
        Intent intent = new Intent(this, TrafficLights.class);
        startActivity(intent);

    }

    /**
     * Opens up trusted contacts page
     */
    public void openTrustedContactsPage() {
        Intent intent = new Intent(this, DependantContacts.class);
        startActivity(intent);
    }

    /**
     * Opens logout page
     */
    private void openLogoutPage() {
        Intent intent = new Intent(this, Logout.class);
        startActivity(intent);
    }

    /**
     * Opens EditProfile page
     */
    private void openMyProfilePage() {
        Intent intent = new Intent(this, DependantEditProfile.class);
        startActivity(intent);
    }

    /**
     * Opens EditProfile page
     */
    private void openDashTipsAndHints() {
        Intent intent = new Intent(this, DependantDashHints.class);
        startActivity(intent);
    }

    /**
     * Opens EditProfile page
     */
    private void openAlertTrackerPage() {
        Intent intent = new Intent(this, DependantAlertTracker.class);
        startActivity(intent);
    }
}
