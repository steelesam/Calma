package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrusteeEditProfile extends AppCompatActivity {

    /**
     * Elements
     */
    EditText fullName, phoneNumber;
    Button updateProfileButton;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    ImageView logoutButton;
    TextView usersName, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trustee_edit_profile);

        usersName = findViewById(R.id.usersName);
        number = findViewById(R.id.pageDesc);
        fullName = findViewById(R.id.fullNameEditText);
        phoneNumber = findViewById(R.id.phoneNumberEditText);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        progressBar = findViewById(R.id.progressBarEditProfile);
        progressBar.setVisibility(View.GONE);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutPage();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        loadUserInformation();


        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });


    }

    /**
     * Updates the user's full name and phone number based on the edit text input
     */
    private void updateUserInfo() {
        progressBar.setVisibility(View.VISIBLE);

        String newName = fullName.getText().toString().trim();
        if(newName.isEmpty()){
            fullName.setError("Name is required");
            fullName.requestFocus();
            return;
        }
        String newPhone = phoneNumber.getText().toString().trim();
        if(newPhone.isEmpty()){
            phoneNumber.setError("Please enter a valid phone number");
            phoneNumber.requestFocus();
            return;
        }

        if(newPhone.length()!=11){
            phoneNumber.setError("Please enter a valid phone number");
            phoneNumber.requestFocus();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        databaseReference.child("name").setValue(newName);
        databaseReference.child("phoneNumber").setValue(newPhone);

        Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();


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
                String phoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                usersName.setText(displayName);
                number.setText(phoneNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Opens logout page
     */
    private void openLogoutPage() {
        Intent intent = new Intent(this, Logout.class);
        startActivity(intent);
    }
}