package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TrusteeContactIsChosen extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    ImageView callImage, smsImage, emailImage;
    TextView chosenContactDetails, usersName;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String phoneNumber;
    EditText smsEditText, emailToEditText, emailSubjectEditText, emailMessageEditText;
    Button sendButton, sendEmailButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trustee_contact_is_chosen);
        chosenContactDetails = findViewById(R.id.contactDetails);
        mAuth = FirebaseAuth.getInstance();
        usersName = findViewById(R.id.usersName);
        callImage = findViewById(R.id.callImage);
        smsImage = findViewById(R.id.smsImage);
        emailImage = findViewById(R.id.emailImage);
        smsEditText = findViewById(R.id.smsEditText);
        sendButton = findViewById(R.id.sendButton);
        emailToEditText = findViewById(R.id.emailToEditText);
        emailSubjectEditText = findViewById(R.id.emailSubjectEditText);
        emailMessageEditText = findViewById(R.id.emailMessageEditText);
        sendEmailButton = findViewById(R.id.sendEmailButton);

        loadUserInformation();
        retrieveContactDetails();

        Intent intent = getIntent();
        final String selectedName = intent.getStringExtra("SelectedContactTrustee");
        String[] separated = selectedName.split(":");
        final String selectedContactName = separated[1].trim();
        phoneNumber = separated[3].trim();

        callImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCall();
            }
        });
        smsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsMessage();
            }
        });
        emailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email();
            }
        });


    }

    /**
     * Retrieves details of selected contact from previous activity and sets TextView to show that contacts details
     */
    public void retrieveContactDetails() {
        Intent intent = getIntent();
        final String selectedName = intent.getStringExtra("SelectedContactTrustee");
        String[] separated = selectedName.split(":");
        final String selectedContactName = separated[1].trim();
        final String phoneNumber = separated[3].trim();
        final String email = separated[5].trim();
        chosenContactDetails.setText(selectedContactName + "\n" + phoneNumber + "\n" + email);
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
     * Rings phone number of selected contact onClick of phone imageView
     */
    private void phoneCall() {
        String number = phoneNumber;
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(TrusteeContactIsChosen.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TrusteeContactIsChosen.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }

    /**
     * Permission handler for phoneCall method
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                phoneCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * OnClick of SMS image button, EditText is displayed - whatever the user types in the EditText will then be sent to phone number of contact after hitting send button.
     */
    private void smsMessage() {
        smsEditText.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = smsEditText.getText().toString();
                sendSmsMessage(message);
            }
        });
    }

    /**
     * Handles sending of actual text - takes parameter from smsMessage() method editText
     * @param message
     */
    private void sendSmsMessage(String message){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(this, "SMS message sent!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Makes recipient, subject and message editTexts visible. On click of the send email button, email client is opened and email is composed based on text variables
     */
    private void email() {
        emailToEditText.setVisibility(View.VISIBLE);
        emailSubjectEditText.setVisibility(View.VISIBLE);
        emailMessageEditText.setVisibility(View.VISIBLE);
        sendEmailButton.setVisibility(View.VISIBLE);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse("mailto:" + emailToEditText.getText().toString()));
                intent.putExtra(Intent.EXTRA_SUBJECT, emailSubjectEditText.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT, emailMessageEditText.getText().toString());
                startActivity(intent);
            }
        });
    }
}