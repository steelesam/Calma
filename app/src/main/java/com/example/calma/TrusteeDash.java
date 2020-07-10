package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrusteeDash extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView usersName;
    LinearLayout trustedContacts, editProfile, dashHints;
    ImageView logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trustee_dash);

        trustedContacts = findViewById(R.id.trustee_contacts_link);
        trustedContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactsPage();
            }
        });
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutPage();
            }
        });
        usersName = findViewById(R.id.usersName);
        dashHints = findViewById(R.id.trusteeHints);
        dashHints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDashHints();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        loadUserInformation();

        editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfile();
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
     * Opens contacts page
     */
    private void openContactsPage() {
        Intent intent = new Intent(this, TrusteeContacts.class);
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
    private void openEditProfile() {
        Intent intent = new Intent(this, TrusteeEditProfile.class);
        startActivity(intent);
    }

    /**
     * Opens Tips and Hints page
     */
    private void openDashHints() {
        Intent intent = new Intent(this, TrusteeDashHints.class);
        startActivity(intent);
    }
}
