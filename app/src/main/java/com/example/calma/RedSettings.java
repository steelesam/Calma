package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RedSettings extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText houseNumber, street, town, city, postcode, county, country;
    TextView usersName;
    Button addSafePlace;
    ImageView logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_settings);
        usersName = findViewById(R.id.usersName);
        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logoutButton);
        addSafePlace = findViewById(R.id.addSafePlaceBtn);
        loadUserInformation();
        houseNumber = findViewById(R.id.houseNumberEditText);
        street = findViewById(R.id.streetEditText);
        town = findViewById(R.id.townEditText);
        city = findViewById(R.id.cityEditText);
        postcode = findViewById(R.id.postcodeEditText);
        county = findViewById(R.id.countyEditText);
        country = findViewById(R.id.countryEditText);

        addSafePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSafePlaceToDatabase();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutPage();
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
     * Adds address details to database so that they can be reverse GeoCoded into markers on RedMaps activity
     */
    private void addSafePlaceToDatabase() {
        String houseAddress = houseNumber.getText().toString().trim();
        String streetAddress = street.getText().toString().trim();
        String townAddress = town.getText().toString().trim();
        String cityAddress = city.getText().toString().trim();
        String postcodeAddress = postcode.getText().toString().trim();
        String countyAddress = county.getText().toString().trim();
        String countryAddress = country.getText().toString().trim();

        if (houseAddress.isEmpty()) {
            houseNumber.setError("House/apartment number is required");
            houseNumber.requestFocus();
            return;
        }

        if (streetAddress.isEmpty()) {
            street.setError("Street is required");
            street.requestFocus();
            return;
        }

        if (townAddress.isEmpty()) {
            town.setError("Town is required");
            town.requestFocus();
            return;
        }

        if (cityAddress.isEmpty()) {
            city.setError("City is required");
            city.requestFocus();
            return;
        }

        if (postcodeAddress.isEmpty()) {
            postcode.setError("Postcode is required");
            postcode.requestFocus();
            return;
        }

        if (countyAddress.isEmpty()) {
            county.setError("County is required");
            county.requestFocus();
            return;
        }

        if (countryAddress.isEmpty()) {
            country.setError("Country is required");
            country.requestFocus();
            return;
        }

        SafePlace safePlace = new SafePlace(houseAddress,streetAddress,townAddress,cityAddress,postcodeAddress,countyAddress,countryAddress);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        db.child("Safe Places").setValue(safePlace);
        Toast.makeText(getApplicationContext(), "Safe place added successfully", Toast.LENGTH_SHORT).show();

    }

    /**
     * Opens logout page
     */
    private void openLogoutPage() {
        Intent intent = new Intent(this, Logout.class);
        startActivity(intent);
    }
}