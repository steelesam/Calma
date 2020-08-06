package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = findViewById(R.id.splash_screen_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null ) {
            Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String accountType = dataSnapshot.child("accountType").getValue().toString();
                    if (mAuth.getCurrentUser() != null && accountType.equals("Dependant")) {
                        Intent dependantIntent = new Intent(getApplicationContext(), DependantDash.class);
                        startActivity(dependantIntent);
                        finish();
                    } else if (mAuth.getCurrentUser() != null && accountType.equals("Trustee")) {
                        Intent trusteeIntent = new Intent(getApplicationContext(), TrusteeDash.class);
                        startActivity(trusteeIntent);
                        finish();
                    } else if (mAuth.getCurrentUser() == null ) {
                        Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        }


}