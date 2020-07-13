package com.example.calma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficLights extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    /**
     * Declare XML elements
     */
    ImageView homeButton;
    FirebaseAuth mAuth;
    TextView usersName;
    DatabaseReference dbRef;
    Button greenAlert, amberAlert, redAlert;
    LinearLayout greenSettings, amberSettings, redSettings, blueTracker;
    public static final Map<String, String> phoneMap = new HashMap<>();

    /**
     * OnCreate start
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_lights);

        usersName = findViewById(R.id.usersName);
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        loadUserInformation();
        greenSettings = findViewById(R.id.greenSettings);
        amberSettings = findViewById(R.id.amberSettings);
        redSettings = findViewById(R.id.redSettings);
        blueTracker = findViewById(R.id.blueTracker);

        // Spinner for choosing recipient(s) of alert
        final Spinner spinner = findViewById(R.id.trusteeOption);
        spinner.setOnItemSelectedListener(this);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("ContactsList");
        Query query = dbRef.getRef();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> namesList = new ArrayList<>();
                namesList.add(0, "Select Trustee");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String trusteeName = dataSnapshot1.child("name").getValue(String.class);
                    String phoneNumber = dataSnapshot1.child("phoneNumber").getValue(String.class);
                    // Adding key and value to hashmap to use as selected recipient phone number for sending text
                    phoneMap.put(trusteeName, phoneNumber);
                    namesList.add(trusteeName);
                }
                // Spinner only displays name - not phone number
                // Greys out first option and makes non selectable
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (TrafficLights.this, android.R.layout.simple_spinner_item, namesList) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView textView = (TextView) view;
                        if (position == 0) {
                            textView.setTextColor(Color.GRAY);
                        } else {
                            textView.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TrafficLights.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePage();
            }
        });
        greenAlert = findViewById(R.id.greenAlert);
        amberAlert = findViewById(R.id.amberAlert);
        redAlert = findViewById(R.id.redAlert);
        greenAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens GreenMaps activity and sends HashMap of name/phoneNumber through
                Intent intent = new Intent(getApplicationContext(), GreenMaps.class);
                intent.putExtra("phoneMap", (Serializable) phoneMap);
                intent.putExtra("spinnerName", String.valueOf(spinner.getSelectedItem()));
                startActivity(intent);

            }
        });

        amberAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AmberMaps.class);
                intent.putExtra("phoneMap", (Serializable) phoneMap);
                intent.putExtra("spinnerName", String.valueOf(spinner.getSelectedItem()));
                startActivity(intent);
            }
        });

        greenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGreenSettings();
            }
        });
        amberSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAmberSettings();
            }
        });
        redSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRedSettings();
            }
        });
        blueTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrackerLog();
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
     * Sends user back to home/dashboard page
     */
    public void openHomePage() {
        Intent intent = new Intent(this, DependantDash.class);
        startActivity(intent);
    }

    /**
     * Deals with recipient of alert - selected from spinner
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item if not the 'prompt' item
        if (position > 0) {
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
        } else if (position == 0){
            Toast.makeText(parent.getContext(), "Please select a trusted contact", Toast.LENGTH_SHORT).show();
        }


    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * Opens GreenSettings
     */
    public void openGreenSettings(){
        Intent intent = new Intent(this, GreenSettings.class);
        startActivity(intent);
    }

    /**
     * Opens GreenSettings
     */
    public void openAmberSettings(){
        Intent intent = new Intent(this, AmberSettings.class);
        startActivity(intent);
    }

    /**
     * Opens GreenSettings
     */
    public void openRedSettings(){
        Intent intent = new Intent(this, RedSettings.class);
        startActivity(intent);
    }

    /**
     * Opens GreenSettings
     */
    public void openTrackerLog(){
        Intent intent = new Intent(this, DependantAlertTracker.class);
        startActivity(intent);
    }
}

